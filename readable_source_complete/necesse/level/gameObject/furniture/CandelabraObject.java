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

public class CandelabraObject
extends LampObject {
    public float flameHue = ParticleOption.defaultFlameHue;
    public float smokeHue = ParticleOption.defaultSmokeHue;

    public CandelabraObject(String textureName, ToolType toolType, Color mapColor, float lightHue, float lightSat, String ... category) {
        super(textureName, new Rectangle(4, 4, 24, 24), toolType, mapColor, lightHue, lightSat);
        this.setItemCategory("objects", "furniture");
        this.setCraftingCategory("objects", "furniture");
        this.furnitureType = "candelabra";
        this.hoverHitbox = new Rectangle(0, -28, 32, 60);
        if (category.length > 0) {
            this.setItemCategory(category);
            this.setCraftingCategory(category);
        } else {
            this.setItemCategory("objects", "lighting");
            this.setCraftingCategory("objects", "lighting");
        }
    }

    public CandelabraObject(String textureName, Color mapColor, float lightHue, float lightSat, String ... category) {
        this(textureName, ToolType.ALL, mapColor, lightHue, lightSat, category);
    }

    @Override
    public void tickEffect(Level level, int layerID, int tileX, int tileY) {
        if (GameRandom.globalRandom.getEveryXthChance(20) && this.isActive(level, tileX, tileY)) {
            Point point;
            byte rotation = level.getObjectRotation(tileX, tileY);
            int height = 40;
            if (rotation == 0 || rotation == 2) {
                point = GameRandom.globalRandom.getOneOf(new Point(-10, 0), new Point(0, 0), new Point(10, 0));
            } else {
                height += GameRandom.globalRandom.getOneOf(-5, 0, 5).intValue();
                point = new Point(0, 0);
            }
            BombProjectile.spawnFuseParticle(level, tileX * 32 + 16 + point.x, tileY * 32 + 16 + point.y, height, this.flameHue, this.smokeHue);
        }
    }
}

