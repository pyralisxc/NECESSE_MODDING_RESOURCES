/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.projectile;

import java.awt.Color;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.DeathMessageTable;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.ParticleOption;
import necesse.entity.projectile.Projectile;
import necesse.entity.trails.Trail;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class EvilsProtectorAttack1Projectile
extends Projectile {
    public EvilsProtectorAttack1Projectile() {
    }

    public EvilsProtectorAttack1Projectile(float x, float y, float angle, float speed, int distance, GameDamage damage, Mob owner) {
        this.x = x;
        this.y = y;
        this.setAngle(angle);
        this.speed = speed;
        this.setDamage(damage);
        this.setOwner(owner);
        this.setDistance(distance);
    }

    @Override
    public void init() {
        super.init();
        this.height = 16.0f;
        this.isSolid = false;
        this.givesLight = true;
        this.setWidth(5.0f);
        this.particleDirOffset = -30.0f;
        this.particleRandomOffset = 3.0f;
    }

    @Override
    public Color getParticleColor() {
        return new Color(220, 40, 20);
    }

    @Override
    public Trail getTrail() {
        return new Trail(this, this.getLevel(), new Color(220, 40, 20), 16.0f, 500, 16.0f);
    }

    @Override
    protected int getExtraSpinningParticles() {
        return super.getExtraSpinningParticles() + 1;
    }

    @Override
    protected void modifySpinningParticle(ParticleOption particle) {
        super.modifySpinningParticle(particle);
        particle.sizeFades(8, 14);
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        if (this.removed()) {
            return;
        }
        GameLight light = level.getLightLevel(this);
        int drawX = camera.getDrawX(this.x) - 16;
        int drawY = camera.getDrawY(this.y);
        int anim = GameUtils.getAnim(this.getWorldEntity().getTime(), 6, 400);
        final TextureDrawOptionsEnd options = this.texture.initDraw().sprite(anim, 0, 32, 64).light(light).rotate(this.getAngle(), 16, 0).pos(drawX, drawY - (int)this.getHeight());
        list.add(new EntityDrawable(this){

            @Override
            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
        TextureDrawOptionsEnd shadowOptions = this.shadowTexture.initDraw().sprite(anim, 0, 32, 64).light(light).rotate(this.getAngle(), 16, 0).pos(drawX, drawY);
        tileList.add(tm -> shadowOptions.draw());
    }

    @Override
    public DeathMessageTable getDeathMessages() {
        return this.getDeathMessages("evilsproj", 3);
    }
}

