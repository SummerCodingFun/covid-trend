package io.summercodingfun.covidtrend.resources;

import io.summercodingfun.covidtrend.api.Saying;
import com.codahale.metrics.annotation.Timed;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.SortedMap;

@Path("/covid-cases/{location}/{date}")
@Produces(MediaType.APPLICATION_JSON)

public class CovidResource {
    private final SortedMap<String, Integer> cases;
    private final SortedMap<String, Integer> deaths;

    public CovidResource(SortedMap<String, Integer> cases, SortedMap<String, Integer> deaths){
        this.cases = cases;
        this.deaths = deaths;
    }

    @GET
    @Timed
    public Saying displayStateData(@PathParam("location") String state, @PathParam("date") String date) {
        return new Saying(state, cases.get(createKey(state, date)), deaths.get(createKey(state, date)));
    }

    public static String createKey(String x, String y){
        return x + ":" + y;
    }
}