/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem;

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

public class MagicProjectileToolItem
extends ProjectileToolItem {
    protected final float incursionMagicDamageMultiplier = 1.2f;

    public MagicProjectileToolItem(int enchantCost, OneOfLootItems lootTableCategory) {
        super(enchantCost, lootTableCategory);
        this.setItemCategory("equipment", "weapons", "magicweapons");
        this.setItemCategory(ItemCategory.equipmentManager, "weapons", "magicweapons");
        this.setItemCategory(ItemCategory.craftingManager, "equipment", "weapons", "magicweapons");
        this.damageType = DamageTypeRegistry.MAGIC;
        this.tierOneEssencesUpgradeRequirement = "primordialessence";
        this.tierTwoEssencesUpgradeRequirement = "slimeessence";
    }

    @Override
    public void addStatTooltips(ItemStatTipList list, InventoryItem currentItem, InventoryItem lastItem, ItemAttackerMob perspective, boolean forceAdd) {
        this.addAttackDamageTip(list, currentItem, lastItem, perspective, forceAdd);
        this.addAttackSpeedTip(list, currentItem, lastItem, perspective);
        this.addResilienceGainTip(list, currentItem, lastItem, perspective, forceAdd);
        this.addCritChanceTip(list, currentItem, lastItem, perspective, forceAdd);
        this.addManaCostTip(list, currentItem, lastItem, perspective);
    }

    @Override
    public String getTranslatedTypeName() {
        return Localization.translate("item", "magicweapon");
    }

    @Override
    protected SoundSettings getSwingSound() {
        return new SoundSettings(GameResources.magicbolt1).volume(this.getAttackSound() == null ? 0.6f : 0.4f);
    }
}

