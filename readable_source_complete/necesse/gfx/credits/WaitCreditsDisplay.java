/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.credits;

import java.awt.Dimension;
import necesse.gfx.credits.GameCreditsDisplay;

public class WaitCreditsDisplay
extends GameCreditsDisplay {
    public int waitTime;
    public Dimension dimension;

    public WaitCreditsDisplay(int waitTime, int width, int height) {
        this.waitTime = waitTime;
        this.dimension = new Dimension(width, height);
    }

    public WaitCreditsDisplay(int waitTime) {
        this(waitTime, 0, 0);
    }

    @Override
    public int initDrawAndGetTotalTimeShown() {
        return this.waitTime;
    }

    @Override
    public Dimension getDrawBounds() {
        return this.dimension;
    }

    @Override
    public void draw(int currentTime, int x, int y, float alpha) {
    }

    @Override
    public boolean isDone(int currentTime) {
        return currentTime >= this.waitTime;
    }
}

