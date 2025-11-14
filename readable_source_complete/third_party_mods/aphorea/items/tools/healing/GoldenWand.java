/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.network.gameNetworkData.GNDItemMap
 *  necesse.engine.sound.PrimitiveSoundEmitter
 *  necesse.engine.sound.SoundEffect
 *  necesse.engine.sound.SoundManager
 *  necesse.engine.sound.gameSound.GameSound
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.itemAttacker.ItemAttackerMob
 *  necesse.entity.projectile.Projectile
 *  necesse.gfx.GameResources
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.item.Item$Rarity
 *  necesse.level.maps.Level
 */
package aphorea.items.tools.healing;

import aphorea.items.tools.healing.AphHealingProjectileToolItem;
import aphorea.projectiles.toolitem.GoldenWandProjectile;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.sound.PrimitiveSoundEmitter;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.gameSound.GameSound;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.gfx.GameResources;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.level.maps.Level;

public class GoldenWand
extends AphHealingProjectileToolItem {
    public GoldenWand() {
        super(350);
        this.rarity = Item.Rarity.NORMAL;
        this.attackAnimTime.setBaseValue(800);
        this.attackRange.setBaseValue(500);
        this.velocity.setBaseValue(200);
        this.manaCost.setBaseValue(5.0f);
        this.attackXOffset += 10;
        this.attackYOffset += 15;
        this.magicHealing.setBaseValue(8).setUpgradedValue(1.0f, 20);
    }

    @Override
    protected Projectile[] getProjectiles(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item) {
        return new Projectile[]{new GoldenWandProjectile(this.getHealing(item), this, item, level, (Mob)attackerMob, attackerMob.x, attackerMob.y, x, y, this.getProjectileVelocity(item, (Mob)attackerMob), this.getAttackRange(item))};
    }

    @Override
    public void showAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, int animAttack, int seed, GNDItemMap mapContent) {
        if (level.isClient()) {
            SoundManager.playSound((GameSound)GameResources.magicbolt1, (SoundEffect)SoundEffect.effect((PrimitiveSoundEmitter)attackerMob).volume(1.0f).pitch(1.0f));
        }
    }
}

