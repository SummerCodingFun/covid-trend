package io.summercodingfun.covidtrend.jobs;

import io.summercodingfun.covidtrend.resources.ConnectionPool;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.knowm.sundial.Job;
import org.knowm.sundial.JobContext;
import org.knowm.sundial.annotations.CronTrigger;
import org.knowm.sundial.annotations.SimpleTrigger;
import org.knowm.sundial.exceptions.JobInterruptException;
import org.quartz.core.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

//@CronTrigger(cron = "0 0 0 * * ?")
@SimpleTrigger(repeatInterval = 30, timeUnit = TimeUnit.SECONDS)

public class DataJob extends Job {
    private final Logger logger = LoggerFactory.getLogger(DataJob.class);

    protected void initContextContainer(JobExecutionContext jobExecutionContext) {
        JobContext jobContext = new JobContext();
        jobContext.addQuartzContext(jobExecutionContext);
    }

    @Override
    public void doRun() throws JobInterruptException {
        logger.info("Retrieving New Data...");
        ConnectionPool pool = new ConnectionPool("jdbc:mysql://localhost:3306/covid_data?characterEncoding=latin1", "root", "Ye11owstone", 10);
        InputStream inputStream = null;
        try {
            inputStream = new URL("https://raw.githubusercontent.com/nytimes/covid-19-data/master/us-states.csv").openStream();
            Files.copy(inputStream, Paths.get("src/main/java/io/summercodingfun/covidtrend/resources/us-states.csv"), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            logger.error("IO Exception when copying data", e);
        }
        Connection conn = null;

        try {
            conn = pool.getConnection();
            ScriptRunner sr = new ScriptRunner(conn);
            Reader reader = new BufferedReader(new FileReader("src/main/java/io/summercodingfun/covidtrend/resources/loadData.sql"));
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
    }
}
