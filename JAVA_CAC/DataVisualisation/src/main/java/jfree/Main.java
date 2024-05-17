package jfree;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            createBarChartWindow("Brand", "Selling Price", "Average Selling Price per Brand");
            createBarChartWindow("Rating", "Selling Price", "Average Selling Price per Rating");
            createPieChartWindow("Brand", "Discount %", "Discount Distribution by Brand");
            createPieChartWindow("Rating", "Discount %", "Discount Distribution by Rating");
            createLineChartWindow("Brand", "Selling Price", "Trend of Average Selling Price by Brand");
            createScatterPlotWindow("Rating", "Selling Price", "Selling Price vs. Rating");
        });
    }

    private static void createBarChartWindow(String category, String value, String chartTitle) {
        JFrame frame = new JFrame(chartTitle);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.add(createBarChartPanel(category, value, chartTitle));
        frame.pack();
        frame.setVisible(true);
    }

    private static void createPieChartWindow(String category, String value, String chartTitle) {
        JFrame frame = new JFrame(chartTitle);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.add(createPieChartPanel(category, value, chartTitle));
        frame.pack();
        frame.setVisible(true);
    }

    private static void createLineChartWindow(String category, String value, String chartTitle) {
        JFrame frame = new JFrame(chartTitle);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.add(createLineChartPanel(category, value, chartTitle));
        frame.pack();
        frame.setVisible(true);
    }

    private static void createScatterPlotWindow(String xCategory, String yCategory, String chartTitle) {
        JFrame frame = new JFrame(chartTitle);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.add(createScatterPlotPanel(xCategory, yCategory, chartTitle));
        frame.pack();
        frame.setVisible(true);
    }

    private static JPanel createBarChartPanel(String category, String value, String chartTitle) {
        DefaultCategoryDataset dataset = createBarChartDataset(category, value);

        JFreeChart chart = ChartFactory.createBarChart(
                chartTitle,
                category,
                value,
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false);

        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        CategoryAxis domainAxis = plot.getDomainAxis();
        if (category.equals("Brand")) {
            domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
        }

        return new ChartPanel(chart);
    }

    private static DefaultCategoryDataset createBarChartDataset(String category, String value) {
        URL csvFileUrl = Main.class.getResource("/Flipkart_mobile_brands_scraped_data.csv");
        if (csvFileUrl == null) {
            System.err.println("CSV file not found!");
            return new DefaultCategoryDataset();
        }

        String csvFile = csvFileUrl.getPath();
        String line;
        String csvSplitBy = ",";
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        Map<String, Double> categoryTotalPrice = new HashMap<>();
        Map<String, Integer> categoryCount = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            br.readLine(); // Skip the header

            while ((line = br.readLine()) != null) {
                String[] columns = line.split(csvSplitBy);
                if (columns.length >= 9) {
                    String categoryValue = columns[getIndexByColumnName(category)];
                    double sellingPrice = Double.parseDouble(columns[getIndexByColumnName("Selling Price")]);

                    categoryTotalPrice.put(categoryValue, categoryTotalPrice.getOrDefault(categoryValue, 0.0) + sellingPrice);
                    categoryCount.put(categoryValue, categoryCount.getOrDefault(categoryValue, 0) + 1);
                }
            }

            for (Map.Entry<String, Double> entry : categoryTotalPrice.entrySet()) {
                String categoryValue = entry.getKey();
                double total = entry.getValue();
                int count = categoryCount.get(categoryValue);
                double average = total / count;
                dataset.addValue(average, value, categoryValue);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return dataset;
    }

    private static JPanel createPieChartPanel(String category, String value, String chartTitle) {
        DefaultPieDataset dataset = createPieChartDataset(category, value);

        JFreeChart chart = ChartFactory.createPieChart(
                chartTitle,
                dataset,
                true, // Include legend
                true, // Include tooltips
                false // Do not include URLs
        );

        return new ChartPanel(chart);
    }

    private static DefaultPieDataset createPieChartDataset(String category, String value) {
        URL csvFileUrl = Main.class.getResource("/Flipkart_mobile_brands_scraped_data.csv");
        if (csvFileUrl == null) {
            System.err.println("CSV file not found!");
            return new DefaultPieDataset();
        }

        String csvFile = csvFileUrl.getPath();
        String line;
        String csvSplitBy = ",";
        DefaultPieDataset dataset = new DefaultPieDataset();

        Map<String, Double> categoryTotalValue = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            br.readLine(); // Skip the header

            while ((line = br.readLine()) != null) {
                String[] columns = line.split(csvSplitBy);
                if (columns.length >= 9) {
                    String categoryValue = columns[getIndexByColumnName(category)];
                    double valueAmount = Double.parseDouble(columns[getIndexByColumnName(value)]);

                    categoryTotalValue.put(categoryValue, categoryTotalValue.getOrDefault(categoryValue, 0.0) + valueAmount);
                }
            }

            for (Map.Entry<String, Double> entry : categoryTotalValue.entrySet()) {
                dataset.setValue(entry.getKey(), entry.getValue());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return dataset;
    }

    private static int getIndexByColumnName(String columnName) {
        switch (columnName) {
            case "Brand":
                return 0;
            case "Model":
                return 1;
            case "Color":
                return 2;
            case "Memory":
                return 3;
            case "Storage":
                return 4;
            case "Rating":
                return 5;
            case "Selling Price":
                return 6;
            case "Original Price":
                return 7;
            case "Discount %":
                return 8;
            default:
                return -1;
        }
    }

    private static JPanel createLineChartPanel(String category, String value, String chartTitle) {
        DefaultCategoryDataset dataset = createBarChartDataset(category, value);

        JFreeChart chart = ChartFactory.createLineChart(
                chartTitle,
                category,
                value,
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false);

        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);

        return new ChartPanel(chart);
    }

    private static JPanel createScatterPlotPanel(String xCategory, String yCategory, String chartTitle) {
        XYDataset dataset = createScatterPlotDataset(xCategory, yCategory);

        JFreeChart chart = ChartFactory.createScatterPlot(
                chartTitle,
                xCategory,
                yCategory,
                dataset
        );

        return new ChartPanel(chart);
    }

    private static DefaultXYDataset createScatterPlotDataset(String xCategory, String yCategory) {
        URL csvFileUrl = Main.class.getResource("/Flipkart_mobile_brands_scraped_data.csv");
        if (csvFileUrl == null) {
            System.err.println("CSV file not found!");
            return new DefaultXYDataset();
        }

        String csvFile = csvFileUrl.getPath();
        String line;
        String csvSplitBy = ",";
        DefaultXYDataset dataset = new DefaultXYDataset();

        double[][] data = new double[2][]; // Dynamically sized data array

        int dataSize = 0; // Track the number of data points read

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            br.readLine(); // Skip the header

            while ((line = br.readLine()) != null) {
                String[] columns = line.split(csvSplitBy);
                if (columns.length >= 9) {
                    double xValue = Double.parseDouble(columns[getIndexByColumnName(xCategory)]); // Get the x-value
                    double yValue = Double.parseDouble(columns[getIndexByColumnName(yCategory)]); // Get the y-value

                    dataSize++; // Increment the data size

                    // Resize the data array and copy existing data
                    double[][] newData = new double[2][dataSize];
                    if (dataSize > 1) {
                        System.arraycopy(data[0], 0, newData[0], 0, dataSize - 1);
                        System.arraycopy(data[1], 0, newData[1], 0, dataSize - 1);
                    }
                    newData[0][dataSize - 1] = xValue; // Add new data
                    newData[1][dataSize - 1] = yValue;

                    data = newData; // Update data array
                }
            }

            dataset.addSeries("Selling Price vs. Rating", data);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return dataset;
    }

    @SuppressWarnings("unused")
    private static int getIndexByColumnName2(String columnName) {
        switch (columnName) {
            case "Brand":
                return 0;
            case "Model":
                return 1;
            case "Color":
                return 2;
            case "Memory":
                return 3;
            case "Storage":
                return 4;
            case "Rating":
                return 5;
            case "Selling Price":
                return 6;
            case "Original Price":
                return 7;
            case "Discount %":
                return 8;
            default:
                return -1;
        }
    }
}

