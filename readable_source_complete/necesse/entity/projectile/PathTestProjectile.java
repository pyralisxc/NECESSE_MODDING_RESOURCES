/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.projectile;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.util.GameMath;
import necesse.engine.util.IntersectionPoint;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.ParticleOption;
import necesse.entity.projectile.pathProjectile.PathProjectile;
import necesse.entity.trails.Trail;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class PathTestProjectile
extends PathProjectile {
    public boolean inverted = false;
    public float startX;
    public float startY;
    public float startDx;
    public float startDy;
    public float movedDist;

    public PathTestProjectile() {
    }

    public PathTestProjectile(Level level, Mob owner, float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback, boolean inverted) {
        this.setLevel(level);
        this.setOwner(owner);
        this.x = x;
        this.y = y;
        this.startX = x;
        this.startY = y;
        this.setTarget(targetX, targetY);
        this.startDx = this.dx;
        this.startDy = this.dy;
        this.speed = speed;
        this.distance = distance;
        this.setDamage(damage);
        this.knockback = knockback;
        this.inverted = inverted;
    }

    @Override
    public void setupPositionPacket(PacketWriter writer) {
        super.setupPositionPacket(writer);
        writer.putNextFloat(this.startX);
        writer.putNextFloat(this.startY);
        writer.putNextFloat(this.startDx);
        writer.putNextFloat(this.startDy);
        writer.putNextFloat(this.movedDist);
        writer.putNextBoolean(this.inverted);
    }

    @Override
    public void applyPositionPacket(PacketReader reader) {
        super.applyPositionPacket(reader);
        this.startX = reader.getNextFloat();
        this.startY = reader.getNextFloat();
        this.startDx = reader.getNextFloat();
        this.startDy = reader.getNextFloat();
        this.movedDist = reader.getNextFloat();
        this.inverted = reader.getNextBoolean();
    }

    @Override
    public void init() {
        super.init();
        this.height = 18.0f;
        this.piercing = 200;
        this.bouncing = 10;
        this.givesLight = true;
        this.trailOffset = 0.0f;
        this.isSolid = true;
        this.autoSetDirection = true;
    }

    @Override
    public Point2D.Float getPosition(double dist) {
        this.movedDist = (float)((double)this.movedDist + dist);
        Point2D.Float dirPos = new Point2D.Float(this.startX + this.startDx * this.movedDist, this.startY + this.startDy * this.movedDist);
        float sin = GameMath.sin(this.movedDist) * 40.0f;
        Point2D.Float perpendicularPoint = GameMath.getPerpendicularPoint(dirPos, this.inverted ? -sin : sin, this.startDx, this.startDy);
        return new Point2D.Float(perpendicularPoint.x, perpendicularPoint.y);
    }

    @Override
    public Color getParticleColor() {
        return new Color(50, 0, 102);
    }

    @Override
    protected int getExtraSpinningParticles() {
        return super.getExtraSpinningParticles() + 2;
    }

    @Override
    protected void modifySpinningParticle(ParticleOption particle) {
        particle.lifeTime(2000);
    }

    @Override
    public Trail getTrail() {
        return new Trail(this, this.getLevel(), new Color(50, 0, 102), 12.0f, 1500, 18.0f);
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        if (this.removed()) {
            return;
        }
        GameLight light = level.getLightLevel(this);
        int drawX = camera.getDrawX(this.x) - this.shadowTexture.getWidth() / 2;
        int drawY = camera.getDrawY(this.y) - this.shadowTexture.getHeight() / 2;
        this.addShadowDrawables(tileList, drawX, drawY, light, this.getAngle(), this.shadowTexture.getHeight() / 2);
    }

    @Override
    public void onBounce(IntersectionPoint p) {
        super.onBounce(p);
        this.startX = this.x;
        this.startY = this.y;
        this.movedDist = 0.0f;
        if (p.dir == IntersectionPoint.Dir.RIGHT || p.dir == IntersectionPoint.Dir.LEFT) {
            this.startDx = -this.startDx;
        } else if (p.dir == IntersectionPoint.Dir.UP || p.dir == IntersectionPoint.Dir.DOWN) {
            this.startDy = -this.startDy;
        }
    }
}

