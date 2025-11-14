/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.projectile;

import java.awt.Color;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.util.GameRandom;
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

public class SpiritSkullProjectile
extends Projectile {
    public static int HEIGHT_BOUNCE = 4;
    public static int DISTANCE_PER_BOUNCE = 100;
    protected GameRandom heightRandom = new GameRandom();
    boolean verticalMirror;

    public SpiritSkullProjectile() {
    }

    public SpiritSkullProjectile(Level level, Mob owner, float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback) {
        this.setLevel(level);
        this.setOwner(owner);
        this.x = x;
        this.y = y;
        this.setTarget(targetX, targetY);
        this.speed = speed;
        this.distance = distance;
        this.setDamage(damage);
        this.knockback = knockback;
    }

    @Override
    public void init() {
        super.init();
        this.height = 18.0f;
        this.piercing = 999;
        this.givesLight = true;
        this.trailOffset = 0.0f;
        this.setWidth(26.0f);
        if (this.isServer() && this.targetX < this.x) {
            this.verticalMirror = true;
        }
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextBoolean(this.verticalMirror);
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.verticalMirror = reader.getNextBoolean();
    }

    @Override
    public float tickMovement(float delta) {
        return super.tickMovement(delta);
    }

    @Override
    public float getHeight() {
        float traveledDistance = this.traveledDistance + this.heightRandom.seeded(this.getUniqueID()).nextFloat() * (float)DISTANCE_PER_BOUNCE;
        float progress = traveledDistance % (float)DISTANCE_PER_BOUNCE / (float)DISTANCE_PER_BOUNCE;
        return this.height + (float)Math.sin((double)progress * Math.PI * 2.0) * (float)HEIGHT_BOUNCE;
    }

    @Override
    public Color getParticleColor() {
        return new Color(0, 191, 163);
    }

    @Override
    protected void modifySpinningParticle(ParticleOption particle) {
        particle.lifeTime(1000);
        particle.fadesAlpha(0.25f, 0.1f);
    }

    @Override
    public Trail getTrail() {
        return new Trail(this, this.getLevel(), new Color(0, 138, 117), 12.0f, 300, 18.0f);
    }

    @Override
    protected void spawnDeathParticles() {
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        if (this.removed()) {
            return;
        }
        GameLight light = level.getLightLevel(this);
        float alpha = 1.0f;
        if (this.traveledDistance > (float)this.distance * 0.9f) {
            alpha = ((float)this.distance - this.traveledDistance) / ((float)this.distance * 0.1f);
        }
        int drawX = camera.getDrawX(this.x) - this.texture.getWidth() / 2;
        int drawY = camera.getDrawY(this.y) - this.texture.getHeight() / 2;
        final TextureDrawOptionsEnd options = this.texture.initDraw().light(light).rotate(this.getAngle() - 90.0f, this.texture.getWidth() / 2, this.texture.getHeight() / 2).mirror(false, this.verticalMirror).alpha(alpha).pos(drawX, drawY - (int)this.getHeight());
        topList.add(new EntityDrawable(this){

            @Override
            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
    }
}

