/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.mobAbilityLevelEvent;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.HashMap;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.SoundPlayer;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.RayLinkedList;
import necesse.entity.ParticleBeamHandler;
import necesse.entity.levelEvent.mobAbilityLevelEvent.MouseBeamLevelEvent;
import necesse.entity.mobs.AttackAnimMob;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.level.maps.LevelObjectHit;

public class ChargeBeamWarningLevelEvent
extends MouseBeamLevelEvent {
    private long startTime;
    private int fullChargeTime;
    private SoundPlayer chargeSound;

    public ChargeBeamWarningLevelEvent() {
    }

    public ChargeBeamWarningLevelEvent(Mob owner, int startTargetX, int startTargetY, int seed, float speed, float distance, float appendAttackSpeedModifier, int bounces, Color color, int fullChargeTime) {
        super(owner, startTargetX, startTargetY, seed, speed, distance, new GameDamage(0.0f), 0, new HashMap<Integer, Long>(), 1000, appendAttackSpeedModifier, bounces, 0.0f, color);
        this.fullChargeTime = fullChargeTime;
    }

    @Override
    public void init() {
        super.init();
        this.startTime = this.getTime();
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextInt(this.fullChargeTime);
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.fullChargeTime = reader.getNextInt();
    }

    @Override
    public void clientTick() {
        Point2D.Float attackDir;
        super.clientTick();
        if (this.chargeSound == null || this.chargeSound.isDone()) {
            this.chargeSound = SoundManager.playSound(GameResources.laserBeam1, (SoundEffect)SoundEffect.effect(this).volume(0.0f));
            if (this.chargeSound != null) {
                this.chargeSound.fadeIn(1.0f);
            }
        }
        if (this.chargeSound != null) {
            long timeProgress = this.getTime() - this.startTime;
            float progress = GameMath.limit((float)timeProgress / (float)this.fullChargeTime, 0.0f, 1.0f);
            float pitch = GameMath.lerp(progress, 0.5f, 1.0f);
            float volume = GameMath.lerp(progress, 0.05f, 0.1f);
            this.chargeSound.effect = SoundEffect.effect(this).pitch(pitch).volume(volume).falloffDistance(1400);
            this.chargeSound.refreshLooping(1.0f);
        }
        float ownerX = this.owner.x;
        float ownerY = this.owner.y;
        if (this.owner instanceof AttackAnimMob && (attackDir = ((AttackAnimMob)this.owner).attackDir) != null) {
            ownerX += attackDir.x * 10.0f;
            ownerY += attackDir.y * 10.0f;
        }
        for (int i = 0; i < 2; ++i) {
            int angle = GameRandom.globalRandom.nextInt(360);
            Point2D.Float dir = GameMath.getAngleDir(angle);
            float range = GameRandom.globalRandom.getFloatBetween(15.0f, 22.0f);
            float startX = ownerX + dir.x * range;
            float startY = ownerY + 4.0f;
            float endHeight = 18.0f;
            float startHeight = endHeight + dir.y * range;
            int lifeTime = GameRandom.globalRandom.getIntBetween(200, 500);
            float speed = dir.x * range * 250.0f / (float)lifeTime;
            Color color1 = new Color(199, 40, 34);
            Color color2 = new Color(222, 42, 75);
            Color color3 = new Color(220, 37, 92);
            Color color = GameRandom.globalRandom.getOneOf(color1, color2, color3);
            this.getLevel().entityManager.addParticle(startX, startY, Particle.GType.IMPORTANT_COSMETIC).sprite(GameResources.puffParticles.sprite(GameRandom.globalRandom.nextInt(5), 0, 12)).sizeFades(12, 14).rotates().heightMoves(startHeight, endHeight).movesConstant(-speed, 0.0f).color(color).fadesAlphaTime(100, 50).lifeTime(lifeTime);
        }
    }

    @Override
    public boolean canHit(LevelObjectHit hit) {
        return false;
    }

    @Override
    public boolean canHit(Mob mob) {
        return false;
    }

    @Override
    protected ParticleBeamHandler constructBeam() {
        return super.constructBeam().particleSize(10, 12).particleThicknessMod(0.2f).endParticleSize(0, 0).distPerParticle(20.0f).thickness(8, 2).speed(50.0f);
    }

    @Override
    protected void updateTrail(RayLinkedList<LevelObjectHit> rays, float delta) {
        super.updateTrail(rays, delta);
        long timeProgress = this.getTime() - this.startTime;
        float progress = GameMath.limit((float)timeProgress / (float)this.fullChargeTime, 0.0f, 1.0f);
        this.beamHandler.color(new Color(this.color.getRed(), this.color.getGreen(), this.color.getBlue(), (int)(progress * 255.0f)));
    }

    @Override
    public void onDispose() {
        super.onDispose();
        if (this.chargeSound != null) {
            this.chargeSound.stop();
        }
    }
}

