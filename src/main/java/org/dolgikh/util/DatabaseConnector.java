package org.dolgikh.util;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

public class DatabaseConnector {
    private static String url;
    private static String user;
    private static String password;

    static {
        loadConfig();
    }

    private static void loadConfig() {
        try (InputStream in = DatabaseConnector.class.getClassLoader().getResourceAsStream("application.yaml")) {
            if (in == null) {
                throw new RuntimeException("Cannot find application.yaml in resources");
            }

            Yaml yaml = new Yaml();
            Map<String, Object> config = yaml.load(in);
            Map<String, String> db = (Map<String, String>) config.get("database");

            url = db.get("url");
            user = db.get("user");
            password = db.get("password");
        } catch (Exception e) {
            throw new RuntimeException("Failed to load database configuration", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}
