package io.summercodingfun.covidtrend.resources;

import io.summercodingfun.covidtrend.api.ListSaying;
import io.summercodingfun.covidtrend.api.Saying;
import com.codahale.metrics.annotation.Timed;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;


@Path("/covid-app")
@Produces(MediaType.APPLICATION_JSON)

public class CovidCaseResource {
    private ConnectionPool pool;
    private static final Logger logger = LoggerFactory.getLogger(CovidCaseResource.class);

    public CovidCaseResource(ConnectionPool pool) {
        this.pool = pool;
    }

    @GET
    @Timed
    @Path("/cases")
    public ListSaying displayStateData(@QueryParam("location") String state, @QueryParam("date") String date) throws Exception {
        Connection conn = null;
        int stateCases = 0;
        int stateDeaths = 0;
        List<Saying> list = new ArrayList<>();

        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");

        try {
            conn = pool.getConnection();
            if (!state.isEmpty() && !date.isEmpty()) {
                logger.info("starting covid case with {}", state);
                if (ConnectionUtil.isAvailable(conn, state, date)) {
                    stateCases = ConnectionUtil.getCases(conn, state, date);
                    stateDeaths = ConnectionUtil.getDeaths(conn, state, date);
                    list.add(new Saying(state, stateCases, stateDeaths));
                } else {
                    logger.error("the state or date is invalid");
                    throw new WebApplicationException("state or date is invalid.", 400);
                }
            } else {
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