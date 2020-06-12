package io.summercodingfun.covidtrend.resources;

import io.summercodingfun.covidtrend.api.Saying;
import com.codahale.metrics.annotation.Timed;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.Connection;

@Path("/covid-cases/{location}")
@Produces(MediaType.APPLICATION_JSON)

public class LatestCovidResource {

    public LatestCovidResource(){
    }

    @GET
    @Timed
    public Saying displayStateData(@PathParam("location") String currentState) throws Exception {
        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");
        ConnectionPool pool = new ConnectionPool("jdbc:mysql://localhost:3306/covid_data?characterEncoding=latin1", "root", "Ye11owstone", 10);
        Connection conn = null;

        DateTime currentDate = new DateTime();

        int stateCases = 0;
        int stateDeaths = 0;

        try {
            conn = pool.getConnection();
            if (!ConnectionUtil.isAvailable(conn, currentState)) {
                throw new WebApplicationException("Please enter a valid state", 400);
            }
            currentDate = ConnectionUtil.getMaxDate(conn, currentState);
            stateCases = ConnectionUtil.getCases(conn, currentState, fmt.print(currentDate));
            stateDeaths = ConnectionUtil.getDeaths(conn, currentState, fmt.print(currentDate));
        } catch (WebApplicationException e) {
            throw new WebApplicationException("Please enter a valid state", 400);
        } finally {
            if (conn != null) {
                pool.returnConnection(conn);
            }
        }

        return new Saying(currentState, stateCases, stateDeaths);
    }
}