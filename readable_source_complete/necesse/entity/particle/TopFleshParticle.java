/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.particle;

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.Particle;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class TopFleshParticle
extends Particle {
    GameTexture texture;
    int spriteX;
    int spriteY;
    int spriteRes;
    int rotation;
    boolean mirrored;
    public float height;
    public float dh;

    public TopFleshParticle(Level level, GameTexture texture, int spriteX, int spriteY, int spriteRes, float x, float y, float knockbackX, float knockbackY) {
        super(level, x, y, 5000L);
        this.texture = texture;
        this.spriteX = spriteX;
        this.spriteY = spriteY;
        this.spriteRes = spriteRes;
        this.x = x;
        this.y = y;
        this.friction = 0.5f;
        this.rotation = GameRandom.globalRandom.nextInt(360);
        this.mirrored = GameRandom.globalRandom.nextBoolean();
        this.dx = (float)GameRandom.globalRandom.nextGaussian() * 20.0f;
        this.dy = (float)GameRandom.globalRandom.nextGaussian() * 20.0f;
        Point2D.Float normVec = this.getNorm(knockbackX, knockbackY);
        this.dx += normVec.x * 50.0f;
        this.dy += normVec.y * 50.0f;
        this.hasCollision = false;
        this.collision = new Rectangle(-5, -5, 10, 10);
        this.height = GameRandom.globalRandom.getFloatBetween(10.0f, 20.0f);
        this.dh = GameRandom.globalRandom.getFloatBetween(20.0f, 30.0f);
    }

    public TopFleshParticle(Level level, GameTexture texture, int spriteX, int spriteY, int spriteRes, float x, float y, float randomUp, float knockbackX, float knockbackY) {
        this(level, texture, spriteX, spriteY, spriteRes, x, y - GameRandom.globalRandom.nextFloat() * randomUp, knockbackX, knockbackY);
    }

    @Override
    public void tickMovement(float delta) {
        super.tickMovement(delta);
        if (this.height != -1.0f) {
            float heightChange = 30.0f * delta / 250.0f;
            this.dh -= heightChange;
            this.height += this.dh * delta / 250.0f;
            if (this.height < 0.0f) {
                this.dh = -this.dh * 0.5f;
                this.height = -this.height;
                if (Math.abs(this.dh) < heightChange * 2.0f) {
                    this.height = -1.0f;
                    this.dh = 0.0f;
                }
            }
        }
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        float cycle = this.getLifeCyclePercent();
        if (this.removed()) {
            return;
        }
        GameLight light = level.getLightLevel(this);
        int drawX = camera.getDrawX(this.x) - this.spriteRes / 2;
        int drawY = camera.getDrawY(this.y) - this.spriteRes / 2 + 4 - (int)Math.max(0.0f, this.height);
        float alpha = 1.0f;
        if (cycle > 0.5f) {
            alpha = Math.abs(cycle - 1.0f) * 2.0f;
        }
        TextureDrawOptionsEnd options = this.texture.initDraw().sprite(this.spriteX, this.spriteY, this.spriteRes).light(light).alpha(alpha).rotate(this.rotation, this.spriteRes / 2, this.spriteRes / 2).mirror(this.mirrored, false).pos(drawX, drawY);
        topList.add(tm -> options.draw());
    }

    public Point2D.Float getNorm(float x, float y) {
        float tempDist = (float)new Point2D.Float(0.0f, 0.0f).distance(x, y);
        if (tempDist == 0.0f) {
            return new Point2D.Float(0.0f, 0.0f);
        }
        return new Point2D.Float(x / tempDist, y / tempDist);
    }
}

