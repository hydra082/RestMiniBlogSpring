package util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class DataSourceUtil {

    private final HikariDataSource dataSource;

    public DataSourceUtil() {
        Properties props = new Properties();
        try (InputStream is = DataSourceUtil.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (is == null) {
                throw new DataSourceException("db.properties file not found in classpath");
            }
            props.load(is);
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(props.getProperty("jdbc.url"));
            config.setUsername(props.getProperty("jdbc.username"));
            config.setPassword(props.getProperty("jdbc.password"));
            config.setDriverClassName(props.getProperty("jdbc.driver"));
            config.setMaximumPoolSize(Integer.parseInt(props.getProperty("hikari.maximumPoolSize")));
            config.setMinimumIdle(Integer.parseInt(props.getProperty("hikari.minimumIdle")));
            dataSource = new HikariDataSource(config);
        } catch (IOException e) {
            throw new DataSourceException("Error uploading file: db.properties", e);
        }
    }
    public DataSourceUtil(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void close() {
        if (dataSource != null) {
            dataSource.close();
        }
    }
}
