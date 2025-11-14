/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject.furniture.doubleBed;

import java.awt.Color;
import java.awt.Rectangle;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.furniture.doubleBed.DoubleBedBaseObject;
import necesse.level.gameObject.furniture.doubleBed.DoubleBedHeadBaseObject;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObject;

public class DoubleBedFootBaseObject
extends DoubleBedBaseObject {
    public DoubleBedFootBaseObject(String textureName, ToolType toolType, Color mapColor) {
        super(textureName, toolType, mapColor);
    }

    @Override
    public boolean isMasterBedObject(Level level, int tileX, int tileY) {
        return false;
    }

    @Override
    public LevelObject getSettlerBedMasterLevelObject(Level level, int tileX, int tileY) {
        LevelObject out;
        switch (level.getObjectRotation(tileX, tileY)) {
            case 0: {
                out = new LevelObject(level, tileX, tileY + 1);
                break;
            }
            case 1: {
                out = new LevelObject(level, tileX - 1, tileY);
                break;
            }
            case 2: {
                out = new LevelObject(level, tileX, tileY - 1);
                break;
            }
            default: {
                out = new LevelObject(level, tileX + 1, tileY);
            }
        }
        if (out.object instanceof DoubleBedHeadBaseObject) {
            return out;
        }
        return null;
    }

    @Override
    public Rectangle getSettlerBedTileRectangle(Level level, int tileX, int tileY) {
        switch (level.getObjectRotation(tileX, tileY)) {
            case 0: {
                return new Rectangle(tileX, tileY, 1, 2);
            }
            case 1: {
                return new Rectangle(tileX - 1, tileY, 2, 1);
            }
            case 2: {
                return new Rectangle(tileX, tileY - 1, 1, 2);
            }
        }
        return new Rectangle(tileX, tileY, 2, 1);
    }
}

