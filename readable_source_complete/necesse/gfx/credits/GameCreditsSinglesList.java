/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.credits;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.NavigableSet;
import java.util.TreeSet;
import necesse.gfx.credits.GameCreditsDisplay;

public class GameCreditsSinglesList
extends GameCreditsDisplay {
    protected ArrayList<GameCreditsDisplay> displays = new ArrayList();
    protected Dimension drawBounds = new Dimension(0, 0);
    protected TreeSet<TimedDisplay> initializedDisplays = new TreeSet<TimedDisplay>(Comparator.comparingInt(c -> c.startTime));
    public boolean centerHorizontally;
    public boolean centerVertically;

    public GameCreditsSinglesList(boolean centerHorizontally, boolean centerVertically) {
        this.centerHorizontally = centerHorizontally;
        this.centerVertically = centerVertically;
    }

    public GameCreditsSinglesList add(GameCreditsDisplay display) {
        this.displays.add(display);
        return this;
    }

    @Override
    public int initDrawAndGetTotalTimeShown() {
        this.initializedDisplays.clear();
        int totalTime = 0;
        int maxWidth = 0;
        int maxHeight = 0;
        for (GameCreditsDisplay gameCreditsDisplay : this.displays) {
            int timeShown = gameCreditsDisplay.initDrawAndGetTotalTimeShown();
            if (timeShown <= 0) continue;
            Dimension drawBounds = gameCreditsDisplay.getDrawBounds();
            if (drawBounds.width > maxWidth) {
                maxWidth = drawBounds.width;
            }
            if (drawBounds.height > maxHeight) {
                maxHeight = drawBounds.height;
            }
            this.initializedDisplays.add(new TimedDisplay(gameCreditsDisplay, totalTime, timeShown, drawBounds));
            totalTime += timeShown;
        }
        for (TimedDisplay timedDisplay : this.initializedDisplays) {
            if (this.centerHorizontally) {
                timedDisplay.drawOffsetX = (maxWidth - timedDisplay.drawBounds.width) / 2;
            }
            if (!this.centerVertically) continue;
            timedDisplay.drawOffsetY = (maxHeight - timedDisplay.drawBounds.height) / 2;
        }
        this.drawBounds = new Dimension(maxWidth, maxHeight);
        return totalTime;
    }

    protected TimedDisplay getCurrentDisplay(int currentTime) {
        NavigableSet<TimedDisplay> timedDisplays = this.initializedDisplays.headSet(new TimedDisplay(currentTime), true);
        if (timedDisplays.isEmpty()) {
            return null;
        }
        return (TimedDisplay)timedDisplays.last();
    }

    @Override
    public Dimension getDrawBounds() {
        return this.drawBounds;
    }

    @Override
    public void draw(int currentTime, int x, int y, float alpha) {
        TimedDisplay current = this.getCurrentDisplay(currentTime);
        if (current == null) {
            return;
        }
        current.display.draw(currentTime - current.startTime, x + current.drawOffsetX, y + current.drawOffsetY, alpha);
    }

    @Override
    public boolean isDone(int currentTime) {
        return this.getCurrentDisplay(currentTime) == null;
    }

    protected static class TimedDisplay {
        public GameCreditsDisplay display;
        public int startTime;
        public int timeShown;
        public Dimension drawBounds;
        public int drawOffsetX;
        public int drawOffsetY;

        protected TimedDisplay(GameCreditsDisplay display, int startTime, int timeShown, Dimension drawBounds) {
            this.display = display;
            this.startTime = startTime;
            this.timeShown = timeShown;
            this.drawBounds = drawBounds;
        }

        protected TimedDisplay(int currentTime) {
            this.startTime = currentTime;
        }
    }
}

