package com.increff.pos.util;

import java.lang.reflect.Field;

public class UpdateUtil {

    /**
     * Copies all non-null fields from source to target.
     */
    public static <T> void applyUpdates(T source, T target) {
        if (source == null || target == null) return;

        Class<?> clazz = source.getClass();
        while (clazz != null) {
            for (Field field : clazz.getDeclaredFields()) {
                try {
                    field.setAccessible(true);
                    Object value = field.get(source);
                    if (value != null) {
                        field.set(target, value);
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Failed to update field: " + field.getName(), e);
                }
            }
            clazz = clazz.getSuperclass();
        }
    }

    /**
     * Overwrites all fields from source to target, including nulls.
     */
    public static <T> void overwrite(T source, T target) {
        if (source == null || target == null) return;

        Class<?> clazz = source.getClass();
        while (clazz != null) {
            for (Field field : clazz.getDeclaredFields()) {
                try {
                    field.setAccessible(true);
                    field.set(target, field.get(source));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Failed to overwrite field: " + field.getName(), e);
                }
            }
            clazz = clazz.getSuperclass();
        }
    }
}
