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
 *  necesse.entity.Entity
 *  necesse.entity.ParticleTypeSwitcher
 *  necesse.entity.mobs.Attacker
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.mobs.attackHandler.AttackHandler
 *  necesse.entity.mobs.attackHandler.MousePositionAttackHandler
 *  necesse.entity.mobs.buffs.ActiveBuff
 *  necesse.entity.mobs.itemAttacker.ItemAttackSlot
 *  necesse.entity.mobs.itemAttacker.ItemAttackerMob
 *  necesse.entity.particle.Particle$GType
 *  necesse.entity.projectile.Projectile
 *  necesse.gfx.GameResources
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.item.Item$Rarity
 *  necesse.inventory.item.ItemAttackerWeaponItem
 *  necesse.level.maps.Level
 */
package aphorea.items.tools.weapons.melee.saber;

import aphorea.items.tools.weapons.melee.saber.AphSaberToolItem;
import aphorea.items.tools.weapons.melee.saber.logic.SaberDashAttackHandler;
import aphorea.projectiles.toolitem.GlacialShardBigProjectile;
import aphorea.projectiles.toolitem.GlacialShardMediumProjectile;
import aphorea.ui.AphCustomUIList;
import aphorea.ui.GlacialSaberAttackUIManger;
import aphorea.utils.AphColors;
import java.awt.Point;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.sound.PrimitiveSoundEmitter;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.gameSound.GameSound;
import necesse.engine.util.GameRandom;
import necesse.entity.Entity;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.attackHandler.AttackHandler;
import necesse.entity.mobs.attackHandler.MousePositionAttackHandler;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.Projectile;
import necesse.gfx.GameResources;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemAttackerWeaponItem;
import necesse.level.maps.Level;

public class GlacialSaber
extends AphSaberToolItem {
    public GlacialSaber() {
        super(1450);
        this.rarity = Item.Rarity.UNCOMMON;
        this.attackDamage.setBaseValue(52.0f).setUpgradedValue(1.0f, 76.0f);
        this.knockback.setBaseValue(75);
        this.attackRange.setBaseValue(75);
    }

    @Override
    public Projectile getProjectile(Level level, int x, int y, int targetX, int targetY, ItemAttackerMob attackerMob, InventoryItem item, float powerPercent, int seed) {
        if (powerPercent > 0.75f) {
            powerPercent = (powerPercent - 0.375f) * 1.6f;
            return new GlacialShardBigProjectile(level, (Mob)attackerMob, x, y, targetX, targetY, 200.0f, 400, this.getAttackDamage(item), (int)((float)this.getKnockback(item, (Attacker)attackerMob) * powerPercent), seed);
        }
        powerPercent = Math.max(powerPercent, 0.1f);
        return new GlacialShardMediumProjectile(level, (Mob)attackerMob, x, y, targetX, targetY, 150.0f, 300, this.getAttackDamage(item).modDamage(0.5f), (int)((float)this.getKnockback(item, (Attacker)attackerMob) * powerPercent), seed);
    }

    @Override
    public void shotProjectile(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item, int seed) {
        float powerPercent = this.chargePercent(item);
        if (powerPercent >= 0.92f) {
            powerPercent = 1.0f;
        }
        Projectile projectile = this.getProjectile(level, attackerMob.getX(), attackerMob.getY(), x, y, attackerMob, item, powerPercent, seed);
        projectile.resetUniqueID(new GameRandom((long)seed));
        attackerMob.addAndSendAttackerProjectile(projectile);
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        if (!attackerMob.isPlayer && this.canDash(attackerMob)) {
            int animTime = (int)((float)this.getChargeAnimTime(item, attackerMob));
            mapContent.setBoolean("charging", true);
            attackerMob.startAttackHandler((AttackHandler)new SaberDashAttackHandler(attackerMob, slot, item, this, animTime, AphColors.lighter_gray, seed));
        } else {
            int animTime = (int)((float)this.getChargeAnimTime(item, attackerMob));
            item.getGndData().setBoolean("charging", false);
            attackerMob.startAttackHandler((AttackHandler)new GlacialSaberAttackHandler(attackerMob, slot, item, this, animTime, this.isAuto, seed));
        }
        return item;
    }

    @Override
    public float getDashDamageMultiplier(InventoryItem item) {
        return 2.0f;
    }

    @Override
    public float chargePercent(InventoryItem item) {
        return 1.0f - Math.abs(GlacialSaberAttackUIManger.barPercent(item.getGndData().getFloat("realChargePercent", 0.0f)));
    }

    public static float calcSownChargePercent(float chargePercent) {
        return Math.min(chargePercent / 0.25f, 1.0f);
    }

    @Override
    public void showAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, int animAttack, int seed, GNDItemMap mapContent) {
        if (item.getGndData().getBoolean("charged")) {
            super.superShowAttack(level, x, y, attackerMob, attackHeight, item, animAttack, seed, mapContent);
        }
        if (level.isClient() && level.getClient().getPlayer().getUniqueID() == attackerMob.getUniqueID()) {
            AphCustomUIList.glacialSaberAttack.chargePercent = item.getGndData().getFloat("realChargePercent");
        }
    }

    public static class GlacialSaberAttackHandler
    extends MousePositionAttackHandler {
        public int chargeTime;
        public boolean fullyCharged;
        public AphSaberToolItem toolItem;
        public long startTime;
        public InventoryItem item;
        public int seed;
        public boolean endedByInteract;
        public boolean isAuto;

        public GlacialSaberAttackHandler(ItemAttackerMob attackerMob, ItemAttackSlot slot, InventoryItem item, AphSaberToolItem toolItem, int chargeTime, boolean isAuto, int seed) {
            super(attackerMob, slot, 20);
            this.item = item;
            this.toolItem = toolItem;
            this.chargeTime = chargeTime;
            this.seed = seed;
            this.startTime = attackerMob.getWorldEntity().getLocalTime();
            this.isAuto = isAuto;
        }

        public long getTimeSinceStart() {
            return this.attackerMob.getWorldEntity().getLocalTime() - this.startTime;
        }

        public float getChargePercent() {
            return (float)this.getTimeSinceStart() / (float)this.chargeTime;
        }

        public Point getNextItemAttackerLevelPos(Mob currentTarget) {
            InventoryItem attackItem = this.item.copy();
            attackItem.getGndData().setFloat("skillPercent", 1.0f);
            return ((ItemAttackerWeaponItem)attackItem.item).getItemAttackerAttackPosition(this.attackerMob.getLevel(), this.attackerMob, currentTarget, -1, attackItem);
        }

        public void onUpdate() {
            super.onUpdate();
            float chargePercent = this.getChargePercent();
            InventoryItem showItem = this.item.copy();
            showItem.getGndData().setBoolean("charging", true);
            showItem.getGndData().setFloat("chargePercent", GlacialSaber.calcSownChargePercent(chargePercent));
            showItem.getGndData().setFloat("realChargePercent", chargePercent);
            if (this.attackerMob.isClient() && this.attackerMob.isPlayer && AphCustomUIList.glacialSaberAttack.form.isHidden()) {
                AphCustomUIList.glacialSaberAttack.form.setHidden(false);
                AphCustomUIList.glacialSaberAttack.chargeTime = this.chargeTime;
            }
            this.attackerMob.showAttackAndSendAttacker(showItem, this.lastX, this.lastY, 0, this.seed);
            if (chargePercent >= 1.0f && (!this.attackerMob.isPlayer || this.isAuto)) {
                this.attackerMob.endAttackHandler(true);
                return;
            }
            if (chargePercent >= 0.25f && !this.fullyCharged) {
                this.fullyCharged = true;
                int particles = 35;
                float anglePerParticle = 360.0f / (float)particles;
                ParticleTypeSwitcher typeSwitcher = new ParticleTypeSwitcher(new Particle.GType[]{Particle.GType.CRITICAL, Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC});
                for (int i = 0; i < particles; ++i) {
                    int angle = (int)((float)i * anglePerParticle + GameRandom.globalRandom.nextFloat() * anglePerParticle);
                    float dx = (float)Math.sin(Math.toRadians(angle)) * (float)GameRandom.globalRandom.getIntBetween(30, 50);
                    float dy = (float)Math.cos(Math.toRadians(angle)) * (float)GameRandom.globalRandom.getIntBetween(30, 50) * 0.8f;
                    this.attackerMob.getLevel().entityManager.addParticle((Entity)this.attackerMob, typeSwitcher.next()).movesFriction(dx, dy, 0.8f).color(AphColors.ice).heightMoves(0.0f, 30.0f).lifeTime(500);
                }
                SoundManager.playSound((GameSound)GameResources.cling, (SoundEffect)SoundEffect.effect((PrimitiveSoundEmitter)this.attackerMob).volume(0.2f).pitch(1.0f));
            }
        }

        public void onMouseInteracted(int levelX, int levelY) {
            this.endedByInteract = true;
            this.attackerMob.endAttackHandler(false);
        }

        public void onControllerInteracted(float aimX, float aimY) {
            this.endedByInteract = true;
            this.attackerMob.endAttackHandler(false);
        }

        public void onEndAttack(boolean bySelf) {
            if (AphCustomUIList.glacialSaberAttack.form != null) {
                AphCustomUIList.glacialSaberAttack.form.setHidden(true);
            }
            float chargePercent = this.getChargePercent();
            if (!this.endedByInteract && chargePercent >= 0.25f) {
                if (this.attackerMob.isPlayer) {
                    ((PlayerMob)this.attackerMob).constantAttack = true;
                }
                InventoryItem attackItem = this.item.copy();
                attackItem.getGndData().setFloat("chargePercent", GlacialSaber.calcSownChargePercent(chargePercent));
                attackItem.getGndData().setFloat("realChargePercent", chargePercent);
                attackItem.getGndData().setBoolean("charged", true);
                if (!this.attackerMob.isPlayer && this.lastItemAttackerTarget != null) {
                    Point attackPos = ((ItemAttackerWeaponItem)attackItem.item).getItemAttackerAttackPosition(this.attackerMob.getLevel(), this.attackerMob, this.lastItemAttackerTarget, -1, attackItem);
                    this.lastX = attackPos.x;
                    this.lastY = attackPos.y;
                }
                GNDItemMap attackMap = this.attackerMob.showAttackAndSendAttacker(attackItem, this.lastX, this.lastY, 0, this.seed);
                this.toolItem.superOnAttack(this.attackerMob.getLevel(), this.lastX, this.lastY, this.attackerMob, this.attackerMob.getCurrentAttackHeight(), attackItem, this.slot, 0, this.seed, attackMap);
                for (ActiveBuff b : this.attackerMob.buffManager.getArrayBuffs()) {
                    b.onItemAttacked(this.lastX, this.lastY, this.attackerMob, this.attackerMob.getCurrentAttackHeight(), attackItem, this.slot, 0, new GNDItemMap());
                }
            } else {
                this.attackerMob.doAndSendStopAttackAttacker(false);
            }
        }
    }
}

