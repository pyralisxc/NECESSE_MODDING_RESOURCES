/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.objectEntity;

import java.awt.Point;
import java.util.ArrayList;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.gameObject.FruitTreeObject;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.jobs.HarvestFruitLevelJob;

public class FruitGrowerObjectEntity
extends ObjectEntity {
    protected int stage;
    protected int maxStage;
    public int minGrowTime;
    public int maxGrowTime;
    protected float fruitPerStage;
    protected String fruitStringID;
    protected long lastGrowTime;
    protected int growTime;
    protected boolean isFertilized;

    public FruitGrowerObjectEntity(Level level, int x, int y, int minGrowTimeInMs, int maxGrowTimeInMs, int maxStage, String fruitStringID, float fruitPerStage) {
        super(level, "fruitgrow", x, y);
        this.minGrowTime = minGrowTimeInMs;
        this.maxGrowTime = maxGrowTimeInMs;
        this.maxStage = maxStage;
        this.fruitStringID = fruitStringID;
        this.fruitPerStage = fruitPerStage;
        this.growTime = this.getNewGrowTime();
    }

    @Override
    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        save.addInt("stage", this.stage);
        save.addLong("lastGrowTime", this.lastGrowTime);
        save.addInt("growTime", this.growTime);
        save.addBoolean("isFertilized", this.isFertilized);
    }

    @Override
    public void applyLoadData(LoadData save) {
        super.applyLoadData(save);
        this.stage = save.getInt("stage", 0, 0, this.maxStage, false);
        this.lastGrowTime = save.getLong("lastGrowTime", 0L, false);
        this.growTime = save.getInt("growTime", 0, false);
        if (this.growTime == 0) {
            this.growTime = this.getNewGrowTime();
        }
        this.isFertilized = save.getBoolean("isFertilized", false);
    }

    @Override
    public void setupContentPacket(PacketWriter writer) {
        super.setupContentPacket(writer);
        writer.putNextShortUnsigned(this.stage);
        writer.putNextLong(this.lastGrowTime);
        writer.putNextInt(this.growTime);
        writer.putNextBoolean(this.isFertilized);
    }

    @Override
    public void applyContentPacket(PacketReader reader) {
        super.applyContentPacket(reader);
        this.stage = reader.getNextShortUnsigned();
        this.lastGrowTime = reader.getNextLong();
        this.growTime = reader.getNextInt();
        this.isFertilized = reader.getNextBoolean();
    }

    @Override
    public void init() {
        super.init();
        if (this.lastGrowTime <= 0L) {
            this.lastGrowTime = this.getWorldEntity().getWorldTime();
        }
    }

    private int getNewGrowTime() {
        if (this.maxGrowTime == 0) {
            return 0;
        }
        int time = GameRandom.globalRandom.getIntBetween(this.minGrowTime, this.maxGrowTime);
        if (this.isFertilized) {
            return time / 2;
        }
        return time;
    }

    @Override
    public void serverTick() {
        long currentTime = this.getWorldEntity().getWorldTime();
        if (this.lastGrowTime <= 0L) {
            this.lastGrowTime = currentTime;
        }
        while (this.stage < this.maxStage && this.lastGrowTime + (long)this.growTime <= currentTime) {
            ++this.stage;
            this.getLevel().jobsLayer.addJob(new HarvestFruitLevelJob(this.tileX, this.tileY));
            this.lastGrowTime += (long)this.growTime;
            this.growTime = this.getNewGrowTime();
            this.markDirty();
        }
        if (this.stage >= this.maxStage) {
            this.lastGrowTime = currentTime;
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
        long growDelta = (long)this.growTime - this.getWorldEntity().getWorldTime();
        this.growTime = (int)((long)this.growTime - growDelta / 2L);
        this.markDirty();
    }

    public int getStage() {
        return this.stage;
    }

    public void resetStage() {
        this.stage = 0;
        this.markDirty();
    }

    public ArrayList<InventoryItem> getHarvestItems() {
        ArrayList<InventoryItem> out = new ArrayList<InventoryItem>();
        int fruitItems = this.getFruitDropCount();
        if (fruitItems > 0) {
            out.add(new InventoryItem(this.fruitStringID, fruitItems));
        }
        return out;
    }

    public ArrayList<InventoryItem> getHarvestSplitItems() {
        ArrayList<InventoryItem> out = new ArrayList<InventoryItem>();
        int fruitItems = this.getFruitDropCount();
        if (fruitItems > 0) {
            new LootItem(this.fruitStringID, fruitItems).splitItems(5).addItems(out, GameRandom.globalRandom, 1.0f, new Object[0]);
        }
        return out;
    }

    public void harvest(Mob mob) {
        if (!this.isClient() && this.stage > 0) {
            Point dropPos = FruitTreeObject.getItemDropPos(this.tileX, this.tileY, mob);
            ArrayList<InventoryItem> items = this.getHarvestSplitItems();
            for (InventoryItem item : items) {
                this.getLevel().entityManager.pickups.add(item.getPickupEntity(this.getLevel(), dropPos.x, dropPos.y));
            }
            this.resetStage();
        }
    }

    public int getFruitDropCount() {
        int amount = 0;
        for (int i = 0; i < this.stage; ++i) {
            float remaining = this.fruitPerStage;
            amount = (int)((float)amount + remaining);
            if (!GameRandom.globalRandom.getChance(remaining -= (float)((int)remaining))) continue;
            ++amount;
        }
        return amount;
    }

    @Override
    public ArrayList<InventoryItem> getDroppedItems() {
        return this.getHarvestSplitItems();
    }

    public void setRandomStage(GameRandom random) {
        this.stage = random.nextInt(this.maxStage);
    }

    @Override
    public void onMouseHover(PlayerMob perspective, boolean debug) {
        super.onMouseHover(perspective, debug);
        if (debug) {
            StringTooltips tooltips = new StringTooltips();
            tooltips.add("Stage: " + this.stage);
            tooltips.add("Grows in: " + GameUtils.getTimeStringMillis(this.lastGrowTime + (long)this.growTime - this.getWorldEntity().getWorldTime()));
            tooltips.add("Fertilized: " + (this.isFertilized ? "Yes" : "No"));
            GameTooltipManager.addTooltip(tooltips, TooltipLocation.INTERACT_FOCUS);
        }
    }
}

