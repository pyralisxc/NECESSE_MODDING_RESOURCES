/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.modifiers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import necesse.engine.modifiers.Modifier;

public class ModifierList
implements Iterable<Modifier> {
    private final ArrayList<Modifier> modifiers = new ArrayList();
    private final HashMap<String, Integer> stringIDIndexes = new HashMap();

    public int getModifierCount() {
        return this.modifiers.size();
    }

    int addModifier(Modifier<?> modifier) {
        if (modifier.stringID != null && this.stringIDIndexes.containsKey(modifier.stringID)) {
            throw new IllegalArgumentException("Modifier with stringID " + modifier.stringID + " already exists");
        }
        int index = this.modifiers.size();
        this.modifiers.add(modifier);
        this.stringIDIndexes.put(modifier.stringID, index);
        return index;
    }

    public Modifier getModifier(int index) {
        return this.modifiers.get(index);
    }

    public Modifier getModifierByStringID(String stringID) {
        int index = this.stringIDIndexes.getOrDefault(stringID, -1);
        if (index == -1) {
            return null;
        }
        return this.getModifier(index);
    }

    @Override
    public Iterator<Modifier> iterator() {
        return this.modifiers.iterator();
    }

    @Override
    public void forEach(Consumer<? super Modifier> action) {
        this.modifiers.forEach((Consumer<Modifier>)action);
    }

    @Override
    public Spliterator<Modifier> spliterator() {
        throw new UnsupportedOperationException("Spliterator not supported for modifier list");
    }

    private static class CombinedIterator<T>
    implements Iterator<T> {
        private final Iterator<T>[] is;
        private int current;

        @SafeVarargs
        public CombinedIterator(Iterator<T> ... iterators) {
            this.is = iterators;
            this.current = 0;
        }

        private void peekNext() {
            while (this.current < this.is.length && !this.is[this.current].hasNext()) {
                ++this.current;
            }
        }

        @Override
        public boolean hasNext() {
            this.peekNext();
            return this.current < this.is.length;
        }

        @Override
        public T next() {
            this.peekNext();
            return this.is[this.current].next();
        }
    }
}

