/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.screenHudManager;

import java.util.ArrayList;
import necesse.engine.gameLoop.tickManager.TickManager;

public abstract class ScreenHudElement {
    private boolean removed;

    public final void remove() {
        if (this.removed) {
            return;
        }
        this.removed = true;
    }

    public final boolean isRemoved() {
        return this.removed;
    }

    protected void onRemove() {
    }

    public final long getTime() {
        return System.currentTimeMillis();
    }

    public int getDrawPriority() {
        return 0;
    }

    public abstract void draw(TickManager var1);

    public void addThis(ArrayList<ScreenHudElement> elements) {
        elements.add(this);
    }
}

