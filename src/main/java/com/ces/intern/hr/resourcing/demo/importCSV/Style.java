package com.ces.intern.hr.resourcing.demo.importCSV;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Style {
    private Row row;
    private CellStyle style;
}
