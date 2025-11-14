/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.projectile;

import java.awt.Color;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.Projectile;
import necesse.entity.trails.Trail;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class AgedChampionWaveProjectile
extends Projectile {
    public AgedChampionWaveProjectile() {
    }

    public AgedChampionWaveProjectile(Level level, float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, Mob owner) {
        this.setLevel(level);
        this.x = x;
        this.y = y;
        this.setTarget(targetX, targetY);
        this.speed = speed;
        this.setDamage(damage);
        this.setOwner(owner);
        this.setDistance(distance);
    }

    @Override
    public void init() {
        super.init();
        this.piercing = 10;
        this.height = 16.0f;
        this.setWidth(45.0f, true);
        this.isSolid = true;
        this.givesLight = true;
        this.particleRandomOffset = 14.0f;
    }

    @Override
    public Color getParticleColor() {
        return new Color(208, 204, 50);
    }

    @Override
    public Trail getTrail() {
        return null;
    }

    @Override
    public void clientTick() {
        super.clientTick();
        float particleAngle = this.getAngle() - 45.0f - 90.0f;
        this.getLevel().entityManager.addParticle(this.x, this.y, Particle.GType.COSMETIC).sprite(GameResources.agedChampionWaveParticles.sprite(0, 0, 54)).color((options, lifeTime, timeAlive, lifePercent) -> options.alpha(0.5f - 0.5f * lifePercent)).height(this.height).size((options, lifeTime, timeAlive, lifePercent) -> options.size(54, 54)).rotation((lifeTime, timeAlive, lifePercent) -> particleAngle).lifeTime(300);
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        if (this.removed()) {
            return;
        }
        float alpha = this.getFadeAlphaTime(300, 200);
        GameLight light = level.getLightLevel(this);
        int drawX = camera.getDrawX(this.x) - this.texture.getWidth() / 2;
        int drawY = camera.getDrawY(this.y - this.getHeight()) - this.texture.getHeight() / 2;
        final TextureDrawOptionsEnd options = this.texture.initDraw().light(light.minLevelCopy(Math.min(light.getLevel() + 100.0f, 150.0f))).rotate(this.getAngle() - 135.0f, this.texture.getWidth() / 2, this.texture.getHeight() / 2).alpha(alpha).pos(drawX, drawY);
        list.add(new EntityDrawable(this){

            @Override
            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
    }
}

