/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.glaiveToolItem;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.registries.EnchantmentRegistry;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LineHitbox;
import necesse.entity.levelEvent.mobAbilityLevelEvent.ToolItemMobAbilityEvent;
import necesse.entity.mobs.AttackAnimMob;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.gfx.GameResources;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.gameTexture.GameSprite;
import necesse.inventory.InventoryItem;
import necesse.inventory.enchants.Enchantable;
import necesse.inventory.enchants.ItemEnchantment;
import necesse.inventory.enchants.ToolItemEnchantment;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.ItemStatTipList;
import necesse.inventory.item.toolItem.ToolItem;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;
import necesse.level.maps.Level;

public class GlaiveToolItem
extends ToolItem {
    public GlaiveToolItem(int enchantCost, OneOfLootItems lootTableCategory) {
        super(enchantCost, lootTableCategory);
        this.setItemCategory("equipment", "weapons", "meleeweapons");
        this.setItemCategory(ItemCategory.equipmentManager, "weapons", "meleeweapons");
        this.setItemCategory(ItemCategory.craftingManager, "equipment", "weapons", "meleeweapons");
        this.keyWords.add("glaive");
        this.damageType = DamageTypeRegistry.MELEE;
        this.attackXOffset = 50;
        this.attackYOffset = 50;
        this.width = 20.0f;
        this.resilienceGain.setBaseValue(2.0f);
        this.tierOneEssencesUpgradeRequirement = "cryoessence";
        this.tierTwoEssencesUpgradeRequirement = "spideressence";
    }

    @Override
    public GameSprite getWorldItemSprite(InventoryItem item, PlayerMob perspective) {
        return this.getItemSprite(item, perspective);
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
    public int getItemAttackerAttackRange(ItemAttackerMob mob, InventoryItem item) {
        return this.getAttackRange(item) / 2;
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        if (animAttack == 0) {
            int animTime = this.getAttackAnimTime(item, attackerMob);
            ToolItemMobAbilityEvent event = new ToolItemMobAbilityEvent(attackerMob, seed, item, x - attackerMob.getX(), y - attackerMob.getY() + attackHeight, animTime, animTime / 2);
            attackerMob.addAndSendAttackerLevelEvent(event);
        }
        return item;
    }

    @Override
    protected SoundSettings getAttackSound() {
        return new SoundSettings(GameResources.singleBladedGlaive).volume(0.6f);
    }

    @Override
    public ArrayList<Shape> getHitboxes(InventoryItem item, AttackAnimMob mob, int aimX, int aimY, ToolItemMobAbilityEvent event, boolean forDebug) {
        ArrayList<Shape> out = new ArrayList<Shape>();
        int mountHeight = 0;
        Mob mount = mob.getMount();
        if (mount != null) {
            mountHeight = mount.getRiderDrawYOffset();
        }
        int attackRange = this.getAttackRange(item) / 2;
        float lastProgress = event.lastHitboxProgress;
        float progress = mob.getAttackAnimProgress();
        float circumference = (float)(Math.PI * (double)attackRange * 2.0);
        float percPerWidth = Math.max(10.0f, this.width) / circumference;
        for (float i = lastProgress; i <= progress; i += percPerWidth) {
            this.addHitbox(out, mob, i, aimX, aimY, attackRange, mountHeight);
            if (forDebug) continue;
            event.lastHitboxProgress = i;
        }
        return out;
    }

    private void addHitbox(List<Shape> list, AttackAnimMob mob, float progress, int aimX, int aimY, int attackRange, int mountHeight) {
        Point2D.Float dir = mob.getDir() == 3 ? GameMath.getAngleDir(-progress * 360.0f - 110.0f) : GameMath.getAngleDir(progress * 360.0f + 110.0f);
        float dirX = dir.x * (float)attackRange;
        float dirY = dir.y * (float)attackRange;
        Line2D.Float attackLine = new Line2D.Float(mob.x - dirX, mob.y - dirY - (float)mob.getCurrentAttackHeight() + (float)mountHeight, mob.x + dirX, mob.y + dirY - (float)mob.getCurrentAttackHeight() + (float)mountHeight);
        if (this.width > 0.0f) {
            list.add(new LineHitbox(attackLine, this.width));
        } else {
            list.add(attackLine);
        }
    }

    @Override
    public ItemAttackDrawOptions setupItemSpriteAttackDrawOptions(ItemAttackDrawOptions options, InventoryItem item, PlayerMob player, int mobDir, float attackDirX, float attackDirY, float attackProgress, Color itemColor) {
        ItemAttackDrawOptions.AttackItemSprite itemSprite = options.itemSprite(this.getAttackSprite(item, player));
        itemSprite.itemRotatePoint(this.attackXOffset, this.attackYOffset);
        itemSprite.itemRawCoords();
        if (itemColor != null) {
            itemSprite.itemColor(itemColor);
        }
        return itemSprite.itemEnd();
    }

    @Override
    public void setDrawAttackRotation(InventoryItem item, ItemAttackDrawOptions drawOptions, float attackDirX, float attackDirY, float attackProgress) {
        if (attackProgress > 0.5f) {
            drawOptions.addedArmRotationOffset(135.0f);
        } else {
            drawOptions.addedArmRotationOffset(-45.0f);
        }
        drawOptions.swingRotation(attackProgress, 360.0f, 65.0f);
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
        return Localization.translate("item", "glaive");
    }
}

