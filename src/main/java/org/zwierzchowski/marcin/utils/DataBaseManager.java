package org.zwierzchowski.marcin.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBaseManager {

  String userName = "marcin";
  String password = "pass";
  String url = "jdbc:postgresql://localhost:5432/client_server";

  public Connection getConnection() {

    try {
      Connection conn = DriverManager.getConnection(url, userName, password);
      return conn;
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    }
  }
}
