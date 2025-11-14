/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.layers;

import necesse.apiDoc.APIDocInclude;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.IDData;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.LevelDrawUtils;
import necesse.level.maps.Level;

@APIDocInclude
public abstract class LevelLayer {
    public final IDData idData = new IDData();
    public final Level level;

    public final String getStringID() {
        return this.idData.getStringID();
    }

    public final int getID() {
        return this.idData.getID();
    }

    public LevelLayer(Level level) {
        this.level = level;
    }

    public abstract void init();

    public abstract void onLoadingComplete();

    public void frameTick(TickManager tickManager) {
    }

    public void clientTick() {
    }

    public void serverTick() {
    }

    public void simulateWorld(long worldTimeIncrease, boolean sendChanges) {
    }

    public void tickTileEffects(GameCamera camera, PlayerMob perspective, LevelDrawUtils.DrawArea drawArea) {
    }

    public void writeLevelDataPacket(PacketWriter writer) {
    }

    public void readLevelDataPacket(PacketReader reader) {
    }

    public abstract void addSaveData(SaveData var1);

    public abstract void loadSaveData(LoadData var1);
}

