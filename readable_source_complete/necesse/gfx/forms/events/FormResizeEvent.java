/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.events;

import necesse.engine.input.InputEvent;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.events.FormInputEvent;

public class FormResizeEvent<T extends FormComponent>
extends FormInputEvent<T> {
    public int x;
    public int y;
    public int width;
    public int height;

    public FormResizeEvent(T from, InputEvent event, int x, int y, int width, int height) {
        super(from, event);
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
}

