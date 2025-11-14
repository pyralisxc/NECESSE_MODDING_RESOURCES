/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.projectile.followingProjectile;

import java.awt.Color;
import java.awt.Point;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.projectile.followingProjectile.FollowingProjectile;
import necesse.entity.trails.Trail;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameSprite;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class FallenWizardScepterProjectile
extends FollowingProjectile {
    public FallenWizardScepterProjectile() {
        this.height = 18.0f;
    }

    public FallenWizardScepterProjectile(float x, float y, float targetX, float targetY, int speed, int distance, GameDamage damage, int knockback, Mob owner) {
        this();
        this.setLevel(owner.getLevel());
        this.x = x;
        this.y = y;
        this.setTarget(targetX, targetY);
        this.speed = speed;
        this.setDistance(distance);
        this.setDamage(damage);
        this.knockback = knockback;
        this.setOwner(owner);
        this.turnSpeed = 5.0f;
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextFloat(this.height);
        writer.putNextFloat(this.turnSpeed);
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.height = reader.getNextFloat();
        this.turnSpeed = reader.getNextFloat();
    }

    @Override
    public void init() {
        super.init();
        this.givesLight = true;
        this.trailOffset = -8.0f;
        this.clearTargetPosWhenAligned = true;
    }

    @Override
    protected CollisionFilter getLevelCollisionFilter() {
        return super.getLevelCollisionFilter().addFilter(tp -> tp.object().object.isWall || tp.object().object.isRock);
    }

    @Override
    public Trail getTrail() {
        Trail trail = new Trail(this, this.getLevel(), new Color(50, 0, 102), 30.0f, 250, this.height);
        trail.sprite = new GameSprite(GameResources.chains, 7, 0, 32);
        return trail;
    }

    @Override
    protected Color getWallHitColor() {
        return new Color(50, 0, 102);
    }

    @Override
    public void refreshParticleLight() {
        this.getLevel().lightManager.refreshParticleLightFloat(this.x, this.y, 260.0f, this.lightSaturation);
    }

    @Override
    public void updateTarget() {
    }

    public float getTurnSpeedMod(int targetX, int targetY, float maxMod, float maxAngle, float maxDistance) {
        float distance = (float)new Point(targetX, targetY).distance(this.getX(), this.getY());
        if (distance < maxDistance && distance > 5.0f) {
            float deltaAngle = Math.abs(this.getAngleDifference(this.getAngleToTarget(targetX, targetY)));
            float angleMod = deltaAngle > maxAngle ? 1.0f : (deltaAngle - maxAngle) / maxAngle;
            float distMod = Math.abs(distance - maxDistance) / maxDistance;
            return 1.0f + distMod * maxMod + angleMod * maxMod;
        }
        return 1.0f;
    }

    public float getTurnSpeedMod(int targetX, int targetY, float maxAngle, float maxAngleMod, float maxDistance, float maxDistMod) {
        float distance = (float)new Point(targetX, targetY).distance(this.getX(), this.getY());
        if (distance < maxDistance && distance > 5.0f) {
            float deltaAngle = Math.abs(this.getAngleDifference(this.getAngleToTarget(targetX, targetY)));
            float angleMod = deltaAngle > maxAngle ? 1.0f : deltaAngle / maxAngle;
            float distMod = distance / maxDistance;
            return 1.0f + distMod * maxDistMod + angleMod * maxAngleMod;
        }
        return 1.0f;
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        if (this.removed()) {
            return;
        }
        GameLight light = level.getLightLevel(this);
        int drawX = camera.getDrawX(this.x) - this.texture.getWidth() / 2;
        int drawY = camera.getDrawY(this.y);
        final TextureDrawOptionsEnd options = this.texture.initDraw().light(light.minLevelCopy(Math.min(light.getLevel() + 100.0f, 150.0f))).rotate(this.getAngle(), this.texture.getWidth() / 2, 0).pos(drawX, drawY - (int)this.getHeight());
        list.add(new EntityDrawable(this){

            @Override
            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
        this.addShadowDrawables(tileList, drawX, drawY, light, this.getAngle(), 0);
    }
}

