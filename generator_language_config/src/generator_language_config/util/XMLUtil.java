package generator_language_config.util;

import org.dom4j.*;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import static generator_language_config.util.ExcelUtil.ANNOTATION_FLAG;

public final class XMLUtil {
    public static void writFormatXML(File file, Map<String, Object> map) {

        try {
            Map<String, Object> readFormatXML = readFormatXML(file);
            readFormatXML.putAll(map);
            map = readFormatXML;
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        //拼装写入格式
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement("resources");
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getKey().contains(ANNOTATION_FLAG)) {
                root.addComment(entry.getValue().toString());
                continue;
            }
            Element element = root.addElement("string");
            element.addAttribute("name", entry.getKey());
            element.setText(entry.getValue().toString());
        }

        // 实例化输出格式对象
        OutputFormat format = OutputFormat.createPrettyPrint();
        // 设置输出编码
        format.setEncoding("UTF-8");
        XMLWriter writer;
        try {
            // 生成XMLWriter对象
            writer = new XMLWriter(new FileOutputStream(file), format);
            // 开始写入，write方法中包含上面创建的Document对象
            writer.write(document);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Map<String, Object> readFormatXML(File file) throws DocumentException {
        if (!file.exists()) {
            throw new DocumentException("文件不存在");
        }
        // 创建saxReader对象
        SAXReader reader = new SAXReader();
        // 通过read方法读取一个文件 转换成Document对象
        Document document = reader.read(file);
        //获取根节点元素对象
        Element rootElement = document.getRootElement();
        Map<String, Object> map = new LinkedHashMap<>();
        for (int i = 0, size = rootElement.nodeCount(); i < size; i++) {
            Node node = rootElement.node(i);
            short nodeType = node.getNodeType();
            switch (nodeType) {
                case Node.COMMENT_NODE:
                    map.put(ANNOTATION_FLAG + i, node.getText());
                    break;
                case Node.ELEMENT_NODE:
                    Element element = (Element) node;
                    String attributeKey = element.attribute("name").getValue();
                    String value = element.getText();
                    map.put(attributeKey, value);
                    break;
            }
        }
        return map;
    }
}
