/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.DeathMessageTable;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.MaskShaderOptions;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.MobWasHitEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ability.TimedMobAbility;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.CollisionPlayerChaserWandererAI;
import necesse.entity.mobs.ai.behaviourTree.util.AIMover;
import necesse.entity.mobs.hostile.HostileMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.entity.particle.SpiritCorruptedParticles;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.maps.Level;
import necesse.level.maps.levelBuffManager.LevelModifiers;
import necesse.level.maps.light.GameLight;

public class DryadSentinelMob
extends HostileMob {
    public static LootTable lootTable = new LootTable(LootItem.between("dryadlog", 2, 6), ChanceLootItem.between(0.3f, "dryadsapling", 1, 2), ChanceLootItem.between(0.15f, "amber", 1, 2), new ChanceLootItem(0.1f, "brokenirontool"), ChanceLootItem.between(0.1f, "apple", 1, 3), new ChanceLootItem(0.1f, "lemon"));
    public final TimedMobAbility awakenAbility;
    public final TimedMobAbility spiritCorruptedStartTimeAbility;
    public final TimedMobAbility spiritCorruptedStopTimeAbility;
    public final TimedMobAbility resetCorruptedTimesAbility;
    public int wakingUpAnimationDuration = 500;
    public int spiritCorruptedDuration = 500;
    public long startWakingUpTime;
    public long spiritCorruptionStartTime;
    public long spiritCorruptionEndTime;

    public DryadSentinelMob() {
        super(1000);
        this.setSpeed(45.0f);
        this.setSwimSpeed(0.75f);
        this.setFriction(3.0f);
        this.setArmor(25);
        this.setKnockbackModifier(0.0f);
        this.collision = new Rectangle(-27, -27, 54, 54);
        this.hitBox = new Rectangle(-32, -27, 64, 54);
        this.selectBox = new Rectangle(-43, -80, 86, 112);
        this.swimMaskMove = 28;
        this.swimMaskOffset = -75;
        this.swimSinkOffset = -4;
        this.awakenAbility = this.registerAbility(new TimedMobAbility(){

            @Override
            protected void run(long time) {
                if (DryadSentinelMob.this.isClient()) {
                    SoundManager.playSound(GameResources.dryadSentinelGrowl, (SoundEffect)SoundEffect.effect(DryadSentinelMob.this).volume(10.0f).pitch(GameRandom.globalRandom.getFloatBetween(0.9f, 1.1f)));
                }
                DryadSentinelMob.this.startWakingUpTime = time;
                DryadSentinelMob.this.isHostile = true;
            }
        });
        this.spiritCorruptedStartTimeAbility = this.registerAbility(new TimedMobAbility(){

            @Override
            protected void run(long time) {
                DryadSentinelMob.this.spiritCorruptionStartTime = time;
            }
        });
        this.spiritCorruptedStopTimeAbility = this.registerAbility(new TimedMobAbility(){

            @Override
            protected void run(long time) {
                DryadSentinelMob.this.spiritCorruptionEndTime = time;
            }
        });
        this.resetCorruptedTimesAbility = this.registerAbility(new TimedMobAbility(){

            @Override
            protected void run(long time) {
                DryadSentinelMob.this.spiritCorruptionStartTime = time;
                DryadSentinelMob.this.spiritCorruptionEndTime = time;
            }
        });
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextLong(this.startWakingUpTime);
        writer.putNextLong(this.spiritCorruptionStartTime);
        writer.putNextLong(this.spiritCorruptionEndTime);
        writer.putNextBoolean(this.isHostile);
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.startWakingUpTime = reader.getNextLong();
        this.spiritCorruptionStartTime = reader.getNextLong();
        this.spiritCorruptionEndTime = reader.getNextLong();
        this.isHostile = reader.getNextBoolean();
    }

    @Override
    public void init() {
        super.init();
        this.isHostile = false;
        CollisionPlayerChaserWandererAI dryadSentinelMobCollisionPlayerChaserWandererAI = new CollisionPlayerChaserWandererAI(null, 960, new GameDamage(60.0f), 200, 40000);
        dryadSentinelMobCollisionPlayerChaserWandererAI.collisionPlayerChaserAI.collisionChaserAINode.attackMoveCooldown = 500;
        this.ai = new BehaviourTreeAI<DryadSentinelMob>(this, dryadSentinelMobCollisionPlayerChaserWandererAI, new AIMover());
    }

    @Override
    public float getSpeed() {
        return this.isAwakened() ? super.getSpeed() : 0.0f;
    }

    @Override
    public float getSpeedModifier() {
        return this.isSpiritCorrupted() ? super.getSpeedModifier() * 1.35f : super.getSpeedModifier();
    }

    @Override
    public Point getPathMoveOffset() {
        return new Point(32, 32);
    }

    public boolean isAwakened() {
        return this.startWakingUpTime != 0L && this.startWakingUpTime + (long)this.wakingUpAnimationDuration <= this.getTime();
    }

    public boolean isSpiritCorrupted() {
        return this.getLevel().buffManager.getModifier(LevelModifiers.SPIRIT_CORRUPTED);
    }

    public float getAwakenedProgress() {
        if (this.startWakingUpTime == 0L) {
            return 0.0f;
        }
        return GameMath.limit((float)(this.getTime() - this.startWakingUpTime) / (float)this.wakingUpAnimationDuration, 0.0f, 1.0f);
    }

    public float getSpiritCorruptedProgress() {
        if (this.isSpiritCorrupted()) {
            if (this.spiritCorruptionStartTime == 0L) {
                return 0.0f;
            }
            return GameMath.limit((float)(this.getTime() - this.spiritCorruptionStartTime) / (float)this.spiritCorruptedDuration, 0.0f, 1.0f);
        }
        if (this.spiritCorruptionEndTime == 0L) {
            return 1.0f;
        }
        return GameMath.limit(1.0f - (float)(this.getTime() - this.spiritCorruptionEndTime) / (float)this.spiritCorruptedDuration, 0.0f, 1.0f);
    }

    @Override
    public LootTable getLootTable() {
        return lootTable;
    }

    @Override
    public DeathMessageTable getDeathMessages() {
        return this.getDeathMessages("dryadsentinel", 3);
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for (int i = 0; i < 8; ++i) {
            this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), MobRegistry.Textures.dryadSentinel, i + 12, 20, 32, this.x, this.y, 50.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    @Override
    public void serverTick() {
        super.serverTick();
        if (this.buffManager.hasBuff(BuffRegistry.BANNER_OF_WAR_MOB) && this.startWakingUpTime == 0L) {
            this.awakenAbility.runAndSend(this.getTime());
        }
        if (this.isSpiritCorrupted()) {
            if (this.spiritCorruptionStartTime == 0L) {
                this.resetCorruptedTimesAbility.runAndSend(0L);
                this.spiritCorruptedStartTimeAbility.runAndSend(this.getTime());
            }
            if (this.getNearestPlayer() != null && this.startWakingUpTime == 0L) {
                this.awakenAbility.runAndSend(this.getTime());
            }
        } else if (this.spiritCorruptionEndTime == 0L) {
            this.resetCorruptedTimesAbility.runAndSend(0L);
            this.spiritCorruptedStopTimeAbility.runAndSend(this.getTime());
        }
    }

    @Override
    public void clientTick() {
        super.clientTick();
        if (this.isSpiritCorrupted()) {
            Level level = this.getLevel();
            if (GameRandom.globalRandom.getChance(0.05f)) {
                level.entityManager.addParticle(new SpiritCorruptedParticles(level, this, this.x, this.y, 2000), Particle.GType.COSMETIC);
            }
        }
    }

    public PlayerMob getNearestPlayer() {
        int checkInRange = 320;
        return this.getLevel().entityManager.players.streamInRegionsInRange(this.x, this.y, checkInRange).filter(s -> s.getDistance(this) <= (float)checkInRange).findFirst().orElse(null);
    }

    @Override
    protected void doWasHitLogic(MobWasHitEvent event) {
        super.doWasHitLogic(event);
        if (this.isServer() && this.startWakingUpTime == 0L) {
            this.awakenAbility.runAndSend(this.getTime());
        }
    }

    @Override
    public Point getAnimSprite(int x, int y, int dir) {
        Point p = new Point(0, dir);
        p.x = Math.abs(this.dx) <= 0.01f && Math.abs(this.dy) <= 0.01f ? 0 : (int)(this.getDistanceRan() / (double)this.getRockSpeed()) % 4 + 1;
        return p;
    }

    @Override
    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(DryadSentinelMob.getTileCoordinate(x), DryadSentinelMob.getTileCoordinate(y));
        int drawX = camera.getDrawX(x) - 64;
        int drawY = camera.getDrawY(y) - 112 + 20;
        Point sprite = this.getAnimSprite(x, y, this.getDir());
        drawY += this.getBobbing(x, y);
        drawY += level.getTile(DryadSentinelMob.getTileCoordinate(x), DryadSentinelMob.getTileCoordinate(y)).getMobSinkingAmount(this);
        int frames = 6;
        if (!this.isAwakened()) {
            if (this.getAwakenedProgress() != 0.0f) {
                sprite.x = Math.min((int)(this.getAwakenedProgress() * (float)frames), frames - 1);
                sprite.y = 4;
            } else {
                frames = 3;
                sprite.y = 5;
                sprite.x = Math.min((int)(this.getSpiritCorruptedProgress() * (float)frames), frames - 1);
            }
        }
        final MaskShaderOptions swimMask = this.getSwimMaskShaderOptions(this.inLiquidFloat(x, y));
        int spriteRes = 128;
        final TextureDrawOptionsEnd options = MobRegistry.Textures.dryadSentinel.initDraw().sprite(sprite.x, sprite.y, spriteRes).addMaskShader(swimMask).light(light).pos(drawX, drawY);
        list.add(new MobDrawable(){

            @Override
            public void draw(TickManager tickManager) {
                swimMask.use();
                options.draw();
                swimMask.stop();
            }
        });
        TextureDrawOptionsEnd shadow = MobRegistry.Textures.dryadSentinel_shadow.initDraw().sprite(sprite.x, sprite.y, spriteRes, spriteRes).light(light).pos(drawX, drawY);
        tileList.add(tm -> shadow.draw());
    }

    @Override
    public int getRockSpeed() {
        return 17;
    }
}

