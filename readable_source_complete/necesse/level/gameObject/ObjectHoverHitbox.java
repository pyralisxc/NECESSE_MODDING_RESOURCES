/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Rectangle;

public class ObjectHoverHitbox
extends Rectangle {
    public final int layerID;
    public final int tileX;
    public final int tileY;
    public final int sortY;

    public ObjectHoverHitbox(int layerID, int tileX, int tileY, int x, int y, int width, int height, int sortY) {
        super(tileX * 32 + x, tileY * 32 + y, width, height);
        this.layerID = layerID;
        this.tileX = tileX;
        this.tileY = tileY;
        this.sortY = tileY * 32 + sortY;
    }

    public ObjectHoverHitbox(int layerID, int tileX, int tileY, int x, int y, int width, int height) {
        this(layerID, tileX, tileY, x, y, width, height, 16);
    }
}

