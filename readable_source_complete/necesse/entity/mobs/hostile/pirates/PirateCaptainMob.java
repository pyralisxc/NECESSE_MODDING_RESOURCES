/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile.pirates;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import necesse.engine.AreaFinder;
import necesse.engine.CameraShake;
import necesse.engine.achievements.AchievementManager;
import necesse.engine.eventStatusBars.EventStatusBarManager;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.MusicRegistry;
import necesse.engine.registries.ProjectileRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.sound.PositionSoundEffect;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.SoundPlayer;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.engine.util.gameAreaSearch.GameAreaStream;
import necesse.entity.Entity;
import necesse.entity.manager.EntityManager;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.DeathMessageTable;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.HumanTexture;
import necesse.entity.mobs.MaskShaderOptions;
import necesse.entity.mobs.MaxHealthGetter;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.MobHealthScaling;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ability.BooleanMobAbility;
import necesse.entity.mobs.ability.CoordinateMobAbility;
import necesse.entity.mobs.ability.EmptyMobAbility;
import necesse.entity.mobs.ability.TimedMobAbility;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.composites.SelectorAINode;
import necesse.entity.mobs.ai.behaviourTree.composites.SequenceAINode;
import necesse.entity.mobs.ai.behaviourTree.decorators.ConditionAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.TargetFinderAINode;
import necesse.entity.mobs.ai.behaviourTree.trees.PirateAITree;
import necesse.entity.mobs.ai.behaviourTree.util.TargetFinderDistance;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.BossNearbyBuff;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.mobs.friendly.human.humanShop.PirateHumanMob;
import necesse.entity.mobs.hostile.bosses.BossMob;
import necesse.entity.mobs.hostile.pirates.PirateMob;
import necesse.entity.mobs.mobMovement.MobMovementLevelPos;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.Projectile;
import necesse.gfx.GameResources;
import necesse.gfx.HumanGender;
import necesse.gfx.HumanLook;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.shader.ShaderState;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.inventory.lootTable.lootItem.ConditionLootItem;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.LootItemList;
import necesse.inventory.lootTable.lootItem.RotationLootItem;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.MobChance;
import necesse.level.maps.biomes.MobSpawnTable;
import necesse.level.maps.light.GameLight;

public class PirateCaptainMob
extends PirateMob {
    public static LootTable lootTable = new LootTable(LootItem.between("coin", 300, 500), new ChanceLootItem(0.2f, "siegevinyl"));
    public static RotationLootItem uniqueDrops = RotationLootItem.privateLootRotation(new LootItemList(new LootItem("handcannon"), LootItem.between("cannonball", 10, 50)), new LootItem("piratetelescope"), new LootItem("spareboatparts"));
    public static LootTable privateLootTable = new LootTable(new ConditionLootItem("piratesheath", (r, o) -> {
        ServerClient client = LootTable.expectExtra(ServerClient.class, o, 1);
        return client != null && client.playerMob.getInv().equipment.getTrinketSlotsSize() < 6 && client.playerMob.getInv().getAmount(ItemRegistry.getItem("piratesheath"), false, false, true, true, "have") == 0;
    }), uniqueDrops);
    public static MobSpawnTable pirateMobs = new MobSpawnTable().add(100, "piraterecruit").add(50, "pirateparrot");
    public static MaxHealthGetter MAX_HEALTH = new MaxHealthGetter(6250, 7750, 8500, 9250, 10750);
    protected MobHealthScaling scaling = new MobHealthScaling(this);
    protected boolean inCombat;
    protected boolean hasPlayedIntroSound;
    protected boolean inSecondStage;
    protected boolean inSecondStageTransition;
    protected long secondStageTransitionStartTime;
    protected CameraShake secondStageCameraShake;
    protected SoundPlayer secondStageRumble;
    protected SoundPlayer secondStageWind;
    private PositionSoundEffect secondStageWindEffect;
    protected int settlerSeed = GameRandom.globalRandom.nextInt();
    protected HumanLook look;
    protected float leftCannonRotation = 135.0f;
    protected float midCannonRotation = 90.0f;
    protected float rightCannonRotation = 45.0f;
    public boolean dropLadder;
    protected int shootDistance = 960;
    public BooleanMobAbility setInCombatAbility;
    public TimedMobAbility startSecondStageAbility;
    public EmptyMobAbility endSecondStageAbility;
    public CoordinateMobAbility fireCannonAbility;
    public static int secondStageTransitionTime = 5000;
    public static int captainMeleeDamage = 50;
    public static int captainCannonDamage = 120;
    public static GameDamage collisionDamage = new GameDamage(55.0f);

    public PirateCaptainMob() {
        super(100);
        this.difficultyChanges.setMaxHealth(MAX_HEALTH);
        this.setSpeed(15.0f);
        this.setKnockbackModifier(0.0f);
        this.setRegen(100.0f);
        this.meleeDamage = captainMeleeDamage;
        this.shootDamage = captainCannonDamage;
        this.setInCombatAbility = this.registerAbility(new BooleanMobAbility(){

            @Override
            protected void run(boolean value) {
                PirateCaptainMob.this.inCombat = value;
            }
        });
        this.startSecondStageAbility = this.registerAbility(new TimedMobAbility(){

            @Override
            protected void run(long time) {
                PirateCaptainMob.this.inSecondStageTransition = true;
                PirateCaptainMob.this.secondStageTransitionStartTime = time;
                PirateCaptainMob.this.inSecondStage = true;
                PirateCaptainMob.this.moveX = 0.0f;
                PirateCaptainMob.this.moveY = 0.0f;
                PirateCaptainMob.this.setMovement(null);
                PirateCaptainMob.this.updateStageVariables();
            }
        });
        this.endSecondStageAbility = this.registerAbility(new EmptyMobAbility(){

            @Override
            protected void run() {
                Point pos;
                PirateCaptainMob.this.inSecondStageTransition = false;
                PirateCaptainMob.this.inSecondStage = false;
                PirateCaptainMob.this.updateStageVariables();
                if (!PirateCaptainMob.this.isClient() && PirateCaptainMob.this.collidesWith(PirateCaptainMob.this.getLevel()) && (pos = PirateCaptainMob.this.findEmptySpot(4)) != null) {
                    PirateCaptainMob.this.setPos(pos.x, pos.y, true);
                    PirateCaptainMob.this.sendMovementPacket(true);
                }
            }
        });
        this.fireCannonAbility = this.registerAbility(new CoordinateMobAbility(){

            @Override
            protected void run(int x, int y) {
                Point2D.Float dir = GameMath.normalize(x - PirateCaptainMob.this.getX(), y - PirateCaptainMob.this.getY() - 20);
                float rotation = GameMath.fixAngle(GameMath.getAngle(dir) + 90.0f);
                if (rotation >= 45.0f && rotation < 135.0f) {
                    PirateCaptainMob.this.rightCannonRotation = rotation - 90.0f;
                } else if (rotation >= 135.0f && rotation < 225.0f) {
                    PirateCaptainMob.this.midCannonRotation = rotation - 90.0f;
                } else if (rotation >= 225.0f && rotation < 315.0f) {
                    PirateCaptainMob.this.leftCannonRotation = rotation - 90.0f;
                }
                int range = (int)PirateCaptainMob.this.getDistance(x, y + 20);
                if (PirateCaptainMob.this.isServer()) {
                    float healthPercInv = Math.abs((float)PirateCaptainMob.this.getHealth() / (float)PirateCaptainMob.this.getMaxHealth() - 1.0f);
                    int velocity = 125 + (int)(healthPercInv * 100.0f);
                    Projectile p = ProjectileRegistry.getProjectile("captaincannonball", PirateCaptainMob.this.getLevel(), (float)PirateCaptainMob.this.getX(), (float)(PirateCaptainMob.this.getY() - 20), (float)x, (float)y, (float)velocity, range, new GameDamage(PirateCaptainMob.this.shootDamage), 50, (Mob)PirateCaptainMob.this);
                    p.isSolid = false;
                    PirateCaptainMob.this.getLevel().entityManager.projectiles.add(p);
                } else {
                    SoundManager.playSound(GameResources.explosionLight, (SoundEffect)SoundEffect.effect(PirateCaptainMob.this).volume(2.0f).pitch(0.9f));
                    for (int i = 0; i < 40; ++i) {
                        PirateCaptainMob.this.getLevel().entityManager.addTopParticle(PirateCaptainMob.this.getX(), PirateCaptainMob.this.getY() - 20, Particle.GType.IMPORTANT_COSMETIC).movesConstant(PirateCaptainMob.this.dx / 4.0f + dir.x * 20.0f + GameRandom.globalRandom.floatGaussian() * 5.0f, PirateCaptainMob.this.dy / 4.0f + dir.y * 20.0f + GameRandom.globalRandom.floatGaussian() * 5.0f).smokeColor().lifeTime(1000);
                    }
                }
            }
        });
    }

    public Point findEmptySpot(int maxTileRange) {
        AreaFinder areaFinder = new AreaFinder(this.getTileX(), this.getTileY(), maxTileRange, true){

            @Override
            public boolean checkPoint(int x, int y) {
                return PirateCaptainMob.this.getLevel().collides((Shape)new Rectangle(PirateCaptainMob.this.getX() - 10, PirateCaptainMob.this.getY() - 7, 20, 14), new CollisionFilter().mobCollision());
            }
        };
        areaFinder.tickFinder(areaFinder.getRemainingTicks());
        if (areaFinder.hasFound()) {
            Point firstFind = areaFinder.getFirstFind();
            return new Point(firstFind.x * 32 + 16, firstFind.y * 32 + 16);
        }
        return null;
    }

    @Override
    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        save.addBoolean("dropLadder", this.dropLadder);
        save.addInt("settlerSeed", this.settlerSeed);
    }

    @Override
    public void applyLoadData(LoadData save) {
        super.applyLoadData(save);
        this.dropLadder = save.getBoolean("dropLadder", true);
        this.setSettlerSeed(save.getInt("settlerSeed", this.settlerSeed, false));
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextBoolean(this.inCombat);
        writer.putNextBoolean(this.inSecondStage);
        writer.putNextBoolean(this.inSecondStageTransition);
        if (this.inSecondStageTransition) {
            writer.putNextLong(this.secondStageTransitionStartTime);
        }
        writer.putNextInt(this.settlerSeed);
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.inCombat = reader.getNextBoolean();
        this.inSecondStage = reader.getNextBoolean();
        this.inSecondStageTransition = reader.getNextBoolean();
        if (this.inSecondStageTransition) {
            this.secondStageTransitionStartTime = reader.getNextLong();
        }
        this.setSettlerSeed(reader.getNextInt());
        this.updateStageVariables();
    }

    @Override
    public void setupHealthPacket(PacketWriter writer, boolean isFull) {
        this.scaling.setupHealthPacket(writer, isFull);
        super.setupHealthPacket(writer, isFull);
    }

    @Override
    public void applyHealthPacket(PacketReader reader, boolean isFull) {
        this.scaling.applyHealthPacket(reader, isFull);
        super.applyHealthPacket(reader, isFull);
    }

    @Override
    public void setMaxHealth(int maxHealth) {
        super.setMaxHealth(maxHealth);
        if (this.scaling != null) {
            this.scaling.updatedMaxHealth();
        }
    }

    @Override
    public void init() {
        super.init();
        this.hasPlayedIntroSound = false;
        this.updateLook();
    }

    public void setSettlerSeed(int seed) {
        if (this.settlerSeed != seed) {
            this.settlerSeed = seed;
            this.updateLook();
        }
    }

    public void updateLook() {
        Mob mob = MobRegistry.getMob("piratehuman", this.getLevel());
        if (mob instanceof HumanMob) {
            HumanMob humanMob = (HumanMob)mob;
            humanMob.setSettlerSeed(this.settlerSeed, false);
            this.look = humanMob.look;
        } else {
            this.look = new HumanLook();
            GameRandom random = new GameRandom(this.settlerSeed);
            this.look.randomizeLook(random, true, HumanGender.MALE, true, true, true, true);
            this.look.setFacialFeature(random.getOneOf(1, 3, 4));
        }
        this.look.setEyeType(7);
    }

    public int getSettlerSeed() {
        return this.settlerSeed;
    }

    @Override
    public GameDamage getCollisionDamage(Mob target, boolean fromPacket, ServerClient packetSubmitter) {
        return collisionDamage;
    }

    @Override
    public boolean canCollisionHit(Mob target) {
        return this.inSecondStage && super.canCollisionHit(target);
    }

    @Override
    public int getMaxHealth() {
        return super.getMaxHealth() + (int)((float)(this.scaling == null ? 0 : this.scaling.getHealthIncrease()) * this.getMaxHealthModifier());
    }

    @Override
    public void setupAI() {
        if (this.baseTile == null || this.baseTile.x == 0 && this.baseTile.y == 0) {
            this.baseTile = new Point(this.getTileX(), this.getTileY());
        }
        this.ai = new BehaviourTreeAI<PirateCaptainMob>(this, new PirateCaptainAITree(this.shootDistance, 5000, 40, 640, 60000));
    }

    @Override
    public boolean isInCombat() {
        return this.inCombat || super.isInCombat();
    }

    @Override
    public void clientTick() {
        super.clientTick();
        if (this.inCombat) {
            if (!this.hasPlayedIntroSound) {
                SoundManager.playSound(GameResources.piratecaptainbegin, (SoundEffect)SoundEffect.effect(this).volume(1.5f).falloffDistance(4000));
                this.hasPlayedIntroSound = true;
            }
            if (this.isClientPlayerNearby()) {
                SoundManager.setMusic(MusicRegistry.PirateCaptainsLair, SoundManager.MusicPriority.EVENT, 1.5f);
                EventStatusBarManager.registerMobHealthStatusBar(this);
            }
            BossNearbyBuff.applyAround(this);
        } else {
            this.hasPlayedIntroSound = false;
        }
        if (this.inSecondStage && !this.inSecondStageTransition) {
            if (this.secondStageWind == null || this.secondStageWind.isDone()) {
                this.secondStageWindEffect = SoundEffect.effect(this).volume(0.7f).falloffDistance(1400);
                this.secondStageWind = SoundManager.playSound(GameResources.wind1, (SoundEffect)this.secondStageWindEffect);
            }
            if (this.secondStageWind != null) {
                this.secondStageWind.refreshLooping(1.0f);
                this.secondStageWindEffect.volume(Math.min(1.0f, this.getCurrentSpeed() / 100.0f) * 0.7f);
            }
        }
        if (this.inSecondStageTransition && this.isClient()) {
            Client client = this.getClient();
            if (this.secondStageCameraShake == null) {
                client.startCameraShake(this, this.secondStageTransitionStartTime, secondStageTransitionTime + 500, 40, 3.0f, 3.0f, true);
                this.secondStageCameraShake = new CameraShake(secondStageTransitionTime, secondStageTransitionTime, 40, 1.5f, 1.5f, true);
            }
            if (this.secondStageRumble == null || this.secondStageRumble.isDone()) {
                this.secondStageRumble = SoundManager.playSound(GameResources.rumble, (SoundEffect)SoundEffect.effect(this).falloffDistance(1400));
            }
            if (this.secondStageRumble != null) {
                this.secondStageRumble.refreshLooping(1.0f);
            }
            this.getSecondStageTransitionProgress();
            for (int i = 0; i < 2; ++i) {
                this.getLevel().entityManager.addParticle(this.x + GameRandom.globalRandom.floatGaussian() * 15.0f, this.y + GameRandom.globalRandom.floatGaussian() * 5.0f + 20.0f, Particle.GType.IMPORTANT_COSMETIC).movesConstant(GameRandom.globalRandom.floatGaussian() * 6.0f, GameRandom.globalRandom.floatGaussian() * 3.0f).smokeColor().heightMoves(10.0f, GameRandom.globalRandom.getFloatBetween(30.0f, 40.0f)).lifeTime(1000);
            }
        } else {
            this.secondStageCameraShake = null;
        }
    }

    @Override
    public void serverTick() {
        float healthPerc;
        super.serverTick();
        this.scaling.serverTick();
        if (this.inCombat) {
            BossNearbyBuff.applyAround(this);
        }
        this.getSecondStageTransitionProgress();
        if (this.inCombat && !this.inSecondStage && !this.isAttacking && (healthPerc = (float)this.getHealth() / (float)this.getMaxHealth()) <= 0.9f) {
            this.startSecondStageAbility.runAndSend(this.getWorldEntity().getTime());
        }
        if (!this.inCombat && this.inSecondStage && this.getHealth() == this.getMaxHealth()) {
            this.endSecondStageAbility.runAndSend();
        }
    }

    public void updateStageVariables() {
        if (this.inSecondStage) {
            this.setSpeed(140.0f);
            this.setFriction(0.5f);
            this.collision = new Rectangle(-20, -37, 40, 44);
            this.hitBox = new Rectangle(-25, -42, 50, 54);
            this.selectBox = new Rectangle(-30, -121, 60, 128);
            this.moveAccuracy = 50;
        } else {
            this.setSpeed(15.0f);
            this.setFriction(3.0f);
            this.collision = new Rectangle(-10, -7, 20, 14);
            this.hitBox = new Rectangle(-14, -12, 28, 24);
            this.selectBox = new Rectangle(-14, -41, 28, 48);
            this.moveAccuracy = 5;
        }
    }

    @Override
    public CollisionFilter getLevelCollisionFilter() {
        if (this.isFlying()) {
            return null;
        }
        return super.getLevelCollisionFilter();
    }

    @Override
    public int getFlyingHeight() {
        if (this.isRiding()) {
            return super.getFlyingHeight();
        }
        if (this.inSecondStageTransition) {
            float progress = this.getSecondStageTransitionProgress();
            return (int)(progress * 50.0f);
        }
        if (this.inSecondStage) {
            return 40;
        }
        return 0;
    }

    @Override
    public boolean canHitThroughCollision() {
        return this.isFlying();
    }

    public float getSecondStageTransitionProgress() {
        if (this.inSecondStageTransition) {
            long transitionTime = this.getWorldEntity().getTime() - this.secondStageTransitionStartTime;
            if (transitionTime >= (long)secondStageTransitionTime) {
                this.inSecondStageTransition = false;
                return 1.0f;
            }
            return (float)transitionTime / (float)secondStageTransitionTime;
        }
        return -1.0f;
    }

    @Override
    public boolean inLiquid(int x, int y) {
        if (this.inSecondStage) {
            return false;
        }
        return super.inLiquid(x, y);
    }

    public HumanDrawOptions getHumanDrawOptions(Level level) {
        HumanDrawOptions humanDrawOptions = new HumanDrawOptions(level, this.look, true);
        humanDrawOptions.helmet(new InventoryItem("captainshat"));
        humanDrawOptions.chestplate(new InventoryItem("captainsshirt"));
        humanDrawOptions.boots(new InventoryItem("captainsboots"));
        return humanDrawOptions;
    }

    @Override
    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        Point sprite;
        GameLight light = level.getLightLevel(PirateCaptainMob.getTileCoordinate(x), PirateCaptainMob.getTileCoordinate(y));
        Point2D.Float shake = this.secondStageCameraShake == null ? new Point2D.Float() : this.secondStageCameraShake.getCurrentShake(this.getWorldEntity().getTime());
        int drawX = camera.getDrawX((float)x + shake.x) - 32;
        int drawY = camera.getDrawY((float)y + shake.y) - 51;
        int dir = this.getDir();
        if (this.inSecondStage) {
            this.setDir(2);
            sprite = new Point(0, 2);
        } else {
            sprite = this.getAnimSprite(x, y, dir);
        }
        drawY += this.getBobbing(x, y);
        HumanDrawOptions humanDrawOptions = this.getHumanDrawOptions(level).sprite(sprite).dir(dir).light(light);
        if (this.inSecondStageTransition) {
            float progress = this.getSecondStageTransitionProgress();
            int height = (int)(progress * 220.0f) - 180;
            final DrawOptions drawOptions = humanDrawOptions.pos(drawX, drawY - Math.max(height, 0));
            GameTexture maskTexture = MobRegistry.Textures.pirateCaptainShip_mask;
            GameTexture shipTexture = MobRegistry.Textures.pirateCaptainShip;
            TextureDrawOptionsEnd shipBack = shipTexture.initDraw().sprite(0, 0, 192).light(light);
            final ShaderState shipBackShader = GameResources.edgeMaskShader.setup(shipBack, maskTexture, 0, height + 70);
            final TextureDrawOptionsEnd shipBackOptions = shipBack.pos(drawX - 64, (drawY -= height) - 64);
            TextureDrawOptionsEnd shipFront = shipTexture.initDraw().sprite(0, 1, 192).light(light);
            final ShaderState shipFontShader = GameResources.edgeMaskShader.setup(shipFront, maskTexture, 0, height + 70);
            final TextureDrawOptionsEnd shipFrontOptions = shipFront.pos(drawX - 64, drawY - 64);
            TextureDrawOptionsEnd leftCannon = shipTexture.initDraw().sprite(0, 12, 32).mirrorX().light(light);
            TextureDrawOptionsEnd midCannon = shipTexture.initDraw().sprite(1, 12, 32).light(light);
            TextureDrawOptionsEnd rightCannon = shipTexture.initDraw().sprite(0, 12, 32).light(light);
            GameResources.edgeMaskShader.setup(leftCannon, maskTexture, -62, height + 70 - 64 - 56);
            GameResources.edgeMaskShader.setup(midCannon, maskTexture, -80, height + 70 - 64 - 62);
            GameResources.edgeMaskShader.setup(rightCannon, maskTexture, -98, height + 70 - 64 - 56);
            final TextureDrawOptionsEnd leftCannonOptions = leftCannon.pos(drawX - 2, drawY + 56);
            final TextureDrawOptionsEnd midCannonOptions = midCannon.pos(drawX + 16, drawY + 62);
            final TextureDrawOptionsEnd rightCannonOptions = rightCannon.pos(drawX + 34, drawY + 56);
            list.add(new MobDrawable(){

                @Override
                public void draw(TickManager tickManager) {
                    shipBackShader.use();
                    try {
                        shipBackOptions.draw();
                    }
                    finally {
                        shipBackShader.stop();
                    }
                    drawOptions.draw();
                    shipFontShader.use();
                    try {
                        shipFrontOptions.draw();
                        leftCannonOptions.draw();
                        midCannonOptions.draw();
                        rightCannonOptions.draw();
                    }
                    finally {
                        shipFontShader.stop();
                    }
                }
            });
        } else if (this.inSecondStage) {
            float rotate = GameMath.limit(this.dx / 10.0f, -15.0f, 15.0f);
            int anim = GameUtils.getAnim(this.getWorldEntity().getTime(), 4, 600);
            GameTexture shipTexture = MobRegistry.Textures.pirateCaptainShip;
            DrawOptions humanOptions = humanDrawOptions.pos(drawX, drawY -= 40);
            TextureDrawOptionsEnd shipBack = shipTexture.initDraw().sprite(anim, 0, 192).mirror(this.dx < 0.0f, false).rotate(rotate, 96, 120).light(light).pos(drawX - 64, drawY - 64);
            TextureDrawOptionsEnd shipFront = shipTexture.initDraw().sprite(anim, 1, 192).mirror(this.dx < 0.0f, false).rotate(rotate, 96, 120).light(light).pos(drawX - 64, drawY - 64);
            TextureDrawOptionsEnd leftCannon = shipTexture.initDraw().sprite(0, 12, 32).rotate(rotate, 34, 0).addRotation(this.leftCannonRotation - rotate + 45.0f + 180.0f, 16, 16).mirrorX().light(light).pos(drawX - 2, drawY + 56);
            TextureDrawOptionsEnd midCannon = shipTexture.initDraw().sprite(1, 12, 32).rotate(rotate, 16, -6).addRotation(this.midCannonRotation - rotate - 90.0f, 16, 14).light(light).pos(drawX + 16, drawY + 62);
            TextureDrawOptionsEnd rightCannon = shipTexture.initDraw().sprite(0, 12, 32).rotate(rotate, -2, 0).addRotation(this.rightCannonRotation - rotate - 45.0f, 14, 14).light(light).pos(drawX + 34, drawY + 56);
            topList.add(tm -> {
                shipBack.draw();
                humanOptions.draw();
                shipFront.draw();
                leftCannon.draw();
                midCannon.draw();
                rightCannon.draw();
            });
        } else {
            boolean inLiquid;
            MaskShaderOptions swimMask = this.getSwimMaskShaderOptions(this.inLiquidFloat(x, y));
            humanDrawOptions.mask(swimMask);
            drawY += level.getTile(PirateCaptainMob.getTileCoordinate(x), PirateCaptainMob.getTileCoordinate(y)).getMobSinkingAmount(this);
            float attackProgress = this.getAttackAnimProgress();
            if (this.isAttacking) {
                this.addAttackDraw(humanDrawOptions, attackProgress);
            }
            if (inLiquid = this.inLiquid(x, y)) {
                humanDrawOptions.armSprite(2);
                humanDrawOptions.mask(MobRegistry.Textures.boat_mask[sprite.y % 4], 0, -7);
            }
            final DrawOptions drawOptions = humanDrawOptions.pos(drawX, drawY);
            final TextureDrawOptionsEnd boat = inLiquid ? MobRegistry.Textures.steelBoat.initDraw().sprite(0, dir % 4, 64).light(light).pos(drawX, drawY + 7) : null;
            list.add(new MobDrawable(){

                @Override
                public void draw(TickManager tickManager) {
                    if (boat != null) {
                        boat.draw();
                    }
                    drawOptions.draw();
                }
            });
        }
        this.addShadowDrawables(tileList, level, x, y, light, camera);
    }

    @Override
    public boolean shouldDrawOnMap() {
        return true;
    }

    @Override
    public void drawOnMap(TickManager tickManager, Client client, int x, int y, double tileScale, Rectangle drawBounds, boolean isMinimap) {
        Point sprite;
        super.drawOnMap(tickManager, client, x, y, tileScale, drawBounds, isMinimap);
        int drawX = x - 16;
        int drawY = y - 26;
        int dir = this.getDir();
        if (this.inSecondStage) {
            this.setDir(2);
            sprite = new Point(0, 2);
        } else {
            sprite = this.getAnimSprite(this.getDrawX(), this.getDrawY(), dir);
        }
        HumanDrawOptions humanOptions = this.getHumanDrawOptions(this.getLevel()).size(32, 32).sprite(sprite).dir(dir);
        if (this.inSecondStage) {
            int anim = GameUtils.getAnim(this.getWorldEntity().getTime(), 4, 600);
            GameTexture shipTexture = MobRegistry.Textures.pirateCaptainShip;
            shipTexture.initDraw().sprite(anim, 0, 192).size(96, 96).draw(drawX - 32, (drawY -= 20) - 32);
            humanOptions.draw(drawX, drawY);
            shipTexture.initDraw().sprite(anim, 1, 192).size(96, 96).draw(drawX - 32, drawY - 32);
            shipTexture.initDraw().sprite(0, 12, 32).size(16, 16).mirrorX().draw(drawX - 1, drawY + 28);
            shipTexture.initDraw().sprite(1, 12, 32).size(16, 16).draw(drawX + 8, drawY + 31);
            shipTexture.initDraw().sprite(0, 12, 32).size(16, 16).draw(drawX + 17, drawY + 28);
        } else {
            humanOptions.draw(drawX, drawY);
        }
    }

    @Override
    protected TextureDrawOptions getShadowDrawOptions(Level level, int x, int y, GameLight light, GameCamera camera) {
        if (this.inSecondStage) {
            GameTexture shadowTexture = MobRegistry.Textures.pirateCaptainShip_shadow;
            int drawX = camera.getDrawX(x) - shadowTexture.getWidth() / 2;
            int drawY = camera.getDrawY(y) - shadowTexture.getHeight() / 2;
            return shadowTexture.initDraw().light(light).pos(drawX, drawY += this.getBobbing(x, y));
        }
        return super.getShadowDrawOptions(level, x, y, light, camera);
    }

    @Override
    protected HumanTexture getPirateTexture() {
        return MobRegistry.Textures.pirateCaptain;
    }

    @Override
    public Rectangle drawOnMapBox(double tileScale, boolean isMinimap) {
        if (this.inSecondStage) {
            return new Rectangle(-16, -60, 32, 60);
        }
        return new Rectangle(-8, -22, 16, 25);
    }

    @Override
    public GameTooltips getMapTooltips() {
        return new StringTooltips(this.getDisplayName() + " " + this.getHealth() + "/" + this.getMaxHealth());
    }

    @Override
    public LootTable getLootTable() {
        if (this.dropLadder) {
            return new LootTable(lootTable, new LootItem("deepladderdown"));
        }
        return lootTable;
    }

    @Override
    public LootTable getPrivateLootTable() {
        return privateLootTable;
    }

    @Override
    public void playHurtSound() {
        float pitch = GameRandom.globalRandom.getOneOf(Float.valueOf(0.95f), Float.valueOf(1.0f), Float.valueOf(1.05f)).floatValue();
        SoundManager.playSound(GameResources.piratecaptainhurt, (SoundEffect)SoundEffect.effect(this).volume(0.3f).pitch(pitch).falloffDistance(1500));
    }

    @Override
    protected void addRangedAttackDraw(HumanDrawOptions drawOptions, float attackProgress) {
        drawOptions.itemAttack(new InventoryItem("handcannon"), null, attackProgress, this.attackDir.x, this.attackDir.y);
    }

    @Override
    public Stream<ModifierValue<?>> getDefaultModifiers() {
        return Stream.of(new ModifierValue<Float>(BuffModifiers.SLOW, Float.valueOf(0.0f)).max(Float.valueOf(0.2f)));
    }

    @Override
    public void shootAbilityProjectile(int x, int y) {
        if (this.isServer()) {
            GameRandom random = new GameRandom(x + y);
            float prcRange = this.getDistance(x, y) / (float)this.shootDistance;
            int finalX = (int)((float)x + (random.nextFloat() * 2.0f - 1.0f) * 30.0f * prcRange);
            int finalY = (int)((float)y + (random.nextFloat() * 2.0f - 1.0f) * 30.0f * prcRange);
            int range = (int)this.getDistance(finalX, finalY);
            Projectile p = ProjectileRegistry.getProjectile("captaincannonball", this.getLevel(), this.x, this.y, (float)finalX, (float)finalY, 150.0f, range, new GameDamage(this.shootDamage), 50, (Mob)this);
            p.isSolid = true;
            p.resetUniqueID(random);
            this.getLevel().entityManager.projectiles.add(p);
        }
        this.showAttack(x, y, false);
        if (this.isClient()) {
            SoundManager.playSound(GameResources.explosionLight, (SoundEffect)SoundEffect.effect(this).volume(2.0f).pitch(0.9f));
        }
    }

    @Override
    public int getRespawnTime() {
        return BossMob.getBossRespawnTime(this);
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        if (this.inSecondStage) {
            for (int i = 0; i < 7; ++i) {
                this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), MobRegistry.Textures.pirateCaptainShip, i, 12, 32, this.x, this.y, 20.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
            }
        }
    }

    @Override
    public void playDeathSound() {
        SoundManager.playSound(GameResources.piratecaptaindeath, (SoundEffect)SoundEffect.effect(this).falloffDistance(3000));
    }

    @Override
    protected void onDeath(Attacker attacker, HashSet<Attacker> attackers) {
        super.onDeath(attacker, attackers);
        attackers.stream().map(Attacker::getFirstPlayerOwner).filter(Objects::nonNull).filter(PlayerMob::isServerClient).map(PlayerMob::getServerClient).distinct().forEach(c -> {
            c.sendChatMessage(new LocalMessage("misc", "bossdefeat", "name", this.getLocalization()));
            if (c.achievementsLoaded()) {
                c.achievements().DEFEAT_PIRATE.markCompleted((ServerClient)c);
            }
        });
        if (!this.isDamagedByPlayers) {
            AchievementManager.checkMeAndThisArmyKill(this.getLevel(), attackers);
        }
        PirateHumanMob pirateHuman = (PirateHumanMob)MobRegistry.getMob("piratehuman", this.getLevel());
        pirateHuman.buffManager.addBuff(new ActiveBuff(BuffRegistry.INVULNERABLE_ACTIVE, (Mob)pirateHuman, 2.0f, null), false);
        pirateHuman.setSettlerSeed(this.settlerSeed, true);
        pirateHuman.setTrapped();
        pirateHuman.setDir(this.getDir());
        Point pos = this.findEmptySpot(4);
        if (pos != null) {
            this.getLevel().entityManager.addMob(pirateHuman, pos.x, pos.y);
        } else {
            this.getLevel().entityManager.addMob(pirateHuman, this.x, this.y);
        }
    }

    @Override
    public DeathMessageTable getDeathMessages() {
        return this.getDeathMessages("piratecap", 4);
    }

    public static class PirateCaptainAITree<T extends PirateCaptainMob>
    extends PirateAITree<T> {
        public String chaserTargetKey = "chaserTarget";
        public String currentTargetKey = "currentTarget";
        private final ArrayList<Mob> spawnedMobs = new ArrayList();
        private long musicCooldown;
        private float minionSpawnBuffer;
        private int dropCombatCounter;

        public PirateCaptainAITree(int shootDistance, int shootCooldown, int meleeDistance, int searchDistance, int wanderFrequency) {
            super(shootDistance, shootCooldown, meleeDistance, searchDistance, wanderFrequency);
            this.addChildFirst(new AINode<T>(){

                @Override
                protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
                    blackboard.onEvent("refreshBossDespawn", event -> dropCombatCounter = 0);
                }

                @Override
                public void init(T mob, Blackboard<T> blackboard) {
                }

                @Override
                public AINodeResult tick(T mob, Blackboard<T> blackboard) {
                    Mob target = blackboard.getObject(Mob.class, chaserTargetKey);
                    if (target != null) {
                        dropCombatCounter = 0;
                        for (int i = 0; i < spawnedMobs.size(); ++i) {
                            Mob m2 = (Mob)spawnedMobs.get(i);
                            if (!m2.removed()) continue;
                            spawnedMobs.remove(i);
                            --i;
                        }
                        long clients = GameUtils.streamServerClients(((Entity)mob).getLevel()).filter(c -> !c.isDead() && mob.getDistance(c.playerMob) < 1280.0f).count();
                        float spawnsMod = Math.min(1.0f + (float)(clients - 1L) / 2.0f, 5.0f);
                        minionSpawnBuffer += 0.0125f * spawnsMod;
                        if (minionSpawnBuffer >= 1.0f) {
                            MobChance randomMob;
                            Point spawnTile;
                            PirateCaptainAITree.access$210(this);
                            if ((float)spawnedMobs.size() < 5.0f * spawnsMod && (spawnTile = EntityManager.getMobSpawnTile(((Entity)mob).getLevel(), target.getX(), target.getY(), Mob.MOB_SPAWN_AREA, null)) != null && (randomMob = pirateMobs.getRandomMob(((Entity)mob).getLevel(), null, spawnTile, GameRandom.globalRandom, "piratecaptain")) != null) {
                                randomMob.spawnMob(((Entity)mob).getLevel(), null, spawnTile, m -> {
                                    if (m instanceof PirateMob) {
                                        ((PirateMob)m).setSummoned();
                                        return true;
                                    }
                                    return false;
                                }, m -> {
                                    ((PirateMob)m).baseTile = new Point(mob.baseTile);
                                    spawnedMobs.add(m);
                                    m.ai.blackboard.put(currentTargetKey, target);
                                }, "piratecaptain");
                            }
                        }
                        if (musicCooldown < ((Entity)mob).getWorldEntity().getTime()) {
                            musicCooldown = ((Entity)mob).getWorldEntity().getTime() + 5000L;
                            ((PirateCaptainMob)mob).setInCombatAbility.runAndSend(true);
                        }
                    } else if (((PirateCaptainMob)mob).inCombat) {
                        dropCombatCounter++;
                        if (dropCombatCounter > 60) {
                            dropCombatCounter = 0;
                            for (int i = 0; i < spawnedMobs.size(); ++i) {
                                Mob m3 = (Mob)spawnedMobs.get(i);
                                m3.remove();
                                spawnedMobs.remove(i);
                                --i;
                            }
                            if (musicCooldown != 0L) {
                                musicCooldown = 0L;
                                ((PirateCaptainMob)mob).setInCombatAbility.runAndSend(false);
                            }
                        }
                    }
                    return AINodeResult.FAILURE;
                }
            });
            this.addChildFirst(new AINode<T>(){

                @Override
                protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
                }

                @Override
                public void init(T mob, Blackboard<T> blackboard) {
                }

                @Override
                public AINodeResult tick(T mob, Blackboard<T> blackboard) {
                    if (((PirateCaptainMob)mob).inSecondStageTransition) {
                        blackboard.mover.stopMoving((Mob)mob);
                        return AINodeResult.SUCCESS;
                    }
                    return AINodeResult.FAILURE;
                }
            });
        }

        @Override
        protected AINode<T> getChaserNode(int shootDistance, int shootCooldown, int meleeDistance, int searchDistance) {
            SequenceAINode sequence = new SequenceAINode();
            TargetFinderDistance targetFinder = new TargetFinderDistance(searchDistance);
            targetFinder.targetLostAddedDistance = searchDistance * 5;
            sequence.addChild(new TargetFinderAINode<T>(targetFinder){

                @Override
                public GameAreaStream<? extends Mob> streamPossibleTargets(T mob, Point base, TargetFinderDistance<T> distance) {
                    return TargetFinderAINode.streamPlayers(mob, base, distance);
                }
            });
            sequence.addChild(new PirateCaptainSecondStageChaserAINode());
            SelectorAINode selector = new SelectorAINode();
            selector.addChild(new ConditionAINode<PirateCaptainMob>(sequence, m -> m.inSecondStage, AINodeResult.FAILURE));
            selector.addChild(super.getChaserNode(shootDistance, shootCooldown, meleeDistance, searchDistance));
            return selector;
        }

        static /* synthetic */ float access$210(PirateCaptainAITree x0) {
            float f = x0.minionSpawnBuffer;
            x0.minionSpawnBuffer = f - 1.0f;
            return f;
        }
    }

    public static class PirateCaptainSecondStageChaserAINode<T extends PirateCaptainMob>
    extends AINode<T> {
        public String chaserTargetKey = "currentTarget";
        public long nextCannonShot = 0L;

        @Override
        protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
        }

        @Override
        public void init(T mob, Blackboard<T> blackboard) {
        }

        @Override
        public AINodeResult tick(T mob, Blackboard<T> blackboard) {
            Mob target = blackboard.getObject(Mob.class, this.chaserTargetKey);
            if (target != null) {
                if (!blackboard.mover.isCurrentlyMovingFor(this) || ((Mob)mob).hasArrivedAtTarget()) {
                    int nextAngle = GameRandom.globalRandom.nextInt(360);
                    Point2D.Float dir = GameMath.getAngleDir(nextAngle);
                    blackboard.mover.setCustomMovement(this, new MobMovementLevelPos(target.x + dir.x * 200.0f, target.y + dir.y * 200.0f));
                }
                if (this.nextCannonShot <= ((Entity)mob).getWorldEntity().getTime()) {
                    float healthPerc = (float)((Mob)mob).getHealth() / (float)((PirateCaptainMob)mob).getMaxHealth();
                    int cooldown = 1500 + (int)(healthPerc * 4000.0f);
                    this.nextCannonShot = ((Entity)mob).getWorldEntity().getTime() + (long)GameRandom.globalRandom.getIntBetween(cooldown, cooldown + 1000);
                    LinkedList<Float> firedAngles = new LinkedList<Float>();
                    List targets = GameUtils.streamServerClients(((Entity)mob).getLevel()).map(c -> c.playerMob).filter(t -> {
                        float distance = t.getDistance((Mob)mob);
                        return distance > 80.0f && distance <= (float)mob.shootDistance;
                    }).collect(Collectors.toList());
                    Collections.shuffle(targets);
                    for (PlayerMob player : targets) {
                        float angleToTarget = Projectile.getAngleToTarget(((PirateCaptainMob)mob).x, ((PirateCaptainMob)mob).y, player.x, player.y);
                        if (!firedAngles.stream().noneMatch(f -> Math.abs(GameMath.getAngleDifference(angleToTarget, f.floatValue())) < 60.0f)) continue;
                        int offset = 35;
                        ((PirateCaptainMob)mob).fireCannonAbility.runAndSend(player.getX() + GameRandom.globalRandom.getIntBetween(-offset, offset), player.getY() + GameRandom.globalRandom.getIntBetween(-offset, offset));
                        firedAngles.add(Float.valueOf(angleToTarget));
                    }
                }
                return AINodeResult.SUCCESS;
            }
            if (blackboard.mover.isCurrentlyMovingFor(this)) {
                blackboard.mover.stopMoving((Mob)mob);
            }
            return AINodeResult.FAILURE;
        }
    }
}

