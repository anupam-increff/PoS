package com.increff.pos.util;

import com.increff.pos.exception.ApiException;
import com.increff.pos.model.data.TSVUploadResponse;
import com.increff.pos.service.TSVDownloadService;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class TSVUploadUtil {

    private TSVUploadUtil() {}

    public static <T> TSVUploadResponse processTSVUpload(
            MultipartFile file, 
            Class<T> formClass, 
            Consumer<T> processor,
            TSVDownloadService tsvDownloadService) {
        
        try {
            // Use existing TSVConvertUtil for reading
            List<T> formList = TSVConvertUtil.readFromTsv(file, formClass);
            List<String[]> rawRows = TSVConvertUtil.readRawRows(file);
            
            if (formList.isEmpty()) {
                throw new ApiException("TSV file is empty or has no valid data");
            }
            
            if (formList.size() > AppConstants.MAX_ROWS) {
                throw new ApiException("Maximum " + AppConstants.MAX_ROWS + " rows allowed per upload but found: " + formList.size());
            }
            
            // Generate error headers using reflection
            String[] errorHeaders = generateErrorHeaders(formClass);
            
            List<String> successList = new ArrayList<>();
            List<String> failureList = new ArrayList<>();
            
            for (int i = 0; i < formList.size(); i++) {
                T form = formList.get(i);
                try {
                    processor.accept(form);
                    successList.add("Row " + (i + 1));
                } catch (Exception e) {
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
                String fileId = tsvDownloadService.storeTSVFile(errorTsv);
                
                TSVUploadResponse resp = TSVUploadResponse.error(
                        "Upload completed with errors. " + successList.size() + " " + AppConstants.DEFAULT_SUCCESS_MESSAGE,
                        formList.size(),
                        failureList.size(),
                        failureList
                );
                resp.setDownloadUrl("/tsv/download/" + fileId);
                return resp;
            } else {
                return TSVUploadResponse.success(
                        AppConstants.DEFAULT_SUCCESS_MESSAGE + ". All " + successList.size() + " items processed.",
                        successList.size()
                );
            }
            
        } catch (Exception e) {
            throw new ApiException("Error processing TSV file: " + e.getMessage());
        }
    }
    
    /**
     * Generate error headers using reflection on the form class
     */
    private static <T> String[] generateErrorHeaders(Class<T> formClass) {
        Field[] fields = formClass.getDeclaredFields();
        List<String> headers = new ArrayList<>();
        
        for (Field field : fields) {
            // Convert camelCase to Title Case for headers
            String fieldName = field.getName();
            String headerName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            headers.add(headerName);
        }
        
        // Add Error column at the end
        headers.add("Error");
        
        return headers.toArray(new String[0]);
    }
} 