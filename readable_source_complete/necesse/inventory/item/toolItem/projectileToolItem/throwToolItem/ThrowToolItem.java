/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.projectileToolItem.throwToolItem;

import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.ItemStatTipList;
import necesse.inventory.item.toolItem.projectileToolItem.ProjectileToolItem;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;

public class ThrowToolItem
extends ProjectileToolItem {
    public ThrowToolItem(int enchantCost, OneOfLootItems lootTableCategory) {
        super(enchantCost, lootTableCategory);
        this.setItemCategory("equipment", "weapons", "throwweapons");
        this.setItemCategory(ItemCategory.craftingManager, "equipment", "weapons", "throwweapons");
        this.keyWords.add("throw");
        this.attackXOffset = 16;
        this.attackYOffset = 16;
        this.knockback.setBaseValue(25);
        this.tierOneEssencesUpgradeRequirement = "bioessence";
        this.tierTwoEssencesUpgradeRequirement = "slimeessence";
    }

    public ThrowToolItem() {
        super(0, null);
        this.stackSize = 250;
    }

    @Override
    public GameSprite getAttackSprite(InventoryItem item, PlayerMob player) {
        return null;
    }

    @Override
    protected ListGameTooltips getBaseTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getBaseTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "throwtip"));
        return tooltips;
    }

    @Override
    public void addStatTooltips(ItemStatTipList list, InventoryItem currentItem, InventoryItem lastItem, ItemAttackerMob perspective, boolean forceAdd) {
        this.addAttackDamageTip(list, currentItem, lastItem, perspective, forceAdd);
        this.addAttackSpeedTip(list, currentItem, lastItem, perspective);
        this.addResilienceGainTip(list, currentItem, lastItem, perspective, forceAdd);
        this.addCritChanceTip(list, currentItem, lastItem, perspective, forceAdd);
    }

    @Override
    public void setDrawAttackRotation(InventoryItem item, ItemAttackDrawOptions drawOptions, float attackDirX, float attackDirY, float attackProgress) {
        if (this.getAnimInverted(item)) {
            drawOptions.swingRotationInv(attackProgress);
        } else {
            drawOptions.swingRotation(attackProgress);
        }
    }

    @Override
    public boolean animDrawBehindHand(InventoryItem item) {
        return false;
    }

    @Override
    public boolean isEnchantable(InventoryItem item) {
        return false;
    }

    @Override
    public String getTranslatedTypeName() {
        return Localization.translate("item", "throwingweapon");
    }
}

