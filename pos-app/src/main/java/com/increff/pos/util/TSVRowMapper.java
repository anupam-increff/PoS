package com.increff.pos.util;

import org.apache.commons.csv.CSVRecord;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class TSVRowMapper {

    public static <T> T map(CSVRecord record, Class<T> clazz) throws Exception {
        T instance = clazz.getDeclaredConstructor().newInstance();

        // Create a mapping of available headers to their values
        Map<String, String> headerValueMap = new HashMap<>();
        try {
            Map<String, String> recordMap = record.toMap();
            for (Map.Entry<String, String> entry : recordMap.entrySet()) {
                String header = entry.getKey();
                String value = entry.getValue();

                if (value != null) {
                    value = value.trim();
                    // Convert empty strings to null
                    if (value.isEmpty()) {
                        value = null;
                    }
                }

                if (header != null) {
                    headerValueMap.put(header.toLowerCase().trim(), value);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Row " + record.getRecordNumber() + ": Error parsing TSV row: " + e.getMessage());
        }

        // Map fields based on available headers
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            String fieldName = field.getName().toLowerCase();

            // Try to find the value for this field in the header map
            String value = headerValueMap.get(fieldName);

            // Also try common variations of field names
            if (value == null) {
                // Try with underscores (e.g., client_name for clientName)
                String underscoreName = camelToUnderscore(field.getName()).toLowerCase();
                value = headerValueMap.get(underscoreName);
            }

            // If header map approach failed, try direct field access
            if (value == null) {
                try {
                    if (record.isMapped(field.getName())) {
                        value = record.get(field.getName());
                        if (value != null) {
                            value = value.trim();
                            if (value.isEmpty()) {
                                value = null;
                            }
                        }
                    }
                } catch (Exception e) {
                    // Field doesn't exist in TSV, skip it
                }
            }

            // Skip null or empty values
            if (value == null) {
                continue;
            }

            Class<?> type = field.getType();
            try {
                if (type.equals(Integer.class) || type.equals(int.class)) {
                    field.set(instance, Integer.parseInt(value));
                } else if (type.equals(Double.class) || type.equals(double.class)) {
                    field.set(instance, Double.parseDouble(value));
                } else if (type.equals(String.class)) {
                    field.set(instance, value);
                } else {
                    // For other types, try to set as string
                    field.set(instance, value);
                }
            } catch (NumberFormatException e) {
                throw new RuntimeException("Row " + (record.getRecordNumber()) + ": Invalid " + type.getSimpleName().toLowerCase() + " value for field '" + field.getName() + "': '" + value + "'. Expected a valid " + type.getSimpleName().toLowerCase() + ". Please check that your TSV file is properly formatted with tab characters separating columns.");
            } catch (Exception e) {
                throw new RuntimeException("Row " + (record.getRecordNumber()) + ": Error setting value for field '" + field.getName() + "': " + e.getMessage());
            }
        }

        return instance;
    }

    private static String camelToUnderscore(String camelCase) {
        return camelCase.replaceAll("([a-z])([A-Z])", "$1_$2");
    }
}
