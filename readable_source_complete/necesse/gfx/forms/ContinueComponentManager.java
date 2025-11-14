/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms;

import necesse.gfx.forms.components.ContinueComponent;

public interface ContinueComponentManager {
    public void addContinueForm(String var1, ContinueComponent var2);

    public void removeContinueForm(String var1);

    public boolean hasContinueForms();
}

