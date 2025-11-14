/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData.jobs;

import necesse.engine.GameState;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.registries.IDData;
import necesse.engine.registries.LevelJobRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameLinkedList;
import necesse.engine.util.GameObjectReservable;
import necesse.engine.world.WorldEntity;
import necesse.engine.world.WorldEntityGameClock;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.ZoneTester;

public abstract class LevelJob
implements GameState,
WorldEntityGameClock {
    public final IDData idData = new IDData();
    public final int tileX;
    public final int tileY;
    public int sameTypePriority;
    private Level level;
    private GameLinkedList.Element element;
    public GameObjectReservable reservable = new GameObjectReservable();

    public final String getStringID() {
        return this.idData.getStringID();
    }

    public final int getID() {
        return this.idData.getID();
    }

    public LevelJob(int tileX, int tileY) {
        LevelJobRegistry.instance.applyIDData(this.getClass(), this.idData);
        this.tileX = tileX;
        this.tileY = tileY;
    }

    public LevelJob(LoadData save) {
        LevelJobRegistry.instance.applyIDData(this.getClass(), this.idData);
        this.tileX = save.getInt("tileX");
        this.tileY = save.getInt("tileY");
    }

    public void addSaveData(SaveData save) {
        save.addInt("tileX", this.tileX);
        save.addInt("tileY", this.tileY);
    }

    public final LevelJob init(Level level, GameLinkedList.Element element) {
        this.level = level;
        this.element = element;
        return this;
    }

    public boolean isWithinRestrictZone(ZoneTester zone) {
        return zone.containsTile(this.tileX, this.tileY);
    }

    public void remove() {
        if (this.element != null && !this.element.isRemoved()) {
            this.element.remove();
        }
    }

    public boolean isRemoved() {
        return this.element != null && this.element.isRemoved();
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

    public boolean shouldSave() {
        return true;
    }

    public boolean isValid() {
        return true;
    }

    protected boolean isSameTile(LevelJob other) {
        return this.tileX == other.tileX && this.tileY == other.tileY;
    }

    public boolean isSameJob(LevelJob other) {
        return this.isSameTile(other) && this.getID() == other.getID();
    }

    public int getSameJobPriority() {
        return 0;
    }

    public int getSameTypePriority() {
        return this.sameTypePriority;
    }

    public int getFirstPriority() {
        return 0;
    }

    public boolean prioritizeForSameJobAgain() {
        return false;
    }

    public int getAfterPrioritizedPriority() {
        return 0;
    }

    public String toString() {
        return super.toString() + "{" + this.tileX + ", " + this.tileY + "}";
    }
}

