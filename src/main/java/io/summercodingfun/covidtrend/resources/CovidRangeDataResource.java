package io.summercodingfun.covidtrend.resources;

import com.codahale.metrics.annotation.Timed;
import io.summercodingfun.covidtrend.api.CovidRangeData;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

@Path("/covid-app")
@Produces(MediaType.APPLICATION_JSON)

public class CovidRangeDataResource {
    private ConnectionPool pool;
    private static final Logger logger = LoggerFactory.getLogger(CovidRangeDataResource.class);

    public CovidRangeDataResource(ConnectionPool pool){
        this.pool = pool;
    }

    @GET
    @Timed
    @Path("/range-data")
    public CovidRangeData displayRangeData(@QueryParam("location") String state, @QueryParam("startingDate") String startingDate, @QueryParam("range") String range) throws Exception {
        logger.info("starting covid range data with {}", state);
        int r;
        Connection conn = null;
        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");
        long millis = fmt.parseMillis(startingDate);
        DateTime theRange = new DateTime(millis);
        List<CasesAndDeathsByDate> information = new ArrayList<>();

        try {
            r = Integer.parseInt(range);
            theRange = theRange.plusDays(r);
            conn = pool.getConnection();
            if (!ConnectionUtil.isAvailable(conn, state, startingDate)) {
                logger.error("the state, starting date or range is invalid");
                throw new WebApplicationException("state, starting date, or range is invalid", 400);
            }
            if (!ConnectionUtil.isAvailable(conn, state, fmt.print(theRange))) {
                logger.error("the state or range was invalid");
                throw new WebApplicationException("state or range is invalid", 400);
            }
        } catch (NumberFormatException e) {
            logger.error("the range entered was not a number");
            throw new WebApplicationException("range must be a number", 400);
        } finally {
            if (conn != null) {
                pool.returnConnection(conn);
            }
        }

        int multiplier = r < 0 ? -1 : 1;
        try {
            conn = pool.getConnection();
            DateTime startingDateTime = new DateTime(millis);
            CasesAndDeathsByDate yourData = new CasesAndDeathsByDate(startingDateTime, ConnectionUtil.getCases(conn, state, fmt.print(startingDateTime)), ConnectionUtil.getDeaths(conn, state, fmt.print(startingDateTime)));
            information.add(yourData);

            for (int i = 0; i < r * multiplier; i++) {
                startingDateTime = startingDateTime.plusDays(1 * multiplier);
                int stateCases = ConnectionUtil.getCases(conn, state, fmt.print(startingDateTime));
                int stateDeaths = ConnectionUtil.getDeaths(conn, state, fmt.print(startingDateTime));
                CasesAndDeathsByDate newData = new CasesAndDeathsByDate(startingDateTime, stateCases, stateDeaths);
                information.add(newData);
            }
        } finally {
            if (conn != null) {
                pool.returnConnection(conn);
            }
        }

        return new CovidRangeData(state, information);
    }
}
