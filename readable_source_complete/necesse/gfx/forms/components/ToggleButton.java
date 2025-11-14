/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components;

import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.events.FormEventListener;
import necesse.gfx.forms.events.FormInputEvent;

public interface ToggleButton<T extends FormComponent> {
    public boolean isToggled();

    public void setToggled(boolean var1);

    public void reset();

    public T onToggled(FormEventListener<FormInputEvent<T>> var1);
}

