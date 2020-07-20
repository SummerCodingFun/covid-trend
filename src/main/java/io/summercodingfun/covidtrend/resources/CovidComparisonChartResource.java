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
import java.util.List;

@Path("/covid-app")
@Produces("image/png")

public class CovidComparisonChartResource {
    private ConnectionPool pool;
    private static final Logger logger = LoggerFactory.getLogger(CovidCasesChangeResource.class);

    public CovidComparisonChartResource(ConnectionPool pool) {
        this.pool = pool;
    }

    @GET
    @Timed
    @Path("/covid-comparison/{state1}/{state2}/{state3}/{state4}/{state5}")
    public StreamingOutput displayTrend(@PathParam("state1") String state1,
                                        @PathParam("state2") String state2,
                                        @PathParam("state3") String state3,
                                        @PathParam("state4") String state4,
                                        @PathParam("state5") String state5) throws Exception {

        logger.info("starting covid comparison chart with {}, {}, {}, {}, {}", state1, state2, state3, state4, state5);

        DateTimeFormatter fmt2 = DateTimeFormat.forPattern("MM-dd-yyyy");
        DateTime minDate = getMinDate(state1, state2, state3, state4, state5);
        DateTime maxDate = getMaxDate(state1, state2, state3, state4, state5);

        var series1 = getSeries(state1, minDate);
        var series2 = getSeries(state2, minDate);
        var series3 = getSeries(state3, minDate);
        var series4 = getSeries(state4, minDate);
        var series5 = getSeries(state5, minDate);


        logger.info("{} has {} data points", state1, series1.getItemCount());
        logger.info("{} has {} data points", state2, series2.getItemCount());
        logger.info("{} has {} data points", state3, series3.getItemCount());
        logger.info("{} has {} data points", state4, series4.getItemCount());
        logger.info("{} has {} data points", state5, series5.getItemCount());
        var dataset = new XYSeriesCollection();
        dataset.addSeries(series1);
        dataset.addSeries(series2);
        dataset.addSeries(series3);
        dataset.addSeries(series4);
        dataset.addSeries(series5);

        JFreeChart chart = Chart.createXYLineChart(
                String.format("Change in Cases from %s to %s", fmt2.print(minDate), fmt2.print(maxDate)),
                "Days",
                "Number of New Cases",
                dataset
        );

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ChartUtils.writeChartAsPNG(outputStream, chart, 700, 467);
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
    public URLMessage getComparisonURL(@QueryParam("state1") String state1,
                                       @QueryParam("state2") String state2,
                                       @QueryParam("state3") String state3,
                                       @QueryParam("state4") String state4,
                                       @QueryParam("state5") String state5) throws Exception {

        URL url = new URL(String.format("http://localhost:8080/covid-app/covid-comparison/%s/%s/%s/%s/%s", state1, state2, state3, state4, state5));
        URLList u = new URLList(url);
        List<URLList> list = new ArrayList<>();
        list.add(u);
        return new URLMessage(list);
    }

    private XYSeries getSeries(String state, DateTime minDate) throws SQLException, ParseException {
        var series = new XYSeries(String.format("Change in Cases for %s", state));

        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");
        Connection conn = null;

        try {
            conn = pool.getConnection();
//            DateTime minDate = ConnectionUtil.getMinDate(conn, state);
            DateTime maxDate = ConnectionUtil.getMaxDate(conn, state);
            DateTime date11 = new DateTime(minDate);
            DateTime date12 = new DateTime(date11.plusDays(1));
            int i = 0;
            while(date12.isBefore(maxDate)) {
                series.add(Double.valueOf(i), Double.valueOf(ConnectionUtil.getCases(conn, state, fmt.print(date12)) - ConnectionUtil.getCases(conn, state, fmt.print(date11))));
                date11 = date11.plusDays(1);
                date12 = date12.plusDays(1);
                i++;
            }
        } finally {
            if (conn != null) {
                pool.returnConnection(conn);
            }
        }
        return series;
    }

    private DateTime getMinDate(String state1, String state2, String state3, String state4, String state5) throws SQLException {
        DateTime minDate = new DateTime();
        Connection conn = null;
        try {
            conn = pool.getConnection();
            DateTime minDate1 = ConnectionUtil.getMinDate(conn, state1);
            DateTime minDate2 = ConnectionUtil.getMinDate(conn, state2);
            DateTime minDate3 = ConnectionUtil.getMinDate(conn, state3);
            DateTime minDate4 = ConnectionUtil.getMinDate(conn, state4);
            DateTime minDate5 = ConnectionUtil.getMinDate(conn, state5);
            if (minDate.isAfter(minDate1)) {
                minDate = minDate1;
            }
            if (minDate.isBefore(minDate2)) {
                minDate = minDate2;
            }
            if (minDate.isBefore(minDate3)) {
                minDate = minDate3;
            }
            if (minDate.isBefore(minDate4)) {
                minDate = minDate4;
            }
            if (minDate.isBefore(minDate5)) {
                minDate = minDate5;
            }
        } finally {
            if (conn != null) {
                pool.returnConnection(conn);
            }
        }
        return minDate  ;
    }

    private DateTime getMaxDate(String state1, String state2, String state3, String state4, String state5) throws SQLException {
        DateTime maxDate = new DateTime(Long.MIN_VALUE);
        Connection conn = null;
        try {
            conn = pool.getConnection();
            DateTime maxDate1 = ConnectionUtil.getMaxDate(conn, state1);
            DateTime minDate2 = ConnectionUtil.getMaxDate(conn, state2);
            DateTime maxDate3 = ConnectionUtil.getMaxDate(conn, state3);
            DateTime maxDate4 = ConnectionUtil.getMaxDate(conn, state4);
            DateTime maxDate5 = ConnectionUtil.getMaxDate(conn, state5);
            if (maxDate.isBefore(maxDate1)) {
                maxDate = maxDate1;
            }
            if (maxDate.isBefore(minDate2)) {
                maxDate = minDate2;
            }
            if (maxDate.isBefore(maxDate3)) {
                maxDate = maxDate3;
            }
            if (maxDate.isBefore(maxDate4)) {
                maxDate = maxDate4;
            }
            if (maxDate.isBefore(maxDate5)) {
                maxDate = maxDate5;
            }
        } finally {
            if (conn != null) {
                pool.returnConnection(conn);
            }
        }
        return maxDate;
    }
}
