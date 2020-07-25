package io.summercodingfun.covidtrend;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.summercodingfun.covidtrend.resources.*;
import io.summercodingfun.covidtrend.health.TemplateHealthCheck;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.knowm.dropwizard.sundial.SundialBundle;
import org.knowm.dropwizard.sundial.SundialConfiguration;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import java.util.EnumSet;

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
        bootstrap.addBundle(new SundialBundle<CovidConfig>() {
            @Override
            public SundialConfiguration getSundialConfiguration(CovidConfig config) {
                return config.getSundialConfiguration();
            }
        });
    }

    @Override
    public void run(CovidConfig config, Environment env) throws Exception {
        ConnectionPool pool = new ConnectionPool("jdbc:mysql://localhost:3306/covid_data?characterEncoding=latin1", "root", "Ye11owstone", 10);
        enableCorsHeaders(env);

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

    private void enableCorsHeaders(Environment env) {
        final FilterRegistration.Dynamic cors = env.servlets().addFilter("CORS", CrossOriginFilter.class);

        cors.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
        cors.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM, "X-Requested-With,Content-Type,Accept,Origin");
        cors.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET,POST,DELETE");

        cors.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
    }
}
