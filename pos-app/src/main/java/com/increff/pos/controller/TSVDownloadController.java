package com.increff.pos.controller;

import com.increff.pos.exception.ApiException;
import com.increff.pos.service.TSVDownloadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/api/tsv")
public class TSVDownloadController {

    @Autowired
    private TSVDownloadService tsvDownloadService;

    @GetMapping("/download/{fileId}")
    public ResponseEntity<byte[]> downloadTsv(@PathVariable String fileId) {

        byte[] tsvBytes = tsvDownloadService.getTSVFile(fileId);

        if (Objects.isNull(tsvBytes)) {
            throw new ApiException("File not found or expired");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf("text/tab-separated-values"));
        headers.setContentDispositionFormData("attachment", "upload_result.tsv");
        headers.setContentLength(tsvBytes.length);

        return new ResponseEntity<>(tsvBytes, headers, HttpStatus.OK);
    }
} 