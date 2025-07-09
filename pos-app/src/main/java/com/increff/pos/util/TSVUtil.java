package com.increff.pos.util;

import com.increff.pos.exception.ApiException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class TSVUtil {

    private static Validator validator;

    public TSVUtil(Validator validator) {
        TSVUtil.validator = validator;
    }

    public static <T> List<T> readFromTsv(MultipartFile file, Class<T> clazz) {
        try (Reader reader = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8)) {
            CSVParser parser = CSVFormat.TDF.withFirstRecordAsHeader().parse(reader);
            List<T> list = new ArrayList<>();
            List<String> errors = new ArrayList<>();

            int rowNum = 1;
            for (CSVRecord record : parser) {
                T obj = TSVRowMapper.map(record, clazz);
                Set<ConstraintViolation<T>> violations = validator.validate(obj);

                if (!violations.isEmpty()) {
                    for (ConstraintViolation<T> violation : violations) {
                        errors.add("Row " + rowNum + ": " +
                                violation.getPropertyPath() + " - " + violation.getMessage());
                    }
                } else {
                    list.add(obj);
                }

                rowNum++;
            }

            if (!errors.isEmpty()) {
                throw new ApiException("TSV validation failed:\n" + String.join("\n", errors));
            }

            return list;
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("Error processing TSV file: " + e.getMessage() , e);
        }
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
            return new String[]{form.getBarcode(), form.getName(), form.getClientName(), form.getMrp().toString()};
        } else if (obj instanceof com.increff.pos.model.form.InventoryForm) {
            com.increff.pos.model.form.InventoryForm form = (com.increff.pos.model.form.InventoryForm) obj;
            return new String[]{form.getBarcode(), form.getQuantity().toString()};
        } else if (obj instanceof com.increff.pos.model.form.ClientForm) {
            com.increff.pos.model.form.ClientForm form = (com.increff.pos.model.form.ClientForm) obj;
            return new String[]{form.getName()};
        }
        return new String[]{obj.toString()};
    }
}
