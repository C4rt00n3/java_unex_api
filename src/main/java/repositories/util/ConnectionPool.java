package repositories.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;

public final class ConnectionPool {
    private static final HikariDataSource DATA_SOURCE = createDataSource();

    private ConnectionPool() {
    }

    public static Connection getConnection() throws SQLException {
        return DATA_SOURCE.getConnection();
    }

    public static void close() {
        DATA_SOURCE.close();
    }

    private static HikariDataSource createDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(ConfigProperties.getRequired("datasource.url"));
        config.setUsername(ConfigProperties.getRequired("datasource.username"));
        config.setPassword(ConfigProperties.getRequired("datasource.password"));
        config.setDriverClassName(ConfigProperties.getRequired("datasource.driver-class-name"));
        config.setMaximumPoolSize(ConfigProperties.getInt("datasource.hikari.maximum-pool-size", 10));
        config.setMinimumIdle(ConfigProperties.getInt("datasource.hikari.minimum-idle", 2));
        config.setConnectionTimeout(ConfigProperties.getInt("datasource.hikari.connection-timeout", 30000));
        return new HikariDataSource(config);
    }
}
