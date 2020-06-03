package io.summercodingfun.covidtrend.resources;

import io.summercodingfun.covidtrend.api.Saying;
import com.codahale.metrics.annotation.Timed;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.Optional;
import java.util.*;

import io.summercodingfun.covidtrend.CovidConfig;

@Path("/covid-cases")
@Produces(MediaType.APPLICATION_JSON)

public class CovidResource {
    private final String template;
    private final String defaultLocation;
    private final String infoCases;
    private final String infoDeaths;
    private final SortedMap<String, Integer> cases;
    private final SortedMap<String, Integer> deaths;
    private final String defaultInfo;

    public CovidResource(CovidConfig config, SortedMap<String, Integer> cases, SortedMap<String, Integer> deaths){
        this.template = config.getTemplate();
        this.defaultLocation = config.getDefaultLocation();
        this.infoCases = config.getInfoCases();
        this.infoDeaths = config.getInfoDeaths();
        this.cases = cases;
        this.deaths = deaths;
        this.defaultInfo = " ";
    }

    @GET
    @Timed
    public Saying displayData(@QueryParam("state") Optional<String> state) {
        final String value = String.format(template, state.orElse(defaultLocation));

        for (String s : cases.keySet()) {
            System.out.println(String.format("%s - %s", s, cases.get(s)));
        }
        
        final String data = String.format(infoCases, cases.get(state.orElse(defaultInfo)));
        final String dd = String.format(infoDeaths, deaths.get(state.orElse(defaultInfo)));
        return new Saying(value, data, dd);
    }

    public String createKey(String x, String y){
        return x + ":" + y;
    }
}