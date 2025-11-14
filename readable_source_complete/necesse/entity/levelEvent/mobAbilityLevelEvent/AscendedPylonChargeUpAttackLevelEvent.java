/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.mobAbilityLevelEvent;

import java.awt.Color;
import java.awt.geom.Point2D;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.SoundPlayer;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.actions.FloatLevelEventAction;
import necesse.entity.levelEvent.mobAbilityLevelEvent.MobAbilityLevelEvent;
import necesse.entity.mobs.AscendedPylonDummyMob;
import necesse.entity.mobs.Mob;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;

public class AscendedPylonChargeUpAttackLevelEvent
extends MobAbilityLevelEvent {
    public float currentTime = 0.0f;
    public int chargeTime;
    public final FloatLevelEventAction setCurrentTimeAction = this.registerAction(new FloatLevelEventAction(){

        @Override
        protected void run(float value) {
            AscendedPylonChargeUpAttackLevelEvent.this.currentTime = value;
        }
    });
    private SoundPlayer chargeSound;

    public AscendedPylonChargeUpAttackLevelEvent() {
    }

    public AscendedPylonChargeUpAttackLevelEvent(Mob owner, int seed, int chargeTime) {
        this();
        this.setupOwnerAndUniqueID(owner, new GameRandom(seed));
        this.chargeTime = chargeTime;
    }

    @Override
    public boolean isNetworkImportant() {
        return true;
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.currentTime = reader.getNextFloat();
        this.chargeTime = reader.getNextInt();
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextFloat(this.currentTime);
        writer.putNextInt(this.chargeTime);
    }

    @Override
    public void tickMovement(float delta) {
        super.tickMovement(delta);
        this.currentTime += delta;
    }

    @Override
    public void clientTick() {
        super.clientTick();
        if (this.owner == null || this.owner.removed() || !this.owner.getLevel().isSamePlace(this.getLevel())) {
            this.over();
            return;
        }
        float progress = this.getEventPercentProgress();
        if (this.chargeSound == null || this.chargeSound.isDone()) {
            this.chargeSound = SoundManager.playSound(GameResources.electricLoop, (SoundEffect)SoundEffect.effect(this.owner).volume(0.0f));
            if (this.chargeSound != null) {
                this.chargeSound.fadeIn(1.0f);
            }
        }
        if (this.chargeSound != null) {
            float pitch = GameMath.lerp(progress, 1.2f, 2.0f);
            float volume = GameMath.lerp(progress, 0.7f, 2.0f);
            this.chargeSound.effect = SoundEffect.effect(this.owner).pitch(pitch).volume(volume).falloffDistance(2500);
            this.chargeSound.refreshLooping(1.0f);
        }
        float ownerX = this.owner.x;
        float ownerY = this.owner.y;
        Color color1 = new Color(255, 0, 231);
        Color color2 = new Color(156, 11, 213);
        Color color3 = new Color(255, 0, 144);
        if (progress < 1.0f) {
            for (int i = 0; i < 6; ++i) {
                int angle = GameRandom.globalRandom.nextInt(360);
                Point2D.Float dir = GameMath.getAngleDir(angle);
                float range = GameRandom.globalRandom.getFloatBetween(25.0f, 40.0f);
                float startX = ownerX + dir.x * range;
                float startY = ownerY + 4.0f;
                float endHeight = AscendedPylonDummyMob.CHARGE_PARTICLE_HEIGHT + 4;
                float startHeight = endHeight + dir.y * range;
                int lifeTime = GameRandom.globalRandom.getIntBetween(200, 500);
                float speed = dir.x * range * 250.0f / (float)lifeTime;
                Color color = GameRandom.globalRandom.getOneOf(color1, color2, color3);
                this.getLevel().entityManager.addParticle(startX, startY, Particle.GType.IMPORTANT_COSMETIC).sprite(GameResources.puffParticles.sprite(GameRandom.globalRandom.nextInt(5), 0, 12)).sizeFades(16, 20).rotates().heightMoves(startHeight, endHeight).movesConstant(-speed, 0.0f).color(color).fadesAlphaTime(100, 50).lifeTime(lifeTime);
            }
        }
        int size = GameMath.lerp(progress, 2, 140);
        this.getLevel().entityManager.addParticle(ownerX, ownerY, Particle.GType.CRITICAL).sprite(GameResources.puffParticles.sprite(GameRandom.globalRandom.nextInt(5), 0, 12)).sizeFades(size, size + 2).rotates().height(AscendedPylonDummyMob.CHARGE_PARTICLE_HEIGHT).color(GameRandom.globalRandom.getOneOf(color1, color2, color3)).lifeTime(200);
    }

    public float getEventPercentProgress() {
        return GameMath.limit(this.currentTime / (float)this.chargeTime, 0.0f, 1.0f);
    }

    public float getEventTimeLeft() {
        return GameMath.limit((float)this.chargeTime - this.currentTime, 0.0f, (float)this.chargeTime);
    }

    @Override
    public void onDispose() {
        super.onDispose();
        if (this.chargeSound != null) {
            this.chargeSound.stop();
        }
    }
}

