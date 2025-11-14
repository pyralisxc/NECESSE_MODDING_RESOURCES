/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.objectEntity;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameObjectReservable;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.inventory.InventoryItem;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.SeedObject;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.jobs.HarvestCropLevelJob;

public class SeedObjectEntity
extends ObjectEntity {
    public int minGrowTime;
    public int maxGrowTime;
    protected long growTime;
    protected boolean isFertilized;
    public GameObjectReservable fertilizeReservable = new GameObjectReservable();

    public SeedObjectEntity(Level level, int x, int y, int minGrowTimeInMs, int maxGrowTimeInMs) {
        super(level, "seed", x, y);
        this.minGrowTime = minGrowTimeInMs;
        this.maxGrowTime = maxGrowTimeInMs;
        this.growTime = this.getNewGrowTime(this.getWorldEntity().getWorldTime());
    }

    @Override
    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        save.addLong("growTime", this.growTime);
        save.addBoolean("isFertilized", this.isFertilized);
    }

    @Override
    public void applyLoadData(LoadData save) {
        super.applyLoadData(save);
        this.growTime = save.getLong("growTime", 0L, false);
        if (this.growTime == 0L) {
            this.growTime = this.getNewGrowTime(this.getWorldEntity().getWorldTime());
        }
        this.isFertilized = save.getBoolean("isFertilized", false);
    }

    @Override
    public void setupContentPacket(PacketWriter writer) {
        super.setupContentPacket(writer);
        writer.putNextLong(this.growTime);
        writer.putNextBoolean(this.isFertilized);
    }

    @Override
    public void applyContentPacket(PacketReader reader) {
        super.applyContentPacket(reader);
        this.growTime = reader.getNextLong();
        this.isFertilized = reader.getNextBoolean();
    }

    private long getNewGrowTime(long startTime) {
        if (this.maxGrowTime == 0) {
            return startTime;
        }
        if (this.isFertilized) {
            return startTime + (long)(GameRandom.globalRandom.getIntBetween(this.minGrowTime, this.maxGrowTime) / 2);
        }
        return startTime + (long)GameRandom.globalRandom.getIntBetween(this.minGrowTime, this.maxGrowTime);
    }

    @Override
    public void serverTick() {
        SeedObject seedObject;
        GameObject object;
        long growDelta = this.growTime - this.getWorldEntity().getWorldTime();
        if (growDelta < 0L && (object = this.getLevel().getObject(this.tileX, this.tileY)) instanceof SeedObject && !(seedObject = (SeedObject)object).isLastStage() && seedObject.getNextStageID() != -1) {
            this.getLevel().sendObjectChangePacket(this.getLevel().getServer(), this.tileX, this.tileY, seedObject.getNextStageID());
            GameObject newObject = this.getLevel().getObject(this.tileX, this.tileY);
            if (newObject instanceof SeedObject) {
                SeedObject newSeedObject = (SeedObject)newObject;
                if (newSeedObject.isLastStage()) {
                    this.getLevel().jobsLayer.addJob(new HarvestCropLevelJob(this.tileX, this.tileY));
                } else {
                    ObjectEntity ent = this.getLevel().entityManager.getObjectEntity(this.tileX, this.tileY);
                    if (ent instanceof SeedObjectEntity) {
                        SeedObjectEntity seedEnt = (SeedObjectEntity)ent;
                        seedEnt.isFertilized = this.isFertilized;
                        seedEnt.fertilizeReservable = this.fertilizeReservable;
                        seedEnt.growTime = seedEnt.getNewGrowTime(this.getWorldEntity().getWorldTime() + growDelta);
                    }
                }
            }
        }
    }

    public boolean isFertilized() {
        return this.isFertilized;
    }

    public void fertilize() {
        if (this.isFertilized) {
            this.getLevel().entityManager.pickups.add(new InventoryItem("fertilizer").getPickupEntity(this.getLevel(), this.tileX * 32 + 16, this.tileY * 32 + 16));
            return;
        }
        this.isFertilized = true;
        long growDelta = this.growTime - this.getWorldEntity().getWorldTime();
        this.growTime -= growDelta / 2L;
        this.markDirty();
    }

    @Override
    public void onMouseHover(PlayerMob perspective, boolean debug) {
        super.onMouseHover(perspective, debug);
        if (debug) {
            StringTooltips tooltips = new StringTooltips();
            tooltips.add("Grows in: " + GameUtils.getTimeStringMillis(this.growTime - this.getWorldEntity().getWorldTime()));
            tooltips.add("Fertilized: " + (this.isFertilized ? "Yes" : "No"));
            GameTooltipManager.addTooltip(tooltips, TooltipLocation.INTERACT_FOCUS);
        }
    }
}

