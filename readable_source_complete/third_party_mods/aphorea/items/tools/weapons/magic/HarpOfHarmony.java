/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.localization.Localization
 *  necesse.engine.network.gameNetworkData.GNDItemMap
 *  necesse.engine.registries.EnchantmentRegistry
 *  necesse.engine.sound.PrimitiveSoundEmitter
 *  necesse.engine.sound.SoundEffect
 *  necesse.engine.sound.SoundManager
 *  necesse.engine.sound.gameSound.GameSound
 *  necesse.engine.util.GameBlackboard
 *  necesse.engine.util.GameRandom
 *  necesse.entity.mobs.Attacker
 *  necesse.entity.mobs.GameDamage
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.mobs.buffs.ActiveBuff
 *  necesse.entity.mobs.itemAttacker.ItemAttackSlot
 *  necesse.entity.mobs.itemAttacker.ItemAttackerMob
 *  necesse.entity.projectile.Projectile
 *  necesse.gfx.gameTooltips.ListGameTooltips
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.item.Item$Rarity
 *  necesse.inventory.item.ItemInteractAction
 *  necesse.inventory.item.ItemStatTipList
 *  necesse.inventory.item.toolItem.ToolItem
 *  necesse.inventory.item.upgradeUtils.FloatUpgradeValue
 *  necesse.level.maps.Level
 */
package aphorea.items.tools.weapons.magic;

import aphorea.AphResources;
import aphorea.items.vanillaitemtypes.weapons.AphMagicProjectileToolItem;
import aphorea.projectiles.toolitem.MusicalNoteProjectile;
import aphorea.registry.AphDamageType;
import aphorea.registry.AphEnchantments;
import aphorea.registry.AphModifiers;
import aphorea.utils.AphColors;
import aphorea.utils.area.AphArea;
import aphorea.utils.area.AphAreaList;
import aphorea.utils.area.AphAreaType;
import aphorea.utils.magichealing.AphMagicHealingBuff;
import java.util.HashSet;
import java.util.Set;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.registries.EnchantmentRegistry;
import necesse.engine.sound.PrimitiveSoundEmitter;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.gameSound.GameSound;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemInteractAction;
import necesse.inventory.item.ItemStatTipList;
import necesse.inventory.item.toolItem.ToolItem;
import necesse.inventory.item.upgradeUtils.FloatUpgradeValue;
import necesse.level.maps.Level;

public class HarpOfHarmony
extends AphMagicProjectileToolItem
implements ItemInteractAction {
    protected FloatUpgradeValue attackDamage2 = new FloatUpgradeValue(0.0f, 0.2f);
    protected FloatUpgradeValue healing = new FloatUpgradeValue(0.0f, 0.2f);

    public HarpOfHarmony() {
        super(1000);
        this.rarity = Item.Rarity.RARE;
        this.attackAnimTime.setBaseValue(500);
        this.manaCost.setBaseValue(2.0f);
        this.attackRange.setBaseValue(500);
        this.attackXOffset = 22;
        this.attackYOffset = 22;
        this.attackDamage.setBaseValue(30.0f).setUpgradedValue(1.0f, 60.0f);
        this.attackDamage2.setBaseValue(3.0f).setUpgradedValue(1.0f, 6.0f);
        this.healing.setBaseValue(3.0f).setUpgradedValue(1.0f, 6.0f);
    }

    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        AphAreaList areaList = this.getAreaList(item);
        MusicalNoteProjectile projectile = new MusicalNoteProjectile(level, (Mob)attackerMob, attackerMob.x, attackerMob.y, x, y, this.getProjectileVelocity(item, (Mob)attackerMob), this.getAttackRange(item), this.getAttackDamage(item), this.getKnockback(item, (Attacker)attackerMob));
        projectile.resetUniqueID(new GameRandom((long)seed));
        attackerMob.addAndSendAttackerProjectile((Projectile)projectile, 20);
        this.consumeMana(attackerMob, item);
        if (areaList.someType(AphAreaType.HEALING)) {
            this.onHealingToolItemUsed((Mob)attackerMob, item);
        }
        float rangeModifier = 1.0f + ((Float)this.getEnchantment(item).getModifier(AphModifiers.TOOL_AREA_RANGE)).floatValue();
        areaList.execute((Mob)attackerMob, attackerMob.x, attackerMob.y, rangeModifier, item, (ToolItem)this, true);
        return item;
    }

    public void showAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, int animAttack, int seed, GNDItemMap mapContent) {
        if (level.isClient()) {
            float distance = attackerMob.getDistance((float)x, (float)y);
            GameSound[] notes = AphResources.SOUNDS.HARP.All;
            int noteIndex = Math.min(notes.length - 1, (int)(distance / (400.0f / (float)notes.length)));
            SoundManager.playSound((GameSound)notes[noteIndex], (SoundEffect)SoundEffect.effect((PrimitiveSoundEmitter)attackerMob));
        }
    }

    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate((String)"itemtooltip", (String)"harpofharmony"));
        tooltips.add(Localization.translate((String)"itemtooltip", (String)"inspiration"));
        return tooltips;
    }

    public void addStatTooltips(ItemStatTipList list, InventoryItem currentItem, InventoryItem lastItem, ItemAttackerMob perspective, boolean forceAdd) {
        this.addAttackDamageTip(list, currentItem, lastItem, (Attacker)perspective, forceAdd);
        this.addAttackSpeedTip(list, currentItem, lastItem, perspective);
        this.addResilienceGainTip(list, currentItem, lastItem, (Mob)perspective, forceAdd);
        this.addCritChanceTip(list, currentItem, lastItem, (Attacker)perspective, forceAdd);
        this.addManaCostTip(list, currentItem, lastItem, (Mob)perspective);
        AphAreaList.addAreasStatTip(list, this.getAreaList(currentItem), lastItem == null ? null : this.getAreaList(lastItem), (Attacker)perspective, forceAdd, currentItem, lastItem, (ToolItem)this);
    }

    public void onHealingToolItemUsed(Mob mob, InventoryItem item) {
        mob.buffManager.getArrayBuffs().stream().filter(buff -> buff.buff instanceof AphMagicHealingBuff).forEach(buff -> ((AphMagicHealingBuff)buff.buff).onMagicalHealingItemUsed((ActiveBuff)buff, mob, (ToolItem)this, item));
    }

    public Set<Integer> getValidEnchantmentIDs(InventoryItem item) {
        HashSet<Integer> enchantments = new HashSet<Integer>();
        enchantments.addAll(EnchantmentRegistry.magicItemEnchantments);
        enchantments.addAll(AphEnchantments.healingItemEnchantments);
        enchantments.addAll(AphEnchantments.areaItemEnchantments);
        return enchantments;
    }

    public AphAreaList getAreaList(InventoryItem item) {
        return new AphAreaList(new AphArea(200.0f, 0.3f, AphColors.spinel).setDamageArea(new GameDamage(AphDamageType.INSPIRATION, this.attackDamage2.getValue(item.item.getUpgradeTier(item)).floatValue())).setHealingArea((int)this.healing.getValue(item.item.getUpgradeTier(item)).floatValue()).setBuffArea(5000, "harmonybuff"));
    }
}

