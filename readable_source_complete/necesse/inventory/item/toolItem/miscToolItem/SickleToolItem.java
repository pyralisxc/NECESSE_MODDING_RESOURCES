/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.miscToolItem;

import necesse.engine.localization.Localization;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemStatTipList;
import necesse.inventory.item.toolItem.ToolDamageItem;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.CustomWildFlowerObject;
import necesse.level.gameObject.FruitBushObject;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.SeedObject;
import necesse.level.maps.Level;

public class SickleToolItem
extends ToolDamageItem {
    public SickleToolItem() {
        super(100, null);
        this.setItemCategory("equipment", "tools", "misc");
        this.toolType = ToolType.PICKAXE;
        this.rarity = Item.Rarity.COMMON;
        this.attackAnimTime.setBaseValue(300);
        this.animAttacks = 3;
        this.toolDps.setBaseValue(100);
        this.addedRange = 1;
        this.attackDamage.setBaseValue(10.0f);
        this.knockback.setBaseValue(50);
        this.attackRange.setBaseValue(50);
        this.width = 10.0f;
        this.attackXOffset = 6;
        this.attackYOffset = 6;
    }

    @Override
    public boolean isEnchantable(InventoryItem item) {
        return false;
    }

    @Override
    public boolean canSmartMineTile(Level level, int tileX, int tileY, PlayerMob player, InventoryItem item) {
        GameObject object = level.getObject(tileX, tileY);
        if (object instanceof SeedObject) {
            return ((SeedObject)object).isLastStage();
        }
        if (object instanceof CustomWildFlowerObject) {
            return true;
        }
        if (object instanceof FruitBushObject) {
            return ((FruitBushObject)object).getFruitStage(level, tileX, tileY) > 0;
        }
        return object.isGrass;
    }

    @Override
    public boolean canDamageTile(Level level, int layerID, int tileX, int tileY, ItemAttackerMob attackerMob, InventoryItem item) {
        GameObject object;
        if (layerID == -1) {
            layerID = 0;
        }
        if ((object = level.getObject(layerID, tileX, tileY)) instanceof SeedObject) {
            return ((SeedObject)object).isLastStage();
        }
        if (object instanceof CustomWildFlowerObject) {
            return true;
        }
        if (object instanceof FruitBushObject) {
            return true;
        }
        return object.isGrass;
    }

    @Override
    protected void runTileDamage(Level level, int levelX, int levelY, int priorityObjectLayerID, int tileX, int tileY, PlayerMob player, InventoryItem item, int damage) {
        GameObject object = level.getObject(tileX, tileY);
        if (object instanceof FruitBushObject) {
            if (level.isServer()) {
                ((FruitBushObject)object).harvest(level, tileX, tileY, player);
            }
        } else {
            super.runTileDamage(level, levelX, levelY, priorityObjectLayerID, tileX, tileY, player, item, damage);
        }
    }

    @Override
    protected void addToolTooltips(ListGameTooltips tooltips) {
        tooltips.add(Localization.translate("itemtooltip", "sickletip"));
    }

    @Override
    public void addStatTooltips(ItemStatTipList list, InventoryItem currentItem, InventoryItem lastItem, ItemAttackerMob perspective, boolean forceAdd) {
    }

    @Override
    public String getCanBeUpgradedError(InventoryItem item) {
        return Localization.translate("ui", "itemnotupgradable");
    }
}

