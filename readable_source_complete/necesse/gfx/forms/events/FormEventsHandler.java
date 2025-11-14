/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.events;

import java.util.ArrayList;
import java.util.Objects;
import necesse.gfx.forms.events.FormEvent;
import necesse.gfx.forms.events.FormEventListener;

public class FormEventsHandler<T extends FormEvent> {
    private ArrayList<FormEventListener<T>> listeners = new ArrayList();

    public void addListener(FormEventListener<T> listener) {
        Objects.requireNonNull(listener);
        this.listeners.add(listener);
    }

    public boolean hasListeners() {
        return !this.listeners.isEmpty();
    }

    public void clearListeners() {
        this.listeners.clear();
    }

    public void onEvent(T event) {
        this.listeners.removeIf(FormEventListener::disposed);
        for (FormEventListener<T> e : this.listeners) {
            e.onEvent(event);
        }
    }
}

