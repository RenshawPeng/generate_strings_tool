package generator_language_config.util;


import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class StringsUtil {
    private static final String KEY_FLAG = "key";
    public static final String ANNOTATION_FLAG = "##ANN##";
    public static final String DEFAULT_FLAG = "default";

    public void readStrings(File inputFile, File outputFile) throws IOException {
        if (!inputFile.exists() || !outputFile.exists()) {
            throw new IOException("文件不存在");
        }
        String fileName = inputFile.getName();
        String extension = fileName.lastIndexOf(".") == -1 ? "" : fileName.substring(fileName.lastIndexOf(".") + 1);
        if ("strings".equals(extension)) {
            readStrings1(inputFile);
        } else {
            throw new IOException("不支持的文件类型");
        }
    }

    public void writStrings(File file, Map<String, Object> map) throws IOException {
        try {
            Map<String, Object> readFormatXML = readStrings1(file);
            readFormatXML.putAll(map);
            map = readFormatXML;
        } catch (Exception e) {
            e.printStackTrace();
        }

        BufferedWriter br = new BufferedWriter(new FileWriter(file.getAbsolutePath()));

        for (String s : map.keySet()) {
            String line = s + " = " + map.get(s);
            if (!s.startsWith("\"")) {
                line = "\"" + s + "\"" + " = " + "\"" + map.get(s) + "\";";
            }
            br.write(line);
            br.newLine();
            br.flush();
        }
        br.close();
    }

    private LinkedHashMap<String, Object> readStrings1(File file) {
        try {
            FileReader reader = new FileReader(file);//定义一个fileReader对象，用来初始化BufferedReader
            BufferedReader bReader = new BufferedReader(reader);//new一个BufferedReader对象，将文件内容读取到缓存
            StringBuilder sb = new StringBuilder();//定义一个字符串缓存，将字符串存放缓存中
            String s = "";
            while ((s = bReader.readLine()) != null) {//逐行读取文件内容，不读取换行符和末尾的空格
                sb.append(s);//将读取的字符串添加换行符后累加存放在缓存中
            }
            bReader.close();
            String str = sb.toString();
            return getKeyAndValues(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new LinkedHashMap<>();
    }

    private LinkedHashMap<String, Object> getKeyAndValues(String s1) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        String[] split = s1.split("\";");

        for (String s : split) {
            if (!s.startsWith("\"")) {
                int i = s.indexOf("\"");
                if (i > 0) {
                    s = s.substring(i);
                }
            }

            getKeyValue(map, s);
        }
        return map;
    }

    private void getKeyValue(Map<String, Object> map, String s) {
        try {

            String replace = s.replace("\\\"", "");
            int start = replace.indexOf("\"", replace.indexOf("\"") + 1);
            int end = replace.indexOf("\"", start + 1);

            String regexString = replace.substring(start, end + 1);
            String[] strings = s.split(regexString);
            if (strings.length > 1) {
                map.put(strings[0].replaceFirst("\"", ""), strings[1]);
                System.out.println("key = " + strings[0].replaceFirst("\"", ""));
                System.out.println("value = " + strings[1]);
            } else {
                System.out.println("解析失败了 ： " + s);
            }
        } catch (Exception e) {
            System.out.println("解析失败了 ： " + s);
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


    public List<String> removeComments(String[] source) {
        // write your code here
        List<String> list = new ArrayList<>();
        boolean block = false;
        String leftString = "";
        for (int i = 0; i < source.length; i++) {
            String s = source[i];
            if (block) {
                int i1 = s.indexOf("*/");
                if (i1 >= 0) {
                    String rightString = s.substring(i1 + 2);
                    leftString += rightString;
                    source[i] = leftString;
                    i--;
                    block = false;
                }
            } else {
                int i1 = s.indexOf("//");
                int i2 = s.indexOf("/*");
                if (i1 < 0 && i2 < 0) {
                    if (s.length() > 0) {
                        list.add(s);
                    }
                } else if (i1 < 0 || (i2 >= 0 && i2 < i1)) {
                    block = true;
                    leftString = s.substring(0, i2);
                    int i3 = s.indexOf("*/", i2 + 2);
                    if (i3 >= 0) {
                        String rightString = s.substring(i3 + 2);
                        leftString += rightString;
                        source[i] = leftString;
                        i--;
                        block = false;
                    }
                } else if (i2 < 0 || i1 < i2) {
                    String substring = s.substring(0, i1);
                    if (substring.length() > 0) {
                        list.add(substring);
                    }
                }
            }
        }
        return list;
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
