/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile.bosses;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Stream;
import necesse.engine.achievements.AchievementManager;
import necesse.engine.eventStatusBars.EventStatusBarManager;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketLevelEventOver;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.MusicRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.SoundPlayer;
import necesse.engine.util.ComputedObjectValue;
import necesse.engine.util.GameLinkedList;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.gameAreaSearch.GameAreaStream;
import necesse.entity.Entity;
import necesse.entity.levelEvent.TempleEntranceEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.FireDanceLevelEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.LevelMob;
import necesse.entity.mobs.MaxHealthGetter;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.MobHealthScaling;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.WormMobHead;
import necesse.entity.mobs.WormMoveLine;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.composites.SequenceAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.RemoveOnNoTargetNode;
import necesse.entity.mobs.ai.behaviourTree.leaves.TargetFinderAINode;
import necesse.entity.mobs.ai.behaviourTree.util.FlyingAIMover;
import necesse.entity.mobs.ai.behaviourTree.util.TargetFinderDistance;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.BossNearbyBuff;
import necesse.entity.mobs.hostile.bosses.BossWormMobHead;
import necesse.entity.mobs.hostile.bosses.FlyingSpiritsBody;
import necesse.entity.mobs.mobMovement.MobMovementCircle;
import necesse.entity.mobs.mobMovement.MobMovementCircleLevelPos;
import necesse.entity.mobs.mobMovement.MobMovementCircleRelative;
import necesse.entity.mobs.mobMovement.MobMovementConstant;
import necesse.entity.mobs.mobMovement.MobMovementRelative;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.LootItemMultiplierIgnored;
import necesse.inventory.lootTable.lootItem.MobConditionLootItemList;
import necesse.inventory.lootTable.lootItem.RotationLootItem;
import necesse.level.maps.IncursionLevel;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.regionSystem.RegionPositionGetter;

public class FlyingSpiritsHead
extends BossWormMobHead<FlyingSpiritsBody, FlyingSpiritsHead> {
    private SoundPlayer sound;
    public static LootTable lootTable = new LootTable(new LootItemMultiplierIgnored(new ChanceLootItem(0.2f, "symphonyoftwinsvinyl")));
    public static RotationLootItem uniqueDrops = RotationLootItem.privateLootRotation((mob, client) -> client.characterStats().mob_kills.getKills("sageandgrit"), new LootItem("dragonsrebound"), new LootItem("dragonlance"), new LootItem("bowofdualism"), new LootItem("skeletonstaff"));
    public static LootTable privateLootTable = new LootTable(new MobConditionLootItemList(mob -> mob.getLevel() == null || !mob.getLevel().isIncursionLevel, uniqueDrops));
    public static float lengthPerBodyPart = 50.0f;
    public static float waveLength = 800.0f;
    public static final int totalBodyParts = 13;
    public static MaxHealthGetter BASE_MAX_HEALTH = new MaxHealthGetter(21000, 27000, 30000, 33000, 39000);
    public static MaxHealthGetter INCURSION_MAX_HEALTH = new MaxHealthGetter(35000, 46000, 52000, 58000, 69000);
    public GameDamage collisionDamage;
    public GameDamage fireDamage;
    public static GameDamage baseHeadCollisionDamage = new GameDamage(115.0f);
    public static GameDamage baseBodyCollisionDamage = new GameDamage(100.0f);
    public static GameDamage baseFireDamage = new GameDamage(100.0f);
    public static GameDamage incursionHeadCollisionDamage = new GameDamage(150.0f);
    public static GameDamage incursionBodyCollisionDamage = new GameDamage(130.0f);
    public static GameDamage incursionFireDamage = new GameDamage(115.0f);
    public final LevelMob<FlyingSpiritsHead> friend = new LevelMob();
    public Point pedestalPosition;
    protected MobHealthScaling scaling = new MobHealthScaling(this);
    protected Variant variant;
    protected boolean isEnraged;
    protected boolean isEscaping;

    public FlyingSpiritsHead(Variant variant) {
        super(100, waveLength, 100.0f, 13, 25.0f, -5.0f);
        this.variant = variant;
        this.difficultyChanges.setMaxHealth(BASE_MAX_HEALTH);
        this.moveAccuracy = 100;
        this.movementUpdateCooldown = 2000;
        this.movePosTolerance = 700.0f;
        this.setSpeed(180.0f);
        this.setArmor(35);
        this.accelerationMod = 1.0f;
        this.decelerationMod = 1.0f;
        this.collision = new Rectangle(-25, -20, 50, 40);
        this.hitBox = new Rectangle(-30, -25, 60, 50);
        this.selectBox = new Rectangle(-40, -60, 80, 64);
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.friend.uniqueID = reader.getNextInt();
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextInt(this.friend.uniqueID);
    }

    @Override
    public void setupMovementPacket(PacketWriter writer) {
        super.setupMovementPacket(writer);
        writer.putNextBoolean(this.isEnraged);
        writer.putNextBoolean(this.isEscaping);
    }

    @Override
    public void applyMovementPacket(PacketReader reader, boolean isDirect) {
        super.applyMovementPacket(reader, isDirect);
        this.isEnraged = reader.getNextBoolean();
        this.isEscaping = reader.getNextBoolean();
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
    protected void onAppearAbility() {
        super.onAppearAbility();
        if (this.isClient()) {
            SoundManager.playSound(GameResources.roar, SoundEffect.globalEffect().pitch(1.2f));
        }
    }

    @Override
    protected float getDistToBodyPart(FlyingSpiritsBody bodyPart, int index, float lastDistance) {
        if (index == 1) {
            return lengthPerBodyPart + 10.0f;
        }
        return lengthPerBodyPart;
    }

    @Override
    protected FlyingSpiritsBody createNewBodyPart(int index) {
        FlyingSpiritsBody bodyPart = new FlyingSpiritsBody();
        int tailParts = 4;
        if (index == 0) {
            bodyPart.spriteY = 1;
        } else if (index == 2) {
            bodyPart.spriteY = 3;
        } else if (index == 13 - tailParts - 2) {
            bodyPart.spriteY = 3;
        } else if (index >= 13 - tailParts) {
            bodyPart.spawnsParticles = true;
            int tailPart = Math.abs(13 - index - tailParts);
            bodyPart.spriteY = 4 + tailPart;
        } else {
            bodyPart.spriteY = 1;
        }
        return bodyPart;
    }

    @Override
    protected void playMoveSound() {
    }

    @Override
    public LootTable getLootTable() {
        return lootTable;
    }

    @Override
    public LootTable getPrivateLootTable() {
        return privateLootTable;
    }

    @Override
    public boolean dropsLoot() {
        if (this.friend.get(this.getLevel()) != null) {
            return false;
        }
        return super.dropsLoot();
    }

    @Override
    public GameDamage getCollisionDamage(Mob target, boolean fromPacket, ServerClient packetSubmitter) {
        return baseHeadCollisionDamage;
    }

    @Override
    public boolean canCollisionHit(Mob target) {
        return this.height < 45.0f && super.canCollisionHit(target);
    }

    @Override
    public int getMaxHealth() {
        return super.getMaxHealth() + (int)((float)(this.scaling == null ? 0 : this.scaling.getHealthIncrease()) * this.getMaxHealthModifier());
    }

    @Override
    public void init() {
        if (this.getLevel() instanceof IncursionLevel) {
            this.difficultyChanges.setMaxHealth(INCURSION_MAX_HEALTH);
            this.setHealth(this.getMaxHealth());
            this.collisionDamage = incursionHeadCollisionDamage;
            this.fireDamage = incursionFireDamage;
        } else {
            this.collisionDamage = baseHeadCollisionDamage;
            this.fireDamage = baseFireDamage;
        }
        super.init();
        this.ai = new BehaviourTreeAI<FlyingSpiritsHead>(this, new FlyingSpiritsHeadAI(), new FlyingAIMover());
        if (this.isClient()) {
            // empty if block
        }
        this.streamBodyParts().forEach(bp -> {
            bp.variant = this.variant;
        });
    }

    @Override
    public float getTurnSpeed(float delta) {
        return super.getTurnSpeed(delta) * (this.isEnraged ? 2.0f : 1.0f);
    }

    @Override
    public void clientTick() {
        super.clientTick();
        if (this.sound == null || this.sound.isDone()) {
            this.sound = SoundManager.playSound(GameResources.wind1, (SoundEffect)SoundEffect.effect(this).falloffDistance(1400).volume(0.4f));
        }
        if (this.sound != null) {
            this.sound.refreshLooping(1.0f);
        }
        if (!this.isEscaping && this.isClientPlayerNearby()) {
            SoundManager.setMusic(MusicRegistry.SymphonyOfTwins, SoundManager.MusicPriority.EVENT, 1.5f);
            EventStatusBarManager.registerMobHealthStatusBar(this);
        }
        BossNearbyBuff.applyAround(this);
        this.setSpeed(this.isEscaping || this.isEnraged ? 250.0f : 180.0f);
        this.accelerationMod = this.isEnraged ? 1.3f : 1.0f;
        this.decelerationMod = this.isEnraged ? 1.3f : 1.0f;
    }

    @Override
    public void serverTick() {
        FlyingSpiritsHead other;
        super.serverTick();
        this.scaling.serverTick();
        BossNearbyBuff.applyAround(this);
        if (!this.isEnraged && ((other = this.friend.get(this.getLevel())) == null || this.getDistance(other) > 3840.0f)) {
            this.isEnraged = true;
            this.sendMovementPacket(false);
        }
        this.setSpeed(this.isEscaping || this.isEnraged ? 280.0f : 180.0f);
        this.accelerationMod = this.isEnraged ? 1.3f : 1.0f;
        this.decelerationMod = this.isEnraged ? 1.3f : 1.0f;
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), MobRegistry.Textures.flyingSpirits, 4 + this.variant.spriteX, GameRandom.globalRandom.nextInt(6), 64, this.x, this.y, 20.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
    }

    @Override
    protected void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        MobDrawable shoulderDrawable;
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        if (!this.isVisible()) {
            return;
        }
        GameLight light = level.getLightLevel(this);
        int drawX = camera.getDrawX(this.x) - 64;
        int drawY = camera.getDrawY(this.y);
        float headAngle = GameMath.fixAngle(GameMath.getAngle(new Point2D.Float(this.dx, this.dy)));
        final MobDrawable headDrawable = WormMobHead.getAngledDrawable(new GameSprite(MobRegistry.Textures.flyingSpirits, this.variant.spriteX, 0, 128), null, light, (int)this.height, headAngle, drawX, drawY, 96);
        ComputedObjectValue<Object, Double> shoulderLine = new ComputedObjectValue<Object, Double>(null, () -> 0.0);
        shoulderLine = WormMobHead.moveDistance(this.moveLines.getFirstElement(), 70.0);
        if (shoulderLine.object != null) {
            Point2D.Double shoulderPos = WormMobHead.linePos(shoulderLine);
            GameLight shoulderLight = level.getLightLevel(FlyingSpiritsHead.getTileCoordinate(shoulderPos.x), FlyingSpiritsHead.getTileCoordinate(shoulderPos.y));
            int shoulderDrawX = camera.getDrawX((float)shoulderPos.x) - 64;
            int shoulderDrawY = camera.getDrawY((float)shoulderPos.y);
            float shoulderHeight = this.getWaveHeight(((WormMoveLine)((GameLinkedList.Element)shoulderLine.object).object).movedDist + ((Double)shoulderLine.get()).floatValue());
            float shoulderAngle = GameMath.fixAngle((float)GameMath.getAngle(new Point2D.Double((double)this.x - shoulderPos.x, (double)(this.y - this.height) - (shoulderPos.y - (double)shoulderHeight))));
            shoulderDrawable = WormMobHead.getAngledDrawable(new GameSprite(MobRegistry.Textures.flyingSpirits, this.variant.spriteX, 1, 128), null, shoulderLight, (int)shoulderHeight, shoulderAngle, shoulderDrawX, shoulderDrawY, 96);
        } else {
            shoulderDrawable = null;
        }
        topList.add(new MobDrawable(){

            @Override
            public void draw(TickManager tickManager) {
                if (shoulderDrawable != null) {
                    shoulderDrawable.draw(tickManager);
                }
                headDrawable.draw(tickManager);
            }
        });
        this.addShadowDrawables(tileList, level, x, y, light, camera);
    }

    @Override
    protected TextureDrawOptions getShadowDrawOptions(Level level, int x, int y, GameLight light, GameCamera camera) {
        GameTexture shadowTexture = MobRegistry.Textures.swampGuardian_shadow;
        int res = shadowTexture.getHeight();
        int drawX = camera.getDrawX(x) - res / 2;
        int drawY = camera.getDrawY(y) - res / 2;
        return shadowTexture.initDraw().sprite(2, 0, res).light(light).pos(drawX, drawY += this.getBobbing(x, y));
    }

    @Override
    public boolean shouldDrawOnMap() {
        return this.isVisible();
    }

    @Override
    public void drawOnMap(TickManager tickManager, Client client, int x, int y, double tileScale, Rectangle drawBounds, boolean isMinimap) {
        super.drawOnMap(tickManager, client, x, y, tileScale, drawBounds, isMinimap);
        int drawX = x - 24;
        int drawY = y - 24;
        float headAngle = GameMath.fixAngle(GameMath.getAngle(new Point2D.Float(this.dx, this.dy)));
        MobRegistry.Textures.flyingSpirits.initDraw().sprite(this.variant.spriteX, 0, 128).rotate(headAngle + 90.0f, 24, 24).size(48, 48).draw(drawX, drawY);
    }

    @Override
    public Rectangle drawOnMapBox(double tileScale, boolean isMinimap) {
        return new Rectangle(-15, -15, 30, 30);
    }

    @Override
    public GameTooltips getMapTooltips() {
        if (!this.isVisible()) {
            return null;
        }
        return new StringTooltips(this.getDisplayName() + " " + this.getHealth() + "/" + this.getMaxHealth());
    }

    @Override
    public Stream<ModifierValue<?>> getDefaultModifiers() {
        return Stream.of(new ModifierValue<Float>(BuffModifiers.SLOW, Float.valueOf(0.0f)).max(Float.valueOf(0.0f)), new ModifierValue<Float>(BuffModifiers.POISON_DAMAGE_FLAT, Float.valueOf(0.0f)).max(Float.valueOf(0.0f)), new ModifierValue<Float>(BuffModifiers.FIRE_DAMAGE_FLAT, Float.valueOf(0.0f)).max(Float.valueOf(0.0f)), new ModifierValue<Float>(BuffModifiers.FROST_DAMAGE_FLAT, Float.valueOf(0.0f)).max(Float.valueOf(0.0f)));
    }

    @Override
    protected void onDeath(Attacker attacker, HashSet<Attacker> attackers) {
        super.onDeath(attacker, attackers);
        boolean defeatedOther = this.friend.get(this.getLevel()) == null;
        boolean openingStaircase = false;
        if (defeatedOther && this.isServer()) {
            if (this.pedestalPosition != null && !(this.getLevel() instanceof IncursionLevel)) {
                Point entrancePosition = new Point(this.pedestalPosition.x, this.pedestalPosition.y + 2);
                if (!this.getLevel().getLevelObject(entrancePosition.x, entrancePosition.y).getMultiTile().getMasterObject().getStringID().equals("templeentrance")) {
                    this.getLevel().entityManager.events.add(new TempleEntranceEvent(this.pedestalPosition.x, this.pedestalPosition.y + 2));
                    openingStaircase = true;
                }
            }
            if (!this.isDamagedByPlayers) {
                AchievementManager.checkMeAndThisArmyKill(this.getLevel(), attackers);
            }
        }
        boolean finalOpeningStaircase = openingStaircase;
        attackers.stream().map(Attacker::getFirstPlayerOwner).filter(Objects::nonNull).filter(PlayerMob::isServerClient).map(PlayerMob::getServerClient).distinct().forEach(c -> {
            if (defeatedOther) {
                c.newStats.mob_kills.addKill("sageandgrit");
            }
            c.sendChatMessage(new LocalMessage("misc", "bossdefeat", "name", this.getLocalization()));
            if (finalOpeningStaircase) {
                c.sendChatMessage(new LocalMessage("misc", "staircaseopening"));
            }
        });
    }

    public boolean controlsAI() {
        return this.variant == Variant.GRIT || this.isEnraged();
    }

    public boolean isEnraged() {
        return this.isEnraged;
    }

    public boolean isEscaping() {
        return this.isEscaping;
    }

    public static enum Variant {
        GRIT(0, 20.0f, new Color(184, 102, 40)),
        SAGE(1, 200.0f, new Color(65, 105, 151));

        public final int spriteX;
        public final float particleHue;
        public final Color fireColor;

        private Variant(int spriteX, float particleHue, Color fireColor) {
            this.spriteX = spriteX;
            this.particleHue = particleHue;
            this.fireColor = fireColor;
        }
    }

    public static class FlyingSpiritsHeadAI<T extends FlyingSpiritsHead>
    extends SequenceAINode<T> {
        public FlyingSpiritsHeadAINode<T> node;

        public FlyingSpiritsHeadAI() {
            this.addChild(new RemoveOnNoTargetNode(100));
            TargetFinderAINode targetFinder = new TargetFinderAINode<T>(3200){

                @Override
                public GameAreaStream<? extends Mob> streamPossibleTargets(T mob, Point base, TargetFinderDistance<T> distance) {
                    return TargetFinderAINode.streamPlayers(mob, base, distance);
                }
            };
            this.addChild(targetFinder);
            targetFinder.moveToAttacker = false;
            this.node = new FlyingSpiritsHeadAINode();
            this.addChild(this.node);
        }
    }

    public static class FlyingSpiritsHeadAINode<T extends FlyingSpiritsHead>
    extends AINode<T> {
        public String targetKey = "currentTarget";
        private int startMoveAccuracy;
        protected MState state;
        protected Mob chargingTarget;
        protected float circlingAngleOffset;
        protected float circlingSpeed;
        protected boolean circlingReversed;
        protected boolean lastWasCharge;
        protected float fireDanceTargetX;
        protected float fireDanceTargetY;
        protected long fireDanceStateTime;
        protected boolean fireDanceActive;
        protected FireDanceLevelEvent fireDanceLevelEvent;
        public long nextStateTime;
        public int circlingRange = 500;
        public int fireDanceRange = 300;

        @Override
        protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
            this.startMoveAccuracy = ((FlyingSpiritsHead)mob).moveAccuracy;
        }

        @Override
        public void init(T mob, Blackboard<T> blackboard) {
        }

        public float getHealthPercent() {
            float myHealthPercent = GameMath.limit((float)((FlyingSpiritsHead)this.mob()).getHealth() / (float)((FlyingSpiritsHead)this.mob()).getMaxHealth(), 0.0f, 1.0f);
            FlyingSpiritsHead friend = ((FlyingSpiritsHead)this.mob()).friend.get(((FlyingSpiritsHead)this.mob()).getLevel());
            if (friend != null) {
                float friendHealthPercent = GameMath.limit((float)friend.getHealth() / (float)friend.getMaxHealth(), 0.0f, 1.0f);
                return (myHealthPercent + friendHealthPercent) / 2.0f;
            }
            return myHealthPercent;
        }

        public int getRandomCooldownBasedOnMissingHealth(int minimum, int maximum, int range) {
            float healthPercent = this.getHealthPercent();
            int totalRange = maximum - minimum;
            range = GameMath.limit(range, 1, Math.abs(totalRange));
            if (totalRange < 0) {
                range = -range;
            }
            int added = (int)(healthPercent * (float)(totalRange - range));
            int randomInt = range < 0 ? -GameRandom.globalRandom.nextInt(-range + 1) : GameRandom.globalRandom.nextInt(range + 1);
            return minimum + added + randomInt;
        }

        @Override
        public AINodeResult tick(T mob, Blackboard<T> blackboard) {
            if (((FlyingSpiritsHead)mob).isEscaping()) {
                blackboard.mover.setCustomMovement(this, new MobMovementConstant(((FlyingSpiritsHead)mob).dx, ((FlyingSpiritsHead)mob).dy));
                return AINodeResult.SUCCESS;
            }
            if (this.state == null) {
                this.startCircling();
                this.nextStateTime = ((Entity)mob).getWorldEntity().getTime() + 15000L;
            } else if (this.state == MState.Charging) {
                if (this.chargingTarget != null && !this.chargingTarget.removed()) {
                    float distance = ((Mob)mob).getDistance(this.chargingTarget);
                    if (distance < (float)this.circlingRange / 4.0f) {
                        float maxAngle;
                        float currentAngle = GameMath.getAngle(new Point2D.Float(((FlyingSpiritsHead)mob).dx, ((FlyingSpiritsHead)mob).dy));
                        float targetAngle = GameMath.getAngle(new Point2D.Float(this.chargingTarget.x - ((FlyingSpiritsHead)mob).x, this.chargingTarget.y - ((FlyingSpiritsHead)mob).y));
                        float diff = GameMath.getAngleDifference(currentAngle, targetAngle);
                        float f = maxAngle = ((FlyingSpiritsHead)mob).isEnraged() ? 20.0f : 40.0f;
                        if (Math.abs(diff) >= maxAngle && distance > 75.0f || ((FlyingSpiritsHead)mob).dx == 0.0f && ((FlyingSpiritsHead)mob).dy == 0.0f) {
                            ((FlyingSpiritsHead)mob).moveAccuracy = this.startMoveAccuracy;
                            this.startCircling();
                        }
                    }
                } else {
                    this.startCircling();
                }
            } else if (this.state == MState.FireDance) {
                boolean anyTargetsInsideArea;
                if (this.fireDanceStateTime < ((Entity)mob).getWorldEntity().getTime()) {
                    if (this.fireDanceActive) {
                        this.startCircling();
                    } else {
                        this.startFireDanceLaser();
                    }
                } else if (((FlyingSpiritsHead)this.mob()).controlsAI() && !(anyTargetsInsideArea = ((Entity)mob).getLevel().entityManager.players.streamArea(this.fireDanceTargetX, this.fireDanceTargetY, this.fireDanceRange).anyMatch(m -> {
                    if (m == null || m.removed() || !m.isVisible()) {
                        return false;
                    }
                    return m.getDistance(this.fireDanceTargetX, this.fireDanceTargetY) <= (float)this.fireDanceRange;
                }))) {
                    this.startCircling();
                    this.friendAI(FlyingSpiritsHeadAINode::startCircling);
                }
            }
            if (((FlyingSpiritsHead)mob).controlsAI()) {
                FlyingSpiritsHeadAINode<?> friendAI = this.getFriendAI();
                if (this.state == MState.Circling && (friendAI == null || friendAI.state == MState.Circling) && this.nextStateTime < ((Entity)mob).getWorldEntity().getTime()) {
                    if (((FlyingSpiritsHead)mob).isEnraged() || !this.lastWasCharge || GameRandom.globalRandom.nextBoolean()) {
                        this.lastWasCharge = true;
                        this.startCharging();
                    } else {
                        this.lastWasCharge = false;
                        this.startFireDance();
                    }
                    this.nextStateTime = ((Entity)mob).getWorldEntity().getTime() + 4000L;
                }
            }
            return AINodeResult.SUCCESS;
        }

        public Mob getTarget() {
            return this.getBlackboard().getObject(Mob.class, this.targetKey);
        }

        public void friendAI(Consumer<FlyingSpiritsHeadAINode<?>> consumer) {
            FlyingSpiritsHeadAINode<?> friendAI = this.getFriendAI();
            if (friendAI != null) {
                consumer.accept(friendAI);
            }
        }

        public FlyingSpiritsHeadAINode<?> getFriendAI() {
            FlyingSpiritsHead friend = ((FlyingSpiritsHead)this.mob()).friend.get(((FlyingSpiritsHead)this.mob()).getLevel());
            if (friend != null && !friend.isEnraged()) {
                return ((FlyingSpiritsHeadAI)friend.ai.tree).node;
            }
            return null;
        }

        public void startCircling() {
            this.stopExistingFireDance();
            Mob target = this.getBlackboard().getObject(Mob.class, this.targetKey);
            ((FlyingSpiritsHead)this.mob()).moveAccuracy = this.startMoveAccuracy;
            this.state = MState.Circling;
            this.nextStateTime = ((FlyingSpiritsHead)this.mob()).isEnraged() ? ((FlyingSpiritsHead)this.mob()).getWorldEntity().getTime() + (long)this.getRandomCooldownBasedOnMissingHealth(1000, 4000, 1000) : ((FlyingSpiritsHead)this.mob()).getWorldEntity().getTime() + (long)this.getRandomCooldownBasedOnMissingHealth(2000, 6000, 1000);
            FlyingSpiritsHeadAINode<?> friendAI = this.getFriendAI();
            if (friendAI != null && friendAI.state == MState.Circling) {
                this.circlingReversed = friendAI.circlingReversed;
                this.circlingAngleOffset = friendAI.circlingAngleOffset + 180.0f;
                this.circlingSpeed = friendAI.circlingSpeed;
                if (target != null) {
                    this.getBlackboard().mover.setCustomMovement(this, new MobMovementCircleRelative((Mob)this.mob(), target, this.circlingRange, this.circlingSpeed, this.circlingAngleOffset, this.circlingReversed));
                } else {
                    this.getBlackboard().mover.setCustomMovement(this, new MobMovementCircleLevelPos((Mob)this.mob(), ((FlyingSpiritsHead)this.mob()).x, ((FlyingSpiritsHead)this.mob()).y, this.circlingRange, this.circlingSpeed, this.circlingAngleOffset, this.circlingReversed));
                }
            } else {
                this.circlingSpeed = MobMovementCircle.convertToRotSpeed(this.circlingRange, ((FlyingSpiritsHead)this.mob()).getSpeed()) * 1.1f;
                MobMovementCircle movement = target != null ? new MobMovementCircleRelative((Mob)this.mob(), target, this.circlingRange, this.circlingSpeed, GameRandom.globalRandom.nextBoolean()) : new MobMovementCircleLevelPos((Mob)this.mob(), ((FlyingSpiritsHead)this.mob()).x, ((FlyingSpiritsHead)this.mob()).y, this.circlingRange, this.circlingSpeed, GameRandom.globalRandom.nextBoolean());
                this.circlingReversed = movement.reversed;
                this.circlingAngleOffset = movement.angleOffset;
                this.getBlackboard().mover.setCustomMovement(this, movement);
            }
        }

        public void startCharging() {
            Mob target = this.getTarget();
            if (target != null) {
                ((FlyingSpiritsHead)this.mob()).moveAccuracy = 5;
                this.state = MState.Charging;
                this.chargingTarget = target;
                this.getBlackboard().mover.setCustomMovement(this, new MobMovementRelative(target, 0.0f, 0.0f));
            }
            if (((FlyingSpiritsHead)this.mob()).controlsAI()) {
                this.friendAI(FlyingSpiritsHeadAINode::startCharging);
            }
        }

        public void startFireDance() {
            Mob target = this.getTarget();
            if (target != null) {
                float speed = MobMovementCircle.convertToRotSpeed(this.fireDanceRange, ((FlyingSpiritsHead)this.mob()).getSpeed()) * 1.1f;
                float angleOffset = new MobMovementCircleLevelPos(this.mob(), (float)target.x, (float)target.y, (int)this.fireDanceRange, (float)speed, (boolean)this.circlingReversed).angleOffset;
                this.startFireDance(target.x, target.y, this.fireDanceRange, speed, angleOffset, this.circlingReversed);
                if (((FlyingSpiritsHead)this.mob()).controlsAI()) {
                    this.friendAI(ai -> ai.startFireDance(target.x, target.y, this.fireDanceRange, speed, angleOffset + 180.0f, this.circlingReversed));
                }
            }
        }

        public void startFireDanceLaser() {
            if (((FlyingSpiritsHead)this.mob()).controlsAI()) {
                int fireTime = this.getRandomCooldownBasedOnMissingHealth(3000, 6000, 1000);
                this.fireDanceStateTime = ((FlyingSpiritsHead)this.mob()).getWorldEntity().getTime() + (long)fireTime;
                ((FlyingSpiritsHead)this.mob()).buffManager.addBuff(new ActiveBuff(BuffRegistry.HARDENED, (Mob)this.mob(), fireTime, null), true, true);
                this.stopExistingFireDance();
                this.fireDanceLevelEvent = new FireDanceLevelEvent((Mob)this.mob(), new GameRandom(), this.fireDanceTargetX, this.fireDanceTargetY, ((FlyingSpiritsHead)this.mob()).fireDamage, 100, ((FlyingSpiritsHead)this.mob()).variant.fireColor, fireTime);
                ((FlyingSpiritsHead)this.mob()).getLevel().entityManager.events.add(this.fireDanceLevelEvent);
                this.fireDanceActive = true;
                this.friendAI(ai -> {
                    ai.fireDanceStateTime = this.fireDanceStateTime;
                    ai.fireDanceActive = true;
                    ((FlyingSpiritsHead)ai.mob()).buffManager.addBuff(new ActiveBuff(BuffRegistry.HARDENED, (Mob)ai.mob(), fireTime, null), true, true);
                    ai.stopExistingFireDance();
                    ai.fireDanceLevelEvent = new FireDanceLevelEvent((Mob)ai.mob(), new GameRandom(), this.fireDanceTargetX, this.fireDanceTargetY, ((FlyingSpiritsHead)this.mob()).fireDamage, 100, ((FlyingSpiritsHead)ai.mob()).variant.fireColor, fireTime);
                    ((FlyingSpiritsHead)ai.mob()).getLevel().entityManager.events.add(ai.fireDanceLevelEvent);
                });
            } else {
                this.fireDanceStateTime = ((FlyingSpiritsHead)this.mob()).getWorldEntity().getTime() + 2000L;
                this.fireDanceActive = true;
            }
        }

        public void stopExistingFireDance() {
            if (this.fireDanceLevelEvent != null) {
                this.fireDanceLevelEvent.over();
                if (((FlyingSpiritsHead)this.mob()).isServer()) {
                    ((FlyingSpiritsHead)this.mob()).getLevel().getServer().network.sendToClientsWithEntity(new PacketLevelEventOver(this.fireDanceLevelEvent.getUniqueID()), (RegionPositionGetter)this.mob());
                }
                this.fireDanceLevelEvent = null;
            }
        }

        public void startFireDance(float x, float y, int range, float speed, float angleOffset, boolean circlingReversed) {
            this.fireDanceTargetX = x;
            this.fireDanceTargetY = y;
            this.state = MState.FireDance;
            this.fireDanceActive = false;
            this.fireDanceStateTime = ((FlyingSpiritsHead)this.mob()).getWorldEntity().getTime() + 3500L;
            this.getBlackboard().mover.setCustomMovement(this, new MobMovementCircleLevelPos((Mob)this.mob(), x, y, range, speed, angleOffset, circlingReversed));
        }

        private static enum MState {
            Circling,
            Charging,
            FireDance;

        }
    }
}

