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
        String url = config.getDatabase().getUrl();
        ConnectionPool pool = new ConnectionPool(
                url + "?characterEncoding=latin1",
                config.getDatabase().getUser(),
                config.getDatabase().getPassword(),
                10
        );

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
