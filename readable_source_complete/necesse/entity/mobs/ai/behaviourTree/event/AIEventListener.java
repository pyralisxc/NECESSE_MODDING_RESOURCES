/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree.event;

import necesse.entity.mobs.ai.behaviourTree.event.AIEvent;

@FunctionalInterface
public interface AIEventListener<T extends AIEvent> {
    public void onEvent(T var1);

    default public boolean disposed() {
        return false;
    }
}

