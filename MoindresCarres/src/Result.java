/**
 * @author Clément Garbay
 */
public class Result implements Comparable<Result> {
    public final double a;
    public final double b;
    public final double c;
    public final double espilon;

    public Result(double a, double b, double c, double espilon) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.espilon = espilon;
    }

    @Override
    public int compareTo(Result o) {
        return Double.compare(this.espilon, o.espilon);
    }

    @Override
    public String toString() {
        return "Result{" +
                "a=" + a +
                ", b=" + b +
                ", c=" + c +
                ", espilon=" + espilon +
                '}';
    }
}