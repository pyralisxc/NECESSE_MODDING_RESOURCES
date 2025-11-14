/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import java.util.stream.Stream;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.gameLoop.tickManager.TicksPerSecond;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.mobs.DeathMessageTable;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.LevelMob;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.hostile.bosses.FlyingBossMob;
import necesse.entity.particle.Particle;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class AscendedBatMob
extends FlyingBossMob {
    public TicksPerSecond particleTicks = TicksPerSecond.ticksPerSecond(8);
    public ParticleTypeSwitcher particleTypes = new ParticleTypeSwitcher(Particle.GType.COSMETIC, Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC);
    public final LevelMob<Mob> master = new LevelMob();
    public static GameDamage COLLISION_DAMAGE = new GameDamage(90.0f);
    public int keepAliveBuffer;
    public float targetX;
    public float targetY;
    public long spawnTime;

    public AscendedBatMob() {
        super(100000);
        this.isSummoned = true;
        this.isStatic = true;
        this.moveAccuracy = 10;
        this.setSpeed(175.0f);
        this.setFriction(1.0f);
        this.setArmor(40);
        this.setKnockbackModifier(0.0f);
        this.shouldSave = false;
        this.collision = new Rectangle(-10, -7, 20, 14);
        this.hitBox = new Rectangle(-14, -12, 28, 24);
        this.selectBox = new Rectangle(-14, -41, 28, 40);
    }

    @Override
    public void init() {
        super.init();
        this.spawnTime = this.getTime();
    }

    @Override
    protected void checkCollision() {
        long timeSinceSpawn = this.getTime() - this.spawnTime;
        if (timeSinceSpawn < 2000L) {
            return;
        }
        super.checkCollision();
    }

    @Override
    public void serverTick() {
        super.serverTick();
        ++this.keepAliveBuffer;
        if (this.keepAliveBuffer >= 20) {
            this.remove();
        }
    }

    @Override
    public void clientTick() {
        super.clientTick();
        this.particleTicks.gameTick();
        while (this.particleTicks.shouldTick()) {
            this.getLevel().entityManager.addParticle(this.x + GameRandom.globalRandom.floatGaussian() * 5.0f, this.y + GameRandom.globalRandom.floatGaussian() * 5.0f, this.particleTypes.next()).movesConstant(this.dx / 2.0f + GameRandom.globalRandom.floatGaussian() * 4.0f, this.dy / 2.0f + GameRandom.globalRandom.floatGaussian() * 4.0f).color(new Color(255, 22, 208)).height(20.0f).lifeTime(500);
        }
    }

    @Override
    public boolean canHitThroughCollision() {
        return true;
    }

    @Override
    public CollisionFilter getLevelCollisionFilter() {
        return null;
    }

    @Override
    public boolean canBePushed(Mob other) {
        return false;
    }

    @Override
    public GameDamage getCollisionDamage(Mob target, boolean fromPacket, ServerClient packetSubmitter) {
        return COLLISION_DAMAGE;
    }

    @Override
    public int getFlyingHeight() {
        return 20;
    }

    @Override
    public void spawnRemoveParticles(float knockbackX, float knockbackY) {
        super.spawnRemoveParticles(knockbackX, knockbackY);
        final int dir = this.getDir();
        this.getLevel().entityManager.addParticle(new Particle(this.getLevel(), this.getX(), this.getY(), 0.0f, 0.0f, 1000L){

            @Override
            public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
                float alpha = GameMath.lerp(this.getLifeCyclePercent(), 0.5f, 0.0f);
                DrawOptions drawOptions = AscendedBatMob.this.getBodyDrawOptions(level, this.getX(), this.getY(), tickManager, camera, dir, alpha);
                topList.add(tm -> drawOptions.draw());
            }
        }, Particle.GType.CRITICAL);
    }

    @Override
    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        long timeSinceSpawn = this.getTime() - this.spawnTime;
        float alpha = timeSinceSpawn < 2000L ? Math.max(0.0f, (float)timeSinceSpawn / 2000.0f) : 1.0f;
        final DrawOptions drawOptions = this.getBodyDrawOptions(level, x, y, tickManager, camera, this.getDir(), alpha);
        list.add(new MobDrawable(){

            @Override
            public void draw(TickManager tickManager) {
                drawOptions.draw();
            }
        });
    }

    public DrawOptions getBodyDrawOptions(Level level, float x, float y, TickManager tickManager, GameCamera camera, int dir, float alpha) {
        GameLight light = level.getLightLevel(AscendedBatMob.getTileCoordinate(x), AscendedBatMob.getTileCoordinate(y));
        int drawX = camera.getDrawX(x) - 32;
        int drawY = camera.getDrawY(y) - 55;
        Point sprite = this.getAnimSprite((int)x, (int)y, dir);
        float bobbing = GameUtils.getBobbing(this.getTime(), 1000) * 5.0f;
        drawY = (int)((float)drawY + bobbing);
        return MobRegistry.Textures.ascendedBat.initDraw().sprite(sprite.x, sprite.y, 64).light(light.minLevelCopy(100.0f)).alpha(alpha).pos(drawX, drawY += level.getTile(AscendedBatMob.getTileCoordinate(x), AscendedBatMob.getTileCoordinate(y)).getMobSinkingAmount(this));
    }

    @Override
    public Point getAnimSprite(int x, int y, int dir) {
        return new Point(GameUtils.getAnim(this.getTime(), 4, 300), dir);
    }

    @Override
    public Stream<ModifierValue<?>> getDefaultModifiers() {
        return Stream.of(new ModifierValue<Float>(BuffModifiers.SLOW, Float.valueOf(0.0f)).max(Float.valueOf(0.2f)));
    }

    @Override
    public Mob getAttackOwner() {
        Mob master = this.master.get(this.getLevel());
        if (master != null) {
            return master;
        }
        return super.getAttackOwner();
    }

    @Override
    public GameMessage getAttackerName() {
        Mob master = this.master.get(this.getLevel());
        if (master != null) {
            return master.getAttackerName();
        }
        return super.getAttackerName();
    }

    @Override
    public DeathMessageTable getDeathMessages() {
        Mob master = this.master.get(this.getLevel());
        if (master != null) {
            return master.getDeathMessages();
        }
        return super.getDeathMessages();
    }
}

