package com.increff.pos.util;

import com.increff.pos.exception.ApiException;
import com.increff.pos.model.data.ErrorTSVData;
import com.increff.pos.model.data.TSVUploadResponse;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class TSVUtil {

    private static TSVUtil instance;
    private final Validator validator;

    @Autowired
    public TSVUtil(Validator validator) {
        this.validator = validator;
        TSVUtil.instance = this;
    }

    public static <T> List<T> readFromTsv(MultipartFile file, Class<T> clazz) {
        try (Reader reader = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8)) {
            CSVParser parser = CSVFormat.TDF.withFirstRecordAsHeader().parse(reader);
            List<T> list = new ArrayList<>();

            for (CSVRecord record : parser) {
                T obj = TSVRowMapper.map(record, clazz);
                list.add(obj);
            }

            return list;
        } catch (Exception e) {
            throw new ApiException("Error processing TSV file: " + e.getMessage() , e);
        }
    }

    public static <T> List<ErrorTSVData> validateAndGetErrors(List<T> dataList, Class<T> clazz) {
        if (instance == null || instance.validator == null) {
            throw new ApiException("TSVUtil not properly initialized. Validator is null.");
        }
        
        List<ErrorTSVData> errorDataList = new ArrayList<>();
        
        for (int i = 0; i < dataList.size(); i++) {
            T item = dataList.get(i);
            Set<ConstraintViolation<T>> violations = instance.validator.validate(item);
            
            if (!violations.isEmpty()) {
                String errorMessage = "Row " + (i + 1) + ": ";
                List<String> errors = new ArrayList<>();
                for (ConstraintViolation<T> violation : violations) {
                    errors.add(violation.getPropertyPath() + " - " + violation.getMessage());
                }
                errorMessage += String.join(", ", errors);
                
                // Create ErrorTSVData based on the class type
                if (clazz.getSimpleName().equals("ProductForm")) {
                    com.increff.pos.model.form.ProductForm form = (com.increff.pos.model.form.ProductForm) item;
                    errorDataList.add(new ErrorTSVData(
                        form.getBarcode() != null ? form.getBarcode() : "",
                        form.getName() != null ? form.getName() : "",
                        form.getClientName() != null ? form.getClientName() : "",
                        form.getMrp() != null ? form.getMrp().toString() : "",
                        errorMessage
                    ));
                } else if (clazz.getSimpleName().equals("InventoryForm")) {
                    com.increff.pos.model.form.InventoryForm form = (com.increff.pos.model.form.InventoryForm) item;
                    errorDataList.add(new ErrorTSVData(
                        form.getBarcode() != null ? form.getBarcode() : "",
                        form.getQuantity() != null ? form.getQuantity().toString() : "",
                        errorMessage
                    ));
                }
            }
        }
        
        return errorDataList;
    }

    public static <T> byte[] createTsvFromList(List<T> dataList, Class<T> clazz) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             OutputStreamWriter writer = new OutputStreamWriter(baos, StandardCharsets.UTF_8);
             CSVPrinter printer = CSVFormat.TDF.print(writer)) {
            
            // Get headers from the first object or use reflection
            if (!dataList.isEmpty()) {
                T firstItem = dataList.get(0);
                String[] headers = getHeadersFromObject(firstItem);
                printer.printRecord((Object[]) headers);
                
                for (T item : dataList) {
                    String[] values = getValuesFromObject(item);
                    printer.printRecord((Object[]) values);
                }
            }
            
            printer.flush();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new ApiException("Error creating TSV file: " + e.getMessage(), e);
        }
    }

    public static byte[] createErrorTsvFromList(List<ErrorTSVData> errorDataList) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             OutputStreamWriter writer = new OutputStreamWriter(baos, StandardCharsets.UTF_8);
             CSVPrinter printer = CSVFormat.TDF.print(writer)) {
            
            if (!errorDataList.isEmpty()) {
                ErrorTSVData firstItem = errorDataList.get(0);
                String[] headers = getErrorHeadersFromObject(firstItem);
                printer.printRecord((Object[]) headers);
                
                for (ErrorTSVData item : errorDataList) {
                    String[] values = getErrorValuesFromObject(item);
                    printer.printRecord((Object[]) values);
                }
            }
            
            printer.flush();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new ApiException("Error creating error TSV file: " + e.getMessage(), e);
        }
    }

    private static <T> String[] getHeadersFromObject(T obj) {
        // This is a simplified version - in a real implementation, you might want to use reflection
        // or have specific implementations for each type
        if (obj instanceof com.increff.pos.model.form.ProductForm) {
            return new String[]{"barcode", "name", "clientName", "mrp"};
        } else if (obj instanceof com.increff.pos.model.form.InventoryForm) {
            return new String[]{"barcode", "quantity"};
        } else if (obj instanceof com.increff.pos.model.form.ClientForm) {
            return new String[]{"name"};
        }
        return new String[]{"data"};
    }

    private static <T> String[] getValuesFromObject(T obj) {
        if (obj instanceof com.increff.pos.model.form.ProductForm) {
            com.increff.pos.model.form.ProductForm form = (com.increff.pos.model.form.ProductForm) obj;
            return new String[]{
                form.getBarcode() != null ? form.getBarcode() : "",
                form.getName() != null ? form.getName() : "",
                form.getClientName() != null ? form.getClientName() : "",
                form.getMrp() != null ? form.getMrp().toString() : ""
            };
        } else if (obj instanceof com.increff.pos.model.form.InventoryForm) {
            com.increff.pos.model.form.InventoryForm form = (com.increff.pos.model.form.InventoryForm) obj;
            return new String[]{
                form.getBarcode() != null ? form.getBarcode() : "",
                form.getQuantity() != null ? form.getQuantity().toString() : ""
            };
        } else if (obj instanceof com.increff.pos.model.form.ClientForm) {
            com.increff.pos.model.form.ClientForm form = (com.increff.pos.model.form.ClientForm) obj;
            return new String[]{form.getName() != null ? form.getName() : ""};
        }
        return new String[]{obj.toString()};
    }

    private static String[] getErrorHeadersFromObject(ErrorTSVData obj) {
        if (obj.getBarcode() != null && obj.getName() != null && obj.getClientName() != null && obj.getMrp() != null) {
            return new String[]{"barcode", "name", "clientName", "mrp", "errorMessage"};
        } else if (obj.getBarcode() != null && obj.getQuantity() != null) {
            return new String[]{"barcode", "quantity", "errorMessage"};
        } else if (obj.getName() != null) {
            return new String[]{"name", "errorMessage"};
        }
        return new String[]{"data", "errorMessage"};
    }

    private static String[] getErrorValuesFromObject(ErrorTSVData obj) {
        if (obj.getBarcode() != null && obj.getName() != null && obj.getClientName() != null && obj.getMrp() != null) {
            return new String[]{obj.getBarcode(), obj.getName(), obj.getClientName(), obj.getMrp(), obj.getErrorMessage()};
        } else if (obj.getBarcode() != null && obj.getQuantity() != null) {
            return new String[]{obj.getBarcode(), obj.getQuantity(), obj.getErrorMessage()};
        } else if (obj.getName() != null) {
            return new String[]{obj.getName(), obj.getErrorMessage()};
        }
        return new String[]{obj.toString(), obj.getErrorMessage()};
    }

    public static <T> TSVUploadResponse processTSV(
            MultipartFile file, 
            Class<T> formClass,
            java.util.function.Consumer<T> processor,
            String successMessage) {
        try {
            // Step 1: Convert TSV file to list of form objects
            List<T> forms = readFromTsv(file, formClass);
            
            if (forms.isEmpty()) {
                throw new ApiException("TSV file is empty or has no valid data");
            }

            // Step 2: Validate forms and collect validation errors
            List<ErrorTSVData> validationErrors = validateAndGetErrors(forms, formClass);
            
            // Step 3: Create set of row numbers that have validation errors for quick lookup
            List<Integer> validationErrorRows = extractErrorRows(validationErrors);
            
            // Step 4: Process each valid form and collect processing errors
            List<ErrorTSVData> processingErrors = new ArrayList<>();
            int successCount = 0;
            
            for (int i = 0; i < forms.size(); i++) {
                T form = forms.get(i);
                int currentRow = i + 1;
                
                // Skip if this form already has validation errors
                if (validationErrorRows.contains(currentRow)) {
                    continue;
                }
                
                try {
                    processor.accept(form);
                    successCount++;
                } catch (ApiException e) {
                    processingErrors.add(createErrorData(form, formClass, currentRow, e.getMessage()));
                } catch (Exception e) {
                    processingErrors.add(createErrorData(form, formClass, currentRow, "Unexpected error - " + e.getMessage()));
                }
            }
            
            // Step 5: Combine all errors and return response
            return createTSVResponse(forms, validationErrors, processingErrors, successCount, successMessage);
            
        } catch (Exception e) {
            throw new ApiException("Failed to process TSV file: " + e.getMessage(), e);
        }
    }

    private static List<Integer> extractErrorRows(List<ErrorTSVData> validationErrors) {
        List<Integer> errorRows = new ArrayList<>();
        for (ErrorTSVData error : validationErrors) {
            String errorMsg = error.getErrorMessage();
            if (errorMsg.startsWith("Row ")) {
                try {
                    int rowNum = Integer.parseInt(errorMsg.substring(4, errorMsg.indexOf(":")));
                    errorRows.add(rowNum);
                } catch (Exception e) {
                    // Ignore parsing errors
                }
            }
        }
        return errorRows;
    }

    private static <T> ErrorTSVData createErrorData(T form, Class<T> formClass, int currentRow, String message) {
        String rowMessage = "Row " + currentRow + ": " + message;
        
        if (formClass.getSimpleName().equals("ProductForm")) {
            com.increff.pos.model.form.ProductForm productForm = (com.increff.pos.model.form.ProductForm) form;
            return new ErrorTSVData(
                productForm.getBarcode() != null ? productForm.getBarcode() : "",
                productForm.getName() != null ? productForm.getName() : "",
                productForm.getClientName() != null ? productForm.getClientName() : "",
                productForm.getMrp() != null ? productForm.getMrp().toString() : "",
                rowMessage
            );
        } else if (formClass.getSimpleName().equals("InventoryForm")) {
            com.increff.pos.model.form.InventoryForm inventoryForm = (com.increff.pos.model.form.InventoryForm) form;
            return new ErrorTSVData(
                inventoryForm.getBarcode() != null ? inventoryForm.getBarcode() : "",
                inventoryForm.getQuantity() != null ? inventoryForm.getQuantity().toString() : "",
                rowMessage
            );
        }
        
        return new ErrorTSVData("", "", "", "", rowMessage);
    }

    private static <T> TSVUploadResponse createTSVResponse(
            List<T> forms, 
            List<ErrorTSVData> validationErrors, 
            List<ErrorTSVData> processingErrors, 
            int successCount, 
            String successMessage) {
        
        List<ErrorTSVData> allErrors = new ArrayList<>();
        allErrors.addAll(validationErrors);
        allErrors.addAll(processingErrors);
        
        if (!allErrors.isEmpty()) {
            List<String> errorMessages = allErrors.stream()
                .map(ErrorTSVData::getErrorMessage)
                .collect(java.util.stream.Collectors.toList());
                
            return TSVUploadResponse.error(
                "TSV processing completed with errors. " + successCount + " items processed successfully.",
                forms.size(),
                allErrors.size(),
                errorMessages
            );
        } else {
            return TSVUploadResponse.success(successMessage + " " + successCount + " items", successCount);
        }
    }
}
