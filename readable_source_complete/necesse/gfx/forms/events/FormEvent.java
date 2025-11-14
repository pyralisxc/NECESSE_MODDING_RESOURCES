/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.events;

import necesse.gfx.forms.components.FormComponent;

public class FormEvent<T extends FormComponent> {
    private boolean preventDefault;
    public final T from;

    public FormEvent(T from) {
        this.from = from;
    }

    public final void preventDefault() {
        this.preventDefault = true;
    }

    public boolean hasPreventedDefault() {
        return this.preventDefault;
    }
}

