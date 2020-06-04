package io.summercodingfun.covidtrend.resources;

import io.summercodingfun.covidtrend.api.Saying;
import com.codahale.metrics.annotation.Timed;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import java.util.SortedMap;

import io.summercodingfun.covidtrend.CovidConfig;

@Path("/covid-cases/{location}")
@Produces(MediaType.APPLICATION_JSON)

public class CovidResource {
    private final String template;
    private final SortedMap<String, Integer> cases;
    private final SortedMap<String, Integer> deaths;

    public CovidResource(CovidConfig config, SortedMap<String, Integer> cases, SortedMap<String, Integer> deaths){
        this.template = config.getTemplate();
        this.cases = cases;
        this.deaths = deaths;
    }

    @GET
    @Timed
    public Saying displayStateData(@PathParam("location") String state) {
        return new Saying(state, cases.get(state), deaths.get(state));
    }
}