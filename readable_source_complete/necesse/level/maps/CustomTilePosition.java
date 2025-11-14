/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps;

import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObject;
import necesse.level.maps.LevelTile;
import necesse.level.maps.TilePosition;

public class CustomTilePosition
extends TilePosition {
    public CustomTilePosition(Level level, int objectLayerID, int tileX, int tileY, int tileID, boolean tileIsPlayerPlaced, int objectID, byte objectRotation, boolean objectIsPlayerPlaced) {
        super(level, tileX, tileY);
        this.object = LevelObject.custom(level, objectLayerID, tileX, tileY, ObjectRegistry.getObject(objectID), objectRotation, objectIsPlayerPlaced);
        this.tile = LevelTile.custom(level, tileX, tileY, TileRegistry.getTile(tileID), tileIsPlayerPlaced);
    }

    public CustomTilePosition(Level level, int tileX, int tileY, int tileID, boolean tileIsPlayerPlaced, int objectID, byte objectRotation, boolean objectIsPlayerPlaced) {
        this(level, 0, tileX, tileY, tileID, tileIsPlayerPlaced, objectID, objectRotation, objectIsPlayerPlaced);
    }

    @Override
    public int objectID() {
        return this.object.object.getID();
    }

    @Override
    public byte objectRotation() {
        return this.object.rotation;
    }

    @Override
    public int tileID() {
        return this.tile.tile.getID();
    }

    @Override
    public boolean isLiquidTile() {
        return this.tile.tile.isLiquid;
    }

    @Override
    public boolean isShore() {
        return super.isShore();
    }

    @Override
    public boolean isSolidTile() {
        return this.object.object.isSolid(this.level, this.tileX, this.tileY);
    }
}

