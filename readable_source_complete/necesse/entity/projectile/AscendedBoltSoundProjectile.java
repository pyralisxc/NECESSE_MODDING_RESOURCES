/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.projectile;

import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.projectile.AscendedBoltProjectile;
import necesse.gfx.GameResources;
import necesse.level.maps.Level;

public class AscendedBoltSoundProjectile
extends AscendedBoltProjectile {
    public AscendedBoltSoundProjectile() {
    }

    public AscendedBoltSoundProjectile(Level level, float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, Mob owner) {
        super(level, x, y, targetX, targetY, speed, distance, damage, owner);
    }

    public AscendedBoltSoundProjectile(Level level, float x, float y, float angle, float speed, int distance, GameDamage damage, Mob owner) {
        super(level, x, y, angle, speed, distance, damage, owner);
    }

    @Override
    public void init() {
        super.init();
        if (this.isClient()) {
            SoundManager.playSound(GameResources.laserBlast1, (SoundEffect)SoundEffect.effect(this.x, this.y).pitch(GameRandom.globalRandom.getFloatBetween(1.1f, 1.2f)).volume(0.25f));
        }
    }
}

