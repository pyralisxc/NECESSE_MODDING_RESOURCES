/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.util;

import java.util.Iterator;
import java.util.LinkedList;
import necesse.engine.util.GroundPillar;

public class GroundPillarList<T extends GroundPillar>
implements Iterable<T> {
    private LinkedList<T> pillars = new LinkedList();

    public void add(T pillar) {
        this.pillars.addFirst(pillar);
    }

    public void clean(long localTime, double currentDistance) {
        while (!this.pillars.isEmpty() && ((GroundPillar)this.pillars.getLast()).shouldRemove(localTime, currentDistance)) {
            this.pillars.removeLast();
        }
    }

    public void cleanThorough(long currentTime, double currentDistance) {
        this.pillars.removeIf(p -> p.shouldRemove(currentTime, currentDistance));
    }

    @Override
    public Iterator<T> iterator() {
        return this.pillars.iterator();
    }

    public int size() {
        return this.pillars.size();
    }

    public boolean isEmpty() {
        return this.pillars.isEmpty();
    }
}

