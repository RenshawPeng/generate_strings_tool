package generator_language_config;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ExcelToStringsUtil {
    private static final String KEY_FLAG = "key";
    public static final String ANNOTATION_FLAG = "##ANN##";
    public static final String DEFAULT_FLAG = "default";

    public void readExcel(File inputFile, File outputFile) throws IOException {
        if (!inputFile.exists() || !outputFile.exists()) {
            throw new IOException("文件不存在");
        }
        String fileName = inputFile.getName();
        String extension = fileName.lastIndexOf(".") == -1 ? "" : fileName.substring(fileName.lastIndexOf(".") + 1);
        if ("xls".equals(extension)) {
            readXLSExcel(inputFile, outputFile);
        } else if ("xlsx".equals(extension)) {
            readXLSXExcel(inputFile, outputFile);
        } else {
            throw new IOException("不支持的文件类型");
        }
    }


    private void readXLSExcel(File file, File outputFile) {
        try {
            HSSFWorkbook hwb = new HSSFWorkbook(new FileInputStream(file));
            HSSFSheet sheet = hwb.getSheetAt(0);
            HSSFRow row = sheet.getRow(sheet.getFirstRowNum());
            if (row == null) {
                throw new IOException("row 不存在");
            }
            HSSFCell cell = row.getCell(row.getFirstCellNum());
            String keyCell = cell.getStringCellValue();
            //第一行 第一列必须为key字段
            if (!KEY_FLAG.equalsIgnoreCase(keyCell)) {
                throw new IOException("key 不存在");
            }
            int startIndex = row.getFirstCellNum() + 1;
            int endIndex = row.getLastCellNum();
            List<String> fileList = new ArrayList<>();
            for (int i = startIndex; i < endIndex; i++) {
                fileList.add(row.getCell(i).getStringCellValue());
            }
            //从第一列开始读
            StringsUtil util = new StringsUtil();
            for (int i = startIndex; i < endIndex; i++) {
                Map<String, Object> map = new LinkedHashMap<>();
                for (int j = sheet.getFirstRowNum(); j < sheet
                        .getPhysicalNumberOfRows(); j++) {
                    if (j == 0) {
                        //跳过第一行
                        continue;
                    }
                    row = sheet.getRow(j);
                    if (row == null) {
                        break;
                    }
                    String key = row.getCell(row.getFirstCellNum()).getStringCellValue();
                    if (containAnnotationKey(key)) {
                        map.put(ANNOTATION_FLAG + j, key.replace("<!--", "").replace("-->", ""));
                        continue;
                    }
                    HSSFCell currentCell = sheet.getRow(j).getCell(i);
                    Object value = getCellValue(currentCell);
                    map.put(key, value);
                }
                File xmlFile = new File(outputFile + File.separator + fileList.get(i - 1) + ".strings");
                makeDirectory(xmlFile);
                util.writStrings(xmlFile, map);
            }
        } catch (OfficeXmlFileException officeXmlFileException) {
            readXLSXExcel(file, outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readXLSXExcel(File file, File outputFile) {
        try {
            XSSFWorkbook xwb = new XSSFWorkbook(new FileInputStream(file));
            // 读取第一章表格内容
            XSSFSheet sheet = xwb.getSheetAt(0);
            //获取第一行 第一列
            XSSFRow row = sheet.getRow(sheet.getFirstRowNum());
            if (row == null) {
                throw new IOException("row 不存在");
            }
            String keyCell = row.getCell(row.getFirstCellNum()).getStringCellValue();
            //第一行 第一列必须为key字段
            if (!KEY_FLAG.equalsIgnoreCase(keyCell)) {
                throw new IOException("key 不存在");
            }
            int startIndex = row.getFirstCellNum() + 1;
            int endIndex = row.getLastCellNum();
            //获取Top文件目录
            List<String> fileList = new ArrayList<>();
            for (int i = startIndex; i < endIndex; i++) {
                fileList.add(row.getCell(i).getStringCellValue());
            }
            //从第一列开始读
            StringsUtil util = new StringsUtil();
            for (int i = startIndex; i < endIndex; i++) {
                Map<String, Object> map = new LinkedHashMap<>();
                for (int j = sheet.getFirstRowNum(); j < sheet
                        .getPhysicalNumberOfRows(); j++) {
                    if (j == 0) {
                        //跳过第一行
                        continue;
                    }
                    row = sheet.getRow(j);
                    if (row == null) {
                        break;
                    }

                    String key = row.getCell(row.getFirstCellNum()).getStringCellValue();
                    if (containAnnotationKey(key)) {
                        map.put(ANNOTATION_FLAG + j, key.replace("<!--", "").replace("-->", ""));
                        continue;
                    }
                    XSSFCell currentCell = sheet.getRow(j).getCell(i);
                    Object value = getCellValue(currentCell);
                    map.put(key, value);
                }
                File xmlFile = new File(outputFile + File.separator + fileList.get(i - 1) + ".strings");
                makeDirectory(xmlFile);
                System.out.println(map.toString());
                util.writStrings(xmlFile, map);
            }
        } catch (OfficeXmlFileException officeXmlFileException) {
            readXLSExcel(file, outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void makeDirectory(File file) {
        if (file.exists()) {
            // 文件已经存在，输出文件的相关信息
            System.out.println(file.getAbsolutePath());
            System.out.println(file.getName());
            System.out.println(file.length());
        } else {
            boolean mkdirs = false;
            try {
                mkdirs = file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("创建文件夹 mkdirs = " + mkdirs);
        }
    }

    private boolean containAnnotationKey(String key) {
        return key.contains("<!--") && key.contains("-->");
    }

    private Object getCellValue(HSSFCell cell) {
        Object value;
        if (cell == null) {
            return value = "";
        }
        switch (cell.getCellType()) {
            case HSSFCell.CELL_TYPE_STRING:
                value = cell.getStringCellValue();
                break;
            case HSSFCell.CELL_TYPE_NUMERIC:
                DecimalFormat df = new DecimalFormat("0");// 格式化 number String
                // 字符
                SimpleDateFormat sdf = new SimpleDateFormat(
                        "yyyy-MM-dd");// 格式化日期字符串
                DecimalFormat nf = new DecimalFormat("0");// 格式化数字

                if ("@".equals(cell.getCellStyle().getDataFormatString())) {
                    value = df.format(cell.getNumericCellValue());
                } else if ("General".equals(cell.getCellStyle()
                        .getDataFormatString())) {
                    value = nf.format(cell.getNumericCellValue());
                } else {
                    value = sdf.format(HSSFDateUtil.getJavaDate(cell
                            .getNumericCellValue()));
                }
                break;
            case HSSFCell.CELL_TYPE_BOOLEAN:
                value = cell.getBooleanCellValue();
                break;
            case HSSFCell.CELL_TYPE_BLANK:
                value = "";
                break;
            default:
                value = cell.toString();
        }

        return value;
    }

    private Object getCellValue(XSSFCell cell) {
        Object value;
        if (cell == null) {
            return value = "";
        }
        switch (cell.getCellType()) {
            case XSSFCell.CELL_TYPE_STRING:
                value = cell.getStringCellValue();
                break;
            case XSSFCell.CELL_TYPE_NUMERIC:
                DecimalFormat df = new DecimalFormat("0");// 格式化 number String
                // 字符
                SimpleDateFormat sdf = new SimpleDateFormat(
                        "yyyy-MM-dd");// 格式化日期字符串
                DecimalFormat nf = new DecimalFormat("0");// 格式化数字

                if ("@".equals(cell.getCellStyle().getDataFormatString())) {
                    value = df.format(cell.getNumericCellValue());
                } else if ("General".equals(cell.getCellStyle()
                        .getDataFormatString())) {
                    value = nf.format(cell.getNumericCellValue());
                } else {
                    value = sdf.format(HSSFDateUtil.getJavaDate(cell
                            .getNumericCellValue()));
                }
                break;
            case XSSFCell.CELL_TYPE_BOOLEAN:
                value = cell.getBooleanCellValue();
                break;
            case XSSFCell.CELL_TYPE_BLANK:
                value = "";
                break;
            default:
                value = cell.toString();
        }
        return value;
    }

    public static String getFileNameNoEx(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length()))) {
                return filename.substring(0, dot);
            }
        }
        return filename;
    }
}
