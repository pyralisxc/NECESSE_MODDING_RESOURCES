/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem;

import java.util.concurrent.atomic.AtomicInteger;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.mobAbilityLevelEvent.RubyGlyphEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.staticBuffs.LifeEssenceStacksBuff;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.RubyStaffProjectile;
import necesse.entity.projectile.modifiers.ResilienceOnHitProjectileModifier;
import necesse.gfx.GameResources;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemInteractAction;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.MagicProjectileToolItem;
import necesse.inventory.item.upgradeUtils.IntUpgradeValue;
import necesse.inventory.lootTable.presets.MagicWeaponsLootTable;
import necesse.level.maps.Level;

public class RubyStaffProjectileToolItem
extends MagicProjectileToolItem
implements ItemInteractAction {
    protected int rightClickLifeEssenceCost = LifeEssenceStacksBuff.STACKS_PER_LIFE_ESSENCE;
    protected IntUpgradeValue explosionRange = new IntUpgradeValue(0, 0.0f);

    public RubyStaffProjectileToolItem() {
        super(1300, MagicWeaponsLootTable.magicWeapons);
        this.rarity = Item.Rarity.RARE;
        this.attackAnimTime.setBaseValue(850);
        this.attackDamage.setBaseValue(68.0f).setUpgradedValue(1.0f, 120.40003f);
        this.velocity.setBaseValue(200);
        this.attackXOffset = 14;
        this.attackYOffset = 4;
        this.attackRange.setBaseValue(300);
        this.knockback.setBaseValue(50);
        this.manaCost.setBaseValue(5.0f).setUpgradedValue(1.0f, 5.0f);
        this.itemAttackerProjectileCanHitWidth = 5.0f;
        this.explosionRange.setBaseValue(50).setUpgradedValue(1.0f, 100);
    }

    @Override
    public void setDrawAttackRotation(InventoryItem item, ItemAttackDrawOptions drawOptions, float attackDirX, float attackDirY, float attackProgress) {
        drawOptions.pointRotation(attackDirX, attackDirY).forEachItemSprite(i -> i.itemRotateOffset(45.0f));
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "rubystafftip1"), 400);
        tooltips.add(Localization.translate("itemtooltip", "rubystafftip2", "value", (Object)(this.rightClickLifeEssenceCost / LifeEssenceStacksBuff.STACKS_PER_LIFE_ESSENCE)), 400);
        tooltips.add(Localization.translate("itemtooltip", "rubystafftip3"), 400);
        return tooltips;
    }

    @Override
    protected SoundSettings getAttackSound() {
        return new SoundSettings(GameResources.rubyStaff).volume(0.3f).basePitch(0.8f).pitchVariance(0.01f);
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        GameRandom random = new GameRandom(seed);
        if (!attackerMob.isPlayer && this.canCastGlyph(attackerMob)) {
            RubyGlyphEvent glyphEvent = this.getGlyphEvent(level, attackerMob, random);
            attackerMob.addAndSendAttackerLevelEvent(glyphEvent);
            mapContent.setBoolean("glyphAttack", true);
        } else {
            RubyStaffProjectile projectile = new RubyStaffProjectile(level, attackerMob, attackerMob.x, attackerMob.y, x, y, this.getProjectileVelocity(item, attackerMob), this.getAttackRange(item), this.getAttackDamage(item), this.getKnockback(item, attackerMob), this.explosionRange.getValue(this.getUpgradeTier(item)), new AtomicInteger(2));
            projectile.setModifier(new ResilienceOnHitProjectileModifier(this.getResilienceGain(item)));
            projectile.resetUniqueID(random);
            attackerMob.addAndSendAttackerProjectile((Projectile)projectile, 50);
            this.consumeMana(attackerMob, item);
        }
        return item;
    }

    @Override
    public boolean canLevelInteract(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item) {
        return this.canCastGlyph(attackerMob);
    }

    @Override
    public InventoryItem onLevelInteract(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int seed, GNDItemMap mapContent) {
        GameRandom random = new GameRandom(seed);
        RubyGlyphEvent event = this.getGlyphEvent(level, attackerMob, random);
        level.entityManager.events.add(event);
        return item;
    }

    public boolean canCastGlyph(Mob mob) {
        return mob.buffManager.getStacks(BuffRegistry.LIFE_ESSENCE) >= this.rightClickLifeEssenceCost;
    }

    public RubyGlyphEvent getGlyphEvent(Level level, Mob player, GameRandom random) {
        if (level.isServer()) {
            for (int i = 0; i < this.rightClickLifeEssenceCost; ++i) {
                player.buffManager.removeStack(BuffRegistry.LIFE_ESSENCE, true, true);
            }
        }
        return new RubyGlyphEvent(player, random);
    }
}

