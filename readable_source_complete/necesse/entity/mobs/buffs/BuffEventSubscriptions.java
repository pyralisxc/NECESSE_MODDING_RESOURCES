/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.function.Consumer;
import necesse.engine.util.HashMapLinkedList;
import necesse.engine.util.HashMapSet;
import necesse.entity.mobs.MobGenericEvent;

public class BuffEventSubscriptions {
    private final HashMapSet<Integer, Class<? extends MobGenericEvent>> buffIDEventSubscriptions = new HashMapSet();
    private final HashMapLinkedList<Class<? extends MobGenericEvent>, BuffSubscription<?>> eventClassBuffSubscriptions = new HashMapLinkedList();

    public void removeSubscriptions(int buffID) {
        HashSet cleared = (HashSet)this.buffIDEventSubscriptions.clear(buffID);
        if (cleared == null) {
            return;
        }
        for (Class eventClasses : cleared) {
            LinkedList elements = (LinkedList)this.eventClassBuffSubscriptions.get(eventClasses);
            elements.removeIf(buffSubscription -> ((BuffSubscription)buffSubscription).buffID == buffID);
        }
    }

    public <T extends MobGenericEvent> void addSubscription(int buffID, Class<T> eventClass, Consumer<T> listener) {
        this.buffIDEventSubscriptions.add(buffID, eventClass);
        this.eventClassBuffSubscriptions.add(eventClass, new BuffSubscription<T>(buffID, listener));
    }

    public void submitEvent(MobGenericEvent event) {
        Class<?> eventClass = event.getClass();
        for (BuffSubscription subscription : (LinkedList)this.eventClassBuffSubscriptions.get(eventClass)) {
            subscription.eventListener.accept(event);
        }
    }

    public void clear() {
        this.buffIDEventSubscriptions.clearAll();
        this.eventClassBuffSubscriptions.clearAll();
    }

    private class BuffSubscription<T extends MobGenericEvent> {
        private int buffID;
        private Consumer<T> eventListener;

        public BuffSubscription(int buffID, Consumer<T> eventListener) {
            this.buffID = buffID;
            this.eventListener = eventListener;
        }
    }
}

