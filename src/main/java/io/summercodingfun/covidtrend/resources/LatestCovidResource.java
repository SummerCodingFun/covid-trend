package io.summercodingfun.covidtrend.resources;

import io.summercodingfun.covidtrend.api.Saying;
import com.codahale.metrics.annotation.Timed;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.SortedMap;

@Path("/covid-cases/{location}")
@Produces(MediaType.APPLICATION_JSON)

public class LatestCovidResource {
    private final SortedMap<String, Integer> cases;
    private final SortedMap<String, Integer> deaths;

    public LatestCovidResource(SortedMap<String, Integer> cases, SortedMap<String, Integer> deaths){
        this.cases = cases;
        this.deaths = deaths;
    }

    @GET
    @Timed
    public Saying displayStateData(@PathParam("location") String state) {
        DateTime today = new DateTime();
        today = today.plusDays(-1);
        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");
        return new Saying(state, cases.get(createKey(state, fmt.print(today))), deaths.get(createKey(state, fmt.print(today))));
    }

    public static String createKey(String x, String y){
        return x + ":" + y;
    }
}