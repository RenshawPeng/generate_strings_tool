package generator_language_config.util;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import org.apache.poi.xssf.usermodel.*;
import org.dom4j.DocumentException;

import java.io.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static generator_language_config.util.ExcelToStringsUtil.getFileNameNoEx;

public class ExcelUtil {
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

    public void writXLSXExcel(String xmlResPath, File outputFile) throws IOException {
        if (xmlResPath == null || xmlResPath.equals("")) {
            throw new IOException("文件不存在");
        }
        try {
            Map<String, File> files = new HashMap<>();
            listStringFile(xmlResPath, files);

            Set<String> langs = files.keySet();

            Map<String, List<StringEntity>> map = new HashMap<>();


            XSSFWorkbook xwb = new XSSFWorkbook();
            XSSFSheet sheet = xwb.createSheet("Sheet1");
            XSSFRow row = sheet.createRow(0);
            row.createCell(0).setCellValue(KEY_FLAG);

            filterXmlString(files, langs, map, row);

            XSSFCellStyle style = xwb.createCellStyle();
            int rowIdx = 1;
            for (Map.Entry<String, List<StringEntity>> entry : map.entrySet()) {
                //第三步创建行row:添加表头0行
                if (entry.getKey().contains(ANNOTATION_FLAG)) {
                    continue;
                }
                XSSFRow createRow = sheet.createRow(rowIdx++);
                createRow.createCell(0).setCellValue(entry.getKey());
                for (StringEntity entity : entry.getValue()) {
                    createRow.createCell(entity.cel).setCellValue(entity.value);
                }
            }
            //将excel写入
            OutputStream stream = new FileOutputStream(outputFile.getAbsolutePath() + File.separator + getFileNameNoEx("Strings") + ".xlsx");
            xwb.write(stream);
            stream.close();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void filterXmlString(Map<String, File> files, Set<String> langs, Map<String, List<StringEntity>> map, XSSFRow row) throws DocumentException {
        int col = 1;
        for (String lang : langs) {
            row.createCell(col).setCellValue(lang);
            Map<String, Object> xmls = XMLUtil.readFormatXML(files.get(lang));
            for (Map.Entry<String, Object> entry : xmls.entrySet()) {
                List<StringEntity> stringEntities = map.get(entry.getKey());
                if (stringEntities == null) {
                    stringEntities = new ArrayList<>();
                }
                StringEntity stringEntity = new StringEntity(lang, entry.getKey(), entry.getValue().toString());
                stringEntity.cel = col;
                stringEntities.add(stringEntity);
                map.put(entry.getKey(), stringEntities);
            }

            col++;
        }
    }

    private void listStringFile(String xmlResPath, Map<String, File> files) {
        File[] tempList = new File(xmlResPath).listFiles();
        for (File value : tempList) {
            if (value.isFile()) {
                if (value.getName().equals("strings.xml")) {
                    String parent = value.getParent();
                    String fileFolderName = parent.substring(parent.lastIndexOf(File.separator) + 1);
                    files.put(fileFolderName.substring(fileFolderName.indexOf("-") + 1), value);
                    System.out.println(fileFolderName);
                }

            } else if (value.isDirectory() && value.toString().contains("values")) {
                listStringFile(value.getAbsolutePath(), files);
            } else {
//                System.out.println("不生成 = " + value.toString());
            }
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
                File xmlFile = new File(outputFile + File.separator + "string-" + fileList.get(i - 1) + ".xml");
                XMLUtil.writFormatXML(xmlFile, map);
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
                String lang = fileList.get(i - 1);
                File xmlFile = new File(outputFile + File.separator + "values-" + lang + File.separator + "strings" + ".xml");
                if (lang.equals("en")) {
                    xmlFile = new File(outputFile + File.separator + "values" + File.separator + "strings" + ".xml");
                }
                makeDirectory(xmlFile);
                System.out.println(map.toString());
                XMLUtil.writFormatXML(xmlFile, map);
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
            boolean mkdirs = file.getParentFile().mkdirs();
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
