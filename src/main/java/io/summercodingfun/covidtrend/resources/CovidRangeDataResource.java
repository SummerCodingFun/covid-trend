package io.summercodingfun.covidtrend.resources;

import com.codahale.metrics.annotation.Timed;
import io.summercodingfun.covidtrend.api.CovidRangeData;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import java.text.ParseException;
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
    public CovidRangeData displayRangeData(@PathParam("location") String state, @PathParam("startingDate") String startingDate, @PathParam("range") int range) throws ParseException {
        List<CasesByDate> information = new ArrayList<>();

        CasesByDate yourData = new CasesByDate(startingDate, cases.get(createKey(state, startingDate)));
        information.add(yourData);

        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");
        long millis  = fmt.parseMillis(startingDate);

        int multiplier = range < 0 ? -1 : 1;
        DateTime startingDateTime = new DateTime(millis);

        for (int i = 0; i < range*multiplier; i++){
            startingDateTime = startingDateTime.plusDays(1*multiplier);
            CasesByDate newData = new CasesByDate(fmt.print(startingDateTime), cases.get(createKey(state, fmt.print(startingDateTime))));
            information.add(newData);
        }
        return new CovidRangeData(state, information);
    }

    public static String createKey(String x, String y){
        return x + ":" + y;
    }
}
