package com.calculation.formulacalculation.interfaces;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface FileService {

    FileService setFile(File file, Integer headerRowIndex, Integer dataRowIndex, Integer afterDataRowIndex);

    FileService setFile(File file, Integer headerRowIndex, Integer dataRowIndex);

    FileService setFile(File file, Integer headerRowIndex);

    FileService setFile(File file);

    FileService setFileAndSheetName(File file, String sheetName, Integer headerRowIndex, Integer dataRowIndex, Integer afterDataRowIndex);

    FileService setFileAndSheetName(File file, String sheetName, Integer headerRowIndex, Integer dataRowIndex);

    FileService setFileAndSheetName(File file, String sheetName, Integer headerRowIndex);

    FileService setFileAndSheetName(File file, String sheetName);

    FileService setMainDataMap(Map<String, Map<String, Object>> mainDataMap);

    FileService setFixedDataMap(Map<String, Object> fixedDataMap);

    Collection<List<Object>> getDataList();

    FileService setDataList(List<List<Object>> dataList);

    FileService setDataList(Collection<Map<String, Object>> dataList, List<String> headerRowList);

    FileService addSheet(String sheetName);

    List<String> getHeaderRowList();

    FileService setHeaderRowList(List<String> headerRowList);

    List<List<Object>> getBeforeHeaderRowList();

    FileService setBeforeHeaderRowList(List<List<Object>> beforeHeaderRowList);

    List<List<Object>> getAfterHeaderRowList();

    FileService setAfterHeaderRowList(List<List<Object>> afterHeaderRowList);

    List<List<Object>> getAfterDataRowList();

    FileService setAfterDataRowList(List<List<Object>> afterDataRowList);


    FileService addExistingDataToFile(File excelFile, Integer headerRowIndex, Integer dataRowIndex, Integer afterDataRowIndex, String referenceHeader);

    FileService mergeExistingDataToFile(File excelFile, Integer headerRowIndex, Integer dataRowIndex, Integer afterDataRowIndex, String referenceHeader);

    FileService addRowNum(String rowHeaderName, String locale, Integer startIndex);

    FileService addRowNum(String rowHeaderName, String locale);

    FileService addRowNum(String rowHeaderName);

    File writeAndGetExcelFile(String fileName);

    Map<Object, Map<String, Object>> getAllMapValue(String referenceHeader);

    Map<String, Map<String, Object>> getAllMapValueStringReference(String referenceHeader);

    Map<Object, Map<String, Object>> getPartialMapValue(String referenceHeader, String... otherHeader);

    Map<String, Map<String, Object>> getPartialMapValueStringReference(String referenceHeader, String... otherHeader);

    String toString(Object o);
}
