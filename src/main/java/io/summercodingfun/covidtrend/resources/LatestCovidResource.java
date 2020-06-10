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
    private final SortedMap<String, Integer> cases;
    private final SortedMap<String, Integer> deaths;
    private final SortedMap<String, MinAndMaxDateByState> minAndMax;

    public LatestCovidResource(SortedMap<String, Integer> cases, SortedMap<String, Integer> deaths, SortedMap<String, MinAndMaxDateByState> minAndMax){
        this.cases = cases;
        this.deaths = deaths;
        this.minAndMax = minAndMax;
    }

    @GET
    @Timed
    public Saying displayStateData(@PathParam("location") String state) {
        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");
        if (!minAndMax.containsKey(state)) {
            throw new WebApplicationException("Please enter a valid state", 400);
        }
        String date = fmt.print(minAndMax.get(state).getMaxDate());
        String key = Util.createKey(state, date);
        return new Saying(state, cases.get(key), deaths.get(key));
    }


}