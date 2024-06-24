package org.zwierzchowski.marcin.utils;

import org.jooq.tools.JooqLogger;
import org.zwierzchowski.marcin.exception.DatabaseConnectionException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBaseManager {

  String userName = "marcin";
  String password = "passs";
  String url = "jdbc:postgresql://localhost:5432/client_server";

  public Connection getConnection() throws DatabaseConnectionException {
    JooqLogger.globalThreshold(org.jooq.tools.JooqLogger.Level.WARN);
    try {
      Connection conn = DriverManager.getConnection(url, userName, password);
      return conn;
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DatabaseConnectionException("Cannot connect to database");
    }
  }
}
