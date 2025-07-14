package com.increff.pos.util;

import com.increff.pos.exception.ApiException;

import java.lang.reflect.Field;
import java.util.Objects;

public class ConvertUtil {

    public static <T> T convert(Object source, Class<T> targetClass) throws ApiException {
        if (source == null || targetClass == null) {
            throw new ApiException("Source or target class must not be null");
        }

        try {
            T target = targetClass.newInstance(); // for Java 1.8

            Field[] sourceFields = source.getClass().getDeclaredFields();
            Field[] targetFields = targetClass.getDeclaredFields();

            for (Field sf : sourceFields) {
                sf.setAccessible(true);
                Object value = sf.get(source);

                for (Field tf : targetFields) {
                    if (sf.getName().equals(tf.getName()) &&
                            sf.getType().equals(tf.getType())) {

                        tf.setAccessible(true);
                        if (tf.getType() == String.class && !Objects.isNull(value)) {
                            // trim string values
                            tf.set(target, value.toString().trim());
                        } else {
                            tf.set(target, value);
                        }
                        break;
                    }
                }
            }
            return target;
        } catch (IllegalAccessException | InstantiationException e) {
            throw new ApiException("Failed to convert: Error type " + e.getClass() + " with error " + e.getMessage());
        }
    }
}