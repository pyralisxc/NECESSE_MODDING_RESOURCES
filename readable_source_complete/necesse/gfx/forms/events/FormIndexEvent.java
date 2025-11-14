/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.events;

import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.events.FormEvent;

public class FormIndexEvent<T extends FormComponent>
extends FormEvent<T> {
    public final int index;

    public FormIndexEvent(T from, int index) {
        super(from);
        this.index = index;
    }
}

