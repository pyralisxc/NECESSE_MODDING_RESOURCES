/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.hudManager.floatText;

import java.awt.Rectangle;
import necesse.level.maps.hudManager.HudDrawElement;

public abstract class FloatText
extends HudDrawElement {
    public abstract int getX();

    public abstract int getY();

    public abstract int getWidth();

    public abstract int getHeight();

    public Rectangle getCollision() {
        return new Rectangle(this.getX(), this.getY(), this.getWidth(), this.getHeight());
    }

    public boolean collidesWith(FloatText other) {
        return this.getCollision().intersects(other.getCollision());
    }
}

