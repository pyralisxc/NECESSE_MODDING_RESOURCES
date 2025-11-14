/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject.furniture;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import necesse.engine.util.GameRandom;
import necesse.entity.particle.ParticleOption;
import necesse.entity.projectile.BombProjectile;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.furniture.LampObject;
import necesse.level.maps.Level;

public class CandlesObject
extends LampObject {
    public float flameHue = ParticleOption.defaultFlameHue;
    public float smokeHue = ParticleOption.defaultSmokeHue;

    public CandlesObject(String textureName, ToolType toolType, Color mapColor, float lightHue, float lightSat) {
        super(textureName, new Rectangle(4, 4, 24, 24), toolType, mapColor, lightHue, lightSat);
        this.lightLevel = 100;
        this.furnitureType = "candles";
        this.hoverHitbox = new Rectangle(0, -10, 32, 42);
    }

    public CandlesObject(String textureName, Color mapColor, float lightHue, float lightSat) {
        this(textureName, ToolType.ALL, mapColor, lightHue, lightSat);
    }

    @Override
    public void tickEffect(Level level, int layerID, int tileX, int tileY) {
        if (GameRandom.globalRandom.getEveryXthChance(10) && this.isActive(level, tileX, tileY)) {
            byte rotation = level.getObjectRotation(tileX, tileY);
            Point point = null;
            int height = 16;
            switch (rotation) {
                case 0: {
                    point = GameRandom.globalRandom.getOneOf(new Point(-6, 0), new Point(0, 2), new Point(6, -4));
                    break;
                }
                case 1: {
                    point = GameRandom.globalRandom.getOneOf(new Point(-6, 6), new Point(0, -6), new Point(6, 0));
                    break;
                }
                case 2: {
                    point = GameRandom.globalRandom.getOneOf(new Point(-6, -6), new Point(0, 4), new Point(6, 0));
                    break;
                }
                case 3: {
                    point = GameRandom.globalRandom.getOneOf(new Point(-6, 0), new Point(0, -10), new Point(6, 6));
                }
            }
            if (point != null) {
                BombProjectile.spawnFuseParticle(level, tileX * 32 + 16 + point.x, tileY * 32 + 16 + point.y, height, this.flameHue, this.smokeHue);
            }
        }
    }
}

