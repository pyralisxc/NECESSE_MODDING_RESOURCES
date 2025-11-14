/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.registries.DamageTypeRegistry
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.item.Item$Rarity
 *  necesse.inventory.item.ItemInteractAction
 */
package aphorea.items.tools.weapons.magic;

import aphorea.items.AphAreaToolItem;
import aphorea.utils.AphColors;
import aphorea.utils.area.AphArea;
import aphorea.utils.area.AphAreaList;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemInteractAction;

public class AdeptsBook
extends AphAreaToolItem
implements ItemInteractAction {
    public AdeptsBook() {
        super(650, true, false);
        this.rarity = Item.Rarity.RARE;
        this.attackAnimTime.setBaseValue(1000);
        this.manaCost.setBaseValue(4.0f);
        this.attackXOffset = -4;
        this.attackYOffset = 10;
        this.attackDamage.setBaseValue(30.0f).setUpgradedValue(1.0f, 60.0f);
        this.damageType = DamageTypeRegistry.MAGIC;
    }

    @Override
    public AphAreaList getAreaList(InventoryItem item) {
        return new AphAreaList(new AphArea(250.0f, AphColors.dark_magic).setDamageArea(this.getAttackDamage(item)));
    }
}

