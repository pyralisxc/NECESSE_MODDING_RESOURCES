/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.mobAbilityLevelEvent;

import java.awt.Color;
import java.awt.geom.Point2D;
import necesse.engine.sound.SoundSettings;
import necesse.entity.ParticleBeamHandler;
import necesse.entity.levelEvent.mobAbilityLevelEvent.GenericBeamLevelEvent;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.gfx.GameResources;
import necesse.gfx.gameTexture.GameSprite;

public class TheVoidClawBeamLevelEvent
extends GenericBeamLevelEvent {
    public TheVoidClawBeamLevelEvent() {
    }

    public TheVoidClawBeamLevelEvent(Mob owner, float startAngle, float endAngle, long startTime, int sweepTime, int seed, float distance, GameDamage damage, int knockback, int hitCooldown, int bounces) {
        super(owner, startAngle, endAngle, startTime, sweepTime, seed, distance, damage, knockback, hitCooldown, bounces);
    }

    @Override
    public void init() {
        super.init();
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
        return new Point2D.Float(this.owner.x, this.owner.y - 20.0f - (float)this.owner.getFlyingHeight());
    }

    @Override
    public SoundSettings getSound() {
        return super.getSound().fallOffDistance(3000);
    }

    @Override
    public void modifyTrail(ParticleBeamHandler handler) {
        handler.sprite(new GameSprite(GameResources.chains, 11, 0, 32)).color(() -> new Color(1.0f, 1.0f, 1.0f, this.getAlpha())).particleColor(() -> new Color(0.0f, 0.0f, 0.0f, this.getAlpha())).particleThicknessMod(4.0f).thickness(25, 20).height(16.0f).drawOnTop(1000);
    }

    public float getAlpha() {
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

