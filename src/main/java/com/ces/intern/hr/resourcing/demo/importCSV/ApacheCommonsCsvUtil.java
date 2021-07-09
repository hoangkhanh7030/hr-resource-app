package com.ces.intern.hr.resourcing.demo.importCSV;

import com.ces.intern.hr.resourcing.demo.dto.ProjectDTO;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ApacheCommonsCsvUtil {
    private static final String csvExtension = "csv";

    public static List<ProjectDTO> parseCsvFile(InputStream is) {
        BufferedReader fileReader = null;
        CSVParser csvParser = null;

        List<ProjectDTO> projectDTOList = new ArrayList<>();

        try {
            fileReader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            csvParser = new CSVParser(fileReader,
                    CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());

            Iterable<CSVRecord> csvRecords = csvParser.getRecords();

            for (CSVRecord csvRecord : csvRecords) {
                ProjectDTO projectDTO = new ProjectDTO(csvRecord.get("name"),
                        csvRecord.get("clientName"),csvRecord.get("color"),csvRecord.get("textColor"),
                        csvRecord.get("colorPattern"),Boolean.TRUE);

                projectDTOList.add(projectDTO);
            }

        } catch (Exception e) {
            System.out.println("Reading CSV Error!");
            e.printStackTrace();
        } finally {
            try {
                assert fileReader != null;
                fileReader.close();
                Objects.requireNonNull(csvParser).close();
            } catch (IOException e) {
                System.out.println("Closing fileReader/csvParser Error!");
                e.printStackTrace();
            }
        }

        return projectDTOList;
    }

    public static boolean isCSVFile(MultipartFile file) {
        String extension = Objects.requireNonNull(file.getOriginalFilename()).split("\\.")[1];

        return extension.equals(csvExtension);
    }
}
