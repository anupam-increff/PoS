package com.increff.pos.service;

import com.increff.pos.exception.ApiException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Transactional(rollbackFor = ApiException.class)
public class TSVDownloadService
{

    private final Map<String, byte[]> tsvFiles = new ConcurrentHashMap<>();

    public String storeTSVFile(byte[] tsvBytes, String filename) {
        String fileId = UUID.randomUUID().toString();
        tsvFiles.put(fileId, tsvBytes);
        return fileId;
    }

    public byte[] getTSVFile(String fileId) {
        return tsvFiles.get(fileId);
    }

}