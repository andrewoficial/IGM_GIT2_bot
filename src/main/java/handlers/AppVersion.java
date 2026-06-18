package handlers;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AppVersion {
    private static final String UNKNOWN = "unknown";
    private static String version = UNKNOWN;

    static {
        try (InputStream in = AppVersion.class.getClassLoader().getResourceAsStream("version.properties")) {
            if (in != null) {
                Properties props = new Properties();
                props.load(in);
                version = props.getProperty("version", UNKNOWN);
            }
        } catch (IOException e) {
            // keep unknown
        }
    }

    public static String get() {
        return version;
    }
}
