package com.increff.pos.util;

import com.increff.pos.exception.ApiException;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Objects;

public class ValidationUtil {
    public static <T> void validate(T obj) throws ApiException {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<T>> violations = validator.validate(obj);
        if (violations.isEmpty()) {
            return;
        }
        
        List<String> errorMessages = new ArrayList<>();
        for (ConstraintViolation<T> violation : violations) {
            errorMessages.add(violation.getPropertyPath() + ": " + violation.getMessage());
        }
        throw new ApiException("Validation failed: " + String.join(", ", errorMessages));
    }

    public static <T> void checkNonNull(T obj, String errorMessage) throws ApiException {
        if (Objects.nonNull(obj)) {
            throw new ApiException(errorMessage);
        }
    }
    
    public static <T> void checkNull(T obj, String errorMessage) throws ApiException {
        if (Objects.isNull(obj)) {
            throw new ApiException(errorMessage);
        }
    }
} 