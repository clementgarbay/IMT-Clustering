import java.awt.*;
import java.util.*;
import Jama.*; 

/**
 * class Matrix{
 * 		function Matrix transpose();
 *  	function Matrix times(Matrix other));
 *  	function Matrix inverse();
 *  	function double get(int i, int j); // les indices commencent ? 0
 *  }
 *  
 *  class Point{
 *  	function double getX();
 *  	function double getY();
 *  }
 */

public class MoindresCarres {

	/**
	 * data = <(x_0,y_0), ... , (x_n,y_n)>
	 * 
	 * model : y = f(x) = coeff_0 + coeff_1.x + coeff_2.x^2 + ... + coeff_max.x^max
	 * 
	 * result  = [coeff_0 , coeff_1 , ..., coeff_max]
	 */
	public static double[] estimatePoly(ArrayList<Point> data, int startx) {
		int m = 4; // degré (+1) du polynome recherché (modifiable)
		double[] result = new double[m];

        Matrix a = new Matrix(data.size(), m);
        Matrix b = new Matrix(data.size(), 1);

        for (int currentLine = 0; currentLine < data.size(); currentLine++) {
            Point xy = data.get(currentLine);

            for (int currentColumn = 0; currentColumn < m; currentColumn++) {
                a.set(currentLine, currentColumn, Math.pow(xy.x - startx, currentColumn));
            }

            b.set(currentLine, 0, xy.y);
        }

        Matrix aTranspose = a.transpose();
        Matrix x = aTranspose.times(a).inverse().times(aTranspose).times(b);

        for (int i = 0; i < m; i++) {
            result[i] = x.get(i, 0);
        }

        return result;
	}

	/**
	 * data = <(x_0,y_0), ..., (x_n,y_n)>
	 * 
	 * model :  ( x ) = ( x_centre + rayon.cos(theta) )
	 *          ( y )   ( y_centre + rayon.sin(theta) )
	 *          
	 * result  = [x_centre , y_centre , rayon]
     * ________________________________________________
     *
     * a =
     * ( 1 0 cos(theta) )
     * (      ...       )
     * ( 0 1 sin(theta) )
     * (      ...       )
     *
     * b =
     * ( x )
     * (...)
     * ( y )
     * (...)
	 */
	public static double[] estimateCircle(ArrayList<Point> data) {
		double[] result = new double[3];

		double centreX = data.stream().mapToDouble(xs -> xs.x).sum() / data.size();
        double centreY = data.stream().mapToDouble(xs -> xs.y).sum() / data.size();

        Matrix a = new Matrix(data.size() * 2, 3);
        Matrix b = new Matrix(data.size() * 2, 1);

        for (int currentLine = 0; currentLine < data.size(); currentLine++) {
            Point xy = data.get(currentLine);
            double adj = xy.x - centreX;
            double opp = xy.y - centreY;
            double hyp = Math.sqrt(Math.pow(adj, 2) + Math.pow(opp, 2));
            double cosTheta = adj / hyp;
            double sinTheta = opp / hyp;

            a.set(currentLine, 0, 1);
            a.set(currentLine, 1, 0);
            a.set(currentLine, 2, cosTheta);

            a.set(currentLine + data.size(), 0, 0);
            a.set(currentLine + data.size(), 1, 1);
            a.set(currentLine + data.size(), 2, sinTheta);

            b.set(currentLine, 0, xy.x);
            b.set(currentLine + data.size(), 0, xy.y);
        }

        Matrix aTranspose = a.transpose();
        Matrix x = aTranspose.times(a).inverse().times(aTranspose).times(b);

        for (int i = 0; i < 3; i++) {
            result[i] = x.get(i, 0);
        }

		return result;
	}
	
}
