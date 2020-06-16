package io.summercodingfun.covidtrend.resources;

import io.summercodingfun.covidtrend.api.Saying;
import com.codahale.metrics.annotation.Timed;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.Connection;


@Path("/covid-cases/{location}/{date}")
@Produces(MediaType.APPLICATION_JSON)

public class CovidCaseResource {
    private ConnectionPool pool;
    private static final Logger logger = LoggerFactory.getLogger("CovidCaseResource");

    public CovidCaseResource(ConnectionPool pool) {
        this.pool = pool;
    }

    @GET
    @Timed
    public Saying displayStateData(@PathParam("location") String state, @PathParam("date") String date) throws Exception {
        Connection conn = null;

        logger.info(String.format("starting covid case with %s", state));
        try {
            conn = pool.getConnection();
            if (!ConnectionUtil.isAvailable(conn, state, date)) {
                logger.error("the state or date is invalid");
                throw new WebApplicationException("state or date is invalid.", 400);
            }
        } finally {
            if (conn != null) {
                pool.returnConnection(conn);
            }
        }

        int stateCases = 0;
        int stateDeaths = 0;

        try {
            conn = pool.getConnection();
            stateCases = ConnectionUtil.getCases(conn, state, date);
            stateDeaths = ConnectionUtil.getDeaths(conn, state, date);
        } finally {
            if (conn != null) {
                pool.returnConnection(conn);
            }
        }

        return new Saying(state, stateCases, stateDeaths);
    }

}