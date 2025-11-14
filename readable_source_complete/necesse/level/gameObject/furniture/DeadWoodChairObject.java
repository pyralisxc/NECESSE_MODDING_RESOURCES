/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject.furniture;

import java.awt.Color;
import java.awt.Point;
import necesse.level.gameObject.furniture.ChairObject;
import necesse.level.maps.Level;

public class DeadWoodChairObject
extends ChairObject {
    public DeadWoodChairObject(String textureName, Color mapColor, String ... category) {
        super(textureName, mapColor, category);
    }

    @Override
    public Point getMobPosSitOffset(Level level, int tileX, int tileY) {
        byte rotation = level.getObjectRotation(tileX, tileY);
        switch (rotation) {
            case 0: {
                return new Point(16, 11);
            }
            case 1: {
                return new Point(20, 10);
            }
            case 2: {
                return new Point(16, 14);
            }
            case 3: {
                return new Point(12, 10);
            }
        }
        return new Point(16, 16);
    }
}

