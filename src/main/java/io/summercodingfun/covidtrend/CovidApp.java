package io.summercodingfun.covidtrend;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.summercodingfun.covidtrend.resources.*;
import io.summercodingfun.covidtrend.health.TemplateHealthCheck;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;

public class CovidApp extends Application<CovidConfig> {

    public static void main(String[] args) throws Exception {
        new CovidApp().run(args);
    }

    @Override
    public String getName() {
        return "covid-cases";
    }

    @Override
    public void initialize(Bootstrap<CovidConfig> bootstrap) {
    }

    @Override
    public void run(CovidConfig config, Environment env) throws Exception {
        ConnectionPool pool = new ConnectionPool("jdbc:mysql://localhost:3306/covid_data?characterEncoding=latin1", "root", "Ye11owstone", 10);

        DateTime date = new DateTime();
        date = date.minusDays(1);
        boolean upToDate = true;
        Connection conn = null;
        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");

        try {
            conn = pool.getConnection();
            if ((ConnectionUtil.getMaxDate(conn, "Alabama")).isBefore(date)) {
                upToDate = false;
            }
        } finally {
            if (conn != null) {
                pool.returnConnection(conn);
            }
        }

        if (!upToDate) {
            InputStream inputStream = new URL("https://raw.githubusercontent.com/nytimes/covid-19-data/master/us-states.csv").openStream();
            Files.copy(inputStream, Paths.get("src/main/java/io/summercodingfun/covidtrend/resources/us-states.csv"), StandardCopyOption.REPLACE_EXISTING);

            try {
                conn = pool.getConnection();
                ScriptRunner sr = new ScriptRunner(conn);
                Reader reader = new BufferedReader(new FileReader("src/main/java/io/summercodingfun/covidtrend/resources/covidDB.sql"));
                sr.runScript(reader);
            } finally {
                if (conn != null) {
                    pool.returnConnection(conn);
                }
            }
        }

        final CovidCaseResource caseResource = new CovidCaseResource(pool);
        final CovidRangeDataResource rangeResource = new CovidRangeDataResource(pool);
        final LatestCovidResource latestResource = new LatestCovidResource(pool);
        final CovidCasesTrendResource casesTrendResource = new CovidCasesTrendResource(pool);
        final CovidCasesChangeResource changeResource = new CovidCasesChangeResource(pool);
        final CovidComparisonChartResource covidComparisonChartResourcechartResource = new CovidComparisonChartResource(pool);
        final TemplateHealthCheck healthCheck = new TemplateHealthCheck(config.getTemplate());

        env.healthChecks().register("template", healthCheck);
        env.jersey().register(caseResource);
        env.jersey().register(rangeResource);
        env.jersey().register(latestResource);
        env.jersey().register(casesTrendResource);
        env.jersey().register(changeResource);
        env.jersey().register(covidComparisonChartResourcechartResource);
    }
}
