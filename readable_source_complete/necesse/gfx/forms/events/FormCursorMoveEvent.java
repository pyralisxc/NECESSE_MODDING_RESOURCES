/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.events;

import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.events.FormEvent;

public class FormCursorMoveEvent<T extends FormComponent>
extends FormEvent<T> {
    public final boolean causedByMouse;

    public FormCursorMoveEvent(T from, boolean causedByMouse) {
        super(from);
        this.causedByMouse = causedByMouse;
    }
}

