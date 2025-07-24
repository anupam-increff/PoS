package com.increff.pos.util;

import com.increff.pos.exception.ApiException;
import com.increff.pos.model.data.TSVUploadResponse;
import com.increff.pos.service.TSVDownloadService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
public class TSVUploadUtil {

    public static <T> TSVUploadResponse processTSVUpload(
            MultipartFile file,
            Class<T> formClass,
            String[] errorHeaders,
            String errorFileName,
            int maxRows,
            Consumer<T> processor,
            String successMessage,
            TSVDownloadService downloadService) {

        try {
            List<T> formList = TSVConvertUtil.readFromTsv(file, formClass);
            List<String[]> rawRows = TSVConvertUtil.readRawRows(file);

            if (formList.isEmpty()) {
                throw new ApiException("TSV file is empty or has no valid data");
            }

            if (maxRows > 0 && formList.size() > maxRows) {
                throw new ApiException("Maximum " + maxRows + " rows allowed per upload but found: " + formList.size());
            }

            List<String> successList = new ArrayList<>();
            List<String> failureList = new ArrayList<>();

            for (int i = 0; i < formList.size(); i++) {
                T form = formList.get(i);
                try {
                    validate(form);
                    processor.accept(form);
                    successList.add("Row " + (i + 1));
                } catch (ApiException e) {
                    String[] row = Arrays.copyOf(rawRows.get(i), rawRows.get(i).length + 1);
                    row[row.length - 1] = e.getMessage();
                    failureList.add(String.join("\t", row));
                }
            }

            if (!failureList.isEmpty()) {
                List<String[]> errorRows = failureList.stream()
                        .map(s -> s.split("\t", -1))
                        .collect(java.util.stream.Collectors.toList());
                byte[] errorTsv = TSVConvertUtil.createTsvFromRows(errorRows, errorHeaders);
                String fileId = downloadService.storeTSVFile(errorTsv, errorFileName);

                TSVUploadResponse resp = TSVUploadResponse.error(
                        "TSV processing completed with errors. " + successList.size() + " " + successMessage,
                        formList.size(),
                        failureList.size(),
                        failureList
                );
                resp.setDownloadUrl("/tsv/download/" + fileId);
                return resp;
            } else {
                return TSVUploadResponse.success(
                        "All " + successList.size() + " " + successMessage,
                        successList.size()
                );
            }

        } catch (Exception e) {
            throw new ApiException("Failed to process TSV file: " + e.getMessage(), e);
        }
    }

    private static <T> void validate(T obj) throws ApiException {
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
} 