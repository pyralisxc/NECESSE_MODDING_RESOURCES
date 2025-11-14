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
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.mobs.attackHandler.MousePositionAttackHandler
 *  necesse.entity.mobs.buffs.ActiveBuff
 *  necesse.entity.mobs.itemAttacker.ItemAttackSlot
 *  necesse.entity.mobs.itemAttacker.ItemAttackerMob
 *  necesse.entity.particle.Particle$GType
 *  necesse.gfx.GameResources
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.item.ItemAttackerWeaponItem
 */
package aphorea.items.tools.weapons.range.sabergun.logic;

import aphorea.items.tools.weapons.range.sabergun.AphSaberGunToolItem;
import aphorea.ui.AphCustomUIList;
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
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.attackHandler.MousePositionAttackHandler;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.ItemAttackerWeaponItem;

public class SaberGunAttackHandler
extends MousePositionAttackHandler {
    public int chargeTime;
    public boolean fullyCharged;
    public AphSaberGunToolItem toolItem;
    public long startTime;
    public InventoryItem item;
    public int seed;
    public boolean endedByInteract;

    public SaberGunAttackHandler(ItemAttackerMob attackerMob, ItemAttackSlot slot, InventoryItem item, AphSaberGunToolItem toolItem, int chargeTime, int seed) {
        super(attackerMob, slot, 20);
        this.item = item;
        this.toolItem = toolItem;
        this.chargeTime = chargeTime;
        this.seed = seed;
        this.startTime = attackerMob.getWorldEntity().getLocalTime();
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
        showItem.getGndData().setFloat("chargePercent", chargePercent);
        if (this.attackerMob.isClient() && this.attackerMob.isPlayer && AphCustomUIList.gunAttack.form.isHidden()) {
            AphCustomUIList.gunAttack.form.setHidden(false);
            AphCustomUIList.gunAttack.chargeTime = this.chargeTime;
        }
        this.attackerMob.showAttackAndSendAttacker(showItem, this.lastX, this.lastY, 0, this.seed);
        if (chargePercent >= 1.5f && !this.attackerMob.isPlayer) {
            this.attackerMob.endAttackHandler(true);
            return;
        }
        if (chargePercent >= 0.75f && !this.fullyCharged) {
            this.fullyCharged = true;
            if (this.attackerMob.isClient()) {
                int particles = 35;
                float anglePerParticle = 360.0f / (float)particles;
                ParticleTypeSwitcher typeSwitcher = new ParticleTypeSwitcher(new Particle.GType[]{Particle.GType.CRITICAL, Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC});
                for (int i = 0; i < particles; ++i) {
                    int angle = (int)((float)i * anglePerParticle + GameRandom.globalRandom.nextFloat() * anglePerParticle);
                    float dx = (float)Math.sin(Math.toRadians(angle)) * (float)GameRandom.globalRandom.getIntBetween(30, 50);
                    float dy = (float)Math.cos(Math.toRadians(angle)) * (float)GameRandom.globalRandom.getIntBetween(30, 50) * 0.8f;
                    this.attackerMob.getLevel().entityManager.addParticle((Entity)this.attackerMob, typeSwitcher.next()).movesFriction(dx, dy, 0.8f).color(AphColors.iron).heightMoves(0.0f, 30.0f).lifeTime(500);
                }
                SoundManager.playSound((GameSound)GameResources.cling, (SoundEffect)SoundEffect.effect((PrimitiveSoundEmitter)this.attackerMob).volume(0.5f).pitch(1.0f));
            }
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
        if (AphCustomUIList.gunAttack.form != null) {
            AphCustomUIList.gunAttack.form.setHidden(true);
        }
        float chargePercent = this.getChargePercent();
        if (!this.endedByInteract && chargePercent >= 0.75f) {
            if (this.attackerMob.isPlayer) {
                ((PlayerMob)this.attackerMob).constantAttack = true;
            }
            InventoryItem attackItem = this.item.copy();
            attackItem.getGndData().setFloat("chargePercent", chargePercent);
            attackItem.getGndData().setBoolean("charged", true);
            if (!this.attackerMob.isPlayer && this.lastItemAttackerTarget != null) {
                Point attackPos = ((ItemAttackerWeaponItem)attackItem.item).getItemAttackerAttackPosition(this.attackerMob.getLevel(), this.attackerMob, this.lastItemAttackerTarget, -1, attackItem);
                this.lastX = attackPos.x;
                this.lastY = attackPos.y;
            }
            if (this.attackerMob.isClient()) {
                SoundManager.playSound((GameSound)GameResources.shotgun, (SoundEffect)SoundEffect.effect((PrimitiveSoundEmitter)this.attackerMob));
            }
            this.attackerMob.showAttackAndSendAttacker(attackItem, this.lastX, this.lastY, 0, this.seed);
            this.toolItem.doAttack(this.attackerMob.getLevel(), this.lastX, this.lastY, this.attackerMob, attackItem, this.seed);
            for (ActiveBuff b : this.attackerMob.buffManager.getArrayBuffs()) {
                b.onItemAttacked(this.lastX, this.lastY, this.attackerMob, this.attackerMob.getCurrentAttackHeight(), attackItem, this.slot, 0, new GNDItemMap());
            }
        }
        this.attackerMob.doAndSendStopAttackAttacker(false);
    }
}

