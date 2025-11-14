/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.util.gameAreaSearch;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Optional;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Stream;
import necesse.engine.util.gameAreaSearch.EmptyGameAreaSearch;
import necesse.engine.util.gameAreaSearch.GameAreaPipeline;

public interface GameAreaStream<T> {
    public GameAreaStream<T> filter(Predicate<? super T> var1);

    public GameAreaStream<T> distinct();

    public <R> GameAreaStream<R> map(Function<? super T, ? extends R> var1);

    public <R> GameAreaStream<R> flatMap(Function<? super T, ? extends Iterable<? extends R>> var1);

    public long count();

    default public <R> GameAreaStream<R> flatStream(Function<? super T, Stream<? extends R>> mapper) {
        return this.flatMap(s -> {
            Iterator iterator = ((Stream)mapper.apply(s)).iterator();
            return () -> iterator;
        });
    }

    public void forEach(Consumer<? super T> var1);

    public boolean anyMatch(Predicate<? super T> var1);

    public boolean allMatch(Predicate<? super T> var1);

    public boolean noneMatch(Predicate<? super T> var1);

    public Optional<T> findFirst();

    default public <R, A> R collect(Collector<? super T, A, R> collector) {
        return this.findExtraDistance(Integer.MAX_VALUE, collector);
    }

    public <R, A> R findExtraDistance(int var1, Collector<? super T, A, R> var2);

    public TreeSet<T> findExtraDistanceSorted(int var1, Comparator<? super T> var2);

    public Optional<T> findBestDistance(int var1, Comparator<? super T> var2);

    public <R, A> R findExtraItems(int var1, Collector<? super T, A, R> var2);

    public TreeSet<T> findExtraItemsSorted(int var1, Comparator<? super T> var2);

    public Optional<T> findBestItems(int var1, Comparator<? super T> var2);

    public static <T> GameAreaStream<T> empty() {
        return new GameAreaPipeline(new EmptyGameAreaSearch());
    }
}

