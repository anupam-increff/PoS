package com.increff.pos.util;

import com.increff.pos.exception.ApiException;
import com.increff.pos.model.data.TSVUploadResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class TSVDownloadUtil {
    private static final Map<String, byte[]> tsvFiles = new ConcurrentHashMap<>();

    private TSVDownloadUtil() {}

    public static String storeTSVFile(byte[] tsvBytes) {
        String fileId = UUID.randomUUID().toString();
        tsvFiles.put(fileId, tsvBytes);
        return fileId;
    }

    public static ResponseEntity<byte[]> downloadTSVFile(String fileId, String filename) {
        byte[] tsvBytes = tsvFiles.get(fileId);
        if (tsvBytes == null) {
            throw new ApiException("File not found or expired");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf("text/tab-separated-values"));
        headers.setContentDispositionFormData("attachment", filename);
        headers.setContentLength(tsvBytes.length);

        return new ResponseEntity<>(tsvBytes, headers, HttpStatus.OK);
    }
} 