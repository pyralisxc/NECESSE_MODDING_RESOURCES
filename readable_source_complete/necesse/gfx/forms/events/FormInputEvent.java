/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.events;

import necesse.engine.input.InputEvent;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.events.FormEvent;

public class FormInputEvent<T extends FormComponent>
extends FormEvent<T> {
    public final InputEvent event;

    public FormInputEvent(T from, InputEvent event) {
        super(from);
        this.event = event;
    }
}

