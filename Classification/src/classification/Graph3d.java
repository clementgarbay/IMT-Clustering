package classification;
import org.jzy3d.chart.*;
import org.jzy3d.colors.*;
import org.jzy3d.maths.*;
import org.jzy3d.plot3d.primitives.*;
import org.jzy3d.ui.*;
import Jama.*;

public class Graph3d {
	Coord3d[] data;
	Color[] colors;
	
	public Graph3d(Matrix m) {
		data = new Coord3d[m.getRowDimension()];
		for(int i=0;i<m.getRowDimension();i++){
			data[i] = new Coord3d(m.get(i, 0),m.get(i, 1),m.get(i, 2)); 
		}
	}
	
	public Graph3d(Matrix m, int[] classe) {
		this(m);
		if(m.getRowDimension()==classe.length){
			colors = new Color[classe.length];
			for(int i=0;i<classe.length;i++){
				colors[i] = color(classe[i]);
			}
		}
	}
	
	public Graph3d(Matrix m, Color[] colors) {
		this(m);
		if(m.getRowDimension()==colors.length){
			this.colors = colors;
		}
	}

	public static Color color(int i){
		switch(i){
		case 0: return Color.BLUE;
		case 1: return Color.GREEN;
		case 3: return Color.RED;
		case 4: return Color.YELLOW;
		case 5: return Color.CYAN;
		case 6: return Color.GRAY;
		case 7: return Color.MAGENTA;
		}
		return Color.BLACK;
	}

	public void display(){
		Scatter scatter; 
		if(colors!=null && colors.length==data.length)
			scatter = new Scatter(data,colors,2.0f);
		else scatter = new Scatter(data,Color.BLACK,2.0f);
		Chart chart = new Chart();
		chart.getScene().add(scatter);
		ChartLauncher.openChart(chart);
	}

}
