/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.armorItem;

import necesse.engine.localization.Localization;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.entity.mobs.gameDamageType.DamageType;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorItem;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;

public class HelmetArmorItem
extends ArmorItem {
    public DamageType damageClass;
    public String hairMaskTextureName;
    public GameTexture hairMaskTexture;

    public HelmetArmorItem(int armorValue, DamageType damageClass, int enchantCost, String textureName, OneOfLootItems lootTableCategory) {
        super(ArmorItem.ArmorType.HEAD, armorValue, enchantCost, lootTableCategory, textureName);
        this.damageClass = damageClass;
        if (armorValue > 0) {
            int upgradedValue = damageClass == DamageTypeRegistry.MELEE ? 30 : (damageClass == DamageTypeRegistry.RANGED ? 24 : (damageClass == DamageTypeRegistry.MAGIC ? 22 : (damageClass == DamageTypeRegistry.SUMMON ? 17 : 30)));
            this.armorValue.setUpgradedValue(1.0f, upgradedValue);
            if (armorValue > upgradedValue) {
                this.armorValue.setBaseValue(upgradedValue);
            }
        }
        this.tierOneEssencesUpgradeRequirement = "shadowessence";
        this.tierTwoEssencesUpgradeRequirement = "slimeessence";
    }

    public HelmetArmorItem(int armorValue, DamageType damageClass, int enchantCost, Item.Rarity itemRarity, String textureName, OneOfLootItems lootTableCategory) {
        this(armorValue, damageClass, enchantCost, textureName, lootTableCategory);
        this.rarity = itemRarity;
    }

    @Override
    protected void loadArmorTexture() {
        super.loadArmorTexture();
        this.hairMaskTexture = this.hairMaskTextureName != null ? GameTexture.fromFile("player/armor/" + this.hairMaskTextureName) : this.loadTextureIfExists("player/armor/" + this.textureName + "_hairmask");
        if (this.hairMaskTexture != null) {
            this.hairMaskTexture.setBlendQuality(GameTexture.BlendQuality.NEAREST);
        }
    }

    @Override
    public String getTranslatedTypeName() {
        return Localization.translate("item", "helmet");
    }
}

