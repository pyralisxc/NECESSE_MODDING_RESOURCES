/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.armorItem.spider;

import java.awt.Color;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.modifiers.ModifierValue;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemStatTip;
import necesse.inventory.item.ItemStatTipList;
import necesse.inventory.item.armorItem.ArmorModifiers;
import necesse.inventory.item.armorItem.ChestArmorItem;
import necesse.inventory.lootTable.presets.BodyArmorLootTable;

public class SpiderChestplateArmorItem
extends ChestArmorItem {
    public SpiderChestplateArmorItem() {
        super(3, 350, Item.Rarity.UNCOMMON, "spiderchest", "spiderarms", BodyArmorLootTable.bodyArmor);
    }

    @Override
    public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
        return new ArmorModifiers(new ModifierValue<Float>(BuffModifiers.KNOCKBACK_INCOMING_MOD, Float.valueOf(0.5f)));
    }

    @Override
    public void addModifierTooltips(ItemStatTipList list, InventoryItem currentItem, InventoryItem lastItem, Mob perspective) {
        list.add(200, new ItemStatTip(){

            @Override
            public GameMessage toMessage(Color betterColor, Color worseColor, Color neutralColor, boolean showDifference) {
                return new LocalMessage("itemtooltip", "spiderchest");
            }
        });
    }
}

