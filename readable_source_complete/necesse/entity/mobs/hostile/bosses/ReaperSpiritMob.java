/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile.bosses;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.MaxHealthGetter;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.PlayerCirclingChaserAI;
import necesse.entity.mobs.hostile.bosses.FlyingBossMob;
import necesse.entity.mobs.hostile.bosses.ReaperMob;
import necesse.entity.particle.Particle;
import necesse.entity.trails.Trail;
import necesse.entity.trails.TrailVector;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.lootTable.LootTable;
import necesse.level.maps.IncursionLevel;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class ReaperSpiritMob
extends FlyingBossMob {
    public static LootTable lootTable = new LootTable();
    public static MaxHealthGetter BASE_MAX_HEALTH = new MaxHealthGetter(25, 40, 50, 60, 85);
    public static MaxHealthGetter INCURSION_MAX_HEALTH = new MaxHealthGetter(50, 75, 90, 105, 130);
    public ReaperMob owner;
    public Trail trail;
    public float moveAngle;
    private float toMove;
    public GameDamage collisionDamage;
    public static GameDamage baseCollisionDamage = new GameDamage(60.0f);
    public static GameDamage incursionCollisionDamage = new GameDamage(90.0f);

    public ReaperSpiritMob() {
        super(100);
        this.difficultyChanges.setMaxHealth(BASE_MAX_HEALTH);
        this.isSummoned = true;
        this.moveAccuracy = 30;
        this.setSpeed(110.0f);
        this.setArmor(20);
        this.setFriction(1.0f);
        this.setKnockbackModifier(0.0f);
        this.collision = new Rectangle(-18, -15, 36, 30);
        this.hitBox = new Rectangle(-18, -15, 36, 36);
        this.selectBox = new Rectangle(-20, -18, 40, 36);
    }

    @Override
    public void init() {
        if (this.getLevel() instanceof IncursionLevel) {
            this.difficultyChanges.setMaxHealth(INCURSION_MAX_HEALTH);
            this.setHealth(this.getMaxHealth());
            this.collisionDamage = incursionCollisionDamage;
        } else {
            this.collisionDamage = baseCollisionDamage;
        }
        super.init();
        this.ai = new BehaviourTreeAI<ReaperSpiritMob>(this, new PlayerCirclingChaserAI(1600, 350, 30));
        if (this.isClient()) {
            this.trail = new Trail(this, this.getLevel(), new Color(14, 131, 121, 150), 16.0f, 500, 0.0f);
            this.trail.drawOnTop = true;
            this.trail.removeOnFadeOut = false;
            this.getLevel().entityManager.addTrail(this.trail);
        }
    }

    @Override
    public boolean canBePushed(Mob other) {
        return false;
    }

    @Override
    public GameDamage getCollisionDamage(Mob target, boolean fromPacket, ServerClient packetSubmitter) {
        return this.collisionDamage;
    }

    @Override
    public int getCollisionKnockback(Mob target) {
        return 50;
    }

    @Override
    public void tickMovement(float delta) {
        this.toMove += delta;
        while (this.toMove > 4.0f) {
            float oldX = this.x;
            float oldY = this.y;
            super.tickMovement(4.0f);
            this.toMove -= 4.0f;
            Point2D.Float d = GameMath.normalize(oldX - this.x, oldY - this.y);
            this.moveAngle = (float)Math.toDegrees(Math.atan2(d.y, d.x)) - 90.0f;
            if (this.trail == null) continue;
            float trailOffset = 5.0f;
            this.trail.addPoint(new TrailVector(this.x + d.x * trailOffset, this.y + d.y * trailOffset, -d.x, -d.y, this.trail.thickness, 0.0f));
        }
    }

    @Override
    public void clientTick() {
        super.clientTick();
        this.getLevel().entityManager.addParticle(this.x + (float)(GameRandom.globalRandom.nextGaussian() * 4.0), this.y + (float)(GameRandom.globalRandom.nextGaussian() * 4.0), Particle.GType.IMPORTANT_COSMETIC).movesConstant(this.dx / 10.0f, this.dy / 10.0f).color(new Color(14, 131, 121, 150));
    }

    @Override
    public LootTable getLootTable() {
        return lootTable;
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for (int i = 0; i < 30; ++i) {
            this.getLevel().entityManager.addParticle(this.x, this.y, Particle.GType.COSMETIC).movesConstantAngle(GameRandom.globalRandom.nextInt(360), GameRandom.globalRandom.getIntBetween(5, 20)).color(new Color(14, 131, 121, 150));
        }
    }

    @Override
    protected SoundSettings getHitDeathSound() {
        return new SoundSettings(GameResources.fadedeath1).volume(0.5f);
    }

    @Override
    protected void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(ReaperSpiritMob.getTileCoordinate(x), ReaperSpiritMob.getTileCoordinate(y));
        int drawX = camera.getDrawX(x) - 16;
        int drawY = camera.getDrawY(y) - 16;
        TextureDrawOptionsEnd body = MobRegistry.Textures.reaperSpirit.initDraw().sprite(0, 0, 32).light(light).rotate(this.moveAngle, 16, 16).pos(drawX, drawY);
        int minLight = 100;
        TextureDrawOptionsEnd eyes = MobRegistry.Textures.reaperSpirit.initDraw().sprite(1, 0, 32).light(light.minLevelCopy(minLight)).rotate(this.moveAngle, 16, 16).pos(drawX, drawY);
        topList.add(tm -> {
            body.draw();
            eyes.draw();
        });
    }

    @Override
    public void dispose() {
        super.dispose();
        if (this.trail != null) {
            this.trail.removeOnFadeOut = true;
        }
    }
}

