import java.awt.*;
import java.awt.event.*;
import java.util.*;
import Jama.*; 

public class InterfaceGraphique extends Canvas implements MouseMotionListener, MouseListener {
	private static final long serialVersionUID = 1L;

	private ArrayList<Point> data;
	private ArrayList<Point> estimatedData;

	boolean circle = false;
	double[] estimatedcircle;

	boolean poly = false;
	double[] estimatedpoly;
	
	int startx = -1;
	int endx = -1;
	
	InterfaceGraphique () {
		data = new ArrayList<Point>();
		estimatedData = new ArrayList<Point>();
		addMouseListener(this);
		addMouseMotionListener(this);
		setBackground(new Color(255,255,255));
	}
	
	public void init(){
		data.clear();
		estimatedData.clear();
		circle = false;
		poly = false;
		estimatedpoly = null;
		estimatedcircle = null;
		startx = -1;
		endx = -1;
	}
	
	public void paint (Graphics g){ 
		g.drawString("Dessiner une courbe polynomiale ou un cercle (dessiner : maintien clic gauche - effacer : clic gauche bref)",10,15); 
		int r = 5;  
		if (!data.isEmpty()) {
			g.setColor(Color.black);
			for (int i = 1; i < data.size(); i++) 
				g.drawLine(data.get(i-1).x, data.get(i-1).y,data.get(i).x, data.get(i).y);
			g.drawArc(data.get(data.size()-1).x - r, data.get(data.size()-1).y - r, 2*r, 2*r, 0, 360);
		}
		
		if(poly) estimatedData = getPoly((int)startx, (int)endx, estimatedpoly);
		
		if (!estimatedData.isEmpty()) {
			g.setColor(Color.RED);
			for (int i = 1; i < estimatedData.size(); i++)
				g.drawLine(estimatedData.get(i-1).x, estimatedData.get(i-1).y,estimatedData.get(i).x, estimatedData.get(i).y);
			g.setColor(Color.black);
		}

		if(circle){
			drawCircle(g, estimatedcircle[0],estimatedcircle[1],estimatedcircle[2]);
			drawCircle(g, estimatedcircle[0],estimatedcircle[1],1);
		}		
	}

	public void drawCircle(Graphics g, double a, double b, double r){
		g.setColor(Color.RED);
		g.drawOval((int)(a-r),(int)(b-r),(int)(2*r),(int)(2*r));
		g.setColor(Color.BLACK);
	}
	
	public ArrayList<Point> getPoly(int start, int end, double[] coeff){
		ArrayList<Point> res = new ArrayList<Point>();
		double y, x;
		for(int i=start;i<=end;i++){
			x = i-start;
			y = 0;
			for(int k=0;k<coeff.length;k++)
				 y+=coeff[k]*Math.pow(x,k);
			res.add(new Point(i, (int)y));
		}
		return res;
	}
	
	public void mouseMoved(MouseEvent e) {

	}

	public void mouseDragged(MouseEvent e) {
		circle = false;
		data.add(new Point(e.getPoint()));
		repaint();
	}	
	
	public void mouseClicked(MouseEvent e) {
		init();
	}

	public void mouseEntered(MouseEvent e) {
		
	}

	public void mouseExited(MouseEvent e) {
		
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
		if (!data.isEmpty()) {
			if(isFunctionnal(data)){
				for(int k=0;k<data.size();k++){
					if(startx == -1 || startx>data.get(k).getX()) startx = (int)data.get(k).getX();
					if(endx<data.get(k).getX()) endx = (int)data.get(k).getX();
				}
				estimatedpoly = MoindresCarres.estimatePoly(data,startx);
				poly = true;
			}else{
				estimatedcircle = MoindresCarres.estimateCircle(data);
				circle = true;
			}				
			repaint();
		}
	}
	
	public boolean isFunctionnal(ArrayList<Point> T){
		if(T.isEmpty()) return false;
		int nb = 0;
		for(int k=0;k<T.size()-1;k++){
			if(T.get(k+1).getX()<T.get(k).getX()) nb++;
		}
		double pr = (double)nb/T.size();
		return (pr<0.3);
	}
	
	public double mod2pi(double x){
		if(x<0) return mod2pi(x+2*Math.PI);
		if(x>2*Math.PI) return mod2pi(x-2*Math.PI);
		return x;
	}

	public static void print(Matrix m){
		System.out.print("[ ");
		for(int i=0;i<m.getRowDimension();i++){
			for(int j=0;j<m.getColumnDimension();j++){
				System.out.print(m.get(i, j));
				if(j<m.getColumnDimension()-1) System.out.print(" , ");
			}
			System.out.println(";");
		}
		System.out.println("]");
	}
}
