/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.friendly;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.FollowPosition;
import necesse.entity.mobs.itemAttacker.FollowerPosition;
import necesse.entity.particle.Particle;
import necesse.entity.trails.Trail;
import necesse.entity.trails.TrailVector;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class LifeEssenceFollowingMob
extends Mob {
    public static FollowPosition LIFE_ESSENCE_CIRCLE = new FollowPosition((m, i, t) -> null, (m, i, t) -> FollowPosition.circlingPos(m, i, t, 40, 60.0f, 10, 40));
    public Trail trail;
    public float moveAngle;
    private float toMove;
    private FollowerPosition currentPos;
    public int index;
    public int totalOther;
    public Mob followingMob;
    public int keepAliveBuffer;

    public LifeEssenceFollowingMob() {
        super(10);
        this.moveAccuracy = 15;
        this.shouldSave = false;
        this.isStatic = true;
        this.setSpeed(140.0f);
        this.setFriction(2.0f);
        this.collision = new Rectangle(-18, -15, 36, 30);
        this.hitBox = new Rectangle(-18, -15, 36, 36);
        this.selectBox = new Rectangle();
    }

    @Override
    public void init() {
        super.init();
        if (this.isClient()) {
            this.trail = new Trail(this, this.getLevel(), new Color(255, 246, 203), 16.0f, 200, 0.0f);
            this.trail.drawOnTop = true;
            this.trail.removeOnFadeOut = false;
            this.getLevel().entityManager.addTrail(this.trail);
        }
        if (this.isServer()) {
            this.remove();
        }
    }

    @Override
    protected void checkCollision() {
    }

    @Override
    public CollisionFilter getLevelCollisionFilter() {
        return null;
    }

    @Override
    public void tickSendSyncPackets() {
        this.nextMovementPacketRegionPositions.clear();
    }

    @Override
    public boolean shouldSendSpawnPacket() {
        return false;
    }

    @Override
    public void requestServerUpdate() {
    }

    @Override
    public boolean canBePushed(Mob other) {
        return false;
    }

    @Override
    public boolean canPushMob(Mob other) {
        return false;
    }

    @Override
    public boolean isHealthBarVisible() {
        return false;
    }

    @Override
    public boolean countDamageDealt() {
        return false;
    }

    @Override
    public void tickMovement(float delta) {
        if (this.followingMob == null) {
            this.remove();
        }
        this.toMove += delta;
        while (this.toMove > 4.0f) {
            float oldX = this.x;
            float oldY = this.y;
            super.tickMovement(4.0f);
            this.toMove -= 4.0f;
            Point2D.Float d = GameMath.normalize(oldX - this.x, oldY - this.y);
            this.moveAngle = (float)Math.toDegrees(Math.atan2(d.y, d.x)) - 90.0f;
            if (this.trail == null) continue;
            float trailOffset = 2.0f;
            this.trail.addPoint(new TrailVector(this.x + d.x * trailOffset, this.y + d.y * trailOffset, -d.x, -d.y, 6.0f, 0.0f));
        }
    }

    @Override
    public void clientTick() {
        super.clientTick();
        --this.keepAliveBuffer;
        if (this.keepAliveBuffer < 0) {
            this.remove();
        }
        if (this.followingMob != null) {
            this.currentPos = LIFE_ESSENCE_CIRCLE.getRelativePos(this.followingMob, this.currentPos, this.index, this.totalOther);
            this.setMovement(this.currentPos.movementGetter.apply(this.followingMob));
        }
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for (int i = 0; i < 10; ++i) {
            this.getLevel().entityManager.addParticle(this.x, this.y, Particle.GType.COSMETIC).movesConstantAngle(GameRandom.globalRandom.nextInt(360), GameRandom.globalRandom.getIntBetween(5, 20)).color(new Color(46, 49, 55));
        }
    }

    @Override
    protected SoundSettings getHitDeathSound() {
        return new SoundSettings(GameResources.fadedeath1).volume(0.5f);
    }

    @Override
    protected void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(LifeEssenceFollowingMob.getTileCoordinate(x), LifeEssenceFollowingMob.getTileCoordinate(y));
        int drawX = camera.getDrawX(x) - 4;
        int drawY = camera.getDrawY(y) - 4;
        int anim = GameUtils.getAnim(this.getTime(), 6, 600);
        TextureDrawOptionsEnd body = MobRegistry.Textures.lifeEssence.initDraw().sprite(anim, 0, 8, 8).light(light).pos(drawX, drawY);
        topList.add(tm -> body.draw());
    }

    @Override
    public void dispose() {
        super.dispose();
        if (this.trail != null) {
            this.trail.removeOnFadeOut = true;
        }
    }
}

