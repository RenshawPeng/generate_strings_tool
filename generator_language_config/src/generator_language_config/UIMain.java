package generator_language_config;

import generator_language_config.frame.MainFrame;

import javax.swing.*;

public class UIMain {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                MainFrame mainFrame = new MainFrame();
            }
        });
    }
}
