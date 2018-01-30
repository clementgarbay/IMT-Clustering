package classification;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;

import javax.imageio.*;
import javax.swing.*;

import Jama.*;

public class Image extends Component {
	private static final long serialVersionUID = 1L;
	
	String name;
	Matrix data;
	BufferedImage img;
	BufferedImage[] img_seg;
    public int width, height;
    int[] classe;
    org.jzy3d.colors.Color colors[];
    org.jzy3d.colors.Color rcolors[];
    int nbClasses;
	
    public Image(String name, String path) throws IOException {
        img = ImageIO.read(new File(path));
        this.nbClasses = 1;
        this.width = img.getWidth();
        this.height = img.getHeight();
        this.name = name;
        this.data = new Matrix(height*width,3);
        this.classe = new int[height*width];
        this.rcolors = new org.jzy3d.colors.Color[height*width];
        for(int x=0; x<height;x++){
        	for(int y=0;y<width;y++){
       			int[] rgb = int2RGB(img.getRGB(y, x));
       			int k = x*width+y;
       			this.data.set(k, 0, rgb[0]);
       			this.data.set(k, 1, rgb[1]);
       			this.data.set(k, 2, rgb[2]);
       			rcolors[k] = new org.jzy3d.colors.Color(rgb[0], rgb[1], rgb[2]);
        	}
        }
	}
    
    public Image(String name, Matrix m, int width, int height) {
    	this.nbClasses = 1;
    	this.name = name;
    	this.width = width;
    	this.height = height;
    	this.data = m;
    	this.img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        this.rcolors = new org.jzy3d.colors.Color[height*width];
    	for(int x=0; x<height;x++){
        	for(int y=0;y<width;y++){
       			int k = x*width+y;
       			int[] rgb = new int[]{
       					(int) this.data.get(k, 0),
       					(int) this.data.get(k, 1),
       					(int) this.data.get(k, 2)
       			};
        		img.setRGB(y, x,RGB2int(rgb));
       			rcolors[k] = new org.jzy3d.colors.Color(rgb[0], rgb[1], rgb[2]);
        	}
    	}
    	this.classe = new int[height*width];
	}
        
    public int[] get(int k){
    	return new int[]{
					(int) this.data.get(k, 0),
   					(int) this.data.get(k, 1),
   					(int) this.data.get(k, 2)
   			};
    }
    
    public Matrix getMatrix(){ return data; }
    public BufferedImage getBufferedImage(){ return img; }

    private int maxHeight(){
    	return (35+height)*(nbClasses+2);
    }
    
    
    public void paint(Graphics g) {
    	if(nbClasses>1){
    		int y = 15;
    		g.drawString("Image d'origine :", width/2-30,y);
    		y+=10;
    		g.drawImage(img, 0, y, null);
    		for(int i=0;i<=nbClasses;i++){
    			y+=(height+25);
    			if(i==0) g.drawString("Image segmentée :", width/2-30,y);
    			else g.drawString("Classe "+i+" :", width/2-30,y); 
    			y+=10;
    			g.drawImage(img_seg[i], 0, y, null);
    		}
    	}
    	else
    		g.drawImage(img, 0, 0, null);
    }
	
	public static int[] int2RGB(int argb){
		Color c = new Color(argb);
		return new int[] {
				c.getRed(),
				c.getGreen(),
				c.getBlue()
		};
	}
	
    public Dimension getPreferredSize() {
        if (img == null) {
             return new Dimension(100,100);
        } else {
           return new Dimension(width,maxHeight());
       }
    }
    
	public static int RGB2int(int[] rgb){
		Color c = new Color(rgb[0],rgb[1],rgb[2]);
		return c.getRGB();
	}

	public static String toString(int[] t){
		String res = "( ";
		for(int i=0;i<t.length;i++){
			res+= t[i];
			if(i==t.length-1) res+=" )";
			else res+=" , ";
		}
		return res;
	}	
		
	public void write(){
		File f = new File(name+".png");
		try {
		   if(!ImageIO.write(img, "png", f))
		     JOptionPane.showMessageDialog(null, "Erreur lors de l'écriture de "+name+".png");
		}catch (Exception e) {
			System.out.println(e);
		}
	}
	
	public void display(){
        JFrame f = new JFrame(name);
        f.addWindowListener(new WindowAdapter(){public void windowClosing(WindowEvent e) {System.exit(0);}});
        JPanel jp = new JPanel();
        jp.add(this);
        JScrollPane scrolledpanel = new JScrollPane(jp);
        jp.setPreferredSize(new Dimension(width,maxHeight()));
        f.add(scrolledpanel);
        f.setPreferredSize(new Dimension(width+50, Math.min(900,maxHeight())));
        f.pack();
        f.setVisible(true);
	}
	
	public void display3D(){
		Graph3d g;
		if(nbClasses>1){
			if(colors!=null)
				g = new Graph3d(data,colors);
			else
			g = new Graph3d(data,classe);
		}
		else
			g = new Graph3d(data);
		g.display();
	}

	
	public void displayColors(){
		Graph3d g;
		g = new Graph3d(data,rcolors);
		g.display();
	}

	public void hmeans(int k){
		if(k<=1) return;
		nbClasses = k;
		Hmeans hm = new Hmeans(data, k);
		classe = hm.getClasses();
    	this.img_seg = new BufferedImage[nbClasses+1];
    	for(int i=0;i<=nbClasses;i++)
    		this.img_seg[i] = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    	colors = new org.jzy3d.colors.Color[height*width];
    	int black = Color.BLACK.getRGB();
    	for(int x=0; x<height;x++){
        	for(int y=0;y<width;y++){
       			int[] rgb = hm.getCouleurClasse(classe[x*width+y]);
       			colors[x*width+y] = new org.jzy3d.colors.Color(rgb[0], rgb[1], rgb[2]);
       			img_seg[0].setRGB(y, x, RGB2int(rgb));
        		img_seg[classe[x*width+y]+1].setRGB(y, x, black);
        	}
    	}
	}
	
	public void kmeans(int k){
		if(k<=1) return;
		nbClasses = k;
		Kmeans km = new Kmeans(data, k);
		classe = km.getClasses();
    	this.img_seg = new BufferedImage[nbClasses+1];
    	for(int i=0;i<=nbClasses;i++)
    		this.img_seg[i] = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    	colors = new org.jzy3d.colors.Color[height*width];
    	int black = Color.BLACK.getRGB();
    	for(int x=0; x<height;x++){
        	for(int y=0;y<width;y++){
       			int[] rgb = km.getCouleurClasse(classe[x*width+y]);
       			colors[x*width+y] = new org.jzy3d.colors.Color(rgb[0], rgb[1], rgb[2]);
       			img_seg[0].setRGB(y, x, RGB2int(rgb));
        		img_seg[classe[x*width+y]+1].setRGB(y, x, black);
        	}
    	}
	}
	
	public void reinit(){
		nbClasses = 1;
		this.img_seg = null;
		colors = null;
		classe = new int[height*width];
	}
	

}
