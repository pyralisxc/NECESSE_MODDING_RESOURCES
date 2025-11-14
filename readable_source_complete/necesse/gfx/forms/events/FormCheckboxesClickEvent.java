/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.events;

import necesse.engine.input.InputEvent;
import necesse.gfx.forms.components.FormCheckBox;
import necesse.gfx.forms.events.FormInputEvent;

public class FormCheckboxesClickEvent
extends FormInputEvent<FormCheckBox> {
    public final int checkboxIndex;

    public FormCheckboxesClickEvent(FormCheckBox from, InputEvent event, int checkboxIndex) {
        super(from, event);
        this.checkboxIndex = checkboxIndex;
    }
}

