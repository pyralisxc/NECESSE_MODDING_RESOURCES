/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.spearToolItem;

import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Set;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.registries.EnchantmentRegistry;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LineHitbox;
import necesse.entity.levelEvent.mobAbilityLevelEvent.ToolItemMobAbilityEvent;
import necesse.entity.mobs.AttackAnimMob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.gfx.GameResources;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.enchants.Enchantable;
import necesse.inventory.enchants.ItemEnchantment;
import necesse.inventory.enchants.ToolItemEnchantment;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.ItemStatTipList;
import necesse.inventory.item.toolItem.ToolItem;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;
import necesse.level.maps.Level;

public class SpearToolItem
extends ToolItem {
    public SpearToolItem(int enchantCost, OneOfLootItems lootTableCategory) {
        super(enchantCost, lootTableCategory);
        this.setItemCategory("equipment", "weapons", "meleeweapons");
        this.setItemCategory(ItemCategory.equipmentManager, "weapons", "meleeweapons");
        this.setItemCategory(ItemCategory.craftingManager, "equipment", "weapons", "meleeweapons");
        this.keyWords.add("spear");
        this.damageType = DamageTypeRegistry.MELEE;
        this.attackXOffset = 26;
        this.attackYOffset = 16;
        this.width = 16.0f;
        this.resilienceGain.setBaseValue(2.0f);
        this.tierOneEssencesUpgradeRequirement = "primordialessence";
        this.tierTwoEssencesUpgradeRequirement = "bloodessence";
    }

    @Override
    public GameSprite getWorldItemSprite(InventoryItem item, PlayerMob perspective) {
        return this.getItemSprite(item, perspective);
    }

    @Override
    protected ListGameTooltips getBaseTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getBaseTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "speartip"));
        return tooltips;
    }

    @Override
    public void addStatTooltips(ItemStatTipList list, InventoryItem currentItem, InventoryItem lastItem, ItemAttackerMob perspective, boolean forceAdd) {
        this.addAttackDamageTip(list, currentItem, lastItem, perspective, forceAdd);
        this.addAttackSpeedTip(list, currentItem, lastItem, perspective);
        this.addResilienceGainTip(list, currentItem, lastItem, perspective, forceAdd);
        this.addKnockbackTip(list, currentItem, lastItem, perspective);
        this.addCritChanceTip(list, currentItem, lastItem, perspective, forceAdd);
    }

    @Override
    public int getItemAttackerMinimumAttackRange(ItemAttackerMob attackerMob, InventoryItem item) {
        return this.getAttackRange(item) / 2;
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        if (animAttack == 0) {
            int animTime = this.getAttackAnimTime(item, attackerMob);
            ToolItemMobAbilityEvent event = new ToolItemMobAbilityEvent(attackerMob, seed, item, x - attackerMob.getX(), y - attackerMob.getY(), animTime, animTime / 2);
            attackerMob.addAndSendAttackerLevelEvent(event);
        }
        return item;
    }

    @Override
    protected SoundSettings getAttackSound() {
        return new SoundSettings(GameResources.defaultSpear).volume(0.7f);
    }

    @Override
    public ArrayList<Shape> getHitboxes(InventoryItem item, AttackAnimMob mob, int aimX, int aimY, ToolItemMobAbilityEvent event, boolean forDebug) {
        ArrayList<Shape> out = new ArrayList<Shape>();
        float attackRange = this.getAttackRange(item);
        Point2D.Float dir = GameMath.normalize(aimX, aimY);
        float yOffset = Math.min(mob.getCurrentAttackDrawYOffset() + mob.getStartAttackHeight(), 0);
        Line2D.Float attackLine = new Line2D.Float(mob.x + dir.x * attackRange / 2.0f, mob.y + dir.y * attackRange / 2.0f + yOffset, mob.x + dir.x * attackRange, mob.y + dir.y * attackRange + yOffset);
        if (this.width > 0.0f) {
            out.add(new LineHitbox(attackLine, this.width));
        } else {
            out.add(attackLine);
        }
        return out;
    }

    @Override
    public void setDrawAttackRotation(InventoryItem item, ItemAttackDrawOptions drawOptions, float attackDirX, float attackDirY, float attackProgress) {
        drawOptions.pointRotation(attackDirX, attackDirY);
        drawOptions.thrustOffsets(attackDirX, attackDirY, attackProgress);
    }

    @Override
    public boolean animDrawBehindHand(InventoryItem item) {
        return true;
    }

    @Override
    public ToolItemEnchantment getRandomEnchantment(GameRandom random, InventoryItem item) {
        return Enchantable.getRandomEnchantment(random, EnchantmentRegistry.meleeItemEnchantments, this.getEnchantmentID(item), ToolItemEnchantment.class);
    }

    @Override
    public boolean isValidEnchantment(InventoryItem item, ItemEnchantment enchantment) {
        return EnchantmentRegistry.meleeItemEnchantments.contains(enchantment.getID());
    }

    @Override
    public Set<Integer> getValidEnchantmentIDs(InventoryItem item) {
        return EnchantmentRegistry.meleeItemEnchantments;
    }

    @Override
    public String getTranslatedTypeName() {
        return Localization.translate("item", "spear");
    }
}

