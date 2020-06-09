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
import java.util.SortedMap;

@Path("/covid-cases-change/{location}")
@Produces("image/png")

public class CovidCasesChangeResource {
    private final SortedMap<String, Integer> cases;
    private final SortedMap<String, MinAndMaxDateByState> minAndMaxDate;

    public CovidCasesChangeResource(SortedMap<String, Integer> cases, SortedMap<String, MinAndMaxDateByState> md) {
        this.cases = cases;
        this.minAndMaxDate = md;
    }

    @GET
    @Timed
    public StreamingOutput displayTrend(@PathParam("location") String state) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        JFreeChart chart = getChart(state);
        ChartUtils.writeChartAsPNG(outputStream, chart, 700, 467);
        StreamingOutput streamingOutput = new StreamingOutput() {
            @Override
            public void write(OutputStream output) throws IOException, WebApplicationException {
                output.write(outputStream.toByteArray());
            }
        };
        return streamingOutput;
    }

    public JFreeChart getChart(String state){
        var series = new XYSeries("Change in Cases");
        DateTime minDate = minAndMaxDate.get(state).getMinDate();
        DateTime maxDate = minAndMaxDate.get(state).getMaxDate();
        DateTime date = new DateTime(minDate);
        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");
        DateTimeFormatter fmt2 = DateTimeFormat.forPattern("MM-dd-yyyy");
        String key = createKey(state, fmt.print(date));
        String key2 = createKey(state, fmt.print(date.plusDays(1)));

        int i = 0;
        while(cases.get(key2) != null) {
            series.add(Double.valueOf(i), Double.valueOf(cases.get(key2) - cases.get(key)));
            date = date.plusDays(1);
            key = createKey(state, fmt.print(date));
            key2 = createKey(state, fmt.print(date.plusDays(1)));
            i++;
        }

        var dataset = new XYSeriesCollection();
        dataset.addSeries(series);

        JFreeChart chart = ChartFactory.createXYLineChart(
                String.format("%s Change in Cases from %s to %s", state, fmt2.print(minDate), fmt2.print(maxDate)),
                "Days",
                "Change in Cases",
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
