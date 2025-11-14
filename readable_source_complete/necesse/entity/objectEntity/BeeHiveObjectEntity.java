/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.objectEntity;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.AbstractBeeHiveObjectEntity;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.level.maps.Level;

public class BeeHiveObjectEntity
extends AbstractBeeHiveObjectEntity {
    public static int maxStoredHoney = 5;
    public static int maxBees = 20;
    public int hiveBeeCapacity;

    public BeeHiveObjectEntity(Level level, int x, int y) {
        super(level, "beehive", x, y);
        this.hasQueen = true;
        this.hiveBeeCapacity = GameRandom.globalRandom.getIntBetween(1, maxBees);
        this.honey.amount = GameRandom.globalRandom.getIntBetween(0, maxStoredHoney);
        this.bees.amount = GameRandom.globalRandom.getIntBetween(this.hiveBeeCapacity / 2, this.hiveBeeCapacity);
    }

    @Override
    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        save.addInt("hiveBeeCapacity", this.hiveBeeCapacity);
    }

    @Override
    public void applyLoadData(LoadData save) {
        super.applyLoadData(save);
        this.hiveBeeCapacity = save.getInt("hiveBeeCapacity", this.hiveBeeCapacity, false);
    }

    @Override
    public void setupContentPacket(PacketWriter writer) {
        super.setupContentPacket(writer);
        writer.putNextInt(this.hiveBeeCapacity);
    }

    @Override
    public void applyContentPacket(PacketReader reader) {
        super.applyContentPacket(reader);
        this.hiveBeeCapacity = reader.getNextInt();
    }

    @Override
    public void onMouseHover(PlayerMob perspective, boolean debug) {
        super.onMouseHover(perspective, debug);
        StringTooltips tooltips = new StringTooltips();
        if (debug) {
            tooltips.add("Capacity: " + this.hiveBeeCapacity);
            this.addDebugTooltips(tooltips);
        }
        GameTooltipManager.addTooltip(tooltips, TooltipLocation.INTERACT_FOCUS);
    }

    @Override
    public boolean canAddWorkerBee() {
        return false;
    }

    @Override
    public int getMaxBees() {
        return this.hiveBeeCapacity;
    }

    @Override
    public int getMaxFrames() {
        return 0;
    }

    @Override
    public int getMaxStoredHoney() {
        return maxStoredHoney;
    }

    @Override
    public boolean canCreateQueens() {
        return false;
    }
}

