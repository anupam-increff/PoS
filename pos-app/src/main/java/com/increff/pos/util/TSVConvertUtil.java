package com.increff.pos.util;

import com.increff.pos.exception.ApiException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class TSVConvertUtil {

    /**
     * Convert TSV file to List of specified class objects
     */
    public static <T> List<T> readFromTsv(MultipartFile file, Class<T> clazz) {
        try (Reader reader = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8)) {
            CSVParser parser = CSVFormat.TDF.withFirstRecordAsHeader().parse(reader);
            List<T> list = new ArrayList<>();

            for (CSVRecord record : parser) {
                T obj = TSVRowMapper.map(record, clazz);
                list.add(obj);
            }

            return list;
        } catch (Exception e) {
            throw new ApiException("Error processing TSV file: " + e.getMessage(), e);
        }
    }

    /**
     * Create TSV from list of String[] rows directly.
     */
    public static byte[] createTsvFromRows(List<String[]> rows, String[] headers) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             OutputStreamWriter writer = new OutputStreamWriter(baos, StandardCharsets.UTF_8);
             CSVPrinter printer = CSVFormat.TDF.print(writer)) {

            if (headers != null && headers.length > 0) {
                printer.printRecord((Object[]) headers);
            }

            for (String[] row : rows) {
                printer.printRecord((Object[]) row);
            }

            printer.flush();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new ApiException("Error creating TSV file: " + e.getMessage(), e);
        }
    }

    public static List<String[]> readRawRows(MultipartFile file) {
        try (Reader reader = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8)) {
            CSVParser parser = CSVFormat.TDF.withFirstRecordAsHeader().parse(reader);
            List<String[]> rows = new ArrayList<>();
            for (CSVRecord record : parser) {
                String[] arr = new String[record.size()];
                for (int i = 0; i < record.size(); i++) {
                    arr[i] = record.get(i);
                }
                rows.add(arr);
            }
            return rows;
        } catch (Exception e) {
            throw new ApiException("Error reading TSV file: " + e.getMessage(), e);
        }
    }
}
