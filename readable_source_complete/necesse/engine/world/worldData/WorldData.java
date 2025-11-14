/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.world.worldData;

import necesse.engine.GameState;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.registries.IDData;
import necesse.engine.registries.WorldDataRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.world.WorldEntity;
import necesse.engine.world.WorldEntityGameClock;

public class WorldData
implements GameState,
WorldEntityGameClock {
    public final IDData idData = new IDData();
    protected WorldEntity worldEntity;

    public final String getStringID() {
        return this.idData.getStringID();
    }

    public final int getID() {
        return this.idData.getID();
    }

    public WorldData() {
        WorldDataRegistry.instance.applyIDData(this.getClass(), this.idData);
    }

    public void addSaveData(SaveData save) {
        save.addUnsafeString("stringID", this.getStringID());
    }

    public void applyLoadData(LoadData save) {
    }

    public void setWorldEntity(WorldEntity worldEntity) {
        this.worldEntity = worldEntity;
    }

    @Override
    public WorldEntity getWorldEntity() {
        return this.worldEntity;
    }

    public void init() {
    }

    public void tick() {
    }

    @Override
    public boolean isClient() {
        return this.worldEntity.isClient();
    }

    @Override
    public Client getClient() {
        return this.worldEntity.getClient();
    }

    @Override
    public boolean isServer() {
        return this.worldEntity.isServer();
    }

    @Override
    public Server getServer() {
        return this.worldEntity.getServer();
    }
}

