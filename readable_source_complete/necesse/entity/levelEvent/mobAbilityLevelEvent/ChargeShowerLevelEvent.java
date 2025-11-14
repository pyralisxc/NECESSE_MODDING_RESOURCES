/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.mobAbilityLevelEvent;

import java.awt.Color;
import java.awt.geom.Point2D;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.SoundPlayer;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.mobAbilityLevelEvent.MobAbilityLevelEvent;
import necesse.entity.mobs.AttackAnimMob;
import necesse.entity.mobs.Mob;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;

public class ChargeShowerLevelEvent
extends MobAbilityLevelEvent {
    protected long startTime;
    protected int chargeTime;
    private SoundPlayer chargeSound;

    public ChargeShowerLevelEvent() {
    }

    public ChargeShowerLevelEvent(Mob owner, int seed, long startTime, int chargeTime) {
        super(owner, new GameRandom(seed));
        this.startTime = startTime;
        this.chargeTime = chargeTime;
    }

    @Override
    public boolean isNetworkImportant() {
        return true;
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.startTime = reader.getNextLong();
        this.chargeTime = reader.getNextInt();
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextLong(this.startTime);
        writer.putNextInt(this.chargeTime);
    }

    @Override
    public void clientTick() {
        Point2D.Float attackDir;
        super.clientTick();
        if (this.owner == null || this.owner.removed() || !this.owner.getLevel().isSamePlace(this.getLevel())) {
            this.over();
            return;
        }
        float progress = this.getEventProgress();
        if (this.chargeSound == null || this.chargeSound.isDone()) {
            this.chargeSound = SoundManager.playSound(GameResources.electricLoop, (SoundEffect)SoundEffect.effect(this.owner).volume(0.0f));
            if (this.chargeSound != null) {
                this.chargeSound.fadeIn(1.0f);
            }
        }
        if (this.chargeSound != null) {
            float pitch = GameMath.lerp(progress, 1.2f, 2.0f);
            float volume = GameMath.lerp(progress, 0.2f, 1.0f);
            this.chargeSound.effect = SoundEffect.effect(this.owner).pitch(pitch).volume(volume);
            this.chargeSound.refreshLooping(1.0f);
        }
        float ownerX = this.owner.x;
        float ownerY = this.owner.y;
        if (this.owner instanceof AttackAnimMob && (attackDir = ((AttackAnimMob)this.owner).attackDir) != null) {
            ownerX += attackDir.x * 40.0f;
            ownerY += attackDir.y * 40.0f;
        }
        Color color1 = new Color(199, 40, 34);
        Color color2 = new Color(222, 42, 75);
        Color color3 = new Color(220, 37, 92);
        if (progress < 1.0f) {
            for (int i = 0; i < 6; ++i) {
                int angle = GameRandom.globalRandom.nextInt(360);
                Point2D.Float dir = GameMath.getAngleDir(angle);
                float range = GameRandom.globalRandom.getFloatBetween(25.0f, 40.0f);
                float startX = ownerX + dir.x * range;
                float startY = ownerY + 4.0f;
                float endHeight = 18.0f;
                float startHeight = endHeight + dir.y * range;
                int lifeTime = GameRandom.globalRandom.getIntBetween(200, 500);
                float speed = dir.x * range * 250.0f / (float)lifeTime;
                Color color = GameRandom.globalRandom.getOneOf(color1, color2, color3);
                this.getLevel().entityManager.addParticle(startX, startY, Particle.GType.IMPORTANT_COSMETIC).sprite(GameResources.puffParticles.sprite(GameRandom.globalRandom.nextInt(5), 0, 12)).sizeFades(16, 20).rotates().heightMoves(startHeight, endHeight).movesConstant(-speed, 0.0f).color(color).fadesAlphaTime(100, 50).lifeTime(lifeTime);
            }
        }
        int size = GameMath.lerp(progress, 2, 80);
        this.getLevel().entityManager.addParticle(ownerX, ownerY, Particle.GType.CRITICAL).sprite(GameResources.puffParticles.sprite(GameRandom.globalRandom.nextInt(5), 0, 12)).sizeFades(size, size + 2).rotates().height(14.0f).color(GameRandom.globalRandom.getOneOf(color1, color2, color3)).lifeTime(200);
    }

    @Override
    public void serverHit(Mob target, Packet content, boolean clientSubmitted) {
        super.serverHit(target, content, clientSubmitted);
        if (this.owner == null || this.owner.removed() || !this.owner.getLevel().isSamePlace(this.getLevel())) {
            this.over();
        }
    }

    public float getEventProgress() {
        long timeProgress = this.getTime() - this.startTime;
        return GameMath.limit((float)timeProgress / (float)this.chargeTime, 0.0f, 1.0f);
    }

    @Override
    public void onDispose() {
        super.onDispose();
        if (this.chargeSound != null) {
            this.chargeSound.stop();
        }
    }
}

