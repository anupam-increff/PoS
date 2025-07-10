package com.increff.pos.model.data;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TSVUploadResponse {
    private boolean success;
    private String message;
    private int totalRows;
    private int successRows;
    private int errorRows;
    private List<String> errors;
    private String downloadUrl; // URL to download the result file

    public TSVUploadResponse() {
    }

    public TSVUploadResponse(boolean success, String message, int totalRows, int successRows, int errorRows) {
        this.success = success;
        this.message = message;
        this.totalRows = totalRows;
        this.successRows = successRows;
        this.errorRows = errorRows;
    }

    public static TSVUploadResponse success(String message, int totalRows) {
        return new TSVUploadResponse(true, message, totalRows, totalRows, 0);
    }

    public static TSVUploadResponse error(String message, int totalRows, int errorRows, List<String> errors) {
        TSVUploadResponse response = new TSVUploadResponse(false, message, totalRows, totalRows - errorRows, errorRows);
        response.setErrors(errors);
        return response;
    }
} 