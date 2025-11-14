/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree.event;

import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Stream;
import necesse.engine.util.HashMapArrayList;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.event.AIEvent;
import necesse.entity.mobs.ai.behaviourTree.event.AIEventListener;

public class CustomAIEventHandler<T extends Mob> {
    private HashMapArrayList<String, AIEventListener<AIEvent>> listeners = new HashMapArrayList();
    private HashMapArrayList<String, AIEvent> lastEvents = new HashMapArrayList();

    public void addListener(String eventType, AIEventListener<AIEvent> listener) {
        Objects.requireNonNull(listener);
        this.listeners.add(eventType, listener);
    }

    public void clearListeners() {
        this.listeners.clearAll();
    }

    public void cleanListeners() {
        for (ArrayList list : this.listeners.values()) {
            list.removeIf(AIEventListener::disposed);
        }
    }

    public void submitEvent(String eventType, AIEvent event) {
        this.lastEvents.add(eventType, event);
        this.cleanListeners();
        this.listeners.stream(eventType).forEach(listener -> listener.onEvent(event));
    }

    public void clearLatestEvents() {
        this.lastEvents.clearAll();
    }

    public Iterable<AIEvent> getLastEvents(String eventType) {
        return (Iterable)this.lastEvents.get(eventType);
    }

    public Stream<AIEvent> streamLastEvents(String eventType) {
        return this.lastEvents.stream(eventType);
    }
}

