package io.summercodingfun.covidtrend.jobs;

import io.summercodingfun.covidtrend.api.DataLoader;
import io.summercodingfun.covidtrend.resources.ConnectionPool;
import org.knowm.sundial.Job;
import org.knowm.sundial.JobContext;
import org.knowm.sundial.exceptions.JobInterruptException;
import org.quartz.core.JobExecutionContext;
import org.quartz.jobs.JobDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        ConnectionPool pool = (ConnectionPool)jobDetail.getJobDataMap().get("pool");
        DataLoader dataLoader = new DataLoader(pool);
        dataLoader.getData();
    }

}
