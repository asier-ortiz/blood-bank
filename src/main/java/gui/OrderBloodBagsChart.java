package gui;

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
import java.util.List;

public class OrderBloodBagsChart {
    private final List<String> bloodGroupNames;
    private final List<Integer> values;
    private final ChartPanel chartPanel;

    public OrderBloodBagsChart(List<String> bloodGroupNames, List<Integer> values) {
        this.bloodGroupNames = bloodGroupNames;
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
            dataset.setValue(values.get(i), "Cantidad", bloodGroupNames.get(i));
        }
        return dataset;
    }

    private JFreeChart createChart(CategoryDataset dataset) {
        JFreeChart barChart = ChartFactory.createBarChart(
                "",
                "Grupo sanguíneo",
                "Cantidad",
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