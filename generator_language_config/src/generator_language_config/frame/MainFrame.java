package generator_language_config.frame;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainFrame extends JFrame implements ActionListener {
    private JButton excelToXmlButton;
    private JButton xmlToExcelButton;

    public MainFrame() {
        super("多语言工具-Metal");
        initUI();
        this.setResizable(false);
        this.setSize(500, 500);
        this.setVisible(true);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

    private void initUI() {
        this.setLayout(null);

        excelToXmlButton = new JButton("Excel生成XML");
        excelToXmlButton.setBounds(190, 150, 140, 40);
        this.add(excelToXmlButton);

        xmlToExcelButton = new JButton("XML生成Excel");
        xmlToExcelButton.setBounds(190, 250, 140, 40);
        this.add(xmlToExcelButton);

        setEvent();
    }

    private void setEvent() {
        excelToXmlButton.addActionListener(this);
        xmlToExcelButton.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        switch (command) {
            case "Excel生成XML":
                MainFrame.this.dispose();
                ExcelToXmlFrame excelToXmlFrame = new ExcelToXmlFrame();
                break;
            case "XML生成Excel":
                MainFrame.this.dispose();
                XmlToExcelFrame xmlToExcelFrame = new XmlToExcelFrame();
                break;
        }
    }
}
