/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.util.gameAreaSearch;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import necesse.engine.util.gameAreaSearch.GameAreaSearch;
import necesse.engine.util.gameAreaSearch.GameAreaSink;
import necesse.engine.util.gameAreaSearch.GameAreaStream;

public class GameAreaPipeline<T_FIRST, T_IN, T_OUT>
implements GameAreaStream<T_OUT> {
    private final GameAreaSearch<T_FIRST> searcher;
    private final GameAreaPipeline<T_FIRST, T_FIRST, ?> firstStage;
    private final GameAreaPipeline<T_FIRST, ?, ?> prevStage;
    private GameAreaPipeline<T_FIRST, T_OUT, ?> nextStage;

    private GameAreaPipeline(GameAreaSearch<T_FIRST> searcher, GameAreaPipeline<T_FIRST, T_FIRST, ?> first, GameAreaPipeline<T_FIRST, ?, ?> prevStage) {
        this.searcher = searcher;
        this.firstStage = first;
        this.prevStage = prevStage;
    }

    public GameAreaPipeline(GameAreaSearch<T_FIRST> searcher) {
        this.searcher = searcher;
        this.firstStage = this;
        this.prevStage = null;
    }

    private <R> GameAreaStream<R> next(GameAreaPipeline<T_FIRST, T_OUT, R> next) {
        this.nextStage = next;
        return next;
    }

    @Override
    public GameAreaStream<T_OUT> filter(final Predicate<? super T_OUT> predicate) {
        Objects.requireNonNull(predicate);
        return this.next(new GameAreaPipeline<T_FIRST, T_OUT, T_OUT>(this.searcher, this.firstStage, this){

            @Override
            protected void handle(GameAreaSink<T_OUT> sink, T_OUT element) {
                if (predicate.test(element)) {
                    sink.accept(element);
                }
            }
        });
    }

    @Override
    public GameAreaStream<T_OUT> distinct() {
        final HashSet handled = new HashSet();
        return this.next(new GameAreaPipeline<T_FIRST, T_OUT, T_OUT>(this.searcher, this.firstStage, this){

            @Override
            protected void handle(GameAreaSink<T_OUT> sink, T_OUT element) {
                if (!handled.contains(element)) {
                    handled.add(element);
                    sink.accept(element);
                }
            }
        });
    }

    @Override
    public <R> GameAreaStream<R> map(final Function<? super T_OUT, ? extends R> mapper) {
        Objects.requireNonNull(mapper);
        return this.next(new GameAreaPipeline<T_FIRST, T_OUT, R>(this.searcher, this.firstStage, this){

            @Override
            protected void handle(GameAreaSink<R> sink, T_OUT element) {
                sink.accept(mapper.apply(element));
            }
        });
    }

    @Override
    public <R> GameAreaStream<R> flatMap(final Function<? super T_OUT, ? extends Iterable<? extends R>> mapper) {
        Objects.requireNonNull(mapper);
        return this.next(new GameAreaPipeline<T_FIRST, T_OUT, R>(this.searcher, this.firstStage, this){

            @Override
            protected void handle(GameAreaSink<R> sink, T_OUT element) {
                for (Object next : (Iterable)mapper.apply(element)) {
                    if (sink.isCancelled()) break;
                    sink.accept(next);
                }
            }
        });
    }

    @Override
    public long count() {
        AtomicLong counter = new AtomicLong(0L);
        this.forEach((Consumer<? super T_OUT>)((Consumer<Object>)out -> counter.addAndGet(1L)));
        return counter.get();
    }

    protected void handle(GameAreaSink<T_OUT> sink, T_IN element) {
    }

    protected void downStream(GameAreaSink<Object> sink, Object element) {
        if (this.nextStage == null) {
            sink.accept(element);
            return;
        }
        this.nextStage.next(sink, element);
    }

    protected void next(final GameAreaSink<Object> sink, Object current) {
        this.handle(new GameAreaSink<T_OUT>(){

            @Override
            public void cancel() {
                sink.cancel();
            }

            @Override
            public boolean isCancelled() {
                return sink.isCancelled();
            }

            @Override
            public void accept(T_OUT e) {
                if (GameAreaPipeline.this.nextStage != null) {
                    GameAreaPipeline.this.nextStage.next(sink, e);
                } else {
                    sink.accept(e);
                }
            }
        }, current);
    }

    protected void forEachProgress(final Predicate<? super T_OUT> progress) {
        final AtomicBoolean cancel = new AtomicBoolean();
        while (!this.searcher.isDone()) {
            GameAreaSearch.FoundElement<T_FIRST> found = this.searcher.next();
            if (found == null) continue;
            GameAreaSink<Object> sink = new GameAreaSink<Object>(){

                @Override
                public void accept(Object e) {
                    if (!progress.test(e)) {
                        cancel.set(true);
                        this.cancel();
                    }
                }
            };
            this.firstStage.downStream(sink, found.element);
            if (!cancel.get()) continue;
            break;
        }
    }

    @Override
    public void forEach(final Consumer<? super T_OUT> action) {
        while (!this.searcher.isDone()) {
            GameAreaSearch.FoundElement<T_FIRST> found = this.searcher.next();
            if (found == null) continue;
            GameAreaSink<Object> sink = new GameAreaSink<Object>(){

                @Override
                public void accept(Object e) {
                    action.accept(e);
                }
            };
            this.firstStage.downStream(sink, found.element);
        }
    }

    @Override
    public boolean anyMatch(Predicate<? super T_OUT> predicate) {
        AtomicBoolean matched = new AtomicBoolean();
        this.forEachProgress(t_out -> {
            if (predicate.test((Object)t_out)) {
                matched.set(true);
                return false;
            }
            return true;
        });
        return matched.get();
    }

    @Override
    public boolean allMatch(Predicate<? super T_OUT> predicate) {
        AtomicBoolean allMatched = new AtomicBoolean(true);
        this.forEachProgress(t_out -> {
            if (!predicate.test((Object)t_out)) {
                allMatched.set(false);
                return false;
            }
            return true;
        });
        return allMatched.get();
    }

    @Override
    public boolean noneMatch(Predicate<? super T_OUT> predicate) {
        return !this.anyMatch(predicate);
    }

    @Override
    public Optional<T_OUT> findFirst() {
        AtomicReference ref = new AtomicReference(Optional.empty());
        this.forEachProgress(t_out -> {
            ref.set(Optional.of(t_out));
            return false;
        });
        return ref.get();
    }

    @Override
    public <R, A> R findExtraDistance(final int extraDistance, final Collector<? super T_OUT, A, R> collector) {
        final A container = collector.supplier().get();
        final AtomicBoolean maxSet = new AtomicBoolean(false);
        while (!this.searcher.isDone()) {
            final GameAreaSearch.FoundElement<T_FIRST> found = this.searcher.next();
            if (found == null) continue;
            GameAreaSink<Object> sink = new GameAreaSink<Object>(){

                @Override
                public void accept(Object e) {
                    collector.accumulator().accept(container, e);
                    if (!maxSet.get()) {
                        if (extraDistance != Integer.MAX_VALUE) {
                            GameAreaPipeline.this.searcher.setMaxDistance(Math.min(GameAreaPipeline.this.searcher.getMaxDistance(), found.distance + extraDistance));
                        }
                        maxSet.set(true);
                    }
                }
            };
            this.firstStage.downStream(sink, found.element);
        }
        return collector.finisher().apply(container);
    }

    @Override
    public TreeSet<T_OUT> findExtraDistanceSorted(int extraDistance, Comparator<? super T_OUT> comparator) {
        return this.findExtraDistance(extraDistance, Collectors.toCollection(() -> new TreeSet(comparator)));
    }

    @Override
    public Optional<T_OUT> findBestDistance(int extraDistance, Comparator<? super T_OUT> comparator) {
        TreeSet<T_OUT> found = this.findExtraDistanceSorted(extraDistance, comparator);
        if (!found.isEmpty()) {
            return Optional.of(found.first());
        }
        return Optional.empty();
    }

    @Override
    public <R, A> R findExtraItems(final int extraItems, final Collector<? super T_OUT, A, R> collector) {
        final A container = collector.supplier().get();
        final AtomicInteger counter = new AtomicInteger();
        while (!this.searcher.isDone()) {
            GameAreaSearch.FoundElement<T_FIRST> found = this.searcher.next();
            if (found == null) continue;
            GameAreaSink<Object> sink = new GameAreaSink<Object>(){

                @Override
                public void accept(Object e) {
                    collector.accumulator().accept(container, e);
                    if (counter.addAndGet(1) > extraItems) {
                        this.cancel();
                    }
                }
            };
            this.firstStage.downStream(sink, found.element);
        }
        return collector.finisher().apply(container);
    }

    @Override
    public TreeSet<T_OUT> findExtraItemsSorted(int extraItems, Comparator<? super T_OUT> comparator) {
        return this.findExtraItems(extraItems, Collectors.toCollection(() -> new TreeSet(comparator)));
    }

    @Override
    public Optional<T_OUT> findBestItems(int extraItems, Comparator<? super T_OUT> comparator) {
        TreeSet<T_OUT> found = this.findExtraItemsSorted(extraItems, comparator);
        if (!found.isEmpty()) {
            return Optional.of(found.first());
        }
        return Optional.empty();
    }
}

