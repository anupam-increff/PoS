package com.increff.pos.util;

import com.increff.pos.exception.ApiException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Utility class for TSV file operations.
 * Contains only static methods, no Spring dependency injection needed.
 */
public class TSVUtil {

    /**
     * Convert TSV file to List of specified class objects
     */
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
            throw new ApiException("Error processing TSV file: " + e.getMessage(), e);
        }
    }

    /**
     * Convert List of objects to TSV byte array using reflection
     */
    public static <T> byte[] createTsvFromList(List<T> dataList, String[] headers) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             OutputStreamWriter writer = new OutputStreamWriter(baos, StandardCharsets.UTF_8);
             CSVPrinter printer = CSVFormat.TDF.print(writer)) {
            
            if (headers != null && headers.length > 0) {
                printer.printRecord((Object[]) headers);
            }
            
            for (T item : dataList) {
                String[] values = getValuesFromObject(item);
                printer.printRecord((Object[]) values);
            }
            
            printer.flush();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new ApiException("Error creating TSV file: " + e.getMessage(), e);
        }
    }

    /**
     * Create TSV from list of String[] rows directly.
     */
    public static byte[] createTsvFromRows(List<String[]> rows, String[] headers) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             OutputStreamWriter writer = new OutputStreamWriter(baos, StandardCharsets.UTF_8);
             CSVPrinter printer = CSVFormat.TDF.print(writer)) {

            if (headers != null && headers.length > 0) {
                printer.printRecord((Object[]) headers);
            }

            for (String[] row : rows) {
                printer.printRecord((Object[]) row);
            }

            printer.flush();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new ApiException("Error creating TSV file: " + e.getMessage(), e);
        }
    }

    public static List<String[]> readRawRows(MultipartFile file){
        try(Reader reader = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8)){
            CSVParser parser = CSVFormat.TDF.withFirstRecordAsHeader().parse(reader);
            List<String[]> rows = new ArrayList<>();
            for(CSVRecord record: parser){
                String[] arr = new String[record.size()];
                for(int i=0;i<record.size();i++){
                    arr[i] = record.get(i);
                }
                rows.add(arr);
            }
            return rows;
        }catch(Exception e){
            throw new ApiException("Error reading TSV file: "+e.getMessage(),e);
        }
    }

    /**
     * Extract values from object using reflection
     */
    private static String[] getValuesFromObject(Object obj) {
        if (obj == null) return new String[0];

        if (obj instanceof String) {
            return new String[]{(String) obj};
        }
        
        Class<?> clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();
        List<String> values = new ArrayList<>();
        
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                Object value = field.get(obj);
                values.add(value != null ? value.toString() : "");
            } catch (Exception e) {
                values.add("");
            }
        }
        
        return values.toArray(new String[0]);
    }
}
