/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData;

import java.awt.Point;
import necesse.engine.GameState;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.registries.IDData;
import necesse.engine.registries.LevelDataRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.OneWorldMigration;
import necesse.engine.world.WorldEntity;
import necesse.engine.world.WorldEntityGameClock;
import necesse.level.maps.Level;

public class LevelData
implements GameState,
WorldEntityGameClock {
    public final IDData idData = new IDData();
    protected Level level;
    protected String managerKey;

    public final String getStringID() {
        return this.idData.getStringID();
    }

    public final int getID() {
        return this.idData.getID();
    }

    public LevelData() {
        LevelDataRegistry.instance.applyIDData(this.getClass(), this.idData);
    }

    public boolean shouldSave() {
        return true;
    }

    public void addSaveData(SaveData save) {
        save.addUnsafeString("stringID", this.getStringID());
    }

    public void applyLoadData(LoadData save) {
    }

    public void init() {
    }

    public void setManagerKey(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Manager key cannot be null");
        }
        if (this.managerKey != null && !key.equals(this.managerKey)) {
            throw new IllegalStateException("Manager key is already set and cannot be changed");
        }
        this.managerKey = key;
    }

    public String getManagerKey() {
        return this.managerKey;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public Level getLevel() {
        return this.level;
    }

    @Override
    public WorldEntity getWorldEntity() {
        return this.level == null ? null : this.level.getWorldEntity();
    }

    @Override
    public boolean isClient() {
        return this.level != null && this.level.isClient();
    }

    @Override
    public Client getClient() {
        return this.level == null ? null : this.level.getClient();
    }

    @Override
    public boolean isServer() {
        return this.level != null && this.level.isServer();
    }

    @Override
    public Server getServer() {
        return this.level == null ? null : this.level.getServer();
    }

    public void onLoadingComplete() {
    }

    public void tick() {
    }

    public void migrateToOneWorld(OneWorldMigration migrationData, LevelIdentifier oldLevelIdentifier, Level newLevel, Point tileOffset, Point positionOffset) {
    }
}

