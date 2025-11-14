/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.friendly;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.HashSet;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.MobRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PathDoorOption;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.QueenBeeAI;
import necesse.entity.mobs.friendly.FriendlyMob;
import necesse.entity.mobs.friendly.HoneyBeeMob;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class QueenBeeMob
extends FriendlyMob {
    public static int minMigrationTime = 120000;
    public static int maxMigrationTime = 240000;
    public static LootTable lootTable = new LootTable(new LootItem("queenbee"));
    public Point migrationApiary = null;
    public long migrateTime;
    public HashSet<Integer> honeyBeeUniqueIDs = new HashSet();
    public long deathTime;

    public QueenBeeMob() {
        super(1);
        this.setSpeed(10.0f);
        this.setFriction(2.0f);
        this.setSwimSpeed(1.0f);
        this.collision = new Rectangle(-7, -5, 14, 10);
        this.hitBox = new Rectangle(-12, -14, 24, 24);
        this.selectBox = new Rectangle(-16, -28, 32, 34);
    }

    @Override
    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        if (this.migrationApiary != null) {
            save.addPoint("migrationApiary", this.migrationApiary);
            save.addLong("migrateTime", this.migrateTime);
        }
        save.addIntArray("honeyBeeUniqueIDs", this.honeyBeeUniqueIDs.stream().mapToInt(i -> i).toArray());
        if (this.deathTime != 0L) {
            save.addLong("deathTime", this.deathTime);
        }
    }

    @Override
    public void applyLoadData(LoadData save) {
        super.applyLoadData(save);
        this.migrationApiary = save.getPoint("migrationApiary", this.migrationApiary, false);
        if (this.migrationApiary != null) {
            this.migrateTime = save.getLong("migrateTime", this.migrateTime, false);
        }
        int[] loadedHoneyBeeUniqueIDs = save.getIntArray("honeyBeeUniqueIDs", new int[0]);
        this.honeyBeeUniqueIDs.clear();
        for (int beeUniqueID : loadedHoneyBeeUniqueIDs) {
            this.honeyBeeUniqueIDs.add(beeUniqueID);
        }
        this.deathTime = save.getLong("deathTime", this.deathTime, false);
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        if (this.migrationApiary != null) {
            writer.putNextBoolean(true);
            writer.putNextInt(this.migrationApiary.x);
            writer.putNextInt(this.migrationApiary.y);
            writer.putNextLong(this.migrateTime);
        } else {
            writer.putNextBoolean(false);
        }
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        if (reader.getNextBoolean()) {
            int apiaryX = reader.getNextInt();
            int apiaryY = reader.getNextInt();
            this.migrationApiary = new Point(apiaryX, apiaryY);
            this.migrateTime = reader.getNextLong();
        } else {
            this.migrationApiary = null;
        }
    }

    @Override
    public void init() {
        super.init();
        this.ai = new BehaviourTreeAI<QueenBeeMob>(this, new QueenBeeAI(8000));
    }

    @Override
    public LootTable getLootTable() {
        return lootTable;
    }

    @Override
    public void serverTick() {
        super.serverTick();
        if (this.migrationApiary == null) {
            if (this.deathTime == 0L) {
                this.deathTime = this.getTime() + (long)GameRandom.globalRandom.getIntBetween(HoneyBeeMob.minLostBeeDeathTime, HoneyBeeMob.maxLostBeeDeathTime);
            }
            if (this.deathTime <= this.getTime()) {
                this.remove();
            }
        }
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
        GameLight light = level.getLightLevel(QueenBeeMob.getTileCoordinate(x), QueenBeeMob.getTileCoordinate(y));
        int drawX = camera.getDrawX(x) - 16;
        int drawY = camera.getDrawY(y) - 22;
        int dir = this.getDir();
        int animationTime = 1000;
        long time = level.getTime();
        Point sprite = this.getAnimSprite(x, y, dir);
        TextureDrawOptionsEnd shadow = MobRegistry.Textures.queenBee.shadow.initDraw().sprite(0, dir, 32).light(light).pos(drawX, drawY);
        tileList.add(tm -> shadow.draw());
        float bobbingFloat = GameUtils.getBobbing(time += (long)new GameRandom(this.getUniqueID()).nextInt(animationTime), animationTime);
        drawY -= 10;
        drawY = (int)((float)drawY + bobbingFloat * 5.0f);
        final TextureDrawOptionsEnd options = MobRegistry.Textures.queenBee.body.initDraw().sprite(sprite.x, sprite.y, 32).light(light).pos(drawX, drawY);
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

    public void setMigrationApiary(int tileX, int tileY) {
        this.migrationApiary = new Point(tileX, tileY);
        this.migrateTime = this.getTime() + (long)GameRandom.globalRandom.getIntBetween(minMigrationTime, maxMigrationTime);
        this.deathTime = 0L;
    }

    public boolean shouldMigrate() {
        return this.migrationApiary != null && this.migrateTime <= this.getTime();
    }

    public void clearMigrationApiary() {
        this.migrationApiary = null;
    }

    @Override
    public boolean isHealthBarVisible() {
        return false;
    }

    @Override
    public void playDeathSound() {
    }

    @Override
    public void playHitDeathSound() {
    }

    @Override
    public void playHurtSound() {
    }

    @Override
    public void spawnDamageText(int damage, int size, boolean isCrit) {
    }

    @Override
    protected SoundSettings getAmbientSound() {
        return new SoundSettings(GameRandom.globalRandom.getOneOf(GameResources.beeAmbient1, GameResources.beeAmbient2, GameResources.beeAmbient3, GameResources.beeAmbient4)).volume(0.3f);
    }
}

