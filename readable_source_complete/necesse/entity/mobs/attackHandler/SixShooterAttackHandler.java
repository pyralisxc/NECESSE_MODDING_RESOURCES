/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.attackHandler;

import java.awt.geom.Point2D;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.packet.PacketFireSixShooter;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.leaves.ChaserAINode;
import necesse.entity.mobs.attackHandler.MouseAngleAttackHandler;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem.SixShooterProjectileToolItem;

public class SixShooterAttackHandler
extends MouseAngleAttackHandler {
    private final InventoryItem item;
    private final SixShooterProjectileToolItem toolItem;
    private final int attackSeed;
    private int shotsRemaining = 6;
    private int shots;
    private long timeBuffer;
    private final GameRandom random = new GameRandom();
    private final int timeBetweenReloads = 1500;
    private final int timeBetweenBurstShots = 200;

    public SixShooterAttackHandler(ItemAttackerMob itemAttacker, ItemAttackSlot slot, InventoryItem item, SixShooterProjectileToolItem toolItem, int seed, int startTargetX, int startTargetY) {
        super(itemAttacker, slot, 50, 1000.0f, startTargetX, startTargetY);
        this.attackSeed = seed;
        this.timeBuffer = 1500L;
        this.item = item;
        this.toolItem = toolItem;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (!this.attackerMob.isPlayer && this.lastItemAttackerTarget != null && !ChaserAINode.hasLineOfSightToTarget(this.attackerMob, this.lastItemAttackerTarget, 5.0f)) {
            this.attackerMob.endAttackHandler(true);
            return;
        }
        Point2D.Float dir = GameMath.getAngleDir(this.currentAngle);
        int attackX = this.attackerMob.getX() + (int)(dir.x * 100.0f);
        int attackY = this.attackerMob.getY() + (int)(dir.y * 100.0f);
        if (this.toolItem.canAttack(this.attackerMob.getLevel(), attackX, attackY, this.attackerMob, this.item) == null) {
            float speedModifier;
            int seed = Item.getRandomAttackSeed(this.random.seeded(GameRandom.prime(this.attackSeed * this.shots)));
            GNDItemMap attackMap = this.attackerMob.showAttackAndSendAttacker(this.item, attackX, attackY, seed, 0);
            this.timeBuffer += (long)this.updateInterval;
            while (!((float)this.timeBuffer < 1500.0f * (speedModifier = this.getSpeedModifier()))) {
                seed = Item.getRandomAttackSeed(this.random.nextSeeded(GameRandom.prime(this.attackSeed * this.shots)));
                ++this.shots;
                --this.shotsRemaining;
                this.toolItem.superOnAttack(this.attackerMob.getLevel(), attackX, attackY, this.attackerMob, this.attackerMob.getCurrentAttackHeight(), this.item, this.slot, 0, seed, attackMap);
                if (this.attackerMob.isClient()) {
                    SixShooterAttackHandler.playFireSound(this.attackerMob);
                } else if (this.attackerMob.isServer()) {
                    this.attackerMob.sendAttackerPacket(this.attackerMob, new PacketFireSixShooter(this.attackerMob));
                }
                if (this.shotsRemaining <= 0) {
                    if (this.attackerMob.isClient()) {
                        this.playReloadEffects(this.attackerMob, 6);
                    }
                    this.shotsRemaining = 6;
                    this.timeBuffer = 0L;
                    break;
                }
                this.timeBuffer = (int)(1300.0f * speedModifier);
            }
        }
    }

    public static void playFireSound(Mob target) {
        SoundManager.playSound(GameResources.explosionHeavy, (SoundEffect)SoundEffect.effect(target).pitch(2.5f).volume(0.5f));
        SoundManager.playSound(GameResources.crystalHit3, (SoundEffect)SoundEffect.effect(target).pitch(2.0f).volume(0.5f));
    }

    private void playReloadEffects(Mob target, int particleCount) {
        if (particleCount == 1) {
            SoundManager.playSound(GameResources.crystalHit2, (SoundEffect)SoundEffect.effect(target).pitch(1.5f).volume(0.5f));
        } else {
            SoundManager.playSound(GameResources.coins, (SoundEffect)SoundEffect.effect(target).pitch(0.75f).volume(0.5f));
        }
        for (int i = 0; i < particleCount; ++i) {
            float xMove = GameRandom.globalRandom.getFloatBetween(-0.05f, 0.05f);
            float yStart = this.attackerMob.y + GameRandom.globalRandom.getFloatBetween(-7.0f, 7.0f);
            this.attackerMob.getLevel().entityManager.addParticle(this.attackerMob.x + GameRandom.globalRandom.getFloatBetween(-5.0f, 5.0f), this.attackerMob.y, Particle.GType.COSMETIC).sprite(GameResources.bulletCasingParticles.sprite(0, 0, 6, 8)).moves((pos, delta, lifeTime, timeAlive, lifePercent) -> {
                pos.x += xMove;
                pos.y = (float)((double)yStart - 5.0 * Math.abs((double)1.3f * Math.cos(10.0f * lifePercent)));
            }).rotates(360.0f, 720.0f).size((options, lifeTime, timeAlive, lifePercent) -> options.size(6, 8)).fadesAlpha(0.0f, 0.2f).lifeTimeBetween(1000, 2000);
        }
    }

    private float getSpeedModifier() {
        return 1.0f / this.toolItem.getAttackSpeedModifier(this.item, this.attackerMob);
    }

    private int getCanceledReloadTime() {
        if (this.shots == 0) {
            return 0;
        }
        if (this.shots % 6 != 0) {
            return 250 * (6 - this.shotsRemaining);
        }
        return 1500;
    }

    @Override
    public void onEndAttack(boolean bySelf) {
        if (this.shotsRemaining < 6) {
            this.playReloadEffects(this.attackerMob, 6 - this.shotsRemaining);
        }
        if (this.attackerMob.isPlayer) {
            ((PlayerMob)this.attackerMob).startItemCooldown(this.toolItem, (int)((float)this.getCanceledReloadTime() * this.getSpeedModifier()));
        }
        this.attackerMob.doAndSendStopAttackAttacker(false);
    }
}

