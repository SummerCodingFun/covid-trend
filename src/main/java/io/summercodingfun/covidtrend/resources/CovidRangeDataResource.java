package io.summercodingfun.covidtrend.resources;

import com.codahale.metrics.annotation.Timed;
import io.summercodingfun.covidtrend.api.CovidRangeData;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

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
    public CovidRangeData displayRangeData(@PathParam("location") String state, @PathParam("startingDate") String startingDate, @PathParam("range") int range){
        List<CasesByDate> information = new ArrayList<>();

        CasesByDate yourData = new CasesByDate(startingDate, cases.get(createKey(state, startingDate)));
        information.add(yourData);

        DateTimeFormatter date = DateTimeFormat.forPattern("yyyy-MM-dd");
        long millis  = date.parseMillis(startingDate);
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(millis);

        int multiplier = range < 0 ? -1 : 1;

        for (int i = 0; i < range*multiplier; i ++) {
            cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) + (1*multiplier));
            Date dd = cal.getTime();
            DateFormat par = new SimpleDateFormat("yyyy-MM-dd");
            String fullKey = createKey(state, par.format(dd));
            CasesByDate newData = new CasesByDate(par.format(dd), cases.get(fullKey));
            information.add(newData);
        }

        return new CovidRangeData(state, information);
    }

    public static String createKey(String x, String y){
        return x + ":" + y;
    }
}
