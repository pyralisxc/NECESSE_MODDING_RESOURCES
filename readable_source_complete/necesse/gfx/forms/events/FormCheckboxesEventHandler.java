/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.events;

import necesse.gfx.forms.components.FormCheckBox;
import necesse.gfx.forms.events.FormCheckboxesClickEvent;
import necesse.gfx.forms.events.FormEventListener;
import necesse.gfx.forms.events.FormInputEvent;

public class FormCheckboxesEventHandler {
    public final FormCheckBox[] checkBoxes;

    public FormCheckboxesEventHandler(FormCheckBox[] checkBoxes) {
        this.checkBoxes = checkBoxes;
    }

    public FormCheckboxesEventHandler onClicked(FormEventListener<FormCheckboxesClickEvent> listener) {
        for (int i = 0; i < this.checkBoxes.length; ++i) {
            int finalIndex = i;
            this.checkBoxes[i].onClicked((FormInputEvent<FormCheckBox> e) -> {
                FormCheckboxesClickEvent newEvent = new FormCheckboxesClickEvent((FormCheckBox)e.from, e.event, finalIndex);
                listener.onEvent(newEvent);
                if (newEvent.hasPreventedDefault()) {
                    e.preventDefault();
                }
            });
        }
        return this;
    }

    public boolean[] getStates() {
        boolean[] states = new boolean[this.checkBoxes.length];
        for (int i = 0; i < this.checkBoxes.length; ++i) {
            states[i] = this.checkBoxes[i].checked;
        }
        return states;
    }
}

