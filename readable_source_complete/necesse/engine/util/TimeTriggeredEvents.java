/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.util;

import necesse.engine.util.GameLinkedList;

public class TimeTriggeredEvents {
    protected GameLinkedList<Event> events = new GameLinkedList();

    public TimeTriggeredEvents addEvent(int time, Runnable action) {
        Event e = new Event(time, action);
        for (GameLinkedList.Element el : this.events.elements()) {
            if (((Event)el.object).time <= time) continue;
            el.insertBefore(e);
            return this;
        }
        this.events.addLast(e);
        return this;
    }

    public boolean tickEvents(long timeSinceStart) {
        while (!this.events.isEmpty()) {
            GameLinkedList.Element firstEl = this.events.getFirstElement();
            if (timeSinceStart < (long)((Event)firstEl.object).time) break;
            ((Event)firstEl.object).action.run();
            firstEl.remove();
        }
        return !this.events.isEmpty();
    }

    protected class Event {
        public final int time;
        public final Runnable action;

        public Event(int time, Runnable action) {
            this.time = time;
            this.action = action;
        }
    }
}

