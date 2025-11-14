/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.objectEntity;

import java.util.ArrayList;
import java.util.Collections;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameObjectReservable;
import necesse.engine.util.GameRandom;
import necesse.entity.events.EmptyEntityEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.event.AIEvent;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.friendly.ChickenMob;
import necesse.entity.mobs.friendly.HusbandryMob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.placeableItem.consumableItem.food.EggItemInterface;
import necesse.level.gameObject.EggNestObject;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;

public class EggNestObjectEntity
extends ObjectEntity {
    public InventoryItem eggItem;
    public long hatchTime;
    public EmptyEntityEvent hatchedEvent;
    public GameObjectReservable layEggOrFertilizeReservable = new GameObjectReservable();

    public EggNestObjectEntity(Level level, int x, int y) {
        super(level, "eggnest", x, y);
        this.hatchedEvent = this.registerEvent(new EmptyEntityEvent(){

            @Override
            protected void run() {
                GameObject eggNest;
                if (!EggNestObjectEntity.this.isServer() && (eggNest = ObjectRegistry.getObject("eggnest")) instanceof EggNestObject) {
                    GameTexture texture = ((EggNestObject)eggNest).texture.getDamagedTexture(0.0f);
                    for (int i = 0; i < 6; ++i) {
                        int sprite = GameRandom.globalRandom.nextInt(4);
                        EggNestObjectEntity.this.getLevel().entityManager.addParticle(new FleshParticle(EggNestObjectEntity.this.getLevel(), texture, 6 + sprite % 2, sprite / 2, 16, (float)(EggNestObjectEntity.this.tileX * 32) + 16.0f, (float)(EggNestObjectEntity.this.tileY * 32) + 16.0f, 10.0f, 0.0f, 0.0f), Particle.GType.IMPORTANT_COSMETIC);
                    }
                }
                EggNestObjectEntity.this.eggItem = null;
            }
        });
    }

    @Override
    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        save.addLong("hatchTime", this.hatchTime);
        if (this.eggItem != null) {
            SaveData eggItemSave = new SaveData("eggItem");
            this.eggItem.addSaveData(eggItemSave);
            save.addSaveData(eggItemSave);
        }
    }

    @Override
    public void applyLoadData(LoadData save) {
        super.applyLoadData(save);
        this.hatchTime = save.getLong("hatchTime", this.hatchTime);
        LoadData eggItemSave = save.getFirstLoadDataByName("eggItem");
        this.eggItem = eggItemSave != null ? InventoryItem.fromLoadData(eggItemSave) : null;
    }

    @Override
    public void setupContentPacket(PacketWriter writer) {
        super.setupContentPacket(writer);
        writer.putNextLong(this.hatchTime);
        if (this.eggItem != null) {
            writer.putNextBoolean(true);
            InventoryItem.addPacketContent(this.eggItem, writer);
        } else {
            writer.putNextBoolean(false);
        }
    }

    @Override
    public void applyContentPacket(PacketReader reader) {
        super.applyContentPacket(reader);
        this.hatchTime = reader.getNextLong();
        this.eggItem = reader.getNextBoolean() ? InventoryItem.fromContentPacket(reader) : null;
    }

    @Override
    public ArrayList<InventoryItem> getDroppedItems() {
        if (this.hasEgg()) {
            return new ArrayList<InventoryItem>(Collections.singletonList(this.eggItem));
        }
        return super.getDroppedItems();
    }

    @Override
    public void serverTick() {
        super.serverTick();
        if (this.hasEgg() && this.isFertilized() && this.getTimeToHatch() <= 0L) {
            this.hatch();
        }
    }

    public long getTimeToHatch() {
        return this.hatchTime - this.getWorldTime();
    }

    public void placeEgg(InventoryItem inventoryItem) {
        this.eggItem = inventoryItem;
        this.hatchTime = 0L;
        this.markDirty();
    }

    public boolean hasEgg() {
        return this.eggItem != null;
    }

    public boolean isFertilized() {
        return this.hasEgg() && this.hatchTime != 0L;
    }

    public void fertilize() {
        if (this.eggItem != null) {
            int hatchTimeSeconds = this.eggItem.item instanceof EggItemInterface ? ((EggItemInterface)((Object)this.eggItem.item)).getRandomHatchTimeSeconds(this.eggItem) : GameRandom.globalRandom.getIntBetween(ChickenMob.EGG_HATCH_SECONDS_MIN, ChickenMob.EGG_HATCH_SECONDS_MAX);
            this.hatchTime = this.getWorldTime() + (long)hatchTimeSeconds * 1000L;
            this.markDirty();
        }
    }

    public boolean hasRecentlyHatched() {
        if (this.hatchTime == 0L) {
            return false;
        }
        long timeSinceHatch = this.getWorldTime() - this.hatchTime;
        return timeSinceHatch < 30000L;
    }

    public void hatch() {
        if (this.eggItem != null) {
            String mobStringID = this.eggItem.item instanceof EggItemInterface ? ((EggItemInterface)((Object)this.eggItem.item)).getHatchMobStringID(this.eggItem) : "chicken";
            this.eggItem = null;
            Mob mob = MobRegistry.getMob(mobStringID, this.getLevel());
            if (mob == null) {
                return;
            }
            if (mob instanceof HusbandryMob) {
                ((HusbandryMob)mob).startBaby();
            }
            mob.resetUniqueID();
            mob.onSpawned(this.tileX * 32 + 16, this.tileY * 32 + 16);
            this.getLevel().entityManager.mobs.add(mob);
            if (mob.ai != null) {
                mob.ai.blackboard.submitEvent("wanderNow", new AIEvent());
            }
            this.hatchedEvent.runAndSend();
        }
    }

    @Override
    public void onMouseHover(PlayerMob perspective, boolean debug) {
        super.onMouseHover(perspective, debug);
        if (debug && this.hasEgg()) {
            ListGameTooltips tooltips = new ListGameTooltips();
            tooltips.add("Egg: " + this.eggItem);
            if (this.hatchTime == 0L) {
                tooltips.add("Not fertilized");
            } else {
                tooltips.add("Hatch time: " + ActiveBuff.convertSecondsToText((float)(this.hatchTime - this.getWorldEntity().getWorldTime()) / 1000.0f));
            }
            GameTooltipManager.addTooltip(tooltips, TooltipLocation.INTERACT_FOCUS);
        }
    }
}

