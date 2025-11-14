/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.util;

import java.util.LinkedList;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class EventVariable<T> {
    public final boolean autoClean;
    private T var;
    private LinkedList<Listener> changedEvents = new LinkedList();

    public EventVariable(T defaultVar, boolean autoClean) {
        this.var = defaultVar;
        this.autoClean = autoClean;
    }

    public EventVariable(T defaultVar) {
        this(defaultVar, true);
    }

    public Listener addChangeListener(Consumer<T> onChange, Supplier<Boolean> isDisposed) {
        if (this.autoClean) {
            this.cleanListeners();
        }
        Listener out = new Listener(onChange, isDisposed);
        this.changedEvents.add(out);
        return out;
    }

    public void cleanListeners() {
        this.changedEvents.removeIf(l -> (Boolean)((Listener)l).isDisposed.get());
    }

    public int getTotalChangeListeners() {
        if (this.autoClean) {
            this.cleanListeners();
        }
        return this.changedEvents.size();
    }

    public void set(T var) {
        if (!Objects.equals(this.var, var)) {
            this.var = var;
            if (this.autoClean) {
                this.cleanListeners();
            }
            this.changedEvents.forEach(e -> ((Listener)e).onChange.accept(var));
        }
    }

    public T get() {
        return this.var;
    }

    public class Listener {
        private final Consumer<T> onChange;
        private final Supplier<Boolean> isDisposed;

        private Listener(Consumer<T> onChange, Supplier<Boolean> isDisposed) {
            this.onChange = onChange;
            this.isDisposed = isDisposed;
        }

        public void dispose() {
            EventVariable.this.changedEvents.remove(this);
        }
    }
}

