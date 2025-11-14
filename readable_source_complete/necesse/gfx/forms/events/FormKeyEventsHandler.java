/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.events;

import java.util.ArrayList;
import necesse.gfx.forms.events.FormEvent;
import necesse.gfx.forms.events.FormEventListener;

public class FormKeyEventsHandler<T extends FormEvent> {
    private ArrayList<StringEventListener<T>> listeners = new ArrayList();

    public void addListener(String key, FormEventListener<T> listener) {
        this.listeners.add(new StringEventListener<T>(key, listener));
    }

    public void clearListeners() {
        this.listeners.clear();
    }

    public void onEvent(String key, T event) {
        this.listeners.stream().filter(e -> e.key.equals(key)).forEach(e -> e.listener.onEvent(event));
    }

    private class StringEventListener<S extends FormEvent> {
        public final String key;
        public final FormEventListener<S> listener;

        public StringEventListener(String key, FormEventListener<S> listener) {
            this.key = key;
            this.listener = listener;
        }
    }
}

