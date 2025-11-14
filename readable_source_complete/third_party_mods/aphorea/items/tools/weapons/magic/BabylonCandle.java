/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.network.gameNetworkData.GNDItemMap
 *  necesse.engine.sound.PrimitiveSoundEmitter
 *  necesse.engine.sound.SoundEffect
 *  necesse.engine.sound.SoundManager
 *  necesse.engine.sound.gameSound.GameSound
 *  necesse.engine.util.GameRandom
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.itemAttacker.ItemAttackSlot
 *  necesse.entity.mobs.itemAttacker.ItemAttackerMob
 *  necesse.entity.projectile.Projectile
 *  necesse.entity.projectile.modifiers.ProjectileModifier
 *  necesse.entity.projectile.modifiers.ResilienceOnHitProjectileModifier
 *  necesse.gfx.GameResources
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.item.Item$Rarity
 *  necesse.level.maps.Level
 */
package aphorea.items.tools.weapons.magic;

import aphorea.items.vanillaitemtypes.weapons.AphMagicProjectileToolItem;
import aphorea.projectiles.toolitem.BabylonCandleProjectile;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.sound.PrimitiveSoundEmitter;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.gameSound.GameSound;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.modifiers.ProjectileModifier;
import necesse.entity.projectile.modifiers.ResilienceOnHitProjectileModifier;
import necesse.gfx.GameResources;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.level.maps.Level;

public class BabylonCandle
extends AphMagicProjectileToolItem {
    public BabylonCandle() {
        super(1550);
        this.rarity = Item.Rarity.EPIC;
        this.attackAnimTime.setBaseValue(200);
        this.attackDamage.setBaseValue(50.0f).setUpgradedValue(1.0f, 70.0f);
        this.velocity.setBaseValue(50);
        this.attackXOffset = 6;
        this.attackYOffset = 20;
        this.attackRange.setBaseValue(400);
        this.manaCost.setBaseValue(2.0f);
        this.resilienceGain.setBaseValue(0.5f);
        this.itemAttackerProjectileCanHitWidth = 25.0f;
        this.itemAttackerPredictionDistanceOffset = -20.0f;
    }

    public void showAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, int animAttack, int seed, GNDItemMap mapContent) {
        if (level.isClient()) {
            SoundManager.playSound((GameSound)GameResources.magicbolt2, (SoundEffect)SoundEffect.effect((PrimitiveSoundEmitter)attackerMob).volume(0.4f).pitch(GameRandom.globalRandom.getFloatBetween(0.8f, 0.9f)));
        }
    }

    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        BabylonCandleProjectile projectile = new BabylonCandleProjectile(level, attackerMob.x, attackerMob.y, x, y, this.getAttackRange(item), this.getAttackDamage(item), (Mob)attackerMob);
        projectile.setModifier((ProjectileModifier)new ResilienceOnHitProjectileModifier(this.getResilienceGain(item)));
        projectile.resetUniqueID(new GameRandom((long)seed));
        attackerMob.addAndSendAttackerProjectile((Projectile)projectile, 20);
        this.consumeMana(attackerMob, item);
        return item;
    }
}

