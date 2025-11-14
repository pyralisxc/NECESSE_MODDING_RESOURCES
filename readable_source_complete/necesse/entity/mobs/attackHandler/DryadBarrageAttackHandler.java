/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.attackHandler;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Point2D;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.SoundPlayer;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.attackHandler.MouseAngleAttackHandler;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.ItemAttackerWeaponItem;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.DryadBarrageToolItem;

public class DryadBarrageAttackHandler
extends MouseAngleAttackHandler {
    private final ParticleTypeSwitcher typeSwitcher = new ParticleTypeSwitcher(Particle.GType.CRITICAL, Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC);
    public DryadBarrageToolItem toolItem;
    public InventoryItem item;
    public int seed;
    public long startTime;
    private final int maxProjectileCount;
    private final float minChargeTime = 0.5f;
    private final float maxChargeTime = 2.0f;
    protected int endAttackBuffer;
    protected boolean hasPlayedSoundEffect;
    protected SoundPlayer chargeSoundPlayer;

    public DryadBarrageAttackHandler(ItemAttackerMob attackerMob, ItemAttackSlot slot, InventoryItem item, DryadBarrageToolItem toolItem, int maxProjectileCount, int seed, int startX, int startY) {
        super(attackerMob, slot, 50, 1000.0f, startX, startY);
        this.toolItem = toolItem;
        this.item = item;
        this.seed = seed;
        this.maxProjectileCount = maxProjectileCount;
        this.startTime = attackerMob.getLocalTime();
        this.hasPlayedSoundEffect = false;
        if (attackerMob.isClient()) {
            this.chargeSoundPlayer = SoundManager.playSound(new SoundSettings(GameResources.magicbolt1).volume(0.3f).basePitch(0.9f).pitchVariance(0.03f), attackerMob);
            this.chargeSoundPlayer.refreshLooping();
        }
    }

    public long getTimeSinceStart() {
        return this.attackerMob.getLocalTime() - this.startTime;
    }

    public float getChargePercent() {
        int chargeTime = Math.round(2.0f / this.toolItem.getAttackSpeedModifier(this.item, this.attackerMob) * 1000.0f);
        return (float)Math.min(this.getTimeSinceStart(), (long)chargeTime) / (float)chargeTime;
    }

    @Override
    public void onUpdate() {
        block8: {
            float anglePerParticle;
            GameRandom random;
            int particleCount;
            block9: {
                super.onUpdate();
                Point2D.Float dir = GameMath.getAngleDir(this.currentAngle);
                int attackX = this.attackerMob.getX() + (int)(dir.x * 100.0f);
                int attackY = this.attackerMob.getY() + (int)(dir.y * 100.0f);
                float chargePercent = this.getChargePercent();
                if (chargePercent < 1.0f) {
                    this.toolItem.consumeMana(this.toolItem.getManaCost(this.item) / (1000.0f / (float)this.updateInterval), this.attackerMob);
                    if (this.attackerMob.buffManager.hasBuff(BuffRegistry.Debuffs.MANA_EXHAUSTION)) {
                        this.attackerMob.endAttackHandler(true);
                        return;
                    }
                }
                if (!this.attackerMob.isPlayer && chargePercent >= 1.0f) {
                    this.endAttackBuffer += this.updateInterval;
                    if (this.endAttackBuffer >= 250) {
                        this.endAttackBuffer = 0;
                        this.attackerMob.endAttackHandler(true);
                        return;
                    }
                }
                InventoryItem showItem = this.item.copy();
                this.attackerMob.showAttackAndSendAttacker(showItem, attackX, attackY, 0, this.seed);
                if (!this.attackerMob.isClient()) break block8;
                if (this.chargeSoundPlayer != null) {
                    this.chargeSoundPlayer.refreshLooping();
                }
                if (!this.hasPlayedSoundEffect && chargePercent >= 1.0f) {
                    SoundManager.playSound(GameResources.magicbolt4, (SoundEffect)SoundEffect.effect(this.attackerMob).pitch(1.4f).volume(0.4f));
                    this.hasPlayedSoundEffect = true;
                }
                particleCount = 4;
                random = GameRandom.globalRandom;
                anglePerParticle = 360.0f / (float)particleCount;
                if (!(chargePercent >= 1.0f)) break block9;
                for (int i = 0; i < particleCount; ++i) {
                    int angle = (int)((float)i * anglePerParticle + random.nextFloat() * anglePerParticle);
                    float dx = (float)Math.sin(Math.toRadians(angle)) * 50.0f;
                    float dy = (float)Math.cos(Math.toRadians(angle)) * 50.0f;
                    this.attackerMob.getLevel().entityManager.addParticle(this.attackerMob, 0.0f, 10.0f, this.typeSwitcher.next()).sprite(GameResources.magicSparkParticles.sprite(random.nextInt(4), 0, 22)).sizeFades(22, 44).movesFriction(dx, dy, 0.8f).color(new Color(16, 146, 187)).heightMoves(0.0f, 30.0f).lifeTime(200);
                }
                break block8;
            }
            if (!((float)this.getTimeSinceStart() >= 500.0f / this.toolItem.getAttackSpeedModifier(this.item, this.attackerMob))) break block8;
            for (int i = 0; i < particleCount; ++i) {
                int angle = (int)((float)i * anglePerParticle + random.nextFloat() * anglePerParticle);
                float dx = (float)Math.sin(Math.toRadians(angle)) * 10.0f;
                float dy = (float)Math.cos(Math.toRadians(angle)) * 10.0f;
                this.attackerMob.getLevel().entityManager.addParticle(this.attackerMob, dx, dy, this.typeSwitcher.next()).sprite(GameResources.magicSparkParticles.sprite(random.nextInt(4), 0, 22)).sizeFades(22, 44).movesFriction(dx, dy, 0.8f).color(new Color(30, 177, 143)).heightMoves(0.0f, 30.0f).lifeTime(200);
            }
        }
    }

    private int getProjectileCount() {
        return (int)((float)this.maxProjectileCount * this.getChargePercent());
    }

    @Override
    public void onEndAttack(boolean bySelf) {
        if ((float)this.getTimeSinceStart() >= 0.5f / this.toolItem.getAttackSpeedModifier(this.item, this.attackerMob) * 1000.0f) {
            if (this.attackerMob.isPlayer) {
                ((PlayerMob)this.attackerMob).constantAttack = true;
            }
            Point2D.Float dir = GameMath.getAngleDir(this.currentAngle);
            int attackX = this.attackerMob.getX() + (int)(dir.x * 100.0f);
            int attackY = this.attackerMob.getY() + (int)(dir.y * 100.0f);
            InventoryItem attackItem = this.item.copy();
            if (!this.attackerMob.isPlayer && this.lastItemAttackerTarget != null) {
                Point attackPos = ((ItemAttackerWeaponItem)((Object)attackItem.item)).getItemAttackerAttackPosition(this.attackerMob.getLevel(), this.attackerMob, this.lastItemAttackerTarget, -1, attackItem);
                attackX = attackPos.x;
                attackY = attackPos.y;
            }
            GNDItemMap attackMap = this.attackerMob.showAttackAndSendAttacker(attackItem, attackX, attackY, 0, this.seed);
            this.toolItem.triggerBarrageAttack(this.attackerMob.getLevel(), attackX, attackY, this.attackerMob, attackItem, this.seed, this.getProjectileCount());
            for (ActiveBuff b : this.attackerMob.buffManager.getArrayBuffs()) {
                b.onItemAttacked(attackX, attackY, this.attackerMob, this.attackerMob.getCurrentAttackHeight(), attackItem, this.slot, 0, attackMap);
            }
        } else {
            this.attackerMob.doAndSendStopAttackAttacker(false);
        }
        if (this.attackerMob.isClient() && this.chargeSoundPlayer != null) {
            this.chargeSoundPlayer.stop();
        }
    }
}

