import Jama.Matrix;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Clément Garbay
 */
public class RechercheManuelle {

    public static void main(String[] args) {
        int min = -20;
        int max = 20;


        List<Result> res = IntStream.range(min, max).boxed().flatMap(a ->
            IntStream.range(min, max).boxed().flatMap(b ->
                IntStream.range(min, max).boxed().map(c ->
                    new Result((double) a, (double) b, (double) c, estimate((double) a, (double) b, (double) c)))))
                        .collect(Collectors.toList());

        /*for (int a = min; a < max; a++) {
            for (int b = min; b < max; b++) {
                for (int c = min; c < max; c++) {
                    res.add(new Result((double) a, (double) b, (double) c, estimate((double) a, (double) b, (double) c)));
                }
            }
        }*/

        res.sort(Result::compareTo);

        System.out.println(res.get(0));
    }

    public static double estimate(double a, double b, double c) {
        double[][] arrayA = {
            { 1, 3, 9 },
            { 1, 2, 4 },
            { 1, 4, 16 },
            { 1, 5, 25 },
            { 1, 9, 81 },
            { 1, 7, 49 },
            { 1, 1, 1 }
        };

        double[][] arrayB = {
            { 8.1 },
            { 4.25 },
            { 14.15 },
            { 22.05 },
            { 73.5 },
            { 43.85 },
            { 1.9 }
        };

        double[][] arrayX = {
            { a },
            { b },
            { c }
        };

        Matrix matrixA = new Matrix(arrayA);
        Matrix matrixB = new Matrix(arrayB);
        Matrix matrixX = new Matrix(arrayX);

        double espilon = matrixA.times(matrixX).minus(matrixB).norm2();

        return espilon;
    }
}
