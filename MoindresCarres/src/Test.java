import javax.swing.*;

public class Test {
	Test() {
		JFrame fenetre = new JFrame("TP moindres carrés - Tony Bourdier (c) 2014");
		fenetre.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		fenetre.setLocationByPlatform(true);
		InterfaceGraphique ig = new InterfaceGraphique();
		ig.setSize(600, 700);
		fenetre.add(ig);
		fenetre.pack();
		fenetre.setVisible(true);
	}

	public static void main(String[] args) {
	    javax.swing.SwingUtilities.invokeLater(Test::new);
	}
}
