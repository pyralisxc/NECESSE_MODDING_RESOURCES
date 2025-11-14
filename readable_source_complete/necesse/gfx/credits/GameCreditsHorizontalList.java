/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.credits;

import java.awt.Dimension;
import java.util.ArrayList;
import necesse.gfx.credits.GameCreditsDisplay;

public class GameCreditsHorizontalList
extends GameCreditsDisplay {
    protected ArrayList<GameCreditsDisplay> displays = new ArrayList();
    protected Dimension drawBounds = new Dimension(0, 0);
    protected ArrayList<InitializedDisplay> initializedDisplays = new ArrayList();
    protected int maxTime;
    public boolean centerText;

    public GameCreditsHorizontalList(boolean centerText) {
        this.centerText = centerText;
    }

    public GameCreditsHorizontalList add(GameCreditsDisplay display) {
        this.displays.add(display);
        return this;
    }

    @Override
    public int initDrawAndGetTotalTimeShown() {
        this.initializedDisplays.clear();
        this.maxTime = 0;
        int maxWidth = 0;
        for (GameCreditsDisplay display : this.displays) {
            int timeShown = display.initDrawAndGetTotalTimeShown();
            if (timeShown <= 0) continue;
            Dimension drawBounds = display.getDrawBounds();
            if (drawBounds.width > maxWidth) {
                maxWidth = drawBounds.width;
            }
            this.initializedDisplays.add(new InitializedDisplay(display, drawBounds));
            if (timeShown <= this.maxTime) continue;
            this.maxTime = timeShown;
        }
        int currentHeight = 0;
        for (InitializedDisplay display : this.initializedDisplays) {
            if (this.centerText) {
                display.drawOffsetX = (maxWidth - display.drawBounds.width) / 2;
            }
            display.drawOffsetY = currentHeight;
            currentHeight += display.drawBounds.height;
        }
        this.drawBounds = new Dimension(maxWidth, currentHeight);
        return this.maxTime;
    }

    @Override
    public Dimension getDrawBounds() {
        return this.drawBounds;
    }

    @Override
    public void draw(int currentTime, int x, int y, float alpha) {
        for (InitializedDisplay currentDisplay : this.initializedDisplays) {
            currentDisplay.display.draw(currentTime, x + currentDisplay.drawOffsetX, y + currentDisplay.drawOffsetY, alpha);
        }
    }

    @Override
    public boolean isDone(int currentTime) {
        return currentTime >= this.maxTime;
    }

    protected static class InitializedDisplay {
        public GameCreditsDisplay display;
        public Dimension drawBounds;
        public int drawOffsetX;
        public int drawOffsetY;

        protected InitializedDisplay(GameCreditsDisplay display, Dimension drawBounds) {
            this.display = display;
            this.drawBounds = drawBounds;
        }
    }
}

