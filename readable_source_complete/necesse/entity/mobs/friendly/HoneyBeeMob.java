/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.friendly;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.HashSet;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.MobRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.LevelMob;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PathDoorOption;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ability.MobAbility;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.HoneyBeeAI;
import necesse.entity.mobs.friendly.FriendlyMob;
import necesse.entity.mobs.friendly.QueenBeeMob;
import necesse.entity.objectEntity.AbstractBeeHiveObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.gameObject.PollinateObjectHandler;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.multiTile.MultiTile;

public class HoneyBeeMob
extends FriendlyMob {
    public static int minStayOutOfApiaryTime = 10000;
    public static int maxStayOutOfApiaryTime = 240000;
    public static int minLostBeeDeathTime = 120000;
    public static int maxLostBeeDeathTime = 600000;
    public static LootTable lootTable = new LootTable(new LootItem("honeybee"));
    public Point apiaryHome = null;
    public long returnToApiaryTime;
    public long pollinateTime;
    public long deathTime;
    public final LevelMob<QueenBeeMob> followingQueen = new LevelMob<QueenBeeMob>(){

        @Override
        public void onMobChanged(QueenBeeMob oldMob, QueenBeeMob newMob) {
            super.onMobChanged(oldMob, newMob);
            if (newMob != null) {
                newMob.honeyBeeUniqueIDs.add(HoneyBeeMob.this.getUniqueID());
            }
        }
    };
    protected long pollinateStartTime;
    protected int pollinateTileX;
    protected int pollinateTileY;
    protected int pollinateAnimationTime;
    protected PollinateObjectHandler pollinateTarget;
    public PollinateMobAbility pollinateAbility;

    public HoneyBeeMob() {
        super(1);
        this.setSpeed(40.0f);
        this.setFriction(2.0f);
        this.setSwimSpeed(1.0f);
        this.collision = new Rectangle(-7, -5, 14, 10);
        this.hitBox = new Rectangle(-12, -14, 24, 24);
        this.selectBox = new Rectangle(-16, -28, 32, 34);
        this.pollinateAbility = this.registerAbility(new PollinateMobAbility());
    }

    @Override
    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        if (this.apiaryHome != null) {
            save.addPoint("apiaryHome", this.apiaryHome);
            save.addLong("returnToApiaryTime", this.returnToApiaryTime);
        }
        if (this.followingQueen != null) {
            save.addInt("followingQueen", this.followingQueen.uniqueID);
        }
        save.addLong("pollinateTime", this.pollinateTime);
        if (this.deathTime != 0L) {
            save.addLong("deathTime", this.deathTime);
        }
    }

    @Override
    public void applyLoadData(LoadData save) {
        super.applyLoadData(save);
        this.apiaryHome = save.getPoint("apiaryHome", this.apiaryHome, false);
        if (this.apiaryHome != null) {
            this.returnToApiaryTime = save.getLong("returnToApiaryTime", this.returnToApiaryTime, false);
        }
        this.followingQueen.uniqueID = save.getInt("followingQueen", -1, false);
        this.pollinateTime = save.getLong("pollinateTime", this.pollinateTime, false);
        this.deathTime = save.getLong("deathTime", this.deathTime, false);
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        if (this.followingQueen.uniqueID != -1) {
            writer.putNextBoolean(true);
            writer.putNextInt(this.followingQueen.uniqueID);
        } else {
            writer.putNextBoolean(false);
        }
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.followingQueen.uniqueID = reader.getNextBoolean() ? reader.getNextInt() : -1;
    }

    @Override
    public void init() {
        super.init();
        this.ai = new BehaviourTreeAI<HoneyBeeMob>(this, new HoneyBeeAI(8000));
    }

    @Override
    public LootTable getLootTable() {
        return lootTable;
    }

    @Override
    public void clientTick() {
        super.clientTick();
        this.tickPollinate();
    }

    @Override
    public void serverTick() {
        super.serverTick();
        QueenBeeMob queenMob = this.followingQueen.get(this.getLevel());
        if (this.apiaryHome == null && queenMob == null) {
            this.followingQueen.uniqueID = -1;
            if (this.deathTime == 0L) {
                this.deathTime = this.getTime() + (long)GameRandom.globalRandom.getIntBetween(minLostBeeDeathTime, maxLostBeeDeathTime);
            }
            if (this.deathTime <= this.getTime()) {
                this.remove();
            }
        }
        this.tickPollinate();
    }

    public void tickPollinate() {
        if (this.pollinateAnimationTime > 0) {
            long timeSinceStart = this.getTime() - this.pollinateStartTime;
            if (timeSinceStart <= (long)this.pollinateAnimationTime) {
                if (!this.isServer() && GameRandom.globalRandom.nextInt(20) == 0) {
                    MultiTile multiTile = this.getLevel().getObject(this.pollinateTileX, this.pollinateTileY).getMultiTile(this.getLevel(), 0, this.pollinateTileX, this.pollinateTileY);
                    Point offset = new Point(multiTile.getCenterXOffset() * 16, multiTile.getCenterYOffset() * 16);
                    int startHeight = 8 + GameRandom.globalRandom.nextInt(24);
                    this.getLevel().entityManager.addParticle(this.pollinateTileX * 32 + offset.x + GameRandom.globalRandom.nextInt(32), this.pollinateTileY * 32 + offset.y + 32, Particle.GType.COSMETIC).color(new Color(255, 211, 58, 200)).heightMoves(startHeight, startHeight + 40).lifeTime(2000);
                }
            } else {
                this.pollinateAnimationTime = 0;
                if (!this.isClient() && this.pollinateTarget != null && this.pollinateTarget.canPollinate()) {
                    this.pollinateTarget.pollinate();
                }
                if (!this.isServer()) {
                    for (int i = 0; i < 5; ++i) {
                        MultiTile multiTile = this.getLevel().getObject(this.pollinateTileX, this.pollinateTileY).getMultiTile(this.getLevel(), 0, this.pollinateTileX, this.pollinateTileY);
                        Point offset = new Point(multiTile.getCenterXOffset() * 16, multiTile.getCenterYOffset() * 16);
                        int startHeight = 8 + GameRandom.globalRandom.nextInt(24);
                        this.getLevel().entityManager.addParticle(this.pollinateTileX * 32 + offset.x + GameRandom.globalRandom.nextInt(32), this.pollinateTileY * 32 + offset.y + 32, Particle.GType.COSMETIC).color(new Color(255, 211, 58, 200)).heightMoves(startHeight, startHeight + 40).lifeTime(2000);
                    }
                }
            }
        }
    }

    public boolean isPollinating() {
        return this.pollinateAnimationTime != 0;
    }

    @Override
    public PathDoorOption getPathDoorOption() {
        if (this.getLevel() != null) {
            return this.getLevel().regionManager.CANNOT_PASS_DOORS_OPTIONS;
        }
        return null;
    }

    @Override
    public int getFlyingHeight() {
        return 20;
    }

    @Override
    public boolean canTakeDamage() {
        return false;
    }

    @Override
    public boolean canBeTargeted(Mob attacker, NetworkClient attackerClient) {
        return true;
    }

    @Override
    protected void checkCollision() {
    }

    @Override
    public boolean canPushMob(Mob other) {
        return false;
    }

    @Override
    public boolean canBePushed(Mob other) {
        return false;
    }

    @Override
    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(HoneyBeeMob.getTileCoordinate(x), HoneyBeeMob.getTileCoordinate(y));
        int drawX = camera.getDrawX(x) - 16;
        int drawY = camera.getDrawY(y) - 22;
        int dir = this.getDir();
        int animationTime = 1000;
        long time = level.getTime();
        Point sprite = this.getAnimSprite(x, y, dir);
        TextureDrawOptionsEnd shadow = MobRegistry.Textures.honeyBee.shadow.initDraw().sprite(0, dir, 32).light(light).pos(drawX, drawY);
        tileList.add(tm -> shadow.draw());
        float bobbingFloat = GameUtils.getBobbing(time += (long)new GameRandom(this.getUniqueID()).nextInt(animationTime), animationTime);
        if (this.isPollinating()) {
            float cosProgress;
            float animProgress;
            long timeSinceStart = this.getTime() - this.pollinateStartTime;
            float pollinateProgress = GameUtils.getAnimFloat(timeSinceStart, this.pollinateAnimationTime);
            if (pollinateProgress < 0.2f) {
                animProgress = pollinateProgress / 0.2f;
                bobbingFloat *= 1.0f - animProgress;
                cosProgress = (float)Math.cos((double)animProgress * Math.PI) / 2.0f + 0.5f;
                drawY = (int)((float)drawY + (1.0f - cosProgress) * 12.0f);
            } else if (pollinateProgress < 0.8f) {
                bobbingFloat = 0.0f;
                drawY += 12;
                sprite.x = 0;
            } else {
                animProgress = (pollinateProgress - 0.8f) / 0.2f;
                bobbingFloat *= animProgress;
                cosProgress = (float)Math.cos((double)animProgress * Math.PI) / 2.0f + 0.5f;
                drawY = (int)((float)drawY + cosProgress * 12.0f);
            }
        }
        drawY -= 6;
        drawY = (int)((float)drawY + bobbingFloat * 5.0f);
        final TextureDrawOptionsEnd options = MobRegistry.Textures.honeyBee.body.initDraw().sprite(sprite.x, sprite.y, 32).light(light).pos(drawX, drawY);
        list.add(new MobDrawable(){

            @Override
            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
    }

    @Override
    public Point getAnimSprite(int x, int y, int dir) {
        long time = this.getTime();
        return new Point(GameUtils.getAnim(time += (long)new GameRandom(this.getUniqueID()).nextInt(200), 2, 200), dir);
    }

    @Override
    protected void onDeath(Attacker attacker, HashSet<Attacker> attackers) {
        ObjectEntity objectEntity;
        super.onDeath(attacker, attackers);
        if (this.apiaryHome != null && (objectEntity = this.getLevel().entityManager.getObjectEntity(this.apiaryHome.x, this.apiaryHome.y)) instanceof AbstractBeeHiveObjectEntity) {
            AbstractBeeHiveObjectEntity apiary = (AbstractBeeHiveObjectEntity)objectEntity;
            apiary.onRoamingBeeDied(this);
        }
    }

    public void setFollowingQueen(QueenBeeMob queen) {
        this.followingQueen.uniqueID = queen != null ? queen.getUniqueID() : -1;
        this.clearApiaryHome();
        this.returnToApiaryTime = 0L;
        this.deathTime = 0L;
    }

    public void setApiaryHome(int tileX, int tileY) {
        this.apiaryHome = new Point(tileX, tileY);
        this.returnToApiaryTime = this.getTime() + (long)GameRandom.globalRandom.getIntBetween(minStayOutOfApiaryTime, maxStayOutOfApiaryTime);
        this.deathTime = 0L;
    }

    public void clearApiaryHome() {
        AbstractBeeHiveObjectEntity hiveEntity;
        if (this.apiaryHome != null && (hiveEntity = this.getLevel().entityManager.getObjectEntity(this.apiaryHome.x, this.apiaryHome.y, AbstractBeeHiveObjectEntity.class)) != null) {
            hiveEntity.onRoamingBeeLost(this);
        }
        this.apiaryHome = null;
    }

    public boolean shouldReturnToApiary() {
        return this.apiaryHome != null && (this.returnToApiaryTime <= this.getTime() || this.getWorldEntity().isNight());
    }

    @Override
    public boolean isHealthBarVisible() {
        return false;
    }

    @Override
    public void playHitDeathSound() {
    }

    @Override
    public void playDeathSound() {
    }

    @Override
    public void playHurtSound() {
    }

    @Override
    public void spawnDamageText(int damage, int size, boolean isCrit) {
    }

    @Override
    protected SoundSettings getAmbientSound() {
        return new SoundSettings(GameRandom.globalRandom.getOneOf(GameResources.beeAmbient1, GameResources.beeAmbient2, GameResources.beeAmbient3, GameResources.beeAmbient4)).volume(0.15f);
    }

    public class PollinateMobAbility
    extends MobAbility {
        public void runAndSend(PollinateObjectHandler target, int animationTime) {
            Packet content = new Packet();
            PacketWriter writer = new PacketWriter(content);
            writer.putNextInt(target.tileX);
            writer.putNextInt(target.tileY);
            writer.putNextShortUnsigned(animationTime);
            HoneyBeeMob.this.pollinateTarget = target;
            this.runAndSendAbility(content);
        }

        @Override
        public void executePacket(PacketReader reader) {
            HoneyBeeMob.this.pollinateStartTime = HoneyBeeMob.this.getTime();
            HoneyBeeMob.this.pollinateTileX = reader.getNextInt();
            HoneyBeeMob.this.pollinateTileY = reader.getNextInt();
            HoneyBeeMob.this.pollinateAnimationTime = reader.getNextShortUnsigned();
        }
    }
}

