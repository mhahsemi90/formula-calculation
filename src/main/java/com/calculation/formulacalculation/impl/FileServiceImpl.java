package com.calculation.formulacalculation.impl;

import com.calculation.formulacalculation.interfaces.FileService;
import com.calculation.formulacalculation.interfaces.MapService;
import io.micrometer.common.util.StringUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.apache.poi.ss.usermodel.CellType.*;

public class FileServiceImpl implements FileService {
    private final MapService mapService;
    private XSSFWorkbook xssfWorkbook;
    private File file;
    private Map<String, Map<String, Object>> mainDataMap;
    private Map<String, Object> fixedDataMap;
    private List<List<Object>> dataList;
    private List<String> headerRowList;
    private List<List<Object>> beforeHeaderRowList;
    private List<List<Object>> afterHeaderRowList;
    private List<List<Object>> afterDataRowList;
    private Integer headerRowIndex;
    private Integer dataRowIndex;
    private Integer afterDataRowIndex;
    private Integer sheetIndex;

    public FileServiceImpl(MapService mapService) {
        this.mapService = mapService;
        xssfWorkbook = new XSSFWorkbook();
        sheetIndex = -1;
        file = null;
        mainDataMap = null;
        fixedDataMap = null;
        headerRowIndex = null;
        dataRowIndex = null;
        afterDataRowIndex = null;
    }


    @Override
    public FileService setFile(File file, Integer headerRowIndex, Integer dataRowIndex, Integer afterDataRowIndex) {
        this.headerRowIndex = headerRowIndex;
        this.dataRowIndex = dataRowIndex;
        this.afterDataRowIndex = afterDataRowIndex;
        validateIndexes();
        this.file = file;
        getValues(null);
        return this;
    }

    @Override
    public FileService setFile(File file, Integer headerRowIndex, Integer dataRowIndex) {
        return setFile(file, headerRowIndex, dataRowIndex, null);
    }

    @Override
    public FileService setFile(File file, Integer headerRowIndex) {
        return setFile(file, headerRowIndex, null, null);
    }

    @Override
    public FileService setFile(File file) {
        return setFile(file, null, null, null);
    }

    @Override
    public FileService setFileAndSheetName(File file, String sheetName, Integer headerRowIndex, Integer dataRowIndex, Integer afterDataRowIndex) {
        this.headerRowIndex = headerRowIndex;
        this.dataRowIndex = dataRowIndex;
        this.afterDataRowIndex = afterDataRowIndex;
        validateIndexes();
        this.file = file;
        getValues(sheetName);
        return this;
    }

    @Override
    public FileService setFileAndSheetName(File file, String sheetName, Integer headerRowIndex, Integer dataRowIndex) {
        return setFileAndSheetName(file, sheetName, headerRowIndex, dataRowIndex, null);
    }

    @Override
    public FileService setFileAndSheetName(File file, String sheetName, Integer headerRowIndex) {
        return setFileAndSheetName(file, sheetName, headerRowIndex, null, null);
    }

    @Override
    public FileService setFileAndSheetName(File file, String sheetName) {
        return setFileAndSheetName(file, sheetName, null, null, null);
    }

    @Override
    public FileService setMainDataMap(Map<String, Map<String, Object>> mainDataMap) {
        this.mainDataMap = new LinkedHashMap<>(mainDataMap);
        return this;
    }

    @Override
    public FileService setFixedDataMap(Map<String, Object> fixedDataMap) {
        this.fixedDataMap = new LinkedHashMap<>(fixedDataMap);
        return this;
    }

    @Override
    public Collection<List<Object>> getDataList() {
        return dataList;
    }

    @Override
    public FileService setDataList(List<List<Object>> dataList) {
        this.dataList = dataList;
        return this;
    }

    @Override
    public FileService setDataList(Collection<Map<String, Object>> dataList, List<String> headerRowList) {
        this.dataList = mapService.convertDataMapToDataList(new ArrayList<>(dataList), headerRowList);
        this.headerRowList = headerRowList;
        validateIndexes();
        return this;
    }

    @Override
    public FileService addSheet(String sheetName) {
        addingSheet(sheetName);
        return this;
    }

    @Override
    public List<String> getHeaderRowList() {
        return headerRowList;
    }

    public FileService setHeaderRowList(List<String> headerRowList) {
        this.headerRowList = headerRowList;
        return this;
    }

    @Override
    public List<List<Object>> getBeforeHeaderRowList() {
        return beforeHeaderRowList;
    }

    @Override
    public FileService setBeforeHeaderRowList(List<List<Object>> beforeHeaderRowList) {
        this.beforeHeaderRowList = beforeHeaderRowList;
        return this;
    }

    @Override
    public List<List<Object>> getAfterHeaderRowList() {
        return afterHeaderRowList;
    }

    @Override
    public FileService setAfterHeaderRowList(List<List<Object>> afterHeaderRowList) {
        this.afterHeaderRowList = afterHeaderRowList;
        return this;
    }

    @Override
    public List<List<Object>> getAfterDataRowList() {
        return afterDataRowList;
    }

    @Override
    public FileService setAfterDataRowList(List<List<Object>> afterDataRowList) {
        this.afterDataRowList = afterDataRowList;
        return this;
    }


    private void addingSheet(String sheetName) {
        XSSFSheet xssfSheet;
        if (StringUtils.isBlank(sheetName))
            xssfSheet = xssfWorkbook.createSheet();
        else
            xssfSheet = xssfWorkbook.createSheet(sheetName);
        XSSFRow xssfRow;
        int row = 0;
        if (CollectionUtils.isNotEmpty(beforeHeaderRowList)) {
            for (List<Object> objects : beforeHeaderRowList) {
                xssfRow = xssfSheet.createRow(row++);
                int cellId = 0;
                for (Object o : objects) {
                    Cell cell = xssfRow.createCell(cellId++);
                    setCellValue(cell, o != null ? o : "");
                }
            }
        }
        if (CollectionUtils.isNotEmpty(headerRowList)) {
            xssfRow = xssfSheet.createRow(row++);
            int cellId = 0;
            for (String s : headerRowList) {
                Cell cell = xssfRow.createCell(cellId++);
                XSSFCellStyle cellStyle = xssfWorkbook.createCellStyle();
                cellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
                cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                cell.setCellValue(s != null ? s : "");
                cell.setCellStyle(cellStyle);
            }
        }
        if (CollectionUtils.isNotEmpty(afterHeaderRowList)) {
            for (List<Object> objects : afterHeaderRowList) {
                xssfRow = xssfSheet.createRow(row++);
                int cellId = 0;
                for (Object o : objects) {
                    Cell cell = xssfRow.createCell(cellId++);
                    setCellValue(cell, o != null ? o : "");
                }
            }

        }
        if (CollectionUtils.isNotEmpty(dataList)) {
            for (List<Object> objects : dataList) {
                xssfRow = xssfSheet.createRow(row++);
                int cellId = 0;
                for (Object o : objects) {
                    Cell cell = xssfRow.createCell(cellId++);
                    setCellValue(cell, o != null ? o : "");
                }
            }

        }
        if (CollectionUtils.isNotEmpty(afterDataRowList)) {
            for (List<Object> objects : afterDataRowList) {
                xssfRow = xssfSheet.createRow(row++);
                int cellId = 0;
                for (Object o : objects) {
                    Cell cell = xssfRow.createCell(cellId++);
                    setCellValue(cell, o != null ? o : "");
                }
            }
        }
        sheetIndex++;
    }

    private void writeToExcelFile(String filePath) throws IOException {
        File file = new File(filePath);
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
            xssfWorkbook.write(fileOutputStream);
            this.file = file;
        } finally {
            try {
                xssfWorkbook.close();
                if (fileOutputStream != null)
                    fileOutputStream.close();
            } catch (IOException ignored) {

            }
        }
        xssfWorkbook = new XSSFWorkbook();
        sheetIndex = -1;
    }

    private void writeToTextFile(String filePath, String separator, Boolean dataOnly) throws IOException {
        StringBuilder content = new StringBuilder();
        separator = separator != null ? separator : "";
        if (dataOnly) {
            if (CollectionUtils.isNotEmpty(dataList)) {
                for (List<Object> objects : dataList) {
                    for (Object o : objects) {
                        content.append(o).append(separator);
                    }
                    if (StringUtils.isNotBlank(separator)) {
                        content = new StringBuilder(content.substring(0, content.length() - separator.length()));
                    }
                    content.append("\r\n");
                }
            }
        } else {
            if (CollectionUtils.isNotEmpty(beforeHeaderRowList)) {
                for (List<Object> objects : beforeHeaderRowList) {
                    for (Object o : objects) {
                        content.append(o).append(separator);
                    }
                    if (StringUtils.isNotBlank(separator)) {
                        content = new StringBuilder(content.substring(0, content.length() - separator.length()));
                    }
                    content.append("\r\n");
                }
            }
            if (CollectionUtils.isNotEmpty(headerRowList)) {
                for (String s : headerRowList) {
                    content.append(s).append(separator);
                }
                if (StringUtils.isNotBlank(separator)) {
                    content = new StringBuilder(content.substring(0, content.length() - separator.length()));
                }
                content.append("\r\n");
            }
            if (CollectionUtils.isNotEmpty(afterHeaderRowList)) {
                for (List<Object> objects : afterHeaderRowList) {
                    for (Object o : objects) {
                        content.append(o).append(separator);
                    }
                    if (StringUtils.isNotBlank(separator)) {
                        content = new StringBuilder(content.substring(0, content.length() - separator.length()));
                    }
                    content.append("\r\n");
                }
            }
            if (CollectionUtils.isNotEmpty(dataList)) {
                for (List<Object> objects : dataList) {
                    for (Object o : objects) {
                        content.append(o).append(separator);
                    }
                    if (StringUtils.isNotBlank(separator)) {
                        content = new StringBuilder(content.substring(0, content.length() - separator.length()));
                    }
                    content.append("\r\n");
                }
            }
            if (CollectionUtils.isNotEmpty(afterDataRowList)) {
                for (List<Object> objects : afterDataRowList) {
                    for (Object o : objects) {
                        content.append(o).append(separator);
                    }
                    if (StringUtils.isNotBlank(separator)) {
                        content = new StringBuilder(content.substring(0, content.length() - separator.length()));
                    }
                    content.append("\r\n");
                }
            }
        }
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(Paths.get(filePath)), StandardCharsets.UTF_8))) {
            writer.write(content.toString());
            file = new File(filePath);
        }
    }

    @Override
    public File writeAndGetExcelFile(String fileName) {
        String dirPath = System.getProperty("user.dir") + File.separator + "fileContext" + System.currentTimeMillis() + File.separator;
        String filePath = dirPath + fileName + ".xlsx";
        Path dir = Paths.get(dirPath);
        try {
            Files.createDirectories(dir);
            if (sheetIndex < 0)
                addingSheet(null);
            writeToExcelFile(filePath);
        } catch (IOException e) {
            try {
                Files.deleteIfExists(Paths.get(filePath));
                Files.deleteIfExists(dir);
            } catch (IOException ignored) {

            }
        }
        return file;
    }

    @Override
    public FileService addExistingDataToFile(File excelFile,
                                             Integer headerRowIndex,
                                             Integer dataRowIndex,
                                             Integer afterDataRowIndex,
                                             String referenceHeader) {
        List<String> oldHeaderRowList = new ArrayList<>(headerRowList);
        List<List<Object>> oldDataList = new ArrayList<>(dataList);
        setFile(excelFile, headerRowIndex, dataRowIndex, afterDataRowIndex);
        for (int i = 0; i < oldHeaderRowList.size(); i++) {
            if (!oldHeaderRowList.get(i).equalsIgnoreCase(headerRowList.get(i))) {
                throw new RuntimeException("PCN_HEADER_LIST_NOT_SAME");
            }
        }
        oldDataList.addAll(dataList);
        dataList = oldDataList;
        return this;
    }

    @Override
    public FileService mergeExistingDataToFile(File excelFile,
                                               Integer headerRowIndex,
                                               Integer dataRowIndex,
                                               Integer afterDataRowIndex,
                                               String referenceHeader) {
        dataList.add(0, new ArrayList<>(headerRowList));
        List<List<Object>> oldObjects = new ArrayList<>(dataList);
        mapService.setObjects(oldObjects);
        Map<Object, Map<String, Object>> oldObjectMap = mapService.convertAllListToMap(referenceHeader);
        List<String> oldHeaderRowList = new ArrayList<>(headerRowList);
        setFile(excelFile, headerRowIndex, dataRowIndex, afterDataRowIndex);
        for (int i = 0; i < oldHeaderRowList.size(); i++) {
            if (!oldHeaderRowList.get(i).equalsIgnoreCase(headerRowList.get(i))) {
                throw new RuntimeException("PCN_HEADER_LIST_NOT_SAME");
            }
        }
        List<List<Object>> newObjects = new ArrayList<>(dataList);
        newObjects.add(0, new ArrayList<>(headerRowList));
        mapService.setObjects(newObjects);
        Map<Object, Map<String, Object>> newObjectMap = mapService.convertAllListToMap(referenceHeader);
        newObjectMap.putAll(oldObjectMap);
        dataList = mapService.convertDataMapToDataList(new ArrayList<>(newObjectMap.values()), headerRowList);
        return this;
    }

    @Override
    public FileService addRowNum(String rowHeaderName, String locale, Integer startIndex) {
        if (CollectionUtils.isNotEmpty(headerRowList) && CollectionUtils.isNotEmpty(dataList)) {
            headerRowList.add(0, rowHeaderName);
            if (CollectionUtils.isNotEmpty(getAfterHeaderRowList()))
                setAfterHeaderRowList(
                        getAfterHeaderRowList()
                                .stream()
                                .peek(o -> o.add(0, "hh"))
                                .collect(Collectors.toList())
                );
            if (CollectionUtils.isNotEmpty(getAfterDataRowList()))
                setAfterDataRowList(
                        getAfterDataRowList()
                                .stream()
                                .peek(o -> o.add(0, ""))
                                .collect(Collectors.toList())
                );
            if (CollectionUtils.isNotEmpty(getBeforeHeaderRowList()))
                setBeforeHeaderRowList(
                        getBeforeHeaderRowList()
                                .stream()
                                .peek(o -> o.add(0, ""))
                                .collect(Collectors.toList())
                );
            AtomicInteger counter = new AtomicInteger(startIndex);
            dataList.forEach(o -> o.add(0, counter.getAndIncrement()));
        }
        return this;
    }

    @Override
    public FileService addRowNum(String rowHeaderName, String locale) {
        return addRowNum(rowHeaderName, locale, 1);
    }

    @Override
    public FileService addRowNum(String rowHeaderName) {
        addRowNum(rowHeaderName, "Fa", 1);
        return this;
    }

    @Override
    public Map<Object, Map<String, Object>> getAllMapValue(String referenceHeader) {
        List<List<Object>> objects = new ArrayList<>(dataList);
        objects.add(0, new ArrayList<>(headerRowList));
        this.mapService.setObjects(objects);
        return mapService.convertAllListToMap(referenceHeader);
    }

    @Override
    public Map<String, Map<String, Object>> getAllMapValueStringReference(String referenceHeader) {
        Map<Object, Map<String, Object>> objectMap = getAllMapValue(referenceHeader);
        Map<String, Map<String, Object>> stringMap = new LinkedHashMap<>();
        objectMap.forEach((o, objectMap1) -> stringMap.put(toString(o), objectMap1));
        return stringMap;
    }

    @Override
    public Map<Object, Map<String, Object>> getPartialMapValue(String referenceHeader, String... otherHeader) {
        List<List<Object>> objects = new ArrayList<>(dataList);
        objects.add(0, new ArrayList<>(headerRowList));
        this.mapService.setObjects(objects);
        return mapService.convertPartialListToMap(referenceHeader, otherHeader);
    }

    @Override
    public Map<String, Map<String, Object>> getPartialMapValueStringReference(String referenceHeader, String...
            otherHeader) {
        Map<Object, Map<String, Object>> objectMap = getPartialMapValue(referenceHeader, otherHeader);
        Map<String, Map<String, Object>> stringMap = new LinkedHashMap<>();
        objectMap.forEach((o, objectMap1) -> stringMap.put(toString(o), objectMap1));
        return stringMap;
    }

    @Override
    public String toString(Object o) {
        if (o instanceof Double) {
            return BigDecimal.valueOf((Double) o).setScale(0, RoundingMode.FLOOR).toString();
        }
        if (o instanceof Float) {
            return BigDecimal.valueOf((Float) o).setScale(0, RoundingMode.FLOOR).toString();
        }
        return o != null ? o.toString() : "";
    }

    private void getValues(String sheetName) {
        if (file == null) {
            throw new RuntimeException("PCN_HEADER_LIST_NOT_SAME");
        }
        if (!file.exists()) {
            throw new RuntimeException("PCN_HEADER_LIST_NOT_SAME");
        }
        Workbook workbook = null;
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            String fileExtension = file.getName().substring(file.getName().lastIndexOf("."));
            if (fileExtension.equalsIgnoreCase(".xls")) {
                workbook = new HSSFWorkbook(fileInputStream);
            } else if (fileExtension.equalsIgnoreCase(".xlsx")) {
                workbook = new XSSFWorkbook(fileInputStream);
            } else {
                throw new RuntimeException("PCN_HEADER_LIST_NOT_SAME");
            }
            Sheet sheet;
            if (StringUtils.isBlank(sheetName))
                sheet = workbook.getSheetAt(0);
            else
                sheet = workbook.getSheet(sheetName);
            if (sheet.getLastRowNum() + 1 < headerRowIndex
                //|| sheet.getLastRowNum() + 1 < dataRowIndex
                //|| sheet.getLastRowNum() + 1 < afterDataRowIndex
            ) {
                throw new RuntimeException("PCN_HEADER_LIST_NOT_SAME");
            }
            dataList = new ArrayList<>();
            beforeHeaderRowList = new ArrayList<>();
            afterHeaderRowList = new ArrayList<>();
            afterDataRowList = new ArrayList<>();
            for (int i = 0; i < sheet.getLastRowNum() + 1; i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    row = sheet.createRow(0);
                }
                if (headerRowIndex > i + 1) {
                    List<Object> rowObjectList = new ArrayList<>();
                    for (int j = 0; j < row.getLastCellNum(); j++) {
                        rowObjectList.add(getCellValue(row.getCell(j)));
                    }
                    beforeHeaderRowList.add(rowObjectList);
                }
                if (headerRowIndex.equals(i + 1)) {
                    List<String> rowObjectList = new ArrayList<>();
                    for (int j = 0; j < row.getLastCellNum(); j++) {
                        rowObjectList.add(toString(getCellValue(row.getCell(j))));
                    }
                    headerRowList = rowObjectList;
                }
                if (i > headerRowIndex - 1 && dataRowIndex > i + 1) {
                    List<Object> rowObjectList = new ArrayList<>();
                    for (int j = 0; j < row.getLastCellNum(); j++) {
                        rowObjectList.add(getCellValue(row.getCell(j)));
                    }
                    afterHeaderRowList.add(rowObjectList);
                }
                List<Object> rowObjectList = new ArrayList<>();
                if (afterDataRowIndex.equals(-1)) {
                    if (i >= dataRowIndex - 1) {
                        for (int j = 0; j < sheet.getRow(headerRowIndex - 1).getLastCellNum(); j++) {
                            rowObjectList.add(getCellValue(row.getCell(j)));
                        }
                        dataList.add(rowObjectList);
                    }
                } else {
                    if (i >= afterDataRowIndex - 1) {
                        for (int j = 0; j < row.getLastCellNum(); j++) {
                            rowObjectList.add(getCellValue(row.getCell(j)));
                        }
                        afterDataRowList.add(rowObjectList);
                    } else if (i >= dataRowIndex - 1) {
                        for (int j = 0; j < sheet.getRow(headerRowIndex - 1).getLastCellNum(); j++) {
                            rowObjectList.add(getCellValue(row.getCell(j)));
                        }
                        dataList.add(rowObjectList);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("PCN_HEADER_LIST_NOT_SAME");
        } finally {
            try {
                if (workbook != null)
                    workbook.close();
                if (fileInputStream != null)
                    fileInputStream.close();
            } catch (IOException ignored) {

            }
        }
    }

    private Object getCellValue(Cell cell) {
        Object object;
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    object = cell.getDateCellValue();
                } else {
                    DataFormatter dataFormatter = new DataFormatter();
                    object = dataFormatter.formatCellValue(cell);
                }
                break;
            case STRING:
                object = cell.getRichStringCellValue().getString();
                break;
            case BOOLEAN:
                object = cell.getBooleanCellValue();
                break;
            case FORMULA:
                object = cell.getCellFormula();
                break;
            default:
                object = "";
        }
        return object;
    }

    private void setCellValue(Cell cell, Object value) {
        cell.setCellValue(value.toString());
        if (value instanceof Number) {
            cell.setCellValue(((Number) value).doubleValue());
        }
        if (value instanceof Date) {
            cell.setCellValue((Date) value);
        }
        if (value instanceof Calendar) {
            cell.setCellValue(((Calendar) value).getTime());
        }
        if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        }
    }

    private void validateIndexes() {
        if (headerRowIndex == null) {
            headerRowIndex = 1;
        }
        if (headerRowIndex < 1) {
            throw new RuntimeException("PCN_HEADER_LIST_NOT_SAME");
        }
        if (dataRowIndex == null) {
            dataRowIndex = headerRowIndex + 1;
        }
        if (dataRowIndex < 2 || dataRowIndex <= headerRowIndex) {
            throw new RuntimeException("PCN_HEADER_LIST_NOT_SAME");
        }
        if (afterDataRowIndex == null) {
            afterDataRowIndex = -1;
        }
        if (afterDataRowIndex != -1) {
            if (afterDataRowIndex < 2 || afterDataRowIndex <= dataRowIndex) {
                throw new RuntimeException("PCN_HEADER_LIST_NOT_SAME");
            }
        }
    }
}
