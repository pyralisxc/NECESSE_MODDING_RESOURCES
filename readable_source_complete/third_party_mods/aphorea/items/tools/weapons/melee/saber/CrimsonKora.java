/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.localization.Localization
 *  necesse.engine.network.Packet
 *  necesse.engine.network.gameNetworkData.GNDItemMap
 *  necesse.engine.registries.BuffRegistry
 *  necesse.engine.sound.PrimitiveSoundEmitter
 *  necesse.engine.sound.SoundEffect
 *  necesse.engine.sound.SoundManager
 *  necesse.engine.sound.gameSound.GameSound
 *  necesse.engine.util.GameBlackboard
 *  necesse.engine.util.GameMath
 *  necesse.engine.util.GameRandom
 *  necesse.entity.ParticleTypeSwitcher
 *  necesse.entity.mobs.Attacker
 *  necesse.entity.mobs.GameDamage
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.mobs.attackHandler.AttackHandler
 *  necesse.entity.mobs.buffs.ActiveBuff
 *  necesse.entity.mobs.itemAttacker.ItemAttackSlot
 *  necesse.entity.mobs.itemAttacker.ItemAttackerMob
 *  necesse.entity.particle.Particle$GType
 *  necesse.entity.projectile.Projectile
 *  necesse.gfx.GameResources
 *  necesse.gfx.gameTooltips.ListGameTooltips
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.item.Item$Rarity
 *  necesse.level.maps.Level
 */
package aphorea.items.tools.weapons.melee.saber;

import aphorea.items.tools.weapons.melee.saber.AphSaberToolItem;
import aphorea.items.tools.weapons.melee.saber.logic.SaberAttackHandler;
import aphorea.packets.AphCustomPushPacket;
import aphorea.projectiles.toolitem.AircutProjectile;
import aphorea.ui.SaberAttackUIManger;
import aphorea.utils.AphColors;
import java.awt.geom.Point2D;
import necesse.engine.localization.Localization;
import necesse.engine.network.Packet;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.sound.PrimitiveSoundEmitter;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.gameSound.GameSound;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.attackHandler.AttackHandler;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.Projectile;
import necesse.gfx.GameResources;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.level.maps.Level;

public class CrimsonKora
extends AphSaberToolItem {
    public CrimsonKora() {
        super(1900);
        this.rarity = Item.Rarity.EPIC;
        this.attackDamage.setBaseValue(120.0f).setUpgradedValue(1.0f, 120.0f);
        this.knockback.setBaseValue(200);
        this.attackRange.setBaseValue(80);
        this.chargeAnimTime.setBaseValue(500);
        this.attackXOffset = 10;
        this.attackYOffset = 10;
    }

    @Override
    public Projectile getProjectile(Level level, int x, int y, int targetX, int targetY, ItemAttackerMob attackerMob, InventoryItem item, float powerPercent, int seed) {
        return new AircutProjectile.CrimsonAircutProjectile(level, (Mob)attackerMob, x, y, targetX, targetY, 300.0f * powerPercent, (int)(400.0f * powerPercent), this.getAttackDamage(item).modDamage(item.getGndData().getFloat("modifyDamage", 1.0f)).modDamage(powerPercent * 0.75f), (int)((float)this.getKnockback(item, (Attacker)attackerMob) * powerPercent));
    }

    @Override
    public void superOnAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        float chargePercent = this.chargePercent(item);
        if (chargePercent >= 0.5f && item.getGndData().getBoolean("doDash")) {
            attackerMob.buffManager.addBuff(new ActiveBuff(BuffRegistry.FOW_ACTIVE, (Mob)attackerMob, 0.15f, null), level.isServer());
            attackerMob.buffManager.forceUpdateBuffs();
            if (attackerMob.isServer()) {
                int strength = (int)(200.0f * this.chargePercent(item));
                Point2D.Float dir = GameMath.normalize((float)((float)x - attackerMob.x), (float)((float)y - attackerMob.y));
                level.getServer().network.sendToClientsAtEntireLevel((Packet)new AphCustomPushPacket((Mob)attackerMob, dir.x, dir.y, strength, AphColors.crimson_kora_dark), level);
            }
        }
        super.superOnAttack(level, x, y, attackerMob, attackHeight, item, slot, animAttack, seed, mapContent);
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        int animTime = this.getChargeAnimTime(item, attackerMob);
        item.getGndData().setBoolean("charging", false);
        item.getGndData().setFloat("modifyDamage", 1.0f);
        item.getGndData().setBoolean("doDash", false);
        attackerMob.startAttackHandler((AttackHandler)new CrimsonKoraAttackHandler(attackerMob, slot, item, this, animTime, false, seed));
        return item;
    }

    @Override
    public void showAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, int animAttack, int seed, GNDItemMap mapContent) {
        super.showAttack(level, x, y, attackerMob, attackHeight, item, animAttack, seed, mapContent);
        if (level.isClient() && item.getGndData().getBoolean("charged") && !item.getGndData().getBoolean("charging")) {
            float damagePercent = item.getGndData().getFloat("modifyDamage", 1.0f);
            float chargePercent = this.chargePercent(item);
            float shownEffect = chargePercent * damagePercent * 0.5f;
            level.getClient().startCameraShake(attackerMob.x, attackerMob.y, (int)(1000.0f * shownEffect), 40, 3.0f * shownEffect, 3.0f * shownEffect, true);
            SoundManager.playSound((GameSound)GameResources.shake, (SoundEffect)SoundEffect.effect((PrimitiveSoundEmitter)attackerMob).volume(shownEffect - 0.3f));
            if (chargePercent > 0.6f) {
                SoundManager.playSound((GameSound)GameResources.electricExplosion, (SoundEffect)SoundEffect.effect((PrimitiveSoundEmitter)attackerMob).volume(shownEffect - 0.3f));
            }
        }
    }

    @Override
    public InventoryItem onLevelInteract(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int seed, GNDItemMap mapContent) {
        int animTime = this.getChargeAnimTime(item, attackerMob) * 2;
        item.getGndData().setBoolean("charging", false);
        item.getGndData().setFloat("modifyDamage", 2.0f);
        item.getGndData().setBoolean("doDash", true);
        attackerMob.startAttackHandler(new CrimsonKoraAttackHandler(attackerMob, slot, item, this, animTime, false, seed).startFromInteract());
        return item;
    }

    @Override
    public boolean canLevelInteract(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item) {
        return !attackerMob.isRiding();
    }

    @Override
    public GameDamage getAttackDamage(InventoryItem item) {
        return super.getAttackDamage(item).modDamage(item.getGndData().getFloat("modifyDamage", 1.0f));
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.removeLast();
        tooltips.add(Localization.translate((String)"itemtooltip", (String)"crimsonkora"));
        return tooltips;
    }

    public static class CrimsonKoraAttackHandler
    extends SaberAttackHandler {
        ParticleTypeSwitcher spinningTypeSwitcher = new ParticleTypeSwitcher(new Particle.GType[]{Particle.GType.COSMETIC});

        public CrimsonKoraAttackHandler(ItemAttackerMob attackerMob, ItemAttackSlot slot, InventoryItem item, AphSaberToolItem toolItem, int chargeTime, boolean isAuto, int seed) {
            super(attackerMob, slot, item, toolItem, chargeTime, isAuto, seed);
        }

        @Override
        public void onUpdate() {
            super.onUpdate();
            float damagePercent = this.item.getGndData().getFloat("modifyDamage", 1.0f);
            float chargePercent = SaberAttackUIManger.barPercent(this.getChargePercent());
            float shownEffect = chargePercent * damagePercent * 0.5f;
            this.attackerMob.getLevel().lightManager.refreshParticleLightFloat(this.attackerMob.x, this.attackerMob.y, AphColors.crimson_kora_light, 1.0f, 50 + (int)(150.0f * shownEffect));
            if (this.attackerMob.isClient()) {
                int i = 0;
                while ((float)i < 3.0f * shownEffect) {
                    this.attackerMob.getLevel().entityManager.addParticle(this.attackerMob.x + GameRandom.globalRandom.floatGaussian() * 4.0f, this.attackerMob.y + GameRandom.globalRandom.floatGaussian() * 4.0f, this.spinningTypeSwitcher.next()).movesFriction(GameRandom.globalRandom.floatGaussian() * 4.0f + this.attackerMob.dx * 0.1f, GameRandom.globalRandom.floatGaussian() * 4.0f + this.attackerMob.dy * 0.1f, 0.5f).heightMoves(12.0f + GameRandom.globalRandom.floatGaussian() * 4.0f, 2.0f + GameRandom.globalRandom.floatGaussian() * 2.0f).color(AphColors.crimson_kora);
                    ++i;
                }
                this.attackerMob.getClient().startCameraShake(this.attackerMob.x, this.attackerMob.y, 50, 40, 0.3f * shownEffect + 0.2f, 0.3f * shownEffect + 0.2f, true);
            }
        }
    }
}

