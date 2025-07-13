package com.increff.pos.dto;

import com.increff.pos.exception.ApiException;
import com.increff.pos.flow.InventoryFlow;
import com.increff.pos.model.data.ErrorTSVData;
import com.increff.pos.model.data.PaginatedResponse;
import com.increff.pos.model.data.InventoryData;
import com.increff.pos.model.data.TSVUploadResponse;
import com.increff.pos.model.form.InventoryForm;
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
public class InventoryDto extends BaseDto {

    private static final int MAX_ROWS = 5000;

    @Autowired
    private InventoryFlow inventoryFlow;

    @Autowired
    private TSVDownloadService tsvDownloadService;

    @Autowired
    private Validator validator;

    public void addInventory(@Valid InventoryForm inventoryForm) {
        inventoryFlow.addInventory(inventoryForm.getBarcode(), inventoryForm.getQuantity());
    }

    public PaginatedResponse<InventoryData> getAll(int page, int pageSize) {
        return inventoryFlow.getAll(page, pageSize);
    }

    public PaginatedResponse<InventoryData> searchByBarcode(String barcode, int page, int pageSize) {
        return inventoryFlow.searchByBarcode(barcode, page, pageSize);
    }

    public TSVUploadResponse uploadInventoryByTsv(MultipartFile file) {
        try {
            // Parse TSV file
            List<InventoryForm> forms = TSVUtil.readFromTsv(file, InventoryForm.class);
            
            // Validate row count
            if (forms.size() > MAX_ROWS) {
                return createErrorResponse("Maximum " + MAX_ROWS + " rows allowed. Found: " + forms.size(), 
                    forms.size(), forms.size(), new ArrayList<>());
            }

            // Validate forms and collect errors
            List<ErrorTSVData> validationErrors = new ArrayList<>();
            List<InventoryForm> validForms = new ArrayList<>();
            
            for (int i = 0; i < forms.size(); i++) {
                InventoryForm form = forms.get(i);
                Set<ConstraintViolation<InventoryForm>> violations = validator.validate(form);
                
                if (!violations.isEmpty()) {
                    String errorMessage = "Row " + (i + 1) + ": ";
                    List<String> errors = new ArrayList<>();
                    for (ConstraintViolation<InventoryForm> violation : violations) {
                        errors.add(violation.getPropertyPath() + " - " + violation.getMessage());
                    }
                    errorMessage += String.join(", ", errors);
                    
                    validationErrors.add(new ErrorTSVData(
                        form.getBarcode() != null ? form.getBarcode() : "",
                        form.getQuantity() != null ? form.getQuantity().toString() : "",
                        errorMessage
                    ));
                } else {
                    validForms.add(form);
                }
            }

            // If validation errors exist, return them
            if (!validationErrors.isEmpty()) {
                byte[] errorTsvBytes = TSVUtil.createErrorTsvFromList(validationErrors);
                String fileId = tsvDownloadService.storeTSVFile(errorTsvBytes, "inventory_validation_errors");
                return createErrorResponse("Validation failed for " + validationErrors.size() + " rows", 
                    forms.size(), validationErrors.size(), new ArrayList<>(), "/tsv/download/" + fileId);
            }

            // Process valid forms through flow
            List<ErrorTSVData> processingErrors = new ArrayList<>();
            List<InventoryForm> successForms = new ArrayList<>();
            
            for (InventoryForm form : validForms) {
                try {
                    inventoryFlow.addInventory(form.getBarcode(), form.getQuantity());
                    successForms.add(form);
                } catch (Exception e) {
                    String errorMessage = "Processing error: " + e.getMessage();
                    processingErrors.add(new ErrorTSVData(
                        form.getBarcode() != null ? form.getBarcode() : "",
                        form.getQuantity() != null ? form.getQuantity().toString() : "",
                        errorMessage
                    ));
                }
            }

            // Create response based on results
            if (!processingErrors.isEmpty()) {
                byte[] errorTsvBytes = TSVUtil.createErrorTsvFromList(processingErrors);
                String fileId = tsvDownloadService.storeTSVFile(errorTsvBytes, "inventory_processing_errors");
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

    public void updateByBarcode(@Valid InventoryForm inventoryForm) {
        inventoryFlow.updateInventory(inventoryForm.getBarcode(), inventoryForm.getQuantity());
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
