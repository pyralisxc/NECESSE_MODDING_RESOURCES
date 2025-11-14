/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.events;

import necesse.inventory.container.events.ContainerEvent;

public abstract class ContainerEventSubscription<T extends ContainerEvent> {
    public final Class<T> eventClass;

    public ContainerEventSubscription(Class<T> eventClass) {
        this.eventClass = eventClass;
    }

    public abstract boolean shouldReceiveEvent(T var1);

    public final boolean testUntypedEvent(ContainerEvent event) {
        return this.eventClass == event.getClass() && this.shouldReceiveEvent(event);
    }

    public abstract boolean isActive();
}

