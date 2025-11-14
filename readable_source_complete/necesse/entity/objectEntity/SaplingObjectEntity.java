/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.objectEntity;

import necesse.engine.registries.ObjectRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;

public class SaplingObjectEntity
extends ObjectEntity {
    public long growTime;
    public int minGrowTime;
    public int maxGrowTime;
    private String resultObjectStringID;

    public SaplingObjectEntity(Level level, int x, int y, String resultObjectStringID, int minGrowTimeInSeconds, int maxGrowTimeInSeconds) {
        super(level, "sapling", x, y);
        this.resultObjectStringID = resultObjectStringID;
        this.minGrowTime = minGrowTimeInSeconds * 1000;
        this.maxGrowTime = maxGrowTimeInSeconds * 1000;
        this.generateGrowTime();
    }

    @Override
    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        save.addLong("growTime", this.growTime);
    }

    @Override
    public void applyLoadData(LoadData save) {
        super.applyLoadData(save);
        this.growTime = save.getLong("growTime", 0L);
    }

    @Override
    public boolean shouldRequestPacket() {
        return false;
    }

    public void generateGrowTime() {
        this.growTime = this.getWorldEntity().getWorldTime() + (long)GameRandom.globalRandom.getIntBetween(this.minGrowTime, this.maxGrowTime);
    }

    @Override
    public void serverTick() {
        if (this.getWorldEntity().getWorldTime() > this.growTime) {
            this.grow();
        }
    }

    public void grow() {
        if (this.resultObjectStringID != null) {
            GameObject resultObject = ObjectRegistry.getObject(this.resultObjectStringID);
            if (resultObject != null && resultObject.isValid(this.getLevel(), 0, this.tileX, this.tileY)) {
                this.getLevel().sendObjectChangePacket(this.getLevel().getServer(), this.tileX, this.tileY, resultObject.getID());
                this.remove();
            } else {
                this.growTime = this.getWorldEntity().getWorldTime() + (long)GameRandom.globalRandom.getIntBetween(this.minGrowTime, this.maxGrowTime);
            }
        }
    }

    @Override
    public void onMouseHover(PlayerMob perspective, boolean debug) {
        super.onMouseHover(perspective, debug);
        if (debug) {
            GameTooltipManager.addTooltip(new StringTooltips("Growtime: " + ActiveBuff.convertSecondsToText((float)(this.growTime - this.getWorldEntity().getWorldTime()) / 1000.0f)), TooltipLocation.INTERACT_FOCUS);
        }
    }
}

