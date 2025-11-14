/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.util;

import java.util.function.Supplier;
import necesse.engine.util.ValueWatcher;

public abstract class MultiValueWatcher {
    private final ValueWatcher<?>[] watchers;
    private boolean changed = false;

    public MultiValueWatcher(Supplier<?> ... getters) {
        this.watchers = new ValueWatcher[getters.length];
        for (int i = 0; i < getters.length; ++i) {
            this.watchers[i] = new ValueWatcher<Object>(getters[i]){

                @Override
                public void onChange(Object current) {
                    MultiValueWatcher.this.changed = true;
                }
            };
        }
    }

    public void update() {
        for (ValueWatcher<?> watcher : this.watchers) {
            watcher.update();
        }
        if (this.changed) {
            this.changed = false;
            this.onChange();
        }
    }

    public abstract void onChange();
}

