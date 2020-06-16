package io.summercodingfun.covidtrend.resources;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionUtil {
    private static Logger logger = LoggerFactory.getLogger("ConnectionUtil");

    private ConnectionUtil() throws IOException {
        logger.info("starting connection util");
    }

    public static int getCases(Connection conn, String currentState, String currentDate) throws SQLException, ParseException {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        int stateCases = 0;
        try {
            Statement statement = conn.createStatement();
            String stmt = String.format("select cases from usStates where state = '%s' AND theDate = '%s'", currentState, fmt.format(fmt.parse(currentDate)));
            ResultSet resultSet = statement.executeQuery(stmt);
            while (resultSet.next()){
                stateCases = resultSet.getInt("cases");
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
        return stateCases;
    }

    public static int getDeaths(Connection conn, String currentState, String currentDate) throws SQLException, ParseException {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        int stateDeaths = 0;
        try {
            Statement statement = conn.createStatement();
            String stmt = String.format("select deaths from usStates where state = '%s' AND theDate = '%s'", currentState, fmt.format(fmt.parse(currentDate)));
            ResultSet resultSet = statement.executeQuery(stmt);
            while (resultSet.next()){
                stateDeaths = resultSet.getInt("deaths");
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
        return stateDeaths;
    }

    public static DateTime getMinDate(Connection conn, String currentState) throws SQLException {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        DateTimeFormatter fmt2 = DateTimeFormat.forPattern("yyyy-MM-dd");
        DateTime minDate = new DateTime();
        try {
            Date md = new Date();
            Statement statement = conn.createStatement();
            String stmt = String.format("select MIN(theDate) from usStates where state = '%s'", currentState);
            ResultSet resultSet = statement.executeQuery(stmt);
            while (resultSet.next()) {
                md = resultSet.getDate("MIN(theDate)");
            }
            long millis = fmt2.parseMillis(fmt.format(md));
            minDate = new DateTime(millis);
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
        logger.info("the min date is: {}", minDate);
        return minDate;
    }

    public static DateTime getMaxDate(Connection conn, String currentState) throws SQLException {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        DateTimeFormatter fmt2 = DateTimeFormat.forPattern("yyyy-MM-dd");
        DateTime maxDate = new DateTime();
        try {
            Date md = new Date();
            Statement statement = conn.createStatement();
            String stmt2 = String.format("select MAX(theDate) from usStates where state = '%s'", currentState);
            ResultSet resultSet = statement.executeQuery(stmt2);
            while (resultSet.next()) {
                md = resultSet.getDate("MAX(theDate)");
            }
            long millis = fmt2.parseMillis(fmt.format(md));
            maxDate = new DateTime(millis);
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
        logger.info("the max date is: {}", maxDate);
        return maxDate;
    }

    public static boolean isAvailable(Connection conn, String currentState) throws SQLException {
        try {
            Statement statement = conn.createStatement();
            String stmt = String.format("select state from usStates where state = '%s'", currentState);
            ResultSet rs = statement.executeQuery(stmt);
            if (rs.next() == false) {
                return false;
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw e;
        }

        return true;
    }

    public static boolean isAvailable(Connection conn, String currentState, String currentDate) throws SQLException, ParseException {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Statement statement = conn.createStatement();
            String stmt = String.format("select cases from usStates where state = '%s' AND theDate = '%s'", currentState, fmt.format(fmt.parse(currentDate)));
            ResultSet rs = statement.executeQuery(stmt);
            if (rs.next() == false) {
                return false;
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw e;
        }

        return true;
    }
}
