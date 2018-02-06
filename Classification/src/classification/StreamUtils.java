package classification;

import java.util.Collection;
import java.util.HashMap;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * @author Clément Garbay
 */
public class StreamUtils {
    public static <T, R> Stream<R> map(Collection<T> collection, Function<? super T, ? extends R> mapper) {
        return collection.stream().map(mapper);
    }

    public static <T> boolean some(Collection<T> collection, Function<? super T, Boolean> mapper) {
        return map(collection, mapper).reduce(Boolean::logicalOr).get();
    }

    public static Vecteur[] toArray(Stream<Vecteur> vecteurStream) {
        return vecteurStream.toArray(Vecteur[]::new);
    }

    public static <K,V> HashMap.SimpleEntry<K,V> toEntry(K key, V value) {
        return new HashMap.SimpleEntry<>(key, value);
    }
}
