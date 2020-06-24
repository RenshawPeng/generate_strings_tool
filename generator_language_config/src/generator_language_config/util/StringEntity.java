package generator_language_config.util;

public class StringEntity {
    public String lang;
    public String key;
    public String value;
    public int cel;//第几列
    public int row;//第几行


    public StringEntity(String lang, String key, String value) {
        this.lang = lang;
        this.key = key;
        this.value = value;
    }

    public StringEntity() {
    }
}
