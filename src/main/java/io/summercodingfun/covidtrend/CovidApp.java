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

        final CovidCaseResource caseResource = new CovidCaseResource();
        final CovidRangeDataResource rangeResource = new CovidRangeDataResource();
        final LatestCovidResource latestResource = new LatestCovidResource();
        final CovidCasesTrendResource casesTrendResource = new CovidCasesTrendResource();
        final CovidCasesChangeResource changeResource = new CovidCasesChangeResource();
        final TemplateHealthCheck healthCheck = new TemplateHealthCheck(config.getTemplate());

        env.healthChecks().register("template", healthCheck);
        env.jersey().register(caseResource);
        env.jersey().register(rangeResource);
        env.jersey().register(latestResource);
        env.jersey().register(casesTrendResource);
        env.jersey().register(changeResource);
    }
}
