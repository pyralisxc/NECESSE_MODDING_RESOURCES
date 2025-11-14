/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.events;

import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.events.FormStringEvent;

public class FormStringIndexEvent<T extends FormComponent>
extends FormStringEvent<T> {
    public final int index;

    public FormStringIndexEvent(T from, String str, int index) {
        super(from, str);
        this.index = index;
    }
}

