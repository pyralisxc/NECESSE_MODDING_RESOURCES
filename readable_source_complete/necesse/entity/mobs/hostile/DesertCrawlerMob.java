/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GroundPillar;
import necesse.engine.util.GroundPillarList;
import necesse.entity.Entity;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.ConfusedCollisionPlayerChaserWandererAI;
import necesse.entity.mobs.hostile.HostileMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.inventory.lootTable.LootTable;
import necesse.level.gameTile.GameTile;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.IncursionLevel;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class DesertCrawlerMob
extends HostileMob {
    public static LootTable lootTable = new LootTable();
    public static GameDamage baseDamage = new GameDamage(90.0f);
    public static GameDamage incursionDamage = new GameDamage(100.0f);
    private final GroundPillarList<Mound> mounds = new GroundPillarList();
    private int moundCounter;
    private double moveBuffer;
    private double moveCounter;

    public DesertCrawlerMob() {
        super(350);
        this.setSpeed(45.0f);
        this.setFriction(4.0f);
        this.setKnockbackModifier(0.4f);
        this.setArmor(20);
        this.collision = new Rectangle(-10, -7, 20, 14);
        this.hitBox = new Rectangle(-16, -16, 32, 32);
        this.selectBox = new Rectangle(-23, -23, 46, 46);
    }

    @Override
    public void init() {
        GameDamage damage;
        super.init();
        if (this.getLevel() instanceof IncursionLevel) {
            this.setMaxHealth(450);
            this.setHealthHidden(this.getMaxHealth());
            this.setArmor(30);
            damage = incursionDamage;
        } else {
            damage = baseDamage;
        }
        this.ai = new BehaviourTreeAI<DesertCrawlerMob>(this, new ConfusedCollisionPlayerChaserWandererAI(null, 512, damage, 100, 40000));
        this.addNewMound(new Point2D.Float(0.0f, 1.0f), true);
        this.addNewMound(new Point2D.Float(1.0f, 0.0f), true);
        this.addNewMound(new Point2D.Float(1.0f, 0.0f), true);
    }

    @Override
    public LootTable getLootTable() {
        return lootTable;
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

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void addNewMound(Point2D.Float dir, boolean isFirst) {
        Point2D.Float perpDir = GameMath.getPerpendicularDir(dir.x, dir.y);
        int offset = this.moundCounter % 2 == 0 ? 5 : -5;
        GroundPillarList<Mound> groundPillarList = this.mounds;
        synchronized (groundPillarList) {
            this.mounds.add(new Mound(this.getX() + (int)(perpDir.x * (float)offset + dir.x * 10.0f) + GameRandom.globalRandom.getIntBetween(-2, 2) + (isFirst ? offset : 0), this.getY() + (int)(perpDir.y * (float)offset * 0.7f + dir.y * 10.0f) + GameRandom.globalRandom.getIntBetween(-2, 2), this.moveCounter - (double)(isFirst ? 20 : 0), this.getWorldEntity().getLocalTime()));
        }
        ++this.moundCounter;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void clientTick() {
        super.clientTick();
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
        GroundPillarList<Mound> groundPillarList = this.mounds;
        synchronized (groundPillarList) {
            this.mounds.clean(this.getWorldEntity().getLocalTime(), this.moveCounter);
        }
    }

    @Override
    public CollisionFilter getLevelCollisionFilter() {
        return super.getLevelCollisionFilter().allLiquidTiles();
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for (int i = 0; i < 4; ++i) {
            this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), MobRegistry.Textures.desertCrawler, i, 0, 32, this.x, this.y, 20.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void addExtraDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addExtraDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        long currentTime = this.getWorldEntity().getLocalTime();
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
    }

    @Override
    public int getRockSpeed() {
        return 5;
    }

    private static class Mound
    extends GroundPillar {
        public int res = 32;
        public GameTextureSection texture = null;

        public Mound(int x, int y, double spawnDistance, long spawnTime) {
            super(x, y, spawnDistance, spawnTime);
            GameTexture sprites = MobRegistry.Textures.mounds32;
            if (sprites != null) {
                int sprite = GameRandom.globalRandom.nextInt(sprites.getWidth() / this.res);
                this.texture = new GameTextureSection(sprites).sprite(sprite, 0, this.res, sprites.getHeight());
            }
            this.behaviour = new GroundPillar.DistanceTimedBehaviour(1000, 0, 500, 50.0, 20.0, 100.0);
        }

        @Override
        public DrawOptions getDrawOptions(Level level, long currentTime, double distanceMoved, GameCamera camera) {
            GameTile tile = level.getTile(Entity.getTileCoordinate(this.x), Entity.getTileCoordinate(this.y));
            if (tile.isLiquid) {
                return null;
            }
            Color color = tile.getMapColor(level, Entity.getTileCoordinate(this.x), Entity.getTileCoordinate(this.y));
            GameLight light = level.getLightLevel(Entity.getTileCoordinate(this.x), Entity.getTileCoordinate(this.y));
            int drawX = camera.getDrawX(this.x);
            int drawY = camera.getDrawY(this.y);
            double height = this.getHeight(currentTime, distanceMoved);
            int endY = (int)(height * (double)this.texture.getHeight());
            return this.texture.section(0, this.texture.getWidth(), 0, endY).initDraw().color(color).light(light).pos(drawX - this.texture.getWidth() / 2, drawY - endY);
        }
    }
}

