package io.summercodingfun.covidtrend.api;

import io.summercodingfun.covidtrend.resources.ConnectionPool;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.reader.StreamReader;

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
        logger.info("Retrieving new Data...");

        try {
            InputStream inputStream = new URL("https://raw.githubusercontent.com/nytimes/covid-19-data/master/us-states.csv").openStream();
            Files.copy(inputStream, Paths.get("/tmp/us-states.csv"), StandardCopyOption.REPLACE_EXISTING);
            logger.info("New data retrieved");
        } catch (IOException e) {
            logger.error("IO Exception when copying new data", e);
            return;
        } catch (Exception e) {
            logger.error("Unknown error retrieving new data", e);
        }

        Connection conn = null;
        try {
            conn = pool.getConnection();
            ScriptRunner sr = new ScriptRunner(conn);
            InputStream resource = getClass().getClassLoader().getResourceAsStream("loadData.sql");

            if (resource == null) {
                logger.error("Cannot find loadData script");
                return;
            } else {
                logger.info("Found loadData script: {}", resource);
                Reader reader = new InputStreamReader(resource);
                sr.runScript(reader);
                logger.info("loaded data successfully");
            }
        } catch (SQLException s) {
            logger.error("SQL Exception when writing in data", s);
        } catch (Exception e) {
            logger.error("Unknown error running loadData", e);
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
    }
}
