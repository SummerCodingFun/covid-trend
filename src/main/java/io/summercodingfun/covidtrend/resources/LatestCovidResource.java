package io.summercodingfun.covidtrend.resources;

import io.summercodingfun.covidtrend.api.Saying;
import com.codahale.metrics.annotation.Timed;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.Connection;
import java.util.logging.Logger;

@Path("/covid-cases/{location}")
@Produces(MediaType.APPLICATION_JSON)

public class LatestCovidResource {
    private ConnectionPool pool;
    private static final Logger logger = Logger.getLogger("io.summercodingfun.covidtrend.resources.LatestCovidResource");


    public LatestCovidResource(ConnectionPool pool){
        this.pool = pool;
    }

    @GET
    @Timed
    public Saying displayStateData(@PathParam("location") String currentState) throws Exception {
        logger.info(String.format("starting latest covid with %s", currentState));
        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");
        Connection conn = null;

        DateTime currentDate = new DateTime();

        int stateCases = 0;
        int stateDeaths = 0;

        try {
            conn = pool.getConnection();
            if (!ConnectionUtil.isAvailable(conn, currentState)) {
                logger.severe("the state is invalid");
                throw new WebApplicationException("Please enter a valid state", 400);
            }
            currentDate = ConnectionUtil.getMaxDate(conn, currentState);
            stateCases = ConnectionUtil.getCases(conn, currentState, fmt.print(currentDate));
            stateDeaths = ConnectionUtil.getDeaths(conn, currentState, fmt.print(currentDate));
        } finally {
            if (conn != null) {
                pool.returnConnection(conn);
            }
        }

        return new Saying(currentState, stateCases, stateDeaths);
    }
}