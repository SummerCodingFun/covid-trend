package io.summercodingfun.covidtrend.resources;

import com.codahale.metrics.annotation.Timed;
import io.summercodingfun.covidtrend.chart.Chart;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.ws.rs.*;
import javax.ws.rs.core.StreamingOutput;
import java.io.*;
import java.util.SortedMap;

@Path("/covid-cases-trend/{location}")
@Produces("image/png")

public class CovidCasesTrendResource {
    private final SortedMap<String, MinAndMaxDateByState> minAndMaxDate;

    public CovidCasesTrendResource(SortedMap<String, MinAndMaxDateByState> md) throws Exception {
        this.minAndMaxDate = md;
    }

    @GET
    @Timed
    public StreamingOutput displayTrend(@PathParam("location") String state) throws Exception {
        var series = new XYSeries("Cases");
        DateTime minDate = minAndMaxDate.get(state).getMinDate();
        DateTime maxDate = minAndMaxDate.get(state).getMaxDate();
        DateTime date = new DateTime(minDate);
        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");
        DateTimeFormatter fmt2 = DateTimeFormat.forPattern("MM-dd-yyyy");
        ConnectionUtil c = new ConnectionUtil();

        int i = 0;
        while(date.isBefore(maxDate)) {
            series.add(Double.valueOf(i), Double.valueOf(c.getCases(state,fmt.print(date))));
            date = date.plusDays(1);
            i++;
        }
        c.close();

        var dataset = new XYSeriesCollection();
        dataset.addSeries(series);

        JFreeChart chart = Chart.createXYLineChart(
                String.format("%s Cases Trend from %s to %s", state, fmt2.print(minDate), fmt2.print(maxDate)),
                "Days",
                "Number of Cases",
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

}
