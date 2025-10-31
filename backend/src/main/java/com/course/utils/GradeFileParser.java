package com.course.utils;

import com.course.dto.GradeImportDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;

import java.io.BufferedReader;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * 成绩文件解析工具类
 */
@Slf4j
public class GradeFileParser {

    /**
     * 解析Excel文件（支持.xls和.xlsx格式）
     */
    public static List<GradeImportDTO> parseExcel(InputStream inputStream) throws Exception {
        List<GradeImportDTO> result = new ArrayList<>();

        Workbook workbook = null;
        BufferedInputStream bis = null;
        try {
            // 使用BufferedInputStream包装，确保WorkbookFactory能正确读取文件
            bis = new BufferedInputStream(inputStream);

            // 使用WorkbookFactory自动识别并创建Workbook（支持.xls和.xlsx格式）
            log.info("开始创建Workbook...");
            workbook = WorkbookFactory.create(bis);
            log.info("Workbook创建成功");
        } catch (Exception e) {
            log.error("创建Workbook失败: {}", e.getMessage(), e);
            throw new Exception("无法解析Excel文件，请确保文件格式正确: " + e.getMessage());
        }

        if (workbook == null) {
            throw new Exception("无法创建Workbook，文件可能为空或格式不正确");
        }

        try {
            Sheet sheet = workbook.getSheetAt(0);

            // 跳过表头，从第二行开始
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }

                GradeImportDTO dto = new GradeImportDTO();

                // 读取学号
                Cell cell0 = row.getCell(0);
                if (cell0 != null) {
                    dto.setStudentNo(getCellValue(cell0));
                }

                // 读取学生姓名
                Cell cell1 = row.getCell(1);
                if (cell1 != null) {
                    dto.setStudentName(getCellValue(cell1));
                }

                // 读取课程名称
                Cell cell2 = row.getCell(2);
                if (cell2 != null) {
                    dto.setCourseName(getCellValue(cell2));
                }

                // 读取分数
                Cell cell3 = row.getCell(3);
                if (cell3 != null) {
                    try {
                        String scoreStr = getCellValue(cell3);
                        dto.setScore(new BigDecimal(scoreStr));
                    } catch (Exception e) {
                        log.warn("解析分数失败: {}", getCellValue(cell3));
                    }
                }

                // 读取学期
                Cell cell4 = row.getCell(4);
                if (cell4 != null) {
                    dto.setSemester(getCellValue(cell4));
                }

                // 跳过空行
                if (dto.getStudentNo() == null || dto.getStudentNo().trim().isEmpty()) {
                    continue;
                }

                result.add(dto);
            }
        } finally {
            // 确保workbook被关闭
            if (workbook != null) {
                workbook.close();
            }
        }

        return result;
    }

    /**
     * 解析CSV文件
     */
    public static List<GradeImportDTO> parseCSV(InputStream inputStream) throws Exception {
        List<GradeImportDTO> result = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;

                // 跳过表头
                if (lineNumber == 1) {
                    continue;
                }

                if (line.trim().isEmpty()) {
                    continue;
                }

                // 解析CSV行（简单实现，假设没有引号和逗号的情况）
                String[] fields = line.split(",");

                if (fields.length < 5) {
                    continue;
                }

                GradeImportDTO dto = new GradeImportDTO();
                dto.setStudentNo(fields[0].trim());
                dto.setStudentName(fields[1].trim());
                dto.setCourseName(fields[2].trim());

                try {
                    dto.setScore(new BigDecimal(fields[3].trim()));
                } catch (Exception e) {
                    log.warn("解析分数失败: {}", fields[3]);
                }

                dto.setSemester(fields[4].trim());

                // 跳过空行
                if (dto.getStudentNo().isEmpty()) {
                    continue;
                }

                result.add(dto);
            }
        }

        return result;
    }

    /**
     * 获取单元格值
     */
    private static String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf(cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }
}

