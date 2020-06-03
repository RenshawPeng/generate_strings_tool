package generator_language_config.frame;

import javax.swing.filechooser.FileFilter;
import java.io.File;

public class StringsFilter extends FileFilter {
    public static final String strings = "strings";


    @Override
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }
        String fileName = f.getName();
        int index = fileName.lastIndexOf('.');
        if (index > 0 && index < fileName.length() - 1) {
            String extension = fileName.substring(index + 1).toLowerCase();
            if (extension.equals(strings))
                return true;
        }
        return false;
    }

    @Override
    public String getDescription() {
        return "strings";
    }
}
