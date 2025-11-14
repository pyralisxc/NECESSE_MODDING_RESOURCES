/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.localization.Localization
 *  necesse.engine.localization.message.GameMessage
 *  necesse.engine.localization.message.LocalMessage
 *  necesse.engine.network.gameNetworkData.GNDItemMap
 *  necesse.engine.util.GameRandom
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.buffs.ActiveBuff
 *  necesse.entity.mobs.itemAttacker.ItemAttackSlot
 *  necesse.entity.mobs.itemAttacker.ItemAttackerMob
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.enchants.Enchantable
 *  necesse.inventory.enchants.ItemEnchantment
 *  necesse.inventory.enchants.ToolItemEnchantment
 *  necesse.inventory.item.ItemCategory
 *  necesse.inventory.item.upgradeUtils.IntUpgradeValue
 *  necesse.level.maps.Level
 *  necesse.level.maps.incursion.IncursionData
 *  org.jetbrains.annotations.Nullable
 */
package aphorea.items.tools.healing;

import aphorea.items.vanillaitemtypes.AphToolItem;
import aphorea.registry.AphEnchantments;
import aphorea.utils.magichealing.AphMagicHealing;
import aphorea.utils.magichealing.AphMagicHealingBuff;
import java.util.Set;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.inventory.InventoryItem;
import necesse.inventory.enchants.Enchantable;
import necesse.inventory.enchants.ItemEnchantment;
import necesse.inventory.enchants.ToolItemEnchantment;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.upgradeUtils.IntUpgradeValue;
import necesse.level.maps.Level;
import necesse.level.maps.incursion.IncursionData;
import org.jetbrains.annotations.Nullable;

public abstract class AphMagicHealingToolItem
extends AphToolItem {
    protected IntUpgradeValue magicHealing = new IntUpgradeValue(0, 0.2f);
    public boolean healingEnchantments = true;

    public AphMagicHealingToolItem(int enchantCost) {
        super(enchantCost);
        this.setItemCategory(new String[]{"equipment", "tools", "healing"});
        this.setItemCategory(ItemCategory.equipmentManager, new String[]{"tools", "healingtools"});
        this.setItemCategory(ItemCategory.craftingManager, new String[]{"equipment", "tools", "healingtools"});
    }

    public GameMessage getItemAttackerCanUseError(ItemAttackerMob mob, InventoryItem item) {
        return new LocalMessage("message", "cantusehealingtools");
    }

    public int getHealing(@Nullable InventoryItem item) {
        return item == null ? this.magicHealing.getValue(0.0f) : this.magicHealing.getValue(item.item.getUpgradeTier(item));
    }

    public void healMob(ItemAttackerMob attackerMob, Mob target, int healing, InventoryItem item) {
        AphMagicHealing.healMob((Mob)attackerMob, target, healing, item, this);
    }

    public void healMob(ItemAttackerMob attackerMob, Mob target, InventoryItem item) {
        this.healMob(attackerMob, target, this.magicHealing.getValue(item.item.getUpgradeTier(item)), item);
    }

    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        this.onHealingToolItemUsed((Mob)attackerMob, item);
        return super.onAttack(level, x, y, attackerMob, attackHeight, item, slot, animAttack, seed, mapContent);
    }

    public ToolItemEnchantment getRandomEnchantment(GameRandom random, InventoryItem item) {
        return (ToolItemEnchantment)Enchantable.getRandomEnchantment((GameRandom)random, this.getValidEnchantmentIDs(item), (int)this.getEnchantmentID(item), ToolItemEnchantment.class);
    }

    public boolean isValidEnchantment(InventoryItem item, ItemEnchantment enchantment) {
        return this.getValidEnchantmentIDs(item).contains(enchantment.getID());
    }

    public Set<Integer> getValidEnchantmentIDs(InventoryItem item) {
        return this.healingEnchantments ? AphEnchantments.healingItemEnchantments : super.getValidEnchantmentIDs(item);
    }

    public String getTranslatedTypeName() {
        return Localization.translate((String)"item", (String)"healingtool");
    }

    public void onHealingToolItemUsed(Mob mob, InventoryItem item) {
        mob.buffManager.getArrayBuffs().stream().filter(buff -> buff.buff instanceof AphMagicHealingBuff).forEach(buff -> ((AphMagicHealingBuff)buff.buff).onMagicalHealingItemUsed((ActiveBuff)buff, mob, this, item));
    }

    public String getCanBeUpgradedError(InventoryItem item) {
        if (!this.magicHealing.hasMoreThanOneValue() && !this.attackDamage.hasMoreThanOneValue()) {
            return Localization.translate((String)"ui", (String)"itemnotupgradable");
        }
        return this.getUpgradeTier(item) >= (float)IncursionData.ITEM_TIER_UPGRADE_CAP ? Localization.translate((String)"ui", (String)"itemupgradelimit") : null;
    }
}

