package com.thredim.regserver.utils;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class ExcelUtils<BaseEntity> {
    private String filePath;
    private List<BaseEntity> dataList;

    public ExcelUtils(String filePath, List<BaseEntity> dataList){
        this.filePath = filePath;
        this.dataList = dataList;
    }

    public void output() throws IOException {
        SXSSFWorkbook wb = new SXSSFWorkbook(1024);
        Sheet sh = wb.createSheet();
        Row row = sh.createRow(0);
        setHeader(row);
        for(int rownum = 0; rownum < dataList.size(); rownum++){
            row = sh.createRow(rownum + 1);
            setCell(row, dataList.get(rownum));
        }

        FileOutputStream out = new FileOutputStream(filePath);
        wb.write(out);
        out.close();

        wb.dispose();
    }

    public void setHeader(Row row){
    }

    public void setCell(Row row, BaseEntity entity){
    }
}
