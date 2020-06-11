package io.summercodingfun.covidtrend.resources;

import java.sql.*;

public class ConnectionUtil {
    Connection connection;
    public ConnectionUtil() throws Exception {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/covid_data?characterEncoding=latin1", "root", "Ye11owstone");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public int getCases(String currentState, String currentDate) throws SQLException {
        int stateCases = 0;
        try {
            Statement statement = connection.createStatement();
            String stmt = String.format("select cases from usStates where state = '%s' AND date = '%s'", currentState, currentDate);
            ResultSet resultSet = statement.executeQuery(stmt);
            while (resultSet.next()){
                stateCases = resultSet.getInt("cases");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return stateCases;
    }

    public int getDeaths(String currentState, String currentDate) throws SQLException {
        int stateDeaths = 0;
        try {
            Statement statement = connection.createStatement();
            String stmt = String.format("select deaths from usStates where state = '%s' AND date = '%s'", currentState, currentDate);
            ResultSet resultSet = statement.executeQuery(stmt);
            while (resultSet.next()){
                stateDeaths = resultSet.getInt("deaths");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return stateDeaths;
    }

    public void close() throws SQLException {
        connection.close();
    }
}
