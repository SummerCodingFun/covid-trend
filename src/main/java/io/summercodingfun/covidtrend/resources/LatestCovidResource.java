package io.summercodingfun.covidtrend.resources;

import io.summercodingfun.covidtrend.api.Saying;
import com.codahale.metrics.annotation.Timed;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.SortedMap;

@Path("/covid-cases/{location}")
@Produces(MediaType.APPLICATION_JSON)

public class LatestCovidResource {
    private final SortedMap<String, MinAndMaxDateByState> minAndMax;

    public LatestCovidResource(SortedMap<String, MinAndMaxDateByState> minAndMax){
        this.minAndMax = minAndMax;
    }

    @GET
    @Timed
    public Saying displayStateData(@PathParam("location") String currentState) throws Exception {
        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");
        if (!minAndMax.containsKey(currentState)) {
            throw new WebApplicationException("Please enter a valid state", 400);
        }
        String currentDate = fmt.print(minAndMax.get(currentState).getMaxDate());
        ConnectionUtil c = new ConnectionUtil();
        int stateCases = c.getCases(currentState, currentDate);
        int stateDeaths = c.getDeaths(currentState, currentDate);
        c.close();

        return new Saying(currentState, stateCases, stateDeaths);
    }
}