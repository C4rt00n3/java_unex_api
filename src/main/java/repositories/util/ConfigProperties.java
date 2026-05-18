package repositories.util;

import br.com.dende.softhouse.annotations.Component;
import br.com.dende.softhouse.annotations.Value;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Component
public final class ConfigProperties {
    private static final String CONFIG_FILE = "application.properties";
    private static final Properties PROPERTIES = load();

    @Value(key = "datasource.url")
    private String datasourceUrl;

    @Value(key = "datasource.username")
    private String datasourceUsername;

    @Value(key = "datasource.password")
    private String datasourcePassword;

    @Value(key = "datasource.driver-class-name")
    private String datasourceDriverClassName;

    @Value(key = "datasource.hikari.maximum-pool-size")
    private String maximumPoolSize;

    @Value(key = "datasource.hikari.minimum-idle")
    private String minimumIdle;

    @Value(key = "datasource.hikari.connection-timeout")
    private String connectionTimeout;

    private ConfigProperties() {
    }

    public static String getRequired(String key) {
        String value = PROPERTIES.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Propriedade obrigatoria nao configurada: " + key);
        }
        return value;
    }

    public static String get(String key, String defaultValue) {
        return PROPERTIES.getProperty(key, defaultValue);
    }

    public static int getInt(String key, int defaultValue) {
        String value = PROPERTIES.getProperty(key);
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        return Integer.parseInt(value);
    }

    private static Properties load() {
        Properties properties = new Properties();
        try (InputStream inputStream = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream(CONFIG_FILE)) {
            if (inputStream == null) {
                throw new IllegalStateException(CONFIG_FILE + " nao encontrado no classpath");
            }
            properties.load(inputStream);
            return properties;
        } catch (IOException exception) {
            throw new IllegalStateException("Erro ao carregar " + CONFIG_FILE, exception);
        }
    }
}
