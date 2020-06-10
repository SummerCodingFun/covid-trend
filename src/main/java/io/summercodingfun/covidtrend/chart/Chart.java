package io.summercodingfun.covidtrend.chart;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeriesCollection;

public class Chart {
    public static JFreeChart createXYLineChart(
            String title,
            String xTitle,
            String yTitle,
            XYSeriesCollection dataset
    ) {
        return ChartFactory.createXYLineChart(title,
                xTitle,
                yTitle,
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false);
    }
}
