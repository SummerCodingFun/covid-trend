package io.summercodingfun.covidtrend.resources;

import com.codahale.metrics.annotation.Timed;
import io.summercodingfun.covidtrend.api.URLList;
import io.summercodingfun.covidtrend.api.URLMessage;
import io.summercodingfun.covidtrend.chart.Chart;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Path("/covid-app")

public class CovidComparisonChartResource {
    private ConnectionPool pool;
    private static final Logger logger = LoggerFactory.getLogger(CovidCasesChangeResource.class);

    public CovidComparisonChartResource(ConnectionPool pool) {
        this.pool = pool;
    }

    @GET
    @Timed
    @Produces("image/png")
    @Path("/covid-comparison")
    public StreamingOutput displayTrend(@QueryParam("state") List<String> states) throws Exception {

        String s = String.format("starting covid comparison chart with %s", states.get(0));
        for (int i = 1; i < states.size(); i++) {
            s += String.format(", %s", states.get(i));
        }
        logger.info(s);

        DateTimeFormatter fmt2 = DateTimeFormat.forPattern("MM-dd-yyyy");
        DateTime minDate = getMinDate(states);
        DateTime maxDate = getMaxDate(states);

        var dataset = new XYSeriesCollection();

        for (int i = 0; i < states.size(); i++) {
            var series = getSeries(states.get(i), minDate);
            logger.info("{} has {} data points", states.get(i), series.getItemCount());
            dataset.addSeries(series);
        }

        JFreeChart chart = Chart.createXYLineChart(
                String.format("Cases from %s to %s", fmt2.print(minDate), fmt2.print(maxDate)),
                "Days",
                "Number of New Cases",
                dataset
        );

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ChartUtils.writeChartAsPNG(outputStream, chart, 1400, 934);
        StreamingOutput streamingOutput = new StreamingOutput() {
            @Override
            public void write(OutputStream output) throws IOException, WebApplicationException {
                output.write(outputStream.toByteArray());
            }
        };
        return streamingOutput;
    }

    @GET
    @Timed
    @Path("/url-comparison")
    @Produces(MediaType.APPLICATION_JSON)
    public URLMessage getComparisonURL(@QueryParam("state") String states) throws Exception {

        String[] s = states.split(",");
        String l = String.format("http://localhost:8080/covid-app/covid-comparison?state=%s", s[0].trim());
        for (int i = 1; i < s.length; i++) {
            l += String.format("&state=%s", s[i].trim());
        }

        URL url = new URL(l);
        URLList u = new URLList(url);
        List<URLList> list = new ArrayList<>();
        list.add(u);
        return new URLMessage(list);
    }

    private XYSeries getSeries(String state, DateTime minDate) throws SQLException, ParseException {
        var series = new XYSeries(String.format("Cases for %s", state));

        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");
        Connection conn = null;

        try {
            conn = pool.getConnection();
            DateTime currentMinDate = ConnectionUtil.getMinDate(conn, state);
            DateTime maxDate = ConnectionUtil.getMaxDate(conn, state);
            DateTime date11 = new DateTime(currentMinDate);
            int i = 0;
            if (currentMinDate.isAfter(minDate)) {
                Date d1 = currentMinDate.toDate();
                Date d2 = minDate.toDate();
                long j = d1.getTime() - d2.getTime();
                i = (int) (j/(24*60*60*1000));
            }

            while(date11.isBefore(maxDate)) {
                series.add(Double.valueOf(i), Double.valueOf( ConnectionUtil.getCases(conn, state, fmt.print(date11))));
                date11 = date11.plusDays(1);
                i++;
            }
        } finally {
            if (conn != null) {
                pool.returnConnection(conn);
            }
        }
        return series;
    }

    public DateTime getMinDate(List<String> states) throws SQLException {
        DateTime minDate = new DateTime();
        Connection conn = null;
        try {
            conn = pool.getConnection();
            for (int i = 0; i < states.size(); i++) {
                DateTime tempMinDate = ConnectionUtil.getMinDate(conn, states.get(i));
                if (minDate.isAfter(tempMinDate)) {
                    minDate = tempMinDate;
                }
            }
        } finally {
            if (conn != null) {
                pool.returnConnection(conn);
            }
        }
        return minDate;
    }

    private DateTime getMaxDate(List<String> states) throws SQLException {
        DateTime maxDate = new DateTime(Long.MIN_VALUE);
        Connection conn = null;
        try {
            conn = pool.getConnection();
            for (int i = 0; i < states.size(); i++) {
                DateTime tempMaxDate = ConnectionUtil.getMaxDate(conn, states.get(i));
                if (maxDate.isBefore(tempMaxDate)) {
                    maxDate = tempMaxDate;
                }
            }
        } finally {
            if (conn != null) {
                pool.returnConnection(conn);
            }
        }
        return maxDate;
    }
}
