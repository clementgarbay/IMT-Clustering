package classification;

import Jama.Matrix;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class BinImage extends Component {
    private static final long serialVersionUID = 1L;
    public int width, height;
    String name;
    Matrix data;
    BufferedImage img;
    BufferedImage[] img_seg;
    int[] classe;
    org.jzy3d.colors.Color colors[];
    int nbClasses;
    int size;

    public BinImage(String name, String path) throws IOException {
        img = ImageIO.read(new File(path));
        this.nbClasses = 1;
        this.width = img.getWidth();
        this.height = img.getHeight();
        this.name = name;
        this.size = 0;
        for (int x = 0; x < height; x++) {
            for (int y = 0; y < width; y++) {
                int[] rgb = int2RGB(img.getRGB(y, x));
                if (rgb[0] + rgb[1] + rgb[2] < 500) this.size++;
            }
        }
        this.data = new Matrix(size, 2);
        this.classe = new int[size];
        int i = 0;
        for (int x = 0; x < height; x++) {
            for (int y = 0; y < width; y++) {
                int[] rgb = int2RGB(img.getRGB(y, x));
                if (rgb[0] + rgb[1] + rgb[2] < 500) {
                    this.data.set(i, 0, x);
                    this.data.set(i, 1, y);
                    i++;
                }
            }
        }

    }

    public static int[] int2RGB(int argb) {
        Color c = new Color(argb);
        return new int[]{
                c.getRed(),
                c.getGreen(),
                c.getBlue()
        };
    }

    public static int RGB2int(int[] rgb) {
        Color c = new Color(rgb[0], rgb[1], rgb[2]);
        return c.getRGB();
    }

    public static String toString(int[] t) {
        String res = "( ";
        for (int i = 0; i < t.length; i++) {
            res += t[i];
            if (i == t.length - 1) res += " )";
            else res += " , ";
        }
        return res;
    }

    public static int color(int i) {
        switch (i) {
            case 0:
                return Color.BLUE.getRGB();
            case 1:
                return Color.GREEN.getRGB();
            case 3:
                return Color.RED.getRGB();
            case 4:
                return Color.YELLOW.getRGB();
            case 5:
                return Color.CYAN.getRGB();
            case 6:
                return Color.GRAY.getRGB();
            case 7:
                return Color.MAGENTA.getRGB();
        }
        return Color.BLACK.getRGB();
    }

    public int[] get(int k) {
        return new int[]{
                (int) this.data.get(k, 0),
                (int) this.data.get(k, 1),
                (int) this.data.get(k, 2)
        };
    }

    public Matrix getMatrix() {
        return data;
    }

    public BufferedImage getBufferedImage() {
        return img;
    }

    private int maxHeight() {
        return (35 + height) * (nbClasses + 2);
    }

    public void paint(Graphics g) {
        if (nbClasses > 1) {
            int y = 15;
            g.drawString("Image d'origine :", width / 2 - 30, y);
            y += 10;
            g.drawImage(img, 0, y, null);
            for (int i = 0; i <= nbClasses; i++) {
                y += (height + 25);
                if (i == 0) g.drawString("Image segment�e :", width / 2 - 30, y);
                else g.drawString("Classe " + i + " :", width / 2 - 30, y);
                y += 10;
                g.drawImage(img_seg[i], 0, y, null);
            }
        } else
            g.drawImage(img, 0, 0, null);
    }

    public Dimension getPreferredSize() {
        if (img == null) {
            return new Dimension(100, 100);
        } else {
            return new Dimension(width, maxHeight());
        }
    }

    public void write() {
        File f = new File(name + ".png");
        try {
            if (!ImageIO.write(img, "png", f))
                JOptionPane.showMessageDialog(null, "Erreur lors de l'�criture de " + name + ".png");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void display() {
        JFrame f = new JFrame(name);
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        JPanel jp = new JPanel();
        jp.add(this);
        JScrollPane scrolledpanel = new JScrollPane(jp);
        jp.setPreferredSize(new Dimension(width, maxHeight()));
        f.add(scrolledpanel);
        f.setPreferredSize(new Dimension(width + 50, Math.min(900, maxHeight())));
        f.pack();
        f.setVisible(true);
    }

    public void hmeans(int k) {
        if (k <= 1) return;
        nbClasses = k;
        Hmeans hm = new Hmeans(data, k);
        classe = hm.getClasses();
        this.img_seg = new BufferedImage[nbClasses + 1];
        for (int i = 0; i <= nbClasses; i++)
            this.img_seg[i] = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        colors = new org.jzy3d.colors.Color[size];
        int black = Color.BLACK.getRGB();
        for (int i = 0; i < size; i++) {
            int x = (int) data.get(i, 0);
            int y = (int) data.get(i, 1);
            img_seg[0].setRGB(y, x, color(classe[i]));
            img_seg[1 + classe[i]].setRGB(y, x, black);
        }
    }

    public void kmeans(int k) {
        if (k <= 1) return;
        nbClasses = k;
        Kmeans km = new Kmeans(data, k);
        classe = km.getClasses();
        this.img_seg = new BufferedImage[nbClasses + 1];
        for (int i = 0; i <= nbClasses; i++)
            this.img_seg[i] = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        colors = new org.jzy3d.colors.Color[size];
        int black = Color.BLACK.getRGB();
        for (int i = 0; i < size; i++) {
            int x = (int) data.get(i, 0);
            int y = (int) data.get(i, 1);
            img_seg[0].setRGB(y, x, color(classe[i]));
            img_seg[1 + classe[i]].setRGB(y, x, black);
        }
    }

    public void reinit() {
        nbClasses = 1;
        this.img_seg = null;
        colors = null;
        classe = new int[size];
    }

}
