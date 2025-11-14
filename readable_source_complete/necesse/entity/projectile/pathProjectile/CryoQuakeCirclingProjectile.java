/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.projectile.pathProjectile;

import java.awt.geom.Point2D;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.sound.SoundSettings;
import necesse.engine.sound.SoundSettingsRegistry;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GroundPillarList;
import necesse.entity.manager.GroundPillarHandler;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.hostile.bosses.CryoQueenMob;
import necesse.entity.projectile.pathProjectile.PositionedCirclingProjectile;
import necesse.entity.trails.Trail;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;

public class CryoQuakeCirclingProjectile
extends PositionedCirclingProjectile {
    private double distCounter;
    private double distBuffer;
    private final GroundPillarList<CryoQueenMob.CryoPillar> pillars = new GroundPillarList();
    protected float radius;
    protected boolean clockwise;

    public CryoQuakeCirclingProjectile() {
    }

    public CryoQuakeCirclingProjectile(float centerX, float centerY, float startRadius, float startAngle, boolean clockwise, float speed, int distance, GameDamage damage, int knockback, Mob owner) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.radius = startRadius;
        this.currentAngle = startAngle;
        this.clockwise = clockwise;
        this.speed = speed;
        this.setDistance(distance);
        this.setDamage(damage);
        this.knockback = knockback;
        this.setOwner(owner);
    }

    @Override
    public void init() {
        super.init();
        this.maxMovePerTick = 12;
        this.height = 0.0f;
        this.piercing = 1000;
        this.setWidth(24.0f);
        if (this.isClient()) {
            this.getLevel().entityManager.addPillarHandler(new GroundPillarHandler<CryoQueenMob.CryoPillar>(this.pillars){

                @Override
                protected boolean canRemove() {
                    return CryoQuakeCirclingProjectile.this.removed();
                }

                @Override
                public double getCurrentDistanceMoved() {
                    return CryoQuakeCirclingProjectile.this.distCounter;
                }
            });
        }
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

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void onMoveTick(Point2D.Float startPos, double movedDist) {
        super.onMoveTick(startPos, movedDist);
        this.radius = (float)((double)this.radius + movedDist * 1.2);
        this.distCounter += movedDist;
        this.distBuffer += movedDist;
        while (this.distBuffer > 8.0) {
            this.distBuffer -= 8.0;
            GroundPillarList<CryoQueenMob.CryoPillar> groundPillarList = this.pillars;
            synchronized (groundPillarList) {
                this.pillars.add(new CryoQueenMob.CryoPillar((int)(this.x + GameRandom.globalRandom.floatGaussian() * 6.0f), (int)(this.y + GameRandom.globalRandom.floatGaussian() * 4.0f), this.distCounter, this.getWorldEntity().getLocalTime()));
            }
        }
    }

    @Override
    public Trail getTrail() {
        return null;
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
    }

    @Override
    protected SoundSettings getMoveSound() {
        return SoundSettingsRegistry.cryoQuakeProjectileMove;
    }
}

