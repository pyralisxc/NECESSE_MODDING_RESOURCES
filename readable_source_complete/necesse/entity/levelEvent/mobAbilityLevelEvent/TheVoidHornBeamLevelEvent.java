/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.mobAbilityLevelEvent;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Point2D;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.SoundSettings;
import necesse.entity.ParticleBeamHandler;
import necesse.entity.levelEvent.mobAbilityLevelEvent.GenericBeamLevelEvent;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.hostile.bosses.theVoid.TheVoidHornMob;
import necesse.gfx.GameResources;
import necesse.gfx.gameTexture.GameSprite;

public class TheVoidHornBeamLevelEvent
extends GenericBeamLevelEvent {
    private boolean isLeftHorn;

    public TheVoidHornBeamLevelEvent() {
    }

    public TheVoidHornBeamLevelEvent(Mob owner, boolean isLeftHorn, float startAngle, float endAngle, long startTime, int sweepTime, int seed, float distance, GameDamage damage, int knockback, int hitCooldown, int bounces) {
        super(owner, startAngle, endAngle, startTime, sweepTime, seed, distance, damage, knockback, hitCooldown, bounces);
        this.isLeftHorn = isLeftHorn;
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextBoolean(this.isLeftHorn);
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.isLeftHorn = reader.getNextBoolean();
    }

    @Override
    public void init() {
        super.init();
        if (this.owner != null && this.isClient()) {
            SoundManager.playSound(GameResources.magicbolt4, (SoundEffect)SoundEffect.effect(this).falloffDistance(1250).volume(0.8f).pitch(0.7f));
        }
    }

    @Override
    public int getHitboxWidth() {
        if (this.getAlpha() < 0.5f) {
            return 0;
        }
        return 10;
    }

    @Override
    public Point2D.Float getBeamStartPosition(float currentAngle, Point2D.Float dir) {
        if (this.owner instanceof TheVoidHornMob) {
            TheVoidHornMob hornMob = (TheVoidHornMob)this.owner;
            Point offset = hornMob.getHornOffset();
            return new Point2D.Float(hornMob.x + (float)offset.x, hornMob.y + (float)offset.y);
        }
        return super.getBeamStartPosition(currentAngle, dir);
    }

    @Override
    public SoundSettings getSound() {
        return super.getSound().fallOffDistance(3000);
    }

    @Override
    public void modifyTrail(ParticleBeamHandler handler) {
        handler.sprite(new GameSprite(GameResources.chains, 11, 0, 32)).color(() -> new Color(1.0f, 1.0f, 1.0f, this.getAlpha())).particleColor(() -> new Color(0.0f, 0.0f, 0.0f, this.getAlpha())).particleThicknessMod(4.0f).thickness(25, 20).height(16.0f).drawOnTop(50);
    }

    public float getAlpha() {
        if (this.owner instanceof TheVoidHornMob && ((TheVoidHornMob)this.owner).isBroken) {
            return 0.0f;
        }
        int fadeTime = Math.min(500, this.sweepTime / 5);
        int timeSinceStart = this.getTimeSinceStart();
        if (timeSinceStart < fadeTime) {
            float progress = (float)timeSinceStart / (float)fadeTime;
            return Math.max(0.0f, progress);
        }
        int timeLeft = this.getTimeLeft();
        if (timeLeft < fadeTime) {
            float progress = (float)timeLeft / (float)fadeTime;
            return Math.max(0.0f, progress);
        }
        return 1.0f;
    }
}

