package com.ces.intern.hr.resourcing.demo.importCSV;

import com.ces.intern.hr.resourcing.demo.dto.ProjectDTO;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class OpenCSV {
    public static List<ProjectDTO> parseCsvFile(InputStream is){
        String[] CSV_HEADER ={"name","clientName","color","textColor","colorPattern","isActivate"};
        Reader fileReader = null;
        CsvToBean<ProjectDTO> csvToBean = null;
        List<ProjectDTO> projectDTOList = new ArrayList<>();
        try {
            fileReader = new InputStreamReader(is);
            ColumnPositionMappingStrategy<ProjectDTO> mappingStrategy = new ColumnPositionMappingStrategy<ProjectDTO>();

            mappingStrategy.setType(ProjectDTO.class);
            mappingStrategy.setColumnMapping(CSV_HEADER);

            csvToBean = new CsvToBeanBuilder<ProjectDTO>(fileReader).withMappingStrategy(mappingStrategy).withSkipLines(1)
                    .withIgnoreLeadingWhiteSpace(true).build();

            projectDTOList = csvToBean.parse();
            return projectDTOList;

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                fileReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return projectDTOList;
    }
}
