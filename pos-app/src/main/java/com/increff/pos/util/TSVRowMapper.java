package com.increff.pos.util;

import org.apache.commons.csv.CSVRecord;

import java.lang.reflect.Field;

public class TSVRowMapper {

    public static <T> T map(CSVRecord record, Class<T> clazz) throws Exception {
        T instance = clazz.getDeclaredConstructor().newInstance();

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);

            String columnName = field.getName();
            String value = record.isMapped(columnName) ? record.get(columnName).trim() : null;

            if (value == null || value.isEmpty()) {
                // Don't set null/empty values, let Bean Validation handle required checks
                continue;
            }

            Class<?> type = field.getType();
            try {
                if (type.equals(Integer.class) || type.equals(int.class)) {
                    field.set(instance, Integer.parseInt(value));
                } else if (type.equals(Double.class) || type.equals(double.class)) {
                    field.set(instance, Double.parseDouble(value));
                } else {
                    field.set(instance, value);
                }
            } catch (Exception e) {
                throw new RuntimeException("Invalid value for field '" + field.getName() + "': '" + value + "'");
            }
        }

        return instance;
    }
}
