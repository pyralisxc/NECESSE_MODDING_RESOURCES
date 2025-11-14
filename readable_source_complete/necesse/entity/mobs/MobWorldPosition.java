/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs;

import necesse.engine.network.server.Server;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.InvalidLevelIdentifierException;
import necesse.engine.util.LevelIdentifier;
import necesse.entity.mobs.Mob;
import necesse.level.maps.Level;

public class MobWorldPosition {
    public final LevelIdentifier levelIdentifier;
    public final int mobUniqueID;
    public int lastTileX = Integer.MIN_VALUE;
    public int lastTileY = Integer.MIN_VALUE;

    public MobWorldPosition(Mob mob) {
        Level level = mob.getLevel();
        this.levelIdentifier = level.getIdentifier();
        this.mobUniqueID = mob.getUniqueID();
        this.updateTilePosition(mob);
    }

    public MobWorldPosition(LevelIdentifier levelIdentifier, int mobUniqueID) {
        this.levelIdentifier = levelIdentifier;
        this.mobUniqueID = mobUniqueID;
    }

    public MobWorldPosition(LoadData save) {
        LevelIdentifier levelIdentifier;
        try {
            levelIdentifier = new LevelIdentifier(save.getUnsafeString("level", null));
        }
        catch (InvalidLevelIdentifierException e) {
            int islandX = save.getInt("islandX");
            int islandY = save.getInt("islandY");
            int dimension = save.getInt("dimension");
            levelIdentifier = new LevelIdentifier(islandX, islandY, dimension);
        }
        this.levelIdentifier = levelIdentifier;
        this.mobUniqueID = save.getInt("mobUniqueID");
        this.lastTileX = save.getInt("lastTileX", Integer.MIN_VALUE, false);
        this.lastTileY = save.getInt("lastTileY", Integer.MIN_VALUE, false);
    }

    public SaveData getSaveData(String dataName) {
        SaveData save = new SaveData(dataName);
        this.addSaveData(save);
        return save;
    }

    public void addSaveData(SaveData save) {
        save.addUnsafeString("level", this.levelIdentifier.stringID);
        if (this.levelIdentifier.isIslandPosition()) {
            save.addInt("islandX", this.levelIdentifier.getIslandX());
            save.addInt("islandY", this.levelIdentifier.getIslandY());
            save.addInt("dimension", this.levelIdentifier.getIslandDimension());
        }
        save.addInt("mobUniqueID", this.mobUniqueID);
        if (this.lastTileX != Integer.MIN_VALUE) {
            save.addInt("lastTileX", this.lastTileX);
        }
        if (this.lastTileY != Integer.MIN_VALUE) {
            save.addInt("lastTileY", this.lastTileY);
        }
    }

    public void updateTilePosition(Mob mob) {
        this.lastTileX = mob.getTileX();
        this.lastTileY = mob.getTileY();
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public Mob getMob(Server server, boolean loadIfNotLoaded) {
        Level level;
        if (!loadIfNotLoaded) {
            if (!server.world.levelManager.isLoaded(this.levelIdentifier)) return null;
            level = server.world.getLevel(this.levelIdentifier, () -> null);
            return this.getMob(level);
        } else {
            level = server.world.getLevel(this.levelIdentifier, () -> null);
        }
        return this.getMob(level);
    }

    public Mob getMob(Level level) {
        if (level == null) {
            return null;
        }
        if (this.lastTileX != Integer.MIN_VALUE && this.lastTileY != Integer.MIN_VALUE) {
            level.regionManager.ensureTileIsLoaded(this.lastTileX, this.lastTileY);
        }
        return level.entityManager.mobs.get(this.mobUniqueID, false);
    }

    public boolean isLevel(Level level) {
        return level.getIdentifier().equals(this.levelIdentifier);
    }
}

