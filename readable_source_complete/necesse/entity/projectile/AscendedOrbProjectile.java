/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.projectile;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.Entity;
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

public class AscendedOrbProjectile
extends Projectile {
    private int posIndex;
    private int layerIndex;
    private Mob target;
    private int timeToFreeze;
    private int timeToMove;
    private final float radius = 125.0f;
    private final float extendedRadius = 75.0f;

    public AscendedOrbProjectile() {
    }

    public AscendedOrbProjectile(Level level, Mob circleTarget, int posIndex, int timeBeforeFreeze, int timeBeforeMove, GameDamage damage, Mob owner) {
        this.setLevel(level);
        this.posIndex = posIndex;
        this.target = circleTarget;
        this.layerIndex = posIndex / 8;
        this.timeToFreeze = timeBeforeFreeze;
        this.timeToMove = timeBeforeMove;
        this.refreshFreezePosition();
        this.setDamage(damage);
        this.setOwner(owner);
        this.speed = 0.0f;
        this.setDistance(10000);
        Point2D.Float activatedDir = GameMath.normalize(this.x - this.target.x, this.y - this.target.y);
        this.setDir(activatedDir.x, activatedDir.y);
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextInt(this.posIndex);
        writer.putNextInt(this.layerIndex);
        if (this.target == null) {
            writer.putNextInt(-1);
        } else {
            writer.putNextInt(this.target.getUniqueID());
        }
        writer.putNextInt(this.timeToFreeze);
        writer.putNextInt(this.timeToMove);
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.posIndex = reader.getNextInt();
        this.layerIndex = reader.getNextInt();
        int targetUniqueID = reader.getNextInt();
        this.target = targetUniqueID == -1 ? null : GameUtils.getLevelMob(targetUniqueID, this.getLevel());
        this.timeToFreeze = reader.getNextInt();
        this.timeToMove = reader.getNextInt();
    }

    @Override
    public void init() {
        super.init();
        this.isSolid = false;
        this.knockback = 200;
        this.height = 18.0f;
        this.piercing = 100;
        this.trailOffset = -6.0f;
        this.setWidth(15.0f);
        if (this.isClient()) {
            GameRandom random = GameRandom.globalRandom;
            int particles = 10;
            float anglePerParticle = 360.0f / (float)particles;
            for (int i = 0; i < particles; ++i) {
                int angle = (int)((float)i * anglePerParticle + random.nextFloat() * anglePerParticle);
                float dx = (float)Math.sin(Math.toRadians(angle)) * 20.0f;
                float dy = (float)Math.cos(Math.toRadians(angle)) * 20.0f;
                this.getLevel().entityManager.addParticle(this, Particle.GType.IMPORTANT_COSMETIC).sprite(GameResources.puffParticles.sprite(random.nextInt(5), 0, 12)).sizeFades(12, 24).movesFriction(dx * 2.0f, dy * 2.0f, 0.8f).color(new Color(255, 0, 231)).height(this.getHeight()).lifeTime(1500);
            }
        }
    }

    @Override
    public void clientTick() {
        super.clientTick();
        if (this.target == null) {
            this.remove();
        } else {
            this.tickOrb();
        }
    }

    @Override
    public void serverTick() {
        super.serverTick();
        if (this.target == null) {
            this.remove();
        } else {
            this.tickOrb();
        }
    }

    private void tickOrb() {
        if (this.timeToFreeze > 0) {
            this.timeToFreeze -= 50;
            this.refreshFreezePosition();
            if (this.timeToFreeze <= 0) {
                this.speed = 0.0f;
                this.setTarget(this.target.x, this.target.y);
                this.setDistance((int)GameMath.getExactDistance(this.x, this.y, this.target.x, this.target.y));
            }
        } else if (this.timeToMove > 0) {
            this.timeToMove -= 50;
            if (this.timeToMove <= 0) {
                this.speed = Entity.getTravelSpeedForMillis(1000, 125.0f + 75.0f * (float)this.layerIndex);
            }
        }
    }

    @Override
    public float tickMovement(float delta) {
        if (this.timeToFreeze > 0) {
            this.refreshFreezePosition();
        }
        if (this.speed == 0.0f && this.timeToFreeze < 1000) {
            float width = Math.max(this.getWidth(), 1.0f);
            this.checkHitCollision(new Line2D.Float(this.x, this.y, this.x + this.dx * width, this.y + this.dy * width));
        }
        return super.tickMovement(delta);
    }

    private void refreshFreezePosition() {
        float angle = (float)this.posIndex * 45.0f + (this.layerIndex % 2 == 1 ? 22.5f : 0.0f);
        float radius = this.radius + 75.0f * (float)this.layerIndex;
        float xOffset = (float)(Math.cos(Math.toRadians(angle)) * (double)radius);
        float yOffset = (float)(Math.sin(Math.toRadians(angle)) * (double)radius);
        Point pos = new Point((int)(this.target.x + xOffset), (int)(this.target.y + yOffset));
        this.x = pos.x;
        this.y = pos.y;
    }

    @Override
    public Trail getTrail() {
        return null;
    }

    @Override
    protected void spawnDeathParticles() {
        super.spawnDeathParticles();
        GameRandom random = GameRandom.globalRandom;
        float anglePerParticle = 72.0f;
        for (int i = 0; i < 5; ++i) {
            int angle = (int)((float)i * anglePerParticle + random.nextFloat() * anglePerParticle);
            float dx = (float)(Math.sin(Math.toRadians(angle)) + (double)this.dx) * 20.0f;
            float dy = (float)(Math.cos(Math.toRadians(angle)) + (double)this.dy) * 20.0f;
            this.getLevel().entityManager.addParticle(this, Particle.GType.IMPORTANT_COSMETIC).sprite(GameResources.ascendedParticle.sprite(0, 0, 20)).sizeFades(10, 20).ignoreLight(true).heightMoves(this.getHeight(), this.getHeight() - 30.0f).movesFriction(dx * random.getFloatBetween(1.0f, 3.0f), dy * random.getFloatBetween(1.0f, 3.0f), 0.8f).lifeTime(1500);
        }
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        if (this.removed()) {
            return;
        }
        GameLight light = level.getLightLevel(this);
        float alpha = 1.0f;
        if (this.timeToFreeze > 0) {
            float progressToFreeze = GameMath.limit((float)this.timeToFreeze / 1000.0f, 0.0f, 1.0f);
            alpha = GameMath.lerp(progressToFreeze, 1.0f, 0.35f);
        }
        int centerDistance = this.texture.getHeight() / 2;
        int drawX = camera.getDrawX(this.x) - centerDistance;
        int drawY = camera.getDrawY(this.y) - centerDistance;
        int anim = GameUtils.getAnim(this.getWorldEntity().getTime(), 4, 400);
        final TextureDrawOptionsEnd options = this.texture.initDraw().sprite(anim, 0, 32).light(light.minLevelCopy(150.0f)).alpha(alpha).pos(drawX, drawY - (int)this.getHeight());
        list.add(new EntityDrawable(this){

            @Override
            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
        TextureDrawOptionsEnd shadowOptions = this.shadowTexture.initDraw().sprite(anim, 0, 32).light(light.minLevelCopy(150.0f)).alpha(alpha).pos(drawX, drawY);
        tileList.add(tm -> shadowOptions.draw());
    }
}

