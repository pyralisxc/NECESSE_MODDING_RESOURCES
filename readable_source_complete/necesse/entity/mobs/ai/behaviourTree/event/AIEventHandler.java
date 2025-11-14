/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree.event;

import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Stream;
import necesse.entity.mobs.ai.behaviourTree.event.AIEvent;
import necesse.entity.mobs.ai.behaviourTree.event.AIEventListener;

public class AIEventHandler<T extends AIEvent> {
    private ArrayList<AIEventListener<T>> listeners = new ArrayList();
    private ArrayList<T> lastEvents = new ArrayList();

    public void addListener(AIEventListener<T> listener) {
        Objects.requireNonNull(listener);
        this.listeners.add(listener);
    }

    public void clearListeners() {
        this.listeners.clear();
    }

    public void cleanListeners() {
        this.listeners.removeIf(AIEventListener::disposed);
    }

    public void submitEvent(T event) {
        this.lastEvents.add(event);
        this.cleanListeners();
        for (AIEventListener<T> e : this.listeners) {
            e.onEvent(event);
        }
    }

    public void clearLatestEvents() {
        this.lastEvents.clear();
    }

    public Iterable<T> getLastEvents() {
        return this.lastEvents;
    }

    public Stream<T> streamLastEvents() {
        return this.lastEvents.stream();
    }
}

