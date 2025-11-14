/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.events;

import necesse.gfx.forms.events.FormEvent;

@FunctionalInterface
public interface FormEventListener<T extends FormEvent> {
    public void onEvent(T var1);

    default public boolean disposed() {
        return false;
    }
}

