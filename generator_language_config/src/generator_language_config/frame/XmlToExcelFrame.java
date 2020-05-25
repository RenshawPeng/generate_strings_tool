package generator_language_config.frame;

import generator_language_config.ExcelUtil;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class XmlToExcelFrame extends JFrame implements ActionListener {
    private JButton homeButton;
    private JButton openFileButton;
    private JTextField openFileTextField;
    private JLabel openFileLabel;

    private JButton outputFileButton;
    private JTextField outputFileTextField;
    private JLabel outputFileLabel;

    private JButton generatorButton;

    private File openFile;
    private File outputFile;

    public XmlToExcelFrame() {
        super("XML生成Excel");
        initUI();
        this.setResizable(false);
        this.setSize(500, 500);
        this.setVisible(true);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void initUI() {
        this.setLayout(null);
        homeButton = new JButton("返回");
        homeButton.setBounds(20, 20, 60, 40);
        homeButton.addActionListener(this);
        this.add(homeButton);

        openFileLabel = new JLabel("XML:");
        openFileLabel.setBounds(60, 90, 40, 40);

        openFileTextField = new JTextField(200);
        openFileTextField.setEnabled(false);
        openFileTextField.setBounds(110, 90, 200, 40);
        this.add(openFileTextField);

        openFileButton = new JButton("选择文件");
        openFileButton.setBounds(320, 90, 90, 40);
        openFileButton.addActionListener(this);
        this.add(openFileLabel);
        this.add(openFileTextField);
        this.add(openFileButton);

        outputFileLabel = new JLabel("生成位置:");
        outputFileLabel.setBounds(40, 160, 60, 40);

        outputFileTextField = new JTextField(200);
        outputFileTextField.setEnabled(false);
        outputFileTextField.setBounds(110, 160, 200, 40);


        outputFileButton = new JButton("选择文件");
        outputFileButton.setBounds(320, 160, 90, 40);
        outputFileButton.addActionListener(this);
        this.add(outputFileLabel);
        this.add(outputFileTextField);
        this.add(outputFileButton);

        generatorButton = new JButton("开始生成");
        generatorButton.setBounds(150, 260, 200, 40);
        generatorButton.addActionListener(this);
        this.add(generatorButton);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == homeButton){
            MainFrame frame=new MainFrame();
            XmlToExcelFrame.this.dispose();
        }else if (e.getSource() == openFileButton) {
            selectXml();
        } else if (e.getSource() == outputFileButton) {
            selectFile();
        } else if (e.getSource() == generatorButton) {
            if (openFile == null) {
                JOptionPane.showMessageDialog(null, "请选择strings.xml", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (outputFile == null) {
                JOptionPane.showMessageDialog(null, "请选择Excel生成位置", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                ExcelUtil excelUtil = new ExcelUtil();
                excelUtil.writXLSXExcel(openFile, outputFile);
                JOptionPane.showMessageDialog(null, "生成完毕", "提示", JOptionPane.WARNING_MESSAGE);
            } catch (IOException e1) {
                JOptionPane.showMessageDialog(null, "很抱歉，发生错误！", "提示", JOptionPane.WARNING_MESSAGE);
                e1.printStackTrace();
            }
        }
    }

    private File selectXml() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.addChoosableFileFilter(new XmlFilter());
        chooser.showDialog(new JLabel(), "选择");
        File file = chooser.getSelectedFile();
        if (file != null && file.isFile()) {
            openFile = file;
            openFileTextField.setText(file.getAbsolutePath());
        }
        return null;
    }

    private File selectFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.showDialog(new JLabel(), "选择");
        File file = chooser.getSelectedFile();
        if (file != null && file.isDirectory()) {
            outputFile = file;
            outputFileTextField.setText(file.getAbsolutePath());
        }
        return null;
    }
}
