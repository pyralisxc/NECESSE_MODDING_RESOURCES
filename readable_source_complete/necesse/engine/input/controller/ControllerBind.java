/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.input.controller;

import necesse.engine.input.controller.ControllerHandle;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.gfx.gameTexture.GameTexture;

public abstract class ControllerBind {
    public abstract int hashCode();

    public abstract boolean equals(Object var1);

    public abstract boolean isBound();

    public abstract void saveBind(SaveData var1);

    public abstract void loadBind(LoadData var1);

    public abstract GameTexture getGlyph(ControllerHandle var1);
}

