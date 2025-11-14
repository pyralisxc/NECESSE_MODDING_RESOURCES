/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps;

import necesse.engine.registries.BiomeRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.WorldEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.SignObjectEntity;
import necesse.level.maps.Level;

public class TemporaryDummyLevel
extends Level {
    public TemporaryDummyLevel(LevelIdentifier identifier, int width, int height, WorldEntity worldEntity) {
        super(identifier, width, height, worldEntity);
    }

    public TemporaryDummyLevel(LevelIdentifier identifier, WorldEntity worldEntity) {
        super(identifier, 20, 20, worldEntity);
        this.baseBiome = BiomeRegistry.UNKNOWN;
        int grassTile = TileRegistry.grassID;
        for (int x = 0; x < this.tileWidth; ++x) {
            for (int y = 0; y < this.tileHeight; ++y) {
                this.setTile(x, y, grassTile);
            }
        }
        this.setObject(this.tileWidth / 2, this.tileHeight / 2, ObjectRegistry.getObjectID("sign"), 2);
        ObjectEntity objectEntity = this.entityManager.getObjectEntity(this.tileWidth / 2, this.tileHeight / 2);
        if (objectEntity instanceof SignObjectEntity) {
            ((SignObjectEntity)objectEntity).setText("This is a temporary level generated because this coordinate does not have a world generator that handles it.\n\nAnything that you put in here will be deleted when it is unloaded.");
        }
    }

    @Override
    public boolean shouldSave() {
        return false;
    }
}

