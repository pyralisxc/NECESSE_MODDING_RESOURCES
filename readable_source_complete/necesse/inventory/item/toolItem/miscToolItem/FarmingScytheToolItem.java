/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.miscToolItem;

import necesse.engine.localization.Localization;
import necesse.engine.network.packet.PacketPlayObjectDamageSound;
import necesse.engine.network.server.ServerClient;
import necesse.entity.ObjectDamageResult;
import necesse.entity.levelEvent.mobAbilityLevelEvent.ToolItemMobAbilityEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.ItemStatTipList;
import necesse.inventory.item.toolItem.ToolDamageItem;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.FruitBushObject;
import necesse.level.gameObject.SeedObject;
import necesse.level.maps.LevelObject;

public class FarmingScytheToolItem
extends ToolDamageItem {
    public FarmingScytheToolItem() {
        super(0, null);
        this.setItemCategory("equipment", "tools", "misc");
        this.setItemCategory(ItemCategory.equipmentManager, (String[])null);
        this.toolType = ToolType.NONE;
        this.rarity = Item.Rarity.RARE;
        this.attackAnimTime.setBaseValue(300);
        this.attackDamage.setBaseValue(0.0f);
        this.attackRange.setBaseValue(80);
        this.width = 90.0f;
        this.attackXOffset = 10;
        this.attackYOffset = 10;
    }

    @Override
    public boolean isEnchantable(InventoryItem item) {
        return false;
    }

    @Override
    public boolean canHitMob(Mob mob, ToolItemMobAbilityEvent event) {
        return false;
    }

    @Override
    public boolean canHitObject(LevelObject levelObject) {
        if (super.canHitObject(levelObject)) {
            return true;
        }
        if (levelObject.object instanceof SeedObject) {
            return ((SeedObject)levelObject.object).isLastStage();
        }
        return levelObject.object instanceof FruitBushObject;
    }

    @Override
    public void hitObject(InventoryItem item, LevelObject levelObject, Mob mob) {
        super.hitObject(item, levelObject, mob);
        if (levelObject.object instanceof SeedObject) {
            if (((SeedObject)levelObject.object).isLastStage()) {
                ServerClient client = mob.isPlayer ? ((PlayerMob)mob).getServerClient() : null;
                ObjectDamageResult result = levelObject.level.entityManager.doObjectDamage(0, levelObject.tileX, levelObject.tileY, levelObject.object.objectHealth, this.getToolTier(item, mob), mob, client);
                if (result != null && result.addedDamage > 0) {
                    levelObject.level.getServer().network.sendToClientsWithTile(new PacketPlayObjectDamageSound(levelObject.tileX, levelObject.tileY, levelObject.object.getID()), levelObject.level, levelObject.tileX, levelObject.tileY);
                }
            }
        } else if (levelObject.object instanceof FruitBushObject) {
            ((FruitBushObject)levelObject.object).harvest(levelObject.level, levelObject.tileX, levelObject.tileY, mob);
        }
    }

    @Override
    protected void addToolTooltips(ListGameTooltips tooltips) {
        tooltips.add(Localization.translate("itemtooltip", "farmingscythetip"));
    }

    @Override
    public void addStatTooltips(ItemStatTipList list, InventoryItem currentItem, InventoryItem lastItem, ItemAttackerMob perspective, boolean forceAdd) {
    }

    @Override
    public String getCanBeUpgradedError(InventoryItem item) {
        return Localization.translate("ui", "itemnotupgradable");
    }
}

