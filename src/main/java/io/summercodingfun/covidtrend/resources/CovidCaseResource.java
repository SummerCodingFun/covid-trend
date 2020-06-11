package io.summercodingfun.covidtrend.resources;

import io.summercodingfun.covidtrend.api.Saying;
import com.codahale.metrics.annotation.Timed;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.SortedMap;

@Path("/covid-cases/{location}/{date}")
@Produces(MediaType.APPLICATION_JSON)

public class CovidCaseResource {
    private final SortedMap<String, MinAndMaxDateByState> minAndMax;

    public CovidCaseResource(SortedMap<String, MinAndMaxDateByState> minAndMax){
        this.minAndMax = minAndMax;
    }

    @GET
    @Timed
    public Saying displayStateData(@PathParam("location") String state, @PathParam("date") String date) throws Exception {
        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");
        long millis = fmt.parseMillis(date);
        DateTime startingDateTime = new DateTime(millis);
        if (minAndMax.containsKey(state) && startingDateTime.isAfter(minAndMax.get(state).getMinDate()) && startingDateTime.isBefore(minAndMax.get(state).getMaxDate())) {
            ConnectionUtil c = new ConnectionUtil();
            int stateCases = c.getCases(state, date);
            int stateDeaths = c.getDeaths(state, date);
            c.close();
            return new Saying(state, stateCases, stateDeaths);
        } else {
            throw new WebApplicationException("state or date is invalid.", 400);
        }
    }

}