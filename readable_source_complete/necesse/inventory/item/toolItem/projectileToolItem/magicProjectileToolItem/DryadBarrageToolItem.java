/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem;

import java.awt.Color;
import java.awt.geom.Point2D;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.WaitForSecondsEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.attackHandler.DryadBarrageAttackHandler;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.SpiritOrbProjectile;
import necesse.entity.projectile.modifiers.ResilienceOnHitProjectileModifier;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.SpacerGameTooltip;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.MagicProjectileToolItem;
import necesse.inventory.lootTable.presets.MagicWeaponsLootTable;
import necesse.level.maps.Level;

public class DryadBarrageToolItem
extends MagicProjectileToolItem {
    private final int dryadHauntedStacksOnHit = 3;

    public DryadBarrageToolItem() {
        super(1550, MagicWeaponsLootTable.magicWeapons);
        this.rarity = Item.Rarity.UNCOMMON;
        this.attackAnimTime.setBaseValue(1000);
        this.attackDamage.setBaseValue(60.0f).setUpgradedValue(1.0f, 91.00002f);
        this.velocity.setBaseValue(200);
        this.attackXOffset = 20;
        this.attackYOffset = 12;
        this.manaCost.setBaseValue(10.0f).setUpgradedValue(1.0f, 10.0f);
    }

    @Override
    public void setDrawAttackRotation(InventoryItem item, ItemAttackDrawOptions drawOptions, float attackDirX, float attackDirY, float attackProgress) {
        if (attackDirX < 0.0f) {
            drawOptions.rotation(-GameMath.getAngle(new Point2D.Float(attackDirX, attackDirY)) + 30.0f + 180.0f);
        } else {
            drawOptions.rotation(GameMath.getAngle(new Point2D.Float(attackDirX, attackDirY)) + 45.0f);
        }
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "dryadbarragetip"), 400);
        tooltips.add(new SpacerGameTooltip(5));
        tooltips.add(new StringTooltips(Localization.translate("itemtooltip", "dryadhauntweapontip", "value", (Object)3), new Color(30, 177, 143), 400));
        return tooltips;
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        attackerMob.startAttackHandler(new DryadBarrageAttackHandler(attackerMob, slot, item, this, 10, seed, x, y));
        return item;
    }

    public void triggerBarrageAttack(Level level, final int x, final int y, final ItemAttackerMob attackerMob, final InventoryItem item, int seed, int projectileCount) {
        GameRandom random = new GameRandom(seed);
        for (int i = 0; i < projectileCount; ++i) {
            final GameRandom beamRandom = random.nextSeeded(27 * (i + 1));
            level.entityManager.events.addHidden(new WaitForSecondsEvent(0.075f * (float)(i + 1)){

                @Override
                public void onWaitOver() {
                    SpiritOrbProjectile projectile = new SpiritOrbProjectile(attackerMob.x, attackerMob.y, x, y, DryadBarrageToolItem.this.getProjectileVelocity(item, attackerMob), 1000, DryadBarrageToolItem.this.getAttackDamage(item), 3, DryadBarrageToolItem.this.getKnockback(item, attackerMob), attackerMob);
                    projectile.setModifier(new ResilienceOnHitProjectileModifier(DryadBarrageToolItem.this.getResilienceGain(item)));
                    projectile.getUniqueID(beamRandom);
                    attackerMob.addAndSendAttackerProjectile(projectile, 70, (beamRandom.nextFloat() - 0.5f) * 10.0f);
                }
            });
        }
    }

    @Override
    protected SoundSettings getSwingSound() {
        return null;
    }
}

