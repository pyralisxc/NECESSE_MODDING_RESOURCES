/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData.settlementData;

import java.util.Collections;
import java.util.List;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.entity.mobs.friendly.human.HappinessModifier;
import necesse.inventory.container.settlement.events.SettlementSettlersChangedEvent;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.furniture.SettlerBedObject;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.LevelSettler;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;
import necesse.level.maps.levelData.settlementData.SettlementRoom;

public class SettlementBed {
    public final ServerSettlementData data;
    public final int tileX;
    public final int tileY;
    protected LevelSettler settler;
    public boolean isLocked;

    public SettlementBed(ServerSettlementData data, int tileX, int tileY) {
        this.data = data;
        this.tileX = tileX;
        this.tileY = tileY;
    }

    public SettlementBed(ServerSettlementData data, LoadData save, int tileXOffset, int tileYOffset) {
        this.data = data;
        this.tileX = save.getInt("tileX") + tileXOffset;
        this.tileY = save.getInt("tileY") + tileYOffset;
        this.isLocked = save.getBoolean("isLocked", this.isLocked, false);
    }

    public void addSaveData(SaveData save) {
        save.addInt("tileX", this.tileX);
        save.addInt("tileY", this.tileY);
        save.addBoolean("isLocked", this.isLocked);
    }

    public boolean isValidBed() {
        return this.data.networkData.isTileWithinBounds(this.tileX, this.tileY) && SettlementBed.isValidBed(this.data.getLevel(), this.tileX, this.tileY);
    }

    public static boolean isValidBed(Level level, int tileX, int tileY) {
        GameObject object = level.getObject(tileX, tileY);
        return object instanceof SettlerBedObject && ((SettlerBedObject)((Object)object)).isMasterBedObject(level, tileX, tileY);
    }

    public SettlementRoom getRoom() {
        return this.data.rooms.getRoom(this.tileX, this.tileY);
    }

    public LevelSettler getSettler() {
        return this.settler;
    }

    public boolean clearSettler() {
        if (this.settler != null) {
            if (this.settler.getBed() == this) {
                this.settler.assignBed(null);
            }
            this.settler = null;
            this.data.sendEvent(SettlementSettlersChangedEvent.class);
            return true;
        }
        return false;
    }

    public List<HappinessModifier> getHappinessModifiers() {
        SettlementRoom room = this.getRoom();
        if (room != null) {
            return room.getHappinessModifiers();
        }
        return Collections.singletonList(HappinessModifier.bedOutsideModifier);
    }

    public int getHappinessScore() {
        SettlementRoom room = this.getRoom();
        if (room == null) {
            return HappinessModifier.bedOutsideModifier.happiness;
        }
        return room.getHappinessScore();
    }
}

