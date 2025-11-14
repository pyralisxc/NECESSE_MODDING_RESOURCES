/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree;

import java.util.HashMap;
import java.util.stream.Stream;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.event.AIBeforeHitCalculatedEvent;
import necesse.entity.mobs.ai.behaviourTree.event.AIBeforeHitEvent;
import necesse.entity.mobs.ai.behaviourTree.event.AIEvent;
import necesse.entity.mobs.ai.behaviourTree.event.AIEventHandler;
import necesse.entity.mobs.ai.behaviourTree.event.AIEventListener;
import necesse.entity.mobs.ai.behaviourTree.event.AIWasHitEvent;
import necesse.entity.mobs.ai.behaviourTree.event.CustomAIEventHandler;
import necesse.entity.mobs.ai.behaviourTree.util.AIMover;

public class Blackboard<T extends Mob>
extends HashMap<String, Object> {
    protected CustomAIEventHandler<T> customEvents = new CustomAIEventHandler();
    protected AIEventHandler<AIEvent> globalTickEvents = new AIEventHandler();
    protected AIEventHandler<AIBeforeHitEvent> beforeHitEvents = new AIEventHandler();
    protected AIEventHandler<AIBeforeHitCalculatedEvent> beforeHitCalculatedEvents = new AIEventHandler();
    protected AIEventHandler<AIWasHitEvent> wasHitEvents = new AIEventHandler();
    protected AIEventHandler<AIEvent> removedEvents = new AIEventHandler();
    protected AIEventHandler<AIEvent> onUnloadingEvents = new AIEventHandler();
    public final AIMover mover;

    public Blackboard(AIMover mover) {
        this.mover = mover;
    }

    protected void clearLatestEvents() {
        this.globalTickEvents.clearLatestEvents();
        this.beforeHitEvents.clearLatestEvents();
        this.beforeHitCalculatedEvents.clearLatestEvents();
        this.wasHitEvents.clearLatestEvents();
        this.removedEvents.clearLatestEvents();
        this.customEvents.clearLatestEvents();
    }

    public <C> C getObject(Class<? extends C> expectedClass, String key) {
        return this.getObject(expectedClass, key, null);
    }

    public <C> C getObject(Class<? extends C> expectedClass, String key, C defaultObject) {
        try {
            return expectedClass.cast(this.getOrDefault(key, defaultObject));
        }
        catch (ClassCastException e) {
            return defaultObject;
        }
    }

    public <C> C getObjectNotNull(Class<? extends C> expectedClass, String key, C defaultObject) {
        C object = this.getObject(expectedClass, key, defaultObject);
        return object == null ? defaultObject : object;
    }

    public void onGlobalTick(AIEventListener<AIEvent> listener) {
        this.globalTickEvents.addListener(listener);
    }

    public void onBeforeHit(AIEventListener<AIBeforeHitEvent> listener) {
        this.beforeHitEvents.addListener(listener);
    }

    public void onBeforeHitCalculated(AIEventListener<AIBeforeHitCalculatedEvent> listener) {
        this.beforeHitCalculatedEvents.addListener(listener);
    }

    public void onWasHit(AIEventListener<AIWasHitEvent> listener) {
        this.wasHitEvents.addListener(listener);
    }

    public void onUnloading(AIEventListener<AIEvent> listener) {
        this.onUnloadingEvents.addListener(listener);
    }

    public void onRemoved(AIEventListener<AIEvent> listener) {
        this.removedEvents.addListener(listener);
    }

    public void onEvent(String eventType, AIEventListener<AIEvent> listener) {
        this.customEvents.addListener(eventType, listener);
    }

    public void submitEvent(String eventType, AIEvent event) {
        this.customEvents.submitEvent(eventType, event);
    }

    public Iterable<AIWasHitEvent> getLastHits() {
        return this.wasHitEvents.getLastEvents();
    }

    public Stream<AIWasHitEvent> streamLastHits() {
        return this.wasHitEvents.streamLastEvents();
    }

    public Iterable<AIEvent> getLastCustomEvents(String eventType) {
        return this.customEvents.getLastEvents(eventType);
    }

    public Stream<AIEvent> streamLastCustomEvents(String eventType) {
        return this.customEvents.streamLastEvents(eventType);
    }
}

