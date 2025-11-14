/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent;

import java.awt.geom.Point2D;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Objects;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.LightningEffectHandler;
import necesse.entity.levelEvent.actions.IntLevelEventAction;
import necesse.entity.levelEvent.mobAbilityLevelEvent.MobAbilityLevelEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.TrainingDummyMob;
import necesse.gfx.GameResources;

public class OverChargedManaHitsLevelEvent
extends MobAbilityLevelEvent {
    private int zapTimeBetweenTargets;
    private Mob currentTarget;
    private LightningEffectHandler handler;
    private int targetsLeft;
    private int totalTargets;
    private final HashSet<Integer> targetsHit = new HashSet();
    private int damageNumber;
    private Attacker attacker;
    private IntLevelEventAction newTargetFoundAction;

    public OverChargedManaHitsLevelEvent() {
        this.setupActions();
    }

    public OverChargedManaHitsLevelEvent(Mob owner, int zapTimeBetweenTargets, Mob initialTarget, int totalTargets, Integer damageNumber, Attacker attacker) {
        super(owner, GameRandom.globalRandom);
        Objects.requireNonNull(initialTarget);
        this.zapTimeBetweenTargets = zapTimeBetweenTargets;
        this.currentTarget = initialTarget;
        this.totalTargets = totalTargets;
        this.targetsLeft = totalTargets;
        this.targetsHit.add(initialTarget.getUniqueID());
        this.damageNumber = damageNumber;
        this.attacker = attacker;
        this.setupActions();
    }

    public void setupActions() {
        this.newTargetFoundAction = this.registerAction(new IntLevelEventAction(){

            @Override
            protected void run(int value) {
                OverChargedManaHitsLevelEvent.this.currentTarget = GameUtils.getLevelMob(value, OverChargedManaHitsLevelEvent.this.getLevel(), OverChargedManaHitsLevelEvent.this.isClient());
                OverChargedManaHitsLevelEvent.this.addNextTargetPoint();
            }
        });
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextInt(this.currentTarget.getUniqueID());
        writer.putNextInt(this.zapTimeBetweenTargets);
        writer.putNextShortUnsigned(this.targetsLeft);
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        int targetUniqueID = reader.getNextInt();
        this.currentTarget = GameUtils.getLevelMob(targetUniqueID, this.getLevel(), true);
        this.zapTimeBetweenTargets = reader.getNextInt();
        this.targetsLeft = reader.getNextShortUnsigned();
    }

    @Override
    public void init() {
        super.init();
        this.handler = new LightningEffectHandler(this.level, 4);
        if (this.owner != null) {
            this.handler.addNextPoint(this.owner.x, this.owner.y, 0, true, null);
        }
        this.addNextTargetPoint();
    }

    public void addNextTargetPoint() {
        Mob currentTarget = this.currentTarget;
        if (currentTarget != null) {
            this.handler.addNextPoint(() -> new Point2D.Float(currentTarget.x, currentTarget.y), this.zapTimeBetweenTargets, true, () -> {
                if (!this.isServer()) {
                    SoundManager.playSound(GameResources.zap2, (SoundEffect)SoundEffect.effect(currentTarget));
                    return;
                }
                int damageLossPerJump = this.damageNumber / this.totalTargets;
                GameDamage damage = new GameDamage(DamageTypeRegistry.NORMAL, (float)(damageLossPerJump * this.targetsLeft));
                currentTarget.isServerHit(damage, currentTarget.x - this.owner.x, currentTarget.y - this.owner.y, 0.0f, this.attacker);
                if (this.targetsLeft <= 0) {
                    this.over();
                    return;
                }
                Mob nextTarget = this.lookForNewTarget(currentTarget);
                if (nextTarget != null) {
                    this.targetsHit.add(nextTarget.getUniqueID());
                    --this.targetsLeft;
                    this.newTargetFoundAction.runAndSend(nextTarget.getUniqueID());
                } else {
                    this.over();
                }
            });
        }
    }

    @Override
    public void tickMovement(float delta) {
        super.tickMovement(delta);
        this.handler.tickMovement(delta);
    }

    @Override
    public void over() {
        super.over();
        if (this.handler != null) {
            this.handler.dispose();
        }
    }

    private Mob lookForNewTarget(Mob previousTarget) {
        int checkForMobsRange = 384;
        Mob newTarget = GameUtils.streamTargetsRange(this.owner, previousTarget.getX(), previousTarget.getY(), checkForMobsRange).filter(mob -> mob != previousTarget && !this.targetsHit.contains(mob.getUniqueID()) && (mob.isHostile || mob instanceof TrainingDummyMob)).min(Comparator.comparingDouble(mob -> mob.getDistance(previousTarget.x, previousTarget.y))).orElse(null);
        if (newTarget != null && newTarget.getDistance(previousTarget.x, previousTarget.y) > (float)checkForMobsRange) {
            return null;
        }
        return newTarget;
    }
}

