package io.summercodingfun.covidtrend.api;

import io.summercodingfun.covidtrend.resources.ConnectionPool;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.SQLException;

public class DataLoader {
    private final Logger logger = LoggerFactory.getLogger(DataLoader.class);
    private ConnectionPool pool;

    public DataLoader(ConnectionPool pool) {
        this.pool = pool;
    }

    public void getData() {
        logger.info("Retrieving New Data...");
        InputStream inputStream = null;
        try {
            inputStream = new URL("https://raw.githubusercontent.com/nytimes/covid-19-data/master/us-states.csv").openStream();
            Files.copy(inputStream, Paths.get("/tmp/us-states.csv"), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            logger.error("IO Exception when copying data", e);
        }
        Connection conn = null;
        logger.info("data retrieved");

        try {
            conn = pool.getConnection();
            ScriptRunner sr = new ScriptRunner(conn);
            File file = new File(getClass().getClassLoader().getResource("loadData.sql").getFile());
            Reader reader = new FileReader(file);
            sr.runScript(reader);
        } catch (SQLException s) {
            logger.error("SQL Exception when writing in data", s);
        } catch (FileNotFoundException f) {
            logger.error("File not found", f);
        }
        finally {
            if (conn != null) {
                try {
                    pool.returnConnection(conn);
                } catch (SQLException e) {
                    logger.error("SQL Exception when returning connection", e);
                }
            }
        }
        logger.info("loaded data successfully");
    }
}
