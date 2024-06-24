package org.zwierzchowski.marcin.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import lombok.extern.log4j.Log4j2;
import org.jooq.tools.JooqLogger;
import org.zwierzchowski.marcin.exception.DatabaseConnectionException;

@Log4j2
public class DataBaseManager {

  String username;
  String password;
  String url;

  public DataBaseManager() {
    getCredentials();
  }

  public Connection getConnection() throws DatabaseConnectionException {
    JooqLogger.globalThreshold(org.jooq.tools.JooqLogger.Level.WARN);
    try {
        return DriverManager.getConnection(url, username, password);
    } catch (SQLException e) {
      log.error("Error connecting to database", e);
      throw new DatabaseConnectionException("Cannot connect to database");
    }
  }

  private void getCredentials() {

    Properties properties = new Properties();

    try {
      properties.load(DataBaseManager.class.getClassLoader().getResourceAsStream("db.properties"));
      username = properties.getProperty("username");
      password = properties.getProperty("password");
      url = properties.getProperty("url");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
