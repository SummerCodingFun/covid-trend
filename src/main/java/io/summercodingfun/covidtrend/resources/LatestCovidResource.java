package io.summercodingfun.covidtrend.resources;

import io.summercodingfun.covidtrend.api.Saying;
import com.codahale.metrics.annotation.Timed;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.*;
import java.util.SortedMap;

@Path("/covid-cases/{location}")
@Produces(MediaType.APPLICATION_JSON)

public class LatestCovidResource {
    private final SortedMap<String, Integer> cases;
    private final SortedMap<String, Integer> deaths;
    private final SortedMap<String, MinAndMaxDateByState> minAndMax;

    public LatestCovidResource(SortedMap<String, Integer> cases, SortedMap<String, Integer> deaths, SortedMap<String, MinAndMaxDateByState> minAndMax){
        this.cases = cases;
        this.deaths = deaths;
        this.minAndMax = minAndMax;
    }

    @GET
    @Timed
    public Saying displayStateData(@PathParam("location") String currentState) throws Exception {
        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");
        if (!minAndMax.containsKey(currentState)) {
            throw new WebApplicationException("Please enter a valid state", 400);
        }
        String currentDate = fmt.print(minAndMax.get(currentState).getMaxDate());
        String key = Util.createKey(currentState, currentDate);
        ResultSet resultSet = null;
        Statement statement = null;
        Connection connection = null;
        String host = "jdbc:mysql://localhost:3306/covid_data?characterEncoding=latin1";
        String user = "root";
        String password = "Ye11owstone";
        int stateCases = 0;
        int stateDeaths = 0;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(host, user, password);
            statement = connection.createStatement();
            String stmt = String.format("select cases, deaths from usStates where state = '%s' AND date = '%s'", currentState, currentDate);
            resultSet = statement.executeQuery(stmt);
            while (resultSet.next()){
                stateCases = resultSet.getInt("cases");
                stateDeaths = resultSet.getInt("deaths");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return new Saying(currentState, stateCases, stateDeaths);
    }


}