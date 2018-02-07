package classification;

import Jama.Matrix;

import java.util.*;
import java.util.stream.IntStream;

import static classification.StreamUtils.*;
import static java.util.AbstractMap.SimpleEntry;

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

        Vecteur[] vecteurs = toArray(map(range(0, classes.length), this::vecteur));
        double inertieInitiale = Vecteur.inertie(vecteurs, getPartition());

        System.out.println("Intertie initiale : " + inertieInitiale);

        while (
            changementClasses()
                .map(delta -> this.totalVariances += delta)
                .isPresent()
        ) ;

        System.out.println("Intertie finale : " + (inertieInitiale + this.totalVariances));
    }

    private void initialise() {
        final Random random = new Random();

        // Calcul des classes aléatoirement (non vide)
        for (int i = 0; i < this.classes.length; i++) {
            int classe = random.nextInt(nombreClasses); // i % nombreClasses;
            this.classes[i] = classe;
            tailles[classe]++;
        }

        calculBarycentres();
    }

    private void calculBarycentres() {
        this.centres = toArray(getPartition().stream()
                .map(partition -> partition.stream().map(this::vecteur))
                .map(partitionVecteurs -> Vecteur.barycentre(toArray(partitionVecteurs))));
    }

//    private boolean changementClasses() {
//        return some(range(0, classes.length), this::affectation);
//    }

    /**
     * Pour chaque points, effectue les changements de classe si nécessaire.
     *
     * @return La somme des delta variance locaux de chaque changement, ou rien si aucun changement n'a été effectué
     */
    private Optional<Double> changementClasses() {
        return range(0, classes.length).stream()
                .map(this::affectation)
                .reduce(Optional.empty(), StreamUtils::add);
    }

    /**
     * Affectue une nouvelle affectation de classe à un point si nécessaire.
     *
     * @param i L'indice du point concerné
     * @return La valeur du delta variance qui a permis de choisir la nouvelle classe, ou rien si aucun changement n'a été effectué
     */
    private Optional<Double> affectation(int i) {
        Vecteur xI = vecteur(i);
        int classeI = classes[i];

        SimpleEntry<Integer, Double> nouvelleClasseEtDelta = IntStream.range(0, nombreClasses).boxed()
                .map(classe -> {
                    if (classeI == classe) return toEntry(classe, 0.0);
                    return toEntry(classe, deltaVariance(xI, classeI, classe));
                }) // calcul le delta pour chaque classe
                .min(Comparator.comparingDouble(AbstractMap.SimpleEntry::getValue)) // prend le plus delta
                .map(min -> (min.getValue() < 0) ? min : toEntry(classeI, 0.0)) // r�cup�re la classe associ� au plus petit delta (i.e. nouvelle classe) ou la m�me classe si delta non n�gatif
                .get();

        int nouvelleClasseI = nouvelleClasseEtDelta.getKey();
        double delta = nouvelleClasseEtDelta.getValue();

        if (classeI == nouvelleClasseI) return Optional.empty(); // pas de changement de classe

        Vecteur baryIP = barycentreWithoutI(i);
        Vecteur baryKP = barycentreClasseWithI(i, nouvelleClasseI);

        centres[classeI] = baryIP; // nouveau barycentre de i
        centres[nouvelleClasseI] = baryKP; // nouveau barycentre de k

        classes[i] = nouvelleClasseI; // la classe de i devient k

        tailles[classeI] = tailles[classeI] - 1;
        tailles[nouvelleClasseI] = tailles[nouvelleClasseI] + 1;

        return Optional.of(delta); // il y a eu un changement de classe
    }

    // Slide 19
    private Vecteur barycentreWithoutI(int pointI) {
        int classeI = classes[pointI];
        Vecteur oldBarycentreI = centres[classeI]; // bary[i]
        Vecteur xI = vecteur(pointI); // x[i]
        double nI = tailles[classeI]; // n[i]
        double coef = 1. / (nI - 1.);

        return Vecteur.plus(oldBarycentreI, Vecteur.mult(Vecteur.moins(oldBarycentreI, xI), coef));
    }

    // Slide 19
    private Vecteur barycentreClasseWithI(int pointI, int classeK) {
        Vecteur oldBarycentreK = centres[classeK]; // bary[k]
        Vecteur xI = vecteur(pointI); // x[i]
        double nK = tailles[classeK]; // n[k]
        double coef = 1. / (nK + 1.);

        return Vecteur.plus(oldBarycentreK, Vecteur.mult(Vecteur.moins(xI, oldBarycentreK), coef));
    }

    private double deltaVariance(Vecteur v, int classeAvant, int classeApres) {
        return (double) (tailles[classeApres]) / (double) (tailles[classeApres] + 1) * Vecteur.distanceCarre(centres[classeApres], v)
                - (double) tailles[classeAvant] / (double) (tailles[classeAvant] - 1) * Vecteur.distanceCarre(centres[classeAvant], v);
    }

    private double ajoutVariance(Vecteur v, int classe) {
        return (double) tailles[classe] / (double) (tailles[classe] + 1) * Vecteur.distanceCarre(centres[classe], v);
    }

    private void initialiseCentres() {
        /* A compl�ter */
        for (int i = 0; i < nombreClasses; i++) {
            int n;
            do {
                n = (int) (Math.random() * nombreVecteurs);
            }
            while (estCentre(n));
            centres[i] = vecteur(n);
        }
    }

    private void calculCentres() {
		/* A compl�ter */
        centres = new Vecteur[nombreClasses];
        for (int i = 0; i < nombreClasses; i++) {
            centres[i] = new Vecteur(dimension);
        }
        int[] nb = new int[nombreClasses];
        for (int i = 0; i < nombreVecteurs; i++) {
            centres[classes[i]].plus(vecteur(i));
            nb[classes[i]]++;
        }
        for (int i = 0; i < nombreClasses; i++) {
            if (nb[classes[i]] == 0) throw new Error("Cas dégénéré : classe " + i + " vide.");
            centres[i].div(nb[i]);
        }
    }

    public int[] getClasses() {
        return this.classes;
    }

    public boolean estCentre(int n) {
        Vecteur vect = vecteur(n);
        for (int i = 0; i < nombreClasses; i++) {
            if (vect.equals(centres[i])) return true;
        }
        return false;
    }


    public int[] getCouleurClasse(int k) {
        int[] res = new int[dimension];
        for (int i = 0; i < dimension; i++) {
            res[i] = (int) centres[k].get(i);
        }
        return res;
    }

    public Vecteur vecteur(int n) {
        double[] result = new double[dimension];
        for (int i = 0; i < dimension; i++) {
            result[i] = donnees.get(n, i);
        }
        return new Vecteur(result);
    }


    private int centreLePlusProche(Vecteur vecteur) {
		/* A compl�ter */
        int nbCentre = centres.length;
        int classe = 0;
        double min = Vecteur.distance(vecteur, centres[0]);
        for (int i = 1; i < nbCentre; i++) {
            if (Vecteur.distance(vecteur, centres[i]) < min) {
                classe = i;
                min = Vecteur.distance(vecteur, centres[i]);
            }
        }
        return classe;
    }

    public ArrayList<HashSet<Integer>> getPartition() {
        ArrayList<HashSet<Integer>> r = new ArrayList<>();
        for (int i = 0; i < nombreClasses; i++) r.add(new HashSet<>());
        for (int i = 0; i < classes.length; i++) {
            r.get(classes[i]).add(i);
        }
        return r;
    }


}
