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
import necesse.gfx.GameResources;
import necesse.gfx.gameTexture.GameTexture;

public class ReverseDamageGlyphTrapEvent
extends GlyphObjectTrapEvent {
    public static Color particleColor = new Color(161, 27, 90);
    public static float particleHue = 327.0f;

    public ReverseDamageGlyphTrapEvent() {
    }

    public ReverseDamageGlyphTrapEvent(int x, int y, GameRandom uniqueIDRandom) {
        super(x, y, uniqueIDRandom);
    }

    @Override
    public GameTexture getParticleTexture() {
        return GameResources.reverseDamageGlyphParticle;
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
        SoundManager.playSound(GameResources.glyphTrapReverseDamage, (SoundEffect)SoundEffect.effect(this.x, this.y).pitch(GameRandom.globalRandom.getFloatBetween(0.9f, 1.1f)));
    }

    @Override
    public void applyGlyphServer(Mob target) {
        target.buffManager.addBuff(new ActiveBuff(BuffRegistry.REVERSE_DAMAGE_GLYPH, target, 15000, null), true, true);
    }
}

