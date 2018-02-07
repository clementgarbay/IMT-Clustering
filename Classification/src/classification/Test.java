package classification;
import Jama.Matrix;

import java.io.IOException;
import java.util.Arrays;

public class Test {
	public static void main(String[] args) throws IOException {
		Vecteur[] nuage = new Vecteur[10];
		nuage[0]=new Vecteur(19,4);
		nuage[1]=new Vecteur(12,15);
		nuage[2]=new Vecteur(15,11);
		nuage[3]=new Vecteur(2,17);
		nuage[4]=new Vecteur(9,18);
		nuage[5]=new Vecteur(18,3);
		nuage[6]=new Vecteur(5,9);
		nuage[7]=new Vecteur(16,10);
		nuage[8]=new Vecteur(6,18);
		nuage[9]=new Vecteur(17,19);
		//Hmeans h = new Hmeans(matrice(nuage), 3);
		Kmeans h = new Kmeans(matrice(nuage), 3);
		System.out.println(h.getPartition());

		/*Vecteur[] vs = new Vecteur[5];
		vs[0]=new Vecteur(4,9);
		vs[1]=new Vecteur(6,5);
		vs[2]=new Vecteur(20,7);
		vs[3]=new Vecteur(22,6);
		vs[4]=new Vecteur(24,11);
		h = new Hmeans(matrice(nuage), 2);
		System.out.println(h.getPartition());

        System.out.println(inertie(nuage));*/

		/*Image im = new Image("test","images/martine.jpg");
		im.kmeans(2);
		im.display();*/
		//im.display3D();
}

	public static Matrix matrice(Vecteur[] vs){
		double[][] c = new double[vs.length][vs[0].length];
		int i=0;
		for(Vecteur v:vs)
			c[i++]=v.toDouble();
		return new Matrix(c);		
	}

    public static double inertie(Vecteur[] vecteurs) {
        Vecteur barycentre = Vecteur.barycentre(vecteurs);
        return Arrays.stream(vecteurs).mapToDouble(vecteur -> Vecteur.distanceCarre(vecteur, barycentre)).sum();
    }

    public static double inertie(Vecteur[] vecteurs, int[][] classes) {
        return Arrays.stream(classes).mapToDouble(classe ->
            inertie(Arrays.stream(classe).mapToObj(i -> vecteurs[i]).toArray(Vecteur[]::new))
        ).sum();
    }

}
