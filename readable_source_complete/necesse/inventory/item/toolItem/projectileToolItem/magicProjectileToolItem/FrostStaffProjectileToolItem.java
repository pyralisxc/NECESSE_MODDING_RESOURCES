/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem;

import java.awt.geom.Point2D;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.FrostStaffProjectile;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.modifiers.ResilienceOnHitProjectileModifier;
import necesse.gfx.GameResources;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.MagicProjectileToolItem;
import necesse.inventory.lootTable.presets.MagicWeaponsLootTable;
import necesse.level.maps.Level;

public class FrostStaffProjectileToolItem
extends MagicProjectileToolItem {
    public FrostStaffProjectileToolItem() {
        super(500, MagicWeaponsLootTable.magicWeapons);
        this.rarity = Item.Rarity.COMMON;
        this.attackAnimTime.setBaseValue(400);
        this.attackCooldownTime.setBaseValue(500);
        this.attackDamage.setBaseValue(19.0f).setUpgradedValue(1.0f, 84.00002f);
        this.velocity.setBaseValue(400);
        this.attackXOffset = 26;
        this.attackYOffset = 26;
        this.attackRange.setBaseValue(800);
        this.knockback.setBaseValue(50);
        this.manaCost.setBaseValue(1.0f).setUpgradedValue(1.0f, 1.0f);
        this.resilienceGain.setBaseValue(0.75f);
        this.itemAttackerProjectileCanHitWidth = 32.0f;
        this.itemAttackerPredictionDistanceOffset = -20.0f;
        this.canBeUsedForRaids = true;
    }

    @Override
    public void setDrawAttackRotation(InventoryItem item, ItemAttackDrawOptions drawOptions, float attackDirX, float attackDirY, float attackProgress) {
        super.setDrawAttackRotation(item, drawOptions, attackDirX, attackDirY, attackProgress);
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "froststafftip"));
        return tooltips;
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        int velocity = this.getProjectileVelocity(item, attackerMob);
        int range = this.getAttackRange(item);
        int knockback = this.getKnockback(item, attackerMob);
        Point2D.Float attackDir = GameMath.normalize((float)x - attackerMob.x, (float)y - attackerMob.y);
        Point2D.Float pos1 = GameMath.getPerpendicularPoint(attackerMob.x, attackerMob.y, 10.0f, attackDir);
        int attackRange = this.getAttackRange(item);
        FrostStaffProjectile p1 = new FrostStaffProjectile(level, attackerMob, pos1.x, pos1.y, attackDir.x, attackDir.y, (float)velocity / 10.0f, velocity, attackRange, range, this.getAttackDamage(item), knockback);
        p1.setModifier(new ResilienceOnHitProjectileModifier(this.getResilienceGain(item)));
        p1.resetUniqueID(new GameRandom(seed));
        attackerMob.addAndSendAttackerProjectile((Projectile)p1, 20);
        Point2D.Float pos2 = GameMath.getPerpendicularPoint(attackerMob.x, attackerMob.y, -10.0f, attackDir);
        FrostStaffProjectile p2 = new FrostStaffProjectile(level, attackerMob, pos2.x, pos2.y, attackDir.x, attackDir.y, (float)velocity / 10.0f, velocity, attackRange, range, this.getAttackDamage(item), knockback);
        p2.setModifier(new ResilienceOnHitProjectileModifier(this.getResilienceGain(item)));
        p2.resetUniqueID(new GameRandom(seed));
        attackerMob.addAndSendAttackerProjectile((Projectile)p2, 20);
        this.consumeMana(attackerMob, item);
        return item;
    }

    @Override
    protected SoundSettings getAttackSound() {
        return new SoundSettings(GameResources.jingle).volume(0.6f);
    }
}

