/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.projectile;

import java.awt.Color;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.DeathMessageTable;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.hostile.bosses.VoidWizard;
import necesse.entity.projectile.Projectile;
import necesse.entity.trails.Trail;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class VoidWizardCloneProjectile
extends Projectile {
    protected boolean moving;

    public VoidWizardCloneProjectile() {
    }

    public VoidWizardCloneProjectile(Level level, float x, float y, Mob owner, GameDamage damage) {
        this.setLevel(level);
        this.x = x;
        this.y = y;
        this.setDir((float)(GameRandom.globalRandom.nextGaussian() * 10.0), (float)(GameRandom.globalRandom.nextGaussian() * 10.0));
        this.speed = 0.0f;
        this.setDamage(damage);
        this.knockback = 0;
        this.setDistance(100000);
        this.setOwner(owner);
        this.setAngle(this.getAngle() + (GameRandom.globalRandom.nextFloat() - 0.5f) * 40.0f);
        this.spawnTime = this.getWorldEntity().getTime();
        this.moving = false;
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextBoolean(this.moving);
        writer.putNextLong(this.spawnTime);
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.moving = reader.getNextBoolean();
        this.spawnTime = reader.getNextLong();
    }

    @Override
    public void init() {
        super.init();
        this.height = 18.0f;
        this.setWidth(10.0f);
        this.bouncing = 1000;
        this.piercing = 1;
        this.givesLight = true;
        this.trailOffset = 0.0f;
        this.speed = this.moving ? 100.0f : 0.0f;
    }

    @Override
    protected CollisionFilter getLevelCollisionFilter() {
        return super.getLevelCollisionFilter().addFilter(tp -> tp.object().object.isWall || tp.object().object.isRock);
    }

    @Override
    public Color getParticleColor() {
        return VoidWizard.getWizardProjectileColor(this.getOwner());
    }

    @Override
    public Trail getTrail() {
        return new Trail(this, this.getLevel(), null, 16.0f, 300, this.getHeight()){

            @Override
            public Color getColor() {
                return VoidWizard.getWizardProjectileColor(VoidWizardCloneProjectile.this.getOwner());
            }
        };
    }

    @Override
    public void clientTick() {
        long timeSinceSpawned;
        super.clientTick();
        if (!this.moving && (timeSinceSpawned = this.getWorldEntity().getTime() - this.spawnTime) >= 2000L) {
            this.speed = 100.0f;
            this.moving = true;
        }
    }

    @Override
    public void serverTick() {
        Mob owner;
        long timeSinceSpawned;
        super.serverTick();
        if (!this.moving && (timeSinceSpawned = this.getWorldEntity().getTime() - this.spawnTime) >= 2000L) {
            this.speed = 100.0f;
            this.moving = true;
        }
        if ((owner = this.getOwner()) != null && owner.removed()) {
            this.remove();
        }
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        int dim;
        if (this.removed()) {
            return;
        }
        GameLight light = level.getLightLevel(this);
        float timeSinceSpawned = this.getWorldEntity().getTime() - this.spawnTime;
        float rotate = timeSinceSpawned / 10.0f;
        float height = this.getHeight();
        if (!this.moving) {
            double movingProgress = GameMath.limit((double)timeSinceSpawned / 2000.0, 0.0, 1.0);
            dim = 16 + (int)(movingProgress * 16.0);
            height = (float)((double)height * movingProgress);
        } else {
            float pulse = timeSinceSpawned / 5.0f % 2.0f;
            if (pulse > 1.0f) {
                pulse = Math.abs(pulse - 2.0f);
            }
            dim = (int)(32.0f * (pulse / 2.0f + 1.0f));
        }
        int drawX = camera.getDrawX(this.x) - dim / 2;
        int drawY = camera.getDrawY(this.y) - dim / 2;
        final TextureDrawOptionsEnd options = this.texture.initDraw().light(light.minLevelCopy(Math.min(light.getLevel() + 100.0f, 150.0f))).size(dim, dim).rotate(rotate, dim / 2, dim / 2).pos(drawX, drawY - (int)height);
        list.add(new EntityDrawable(this){

            @Override
            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
        this.addShadowDrawables(tileList, drawX, drawY, light, rotate, dim / 2);
    }

    @Override
    public DeathMessageTable getDeathMessages() {
        return this.getDeathMessages("voidwiz", 4);
    }
}

