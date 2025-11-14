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
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.projectile.Projectile;
import necesse.entity.trails.Trail;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class BloodClawProjectile
extends Projectile {
    protected float angleChangeCounter = 0.2f;
    protected Trail trail;

    public BloodClawProjectile() {
    }

    public BloodClawProjectile(Level level, float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, Mob owner) {
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
        this.piercing = 3;
        this.height = 16.0f;
        this.setWidth(45.0f, true);
        this.isSolid = true;
        this.bouncing = 0;
        this.givesLight = true;
        this.particleRandomOffset = 14.0f;
        this.spawnTime = this.getWorldEntity().getTime();
    }

    @Override
    public Color getParticleColor() {
        return new Color(145, 0, 0);
    }

    @Override
    public Trail getTrail() {
        return new Trail(this, this.getLevel(), new Color(164, 0, 0), this.getTrailThickness(), 340, this.getHeight());
    }

    @Override
    public float getTrailThickness() {
        return this.traveledDistance / 18.0f;
    }

    @Override
    public void onMoveTick(Point2D.Float startPos, double movedDist) {
        super.onMoveTick(startPos, movedDist);
        if (this.traveledDistance > (float)this.distance * this.angleChangeCounter) {
            this.angleChangeCounter += 0.2f;
            GameRandom random = new GameRandom((long)(this.angleChangeCounter * (float)this.getUniqueID() * (float)GameRandom.prime(this.getUniqueID())));
            this.setAngle(this.angle += (float)random.getIntBetween(-50, 50));
        }
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextFloat(this.angleChangeCounter);
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.angleChangeCounter = reader.getNextFloat();
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
        final TextureDrawOptionsEnd options = this.texture.initDraw().light(light.minLevelCopy(Math.min(light.getLevel() + 100.0f, 150.0f))).rotate(this.getWorldEntity().getTime() - this.spawnTime).alpha(alpha).color(new Color(164, 0, 0)).pos(drawX, drawY);
        list.add(new EntityDrawable(this){

            @Override
            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
    }

    @Override
    protected SoundSettings getSpawnSound() {
        return null;
    }
}

