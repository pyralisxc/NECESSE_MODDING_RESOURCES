/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.localization.Localization
 *  necesse.engine.network.gameNetworkData.GNDItemMap
 *  necesse.engine.sound.PrimitiveSoundEmitter
 *  necesse.engine.sound.SoundEffect
 *  necesse.engine.sound.SoundManager
 *  necesse.engine.sound.gameSound.GameSound
 *  necesse.engine.util.GameBlackboard
 *  necesse.engine.util.GameRandom
 *  necesse.entity.levelEvent.mobAbilityLevelEvent.ToolItemMobAbilityEvent
 *  necesse.entity.mobs.Attacker
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.mobs.buffs.ActiveBuff
 *  necesse.entity.mobs.itemAttacker.ItemAttackSlot
 *  necesse.entity.mobs.itemAttacker.ItemAttackerMob
 *  necesse.entity.projectile.Projectile
 *  necesse.gfx.GameResources
 *  necesse.gfx.gameTooltips.ListGameTooltips
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.item.Item$Rarity
 *  necesse.inventory.item.ItemInteractAction
 *  necesse.inventory.item.ItemStatTipList
 *  necesse.inventory.item.toolItem.ToolItem
 *  necesse.level.maps.Level
 */
package aphorea.items.tools.weapons.magic;

import aphorea.items.tools.weapons.magic.AphMagicProjectileSecondaryAreaToolItem;
import aphorea.projectiles.toolitem.UnstableGelProjectile;
import aphorea.registry.AphBuffs;
import aphorea.utils.AphColors;
import aphorea.utils.area.AphArea;
import aphorea.utils.area.AphAreaList;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.sound.PrimitiveSoundEmitter;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.gameSound.GameSound;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.mobAbilityLevelEvent.ToolItemMobAbilityEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.gfx.GameResources;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemInteractAction;
import necesse.inventory.item.ItemStatTipList;
import necesse.inventory.item.toolItem.ToolItem;
import necesse.level.maps.Level;

public class UnstableGelStaff
extends AphMagicProjectileSecondaryAreaToolItem
implements ItemInteractAction {
    public UnstableGelStaff() {
        super(400, 800, 6.0f);
        this.rarity = Item.Rarity.COMMON;
        this.attackAnimTime.setBaseValue(800);
        this.attackDamage.setBaseValue(30.0f).setUpgradedValue(1.0f, 80.0f);
        this.velocity.setBaseValue(100);
        this.knockback.setBaseValue(0);
        this.attackRange.setBaseValue(600);
        this.manaCost.setBaseValue(3.0f);
        this.attackXOffset = 12;
        this.attackYOffset = 22;
    }

    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate((String)"itemtooltip", (String)"unstablegelstaff"));
        tooltips.add(Localization.translate((String)"itemtooltip", (String)"stikybuff2"));
        tooltips.add(Localization.translate((String)"itemtooltip", (String)"areasecondaryattack", (String)"mana", (Object)Float.valueOf(this.getSecondaryManaCost(item))));
        return tooltips;
    }

    public void addStatTooltips(ItemStatTipList list, InventoryItem currentItem, InventoryItem lastItem, ItemAttackerMob perspective, boolean forceAdd) {
        this.addAttackDamageTip(list, currentItem, lastItem, (Attacker)perspective, forceAdd);
        this.addAttackSpeedTip(list, currentItem, lastItem, perspective);
        this.addCritChanceTip(list, currentItem, lastItem, (Attacker)perspective, forceAdd);
        this.addManaCostTip(list, currentItem, lastItem, (Mob)perspective);
        AphAreaList.addAreasStatTip(list, this.getAreaList(perspective, currentItem), lastItem == null ? null : this.getAreaList(perspective, lastItem), (Attacker)perspective, forceAdd, currentItem, lastItem, (ToolItem)this);
    }

    public void showAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, int animAttack, int seed, GNDItemMap mapContent) {
        if (level.isClient()) {
            SoundManager.playSound((GameSound)GameResources.slimeSplash1, (SoundEffect)SoundEffect.effect((PrimitiveSoundEmitter)attackerMob).volume(0.7f).pitch(GameRandom.globalRandom.getFloatBetween(1.0f, 1.1f)));
        }
    }

    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        UnstableGelProjectile projectile = new UnstableGelProjectile(level, (Mob)attackerMob, attackerMob.x, attackerMob.y, x, y, this.getProjectileVelocity(item, (Mob)attackerMob), this.getAttackRange(item), this.getAttackDamage(item), this.getKnockback(item, (Attacker)attackerMob), 0, seed);
        GameRandom random = new GameRandom((long)seed);
        projectile.resetUniqueID(random);
        attackerMob.addAndSendAttackerProjectile((Projectile)projectile, 20);
        this.consumeMana(attackerMob, item);
        return item;
    }

    public void hitMob(InventoryItem item, ToolItemMobAbilityEvent event, Level level, Mob target, Mob attacker) {
        super.hitMob(item, event, level, target, attacker);
        target.addBuff(new ActiveBuff(AphBuffs.STICKY, target, 2000, null), true);
    }

    @Override
    public AphAreaList getAreaList(ItemAttackerMob attackerMob, InventoryItem item) {
        return new AphAreaList(new AphArea(200.0f, AphColors.unstableGel).setDamageArea(this.getAttackDamage(item).modDamage(0.7f)));
    }
}

