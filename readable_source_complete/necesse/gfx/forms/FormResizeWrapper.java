/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms;

import necesse.gfx.forms.components.FormComponent;

public class FormResizeWrapper {
    public final FormComponent component;
    public final Runnable resizeLogic;

    public FormResizeWrapper(FormComponent component, Runnable resizeLogic) {
        this.component = component;
        this.resizeLogic = resizeLogic;
    }
}

