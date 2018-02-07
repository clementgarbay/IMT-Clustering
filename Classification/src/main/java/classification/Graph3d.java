package classification;

import Jama.Matrix;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.ChartLauncher;
import org.jzy3d.chart.factories.AWTChartComponentFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.Scatter;

public class Graph3d {
    Coord3d[] data;
    Color[] colors;

    public Graph3d(Matrix m) {
        data = new Coord3d[m.getRowDimension()];
        for (int i = 0; i < m.getRowDimension(); i++) {
            data[i] = new Coord3d(m.get(i, 0), m.get(i, 1), m.get(i, 2));
        }
    }

    public Graph3d(Matrix m, int[] classe) {
        this(m);
        if (m.getRowDimension() == classe.length) {
            colors = new Color[classe.length];
            for (int i = 0; i < classe.length; i++) {
                colors[i] = color(classe[i]);
            }
        }
    }

    public Graph3d(Matrix m, Color[] colors) {
        this(m);
        if (m.getRowDimension() == colors.length) {
            this.colors = colors;
        }
    }

    public static Color color(int i) {
        switch (i) {
            case 0:
                return Color.BLUE;
            case 1:
                return Color.GREEN;
            case 3:
                return Color.RED;
            case 4:
                return Color.YELLOW;
            case 5:
                return Color.CYAN;
            case 6:
                return Color.GRAY;
            case 7:
                return Color.MAGENTA;
        }
        return Color.BLACK;
    }

    public void display() {
        Scatter scatter;
        if (colors != null && colors.length == data.length)
            scatter = new Scatter(data, colors, 2.0f);
        else scatter = new Scatter(data, Color.BLACK, 2.0f);
        Chart chart = new Chart(new AWTChartComponentFactory(), Chart.DEFAULT_QUALITY);
        chart.getScene().add(scatter);
        ChartLauncher.openChart(chart);
    }

}
