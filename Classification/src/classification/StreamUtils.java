package classification;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * @author Clément Garbay
 */
class StreamUtils {
    static <T, R> Stream<R> map(Collection<T> collection, Function<? super T, ? extends R> mapper) {
        return collection.stream().map(mapper);
    }

    static <T> boolean some(Collection<T> collection, Function<? super T, Boolean> mapper) {
        return map(collection, mapper).reduce(Boolean::logicalOr).get();
    }

    static Vecteur[] toArray(Stream<Vecteur> vecteurStream) {
        return vecteurStream.toArray(Vecteur[]::new);
    }

    static <K,V> HashMap.SimpleEntry<K,V> toEntry(K key, V value) {
        return new HashMap.SimpleEntry<>(key, value);
    }

    static List<Integer> range(int from, int to) {
        return IntStream.range(from, to).boxed().collect(toList());
    }
}
