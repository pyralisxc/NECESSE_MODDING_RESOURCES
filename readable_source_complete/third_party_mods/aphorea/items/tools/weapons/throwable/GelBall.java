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
 *  necesse.entity.mobs.Attacker
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.mobs.itemAttacker.ItemAttackSlot
 *  necesse.entity.mobs.itemAttacker.ItemAttackerMob
 *  necesse.entity.projectile.Projectile
 *  necesse.gfx.GameResources
 *  necesse.gfx.gameTooltips.ListGameTooltips
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.item.Item$Rarity
 *  necesse.level.maps.Level
 */
package aphorea.items.tools.weapons.throwable;

import aphorea.items.vanillaitemtypes.weapons.AphThrowToolItem;
import aphorea.projectiles.toolitem.GelProjectile;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.sound.PrimitiveSoundEmitter;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.gameSound.GameSound;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.gfx.GameResources;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.level.maps.Level;

public class GelBall
extends AphThrowToolItem {
    boolean infinity;

    public GelBall() {
        super(10);
        this.rarity = Item.Rarity.NORMAL;
        this.attackAnimTime.setBaseValue(500);
        this.attackDamage.setBaseValue(15.0f);
        this.velocity.setBaseValue(100);
        this.knockback.setBaseValue(0);
        this.attackRange.setBaseValue(200);
        this.attackXOffset = 12;
        this.attackYOffset = 22;
        this.dropsAsMatDeathPenalty = true;
        this.stackSize = 500;
        this.infinity = false;
    }

    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate((String)"itemtooltip", (String)"gelball"));
        tooltips.add(Localization.translate((String)"itemtooltip", (String)"stikybuff1"));
        return tooltips;
    }

    public void showAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, int animAttack, int seed, GNDItemMap mapContent) {
        if (level.isClient()) {
            SoundManager.playSound((GameSound)GameResources.slimeSplash1, (SoundEffect)SoundEffect.effect((PrimitiveSoundEmitter)attackerMob).volume(0.7f).pitch(GameRandom.globalRandom.getFloatBetween(1.0f, 1.1f)));
        }
    }

    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        GelProjectile projectile = new GelProjectile(level, (Mob)attackerMob, attackerMob.x, attackerMob.y, x, y, this.getProjectileVelocity(item, (Mob)attackerMob), this.getAttackRange(item), this.getAttackDamage(item), this.getKnockback(item, (Attacker)attackerMob));
        projectile.resetUniqueID(new GameRandom((long)seed));
        attackerMob.addAndSendAttackerProjectile((Projectile)projectile);
        if (!this.infinity) {
            item.setAmount(item.getAmount() - 1);
        }
        return item;
    }
}

