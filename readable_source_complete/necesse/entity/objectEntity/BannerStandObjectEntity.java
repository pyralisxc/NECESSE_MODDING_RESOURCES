/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.objectEntity;

import necesse.engine.sound.SoundPlayer;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.objectEntity.InventoryObjectEntity;
import necesse.gfx.GameResources;
import necesse.inventory.InventoryItem;
import necesse.inventory.InventoryRange;
import necesse.inventory.item.Item;
import necesse.inventory.item.miscItem.BannerItem;
import necesse.level.maps.Level;

public class BannerStandObjectEntity
extends InventoryObjectEntity {
    protected SoundPlayer soundPlayer;

    public BannerStandObjectEntity(Level level, int x, int y) {
        super(level, x, y, 1);
    }

    @Override
    public boolean isItemValid(int slot, InventoryItem item) {
        if (item != null) {
            return item.item instanceof BannerItem;
        }
        return false;
    }

    @Override
    public InventoryRange getSettlementStorage() {
        return null;
    }

    @Override
    public boolean canQuickStackInventory() {
        return false;
    }

    @Override
    public boolean canRestockInventory() {
        return false;
    }

    @Override
    public boolean canSortInventory() {
        return false;
    }

    @Override
    public boolean canUseForNearbyCrafting() {
        return false;
    }

    @Override
    public boolean canSetInventoryName() {
        return false;
    }

    @Override
    public void clientTick() {
        super.clientTick();
        this.tickBuffs();
    }

    @Override
    public void serverTick() {
        super.serverTick();
        this.tickBuffs();
    }

    public BannerItem getBannerInInventory() {
        InventoryItem invItem = this.getInventory().getItem(0);
        if (invItem != null && invItem.item instanceof BannerItem) {
            return (BannerItem)invItem.item;
        }
        return null;
    }

    @Override
    public boolean shouldPlayAmbientSound() {
        return this.getBannerInInventory() != null && GameRandom.globalRandom.getEveryXthChance(30);
    }

    @Override
    public boolean ambienceIsLooping() {
        return false;
    }

    @Override
    protected SoundSettings getAmbientSound() {
        return new SoundSettings(GameRandom.globalRandom.getOneOf(GameResources.flagNoises)).volume(0.3f).basePitch(1.5f).pitchVariance(0.2f);
    }

    public void tickBuffs() {
        Item item;
        if (!this.inventory.isSlotClear(0) && (item = this.inventory.getItemSlot(0)) instanceof BannerItem) {
            BannerItem banner = (BannerItem)item;
            int range = (int)((float)banner.range * 1.5f);
            GameUtils.streamNetworkClients(this.getLevel()).filter(c -> c.playerMob.getDistance(this.tileX * 32 + 16, this.tileY * 32 + 16) <= (float)range).forEach(c -> banner.applyBuffs(c.playerMob));
            this.getLevel().entityManager.mobs.streamInRegionsInRange(this.tileX * 32 + 16, this.tileY * 32 + 16, range).filter(m -> m.isHuman).filter(m -> m.getDistance(this.tileX * 32 + 16, this.tileY * 32 + 16) <= (float)range).forEach(banner::applyBuffs);
        }
    }
}

