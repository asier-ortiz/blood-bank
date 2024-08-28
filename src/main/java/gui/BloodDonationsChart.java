package gui;

import java.util.List;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;

public class BloodDonationsChart {
    private final List<String> years;
    private final List<Integer> values;
    private final ChartPanel chartPanel;

    public BloodDonationsChart(List<String> years, List<Integer> values) {
        this.years = years;
        this.values = values;
        CategoryDataset dataset = createDataset();
        JFreeChart chart = createChart(dataset);
        chartPanel = new ChartPanel(chart);
        chartPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        chartPanel.setBackground(Color.BLACK);
    }

    private CategoryDataset createDataset() {
        var dataset = new DefaultCategoryDataset();
        for (int i = 0; i < values.size(); i++) {
            dataset.setValue(values.get(i), "Donaciones", years.get(i));
        }
        return dataset;
    }

    private JFreeChart createChart(CategoryDataset dataset) {
        JFreeChart barChart = ChartFactory.createBarChart(
                "",
                "Años",
                "Donaciones",
                dataset,
                PlotOrientation.VERTICAL,
                false, true, false);
        CategoryPlot plot = (CategoryPlot) barChart.getPlot();
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        return barChart;
    }

    public ChartPanel getChartPanel() {
        return chartPanel;
    }
}