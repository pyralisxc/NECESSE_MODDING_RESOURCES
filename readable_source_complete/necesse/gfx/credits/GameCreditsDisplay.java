/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.credits;

import java.awt.Dimension;

public abstract class GameCreditsDisplay {
    public abstract int initDrawAndGetTotalTimeShown();

    public abstract Dimension getDrawBounds();

    public abstract void draw(int var1, int var2, int var3, float var4);

    public abstract boolean isDone(int var1);
}

