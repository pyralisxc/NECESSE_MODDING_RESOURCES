/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.localization.Localization
 *  necesse.engine.localization.message.GameMessage
 *  necesse.engine.network.gameNetworkData.GNDItemMap
 *  necesse.engine.registries.DamageTypeRegistry
 *  necesse.engine.registries.EnchantmentRegistry
 *  necesse.entity.mobs.Attacker
 *  necesse.entity.mobs.GameDamage
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.itemAttacker.ItemAttackSlot
 *  necesse.entity.mobs.itemAttacker.ItemAttackerMob
 *  necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.item.ItemCategory
 *  necesse.inventory.item.ItemStatTipList
 *  necesse.level.maps.Level
 */
package aphorea.items;

import aphorea.items.tools.healing.AphMagicHealingToolItem;
import aphorea.registry.AphEnchantments;
import aphorea.registry.AphModifiers;
import aphorea.utils.area.AphArea;
import aphorea.utils.area.AphAreaList;
import aphorea.utils.area.AphAreaType;
import java.util.HashSet;
import java.util.Set;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.registries.EnchantmentRegistry;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.ItemStatTipList;
import necesse.level.maps.Level;

public abstract class AphAreaToolItem
extends AphMagicHealingToolItem {
    public boolean isMagicWeapon;
    public boolean isHealingTool;
    float rotationOffset;

    public AphAreaToolItem(int enchantCost, boolean isMagicWeapon, boolean isHealingTool) {
        super(enchantCost);
        this.isMagicWeapon = isMagicWeapon;
        this.isHealingTool = isHealingTool;
        this.damageType = DamageTypeRegistry.MAGIC;
        if (isMagicWeapon) {
            this.setItemCategory(new String[]{"equipment", "weapons", "magicweapons"});
            this.setItemCategory(ItemCategory.equipmentManager, new String[]{"weapons", "magicweapons"});
            this.setItemCategory(ItemCategory.craftingManager, new String[]{"equipment", "weapons", "magicweapons"});
        }
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        AphAreaList areaList = this.getAreaList(item);
        if (areaList.someType(AphAreaType.HEALING)) {
            this.onHealingToolItemUsed((Mob)attackerMob, item);
        }
        if (this.getManaCost(item) > 0.0f) {
            this.consumeMana(attackerMob, item);
        }
        float rangeModifier = 1.0f + ((Float)this.getEnchantment(item).getModifier(AphModifiers.TOOL_AREA_RANGE)).floatValue();
        areaList.execute((Mob)attackerMob, attackerMob.x, attackerMob.y, rangeModifier, item, this, true);
        return item;
    }

    public GameDamage getAttackDamage(InventoryItem item) {
        return super.getAttackDamage(item);
    }

    @Override
    public GameMessage getItemAttackerCanUseError(ItemAttackerMob mob, InventoryItem item) {
        AphAreaList areaList = this.getAreaList(item);
        if (areaList.someType(AphAreaType.DAMAGE) || areaList.someType(AphAreaType.DEBUFF)) {
            return null;
        }
        return super.getItemAttackerCanUseError(mob, item);
    }

    public boolean animDrawBehindHand(InventoryItem item) {
        return true;
    }

    public void addStatTooltips(ItemStatTipList list, InventoryItem currentItem, InventoryItem lastItem, ItemAttackerMob perspective, boolean forceAdd) {
        AphAreaList.addAreasStatTip(list, this.getAreaList(currentItem), lastItem == null ? null : this.getAreaList(lastItem), (Attacker)perspective, forceAdd, currentItem, lastItem, this, 100);
        this.addAttackSpeedTip(list, currentItem, lastItem, perspective);
        this.addResilienceGainTip(list, currentItem, lastItem, (Mob)perspective, forceAdd);
        this.addCritChanceTip(list, currentItem, lastItem, (Attacker)perspective, forceAdd);
        this.addManaCostTip(list, currentItem, lastItem, (Mob)perspective);
    }

    public abstract AphAreaList getAreaList(InventoryItem var1);

    @Override
    public Set<Integer> getValidEnchantmentIDs(InventoryItem item) {
        HashSet<Integer> enchantments = new HashSet<Integer>();
        if (this.isMagicWeapon) {
            enchantments.addAll(EnchantmentRegistry.magicItemEnchantments);
        }
        if (this.isHealingTool) {
            enchantments.addAll(AphEnchantments.healingItemEnchantments);
        }
        enchantments.addAll(AphEnchantments.areaItemEnchantments);
        return enchantments;
    }

    @Override
    public String getTranslatedTypeName() {
        if (this.isMagicWeapon) {
            return Localization.translate((String)"item", (String)"magicweapon");
        }
        return super.getTranslatedTypeName();
    }

    public void setDrawAttackRotation(InventoryItem item, ItemAttackDrawOptions drawOptions, float attackDirX, float attackDirY, float attackProgress) {
        drawOptions.rotation(0.0f + this.rotationOffset);
    }

    public boolean canItemAttackerHitTarget(ItemAttackerMob attackerMob, float fromX, float fromY, Mob target, InventoryItem item) {
        return this.itemAttackerHasLineOfSightToTarget(attackerMob, fromX, fromY, target, 0.0f);
    }

    public int getItemAttackerAttackRange(ItemAttackerMob mob, InventoryItem item) {
        AphArea lastArea = null;
        for (int i = this.getAreaList((InventoryItem)item).areas.size() - 1; i >= 0; --i) {
            AphArea area = this.getAreaList((InventoryItem)item).areas.get(i);
            if (!area.areaTypes.contains((Object)AphAreaType.DAMAGE) && !area.areaTypes.contains((Object)AphAreaType.DEBUFF)) continue;
            lastArea = area;
            break;
        }
        if (lastArea == null) {
            lastArea = this.getAreaList((InventoryItem)item).areas.get(this.getAreaList((InventoryItem)item).areas.size() - 1);
        }
        return (int)(lastArea.range * 0.9f);
    }
}

