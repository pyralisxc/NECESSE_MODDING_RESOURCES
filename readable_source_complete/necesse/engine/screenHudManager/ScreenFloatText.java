/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.screenHudManager;

import java.awt.Rectangle;
import necesse.engine.screenHudManager.ScreenHudElement;

public abstract class ScreenFloatText
extends ScreenHudElement {
    public abstract int getX();

    public abstract int getY();

    public abstract int getWidth();

    public abstract int getHeight();

    public Rectangle getCollision() {
        return new Rectangle(this.getX(), this.getY(), this.getWidth(), this.getHeight());
    }

    public boolean collidesWith(ScreenFloatText other) {
        return this.getCollision().intersects(other.getCollision());
    }
}

