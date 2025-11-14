/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import necesse.engine.util.GameRandom;
import necesse.entity.particle.ParticleOption;
import necesse.entity.projectile.BombProjectile;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.gameObject.WallTorchObject;
import necesse.level.maps.Level;

public class WallCandleObject
extends WallTorchObject {
    @Override
    public void loadTextures() {
        this.texture = GameTexture.fromFile("objects/" + this.getStringID());
    }

    @Override
    public void tickEffect(Level level, int layerID, int tileX, int tileY) {
        if (this.disableParticles) {
            return;
        }
        if (GameRandom.globalRandom.getEveryXthChance(40) && this.isActive(level, layerID, tileX, tileY)) {
            byte rotation = level.getObjectRotation(layerID, tileX, tileY);
            int sprite = this.getSprite(level, tileX, tileY, rotation);
            int xOffset = 16;
            int startHeight = 10;
            if (sprite == 0) {
                startHeight += 32;
            } else if (sprite == 1 || sprite == 3) {
                startHeight += 14;
                xOffset = sprite == 1 ? 27 : 5;
            }
            BombProjectile.spawnFuseParticle(level, tileX * 32 + xOffset, tileY * 32 + 16 + 2, startHeight, this.lightHue, ParticleOption.defaultSmokeHue);
        }
    }
}

