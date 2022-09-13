package Util;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class Util {
    public static String getEnvValue(String s) {
        try {
            return Dotenv.configure().load().get(s);
        } catch (DotenvException e) {
            return System.getenv().get(s);
        } catch (Exception e) {
            System.out.println("Cannot get environment value");
        }
        return "";
    }

    public static Connection getConnection() {
        Connection conn = null;
        try {
            // This is for production
            DriverManager.registerDriver(new org.postgresql.Driver());
            conn = DriverManager.getConnection(getEnvValue("POSTGRES_URL"));
        } catch(SQLException e) {
            try {
                // This is for dev
                System.out.println(getEnvValue("POSTGRES_URL"));
                conn = DriverManager.getConnection(
                        getEnvValue("POSTGRES_URL"),
                        getEnvValue("POSTGRES_USER"),
                        getEnvValue("POSTGRES_PASSWORD"));
            } catch (SQLException ex) {
                System.out.println(ex);
            }
        }
        return conn;
    }
}
