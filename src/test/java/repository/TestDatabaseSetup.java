package repository;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.PostgreSQLContainer;
import util.DataSourceUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class TestDatabaseSetup {

    @SuppressWarnings("resource")
    protected static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    protected static DataSourceUtil dataSourceUtil;

    @BeforeAll
    static void setupDatabase() {
        postgres.start();
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(postgres.getJdbcUrl());
        config.setUsername(postgres.getUsername());
        config.setPassword(postgres.getPassword());
        config.setDriverClassName("org.postgresql.Driver");
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setIdleTimeout(60000);
        config.setMaxLifetime(180000);
        config.setConnectionTimeout(30000);
        HikariDataSource dataSource = new HikariDataSource(config);
        dataSourceUtil = new DataSourceUtil(dataSource);
        initializeSchema();
    }

    private static void initializeSchema() {
        try (Connection conn = dataSourceUtil.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id SERIAL PRIMARY KEY,
                    name VARCHAR(255) NOT NULL
                )
                """);
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS posts (
                    id SERIAL PRIMARY KEY,
                    title VARCHAR(255) NOT NULL,
                    content TEXT,
                    user_id BIGINT NOT NULL,
                    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
                )
                """);
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS comments (
                    id SERIAL PRIMARY KEY,
                    text TEXT NOT NULL,
                    user_id BIGINT NOT NULL,
                    post_id BIGINT NOT NULL,
                    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                    FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE
                )
                """);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database schema", e);
        }
    }

    @AfterAll
    static void stopDatabase() {
        if (dataSourceUtil != null) {
            dataSourceUtil.close();
            dataSourceUtil = null;
        }
        if (postgres != null) {
            postgres.stop();
        }
    }

    protected DataSourceUtil getDataSourceUtil() {
        return dataSourceUtil;
    }
}
