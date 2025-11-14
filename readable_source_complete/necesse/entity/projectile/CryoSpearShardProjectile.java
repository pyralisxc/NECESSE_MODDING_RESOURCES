/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.projectile;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.projectile.Projectile;
import necesse.entity.trails.Trail;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class CryoSpearShardProjectile
extends Projectile {
    private float startSpeed;
    private int sprite;

    public CryoSpearShardProjectile() {
    }

    public CryoSpearShardProjectile(float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback, Mob owner, int attackHeight) {
        this.x = x;
        this.y = y;
        this.setTarget(targetX, targetY);
        this.speed = speed;
        this.setDistance(distance);
        this.setDamage(damage);
        this.knockback = knockback;
        this.setOwner(owner);
        this.height = attackHeight;
    }

    @Override
    public void init() {
        super.init();
        this.startSpeed = this.speed;
        this.setWidth(10.0f);
        if (this.texture != null) {
            this.sprite = GameRandom.globalRandom.nextInt(this.texture.getHeight() / 32);
        }
    }

    @Override
    public void onMoveTick(Point2D.Float startPos, double movedDist) {
        super.onMoveTick(startPos, movedDist);
        if (this.startSpeed == 0.0f) {
            return;
        }
        float perc = Math.abs(GameMath.limit(this.traveledDistance / (float)this.distance, 0.0f, 1.0f) - 1.0f);
        this.speed = Math.max(10.0f, perc * this.startSpeed);
    }

    @Override
    public boolean canHit(Mob mob) {
        return super.canHit(mob) && this.startSpeed != 0.0f;
    }

    @Override
    public Color getParticleColor() {
        return new Color(0, 222, 218);
    }

    @Override
    public Trail getTrail() {
        return new Trail(this, this.getLevel(), this.getParticleColor(), 12.0f, 200, this.getHeight());
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        if (this.removed()) {
            return;
        }
        GameLight light = level.getLightLevel(this);
        GameTextureSection shardTexture = new GameTextureSection(this.texture).sprite(0, this.sprite, 32);
        GameTextureSection shadowTexture = new GameTextureSection(this.shadowTexture).sprite(0, this.sprite, 32);
        int drawX = camera.getDrawX(this.x) - shardTexture.getWidth() / 2;
        int drawY = camera.getDrawY(this.y) - shardTexture.getHeight() / 2;
        final TextureDrawOptionsEnd options = shardTexture.initDraw().light(light).rotate(this.getAngle(), shardTexture.getWidth() / 2, shardTexture.getHeight() / 2).pos(drawX, drawY - (int)this.getHeight());
        list.add(new EntityDrawable(this){

            @Override
            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
        TextureDrawOptionsEnd shadowOptions = shadowTexture.initDraw().light(light).rotate(this.getAngle(), shardTexture.getWidth() / 2, shardTexture.getHeight() / 2).pos(drawX, drawY);
        tileList.add(tm -> shadowOptions.draw());
    }

    @Override
    public float getAngle() {
        return this.getWorldEntity().getTime() - this.spawnTime;
    }

    @Override
    protected SoundSettings getSpawnSound() {
        return new SoundSettings(GameResources.cryoSpear);
    }
}

