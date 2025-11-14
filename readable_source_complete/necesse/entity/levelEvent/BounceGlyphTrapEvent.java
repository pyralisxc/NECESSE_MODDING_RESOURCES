/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent;

import java.awt.Color;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.GlyphObjectTrapEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.staticBuffs.BounceGlyphBuff;
import necesse.gfx.GameResources;
import necesse.gfx.gameTexture.GameTexture;

public class BounceGlyphTrapEvent
extends GlyphObjectTrapEvent {
    public static Color particleColor = new Color(168, 63, 253);
    public static float particleHue = 273.0f;

    public BounceGlyphTrapEvent() {
    }

    public BounceGlyphTrapEvent(int x, int y, GameRandom uniqueIDRandom) {
        super(x, y, uniqueIDRandom);
    }

    @Override
    public GameTexture getParticleTexture() {
        return GameResources.bounceGlyphParticle;
    }

    @Override
    public Color getParticleColor() {
        return particleColor;
    }

    @Override
    public float getParticleColorHue() {
        return particleHue;
    }

    @Override
    public void playGlyphSound() {
        SoundManager.playSound(GameResources.bounce, (SoundEffect)SoundEffect.effect(this.x, this.y).pitch(GameRandom.globalRandom.getFloatBetween(0.85f, 1.35f)));
    }

    @Override
    public void applyGlyphServer(Mob target) {
        int duration = 4000;
        target.buffManager.addBuff(new ActiveBuff(BuffRegistry.BOUNCE_GLYPH, target, duration, null), true, true);
        BounceGlyphBuff.bounceMobAndSendPacket(this.level, this.x, this.y, target);
        Mob mount = target.getMount();
        if (mount != null) {
            mount.buffManager.addBuff(new ActiveBuff(BuffRegistry.BOUNCE_GLYPH, mount, duration, null), true, true);
            BounceGlyphBuff.bounceMobAndSendPacket(this.level, this.x, this.y, mount);
        }
    }
}

