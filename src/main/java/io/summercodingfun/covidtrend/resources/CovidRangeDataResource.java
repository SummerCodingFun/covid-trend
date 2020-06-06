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
    private final SortedMap<String, Integer> cases;
    private final SortedMap<String, Integer> deaths;

    public CovidRangeDataResource(SortedMap<String, Integer> cases, SortedMap<String, Integer> deaths) {
        this.cases = cases;
        this.deaths = deaths;
    }

    @GET
    @Timed
    public CovidRangeData displayRangeData(@PathParam("location") String state, @PathParam("startingDate") String startingDate, @PathParam("range") String range) {
        try {
            String key = createKey(state, startingDate);
            System.out.println(cases.size());
            int r = Integer.parseInt(range);
            int positiveRange = r < 0 ? r * -1 : r;
            if (cases.containsKey(key) && deaths.containsKey(key) && positiveRange < cases.size() / 50) {
                List<CasesAndDeathsByDate> information = new ArrayList<>();

                CasesAndDeathsByDate yourData = new CasesAndDeathsByDate(startingDate, cases.get(key), deaths.get(key));
                information.add(yourData);

                DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");
                long millis = fmt.parseMillis(startingDate);

                int multiplier = r < 0 ? -1 : 1;
                DateTime startingDateTime = new DateTime(millis);

                for (int i = 0; i < r * multiplier; i++) {
                    startingDateTime = startingDateTime.plusDays(1 * multiplier);
                    String key1 = createKey(state, fmt.print(startingDateTime));
                    CasesAndDeathsByDate newData = new CasesAndDeathsByDate(fmt.print(startingDateTime), cases.get(key1), deaths.get(key1));
                    information.add(newData);
                }
                return new CovidRangeData(state, information);
            } else {
                throw new WebApplicationException("state, starting date, or range is invalid", 400);
            }
        } catch (Exception e) {
            throw new WebApplicationException("range must be a number", 400);
        }
    }

    public static String createKey(String x, String y){
        return x + ":" + y;
    }
}
