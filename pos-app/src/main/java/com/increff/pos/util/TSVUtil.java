package com.increff.pos.util;

import com.increff.pos.exception.ApiException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.io.InputStreamReader;
import java.io.Reader;
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
}
