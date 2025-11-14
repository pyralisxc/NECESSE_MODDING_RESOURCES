/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GroundPillar;
import necesse.engine.util.GroundPillarList;
import necesse.entity.Entity;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.ConfusedPlayerChaserWandererAI;
import necesse.entity.mobs.hostile.HostileMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.DrawOptionsList;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.lootTable.LootTable;
import necesse.level.gameTile.GameTile;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class CaveMoleMob
extends HostileMob {
    public static LootTable lootTable = new LootTable();
    public static GameDamage damage = new GameDamage(15.0f);
    private final GroundPillarList<Mound> mounds = new GroundPillarList();
    private int moundCounter;
    private final int stateChangeAnimationTime = 200;
    private double moveBuffer;
    private double moveCounter;
    private long stateChangeTime;
    private boolean isUp;
    private boolean nextIsUp;
    private boolean wantIsUp;
    private int wantIsUpCounter;

    public CaveMoleMob() {
        super(90);
        this.setSpeed(40.0f);
        this.setFriction(3.0f);
        this.setKnockbackModifier(0.0f);
        this.attackCooldown = 1500;
        this.collision = new Rectangle(-10, -7, 20, 14);
        this.hitBox = new Rectangle(-14, -12, 28, 24);
        this.selectBox = new Rectangle(-14, -31, 28, 38);
        this.isUp = true;
        this.nextIsUp = true;
    }

    @Override
    public void setupMovementPacket(PacketWriter writer) {
        super.setupMovementPacket(writer);
        writer.putNextBoolean(this.isUp);
        writer.putNextBoolean(this.nextIsUp);
        writer.putNextBoolean(this.wantIsUp);
        long stateChangeTimeDelta = this.getWorldEntity().getLocalTime() - this.stateChangeTime;
        writer.putNextLong(stateChangeTimeDelta);
        writer.putNextByteUnsigned(this.wantIsUpCounter);
    }

    @Override
    public void applyMovementPacket(PacketReader reader, boolean isDirect) {
        super.applyMovementPacket(reader, isDirect);
        this.isUp = reader.getNextBoolean();
        this.nextIsUp = reader.getNextBoolean();
        this.wantIsUp = reader.getNextBoolean();
        long stateChangeTimeDelta = reader.getNextLong();
        this.stateChangeTime = this.getWorldEntity().getLocalTime() - stateChangeTimeDelta;
        this.wantIsUpCounter = reader.getNextByteUnsigned();
    }

    @Override
    public void init() {
        super.init();
        this.ai = new BehaviourTreeAI<CaveMoleMob>(this, new ConfusedPlayerChaserWandererAI<CaveMoleMob>(null, 512, 256, 40000, true, false){

            @Override
            public boolean attackTarget(CaveMoleMob mob, Mob target) {
                boolean success = this.shootSimpleProjectile(mob, target, "stone", damage, 80, 384);
                if (success) {
                    this.wanderAfterAttack = GameRandom.globalRandom.getChance(0.75f);
                }
                return success;
            }
        });
    }

    @Override
    public boolean canAttack() {
        return super.canAttack() && this.isUp && this.nextIsUp;
    }

    @Override
    public void tickMovement(float delta) {
        while (delta > 0.0f) {
            int rockSpeed = this.getRockSpeed();
            Point2D.Float lastPos = new Point2D.Float(this.x, this.y);
            super.tickMovement(Math.min((float)(rockSpeed * 2), delta));
            delta -= (float)(rockSpeed * 2);
            double dist = lastPos.distance(this.x, this.y);
            this.moveBuffer += dist;
            this.moveCounter += dist;
            while (this.moveBuffer > (double)rockSpeed) {
                this.addNewMound(GameMath.normalize(this.x - lastPos.x, this.y - lastPos.y), false);
                this.moveBuffer -= (double)rockSpeed;
            }
        }
    }

    @Override
    protected void calcAcceleration(float speed, float friction, float moveX, float moveY, float delta) {
        super.calcAcceleration(speed, friction, moveX, moveY, delta);
        if (this.isUp || this.nextIsUp) {
            this.dx = 0.0f;
            this.dy = 0.0f;
        }
    }

    @Override
    public void setFacingDir(float deltaX, float deltaY) {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void clientTick() {
        super.clientTick();
        this.tickStateChange();
        GroundPillarList<Mound> groundPillarList = this.mounds;
        synchronized (groundPillarList) {
            this.mounds.clean(this.getWorldEntity().getLocalTime(), this.moveCounter);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void serverTick() {
        super.serverTick();
        this.tickStateChange();
        GroundPillarList<Mound> groundPillarList = this.mounds;
        synchronized (groundPillarList) {
            this.mounds.clean(this.getWorldEntity().getLocalTime(), this.moveCounter);
        }
    }

    @Override
    public CollisionFilter getLevelCollisionFilter() {
        return super.getLevelCollisionFilter().allLiquidTiles();
    }

    private void tickStateChange() {
        if (this.nextIsUp != this.isUp) {
            long currentTime = this.getWorldEntity().getLocalTime();
            if (currentTime > this.stateChangeTime + 200L) {
                this.isUp = this.nextIsUp;
            }
        } else if (this.isAccelerating() && this.isUp && this.canAttack()) {
            this.changeIsUp(false);
        } else if (!this.isAccelerating() && !this.isUp) {
            this.changeIsUp(true);
        }
    }

    private float getStateChangeProgress() {
        long currentTime;
        if (this.nextIsUp != this.isUp && (currentTime = this.getWorldEntity().getLocalTime()) <= this.stateChangeTime + 200L) {
            return GameMath.limit((float)(currentTime - this.stateChangeTime) / 200.0f, 0.0f, 1.0f);
        }
        return 1.0f;
    }

    private void changeIsUp(boolean isUp) {
        if (this.nextIsUp == isUp) {
            return;
        }
        if (this.wantIsUp != isUp) {
            this.wantIsUp = isUp;
            this.wantIsUpCounter = 0;
            return;
        }
        ++this.wantIsUpCounter;
        if (this.wantIsUpCounter < 4) {
            return;
        }
        this.stateChangeTime = this.getWorldEntity().getLocalTime();
        this.nextIsUp = isUp;
        this.wantIsUpCounter = 0;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void addNewMound(Point2D.Float dir, boolean isFirst) {
        Point2D.Float perpDir = GameMath.getPerpendicularDir(dir.x, dir.y);
        int offset = this.moundCounter % 2 == 0 ? 5 : -5;
        Mound mound = new Mound(this.getX() + (int)(perpDir.x * (float)offset + dir.x * 10.0f) + GameRandom.globalRandom.getIntBetween(-2, 2) + (isFirst ? offset : 0), this.getY() + (int)(perpDir.y * (float)offset * 0.7f + dir.y * 10.0f) + GameRandom.globalRandom.getIntBetween(-2, 2), this.moveCounter - (double)(isFirst ? 20 : 0), this.getLocalTime());
        GroundPillarList<Mound> groundPillarList = this.mounds;
        synchronized (groundPillarList) {
            this.mounds.add(mound);
        }
        ++this.moundCounter;
    }

    @Override
    public LootTable getLootTable() {
        return lootTable;
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for (int i = 0; i < 4; ++i) {
            this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), MobRegistry.Textures.mole, 2, i, 16, this.x, this.y, 20.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void addExtraDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addExtraDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        long currentTime = this.getLocalTime();
        GroundPillarList<Mound> groundPillarList = this.mounds;
        synchronized (groundPillarList) {
            this.mounds.clean(currentTime, this.moveCounter);
            for (final Mound mound : this.mounds) {
                final DrawOptions moundDraw = mound.getDrawOptions(level, currentTime, this.moveCounter, camera);
                if (moundDraw == null) continue;
                list.add(new LevelSortedDrawable(this){

                    @Override
                    public int getSortY() {
                        return mound.y;
                    }

                    @Override
                    public void draw(TickManager tickManager) {
                        moundDraw.draw();
                    }
                });
            }
        }
    }

    @Override
    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(CaveMoleMob.getTileCoordinate(x), CaveMoleMob.getTileCoordinate(y));
        int drawX = camera.getDrawX(x);
        int drawY = camera.getDrawY(y);
        int dir = this.getDir();
        int spriteY = 0;
        if (dir == 3 || dir == 2) {
            spriteY = 1;
        }
        float changeProgress = this.getStateChangeProgress();
        if (!this.nextIsUp) {
            changeProgress = Math.abs(changeProgress - 1.0f);
        }
        int changeProgressY = (int)(changeProgress * 32.0f);
        final DrawOptionsList draws = new DrawOptionsList();
        draws.add(MobRegistry.Textures.mole.initDraw().spriteSection(0, spriteY, 32, 0, 32, 0, changeProgressY).light(light).pos(drawX - 16, drawY + 32 - changeProgressY - 30));
        GameTile tile = level.getTile(CaveMoleMob.getTileCoordinate(x), CaveMoleMob.getTileCoordinate(y));
        if (!tile.isLiquid) {
            Color moundColor = tile.getMapColor(level, CaveMoleMob.getTileCoordinate(x), CaveMoleMob.getTileCoordinate(y));
            int moundWidth = MobRegistry.Textures.mound1.getWidth();
            int moundHeight = MobRegistry.Textures.mound1.getHeight();
            int moundProgressY = (int)(changeProgress * (float)moundHeight);
            draws.add(MobRegistry.Textures.mound1.initDraw().section(0, moundWidth, 0, moundProgressY).color(moundColor).light(light).pos(drawX - moundWidth / 2 - 5, drawY + moundHeight / 2 - moundProgressY));
            draws.add(MobRegistry.Textures.mound1.initDraw().section(0, moundWidth, 0, moundProgressY).color(moundColor).light(light).pos(drawX - moundWidth / 2 + 5, drawY + moundHeight / 2 - moundProgressY));
        }
        float attackProgress = this.getAttackAnimProgress();
        final DrawOptions arms = this.isAttacking ? ItemAttackDrawOptions.start(dir).armSprite(MobRegistry.Textures.mole, 0, 2, 32).setOffsets(dir == 3 ? 34 : 28, 18, 10, 15, 12, 4, 12).swingRotation(attackProgress).light(light).pos(drawX - 31, drawY - 45) : null;
        list.add(new MobDrawable(){

            @Override
            public void draw(TickManager tickManager) {
                draws.draw();
                if (arms != null) {
                    arms.draw();
                }
            }
        });
    }

    @Override
    public int getRockSpeed() {
        return 5;
    }

    @Override
    protected void addDebugTooltips(ListGameTooltips tooltips) {
        super.addDebugTooltips(tooltips);
        tooltips.add("isUp: " + this.isUp);
        tooltips.add("nextIsUp: " + this.nextIsUp);
        tooltips.add("wantIsUp: " + this.wantIsUp);
        tooltips.add("stateChange: " + this.getStateChangeProgress());
    }

    private static class Mound
    extends GroundPillar {
        public GameTexture texture = GameRandom.globalRandom.getOneOf(MobRegistry.Textures.mound1, MobRegistry.Textures.mound2, MobRegistry.Textures.mound3);

        public Mound(int x, int y, double spawnDistance, long spawnTime) {
            super(x, y, spawnDistance, spawnTime);
        }

        @Override
        public DrawOptions getDrawOptions(Level level, long currentTime, double distanceMoved, GameCamera camera) {
            GameTile tile = level.getTile(Entity.getTileCoordinate(this.x), Entity.getTileCoordinate(this.y));
            if (tile.isLiquid) {
                return null;
            }
            GameLight light = level.getLightLevel(Entity.getTileCoordinate(this.x), Entity.getTileCoordinate(this.y));
            Color color = tile.getMapColor(level, Entity.getTileCoordinate(this.x), Entity.getTileCoordinate(this.y));
            int drawX = camera.getDrawX(this.x);
            int drawY = camera.getDrawY(this.y);
            double height = this.getHeight(currentTime, distanceMoved);
            int endY = (int)(height * (double)this.texture.getHeight());
            return this.texture.initDraw().section(0, this.texture.getWidth(), 0, endY).color(color).light(light).pos(drawX - this.texture.getWidth() / 2, drawY - endY);
        }
    }
}

