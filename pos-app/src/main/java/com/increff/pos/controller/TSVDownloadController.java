package com.increff.pos.controller;

import com.increff.pos.util.TSVDownloadUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tsv")
public class TSVDownloadController {

    @GetMapping("/download/{fileId}")
    public ResponseEntity<byte[]> downloadTsv(@PathVariable String fileId) {
        return TSVDownloadUtil.downloadTSVFile(fileId, "upload_result.tsv");
    }
} 