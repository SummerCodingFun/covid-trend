package io.summercodingfun.covidtrend.resources;

import com.codahale.metrics.annotation.Timed;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import org.jfree.chart.ChartUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import javax.ws.rs.*;
import javax.ws.rs.core.StreamingOutput;
import java.io.*;
import java.util.ArrayList;
import java.util.SortedMap;
import java.util.List;

@Path("/covid-cases-trend/{location}")
@Produces("image/png")

public class CovidCasesTrendResource {
    private final SortedMap<String, Integer> cases;
    private final DateTime maxDate;

    public CovidCasesTrendResource(SortedMap<String, Integer> cases, DateTime md) {
        this.cases = cases;
        this.maxDate = md;
    }

    @GET
    @Timed
    public StreamingOutput displayTrend(@PathParam("location") String state) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        JFreeChart chart = getChart(state);
        ChartUtils.writeChartAsPNG(outputStream, chart, 500, 350);
        StreamingOutput streamingOutput = new StreamingOutput() {
            @Override
            public void write(OutputStream output) throws IOException, WebApplicationException {
                output.write(outputStream.toByteArray());
            }
        };
        return streamingOutput;
    }

    public JFreeChart getChart(String state){
        List<Integer> list = new ArrayList<>();
        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");
        DateTime date2 = new DateTime(maxDate);
        String key2 = createKey(state, fmt.print(maxDate));

        while (cases.get(key2) != null){
            list.add(cases.get(key2));
            date2 = date2.minusDays(1);
            key2 = createKey(state, fmt.print(date2));
        }
        var series = new XYSeries("Cases");
        DateTime date = new DateTime(maxDate);
        DateTimeFormatter fmt2 = DateTimeFormat.forPattern("yyyyMMdd");
        String key = createKey(state, fmt.print(date));

        int i = list.size();
        while(cases.get(key) != null) {
            series.add(Double.valueOf(i), Double.valueOf(cases.get(key)));
            date = date.minusDays(1);
            key = createKey(state, fmt.print(date));
            i--;
        }

        var dataset = new XYSeriesCollection();
        dataset.addSeries(series);

        JFreeChart chart = ChartFactory.createXYLineChart(
                String.format("%s Cases Trend from Jan 1 to June 7", state),
                "Date",
                "Number of Cases",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false);

        return chart;
    }

    public static String createKey(String x, String y){
        return x + ":" + y;
    }
}
