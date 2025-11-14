/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.projectile.pathProjectile;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.projectile.pathProjectile.PositionedCirclingProjectile;
import necesse.entity.trails.Trail;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;

public class CryoWarningCirclingProjectile
extends PositionedCirclingProjectile {
    protected float radius;
    protected boolean clockwise;

    public CryoWarningCirclingProjectile() {
    }

    public CryoWarningCirclingProjectile(float centerX, float centerY, float startRadius, float startAngle, boolean clockwise, float speed, int distance) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.radius = startRadius;
        this.currentAngle = startAngle;
        this.clockwise = clockwise;
        this.speed = speed;
        this.setDistance(distance);
    }

    @Override
    public void init() {
        super.init();
        this.maxMovePerTick = 12;
        this.height = 0.0f;
        this.canHitMobs = false;
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextFloat(this.radius);
        writer.putNextBoolean(this.clockwise);
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.radius = reader.getNextFloat();
        this.clockwise = reader.getNextBoolean();
    }

    @Override
    public float getRadius() {
        return this.radius;
    }

    @Override
    public boolean rotatesClockwise() {
        return this.clockwise;
    }

    @Override
    public void onMoveTick(Point2D.Float startPos, double movedDist) {
        super.onMoveTick(startPos, movedDist);
        this.radius = (float)((double)this.radius + movedDist * 1.2);
    }

    @Override
    public Color getParticleColor() {
        return new Color(64, 151, 234);
    }

    @Override
    public Trail getTrail() {
        Trail trail = new Trail(this, this.getLevel(), new Color(64, 151, 234), 36.0f, 1000, this.getHeight());
        trail.drawOnTop = true;
        return trail;
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
    }
}

