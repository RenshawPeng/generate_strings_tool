package generator_language_config.frame;

import javax.swing.filechooser.FileFilter;
import java.io.File;

public class XmlFilter extends FileFilter {
    public static final String TYPE_XMl = "xml";

    @Override
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }
        String fileName = f.getName();
        int index = fileName.lastIndexOf('.');
        if (index > 0 && index < fileName.length() - 1) {
            String extension = fileName.substring(index + 1).toLowerCase();
            if (extension.equals(TYPE_XMl) )
                return true;
        }
        return false;
    }

    @Override
    public String getDescription() {
        return TYPE_XMl;
    }
}
