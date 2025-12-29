package com.zhu.utils.excel;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Excel 工具类
 */
public class ExcelUtils {

    /**
     * 解析 Excel 文件
     * @param file Excel 文件
     * @return 数据列表，每行是一个 Map，key 为列名，value 为单元格值
     */
    public static List<Map<String, Object>> parseExcel(MultipartFile file) throws IOException {
        List<Map<String, Object>> dataList = new ArrayList<>();
        
        InputStream inputStream = file.getInputStream();
        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0);
        
        // 获取表头
        Row headerRow = sheet.getRow(0);
        if (headerRow == null) {
            workbook.close();
            throw new RuntimeException("Excel 文件表头为空");
        }
        
        List<String> headers = new ArrayList<>();
        for (Cell cell : headerRow) {
            headers.add(getCellValue(cell).toString());
        }
        
        // 解析数据行
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            
            Map<String, Object> rowData = new LinkedHashMap<>();
            for (int j = 0; j < headers.size(); j++) {
                Cell cell = row.getCell(j);
                Object value = cell == null ? null : getCellValue(cell);
                rowData.put(headers.get(j), value);
            }
            
            // 跳过空行
            if (!isEmptyRow(rowData)) {
                dataList.add(rowData);
            }
        }
        
        workbook.close();
        return dataList;
    }

    /**
     * 获取单元格值
     */
    private static Object getCellValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue();
                } else {
                    // 使用 DecimalFormat 避免科学计数法
                    DecimalFormat df = new DecimalFormat("0");
                    return df.format(cell.getNumericCellValue());
                }
            case BOOLEAN:
                return cell.getBooleanCellValue();
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
                return null;
            default:
                return null;
        }
    }

    /**
     * 判断是否为空行
     */
    private static boolean isEmptyRow(Map<String, Object> rowData) {
        for (Object value : rowData.values()) {
            if (value != null && !value.toString().trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * 数据清洗：去重
     */
    public static <T> List<T> removeDuplicates(List<T> list) {
        List<T> result = new ArrayList<>();
        for (T item : list) {
            if (!result.contains(item)) {
                result.add(item);
            }
        }
        return result;
    }

    /**
     * 数据清洗：填补缺失值（数值型使用均值）
     */
    public static void fillMissingValues(List<Map<String, Object>> dataList, String columnName) {
        // 计算均值
        BigDecimal sum = BigDecimal.ZERO;
        int count = 0;
        for (Map<String, Object> row : dataList) {
            Object value = row.get(columnName);
            if (value != null && !value.toString().trim().isEmpty()) {
                try {
                    sum = sum.add(new BigDecimal(value.toString()));
                    count++;
                } catch (NumberFormatException e) {
                    // 忽略非数值
                }
            }
        }
        
        if (count == 0) {
            return;
        }
        
        BigDecimal avg = sum.divide(new BigDecimal(count), 2, BigDecimal.ROUND_HALF_UP);
        
        // 填补缺失值
        for (Map<String, Object> row : dataList) {
            Object value = row.get(columnName);
            if (value == null || value.toString().trim().isEmpty()) {
                row.put(columnName, avg);
            }
        }
    }

    /**
     * 数据清洗：填补缺失值（文本型使用默认值）
     */
    public static void fillMissingValues(List<Map<String, Object>> dataList, String columnName, String defaultValue) {
        for (Map<String, Object> row : dataList) {
            Object value = row.get(columnName);
            if (value == null || value.toString().trim().isEmpty()) {
                row.put(columnName, defaultValue);
            }
        }
    }
}



