/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components.lists;

import necesse.engine.input.InputEvent;
import necesse.engine.input.controller.ControllerEvent;
import necesse.gfx.forms.Form;

public interface FormControlListPopulator {
    public void populateForm(Form var1, int var2, int var3, int var4, int var5, Runnable var6);

    public void handleInputEvent(InputEvent var1, Form var2, Runnable var3);

    public void handleControllerEvent(ControllerEvent var1, Form var2, Runnable var3);

    public void runOnBindChanged(Runnable var1);
}

