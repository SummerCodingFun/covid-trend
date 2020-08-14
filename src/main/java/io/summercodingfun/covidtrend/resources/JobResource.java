package io.summercodingfun.covidtrend.resources;

import com.codahale.metrics.annotation.Timed;
import io.summercodingfun.covidtrend.api.DataLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/covid-app/get-data")
@Produces(MediaType.APPLICATION_JSON)
public class JobResource {
    private static final Logger logger = LoggerFactory.getLogger(JobResource.class);
    private ConnectionPool pool;

    public JobResource(ConnectionPool pool) {
        this.pool = pool;
    }

    @GET
    @Timed
    public Response getData() {
        DataLoader dataLoader = new DataLoader(pool);
        dataLoader.getData();
        return Response.ok().build();
    }
}
