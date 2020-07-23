package io.summercodingfun.covidtrend.jobs;

import io.summercodingfun.covidtrend.resources.ConnectionPool;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.knowm.sundial.Job;
import org.knowm.sundial.JobContext;
import org.knowm.sundial.annotations.CronTrigger;
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

@CronTrigger(cron = "0 0 0 * * ?")
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
            e.printStackTrace();
            logger.error("IO Exception when copying data");
        }
        Connection conn = null;

        try {
            conn = pool.getConnection();
            ScriptRunner sr = new ScriptRunner(conn);
            Reader reader = new BufferedReader(new FileReader("src/main/java/io/summercodingfun/covidtrend/resources/covidDB.sql"));
            sr.runScript(reader);
        } catch (SQLException s) {
            s.printStackTrace();
            logger.error("SQL Exception when writing in data");
        } catch (FileNotFoundException f) {
            f.printStackTrace();
            logger.error("File not found");
        }
        finally {
            if (conn != null) {
                try {
                    pool.returnConnection(conn);
                } catch (SQLException e) {
                    e.printStackTrace();
                    logger.error("SQL Exception when returning connection");
                }
            }
        }
    }
}
