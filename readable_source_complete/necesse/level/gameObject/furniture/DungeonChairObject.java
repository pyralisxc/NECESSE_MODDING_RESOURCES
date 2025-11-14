/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject.furniture;

import java.awt.Color;
import java.awt.Point;
import necesse.level.gameObject.furniture.ChairObject;
import necesse.level.maps.Level;

public class DungeonChairObject
extends ChairObject {
    public DungeonChairObject(String textureName, Color mapColor, String ... category) {
        super(textureName, mapColor, category);
    }

    @Override
    public Point getMobPosSitOffset(Level level, int tileX, int tileY) {
        byte rotation = level.getObjectRotation(tileX, tileY);
        switch (rotation) {
            case 0: {
                return new Point(16, 20);
            }
            case 1: {
                return new Point(22, 20);
            }
            case 2: {
                return new Point(16, 23);
            }
            case 3: {
                return new Point(8, 20);
            }
        }
        return new Point(16, 16);
    }
}

