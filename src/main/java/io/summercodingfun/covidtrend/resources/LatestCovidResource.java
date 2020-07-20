package io.summercodingfun.covidtrend.resources;

import io.summercodingfun.covidtrend.api.ListSaying;
import io.summercodingfun.covidtrend.api.Saying;
import com.codahale.metrics.annotation.Timed;
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

public class LatestCovidResource {
    private static final Logger logger = LoggerFactory.getLogger(LatestCovidResource.class);
    private ConnectionPool pool;

    public LatestCovidResource(ConnectionPool pool){
        this.pool = pool;
    }

    @GET
    @Timed
    @Path("/latest-case/{state}")
    public ListSaying displayStateData(@PathParam("state") String currentState) throws Exception {
        logger.info("starting latest covid with {}", currentState);
        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");
        Connection conn = null;

        DateTime currentDate = new DateTime();

        int stateCases = 0;
        int stateDeaths = 0;

        try {
            conn = pool.getConnection();
            if (!ConnectionUtil.isAvailable(conn, currentState)) {
                logger.error("the state is invalid");
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

        List<Saying> list = new ArrayList<>();
        list.add(new Saying(String.format("%s, %s", currentState, fmt.print(currentDate)), stateCases, stateDeaths));
        return new ListSaying(list);
    }

    @GET
    @Timed
    @Path("/all-latest-cases")
    public ListSaying getAllCases(@QueryParam("state") String currentState) throws Exception {
        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");
        Connection conn = null;

        DateTime currentDate = new DateTime();
        List<Saying> list = new ArrayList<>();

        int stateCases = 0;
        int stateDeaths = 0;

        try {
            conn = pool.getConnection();
            if (!currentState.isEmpty() && ConnectionUtil.isAvailable(conn, currentState)) {
                currentDate = ConnectionUtil.getMaxDate(conn, currentState);
                stateCases = ConnectionUtil.getCases(conn, currentState, fmt.print(currentDate));
                stateDeaths = ConnectionUtil.getDeaths(conn, currentState, fmt.print(currentDate));
                list.add(new Saying(String.format("%s, %s", currentState, fmt.print(currentDate)), stateCases, stateDeaths));
            } else {
                String ss = "Alabama";
                if (!ConnectionUtil.isAvailable(conn, ss)) {
                    logger.error("the state is invalid");
                    throw new WebApplicationException("Please enter a valid state", 400);
                }
                list = ConnectionUtil.getAllData(conn);
            }
        } finally {
            if (conn != null) {
                pool.returnConnection(conn);
            }
        }

        return new ListSaying(list);
    }
}