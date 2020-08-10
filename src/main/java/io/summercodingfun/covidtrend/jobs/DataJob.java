package io.summercodingfun.covidtrend.jobs;

import io.summercodingfun.covidtrend.resources.ConnectionPool;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.knowm.sundial.Job;
import org.knowm.sundial.JobContext;
import org.knowm.sundial.exceptions.JobInterruptException;
import org.quartz.core.JobExecutionContext;
import org.quartz.jobs.JobDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.SQLException;

public class DataJob extends Job {
    private final Logger logger = LoggerFactory.getLogger(DataJob.class);
    private JobDetail jobDetail;

    protected void initContextContainer(JobExecutionContext jobExecutionContext) {
        JobContext jobContext = new JobContext();
        jobContext.addQuartzContext(jobExecutionContext);
        jobDetail = jobExecutionContext.getJobDetail();
    }

    @Override
    public void doRun() throws JobInterruptException {
        logger.info("Retrieving New Data...");
        ConnectionPool pool = (ConnectionPool)jobDetail.getJobDataMap().get("pool");
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
            InputStream stream = getClass().getResourceAsStream("loadData.sql");
            Reader reader = new InputStreamReader(stream);
            sr.runScript(reader);
        } catch (SQLException s) {
            logger.error("SQL Exception when writing in data", s);
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
