/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject.furniture;

import java.awt.Color;
import java.awt.Point;
import necesse.level.gameObject.furniture.ChairObject;
import necesse.level.maps.Level;

public class SpiderCastleChairObject
extends ChairObject {
    public SpiderCastleChairObject(String textureName, Color mapColor) {
        super(textureName, mapColor, new String[0]);
        this.alwaysRenderBehindUser = true;
    }

    @Override
    public Point getMobPosSitOffset(Level level, int tileX, int tileY) {
        byte rotation = level.getObjectRotation(tileX, tileY);
        switch (rotation) {
            case 0: {
                return new Point(16, 12);
            }
            case 1: {
                return new Point(16, 15);
            }
            case 2: {
                return new Point(16, 16);
            }
            case 3: {
                return new Point(16, 15);
            }
        }
        return new Point(16, 16);
    }
}

