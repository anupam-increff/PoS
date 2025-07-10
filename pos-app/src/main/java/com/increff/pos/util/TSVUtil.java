package com.increff.pos.util;

import com.increff.pos.exception.ApiException;
import com.increff.pos.model.data.ErrorTSVData;
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
}
