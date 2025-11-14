/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps;

import java.awt.Point;
import java.awt.Rectangle;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObject;

public class LevelObjectHit
extends Rectangle {
    public final Level level;
    public final int tileX;
    public final int tileY;

    public LevelObjectHit(Rectangle r, Level level, int tileX, int tileY) {
        super(r);
        this.level = level;
        this.tileX = tileX;
        this.tileY = tileY;
    }

    public LevelObjectHit(Rectangle r, Level level) {
        this(r, level, -1, -1);
    }

    public Point getPoint() {
        return new Point(this.tileX, this.tileY);
    }

    public GameObject getObject() {
        return this.level.getObject(this.tileX, this.tileY);
    }

    public LevelObject getLevelObject() {
        return this.level.getLevelObject(this.tileX, this.tileY);
    }

    public boolean invalidPos() {
        return this.tileX == -1 || this.tileY == -1;
    }
}

