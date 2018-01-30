package classification;
import java.util.ArrayList;
import java.util.HashSet;

import Jama.*;

public class Kmeans {

	int[] classes;
	int[] tailles;
	Vecteur[] centres;
	
	double totalVariances;
	
	int nbVecteurs;
	int dimension;
	int k;
		
	Matrix data;	
	
	
	public Kmeans(Matrix m, int k){
		this.k = k;
		this.data = m;
		this.nbVecteurs = m.getRowDimension();
		this.dimension = m.getColumnDimension();
		this.classes = new int[nbVecteurs];
		this.centres = new Vecteur[k];
		this.tailles = new int[k];
		this.totalVariances = 0;
		
		/* A compléter */
	}
	
	private boolean affectation(int i) {
		/* A compléter */
		return false;
	}
	
	private double deltaVariance(Vecteur v, int classeAvant, int classeApres){
		return (double)(tailles[classeApres])/(double)(tailles[classeApres]+1)*Vecteur.distanceCarre(centres[classeApres], v)
				- (double)tailles[classeAvant]/(double)(tailles[classeAvant]-1)*Vecteur.distanceCarre(centres[classeAvant], v);
	}
	
	private double ajoutVariance(Vecteur v, int classe){
		return (double)tailles[classe]/(double)(tailles[classe]+1)*Vecteur.distanceCarre(centres[classe], v);
	}

	private void initialiseCentres() {
		/* A compléter */
		for(int i=0;i<k;i++){
			int n;
			do{n = (int)(Math.random()*nbVecteurs);}
			while(estCentre(n));
			centres[i] = vecteur(n);
		}
	}

	private void calculCentres(){
		/* A compléter */
		centres = new Vecteur[k];
		for(int i=0;i<k;i++){
			centres[i]=new Vecteur(dimension);
		}
		int[] nb = new int[k];
		for(int i=0;i<nbVecteurs;i++){
			centres[classes[i]].plus(vecteur(i));
			nb[classes[i]]++;
		}
		for(int i=0;i<k;i++){
			if(nb[classes[i]]==0) throw new Error("Cas dégénéré : classe "+i+" vide.");
			centres[i].div(nb[i]);
		}
	}

	public int[] getClasses(){ return this.classes; } 
	
	public boolean estCentre(int n){
		Vecteur vect = vecteur(n);
		for(int i=0;i<k;i++){
			if(vect.equals(centres[i])) return true;
		}
		return false;
	}
	
	
	public int[] getCouleurClasse(int k){
		int[] res = new int[dimension];
		for(int i=0;i<dimension;i++){
			res[i] = (int)centres[k].get(i);
		}
		return res;
	}
	
	public Vecteur vecteur(int n){
		double[] result = new double[dimension];
		for(int i=0;i<dimension;i++){
			result[i]=(double)data.get(n, i);
		}
		return new Vecteur(result);
	}
	

	private int centreLePlusProche(Vecteur vecteur){
		/* A compléter */
		int nbCentre = centres.length;
		int classe = 0;
		double min = Vecteur.distance(vecteur, centres[0]);
		for(int i=1;i<nbCentre;i++){
			if(Vecteur.distance(vecteur, centres[i])<min){
				classe = i;
				min = Vecteur.distance(vecteur, centres[i]);
			}
		}
		return classe;
	}

	public ArrayList<HashSet<Integer>> getPartition(){ 
		ArrayList<HashSet<Integer>> r = new ArrayList<HashSet<Integer>>();
		for(int i=0;i<k;i++) r.add(new HashSet<Integer>());
		for(int i=0;i<classes.length;i++){
			r.get(classes[i]).add(i);
		}
		return r;
	}


}
