package classification;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import Jama.*;

import static classification.StreamUtils.*;

public class Kmeans {

	Matrix donnees;
	int[] classes;
	int[] tailles;
	Vecteur[] centres;
	
	double totalVariances;
	
	int nombreVecteurs;
	int nombreClasses;
	int dimension;

	public Kmeans(Matrix donnees, int nombreClasses) {
		this.nombreClasses = nombreClasses;
		this.donnees = donnees;
		this.nombreVecteurs = donnees.getRowDimension();
		this.dimension = donnees.getColumnDimension();
		this.classes = new int[nombreVecteurs];
		this.centres = new Vecteur[nombreClasses];
		this.tailles = new int[nombreClasses];
		this.totalVariances = 0;
		
        run();
	}

	private void run() {
	    initialise();

	    while (changementClasses());
    }

    private void initialise() {
        final Random random = new Random();

        // Calcul des classes aléatoirement (non vide)
        for (int i = 0; i < this.classes.length; i++) {
            int classe = i % nombreClasses;
            this.classes[i] = classe; // random.nextInt(nombreClasses);
            tailles[classe]++;
        }

        calculBarycentres();
    }

    private void calculBarycentres() {
        this.centres = toArray(getPartition().stream()
			.map(partition -> partition.stream().map(this::vecteur))
			.map(partitionVecteurs -> Vecteur.barycentre(toArray(partitionVecteurs))));
    }

    private boolean changementClasses() {
	    //return some(IntStream.range(0, classes.length).toArray(), point -> this.affectation(point));

	    boolean hasChanged = false;

	    int length = classes.length;
        for (int point = 0; point < length; point++) {
            //System.out.println("On traite le point " + point);
            hasChanged = this.affectation(point) || hasChanged;
        }

        return hasChanged; // some(getPartition(), partition -> some(partition, this::affectation));
    }
	
	private boolean affectation(int i) {
        //System.out.println("On traite le point dans affectation " + i);
        Vecteur xI = vecteur(i);
        int classeI = classes[i];

        int nouvelleClasseI = IntStream.range(0, nombreClasses).boxed()
            .map(classe -> {
                if (classeI == classe) {
                    return toEntry(classe, 0.0); // System.out.println("delta -> " + delta);
                }

                double delta = deltaVariance(xI, classeI, classe);

                return toEntry(classe, delta);
            }) // calcul le delta pour chaque classe
            .min(Comparator.comparingDouble(AbstractMap.SimpleEntry::getValue)) // prend le plus delta
            .map(min -> (min.getValue() < 0) ? min.getKey() : classeI) // récupère la classe associé au plus petit delta (i.e. nouvelle classe) ou la même classe si delta non négatif
            .get();


        if (classeI == nouvelleClasseI) {
//            System.out.println("Pas de changement pour " + i + " (classe " + classeI + ")");
            return false; // pas de changement de classe
        }

//        System.out.println("Changement pour " + i + " (classe " + classeI + " ->  " + nouvelleClasseI + ")");

        Vecteur baryIP = barycentreWithoutI(i);
        Vecteur baryKP = barycentreClasseWithI(i, nouvelleClasseI);

        centres[classeI] = baryIP; // nouveau barycentre de i
        centres[nouvelleClasseI] = baryKP; // nouveau barycentre de k

        classes[i] = nouvelleClasseI; // la classe de i devient k

        tailles[classeI] = tailles[classeI] - 1;
        tailles[nouvelleClasseI] = tailles[nouvelleClasseI] + 1;

        return true; // il y a eu un changement de classe
	}

	// Slide 19
	private Vecteur barycentreWithoutI(int pointI) {
	    int classeI = classes[pointI];
        Vecteur oldBarycentreI = centres[classeI]; // bary[i]
        Vecteur xI = vecteur(classeI); // x[i]
        double nI = tailles[classeI]; // n[i]
        double coef = 1f / (nI - 1f);

        return Vecteur.plus(oldBarycentreI, Vecteur.mult(Vecteur.moins(oldBarycentreI, xI), coef));
    }

    // Slide 19
	private Vecteur barycentreClasseWithI(int pointI, int classeK) {
        int classeI = classes[pointI];
		Vecteur oldBarycentreK = centres[classeK]; // bary[k]
	    Vecteur xI = vecteur(classeI); // x[i]
        double nK = tailles[classeK]; // n[k]
        double coef = 1f / (nK + 1f);

        return Vecteur.plus(oldBarycentreK, Vecteur.mult(Vecteur.moins(xI, oldBarycentreK), coef));
    }
	
	private double deltaVariance(Vecteur v, int classeAvant, int classeApres) {
		return (double)(tailles[classeApres])/(double)(tailles[classeApres]+1)*Vecteur.distanceCarre(centres[classeApres], v)
				- (double)tailles[classeAvant]/(double)(tailles[classeAvant]-1)*Vecteur.distanceCarre(centres[classeAvant], v);
	}
	
	private double ajoutVariance(Vecteur v, int classe) {
		return (double)tailles[classe]/(double)(tailles[classe]+1)*Vecteur.distanceCarre(centres[classe], v);
	}

	private void initialiseCentres() {
		/* A compléter */
		for(int i = 0; i< nombreClasses; i++){
			int n;
			do{n = (int)(Math.random()* nombreVecteurs);}
			while(estCentre(n));
			centres[i] = vecteur(n);
		}
	}

	private void calculCentres() {
		/* A compléter */
		centres = new Vecteur[nombreClasses];
		for(int i = 0; i< nombreClasses; i++){
			centres[i]=new Vecteur(dimension);
		}
		int[] nb = new int[nombreClasses];
		for(int i = 0; i< nombreVecteurs; i++){
			centres[classes[i]].plus(vecteur(i));
			nb[classes[i]]++;
		}
		for(int i = 0; i< nombreClasses; i++){
			if(nb[classes[i]]==0) throw new Error("Cas dégénéré : classe "+i+" vide.");
			centres[i].div(nb[i]);
		}
	}

	public int[] getClasses(){ return this.classes; } 
	
	public boolean estCentre(int n){
		Vecteur vect = vecteur(n);
		for(int i = 0; i< nombreClasses; i++){
			if(vect.equals(centres[i])) return true;
		}
		return false;
	}
	
	
	public int[] getCouleurClasse(int k){
		int[] res = new int[dimension];
		for(int i = 0; i < dimension; i++){
			res[i] = (int)centres[k].get(i);
		}
		return res;
	}
	
	public Vecteur vecteur(int n){
		double[] result = new double[dimension];
		for (int i = 0; i < dimension; i++) {
			result[i] = donnees.get(n, i);
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
		ArrayList<HashSet<Integer>> r = new ArrayList<>();
		for(int i = 0; i< nombreClasses; i++) r.add(new HashSet<>());
		for(int i=0;i<classes.length;i++){
			r.get(classes[i]).add(i);
		}
		return r;
	}


}
