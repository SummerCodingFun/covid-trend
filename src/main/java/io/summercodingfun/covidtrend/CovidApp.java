package io.summercodingfun.covidtrend;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.summercodingfun.covidtrend.resources.*;
import io.summercodingfun.covidtrend.health.TemplateHealthCheck;

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

        final CovidCaseResource caseResource = new CovidCaseResource(pool);
        final CovidRangeDataResource rangeResource = new CovidRangeDataResource(pool);
        final LatestCovidResource latestResource = new LatestCovidResource(pool);
        final CovidCasesTrendResource casesTrendResource = new CovidCasesTrendResource(pool);
        final CovidCasesChangeResource changeResource = new CovidCasesChangeResource(pool);
        final TemplateHealthCheck healthCheck = new TemplateHealthCheck(config.getTemplate());

        env.healthChecks().register("template", healthCheck);
        env.jersey().register(caseResource);
        env.jersey().register(rangeResource);
        env.jersey().register(latestResource);
        env.jersey().register(casesTrendResource);
        env.jersey().register(changeResource);
    }
}
