/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.projectile;

import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.Particle;
import necesse.entity.particle.ProjectileHitStuckParticle;
import necesse.entity.projectile.Projectile;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;
import necesse.level.maps.light.GameLight;

public class CrystalDragonShardProjectile
extends Projectile {
    public CrystalDragonShardProjectile() {
    }

    public CrystalDragonShardProjectile(float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback, Mob owner) {
        this.x = x;
        this.y = y;
        this.setTarget(targetX, targetY);
        this.speed = speed;
        this.setDistance(distance);
        this.setDamage(damage);
        this.knockback = knockback;
        this.setOwner(owner);
    }

    public CrystalDragonShardProjectile(Level level, float x, float y, float angle, float speed, GameDamage damage, Mob owner) {
        this.x = x;
        this.y = y;
        this.setLevel(level);
        this.setAngle(angle);
        this.setDamage(damage);
        this.setOwner(owner);
        this.setDistance(500);
        this.speed = speed;
    }

    @Override
    public void init() {
        super.init();
        this.isSolid = false;
        this.setWidth(10.0f);
    }

    @Override
    public void doHitLogic(Mob mob, LevelObjectHit object, float x, float y) {
        super.doHitLogic(mob, object, x, y);
        if (this.isClient() && this.bounced == this.getTotalBouncing()) {
            this.getLevel().entityManager.addParticle(new ProjectileHitStuckParticle(mob, this, x, y, GameRandom.globalRandom.getIntBetween(10, 20), 5000L){

                @Override
                public void addDrawables(Mob target, float x, float y, float angle, List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
                    int fadeTime;
                    GameLight light = level.getLightLevel(this);
                    int drawX = camera.getDrawX(x) - CrystalDragonShardProjectile.this.texture.getWidth() / 2;
                    int drawY = camera.getDrawY(y);
                    float alpha = 1.0f;
                    long lifeCycleTime = this.getLifeCycleTime();
                    if (lifeCycleTime >= this.lifeTime - (long)(fadeTime = 250)) {
                        alpha = Math.abs((float)(lifeCycleTime - (this.lifeTime - (long)fadeTime)) / (float)fadeTime - 1.0f);
                    }
                    final TextureDrawOptionsEnd options = CrystalDragonShardProjectile.this.texture.initDraw().light(light).rotate(CrystalDragonShardProjectile.this.getAngle(), CrystalDragonShardProjectile.this.texture.getWidth() / 2, 0).alpha(alpha).pos(drawX, drawY - (int)CrystalDragonShardProjectile.this.getHeight());
                    EntityDrawable drawable = new EntityDrawable(this){

                        @Override
                        public void draw(TickManager tickManager) {
                            options.draw();
                        }
                    };
                    if (target != null) {
                        topList.add(drawable);
                    } else {
                        list.add(drawable);
                    }
                }
            }, Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        if (this.removed()) {
            return;
        }
        GameLight light = level.getLightLevel(this);
        int drawX = camera.getDrawX(this.x) - this.texture.getWidth() / 2;
        int drawY = camera.getDrawY(this.y);
        final TextureDrawOptionsEnd options = this.texture.initDraw().light(light).rotate(this.getAngle(), this.texture.getWidth() / 2, 0).pos(drawX, drawY - (int)this.getHeight());
        list.add(new EntityDrawable(this){

            @Override
            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
        this.addShadowDrawables(tileList, drawX, drawY, light, this.getAngle(), 0);
    }

    @Override
    protected SoundSettings getSpawnSound() {
        return new SoundSettings(GameResources.crystalGlyph).volume(0.5f);
    }
}

