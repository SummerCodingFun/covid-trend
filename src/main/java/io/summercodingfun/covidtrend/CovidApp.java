package io.summercodingfun.covidtrend;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.summercodingfun.covidtrend.jobs.DataJob;
import io.summercodingfun.covidtrend.resources.*;
import io.summercodingfun.covidtrend.health.TemplateHealthCheck;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.knowm.dropwizard.sundial.SundialBundle;
import org.knowm.dropwizard.sundial.SundialConfiguration;
import org.knowm.sundial.SundialJobScheduler;
import org.quartz.core.Scheduler;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import java.util.EnumSet;
import java.util.Map;

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
        String url = config.getDatabase().getUrl();
        ConnectionPool pool = new ConnectionPool(
                url + "?characterEncoding=latin1",
                config.getDatabase().getUser(),
                config.getDatabase().getPassword(),
                10
        );

        Scheduler scheduler = SundialJobScheduler.createScheduler(1, "io.summercodingfun.covidtrend.jobs");
        SundialJobScheduler.addJob("GetData", DataJob.class, Map.of("pool", pool), false);
        SundialJobScheduler.addCronTrigger("midnight", "GetData", "0 0 0 * * ?");

        enableCorsHeaders(env);

        final CovidCaseResource caseResource = new CovidCaseResource(pool);
        final CovidRangeDataResource rangeResource = new CovidRangeDataResource(pool);
        final LatestCovidResource latestResource = new LatestCovidResource(pool);
        final CovidCasesTrendResource casesTrendResource = new CovidCasesTrendResource(pool, config.getHost(), config.getPort());
        final CovidCasesChangeResource changeResource = new CovidCasesChangeResource(pool, config.getHost(), config.getPort());
        final CovidComparisonChartResource comparisonResource= new CovidComparisonChartResource(pool, config.getHost(), config.getPort());
        final JobResource jobResource = new JobResource(pool);
        final TemplateHealthCheck healthCheck = new TemplateHealthCheck(config.getTemplate());

        env.healthChecks().register("template", healthCheck);
        env.jersey().register(caseResource);
        env.jersey().register(rangeResource);
        env.jersey().register(latestResource);
        env.jersey().register(casesTrendResource);
        env.jersey().register(changeResource);
        env.jersey().register(comparisonResource);
        env.jersey().register(jobResource);
    }

    private void enableCorsHeaders(Environment env) {
        final FilterRegistration.Dynamic cors = env.servlets().addFilter("CORS", CrossOriginFilter.class);

        cors.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
        cors.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM, "X-Requested-With,Content-Type,Accept,Origin");
        cors.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET,POST,DELETE");

        cors.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
    }
}
