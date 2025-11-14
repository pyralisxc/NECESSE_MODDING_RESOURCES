/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.events;

import necesse.engine.input.InputEvent;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.events.FormInputEvent;

public class FormDraggingEvent<T extends FormComponent>
extends FormInputEvent<T> {
    public final InputEvent draggingStartedEvent;

    public FormDraggingEvent(T from, InputEvent event, InputEvent draggingStartedEvent) {
        super(from, event);
        this.draggingStartedEvent = draggingStartedEvent;
    }
}

