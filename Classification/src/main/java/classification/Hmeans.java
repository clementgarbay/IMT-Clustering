package classification;

import Jama.Matrix;

import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static classification.StreamUtils.*;

public class Hmeans {

    public Vecteur[] barycentres;
    Matrix donnees;
    int[] classes; // classes[i] correspond à x[i]
    int nombreVecteurs;
    int nombreClasses;
    int dimension;

    public Hmeans(Matrix donnees, int nombreClasses) {
        this.nombreClasses = nombreClasses;
        this.donnees = donnees;
        this.nombreVecteurs = donnees.getRowDimension();
        this.dimension = donnees.getColumnDimension();
        this.classes = new int[nombreVecteurs];
        this.barycentres = new Vecteur[nombreClasses];

        run();
    }

    private void run() {
        initialise();

        while (changementClasses()) {
            calculBarycentres();
        }
    }

    private boolean changementClasses() {
        return some(getPartition(), partition -> some(partition, point ->
                setClasse(point, centreLePlusProche(vecteur(point)))));
    }

    private boolean setClasse(int point, int classe) {
        if (this.classes[point] != classe) {
            this.classes[point] = classe;
            return true;
        }
        return false;
    }

    private void initialise() {
        final Random random = new Random();

        // Calcul des classes aléatoirement (non vide)
        for (int i = 0; i < this.classes.length; i++) {
            this.classes[i] = random.nextInt(nombreClasses);
        }

        calculBarycentres();
    }

    private void calculBarycentres() {
        this.barycentres = toArray(getPartition().stream()
                .map(partition -> partition.stream().map(this::vecteur))
                .map(partitionVecteurs -> Vecteur.barycentre(toArray(partitionVecteurs))));
    }

    private int centreLePlusProche(Vecteur vecteur) {
        Stream<Map.Entry<Integer, Vecteur>> barycentresWithIndex = IntStream.range(0, barycentres.length)
                .mapToObj(i -> toEntry(i, barycentres[i]));

        Optional<Integer> index = barycentresWithIndex
                .map(simpleEntry -> toEntry(simpleEntry.getKey(), Vecteur.distanceCarre(vecteur, simpleEntry.getValue())))
                .min(Comparator.comparingDouble(AbstractMap.SimpleEntry::getValue))
                .map(AbstractMap.SimpleEntry::getKey);

        return index.orElseThrow(() -> new NoSuchElementException("Impossible de trouver le centre le plus proche"));
    }

    public int[] getClasses() {
        return this.classes;
    }

    public ArrayList<HashSet<Integer>> getPartition() {
        ArrayList<HashSet<Integer>> r = new ArrayList<>();
        for (int i = 0; i < nombreClasses; i++) r.add(new HashSet<>());
        for (int i = 0; i < classes.length; i++) {
            r.get(classes[i]).add(i);
        }
        return r;
    }

    public boolean estCentre(int n) {
        Vecteur vect = vecteur(n);
        for (int i = 0; i < nombreClasses; i++) {
            if (vect.equals(barycentres[i])) return true;
        }
        return false;
    }

    public int[] getCouleurClasse(int k) {
        int[] res = new int[dimension];
        for (int i = 0; i < dimension; i++) {
            res[i] = (int) barycentres[k].get(i);
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
}
