/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.attackHandler;

import java.awt.Color;
import java.awt.geom.Point2D;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.mobs.attackHandler.MouseAngleAttackHandler;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem.SapphireRevolverProjectileToolItem;

public class SapphireRevolverAttackHandler
extends MouseAngleAttackHandler {
    private final int chargeDelay = 1000;
    private final long startTime;
    public SapphireRevolverProjectileToolItem toolItem;
    public InventoryItem item;
    private final int seed;
    private boolean charged;
    protected int endAttackBuffer;

    public SapphireRevolverAttackHandler(ItemAttackerMob attackerMob, ItemAttackSlot slot, InventoryItem item, SapphireRevolverProjectileToolItem toolItem, int seed, int startTargetX, int startTargetY) {
        super(attackerMob, slot, 50, 1000.0f, startTargetX, startTargetY);
        this.item = item;
        this.toolItem = toolItem;
        this.seed = seed;
        this.startTime = attackerMob.getWorldEntity().getLocalTime();
    }

    public long getTimeSinceStart() {
        return this.attackerMob.getWorldEntity().getLocalTime() - this.startTime;
    }

    public float getChargePercent() {
        return Math.min((float)this.getTimeSinceStart() / this.getChargeTime(), 1.0f);
    }

    public float getChargeTime() {
        float multiplier = 0.5f / this.toolItem.getAttackSpeedModifier(this.item, this.attackerMob);
        return (int)(multiplier * 1000.0f);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        float chargePercent = this.getChargePercent();
        if (!this.attackerMob.isPlayer && chargePercent >= 1.0f) {
            this.endAttackBuffer += this.updateInterval;
            if (this.endAttackBuffer >= 350) {
                this.endAttackBuffer = 0;
                this.attackerMob.endAttackHandler(true);
                return;
            }
        }
        Point2D.Float dir = GameMath.getAngleDir(this.currentAngle);
        int attackX = this.attackerMob.getX() + (int)(dir.x * 100.0f);
        int attackY = this.attackerMob.getY() + (int)(dir.y * 100.0f);
        if (this.toolItem.canAttack(this.attackerMob.getLevel(), attackX, attackY, this.attackerMob, this.item) == null) {
            this.attackerMob.showAttackAndSendAttacker(this.item, attackX, attackY, 0, this.seed);
            if (this.attackerMob.isClient() && chargePercent >= 1.0f && !this.charged) {
                this.charged = true;
                SoundManager.playSound(GameResources.cling, (SoundEffect)SoundEffect.effect(this.attackerMob).pitch(2.0f));
                SoundManager.playSound(GameResources.jingle, (SoundEffect)SoundEffect.effect(this.attackerMob).volume(0.5f).pitch(1.0f));
                ParticleTypeSwitcher typeSwitcher = new ParticleTypeSwitcher(Particle.GType.CRITICAL, Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC);
                float anglePerParticle = 18.0f;
                for (int i = 0; i < 20; ++i) {
                    int angle = (int)((float)i * anglePerParticle + GameRandom.globalRandom.nextFloat() * anglePerParticle);
                    float dx = (float)Math.sin(Math.toRadians(angle)) * 50.0f;
                    float dy = (float)Math.cos(Math.toRadians(angle)) * 50.0f * 0.8f;
                    this.attackerMob.getLevel().entityManager.addParticle(this.attackerMob, typeSwitcher.next()).movesFriction(dx, dy, 0.8f).color(new Color(116, 245, 253)).heightMoves(0.0f, 10.0f).sizeFades(22, 44).sprite(GameResources.magicSparkParticles.sprite(GameRandom.globalRandom.nextInt(4), 0, 22)).lifeTime(500);
                }
            }
        }
    }

    @Override
    public void onEndAttack(boolean bySelf) {
        if (this.getChargePercent() >= 1.0f) {
            Point2D.Float dir = GameMath.getAngleDir(this.currentAngle);
            int attackX = this.attackerMob.getX() + (int)(dir.x * 100.0f);
            int attackY = this.attackerMob.getY() + (int)(dir.y * 100.0f);
            InventoryItem attackItem = this.item.copy();
            attackItem.getGndData().setBoolean("charged", true);
            GNDItemMap attackMap = this.attackerMob.showAttackAndSendAttacker(attackItem, attackX, attackY, 0, this.seed);
            this.toolItem.superOnAttack(this.attackerMob.getLevel(), attackX, attackY, this.attackerMob, this.attackerMob.getCurrentAttackHeight(), attackItem, this.slot, 0, this.seed, attackMap);
            for (ActiveBuff b : this.attackerMob.buffManager.getArrayBuffs()) {
                b.onItemAttacked(attackX, attackY, this.attackerMob, this.attackerMob.getCurrentAttackHeight(), attackItem, this.slot, 0, attackMap);
            }
        }
        this.attackerMob.doAndSendStopAttackAttacker(false);
    }
}

