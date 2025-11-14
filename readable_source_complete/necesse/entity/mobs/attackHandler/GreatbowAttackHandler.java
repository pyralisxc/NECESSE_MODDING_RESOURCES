/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.attackHandler;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Point2D;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.SoundPlayer;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
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
import necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.greatbowProjectileToolItem.GreatbowProjectileToolItem;

public class GreatbowAttackHandler
extends MousePositionAttackHandler {
    protected SoundPlayer drawBowSoundPlayer;
    public int chargeTime;
    public boolean fullyCharged;
    public GreatbowProjectileToolItem toolItem;
    public long startTime;
    public InventoryItem item;
    public int seed;
    public Color particleColors;
    public boolean endedByInteract;
    protected int endAttackBuffer;

    public GreatbowAttackHandler(ItemAttackerMob attackerMob, ItemAttackSlot slot, InventoryItem item, GreatbowProjectileToolItem toolItem, int chargeTime, Color particleColors, int seed) {
        super(attackerMob, slot, 20);
        this.item = item;
        this.toolItem = toolItem;
        this.chargeTime = chargeTime;
        this.particleColors = particleColors;
        this.seed = seed;
        this.startTime = attackerMob.getWorldEntity().getLocalTime();
        if (!attackerMob.isClient()) {
            return;
        }
        this.drawBowSoundPlayer = SoundManager.playSound(GameResources.greatbowCharge, (SoundEffect)SoundEffect.effect(attackerMob).volume(0.2f).pitch(GameRandom.globalRandom.getFloatBetween(0.75f, 0.78f)));
    }

    public long getTimeSinceStart() {
        return this.attackerMob.getWorldEntity().getLocalTime() - this.startTime;
    }

    public float getChargePercent() {
        return (float)this.getTimeSinceStart() / (float)this.chargeTime;
    }

    @Override
    public Point getNextItemAttackerLevelPos(Mob currentTarget) {
        InventoryItem attackItem = this.item.copy();
        attackItem.getGndData().setFloat("skillPercent", 1.0f);
        return ((ItemAttackerWeaponItem)((Object)attackItem.item)).getItemAttackerAttackPosition(this.attackerMob.getLevel(), this.attackerMob, currentTarget, -1, attackItem);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        Point2D.Float dir = GameMath.normalize((float)this.lastX - this.attackerMob.x, (float)this.lastY - this.attackerMob.y);
        float chargePercent = this.getChargePercent();
        InventoryItem showItem = this.item.copy();
        showItem.getGndData().setBoolean("charging", true);
        showItem.getGndData().setFloat("chargePercent", chargePercent);
        this.attackerMob.showAttackAndSendAttacker(showItem, this.lastX, this.lastY, 0, this.seed);
        if (chargePercent >= 1.0f) {
            if (!this.attackerMob.isPlayer) {
                this.endAttackBuffer += this.updateInterval;
                if (this.endAttackBuffer >= 350) {
                    this.endAttackBuffer = 0;
                    this.attackerMob.endAttackHandler(true);
                    return;
                }
            }
            if (this.attackerMob.isClient()) {
                this.attackerMob.getLevel().entityManager.addParticle(this.attackerMob.x + dir.x * 16.0f + (float)(GameRandom.globalRandom.nextGaussian() * 6.0), this.attackerMob.y + 4.0f + (float)(GameRandom.globalRandom.nextGaussian() * 8.0), Particle.GType.IMPORTANT_COSMETIC).movesConstant(this.attackerMob.dx / 10.0f, this.attackerMob.dy / 10.0f).color(this.particleColors).height(20.0f - dir.y * 16.0f);
            }
            if (!this.fullyCharged) {
                this.fullyCharged = true;
                if (this.attackerMob.isClient()) {
                    int particles = 35;
                    float anglePerParticle = 360.0f / (float)particles;
                    ParticleTypeSwitcher typeSwitcher = new ParticleTypeSwitcher(Particle.GType.CRITICAL, Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC);
                    for (int i = 0; i < particles; ++i) {
                        int angle = (int)((float)i * anglePerParticle + GameRandom.globalRandom.nextFloat() * anglePerParticle);
                        float dx = (float)Math.sin(Math.toRadians(angle)) * (float)GameRandom.globalRandom.getIntBetween(30, 50);
                        float dy = (float)Math.cos(Math.toRadians(angle)) * (float)GameRandom.globalRandom.getIntBetween(30, 50) * 0.8f;
                        this.attackerMob.getLevel().entityManager.addParticle(this.attackerMob, typeSwitcher.next()).movesFriction(dx, dy, 0.8f).color(this.particleColors).heightMoves(0.0f, 30.0f).lifeTime(500);
                    }
                    if (this.drawBowSoundPlayer != null) {
                        this.drawBowSoundPlayer.stop();
                    }
                    SoundManager.playSound(GameResources.greatbowChargeComplete, (SoundEffect)SoundEffect.effect(this.attackerMob).volume(0.2f).pitch(1.1f));
                }
            }
        }
    }

    @Override
    public void onMouseInteracted(int levelX, int levelY) {
        this.endedByInteract = true;
        this.attackerMob.endAttackHandler(false);
    }

    @Override
    public void onControllerInteracted(float aimX, float aimY) {
        this.endedByInteract = true;
        this.attackerMob.endAttackHandler(false);
    }

    @Override
    public void onEndAttack(boolean bySelf) {
        float chargePercent = this.getChargePercent();
        if (this.drawBowSoundPlayer != null) {
            this.drawBowSoundPlayer.stop();
        }
        if (!this.endedByInteract && (this.getTimeSinceStart() >= 200L || chargePercent >= 0.5f)) {
            if (this.attackerMob.isPlayer) {
                ((PlayerMob)this.attackerMob).constantAttack = true;
            }
            InventoryItem attackItem = this.item.copy();
            attackItem.getGndData().setFloat("chargePercent", chargePercent);
            attackItem.getGndData().setBoolean("charged", true);
            if (!this.attackerMob.isPlayer && this.lastItemAttackerTarget != null) {
                Point attackPos = ((ItemAttackerWeaponItem)((Object)attackItem.item)).getItemAttackerAttackPosition(this.attackerMob.getLevel(), this.attackerMob, this.lastItemAttackerTarget, -1, attackItem);
                this.lastX = attackPos.x;
                this.lastY = attackPos.y;
            }
            GNDItemMap attackMap = this.attackerMob.showAttackAndSendAttacker(attackItem, this.lastX, this.lastY, 0, this.seed);
            this.toolItem.superOnAttack(this.attackerMob.getLevel(), this.lastX, this.lastY, this.attackerMob, this.attackerMob.getCurrentAttackHeight(), attackItem, this.slot, 0, this.seed, attackMap);
            for (ActiveBuff b : this.attackerMob.buffManager.getArrayBuffs()) {
                b.onItemAttacked(this.lastX, this.lastY, this.attackerMob, this.attackerMob.getCurrentAttackHeight(), attackItem, this.slot, 0, attackMap);
            }
        }
        this.attackerMob.doAndSendStopAttackAttacker(false);
    }
}

