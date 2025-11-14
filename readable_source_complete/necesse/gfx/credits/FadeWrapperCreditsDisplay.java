/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.credits;

import java.awt.Dimension;
import necesse.engine.util.GameMath;
import necesse.gfx.credits.GameCreditsDisplay;

public class FadeWrapperCreditsDisplay
extends GameCreditsDisplay {
    public GameCreditsDisplay display;
    public int fadeInTime;
    public int fadeOutTime;
    protected int displayTime;

    public FadeWrapperCreditsDisplay(GameCreditsDisplay display, int fadeInTime, int fadeOutTime) {
        this.display = display;
        this.fadeInTime = fadeInTime;
        this.fadeOutTime = fadeOutTime;
    }

    @Override
    public int initDrawAndGetTotalTimeShown() {
        this.displayTime = this.display.initDrawAndGetTotalTimeShown();
        return this.fadeInTime + this.displayTime + this.fadeOutTime;
    }

    @Override
    public Dimension getDrawBounds() {
        return this.display.getDrawBounds();
    }

    @Override
    public void draw(int currentTime, int x, int y, float alpha) {
        if (this.fadeInTime > 0 && currentTime < this.fadeInTime) {
            float fadeInProgress = (float)currentTime / (float)this.fadeInTime;
            alpha *= fadeInProgress;
        } else if (this.fadeOutTime > 0 && currentTime > this.fadeInTime + this.displayTime) {
            float fadeOutProgress = (float)(currentTime - (this.fadeInTime + this.displayTime)) / (float)this.fadeOutTime;
            alpha *= 1.0f - fadeOutProgress;
        }
        this.display.draw(GameMath.limit(currentTime - this.fadeInTime, 0, this.displayTime - 1), x, y, alpha);
    }

    @Override
    public boolean isDone(int currentTime) {
        return currentTime >= this.fadeInTime + this.displayTime + this.fadeOutTime;
    }
}

