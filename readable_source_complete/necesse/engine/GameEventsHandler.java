/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine;

import necesse.engine.GameEventInterface;
import necesse.engine.util.GameLinkedList;

public class GameEventsHandler<T> {
    private GameLinkedList<GameEventInterface<T>> listeners = new GameLinkedList();
    private boolean autoClean;

    public GameEventsHandler(boolean autoClean) {
        this.autoClean = autoClean;
    }

    public <R extends GameEventInterface<T>> R addListener(R listener) {
        if (this.autoClean) {
            this.cleanListeners();
        }
        GameLinkedList.Element e = this.listeners.addLast(listener);
        listener.init(() -> {
            if (!e.isRemoved()) {
                e.remove();
            }
        });
        return listener;
    }

    public void triggerEvent(T event) {
        for (GameLinkedList.Element e : this.listeners.elements()) {
            if (((GameEventInterface)e.object).isDisposed()) {
                e.remove();
                continue;
            }
            ((GameEventInterface)e.object).onEvent(event);
        }
    }

    public void cleanListeners() {
        for (GameLinkedList.Element e : this.listeners.elements()) {
            if (!((GameEventInterface)e.object).isDisposed()) continue;
            e.remove();
        }
    }

    public int getListenerCount() {
        return this.listeners.size();
    }
}

