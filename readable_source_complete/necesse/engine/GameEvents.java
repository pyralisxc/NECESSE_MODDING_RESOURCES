/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import necesse.engine.GameEventInterface;
import necesse.engine.GameEventsHandler;
import necesse.engine.events.GameEvent;
import necesse.engine.events.PreventableGameEvent;

public class GameEvents {
    private static final HashMap<Class<? extends GameEvent>, GameEventsHandler<GameEvent>> handlers = new HashMap();

    public static <T extends GameEvent, R extends GameEventInterface<T>> R addListener(Class<T> eventClass, R listener) {
        GameEventsHandler handler = handlers.compute(eventClass, (aClass, cHandler) -> {
            if (cHandler == null) {
                cHandler = new GameEventsHandler(true);
            }
            return cHandler;
        });
        handler.addListener(listener);
        return listener;
    }

    public static <T extends GameEvent> void triggerEvent(T event) {
        handlers.computeIfPresent(event.getClass(), (aClass, handler) -> {
            handler.triggerEvent(event);
            return handler;
        });
    }

    public static <T extends PreventableGameEvent> void triggerEvent(T event, Consumer<T> runOnNotPrevented) {
        handlers.computeIfPresent(event.getClass(), (aClass, handler) -> {
            handler.triggerEvent(event);
            return handler;
        });
        if (!event.isPrevented()) {
            runOnNotPrevented.accept(event);
        }
    }

    public void cleanListeners() {
        handlers.values().forEach(GameEventsHandler::cleanListeners);
    }

    public <T extends GameEvent> void cleanListeners(Class<T> eventClass) {
        handlers.computeIfPresent(eventClass, (aClass, handler) -> {
            handler.cleanListeners();
            return handler;
        });
    }

    public static <T extends GameEvent> int getListenerCount(Class<T> eventClass) {
        AtomicInteger out = new AtomicInteger(0);
        handlers.computeIfPresent(eventClass, (aClass, handler) -> {
            out.set(handler.getListenerCount());
            return handler;
        });
        return out.get();
    }
}

