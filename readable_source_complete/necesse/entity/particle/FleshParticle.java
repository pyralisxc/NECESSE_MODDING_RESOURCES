/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.particle;

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.Particle;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class FleshParticle
extends Particle {
    public GameTextureSection sprite;
    public float rotation;
    public boolean mirrored;
    public float height;
    public float dh;

    public FleshParticle(Level level, GameTextureSection sprite, float x, float y, float knockbackX, float knockbackY) {
        super(level, x, y, 5000L);
        this.sprite = sprite;
        this.x = x;
        this.y = y;
        this.friction = 0.5f;
        this.rotation = GameRandom.globalRandom.nextInt(360);
        this.mirrored = GameRandom.globalRandom.nextBoolean();
        this.dx = (float)GameRandom.globalRandom.nextGaussian() * 20.0f;
        this.dy = (float)GameRandom.globalRandom.nextGaussian() * 20.0f;
        Point2D.Float normVec = GameMath.normalize(knockbackX, knockbackY);
        this.dx += normVec.x * 50.0f;
        this.dy += normVec.y * 50.0f;
        this.hasCollision = true;
        this.collision = new Rectangle(-5, -5, 10, 10);
        this.height = GameRandom.globalRandom.getFloatBetween(10.0f, 20.0f);
        this.dh = GameRandom.globalRandom.getFloatBetween(20.0f, 30.0f);
    }

    public FleshParticle(Level level, GameTextureSection sprite, float x, float y, float randomUp, float knockbackX, float knockbackY) {
        this(level, sprite, x, y - GameRandom.globalRandom.nextFloat() * randomUp, knockbackX, knockbackY);
    }

    public FleshParticle(Level level, GameTexture texture, int spriteX, int spriteY, int spriteRes, float x, float y, float knockbackX, float knockbackY) {
        this(level, new GameTextureSection(texture).sprite(spriteX, spriteY, spriteRes), x, y, knockbackX, knockbackY);
    }

    public FleshParticle(Level level, GameTexture texture, int spriteX, int spriteY, int spriteWidth, int spriteHeight, float x, float y, float knockbackX, float knockbackY) {
        this(level, new GameTextureSection(texture).sprite(spriteX, spriteY, spriteWidth, spriteHeight), x, y, knockbackX, knockbackY);
    }

    public FleshParticle(Level level, GameTexture texture, int spriteX, int spriteY, int spriteRes, float x, float y, float randomUp, float knockbackX, float knockbackY) {
        this(level, texture, spriteX, spriteY, spriteRes, x, y - GameRandom.globalRandom.nextFloat() * randomUp, knockbackX, knockbackY);
    }

    public FleshParticle(Level level, GameTexture texture, int spriteX, int spriteY, int spriteWidth, int spriteHeight, float x, float y, float randomUp, float knockbackX, float knockbackY) {
        this(level, texture, spriteX, spriteY, spriteWidth, spriteHeight, x, y - GameRandom.globalRandom.nextFloat() * randomUp, knockbackX, knockbackY);
    }

    @Override
    public void tickMovement(float delta) {
        float speed = GameMath.preciseDistance(0.0f, 0.0f, this.dx, this.dy);
        super.tickMovement(delta);
        if (speed > 0.0f) {
            float rotate = speed * 3.0f * delta / 250.0f;
            this.rotation = this.dx < 0.0f ? (this.rotation -= rotate) : (this.rotation += rotate);
        }
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
        int halfWidth = this.sprite.getWidth() / 2;
        int halfHeight = this.sprite.getHeight() / 2;
        int drawX = camera.getDrawX(this.x) - halfWidth;
        int drawY = camera.getDrawY(this.y) - halfHeight + 4 - (int)Math.max(0.0f, this.height);
        float alpha = 1.0f;
        if (cycle > 0.5f) {
            alpha = Math.abs(cycle - 1.0f) * 2.0f;
        }
        final TextureDrawOptionsEnd options = this.sprite.initDraw().light(light).alpha(alpha).rotate(this.rotation, halfWidth, halfHeight).mirror(this.mirrored, false).pos(drawX, drawY);
        list.add(new EntityDrawable(this){

            @Override
            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
    }
}

