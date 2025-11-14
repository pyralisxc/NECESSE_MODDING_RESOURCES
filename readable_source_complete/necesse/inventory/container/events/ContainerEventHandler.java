/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.events;

import necesse.engine.util.GameLinkedList;
import necesse.inventory.container.events.ContainerEvent;

public abstract class ContainerEventHandler<T extends ContainerEvent> {
    private Class<T> eventClass;
    private GameLinkedList.Element element;
    private boolean isDisposed;

    public void init(Class<T> eventClass, GameLinkedList.Element element) {
        if (this.eventClass != null) {
            throw new IllegalStateException("Cannot initialize event handler twice");
        }
        this.eventClass = eventClass;
        this.element = element;
    }

    public abstract void handleEvent(T var1);

    public void dispose() {
        if (this.isDisposed) {
            return;
        }
        this.isDisposed = true;
        if (this.element != null) {
            this.element.remove();
        }
    }

    public boolean isDisposed() {
        return this.isDisposed;
    }

    public final void handleEventUntyped(ContainerEvent event) {
        if (this.eventClass == event.getClass()) {
            this.handleEvent(event);
        }
    }
}

