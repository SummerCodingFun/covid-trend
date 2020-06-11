package io.summercodingfun.covidtrend.resources;

import com.codahale.metrics.annotation.Timed;
import io.summercodingfun.covidtrend.api.CovidRangeData;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.SortedMap;
import java.util.List;

@Path("/covid-range-data/{location}/{startingDate}/{range}")
@Produces(MediaType.APPLICATION_JSON)

public class CovidRangeDataResource {
    private final SortedMap<String, MinAndMaxDateByState> minAndMax;

    public CovidRangeDataResource(SortedMap<String, MinAndMaxDateByState> minAndMax) {
        this.minAndMax = minAndMax;
    }

    @GET
    @Timed
    public CovidRangeData displayRangeData(@PathParam("location") String state, @PathParam("startingDate") String startingDate, @PathParam("range") String range) throws Exception {
        int r;
        try {
            r = Integer.parseInt(range);
        } catch (NumberFormatException e) {
            throw new WebApplicationException("range must be a number", 400);
        }

        if (!minAndMax.containsKey(state)) {
            throw new WebApplicationException("Please enter a valid state", 400);
        }
        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");
        long millis = fmt.parseMillis(startingDate);
        DateTime theRange = new DateTime(millis);
        theRange = theRange.plusDays(r);
        if (minAndMax.containsKey(state) && theRange.isBefore(minAndMax.get(state).getMaxDate()) && theRange.isAfter(minAndMax.get(state).getMinDate())) {
            List<CasesAndDeathsByDate> information = new ArrayList<>();
            ConnectionUtil c = new ConnectionUtil();

            CasesAndDeathsByDate yourData = new CasesAndDeathsByDate(startingDate, c.getCases(state, startingDate), c.getDeaths(state, startingDate));
            information.add(yourData);

            int multiplier = r < 0 ? -1 : 1;

            DateTime startingDateTime = new DateTime(millis);

            for (int i = 0; i < r * multiplier; i++) {
                startingDateTime = startingDateTime.plusDays(1 * multiplier);
                int stateCases = c.getCases(state, fmt.print(startingDateTime));
                int stateDeaths = c.getDeaths(state, fmt.print(startingDateTime));
                CasesAndDeathsByDate newData = new CasesAndDeathsByDate(fmt.print(startingDateTime), stateCases, stateDeaths);
                information.add(newData);
            }
            c.close();
            return new CovidRangeData(state, information);
        } else {
            throw new WebApplicationException("state, starting date, or range is invalid", 400);
        }
    }

}
