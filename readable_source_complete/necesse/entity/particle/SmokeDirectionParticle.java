/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.particle;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class SmokeDirectionParticle
extends Particle {
    float angle;
    public Color col;

    public SmokeDirectionParticle(Level level, float x, float y, float targetX, float targetY, float speed) {
        super(level, x, y, 1000L);
        this.setTarget(targetX, targetY);
        this.dx *= speed;
        this.dy *= speed;
        this.friction = 1.0f;
        this.col = new Color(1.0f, 1.0f, 1.0f, 1.0f);
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        float life = this.getLifeCyclePercent();
        if (this.removed()) {
            return;
        }
        GameLight light = level.getLightLevel(this);
        int drawX = this.getX() - camera.getX() - 16;
        int drawY = this.getY() - camera.getY() - 16 - 16;
        int spriteY = life < 0.5f ? 0 : 1;
        int spriteX = 4;
        if (life < 0.1f) {
            spriteX = 0;
        } else if (life < 0.2f) {
            spriteX = 1;
        } else if (life < 0.3f) {
            spriteX = 2;
        } else if (life < 0.4f) {
            spriteX = 3;
        } else if (life < 0.5f) {
            spriteX = 4;
        } else if (life < 0.6f) {
            spriteX = 0;
        } else if (life < 0.7f) {
            spriteX = 1;
        } else if (life < 0.8f) {
            spriteX = 2;
        } else if (life < 0.9f) {
            spriteX = 3;
        }
        final TextureDrawOptionsEnd options = GameResources.smokeParticles.initDraw().sprite(spriteX, spriteY + 4, 64).colorLight(this.col, light).rotate(this.getAngle() - 90.0f, 16, 16).size(32, 32).pos(drawX, drawY);
        list.add(new EntityDrawable(this){

            @Override
            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
    }

    public void moveDist(float dist) {
        Point2D.Float dir = this.getDir(this.dx, this.dy);
        this.x += dir.x * dist;
        this.y += dir.y * dist;
    }

    public void setTarget(float x, float y) {
        Point2D.Float point = this.getDir(x - this.x, y - this.y);
        this.dx = point.x;
        this.dy = point.y;
        this.angle = (float)Math.toDegrees(Math.atan(this.dy / this.dx));
        this.angle = this.dx < 0.0f ? (this.angle += 270.0f) : (this.angle += 90.0f);
    }

    public Point2D.Float getDir(float dx, float dy) {
        Point2D.Float tempPoint = new Point2D.Float(dx, dy);
        float dist = (float)tempPoint.distance(0.0, 0.0);
        float normX = tempPoint.x / dist;
        float normY = tempPoint.y / dist;
        return new Point2D.Float(normX, normY);
    }

    public float getAngle() {
        return this.angle % 360.0f;
    }
}

