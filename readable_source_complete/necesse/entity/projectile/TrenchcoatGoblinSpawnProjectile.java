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
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.event.ConfuseWanderAIEvent;
import necesse.entity.mobs.hostile.TrenchcoatGoblinScatteredMob;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.Projectile;
import necesse.entity.trails.Trail;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;
import necesse.level.maps.light.GameLight;

public class TrenchcoatGoblinSpawnProjectile
extends Projectile {
    private int startHeight;
    private float maxTileHeight;
    private TrenchcoatGoblinScatteredMob.TrenchCoatGoblinType goblinType;

    public TrenchcoatGoblinSpawnProjectile() {
    }

    public TrenchcoatGoblinSpawnProjectile(Level level, TrenchcoatGoblinScatteredMob.TrenchCoatGoblinType goblinType, Mob owner, float x, float y, float targetX, float targetY, int distance, GameDamage damage, int knockback) {
        this.setLevel(level);
        this.x = x;
        this.y = y;
        this.goblinType = goblinType;
        this.speed = 0.5f * (float)distance * goblinType.speedMultiplier;
        this.setTarget(targetX, targetY);
        this.setDamage(damage);
        this.knockback = knockback;
        this.setDistance(distance);
        this.setOwner(owner);
        this.startHeight = goblinType.startHeight;
        this.maxTileHeight = goblinType.maxTileHeight;
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextEnum(this.goblinType);
        writer.putNextInt(this.startHeight);
        writer.putNextFloat(this.maxTileHeight);
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.goblinType = reader.getNextEnum(TrenchcoatGoblinScatteredMob.TrenchCoatGoblinType.class);
        this.startHeight = reader.getNextInt();
        this.maxTileHeight = reader.getNextFloat();
    }

    @Override
    public void init() {
        super.init();
        this.spawnTime = this.getWorldEntity().getTime();
        this.isSolid = false;
        this.canHitMobs = false;
    }

    @Override
    public float tickMovement(float delta) {
        float out = super.tickMovement(delta);
        float travelPerc = GameMath.limit(this.traveledDistance / (float)this.distance, 0.0f, 1.0f);
        float bounceHeight = GameMath.sin(travelPerc * 180.0f);
        float groundHeight = GameMath.lerp(travelPerc, this.startHeight, 0);
        this.height = 24.0f + groundHeight + bounceHeight * 16.0f * this.maxTileHeight;
        return out;
    }

    @Override
    public Color getParticleColor() {
        return new Color(31, 153, 54);
    }

    @Override
    public Trail getTrail() {
        return null;
    }

    @Override
    public void doHitLogic(Mob mob, LevelObjectHit object, float x, float y) {
        super.doHitLogic(mob, object, x, y);
        if (!this.isServer()) {
            return;
        }
        Mob owner = this.getOwner();
        if (owner != null) {
            Mob tcGoblin = this.goblinType.goblinMob.get();
            tcGoblin.isSummoned = true;
            if (!tcGoblin.collidesWith(this.getLevel(), (int)x, (int)y)) {
                this.getLevel().entityManager.addMob(tcGoblin, (int)x, (int)y);
                int confuseTime = GameRandom.globalRandom.getIntBetween(2500, 5000);
                tcGoblin.ai.blackboard.submitEvent("confuseWander", new ConfuseWanderAIEvent(confuseTime));
            }
        }
    }

    @Override
    protected void spawnDeathParticles() {
        Color particleColor = this.getParticleColor();
        if (particleColor != null) {
            for (int i = 0; i < 10; ++i) {
                int angle = GameRandom.globalRandom.nextInt(360);
                Point2D.Float dir = GameMath.getAngleDir(angle);
                this.getLevel().entityManager.addParticle(this.x, this.y, Particle.GType.CRITICAL).movesConstant((float)GameRandom.globalRandom.getIntBetween(20, 50) * dir.x, (float)GameRandom.globalRandom.getIntBetween(20, 50) * dir.y).color(this.getParticleColor()).height(this.getHeight());
            }
        }
        Float pitch = GameRandom.globalRandom.getOneOf(Float.valueOf(0.9f), Float.valueOf(0.95f), Float.valueOf(1.0f));
        SoundManager.playSound(GameResources.crackdeath, (SoundEffect)SoundEffect.effect(this.x, this.y).volume(0.7f).pitch(pitch.floatValue()));
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        if (this.removed()) {
            return;
        }
        GameLight light = level.getLightLevel(this.getTileX(), this.getTileY());
        int drawX = camera.getDrawX(this.x) - 32;
        int drawY = camera.getDrawY(this.y) - 32;
        float angle = (float)(this.getWorldEntity().getTime() - this.spawnTime) * 0.15f;
        if (this.dx < 0.0f) {
            angle = -angle;
        }
        TextureDrawOptionsEnd options = this.goblinType.texture.get().initDraw().sprite(0, 2, 64).light(light).rotate(angle, 32, 48).pos(drawX, drawY - (int)this.getHeight());
        float shadowAlpha = Math.abs(GameMath.limit(this.height / 300.0f, 0.0f, 1.0f) - 1.0f);
        int shadowX = camera.getDrawX(this.x) - this.shadowTexture.getWidth() / 2;
        int shadowY = camera.getDrawY(this.y) - this.shadowTexture.getHeight() / 2;
        TextureDrawOptionsEnd shadowOptions = this.shadowTexture.initDraw().light(light).rotate(angle).alpha(shadowAlpha).pos(shadowX, shadowY);
        topList.add(tm -> {
            shadowOptions.draw();
            options.draw();
        });
    }
}

