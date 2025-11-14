/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.placeableItem;

import java.awt.geom.Line2D;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.friendly.HusbandryMob;
import necesse.entity.mobs.hostile.ItemAttackerRaiderMob;
import necesse.gfx.GameResources;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.placeableItem.PlaceableItem;
import necesse.level.maps.Level;

public class MobSpawnItem
extends PlaceableItem {
    public final String mobStringID;

    public MobSpawnItem(int stackSize, boolean singleUse, String mobStringID) {
        super(stackSize, singleUse);
        this.dropsAsMatDeathPenalty = true;
        this.keyWords.add("spawn");
        this.keyWords.add("mob");
        this.mobStringID = mobStringID;
    }

    @Override
    public void onItemRegistryClosed() {
        super.onItemRegistryClosed();
        if (MobRegistry.mobExists(this.mobStringID)) {
            Mob mob = MobRegistry.getMob(this.mobStringID, null);
            if (mob.isHostile || mob.isBoss()) {
                this.keyWords.add("hostile");
                if (mob.isBoss()) {
                    this.setItemCategory("mobs", "hostile", "bosses");
                    this.keyWords.add("bosses");
                } else if (mob instanceof ItemAttackerRaiderMob) {
                    this.setItemCategory("mobs", "hostile", "raiders");
                    this.keyWords.add("raiders");
                } else {
                    this.setItemCategory("mobs", "hostile");
                }
            } else {
                this.keyWords.add("passive");
                if (mob.isCritter) {
                    this.setItemCategory("mobs", "passive", "critters");
                    this.keyWords.add("critters");
                } else if (mob.isHuman) {
                    this.setItemCategory("mobs", "passive", "humans");
                    this.keyWords.add("humans");
                } else if (mob instanceof HusbandryMob) {
                    this.setItemCategory("mobs", "passive", "husbandry");
                    this.keyWords.add("husbandry");
                } else {
                    this.setItemCategory("mobs", "passive");
                }
            }
        }
    }

    @Override
    public GameSprite getItemSprite(InventoryItem item, PlayerMob perspective) {
        return new GameSprite(MobRegistry.getMobIcon(this.mobStringID));
    }

    @Override
    public InventoryItem onPlace(Level level, int x, int y, PlayerMob player, int seed, InventoryItem item, GNDItemMap mapContent) {
        if (level.isServer()) {
            Mob mob = MobRegistry.getMob(this.mobStringID, level);
            this.beforeSpawned(level, x, y, player, item, mapContent, mob);
            level.entityManager.addMob(mob, x, y);
        }
        if (this.isSingleUse(player)) {
            item.setAmount(item.getAmount() - 1);
        }
        if (level.isClient()) {
            SoundManager.playSound(GameResources.pop, (SoundEffect)SoundEffect.effect(player).pitch(0.8f));
        }
        return item;
    }

    protected void beforeSpawned(Level level, int x, int y, PlayerMob player, InventoryItem item, GNDItemMap mapContent, Mob mob) {
        mob.canDespawn = false;
    }

    @Override
    public String canPlace(Level level, int x, int y, PlayerMob player, Line2D playerPositionLine, InventoryItem item, GNDItemMap mapContent) {
        if (!level.isLevelPosWithinBounds(x, y)) {
            return "outsidelevelbounds";
        }
        return null;
    }

    @Override
    public Item.Rarity getRarity(InventoryItem item) {
        if (item.getGndData().getString("settlerIncursionReward") != null) {
            return Item.Rarity.UNIQUE;
        }
        return super.getRarity(item);
    }

    @Override
    public GameMessage getLocalization(InventoryItem item) {
        if (item.getGndData().getString("settlerIncursionReward") != null) {
            return MobRegistry.getLocalization(this.mobStringID);
        }
        return super.getLocalization(item);
    }

    @Override
    public GameMessage getNewLocalization() {
        return new LocalMessage("item", "mobspawnitem", "mob", MobRegistry.getLocalization(this.mobStringID));
    }

    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        if (item.getGndData().getString("settlerIncursionReward") == null) {
            tooltips.add(new StringTooltips(Localization.translate("itemtooltip", "spawnmob", "mob", MobRegistry.getDisplayName(MobRegistry.getMobID(this.mobStringID)))));
            if (this.isSingleUse(perspective)) {
                tooltips.add(Localization.translate("itemtooltip", "singleuse"));
            } else {
                tooltips.add(Localization.translate("itemtooltip", "infiniteuse"));
            }
        }
        return tooltips;
    }
}

