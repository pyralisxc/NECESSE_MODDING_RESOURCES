/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.events;

import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.events.FormEvent;

public class FormStringEvent<T extends FormComponent>
extends FormEvent<T> {
    public final String str;

    public FormStringEvent(T from, String str) {
        super(from);
        this.str = str;
    }

    public boolean equals(String str) {
        return this.str.equals(str);
    }

    public boolean equals(FormStringEvent event) {
        if (this == event) {
            return true;
        }
        return this.equals(event.str) && this.from == event.from;
    }
}

