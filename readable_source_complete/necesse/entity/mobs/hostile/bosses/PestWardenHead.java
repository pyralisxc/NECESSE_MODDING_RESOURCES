/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile.bosses;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import necesse.engine.achievements.AchievementManager;
import necesse.engine.eventStatusBars.EventStatusBarManager;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.MusicRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.SoundSettings;
import necesse.engine.sound.SoundSettingsRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.gameAreaSearch.GameAreaStream;
import necesse.entity.Entity;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.MaxHealthGetter;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.MobHealthScaling;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.WormMobHead;
import necesse.entity.mobs.WormMoveLine;
import necesse.entity.mobs.WormMoveLineSpawnData;
import necesse.entity.mobs.ability.BooleanMobAbility;
import necesse.entity.mobs.ability.FloatMobAbility;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.composites.SelectorAINode;
import necesse.entity.mobs.ai.behaviourTree.composites.SequenceAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.ChargingCirclingChaserAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.RemoveOnNoTargetNode;
import necesse.entity.mobs.ai.behaviourTree.leaves.TargetFinderAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.WandererAINode;
import necesse.entity.mobs.ai.behaviourTree.util.FlyingAIMover;
import necesse.entity.mobs.ai.behaviourTree.util.TargetFinderDistance;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.BossNearbyBuff;
import necesse.entity.mobs.hostile.bosses.BossWormMobHead;
import necesse.entity.mobs.hostile.bosses.PestWardenBody;
import necesse.entity.mobs.hostile.bosses.PestWardenMoveLine;
import necesse.entity.mobs.mobMovement.MobMovement;
import necesse.entity.mobs.mobMovement.MobMovementRelative;
import necesse.entity.mobs.mobMovement.MobMovementSpiral;
import necesse.entity.mobs.mobMovement.MobMovementSpiralLevelPos;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.inventory.lootTable.lootItem.ConditionLootItem;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.LootItemMultiplierIgnored;
import necesse.inventory.lootTable.lootItem.MobConditionLootItemList;
import necesse.inventory.lootTable.lootItem.RotationLootItem;
import necesse.level.maps.IncursionLevel;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class PestWardenHead
extends BossWormMobHead<PestWardenBody, PestWardenHead> {
    public static LootTable lootTable = new LootTable(new LootItemMultiplierIgnored(new ChanceLootItem(0.2f, "pestwardenschargevinyl")));
    public static RotationLootItem uniqueDrops = RotationLootItem.privateLootRotation(new LootItem("venomshower"), new LootItem("venomslasher"), new LootItem("livingshotty"), new LootItem("swampsgrasp"));
    public static LootTable privateLootTable = new LootTable(new MobConditionLootItemList(mob -> mob.getLevel() == null || !mob.getLevel().isIncursionLevel, new ConditionLootItem("wardenheart", (r, o) -> {
        ServerClient client = LootTable.expectExtra(ServerClient.class, o, 1);
        return client != null && client.playerMob.healthUpgradeManager.canUpgrade("wardenheart") && client.playerMob.getInv().getAmount(ItemRegistry.getItem("wardenheart"), false, false, true, true, "have") == 0;
    }), uniqueDrops));
    public static MaxHealthGetter BASE_MAX_HEALTH = new MaxHealthGetter(24000, 38000, 45000, 50000, 58000);
    public static MaxHealthGetter INCURSION_MAX_HEALTH = new MaxHealthGetter(58000, 81000, 92000, 104000, 125000);
    public static float lengthPerBodyPart = 35.0f;
    public static float waveLength = 500.0f;
    public static final int totalBodyParts = 150;
    protected MobHealthScaling scaling = new MobHealthScaling(this);
    protected float temporarySpeed;
    protected FloatMobAbility setTemporarySpeed;
    protected boolean isHardened;
    protected BooleanMobAbility setHardened;
    public GameDamage collisionDamage;
    public static GameDamage baseHeadCollisionDamage = new GameDamage(95.0f);
    public static GameDamage baseBodyCollisionDamage = new GameDamage(75.0f);
    public static GameDamage incursionHeadCollisionDamage = new GameDamage(150.0f);
    public static GameDamage incursionBodyCollisionDamage = new GameDamage(130.0f);

    public PestWardenHead() {
        super(100, waveLength, 80.0f, 150, 0.0f, 0.0f);
        this.difficultyChanges.setMaxHealth(BASE_MAX_HEALTH);
        this.moveAccuracy = 160;
        this.setSpeed(150.0f);
        this.setArmor(30);
        this.accelerationMod = 1.0f;
        this.decelerationMod = 1.0f;
        this.collision = new Rectangle(-35, -20, 70, 40);
        this.hitBox = new Rectangle(-40, -25, 80, 50);
        this.selectBox = new Rectangle(-50, -80, 100, 100);
        this.setTemporarySpeed = this.registerAbility(new FloatMobAbility(){

            @Override
            protected void run(float value) {
                PestWardenHead.this.temporarySpeed = value;
            }
        });
        this.setHardened = this.registerAbility(new BooleanMobAbility(){

            @Override
            protected void run(boolean value) {
                PestWardenHead.this.isHardened = value;
            }
        });
    }

    @Override
    public void setupMovementPacket(PacketWriter writer) {
        super.setupMovementPacket(writer);
        writer.putNextFloat(this.temporarySpeed);
        writer.putNextBoolean(this.isHardened);
    }

    @Override
    public void applyMovementPacket(PacketReader reader, boolean isDirect) {
        super.applyMovementPacket(reader, isDirect);
        this.temporarySpeed = reader.getNextFloat();
        this.isHardened = reader.getNextBoolean();
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
    protected float getDistToBodyPart(PestWardenBody bodyPart, int index, float lastDistance) {
        if (index == 0) {
            return Math.max(lengthPerBodyPart - 5.0f, 5.0f);
        }
        if (index >= 146) {
            int sprite = index - 146 + 1;
            return Math.max(lengthPerBodyPart - (float)(sprite * 5), 5.0f);
        }
        return lengthPerBodyPart;
    }

    @Override
    protected PestWardenBody createNewBodyPart(int index) {
        PestWardenBody bodyPart = new PestWardenBody();
        bodyPart.index = index;
        bodyPart.sharesHitCooldownWithNext = index % 5 < 4;
        boolean bl = bodyPart.relaysBuffsToNext = index % 5 < 4;
        if (index >= 146) {
            int sprite = index - 146 + 2;
            bodyPart.sprite = new Point(sprite, 0);
            bodyPart.shadowSprite = sprite;
            bodyPart.showLegs = false;
        } else {
            int sprite = index % 2;
            bodyPart.sprite = new Point(sprite, 0);
            bodyPart.shadowSprite = sprite;
            bodyPart.showLegs = true;
        }
        return bodyPart;
    }

    @Override
    public void init() {
        if (this.getLevel() instanceof IncursionLevel) {
            this.difficultyChanges.setMaxHealth(INCURSION_MAX_HEALTH);
            this.setHealth(this.getMaxHealth());
            this.collisionDamage = incursionHeadCollisionDamage;
        } else {
            this.collisionDamage = baseHeadCollisionDamage;
        }
        super.init();
        this.ai = new BehaviourTreeAI<PestWardenHead>(this, new PestWardenAI(), new FlyingAIMover());
        if (this.isClient()) {
            SoundManager.playSound(GameResources.pestwardenbegin, (SoundEffect)SoundEffect.effect(this).volume(1.3f).falloffDistance(4000));
        }
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
    protected void playMoveSound() {
        SoundManager.playSound(SoundSettingsRegistry.crawlerFootsteps, this);
    }

    @Override
    protected SoundSettings getHurtSound() {
        return new SoundSettings(GameResources.pestwardenhurt).volume(0.2f).pitchVariance(0.04f).fallOffDistance(1500);
    }

    @Override
    protected SoundSettings getDeathSound() {
        return new SoundSettings(GameResources.pestwardendeath).pitchVariance(0.0f).fallOffDistance(3500);
    }

    @Override
    public GameDamage getCollisionDamage(Mob target, boolean fromPacket, ServerClient packetSubmitter) {
        return this.collisionDamage;
    }

    @Override
    public int getCollisionKnockback(Mob target) {
        return 150;
    }

    @Override
    public int getMaxHealth() {
        return super.getMaxHealth() + (int)((float)(this.scaling == null ? 0 : this.scaling.getHealthIncrease()) * this.getMaxHealthModifier());
    }

    @Override
    public void clientTick() {
        super.clientTick();
        if (this.isClientPlayerNearby()) {
            SoundManager.setMusic(MusicRegistry.PestWardensCharge, SoundManager.MusicPriority.EVENT, 1.0f);
            EventStatusBarManager.registerMobHealthStatusBar(this);
        }
        BossNearbyBuff.applyAround(this);
        if (this.temporarySpeed > 0.0f) {
            this.setSpeed(this.temporarySpeed);
        } else {
            float healthPerc = (float)this.getHealth() / (float)this.getMaxHealth();
            float mod = Math.abs((float)Math.pow(healthPerc, 0.2f) - 1.0f);
            this.setSpeed(140.0f + mod * 300.0f);
        }
    }

    @Override
    public void serverTick() {
        super.serverTick();
        this.scaling.serverTick();
        BossNearbyBuff.applyAround(this);
        if (this.temporarySpeed > 0.0f) {
            this.setSpeed(this.temporarySpeed);
        } else {
            float healthPerc = (float)this.getHealth() / (float)this.getMaxHealth();
            float mod = Math.abs((float)Math.pow(healthPerc, 0.2f) - 1.0f);
            this.setSpeed(140.0f + mod * 300.0f);
        }
    }

    @Override
    public WormMoveLine newMoveLine(Point2D lastPos, Point2D newPos, boolean isMoveJump, float movedDist, boolean isUnderground) {
        return new PestWardenMoveLine(lastPos, newPos, isMoveJump, movedDist, isUnderground, this.isHardened);
    }

    @Override
    public WormMoveLine readMoveLine(PacketReader reader, WormMoveLineSpawnData data) {
        return new PestWardenMoveLine(reader, data);
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), MobRegistry.Textures.pestWarden, 8 + GameRandom.globalRandom.nextInt(6), 4, 64, this.x, this.y, 20.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
    }

    @Override
    protected void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        if (!this.isVisible()) {
            return;
        }
        GameLight light = level.getLightLevel(this);
        int drawX = camera.getDrawX(x) - 64;
        int drawY = camera.getDrawY(y);
        float headAngle = GameMath.fixAngle(GameMath.getAngle(new Point2D.Float(this.dx, this.dy)));
        Point2D.Float headDir = GameMath.getAngleDir(headAngle);
        int eyeSprite = (int)(headAngle / 45.0f);
        eyeSprite = (eyeSprite + 2) % 8;
        float eyeDrawX = (float)(drawX + 64 - 32) + headDir.x * 8.0f;
        float eyeDrawY = (float)(drawY - 96 + 4) + headDir.y * 4.0f;
        final Point2D.Float eye1Dir = GameMath.getAngleDir(headAngle + 90.0f);
        final Point2D.Float eye2Dir = GameMath.getAngleDir(headAngle + 90.0f + 180.0f);
        int eye1DrawX = (int)(eyeDrawX + eye1Dir.x * 16.0f);
        int eye2DrawX = (int)(eyeDrawX + eye2Dir.x * 16.0f);
        int eye1DrawY = (int)(eyeDrawY + eye1Dir.y * 4.0f);
        int eye2DrawY = (int)(eyeDrawY + eye2Dir.y * 4.0f);
        final TextureDrawOptionsEnd eyeDraw1 = MobRegistry.Textures.pestWarden.initDraw().sprite(eyeSprite, 4, 64).light(light).pos(eye1DrawX, eye1DrawY);
        final TextureDrawOptionsEnd eyeDraw2 = MobRegistry.Textures.pestWarden.initDraw().sprite(eyeSprite, 4, 64).light(light).pos(eye2DrawX, eye2DrawY);
        final MobDrawable drawable = WormMobHead.getDrawable(new GameSprite(MobRegistry.Textures.pestWarden, eyeSprite, 1, 128), MobRegistry.Textures.pestWarden_mask, light, (int)this.height, drawX, drawY, 112);
        list.add(new MobDrawable(){

            @Override
            public void draw(TickManager tickManager) {
                float cutOff = -0.7f;
                if (eye1Dir.y < cutOff) {
                    eyeDraw1.draw();
                }
                if (eye2Dir.y < cutOff) {
                    eyeDraw2.draw();
                }
                drawable.draw(tickManager);
                if (eye1Dir.y >= cutOff) {
                    eyeDraw1.draw();
                }
                if (eye2Dir.y >= cutOff) {
                    eyeDraw2.draw();
                }
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
        return shadowTexture.initDraw().sprite(2, 0, res).light(light).pos(drawX, (drawY += this.getBobbing(x, y)) - 40);
    }

    @Override
    public boolean shouldDrawOnMap() {
        return this.isVisible();
    }

    @Override
    public void drawOnMap(TickManager tickManager, Client client, int x, int y, double tileScale, Rectangle drawBounds, boolean isMinimap) {
        super.drawOnMap(tickManager, client, x, y, tileScale, drawBounds, isMinimap);
        int drawX = x - 20;
        int drawY = y - 6;
        float headAngle = GameMath.fixAngle(GameMath.getAngle(new Point2D.Float(this.dx, this.dy)));
        Point2D.Float headDir = GameMath.getAngleDir(headAngle);
        int eyeSprite = (int)(headAngle / 45.0f);
        eyeSprite = (eyeSprite + 2) % 8;
        float eyeDrawX = (float)drawX + 21.333334f - 10.666667f + headDir.x * 8.0f / 3.0f;
        float eyeDrawY = (float)drawY - 32.0f + 1.3333334f + headDir.y * 4.0f / 3.0f + 8.0f;
        Point2D.Float eye1Dir = GameMath.getAngleDir(headAngle + 90.0f);
        Point2D.Float eye2Dir = GameMath.getAngleDir(headAngle + 90.0f + 180.0f);
        int eye1DrawX = (int)(eyeDrawX + eye1Dir.x * 16.0f / 3.0f);
        int eye2DrawX = (int)(eyeDrawX + eye2Dir.x * 16.0f / 3.0f);
        int eye1DrawY = (int)(eyeDrawY + eye1Dir.y * 4.0f / 3.0f);
        int eye2DrawY = (int)(eyeDrawY + eye2Dir.y * 4.0f / 3.0f);
        TextureDrawOptionsEnd eyeDraw1 = MobRegistry.Textures.pestWarden.initDraw().sprite(eyeSprite, 4, 64).size(21).pos(eye1DrawX, eye1DrawY);
        TextureDrawOptionsEnd eyeDraw2 = MobRegistry.Textures.pestWarden.initDraw().sprite(eyeSprite, 4, 64).size(21).pos(eye2DrawX, eye2DrawY);
        GameLight light = this.getLevel().lightManager.newLight(150.0f);
        MobDrawable drawable = WormMobHead.getDrawable(new GameSprite(MobRegistry.Textures.pestWarden, eyeSprite, 1, 128, 42), null, light, (int)this.height, drawX, drawY, 26);
        float cutOff = -0.7f;
        if (eye1Dir.y < cutOff) {
            eyeDraw1.draw();
        }
        if (eye2Dir.y < cutOff) {
            eyeDraw2.draw();
        }
        drawable.draw(tickManager);
        if (eye1Dir.y >= cutOff) {
            eyeDraw1.draw();
        }
        if (eye2Dir.y >= cutOff) {
            eyeDraw2.draw();
        }
    }

    @Override
    public Rectangle drawOnMapBox(double tileScale, boolean isMinimap) {
        return new Rectangle(-15, -25, 30, 36);
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
        return Stream.of(new ModifierValue<Float>(BuffModifiers.SLOW, Float.valueOf(0.0f)).max(Float.valueOf(0.2f)), new ModifierValue<Float>(BuffModifiers.POISON_DAMAGE, Float.valueOf(1.0f)).max(Float.valueOf(0.2f)), new ModifierValue<Float>(BuffModifiers.FIRE_DAMAGE, Float.valueOf(1.0f)).max(Float.valueOf(0.2f)), new ModifierValue<Float>(BuffModifiers.FROST_DAMAGE, Float.valueOf(1.0f)).max(Float.valueOf(0.2f)));
    }

    @Override
    public float getIncomingDamageModifier() {
        return super.getIncomingDamageModifier() * (this.isHardened ? 0.1f : 1.0f);
    }

    @Override
    protected void onDeath(Attacker attacker, HashSet<Attacker> attackers) {
        super.onDeath(attacker, attackers);
        attackers.stream().map(Attacker::getFirstPlayerOwner).filter(Objects::nonNull).filter(PlayerMob::isServerClient).map(PlayerMob::getServerClient).distinct().forEach(c -> c.sendChatMessage(new LocalMessage("misc", "bossdefeat", "name", this.getLocalization())));
        if (!this.isDamagedByPlayers) {
            AchievementManager.checkMeAndThisArmyKill(this.getLevel(), attackers);
        }
    }

    public static class PestWardenAI<T extends PestWardenHead>
    extends SelectorAINode<T> {
        public PestWardenAI() {
            SequenceAINode chaserSequence = new SequenceAINode();
            this.addChild(chaserSequence);
            chaserSequence.addChild(new RemoveOnNoTargetNode(100));
            TargetFinderAINode targetFinder = new TargetFinderAINode<T>(3200){

                @Override
                public GameAreaStream<? extends Mob> streamPossibleTargets(T mob, Point base, TargetFinderDistance<T> distance) {
                    return TargetFinderAINode.streamPlayers(mob, base, distance);
                }
            };
            chaserSequence.addChild(targetFinder);
            targetFinder.moveToAttacker = false;
            SelectorAINode selector = new SelectorAINode();
            chaserSequence.addChild(selector);
            SpirallingAINode spirallingAI = new SpirallingAINode();
            selector.addChild(spirallingAI);
            ChargingCirclingChaserAINode chaser = new ChargingCirclingChaserAINode(800, 40);
            selector.addChild(chaser);
            chaser.backOffOnReset = false;
            spirallingAI.chaser = chaser;
            this.addChild(new WandererAINode(0));
        }
    }

    public static class SpirallingAINode<T extends PestWardenHead>
    extends AINode<T> {
        public int startMoveAccuracy;
        public ChargingCirclingChaserAINode<T> chaser;
        public boolean isActive = true;
        public int activeTicker;
        public boolean isMovingToStartPos = true;
        public Mob spiralTarget;
        public Point2D.Float spiralPos;
        public float spiralRadius = 380.0f;

        @Override
        protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
            this.startMoveAccuracy = ((PestWardenHead)mob).moveAccuracy;
            this.activeTicker = 20 * GameRandom.globalRandom.getIntBetween(15, 20);
        }

        @Override
        public void init(T mob, Blackboard<T> blackboard) {
        }

        @Override
        public AINodeResult tick(T mob, Blackboard<T> blackboard) {
            boolean abandonSpiral;
            if (!this.isActive) {
                --this.activeTicker;
                if (this.activeTicker < 0) {
                    this.isActive = true;
                    this.activeTicker = 20 * GameRandom.globalRandom.getIntBetween(15, 20);
                    if (this.chaser != null) {
                        this.chaser.fixMoveAccuracy();
                    }
                    return this.findNewStartPos();
                }
                return AINodeResult.FAILURE;
            }
            boolean isCurrentlyMoving = blackboard.mover.isCurrentlyMovingFor(this);
            if (this.isMovingToStartPos) {
                if (!isCurrentlyMoving) {
                    return this.findNewStartPos();
                }
                if (((Mob)mob).hasArrivedAtTarget()) {
                    if (this.spiralTarget != null && !this.spiralTarget.removed() && this.spiralTarget.isSamePlace((Entity)mob)) {
                        this.startSpiralling(this.spiralTarget);
                        return AINodeResult.SUCCESS;
                    }
                    return this.findNewStartPos();
                }
                return AINodeResult.SUCCESS;
            }
            if (!isCurrentlyMoving || this.spiralTarget == null || this.spiralPos == null) {
                this.isActive = false;
                ((PestWardenHead)mob).moveAccuracy = this.startMoveAccuracy;
                return AINodeResult.FAILURE;
            }
            boolean bl = abandonSpiral = ((Mob)mob).hasArrivedAtTarget() || this.spiralTarget.removed() || !((Entity)mob).isSamePlace(this.spiralTarget);
            if (!abandonSpiral) {
                float targetDistanceFromCenter = this.spiralTarget.getDistance(this.spiralPos.x, this.spiralPos.y);
                float maxDistance = this.spiralRadius + 50.0f;
                MobMovement currentMovement = ((Mob)mob).getCurrentMovement();
                if (currentMovement instanceof MobMovementSpiral) {
                    float currentRadius = ((MobMovementSpiral)currentMovement).getCurrentRadius();
                    maxDistance = Math.min(maxDistance, currentRadius + 50.0f);
                }
                boolean bl2 = abandonSpiral = targetDistanceFromCenter > maxDistance;
            }
            if (abandonSpiral) {
                this.isActive = false;
                ((PestWardenHead)mob).moveAccuracy = this.startMoveAccuracy;
                ((PestWardenHead)mob).setTemporarySpeed.runAndSend(0.0f);
                ((PestWardenHead)mob).setHardened.runAndSend(false);
                if (this.chaser != null && !this.spiralTarget.removed() && ((Entity)mob).isSamePlace(this.spiralTarget)) {
                    this.chaser.startCircling(mob, blackboard, this.spiralTarget, ((Mob)mob).hasArrivedAtTarget() ? 8 : 1);
                }
                return AINodeResult.FAILURE;
            }
            if (!((PestWardenHead)mob).isHardened) {
                ((PestWardenHead)mob).setHardened.runAndSend(true);
            }
            return AINodeResult.SUCCESS;
        }

        public void startSpiralling(Mob target) {
            PestWardenHead mob = (PestWardenHead)this.mob();
            Point2D.Float movingDir = GameMath.normalize(mob.dx, mob.dy);
            float movingAngle = GameMath.getAngle(movingDir);
            float targetAngle = GameMath.getAngle(GameMath.normalize(target.x - mob.x, target.y - mob.y));
            float angleDifference = GameMath.getAngleDifference(movingAngle, targetAngle);
            Point2D.Float centerPos = new Point2D.Float(target.x, target.y);
            int radiusDecreasePerSemiCircle = 40;
            int semiCircles = MobMovementSpiral.getSemiCircles(this.spiralRadius, radiusDecreasePerSemiCircle, 200.0f);
            float speed = mob.getSpeed();
            MobMovementSpiralLevelPos movement = new MobMovementSpiralLevelPos(mob, centerPos.x, centerPos.y, this.spiralRadius, semiCircles, radiusDecreasePerSemiCircle, speed, angleDifference < 0.0f);
            movement.startAngle = movement.clockwise ? (movement.startAngle += 15.0f) : (movement.startAngle -= 15.0f);
            this.getBlackboard().mover.setCustomMovement(this, movement);
            this.isMovingToStartPos = false;
            this.spiralTarget = target;
            this.spiralPos = centerPos;
            mob.moveAccuracy = 5;
            mob.setTemporarySpeed.runAndSend(speed);
            mob.setHardened.runAndSend(true);
        }

        public AINodeResult findNewStartPos() {
            Blackboard blackboard = this.getBlackboard();
            Mob target = blackboard.getObject(Mob.class, "currentTarget");
            if (target != null) {
                int angle = GameRandom.globalRandom.nextInt(360);
                Point2D.Float dir = GameMath.getAngleDir(angle);
                ((PestWardenHead)this.mob()).moveAccuracy = this.startMoveAccuracy;
                blackboard.mover.setCustomMovement(this, new MobMovementRelative(target, dir.x * this.spiralRadius, dir.y * this.spiralRadius));
                this.spiralTarget = target;
                this.isMovingToStartPos = true;
                return AINodeResult.SUCCESS;
            }
            this.isActive = false;
            ((PestWardenHead)this.mob()).moveAccuracy = this.startMoveAccuracy;
            return AINodeResult.FAILURE;
        }
    }
}

