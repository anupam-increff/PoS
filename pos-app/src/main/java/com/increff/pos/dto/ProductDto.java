package com.increff.pos.dto;

import com.increff.pos.exception.ApiException;
import com.increff.pos.flow.ProductFlow;
import com.increff.pos.model.data.ErrorTSVData;
import com.increff.pos.model.data.PaginatedResponse;
import com.increff.pos.model.data.ProductData;
import com.increff.pos.model.data.TSVUploadResponse;
import com.increff.pos.model.form.ProductForm;
import com.increff.pos.service.TSVDownloadService;
import com.increff.pos.util.TSVUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validator;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class ProductDto extends BaseDto {

    private static final int MAX_ROWS = 5000;

    @Autowired
    private ProductFlow productFlow;

    @Autowired
    private TSVDownloadService tsvDownloadService;

    @Autowired
    private Validator validator;

    public void addProduct(@Valid ProductForm productForm) {
        productFlow.addProduct(productForm);
    }

    public PaginatedResponse<ProductData> getAll(int page, int pageSize) {
        List<ProductData> products = productFlow.getAllProducts(page, pageSize);
        long total = productFlow.countAllProducts();
        return createPaginatedResponse(products, page, pageSize, total);
    }

    public PaginatedResponse<ProductData> getByClient(String clientName, int page, int pageSize) {
        List<ProductData> products = productFlow.getProductsByClient(clientName, page, pageSize);
        long total = productFlow.countProductsByClient(clientName);
        return createPaginatedResponse(products, page, pageSize, total);
    }

    public PaginatedResponse<ProductData> searchByBarcode(String barcode, int page, int pageSize) {
        List<ProductData> products = productFlow.searchProductsByBarcode(barcode, page, pageSize);
        long total = productFlow.countSearchByBarcode(barcode);
        return createPaginatedResponse(products, page, pageSize, total);
    }

    public ProductData getByBarcode(String barcode) {
        return productFlow.getProductByBarcode(barcode);
    }

    public TSVUploadResponse uploadProductMasterByTsv(MultipartFile file) {
        try {
            // Parse TSV file
            List<ProductForm> forms;
            try {
                forms = TSVUtil.readFromTsv(file, ProductForm.class);
            } catch (Exception e) {
                String errorMessage = "TSV file parsing failed: " + e.getMessage();
                if (e.getMessage().contains("Invalid double value")) {
                    errorMessage += "\n\nCommon causes:\n" +
                        "1. Check that your TSV file uses TAB characters (not spaces) to separate columns\n" +
                        "2. Verify the column order: barcode, clientName, name, mrp, imageUrl\n" +
                        "3. Ensure no extra spaces or tabs in the data\n" +
                        "4. Check that MRP values are valid numbers\n" +
                        "5. Make sure each row has the same number of columns as the header";
                }
                throw new ApiException(errorMessage, e);
            }
            
            // Validate row count
            if (forms.size() > MAX_ROWS) {
                return createErrorResponse("Maximum " + MAX_ROWS + " rows allowed. Found: " + forms.size(), 
                    forms.size(), forms.size(), new ArrayList<>());
            }

            // Validate forms and collect errors
            List<ErrorTSVData> validationErrors = new ArrayList<>();
            List<ProductForm> validForms = new ArrayList<>();
            
            for (int i = 0; i < forms.size(); i++) {
                ProductForm form = forms.get(i);
                
                // Check for potential column misalignment
                if (form.getBarcode() != null && form.getBarcode().contains(" ")) {
                    // Check if this looks like misaligned data
                    if (form.getClientName() != null && isNumericString(form.getClientName())) {
                        validationErrors.add(new ErrorTSVData(
                            form.getBarcode(),
                            form.getName() != null ? form.getName() : "",
                            form.getClientName(),
                            form.getMrp() != null ? form.getMrp().toString() : "",
                            "Row " + (i + 1) + ": Possible column misalignment detected. " +
                            "Barcode contains spaces and clientName appears to be numeric. " +
                            "Please ensure TSV file uses TAB characters to separate columns."
                        ));
                        continue;
                    }
                }
                
                Set<ConstraintViolation<ProductForm>> violations = validator.validate(form);
                
                if (!violations.isEmpty()) {
                    String errorMessage = "Row " + (i + 1) + ": ";
                    List<String> errors = new ArrayList<>();
                    for (ConstraintViolation<ProductForm> violation : violations) {
                        errors.add(violation.getPropertyPath() + " - " + violation.getMessage());
                    }
                    errorMessage += String.join(", ", errors);
                    
                    validationErrors.add(new ErrorTSVData(
                        form.getBarcode() != null ? form.getBarcode() : "",
                        form.getName() != null ? form.getName() : "",
                        form.getClientName() != null ? form.getClientName() : "",
                        form.getMrp() != null ? form.getMrp().toString() : "",
                        errorMessage
                    ));
                } else {
                    validForms.add(form);
                }
            }

            // If validation errors exist, return them
            if (!validationErrors.isEmpty()) {
                byte[] errorTsvBytes = TSVUtil.createErrorTsvFromList(validationErrors);
                String fileId = tsvDownloadService.storeTSVFile(errorTsvBytes, "product_validation_errors");
                return createErrorResponse("Validation failed for " + validationErrors.size() + " rows", 
                    forms.size(), validationErrors.size(), new ArrayList<>(), "/tsv/download/" + fileId);
            }

            // Process valid forms through flow
            List<ErrorTSVData> processingErrors = new ArrayList<>();
            List<ProductForm> successForms = new ArrayList<>();
            
            for (ProductForm form : validForms) {
                try {
                    productFlow.addProduct(form);
                    successForms.add(form);
                } catch (Exception e) {
                    String errorMessage = "Processing error: " + e.getMessage();
                    processingErrors.add(new ErrorTSVData(
                        form.getBarcode() != null ? form.getBarcode() : "",
                        form.getName() != null ? form.getName() : "",
                        form.getClientName() != null ? form.getClientName() : "",
                        form.getMrp() != null ? form.getMrp().toString() : "",
                        errorMessage
                    ));
                }
            }

            // Create response based on results
            if (!processingErrors.isEmpty()) {
                byte[] errorTsvBytes = TSVUtil.createErrorTsvFromList(processingErrors);
                String fileId = tsvDownloadService.storeTSVFile(errorTsvBytes, "product_processing_errors");
                return createErrorResponse("Processing failed for " + processingErrors.size() + " rows", 
                    forms.size(), processingErrors.size(), new ArrayList<>(), "/tsv/download/" + fileId);
            } else {
                return TSVUploadResponse.success("Successfully processed " + successForms.size() + " rows", 
                    successForms.size());
            }

        } catch (Exception e) {
            throw new ApiException("Error processing TSV file: " + e.getMessage(), e);
        }
    }

    private boolean isNumericString(String str) {
        if (str == null || str.trim().isEmpty()) {
            return false;
        }
        try {
            Double.parseDouble(str.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public void update(Integer id, @Valid ProductForm productForm) {
        productFlow.updateProduct(id, productForm);
    }

    private TSVUploadResponse createErrorResponse(String message, int totalRows, int errorRows, List<String> errors) {
        return createErrorResponse(message, totalRows, errorRows, errors, null);
    }

    private TSVUploadResponse createErrorResponse(String message, int totalRows, int errorRows, List<String> errors, String downloadUrl) {
        TSVUploadResponse response = TSVUploadResponse.error(message, totalRows, errorRows, errors);
        if (downloadUrl != null) {
            response.setDownloadUrl(downloadUrl);
        }
        return response;
    }
}
