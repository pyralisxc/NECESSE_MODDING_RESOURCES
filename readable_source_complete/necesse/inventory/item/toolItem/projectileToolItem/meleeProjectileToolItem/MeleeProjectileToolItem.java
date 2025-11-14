/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.projectileToolItem.meleeProjectileToolItem;

import necesse.engine.localization.Localization;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.sound.SoundSettings;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.gfx.GameResources;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.ItemStatTipList;
import necesse.inventory.item.toolItem.projectileToolItem.ProjectileToolItem;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;

public class MeleeProjectileToolItem
extends ProjectileToolItem {
    public MeleeProjectileToolItem(int enchantCost, OneOfLootItems lootTableCategory) {
        super(enchantCost, lootTableCategory);
        this.setItemCategory("equipment", "weapons", "meleeweapons");
        this.setItemCategory(ItemCategory.equipmentManager, "weapons", "meleeweapons");
        this.setItemCategory(ItemCategory.craftingManager, "equipment", "weapons", "meleeweapons");
        this.damageType = DamageTypeRegistry.MELEE;
        this.tierOneEssencesUpgradeRequirement = "primordialessence";
        this.tierTwoEssencesUpgradeRequirement = "bloodessence";
    }

    @Override
    public void addStatTooltips(ItemStatTipList list, InventoryItem currentItem, InventoryItem lastItem, ItemAttackerMob perspective, boolean forceAdd) {
        this.addAttackDamageTip(list, currentItem, lastItem, perspective, forceAdd);
        this.addAttackSpeedTip(list, currentItem, lastItem, perspective);
        this.addResilienceGainTip(list, currentItem, lastItem, perspective, forceAdd);
        this.addCritChanceTip(list, currentItem, lastItem, perspective, forceAdd);
    }

    @Override
    protected SoundSettings getAttackSound() {
        return new SoundSettings(GameResources.regularBoomerang);
    }

    @Override
    public String getTranslatedTypeName() {
        return Localization.translate("item", "meleeweapon");
    }
}

