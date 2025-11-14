/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.world.worldEvent;

import java.util.List;
import necesse.engine.GameState;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.registries.IDData;
import necesse.engine.registries.WorldEventRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.world.WorldEntity;
import necesse.engine.world.WorldEntityGameClock;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.LevelDrawUtils;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;

public class WorldEvent
implements GameState,
WorldEntityGameClock {
    public final IDData idData = new IDData();
    public boolean shouldSave;
    private boolean isOver;
    public WorldEntity world;

    public final String getStringID() {
        return this.idData.getStringID();
    }

    public final int getID() {
        return this.idData.getID();
    }

    public WorldEvent() {
        WorldEventRegistry.instance.applyIDData(this.getClass(), this.idData);
    }

    public void addSaveData(SaveData save) {
    }

    public void applyLoadData(LoadData save) {
    }

    public void applySpawnPacket(PacketReader reader) {
    }

    public void setupSpawnPacket(PacketWriter writer) {
    }

    public void init() {
    }

    public void tickMovement(float delta) {
    }

    public void clientTick() {
    }

    public void serverTick() {
    }

    public void addDrawables(List<LevelSortedDrawable> sortedDrawables, OrderableDrawables tileDrawables, OrderableDrawables topDrawables, LevelDrawUtils.DrawArea drawArea, Level level, TickManager tickManager, GameCamera camera) {
    }

    public void over() {
        this.isOver = true;
    }

    public boolean isOver() {
        return this.isOver;
    }

    @Override
    public boolean isClient() {
        if (this.world != null) {
            return this.world.isClient();
        }
        return false;
    }

    @Override
    public Client getClient() {
        if (this.world != null) {
            return this.world.getClient();
        }
        return null;
    }

    @Override
    public boolean isServer() {
        if (this.world != null) {
            return this.world.isServer();
        }
        return false;
    }

    @Override
    public Server getServer() {
        if (this.world != null) {
            return this.world.getServer();
        }
        return null;
    }

    @Override
    public WorldEntity getWorldEntity() {
        return this.world;
    }
}

