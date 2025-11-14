/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.network.gameNetworkData.GNDItemMap
 *  necesse.engine.util.GameRandom
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.mobs.itemAttacker.ItemAttackSlot
 *  necesse.entity.mobs.itemAttacker.ItemAttackerMob
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.enchants.Enchantable
 *  necesse.inventory.enchants.ItemEnchantment
 *  necesse.inventory.enchants.ToolItemEnchantment
 *  necesse.inventory.item.ItemInteractAction
 *  necesse.inventory.item.toolItem.ToolItem
 *  necesse.level.maps.Level
 */
package aphorea.items.tools.weapons.magic;

import aphorea.items.vanillaitemtypes.weapons.AphMagicProjectileToolItem;
import aphorea.registry.AphEnchantments;
import aphorea.registry.AphModifiers;
import aphorea.utils.area.AphAreaList;
import java.util.HashSet;
import java.util.Set;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.inventory.InventoryItem;
import necesse.inventory.enchants.Enchantable;
import necesse.inventory.enchants.ItemEnchantment;
import necesse.inventory.enchants.ToolItemEnchantment;
import necesse.inventory.item.ItemInteractAction;
import necesse.inventory.item.toolItem.ToolItem;
import necesse.level.maps.Level;

public abstract class AphMagicProjectileSecondaryAreaToolItem
extends AphMagicProjectileToolItem
implements ItemInteractAction {
    int secondaryAttackAnimTime;
    float consumeManaSecondary;

    public AphMagicProjectileSecondaryAreaToolItem(int enchantCost, int secondaryAttackAnimTime, float consumeManaSecondary) {
        super(enchantCost);
        this.secondaryAttackAnimTime = secondaryAttackAnimTime;
        this.consumeManaSecondary = consumeManaSecondary;
    }

    public abstract AphAreaList getAreaList(ItemAttackerMob var1, InventoryItem var2);

    public float getSecondaryManaCost(InventoryItem item) {
        return this.consumeManaSecondary * this.getManaUsageModifier(item);
    }

    public int getLevelInteractAttackAnimTime(InventoryItem item, ItemAttackerMob attackerMob) {
        return Math.round((float)this.secondaryAttackAnimTime * (1.0f / this.getAttackSpeedModifier(item, attackerMob)));
    }

    public boolean canLevelInteract(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item) {
        return !attackerMob.isAttacking;
    }

    public InventoryItem onLevelInteract(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int seed, GNDItemMap mapContent) {
        this.consumeManaSecondary(attackerMob, item);
        float rangeModifier = 1.0f + ((Float)this.getEnchantment(item).getModifier(AphModifiers.TOOL_AREA_RANGE)).floatValue();
        this.getAreaList(attackerMob, item).execute((Mob)attackerMob, attackerMob.x, attackerMob.y, rangeModifier, item, (ToolItem)this, true);
        return item;
    }

    public void consumeManaSecondary(ItemAttackerMob attackerMob, InventoryItem item) {
        float manaCost = this.getSecondaryManaCost(item);
        if (manaCost > 0.0f) {
            attackerMob.useMana(manaCost, attackerMob.isPlayer && ((PlayerMob)attackerMob).isServerClient() ? ((PlayerMob)attackerMob).getServerClient() : null);
        }
    }

    public ToolItemEnchantment getRandomEnchantment(GameRandom random, InventoryItem item) {
        return (ToolItemEnchantment)Enchantable.getRandomEnchantment((GameRandom)random, this.getValidEnchantmentIDs(item), (int)this.getEnchantmentID(item), ToolItemEnchantment.class);
    }

    public boolean isValidEnchantment(InventoryItem item, ItemEnchantment enchantment) {
        return this.getValidEnchantmentIDs(item).contains(enchantment.getID());
    }

    public Set<Integer> getValidEnchantmentIDs(InventoryItem item) {
        HashSet<Integer> enchantments = new HashSet<Integer>(super.getValidEnchantmentIDs(item));
        enchantments.addAll(AphEnchantments.areaItemEnchantments);
        return enchantments;
    }
}

