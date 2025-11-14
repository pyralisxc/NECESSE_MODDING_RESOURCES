/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.ui;

import necesse.gfx.drawOptions.DrawOptions;

public interface ControllerInteractTarget {
    public void runInteract();

    public DrawOptions getDrawOptions();

    public void onCurrentlyFocused();
}

