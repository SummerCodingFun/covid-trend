package io.summercodingfun.covidtrend;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.summercodingfun.covidtrend.resources.CovidCasesTrendResource;
import io.summercodingfun.covidtrend.resources.CovidRangeDataResource;
import io.summercodingfun.covidtrend.resources.CovidCaseResource;
import io.summercodingfun.covidtrend.health.TemplateHealthCheck;
import io.summercodingfun.covidtrend.resources.LatestCovidResource;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.SortedMap;
import java.util.TreeMap;


public class CovidApp extends Application<CovidConfig>{
    public static void main(String[] args) throws Exception {
        new CovidApp().run(args);
    }

    @Override
    public String getName() {
        return "covid-cases";
    }

    @Override
    public void initialize(Bootstrap<CovidConfig> bootstrap){
    }

    @Override
    public void run(CovidConfig config, Environment env)  throws IOException {
        SortedMap<String, Integer> cases = new TreeMap<>();
        SortedMap<String, Integer> deaths = new TreeMap<>();

        BufferedReader reader = new BufferedReader(new FileReader("us-states.csv"));
        String line = null;
        reader.readLine();
        DateTime maxDate = new DateTime(2020, 1, 1, 0, 0);
        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");
        while((line = reader.readLine()) != null){
            String[] arr = line.split(",");
            String k = createKey(arr[1], arr[0]);
            cases.put(k, Integer.parseInt(arr[3]));
            deaths.put(k, Integer.parseInt(arr[4]));

            long millis = fmt.parseMillis(arr[0]);
            if(maxDate.isBefore(millis)) {
                maxDate = new DateTime(millis);
            }
        }

        final CovidCaseResource caseResource = new CovidCaseResource(cases, deaths);
        final CovidRangeDataResource rangeResource = new CovidRangeDataResource(cases, deaths);
        final LatestCovidResource latestResource = new LatestCovidResource(cases, deaths, maxDate);
        final CovidCasesTrendResource casesTrendResource = new CovidCasesTrendResource(cases);
        final TemplateHealthCheck healthCheck = new TemplateHealthCheck(config.getTemplate());

        env.healthChecks().register("template", healthCheck);
        env.jersey().register(caseResource);
        env.jersey().register(rangeResource);
        env.jersey().register(latestResource);
        env.jersey().register(casesTrendResource);
    }
    public String createKey(String x, String y){
        return x + ":" + y;
    }
}
