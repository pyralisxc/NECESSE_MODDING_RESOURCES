/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.projectile;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.EmpressAcidGroundEvent;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.ParticleOption;
import necesse.entity.projectile.Projectile;
import necesse.entity.trails.Trail;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;

public class EmpressAcidProjectile
extends Projectile {
    private double distBuffer;

    public EmpressAcidProjectile() {
    }

    public EmpressAcidProjectile(Level level, float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, Mob owner) {
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
        this.maxMovePerTick = 32;
        this.height = 0.0f;
        this.isSolid = false;
        this.givesLight = true;
        this.canHitMobs = false;
        this.particleRandomOffset = 10.0f;
        this.particleDirOffset = 0.0f;
    }

    @Override
    public void onMoveTick(Point2D.Float startPos, double movedDist) {
        super.onMoveTick(startPos, movedDist);
        this.distBuffer += movedDist;
        if (this.isServer()) {
            while (this.distBuffer > 32.0) {
                this.distBuffer -= 32.0;
                EmpressAcidGroundEvent event = new EmpressAcidGroundEvent(this.getOwner(), (int)this.x, (int)this.y, this.getDamage(), GameRandom.globalRandom);
                this.getLevel().entityManager.events.add(event);
            }
        }
    }

    @Override
    public Color getParticleColor() {
        return new Color(166, 204, 52);
    }

    @Override
    public Trail getTrail() {
        return null;
    }

    @Override
    protected void modifySpinningParticle(ParticleOption particle) {
        super.modifySpinningParticle(particle);
        particle.sprite(GameResources.bubbleParticle.sprite(0, 0, 12)).sizeFades(10, 30).givesLight(75.0f, 0.5f);
    }

    @Override
    protected int getExtraSpinningParticles() {
        return 10;
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
    }
}

