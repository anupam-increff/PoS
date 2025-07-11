package com.increff.pos.util;

import com.increff.pos.exception.ApiException;
import com.increff.pos.model.data.ErrorTSVData;
import com.increff.pos.model.data.TSVUploadResponse;
import com.increff.pos.service.TSVDownloadService;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class BulkUploadUtil {

    /**
     * Generic bulk upload processor that handles TSV validation and processing
     * @param forms List of forms to process
     * @param formClass Class type for validation
     * @param processor Function to process each form
     * @param tsvDownloadService Service for storing TSV files
     * @param errorFilePrefix Prefix for error file names
     * @param successFilePrefix Prefix for success file names
     * @return TSVUploadResponse with results
     */
    public static <T> TSVUploadResponse processBulkUpload(
            List<T> forms,
            Class<T> formClass,
            Function<T, Void> processor,
            TSVDownloadService tsvDownloadService,
            String errorFilePrefix,
            String successFilePrefix) {

        // Validate forms first
        List<ErrorTSVData> validationErrors = TSVUtil.validateAndGetErrors(forms, formClass);
        if (!validationErrors.isEmpty()) {
            byte[] errorTsvBytes = TSVUtil.createErrorTsvFromList(validationErrors);
            String fileId = tsvDownloadService.storeTSVFile(errorTsvBytes, errorFilePrefix + "_validation_errors");
            List<String> errorMessages = new ArrayList<>();
            for (ErrorTSVData error : validationErrors) {
                errorMessages.add(error.getErrorMessage());
            }
            TSVUploadResponse response = TSVUploadResponse.error(
                "Validation failed for " + validationErrors.size() + " rows", 
                forms.size(), 
                validationErrors.size(), 
                errorMessages
            );
            response.setDownloadUrl("/tsv/download/" + fileId);
            return response;
        }

        // Process forms
        List<String> errors = new ArrayList<>();
        List<ErrorTSVData> errorDataList = new ArrayList<>();
        List<T> successForms = new ArrayList<>();
        
        for (T form : forms) {
            try {
                processor.apply(form);
                successForms.add(form);
            } catch (ApiException e) {
                errors.add(e.getMessage());
                errorDataList.add(ErrorTSVData.fromForm(form, e.getMessage()));
            } catch (Exception e) {
                errors.add(": Unexpected error - " + e.getMessage());
                errorDataList.add(ErrorTSVData.fromForm(form, e.getMessage()));
            }
        }

        if (!errors.isEmpty()) {
            byte[] errorTsvBytes = TSVUtil.createErrorTsvFromList(errorDataList);
            String fileId = tsvDownloadService.storeTSVFile(errorTsvBytes, errorFilePrefix + "_processing_errors");
            TSVUploadResponse response = TSVUploadResponse.error(
                "Processing failed for " + errors.size() + " rows", 
                forms.size(), 
                errors.size(), 
                errors
            );
            response.setDownloadUrl("/tsv/download/" + fileId);
            return response;
        } else {
            byte[] successTsvBytes = TSVUtil.createTsvFromList(successForms, formClass);
            String fileId = tsvDownloadService.storeTSVFile(successTsvBytes, successFilePrefix + "_success");
            TSVUploadResponse response = TSVUploadResponse.success(
                "Successfully processed " + successForms.size() + " rows", 
                successForms.size()
            );
            response.setDownloadUrl("/tsv/download/" + fileId);
            return response;
        }
    }
} 