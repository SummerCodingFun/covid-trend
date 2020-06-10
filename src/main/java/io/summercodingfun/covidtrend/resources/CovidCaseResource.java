package io.summercodingfun.covidtrend.resources;

import io.summercodingfun.covidtrend.api.Saying;
import com.codahale.metrics.annotation.Timed;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.SortedMap;

@Path("/covid-cases/{location}/{date}")
@Produces(MediaType.APPLICATION_JSON)

public class CovidCaseResource {
    private final SortedMap<String, Integer> cases;
    private final SortedMap<String, Integer> deaths;

    public CovidCaseResource(SortedMap<String, Integer> cases, SortedMap<String, Integer> deaths){
        this.cases = cases;
        this.deaths = deaths;
    }

    @GET
    @Timed
    public Saying displayStateData(@PathParam("location") String state, @PathParam("date") String date) {
        String key = Util.createKey(state, date);

        if (cases.containsKey(key) && deaths.containsKey(key)) {
            return new Saying(state, cases.get(key), deaths.get(key));
        } else {
            throw new WebApplicationException("state or date is invalid.", 400);
        }
    }

}