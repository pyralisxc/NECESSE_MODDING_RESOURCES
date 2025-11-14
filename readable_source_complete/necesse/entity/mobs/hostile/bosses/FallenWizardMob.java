/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile.bosses;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import necesse.engine.achievements.AchievementManager;
import necesse.engine.eventStatusBars.EventStatusBarManager;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketChatMessage;
import necesse.engine.network.packet.PacketMobAttack;
import necesse.engine.network.server.ServerClient;
import necesse.engine.postProcessing.PostProcessingEffects;
import necesse.engine.registries.BiomeRegistry;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.MusicRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.SoundPlayer;
import necesse.engine.sound.SoundSettings;
import necesse.engine.sound.SoundSettingsRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.PriorityMap;
import necesse.engine.util.gameAreaSearch.GameAreaStream;
import necesse.entity.Entity;
import necesse.entity.levelEvent.FallenWizardRespawnEvent;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.FallenWizardBeamLevelEvent;
import necesse.entity.mobs.AttackAnimMob;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.MaskShaderOptions;
import necesse.entity.mobs.MaxHealthGetter;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.MobHealthScaling;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ability.BooleanMobAbility;
import necesse.entity.mobs.ability.CoordinateMobAbility;
import necesse.entity.mobs.ability.EmptyMobAbility;
import necesse.entity.mobs.ability.IntMobAbility;
import necesse.entity.mobs.ability.MobAbility;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.composites.SequenceAINode;
import necesse.entity.mobs.ai.behaviourTree.decorators.IsolateRunningAINode;
import necesse.entity.mobs.ai.behaviourTree.decorators.MoveTaskAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.TargetFinderAINode;
import necesse.entity.mobs.ai.behaviourTree.util.TargetFinderDistance;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.BossNearbyBuff;
import necesse.entity.mobs.hostile.bosses.BossMob;
import necesse.entity.mobs.hostile.bosses.FallenDragonHead;
import necesse.entity.mobs.hostile.bosses.FallenWizardGhostMob;
import necesse.entity.mobs.hostile.bosses.VoidWizard;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.AttackStageInterface;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.AttackStageManagerNode;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.IdleTimeAttackStage;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.RotationAttackStageNode;
import necesse.entity.particle.Particle;
import necesse.entity.particle.SmokePuffParticle;
import necesse.entity.projectile.FallenWizardBallProjectile;
import necesse.entity.projectile.FallenWizardWaveProjectile;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.followingProjectile.FallenWizardScepterProjectile;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.miscItem.GatewayTabletItem;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootList;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.inventory.lootTable.lootItem.ConditionLootItem;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.RotationLootItem;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.temple.TempleArenaLevel;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.regionSystem.RegionPositionGetter;

public class FallenWizardMob
extends BossMob {
    public static LootTable lootTable = new LootTable(new LootItemInterface(){

        @Override
        public void addPossibleLoot(LootList list, Object ... extra) {
            list.add("gatewaytablet");
        }

        @Override
        public void addItems(List<InventoryItem> list, GameRandom random, float lootMultiplier, Object ... extra) {
            InventoryItem gatewayTablet = new InventoryItem("gatewaytablet");
            GatewayTabletItem.initializeGatewayTablet(gatewayTablet, random, 1, null);
            list.add(gatewayTablet);
        }
    }, new LootItem("altardust", 60), new ChanceLootItem(0.2f, "wizardsrematchvinyl"));
    public static RotationLootItem uniqueDrops = RotationLootItem.privateLootRotation(new LootItemInterface[0]);
    public static LootTable privateLootTable = new LootTable(new ConditionLootItem("wizardsocket", (r, o) -> {
        ServerClient client = LootTable.expectExtra(ServerClient.class, o, 1);
        return client != null && client.playerMob.getInv().equipment.getTrinketSlotsSize() < 7 && client.playerMob.getInv().getAmount(ItemRegistry.getItem("wizardsocket"), false, false, true, true, "have") == 0;
    }), new ConditionLootItem("abyssalcloak", (r, o) -> {
        ServerClient client = LootTable.expectExtra(ServerClient.class, o, 1);
        return client != null && client.playerMob.getInv().equipment.getTotalSets() < 4 && client.playerMob.getInv().getAmount(ItemRegistry.getItem("abyssalcloak"), false, false, true, true, "have") == 0;
    }), uniqueDrops);
    public static MaxHealthGetter MAX_HEALTH = new MaxHealthGetter(20000, 30000, 35000, 40000, 50000);
    protected MobHealthScaling scaling = new MobHealthScaling(this);
    protected LinkedList<Projectile> projectiles = new LinkedList();
    public boolean isVisible;
    public long visibilityFadeTime;
    public boolean spawnParticles;
    public final FallenTeleportAbility teleportAbility;
    public final FallenSetVisibilityAbility visibilityAbility;
    public final BooleanMobAbility changeHostile;
    public final EmptyMobAbility laserBlastSound;
    public final EmptyMobAbility magicBoltSound;
    public final CoordinateMobAbility spawnDragonEffects;
    public final IntMobAbility humanSoundAbility;
    protected SoundPlayer tiptoeSoundPlayer;
    public static GameDamage laserDamage = new GameDamage(130.0f);
    public static GameDamage scepterDamage = new GameDamage(100.0f);
    public static GameDamage waveDamage = new GameDamage(90.0f);
    public static GameDamage ballDamage = new GameDamage(90.0f);
    public static GameDamage dragonHeadDamage = new GameDamage(130.0f);
    public static GameDamage dragonBodyDamage = new GameDamage(100.0f);

    public FallenWizardMob() {
        super(100);
        this.difficultyChanges.setMaxHealth(MAX_HEALTH);
        this.setSpeed(60.0f);
        this.setFriction(3.0f);
        this.setArmor(30);
        this.setKnockbackModifier(0.0f);
        this.collision = new Rectangle(-10, -7, 20, 14);
        this.hitBox = new Rectangle(-26, -24, 52, 48);
        this.selectBox = new Rectangle(-19, -52, 38, 64);
        this.swimMaskMove = 20;
        this.swimMaskOffset = -20;
        this.swimSinkOffset = 0;
        this.setDir(2);
        this.shouldSave = true;
        this.isHostile = false;
        this.isVisible = true;
        this.teleportAbility = this.registerAbility(new FallenTeleportAbility());
        this.visibilityAbility = this.registerAbility(new FallenSetVisibilityAbility());
        this.changeHostile = this.registerAbility(new BooleanMobAbility(){

            @Override
            protected void run(boolean value) {
                if (!FallenWizardMob.this.isHostile && value) {
                    if (FallenWizardMob.this.isClient()) {
                        SoundManager.playSound(GameResources.fallenwizardbegin, (SoundEffect)SoundEffect.effect(FallenWizardMob.this).volume(0.7f).falloffDistance(4000));
                    }
                } else if (FallenWizardMob.this.isHostile && !value) {
                    FallenWizardMob.this.setDir(2);
                    FallenWizardMob.this.isVisible = true;
                    FallenWizardMob.this.stopMoving();
                    FallenWizardMob.this.resetAI();
                }
                FallenWizardMob.this.isHostile = value;
            }
        });
        this.laserBlastSound = this.registerAbility(new EmptyMobAbility(){

            @Override
            protected void run() {
                if (FallenWizardMob.this.isClient()) {
                    SoundManager.playSound(GameResources.laserBlast2, (SoundEffect)SoundEffect.effect(FallenWizardMob.this).falloffDistance(1250).volume(0.7f).pitch(GameRandom.globalRandom.getOneOf(Float.valueOf(0.95f), Float.valueOf(1.0f), Float.valueOf(1.05f)).floatValue()));
                }
            }
        });
        this.magicBoltSound = this.registerAbility(new EmptyMobAbility(){

            @Override
            protected void run() {
                if (FallenWizardMob.this.isClient()) {
                    SoundManager.playSound(GameResources.magicbolt2, (SoundEffect)SoundEffect.effect(FallenWizardMob.this).falloffDistance(1250).volume(0.8f).pitch(GameRandom.globalRandom.getOneOf(Float.valueOf(0.95f), Float.valueOf(1.0f), Float.valueOf(1.05f)).floatValue()));
                }
            }
        });
        this.spawnDragonEffects = this.registerAbility(new CoordinateMobAbility(){

            @Override
            protected void run(int x, int y) {
                if (FallenWizardMob.this.isClient()) {
                    FallenWizardMob.this.getLevel().entityManager.addParticle(new PortalParticle(FallenWizardMob.this.getLevel(), x, y, 1000L), true, Particle.GType.CRITICAL);
                    SoundManager.playSound(GameResources.magicbolt4, (SoundEffect)SoundEffect.effect(FallenWizardMob.this).falloffDistance(1400).volume(0.6f));
                    SoundManager.playSound(GameResources.roar, (SoundEffect)SoundEffect.effect(x, y).falloffDistance(1400).volume(0.7f));
                }
            }
        });
        this.humanSoundAbility = this.registerAbility(new IntMobAbility(){

            @Override
            protected void run(int value) {
                if (!FallenWizardMob.this.isClient()) {
                    return;
                }
                switch (value) {
                    case 0: {
                        if (FallenWizardMob.this.tiptoeSoundPlayer == null || FallenWizardMob.this.tiptoeSoundPlayer.isDone()) {
                            FallenWizardMob.this.tiptoeSoundPlayer = SoundManager.playSound(SoundSettingsRegistry.humanFootsteps, FallenWizardMob.this);
                        }
                        return;
                    }
                    case 1: {
                        SoundManager.playSound(new SoundSettings(GameResources.warcryumf).volume(0.3f).pitchVariance(0.0f).fallOffDistance(1000), FallenWizardMob.this);
                        return;
                    }
                    case 2: {
                        SoundManager.playSound(new SoundSettings(GameResources.swoosh2).volume(0.2f).basePitch(0.7f).pitchVariance(0.0f).fallOffDistance(1000), FallenWizardMob.this);
                    }
                }
            }
        });
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextBoolean(this.spawnParticles);
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.spawnParticles = reader.getNextBoolean();
    }

    @Override
    public void setupMovementPacket(PacketWriter writer) {
        super.setupMovementPacket(writer);
        writer.putNextBoolean(this.isHostile);
    }

    @Override
    public void applyMovementPacket(PacketReader reader, boolean isDirect) {
        super.applyMovementPacket(reader, isDirect);
        this.isHostile = reader.getNextBoolean();
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
        this.resetAI();
        this.setDir(2);
        if (this.spawnParticles && !this.isServer()) {
            this.getLevel().entityManager.addParticle(new SmokePuffParticle(this.getLevel(), this.getX(), this.getY() + 2, 96, new Color(67, 6, 127)), Particle.GType.CRITICAL);
        }
        if (this.isServer() && this.isInArena()) {
            Point2D.Float pos = TempleArenaLevel.getBossPosition();
            this.setPos(pos.x, pos.y, true);
            this.setHealth(this.getMaxHealth());
        }
    }

    protected void resetAI() {
        this.ai = new BehaviourTreeAI<FallenWizardMob>(this, new FallenWizardAI());
    }

    public boolean isInArena() {
        return this.getLevel().getBiome(this.getTileX(), this.getTileY()) == BiomeRegistry.TEMPLE && this.getLevel() instanceof TempleArenaLevel;
    }

    @Override
    public LootTable getLootTable() {
        return lootTable;
    }

    @Override
    public LootTable getPrivateLootTable() {
        return privateLootTable;
    }

    public void addProjectile(Projectile p) {
        this.projectiles.add(p);
    }

    public void clearProjectiles() {
        this.projectiles.forEach(Projectile::remove);
        this.projectiles.clear();
    }

    @Override
    public int getMaxHealth() {
        return super.getMaxHealth() + (int)((float)(this.scaling == null ? 0 : this.scaling.getHealthIncrease()) * this.getMaxHealthModifier());
    }

    @Override
    public void clientTick() {
        super.clientTick();
        float healthPercInv = Math.abs((float)this.getHealth() / (float)this.getMaxHealth() - 1.0f);
        this.setSpeed(60.0f + 30.0f * healthPercInv);
        if (this.isHostile) {
            if (this.isClientPlayerNearby()) {
                SoundManager.setMusic(MusicRegistry.WizardsRematch, SoundManager.MusicPriority.EVENT, 1.5f);
                EventStatusBarManager.registerMobHealthStatusBar(this);
                Color shade = VoidWizard.getWizardShade(this);
                PostProcessingEffects.setSceneShade(shade);
            }
            BossNearbyBuff.applyAround(this);
        }
        this.getLevel().lightManager.refreshParticleLightFloat(this.x, this.y, 260.0f, 0.5f);
        this.spawnParticles = false;
    }

    @Override
    public void serverTick() {
        super.serverTick();
        float healthPercInv = Math.abs((float)this.getHealth() / (float)this.getMaxHealth() - 1.0f);
        this.setSpeed(60.0f + 30.0f * healthPercInv);
        this.scaling.serverTick();
        if (this.isHostile) {
            BossNearbyBuff.applyAround(this);
        }
        this.spawnParticles = false;
    }

    @Override
    public CollisionFilter getLevelCollisionFilter() {
        return super.getLevelCollisionFilter().addFilter(tp -> tp.object().object.isWall || tp.object().object.isRock);
    }

    @Override
    public boolean canBePushed(Mob other) {
        return false;
    }

    @Override
    public boolean isVisible() {
        return this.isVisible;
    }

    @Override
    public boolean canTakeDamage() {
        return super.canTakeDamage() && this.isVisible();
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for (int i = 0; i < 50; ++i) {
            int lifeTime = GameRandom.globalRandom.getIntBetween(500, 5000);
            float lifePerc = (float)lifeTime / 5000.0f;
            float startHeight = 10.0f;
            float height = startHeight + (float)GameRandom.globalRandom.getIntBetween(70, 150) * lifePerc;
            this.getLevel().entityManager.addParticle(this.x + GameRandom.globalRandom.getFloatBetween(-20.0f, 20.0f), this.y + GameRandom.globalRandom.getFloatBetween(-10.0f, 10.0f), Particle.GType.IMPORTANT_COSMETIC).sizeFades(20, 30).movesFriction(GameRandom.globalRandom.getFloatBetween(-40.0f, 40.0f), GameRandom.globalRandom.getFloatBetween(-20.0f, 20.0f), 0.5f).heightMoves(startHeight, height).colorRandom(270.0f, 0.8f, 0.5f, 10.0f, 0.1f, 0.1f).lifeTime(lifeTime);
        }
    }

    @Override
    public void playHurtSound() {
        if (this.isHostile) {
            float pitch = GameRandom.globalRandom.getOneOf(Float.valueOf(0.95f), Float.valueOf(1.0f), Float.valueOf(1.05f)).floatValue();
            SoundManager.playSound(GameResources.fallenwizardhurt, (SoundEffect)SoundEffect.effect(this).volume(0.4f).pitch(pitch).falloffDistance(1500));
        }
    }

    @Override
    public void playDeathSound() {
        SoundManager.playSound(GameResources.fallenwizarddeath, (SoundEffect)SoundEffect.effect(this).falloffExponent(3000.0f));
    }

    @Override
    protected void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        long timeSinceFade = level.getWorldEntity().getLocalTime() - this.visibilityFadeTime;
        if (!this.isVisible() && timeSinceFade > 1000L) {
            return;
        }
        GameLight light = level.getLightLevel(FallenWizardMob.getTileCoordinate(x), FallenWizardMob.getTileCoordinate(y));
        int drawX = camera.getDrawX(x) - 48;
        int drawY = camera.getDrawY(y) - 75;
        int dir = this.getDir();
        Point sprite = this.getAnimSprite(x, y, dir);
        drawY += this.getBobbing(x, y);
        drawY += level.getTile(FallenWizardMob.getTileCoordinate(x), FallenWizardMob.getTileCoordinate(y)).getMobSinkingAmount(this);
        MaskShaderOptions swimMask = this.getSwimMaskShaderOptions(this.inLiquidFloat(x, y));
        HumanDrawOptions humanDrawOptions = new HumanDrawOptions(level, MobRegistry.Textures.fallenWizard).sprite(sprite, 96).size(96, 96).mask(swimMask).dir(dir).light(light).attackOffsets(48, 34, 16, 25, 18, 5, 20);
        if (timeSinceFade < 1000L) {
            float alpha = Math.max(0.0f, (float)timeSinceFade / 1000.0f);
            if (!this.isVisible()) {
                alpha = Math.abs(alpha - 1.0f);
            }
            humanDrawOptions.allAlpha(alpha);
        }
        float animProgress = this.getAttackAnimProgress();
        if (this.isAttacking) {
            ItemAttackDrawOptions attackOptions = ItemAttackDrawOptions.start(dir).itemSprite(MobRegistry.Textures.fallenWizard.body, 0, 9, 48).itemRotatePoint(14, 4).itemEnd().armSprite(MobRegistry.Textures.fallenWizard.body, 0, 8, 48).light(light);
            attackOptions.pointRotation(this.attackDir.x, this.attackDir.y).forEachItemSprite(i -> i.itemRotateOffset(45.0f));
            humanDrawOptions.attackAnim(attackOptions, animProgress);
        }
        final DrawOptions drawOptions = humanDrawOptions.pos(drawX, drawY);
        list.add(new MobDrawable(){

            @Override
            public void draw(TickManager tickManager) {
                drawOptions.draw();
            }
        });
        this.addShadowDrawables(tileList, level, x, y, light, camera);
    }

    @Override
    protected TextureDrawOptions getShadowDrawOptions(Level level, int x, int y, GameLight light, GameCamera camera) {
        GameTexture shadowTexture = MobRegistry.Textures.human_big_shadow;
        int res = shadowTexture.getHeight();
        int drawX = camera.getDrawX(x) - res / 2;
        int drawY = camera.getDrawY(y) - res / 2;
        return shadowTexture.initDraw().sprite(this.getDir(), 0, res).light(light).pos(drawX, drawY += this.getBobbing(x, y));
    }

    @Override
    public boolean shouldDrawOnMap() {
        return this.isVisible();
    }

    @Override
    public Rectangle drawOnMapBox(double tileScale, boolean isMinimap) {
        return new Rectangle(-12, -28, 24, 34);
    }

    @Override
    public GameTooltips getMapTooltips() {
        return new StringTooltips(this.getDisplayName() + " " + this.getHealth() + "/" + this.getMaxHealth());
    }

    @Override
    public void drawOnMap(TickManager tickManager, Client client, int x, int y, double tileScale, Rectangle drawBounds, boolean isMinimap) {
        super.drawOnMap(tickManager, client, x, y, tileScale, drawBounds, isMinimap);
        int drawX = x - 24;
        int drawY = y - 34;
        int dir = this.getDir();
        Point sprite = this.getAnimSprite(this.getDrawX(), this.getDrawY(), dir);
        new HumanDrawOptions(this.getLevel(), MobRegistry.Textures.fallenWizard).sprite(sprite, 96).dir(dir).size(48, 48).draw(drawX, drawY);
    }

    @Override
    public Stream<ModifierValue<?>> getDefaultModifiers() {
        return Stream.of(new ModifierValue<Float>(BuffModifiers.SLOW, Float.valueOf(0.0f)).max(Float.valueOf(0.2f)), new ModifierValue<Float>(BuffModifiers.ATTACK_MOVEMENT_MOD, Float.valueOf(0.0f)));
    }

    @Override
    protected void onDeath(Attacker attacker, HashSet<Attacker> attackers) {
        super.onDeath(attacker, attackers);
        boolean dodgeThisValid = this.getLevel().entityManager.mobs.stream().filter(m -> m instanceof FallenDragonHead).count() >= 5L;
        attackers.stream().map(Attacker::getFirstPlayerOwner).filter(Objects::nonNull).filter(PlayerMob::isServerClient).map(PlayerMob::getServerClient).distinct().forEach(c -> {
            c.sendChatMessage(new LocalMessage("misc", "bossdefeat", "name", this.getLocalization()));
            if (c.achievementsLoaded()) {
                c.achievements().REMATCH.markCompleted((ServerClient)c);
                if (dodgeThisValid) {
                    c.achievements().DODGE_THIS.markCompleted((ServerClient)c);
                }
            }
        });
        if (!this.isDamagedByPlayers) {
            AchievementManager.checkMeAndThisArmyKill(this.getLevel(), attackers);
        }
        if (this.isInArena() && this.getLevel().entityManager.events.stream().noneMatch(e -> e instanceof FallenWizardRespawnEvent)) {
            Point2D.Float pos = TempleArenaLevel.getBossPosition();
            long spawnInTime = 600000L;
            this.getLevel().entityManager.events.add(new FallenWizardRespawnEvent(pos.x, pos.y, this.getLevel().getWorldEntity().getWorldTime() + spawnInTime));
        }
    }

    public class FallenTeleportAbility
    extends MobAbility {
        public void runAndSend(int x, int y, int dir, boolean smokeParticle, boolean spawnParticlesBefore, boolean spawnParticlesAfter) {
            Packet content = new Packet();
            PacketWriter writer = new PacketWriter(content);
            writer.putNextInt(x);
            writer.putNextInt(y);
            writer.putNextInt(dir);
            writer.putNextBoolean(smokeParticle);
            writer.putNextBoolean(spawnParticlesBefore);
            writer.putNextBoolean(spawnParticlesAfter);
            this.runAndSendAbility(content);
        }

        @Override
        public void executePacket(PacketReader reader) {
            int x = reader.getNextInt();
            int y = reader.getNextInt();
            int dir = reader.getNextInt();
            boolean smokeParticle = reader.getNextBoolean();
            boolean spawnParticlesBefore = reader.getNextBoolean();
            boolean spawnParticlesAfter = reader.getNextBoolean();
            if (spawnParticlesBefore && FallenWizardMob.this.isClient()) {
                if (smokeParticle) {
                    FallenWizardMob.this.getLevel().entityManager.addParticle(new SmokePuffParticle(FallenWizardMob.this.getLevel(), FallenWizardMob.this.getX(), FallenWizardMob.this.getY(), 96, new Color(67, 6, 127)), Particle.GType.CRITICAL);
                } else {
                    FallenWizardMob.this.getLevel().entityManager.addParticle(new TeleportParticle(FallenWizardMob.this.getLevel(), FallenWizardMob.this.getX(), FallenWizardMob.this.getY(), FallenWizardMob.this), Particle.GType.CRITICAL);
                    FallenWizardMob.this.getLevel().lightManager.refreshParticleLightFloat((float)x, (float)y, 270.0f, 0.5f);
                }
            }
            FallenWizardMob.this.setDir(dir);
            FallenWizardMob.this.setPos(x, y, true);
            if (spawnParticlesAfter && FallenWizardMob.this.isClient()) {
                if (smokeParticle) {
                    FallenWizardMob.this.getLevel().entityManager.addParticle(new SmokePuffParticle(FallenWizardMob.this.getLevel(), FallenWizardMob.this.getX(), FallenWizardMob.this.getY() + 2, 96, new Color(67, 6, 127)), Particle.GType.CRITICAL);
                } else {
                    FallenWizardMob.this.getLevel().entityManager.addParticle(new TeleportParticle(FallenWizardMob.this.getLevel(), FallenWizardMob.this.getX(), FallenWizardMob.this.getY() + 2, FallenWizardMob.this), Particle.GType.CRITICAL);
                }
            }
        }
    }

    public class FallenSetVisibilityAbility
    extends MobAbility {
        public void runAndSend(boolean visible, boolean fade) {
            Packet content = new Packet();
            PacketWriter writer = new PacketWriter(content);
            writer.putNextBoolean(visible);
            writer.putNextBoolean(fade);
            this.runAndSendAbility(content);
        }

        @Override
        public void executePacket(PacketReader reader) {
            FallenWizardMob.this.isVisible = reader.getNextBoolean();
            if (reader.getNextBoolean()) {
                FallenWizardMob.this.visibilityFadeTime = FallenWizardMob.this.getWorldEntity().getLocalTime();
            }
        }
    }

    public static class FallenWizardAI<T extends FallenWizardMob>
    extends SequenceAINode<T> {
        private int inActiveTimer;

        public FallenWizardAI() {
            this.addChild(new AINode<T>(){

                @Override
                protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
                }

                @Override
                public void init(T mob, Blackboard<T> blackboard) {
                }

                @Override
                public AINodeResult tick(T mob, Blackboard<T> blackboard) {
                    if (!((FallenWizardMob)mob).isHostile) {
                        blackboard.mover.stopMoving((Mob)mob);
                        return AINodeResult.FAILURE;
                    }
                    return AINodeResult.SUCCESS;
                }
            });
            this.addChild(new AINode<T>(){

                @Override
                protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
                }

                @Override
                public void init(T mob, Blackboard<T> blackboard) {
                }

                @Override
                public AINodeResult tick(T mob, Blackboard<T> blackboard) {
                    Mob currentTarget = blackboard.getObject(Mob.class, "currentTarget");
                    if (currentTarget == null) {
                        inActiveTimer++;
                        if (inActiveTimer > 100) {
                            if (((FallenWizardMob)mob).isInArena()) {
                                Point2D.Float tpPos = TempleArenaLevel.getBossPosition();
                                ((FallenWizardMob)mob).teleportAbility.runAndSend((int)tpPos.x, (int)tpPos.y, 2, false, true, false);
                                ((FallenWizardMob)mob).humanSoundAbility.runAndSend(2);
                            }
                            ((FallenWizardMob)mob).changeHostile.runAndSend(false);
                            inActiveTimer = 0;
                            ((FallenWizardMob)mob).clearProjectiles();
                            ((Mob)mob).setHealth(((FallenWizardMob)mob).getMaxHealth());
                            return AINodeResult.FAILURE;
                        }
                    } else {
                        inActiveTimer = 0;
                    }
                    return AINodeResult.SUCCESS;
                }
            });
            this.addChild(new TargetFinderAINode<T>(3200){

                @Override
                public GameAreaStream<? extends Mob> streamPossibleTargets(T mob, Point base, TargetFinderDistance<T> distance) {
                    return TargetFinderAINode.streamPlayers(mob, base, distance);
                }
            });
            AttackStageManagerNode attackStages = new AttackStageManagerNode();
            attackStages.allowSkippingBack = true;
            this.addChild(new IsolateRunningAINode(attackStages));
            attackStages.addChild(new FindNewPosStage(true));
            attackStages.addChild(new IdleTimeAttackStage(0, 200));
            attackStages.addChild(new RotationAttackStageNode(new DirectionWaveProjectilesStage(0, 250, 200, 400, 60.0f, 5), new RandomWaveProjectilesStage(0, 250, 400, 600, 4)));
            attackStages.addChild(new GhostFindNewPosStage());
            attackStages.addChild(new IdleTimeAttackStage(100, 500));
            attackStages.addChild(new BeamSweepStage(1500, 3000, 110.0f, 270.0f));
            attackStages.addChild(new FindNewPosStage(true));
            attackStages.addChild(new IdleTimeAttackStage(0, 100));
            attackStages.addChild(new BulletHellStage(250, -35.0f, 100.0f, 4, 1000, 2000, 7.5f));
            attackStages.addChild(new FindNewPosStage(true));
            attackStages.addChild(new IdleTimeAttackStage(0, 100));
            attackStages.addChild(new DragonSpawnStage(0, 250, 500, 1000, 2));
            attackStages.addChild(new FindNewPosStage(true));
            attackStages.addChild(new IdleTimeAttackStage(0, 100));
            attackStages.addChild(new ScepterProjectilesStage(25, 2250, 2750));
        }

        public PriorityMap<Point> findNewPositions(Mob mob, Blackboard<T> blackboard) {
            Mob currentTarget = blackboard.getObject(Mob.class, "currentTarget");
            Point2D.Float base = new Point2D.Float(mob.x, mob.y);
            if (currentTarget != null) {
                base = new Point2D.Float(currentTarget.x, currentTarget.y);
            }
            int searchRadius = 8;
            int minRadius = 3;
            int mobTileX = mob.getTileX();
            int mobTileY = mob.getTileY();
            int xOffset = (int)Math.signum(Entity.getTileCoordinate(base.x) - mobTileX) * searchRadius / 2;
            int yOffset = (int)Math.signum(Entity.getTileCoordinate(base.y) - mobTileY) * searchRadius / 2;
            PriorityMap<Point> priorityMap = new PriorityMap<Point>();
            for (int x = -searchRadius; x <= searchRadius; ++x) {
                if (x > -minRadius && x < minRadius) continue;
                for (int y = -searchRadius; y <= searchRadius; ++y) {
                    if (y > -minRadius && y < minRadius) continue;
                    Point p = new Point(mobTileX + xOffset + x, mobTileY + yOffset + y);
                    if (mob.getLevel().isSolidTile(p.x, p.y)) continue;
                    int height = mob.getLevel().liquidManager.getHeight(p.x, p.y);
                    if (height >= 8) {
                        priorityMap.add(10, p);
                        continue;
                    }
                    priorityMap.add(height / 2, p);
                }
            }
            return priorityMap;
        }

        public Point findNewPosition(Mob mob, Blackboard<T> blackboard) {
            return this.findNewPositions(mob, blackboard).getRandomBestObject(GameRandom.globalRandom, 20);
        }

        @Override
        public void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
            super.onRootSet(root, mob, blackboard);
            blackboard.onWasHit(e -> this.makeHostile(mob));
        }

        public void makeHostile(T mob) {
            if (!((FallenWizardMob)mob).isHostile) {
                if (((Entity)mob).isServer()) {
                    ((Entity)mob).getLevel().getServer().network.sendToClientsWithEntity(new PacketChatMessage(new LocalMessage("misc", "bossawoke", "name", ((Mob)mob).getLocalization())), (RegionPositionGetter)mob);
                }
                ((FallenWizardMob)mob).changeHostile.runAndSend(true);
            }
        }

        public int getHealthPercValue(T mob, int noHealthValue, int fullHealthValue) {
            int delta = fullHealthValue - noHealthValue;
            float healthPerc = (float)((Mob)mob).getHealth() / (float)((FallenWizardMob)mob).getMaxHealth();
            return noHealthValue + (int)((float)delta * healthPerc);
        }

        protected class FindNewPosStage
        extends MoveTaskAINode<T>
        implements AttackStageInterface<T> {
            public boolean waitForArrive;
            private boolean hasStartedMoving;

            public FindNewPosStage(boolean waitForArrive) {
                this.waitForArrive = waitForArrive;
            }

            @Override
            protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
            }

            @Override
            public void init(T mob, Blackboard<T> blackboard) {
            }

            @Override
            public AINodeResult tickNode(T mob, Blackboard<T> blackboard) {
                if (!this.hasStartedMoving) {
                    this.hasStartedMoving = true;
                    Point pos = FallenWizardAI.this.findNewPosition((Mob)mob, blackboard);
                    if (pos != null) {
                        return this.moveToTileTask(pos.x, pos.y, null, path -> {
                            path.move(null);
                            return null;
                        });
                    }
                }
                if (this.waitForArrive && blackboard.mover.isCurrentlyMovingFor(this) && blackboard.mover.isMoving()) {
                    ((FallenWizardMob)mob).humanSoundAbility.runAndSend(0);
                    return AINodeResult.RUNNING;
                }
                return AINodeResult.SUCCESS;
            }

            @Override
            public void onStarted(T mob, Blackboard<T> blackboard) {
                this.hasStartedMoving = false;
                ((FallenWizardMob)mob).humanSoundAbility.runAndSend(2);
            }

            @Override
            public void onEnded(T mob, Blackboard<T> blackboard) {
            }
        }

        protected class DirectionWaveProjectilesStage
        extends AINode<T>
        implements AttackStageInterface<T> {
            private Point2D.Float direction;
            private int projectilesFired;
            private long lastProjectileTime;
            private long nextShowAttackTime;
            private boolean reversed;
            private int currentTimeGap;
            public float gapSize;
            public int noHealthStartDelay;
            public int fullHealthStartDelay;
            public int noHealthTimeGap;
            public int fullHealthTimeGap;
            public int waves;

            public DirectionWaveProjectilesStage(int noHealthStartDelay, int fullHealthStartDelay, int noHealthTimeGap, int fullHealthTimeGap, float gapSize, int waves) {
                this.noHealthStartDelay = noHealthStartDelay;
                this.fullHealthStartDelay = fullHealthStartDelay;
                this.noHealthTimeGap = noHealthTimeGap;
                this.fullHealthTimeGap = fullHealthTimeGap;
                this.gapSize = gapSize;
                this.waves = waves;
            }

            @Override
            protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
            }

            @Override
            public void init(T mob, Blackboard<T> blackboard) {
            }

            @Override
            public AINodeResult tick(T mob, Blackboard<T> blackboard) {
                long currentTime = ((Entity)mob).getWorldEntity().getTime();
                if (this.nextShowAttackTime <= currentTime) {
                    ((AttackAnimMob)mob).showAttack((int)(((FallenWizardMob)mob).x + this.direction.x * 100.0f), (int)(((FallenWizardMob)mob).y + this.direction.y * 100.0f), false);
                    this.nextShowAttackTime = currentTime + 100L;
                    if (((Entity)mob).isServer()) {
                        ((Entity)mob).getLevel().getServer().network.sendToClientsWithEntity(new PacketMobAttack((Mob)mob, (int)(((FallenWizardMob)mob).x + this.direction.x * 100.0f), (int)(((FallenWizardMob)mob).y + this.direction.y * 100.0f), false), (RegionPositionGetter)mob);
                    }
                }
                long timeSinceLastProjectile = currentTime - this.lastProjectileTime;
                while (this.projectilesFired < this.waves && timeSinceLastProjectile >= (long)this.currentTimeGap) {
                    this.lastProjectileTime += (long)this.currentTimeGap;
                    timeSinceLastProjectile = currentTime - this.lastProjectileTime;
                    Point2D.Float perpDir = GameMath.getPerpendicularDir(this.direction.x, this.direction.y);
                    if (this.reversed) {
                        perpDir = new Point2D.Float(-perpDir.x, -perpDir.y);
                    }
                    float projectileWidth = 200.0f;
                    float totalDistance = (projectileWidth + this.gapSize) * (float)this.waves - this.gapSize;
                    float currentDistance = (projectileWidth + this.gapSize) * (float)this.projectilesFired + projectileWidth / 2.0f;
                    float startBehindDistance = 500.0f;
                    float startX = ((FallenWizardMob)mob).x + perpDir.x * totalDistance / 2.0f - perpDir.x * currentDistance - this.direction.x * startBehindDistance;
                    float startY = ((FallenWizardMob)mob).y + perpDir.y * totalDistance / 2.0f - perpDir.y * currentDistance - this.direction.y * startBehindDistance;
                    int range = 2000;
                    ((Entity)mob).getLevel().entityManager.projectiles.add(new FallenWizardWaveProjectile(startX, startY, startX + this.direction.x * 100.0f, startY + this.direction.y * 100.0f, 120.0f, range, waveDamage, 50, (Mob)mob));
                    ++this.projectilesFired;
                    ((FallenWizardMob)mob).magicBoltSound.runAndSend();
                }
                if (this.projectilesFired < this.waves) {
                    return AINodeResult.RUNNING;
                }
                return AINodeResult.SUCCESS;
            }

            @Override
            public void onStarted(T mob, Blackboard<T> blackboard) {
                Mob target = blackboard.getObject(Mob.class, "currentTarget");
                Point2D.Float targetPos = target != null ? new Point2D.Float(target.x, target.y) : (((FallenWizardMob)mob).isInArena() ? TempleArenaLevel.getBossPosition() : new Point2D.Float(((FallenWizardMob)mob).x, ((FallenWizardMob)mob).y + 100.0f));
                this.direction = GameMath.normalize(targetPos.x - ((FallenWizardMob)mob).x, targetPos.y - ((FallenWizardMob)mob).y);
                if (this.direction.x == 0.0f && this.direction.y == 0.0f) {
                    this.direction = new Point2D.Float(0.0f, 1.0f);
                }
                this.reversed = !this.reversed;
                this.nextShowAttackTime = 0L;
                this.projectilesFired = 0;
                this.currentTimeGap = FallenWizardAI.this.getHealthPercValue(mob, this.noHealthTimeGap, this.fullHealthTimeGap);
                int startDelay = FallenWizardAI.this.getHealthPercValue(mob, this.noHealthStartDelay, this.fullHealthStartDelay);
                this.lastProjectileTime = ((Entity)mob).getWorldEntity().getTime() - (long)(this.currentTimeGap - startDelay);
                ((FallenWizardMob)mob).humanSoundAbility.runAndSend(1);
            }

            @Override
            public void onEnded(T mob, Blackboard<T> blackboard) {
            }
        }

        protected class RandomWaveProjectilesStage
        extends AINode<T>
        implements AttackStageInterface<T> {
            private Point2D.Float lastDirection;
            private int projectilesFired;
            private long lastProjectileTime;
            private long nextShowAttackTime;
            private int currentTimeGap;
            public int noHealthStartDelay;
            public int fullHealthStartDelay;
            public int noHealthTimeGap;
            public int fullHealthTimeGap;
            public int waves;

            public RandomWaveProjectilesStage(int noHealthStartDelay, int fullHealthStartDelay, int noHealthTimeGap, int fullHealthTimeGap, int waves) {
                this.noHealthStartDelay = noHealthStartDelay;
                this.fullHealthStartDelay = fullHealthStartDelay;
                this.noHealthTimeGap = noHealthTimeGap;
                this.fullHealthTimeGap = fullHealthTimeGap;
                this.waves = waves;
            }

            @Override
            protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
            }

            @Override
            public void init(T mob, Blackboard<T> blackboard) {
            }

            @Override
            public AINodeResult tick(T mob, Blackboard<T> blackboard) {
                long currentTime = ((Entity)mob).getWorldEntity().getTime();
                if (this.lastDirection != null && this.nextShowAttackTime <= currentTime) {
                    ((AttackAnimMob)mob).showAttack((int)(((FallenWizardMob)mob).x + this.lastDirection.x * 100.0f), (int)(((FallenWizardMob)mob).y + this.lastDirection.y * 100.0f), false);
                    this.nextShowAttackTime = currentTime + 100L;
                    if (((Entity)mob).isServer()) {
                        ((Entity)mob).getLevel().getServer().network.sendToClientsWithEntity(new PacketMobAttack((Mob)mob, (int)(((FallenWizardMob)mob).x + this.lastDirection.x * 100.0f), (int)(((FallenWizardMob)mob).y + this.lastDirection.y * 100.0f), false), (RegionPositionGetter)mob);
                    }
                }
                long timeSinceLastProjectile = currentTime - this.lastProjectileTime;
                while (this.projectilesFired < this.waves && timeSinceLastProjectile >= (long)this.currentTimeGap) {
                    Mob target = blackboard.getObject(Mob.class, "currentTarget");
                    this.lastProjectileTime += (long)this.currentTimeGap;
                    timeSinceLastProjectile = currentTime - this.lastProjectileTime;
                    if (target != null) {
                        this.lastDirection = GameMath.getAngleDir(GameRandom.globalRandom.nextInt(360));
                        Point2D.Float perpDir = GameMath.getPerpendicularDir(this.lastDirection.x, this.lastDirection.y);
                        float perpDistance = GameRandom.globalRandom.getFloatBetween(-200.0f, 200.0f);
                        float startBehindDistance = 800.0f;
                        float startX = target.x + perpDir.x * perpDistance - this.lastDirection.x * startBehindDistance;
                        float startY = target.y + perpDir.y * perpDistance - this.lastDirection.y * startBehindDistance;
                        int range = 2300;
                        ((Entity)mob).getLevel().entityManager.projectiles.add(new FallenWizardWaveProjectile(startX, startY, startX + this.lastDirection.x * 100.0f, startY + this.lastDirection.y * 100.0f, 120.0f, range, waveDamage, 50, (Mob)mob));
                    }
                    ++this.projectilesFired;
                    ((FallenWizardMob)mob).magicBoltSound.runAndSend();
                }
                if (this.projectilesFired < this.waves) {
                    return AINodeResult.RUNNING;
                }
                return AINodeResult.SUCCESS;
            }

            @Override
            public void onStarted(T mob, Blackboard<T> blackboard) {
                this.nextShowAttackTime = 0L;
                this.projectilesFired = 0;
                this.currentTimeGap = FallenWizardAI.this.getHealthPercValue(mob, this.noHealthTimeGap, this.fullHealthTimeGap);
                int startDelay = FallenWizardAI.this.getHealthPercValue(mob, this.noHealthStartDelay, this.fullHealthStartDelay);
                this.lastProjectileTime = ((Entity)mob).getWorldEntity().getTime() - (long)(this.currentTimeGap - startDelay);
                ((FallenWizardMob)mob).humanSoundAbility.runAndSend(1);
            }

            @Override
            public void onEnded(T mob, Blackboard<T> blackboard) {
            }
        }

        protected class GhostFindNewPosStage
        extends AINode<T>
        implements AttackStageInterface<T> {
            private final LinkedList<Mob> ghostMobs = new LinkedList();

            @Override
            protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
            }

            @Override
            public void init(T mob, Blackboard<T> blackboard) {
            }

            @Override
            public AINodeResult tick(T mob, Blackboard<T> blackboard) {
                ListIterator li = this.ghostMobs.listIterator();
                Mob last = null;
                while (li.hasNext()) {
                    Mob next = (Mob)li.next();
                    if (!next.removed()) continue;
                    last = next;
                    li.remove();
                }
                if (!this.ghostMobs.isEmpty()) {
                    return AINodeResult.RUNNING;
                }
                if (last != null) {
                    ((FallenWizardMob)mob).teleportAbility.runAndSend(last.getX(), last.getY(), last.getDir(), true, false, true);
                    ((FallenWizardMob)mob).visibilityAbility.runAndSend(true, false);
                    ((FallenWizardMob)mob).humanSoundAbility.runAndSend(1);
                }
                return AINodeResult.SUCCESS;
            }

            @Override
            public void onStarted(T mob, Blackboard<T> blackboard) {
                this.ghostMobs.clear();
                PriorityMap<Point> positions = FallenWizardAI.this.findNewPositions((Mob)mob, blackboard);
                ArrayList<Point> best = positions.getBestObjects(20);
                LinkedList<Point> lastPoints = new LinkedList<Point>();
                for (int i = 0; i < 4 && !best.isEmpty(); ++i) {
                    Point pos;
                    if (lastPoints.isEmpty()) {
                        pos = best.stream().min(Comparator.comparingDouble(p -> p.distance(mob.x, mob.y))).get();
                    } else {
                        int bestIndex = IntStream.range(0, best.size()).boxed().max(Comparator.comparingDouble(n -> lastPoints.stream().mapToDouble(p -> ((Point)best.get((int)n)).distance((Point2D)p)).sum())).orElse(-1);
                        pos = best.remove(bestIndex);
                    }
                    FallenWizardGhostMob newGhost = (FallenWizardGhostMob)MobRegistry.getMob("fallenwizardghost", ((Entity)mob).getLevel());
                    ((Entity)mob).getLevel().entityManager.addMob(newGhost, ((FallenWizardMob)mob).x, ((FallenWizardMob)mob).y);
                    newGhost.moveToPos(pos.x, pos.y);
                    this.ghostMobs.add(newGhost);
                    lastPoints.add(pos);
                    ((FallenWizardMob)mob).humanSoundAbility.runAndSend(2);
                }
                if (!this.ghostMobs.isEmpty()) {
                    ((FallenWizardMob)mob).visibilityAbility.runAndSend(false, false);
                }
            }

            @Override
            public void onEnded(T mob, Blackboard<T> blackboard) {
            }
        }

        protected class BeamSweepStage
        extends AINode<T>
        implements AttackStageInterface<T> {
            private boolean clockwise = GameRandom.globalRandom.nextBoolean();
            private LevelEvent event;
            public int noHealthSweepTime;
            public int fullHealthSweepTime;
            public float startAngleOffset;
            public float totalAngleCover;

            public BeamSweepStage(int noHealthSweepTime, int fullHealthSweepTime, float startAngleOffset, float totalAngleCover) {
                this.noHealthSweepTime = noHealthSweepTime;
                this.fullHealthSweepTime = fullHealthSweepTime;
                this.startAngleOffset = startAngleOffset;
                this.totalAngleCover = totalAngleCover;
            }

            @Override
            protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
            }

            @Override
            public void init(T mob, Blackboard<T> blackboard) {
            }

            @Override
            public AINodeResult tick(T mob, Blackboard<T> blackboard) {
                if (this.event.isOver()) {
                    return AINodeResult.SUCCESS;
                }
                return AINodeResult.RUNNING;
            }

            @Override
            public void onStarted(T mob, Blackboard<T> blackboard) {
                Mob currentTarget = blackboard.getObject(Mob.class, "currentTarget");
                long startTime = ((Entity)mob).getWorldEntity().getTime();
                float startAngle = currentTarget != null ? GameMath.getAngle(new Point2D.Float(currentTarget.x - ((FallenWizardMob)mob).x, currentTarget.y - ((FallenWizardMob)mob).y)) : (float)GameRandom.globalRandom.nextInt(360);
                float endAngle = this.clockwise ? (startAngle += this.startAngleOffset) - this.totalAngleCover : (startAngle -= this.startAngleOffset) + this.totalAngleCover;
                int sweepTime = FallenWizardAI.this.getHealthPercValue(mob, this.noHealthSweepTime, this.fullHealthSweepTime);
                this.event = new FallenWizardBeamLevelEvent((Mob)mob, startAngle, endAngle, startTime, sweepTime, GameRandom.globalRandom.nextInt(), 1400.0f, laserDamage, 50, 500, 0);
                ((Entity)mob).getLevel().entityManager.events.add(this.event);
                this.clockwise = !this.clockwise;
                ((FallenWizardMob)mob).humanSoundAbility.runAndSend(1);
            }

            @Override
            public void onEnded(T mob, Blackboard<T> blackboard) {
            }
        }

        protected class BulletHellStage
        extends AINode<T>
        implements AttackStageInterface<T> {
            private long nextShowAttackTime;
            private float currentAngle;
            private float remainingAngle;
            private float angleBuffer;
            private int attackDirection;
            private int direction;
            private long startTime;
            private int currentShootTime;
            public float targetStartAngleOffset;
            public float totalAngle;
            public float anglePerProjectile;
            public int arms;
            public int chargeUpTime;
            public int noHealthShootTime;
            public int fullHealthShootTime;

            public BulletHellStage(int chargeUpTime, float targetStartAngleOffset, float totalAngle, int arms, int noHealthShootTime, int fullHealthShootTime, float anglePerProjectile) {
                this.targetStartAngleOffset = targetStartAngleOffset;
                this.totalAngle = totalAngle;
                this.arms = arms;
                this.chargeUpTime = chargeUpTime;
                this.noHealthShootTime = noHealthShootTime;
                this.fullHealthShootTime = fullHealthShootTime;
                this.anglePerProjectile = anglePerProjectile;
                this.direction = GameRandom.globalRandom.getOneOf(-1, 1);
            }

            @Override
            protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
            }

            @Override
            public void init(T mob, Blackboard<T> blackboard) {
            }

            @Override
            public AINodeResult tick(T mob, Blackboard<T> blackboard) {
                long currentTime = ((Entity)mob).getWorldEntity().getTime();
                long timePassed = currentTime - this.startTime;
                if (this.nextShowAttackTime <= currentTime) {
                    ((AttackAnimMob)mob).showAttack((int)(((FallenWizardMob)mob).x + (float)(this.attackDirection * 30)), (int)(((FallenWizardMob)mob).y - 100.0f), false);
                    this.nextShowAttackTime = currentTime + 200L;
                    if (timePassed >= (long)this.chargeUpTime) {
                        ((FallenWizardMob)mob).magicBoltSound.runAndSend();
                    }
                    if (((Entity)mob).isServer()) {
                        ((Entity)mob).getLevel().getServer().network.sendToClientsWithEntity(new PacketMobAttack((Mob)mob, (int)(((FallenWizardMob)mob).x + (float)(this.attackDirection * 30)), (int)(((FallenWizardMob)mob).y - 100.0f), false), (RegionPositionGetter)mob);
                    }
                }
                if (timePassed < (long)this.chargeUpTime) {
                    return AINodeResult.RUNNING;
                }
                this.angleBuffer += this.totalAngle * 50.0f / (float)this.currentShootTime;
                while (this.angleBuffer >= this.anglePerProjectile) {
                    this.currentAngle += this.anglePerProjectile * (float)this.direction;
                    for (int i = 0; i < this.arms; ++i) {
                        float thisAngle = this.currentAngle + 360.0f / (float)this.arms * (float)i;
                        FallenWizardBallProjectile projectile = new FallenWizardBallProjectile(((Entity)mob).getLevel(), (Mob)mob, ((FallenWizardMob)mob).x + (float)(this.attackDirection * 15), ((FallenWizardMob)mob).y - 35.0f, thisAngle, 90.0f, 1200, ballDamage, 50);
                        ((Entity)mob).getLevel().entityManager.projectiles.add(projectile);
                    }
                    this.angleBuffer -= this.anglePerProjectile;
                    this.remainingAngle -= this.anglePerProjectile;
                    if (!(this.remainingAngle <= 0.0f)) continue;
                }
                if (this.angleBuffer >= this.remainingAngle) {
                    return AINodeResult.SUCCESS;
                }
                return AINodeResult.RUNNING;
            }

            @Override
            public void onStarted(T mob, Blackboard<T> blackboard) {
                this.direction *= -1;
                Mob target = blackboard.getObject(Mob.class, "currentTarget");
                this.currentAngle = target != null ? GameMath.getAngle(new Point2D.Float(target.x - ((FallenWizardMob)mob).x, target.y - ((FallenWizardMob)mob).y)) + this.targetStartAngleOffset * (float)this.direction + 90.0f : (float)GameRandom.globalRandom.nextInt(360);
                this.attackDirection = (int)Math.signum(GameMath.getAngleDir((float)this.currentAngle).x);
                if (this.attackDirection == 0) {
                    this.attackDirection = GameRandom.globalRandom.getOneOf(-1, 1);
                }
                this.remainingAngle = this.totalAngle;
                this.startTime = ((Entity)mob).getLevel().getWorldEntity().getTime();
                this.currentShootTime = FallenWizardAI.this.getHealthPercValue(mob, this.noHealthShootTime, this.fullHealthShootTime);
                this.nextShowAttackTime = 0L;
                ((FallenWizardMob)mob).humanSoundAbility.runAndSend(1);
            }

            @Override
            public void onEnded(T mob, Blackboard<T> blackboard) {
            }
        }

        protected class DragonSpawnStage
        extends AINode<T>
        implements AttackStageInterface<T> {
            private Point2D.Float direction;
            private int dragonsSpawned;
            private long lastSpawnTime;
            private long nextShowAttackTime;
            private int currentTimeGap;
            public int noHealthStartDelay;
            public int fullHealthStartDelay;
            public int noHealthTimeGap;
            public int fullHealthTimeGap;
            public int dragons;
            private float circlingOffset;

            public DragonSpawnStage(int noHealthStartDelay, int fullHealthStartDelay, int noHealthTimeGap, int fullHealthTimeGap, int dragons) {
                this.noHealthStartDelay = noHealthStartDelay;
                this.fullHealthStartDelay = fullHealthStartDelay;
                this.noHealthTimeGap = noHealthTimeGap;
                this.fullHealthTimeGap = fullHealthTimeGap;
                this.dragons = dragons;
            }

            @Override
            protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
            }

            @Override
            public void init(T mob, Blackboard<T> blackboard) {
            }

            @Override
            public AINodeResult tick(T mob, Blackboard<T> blackboard) {
                long currentTime = ((Entity)mob).getWorldEntity().getTime();
                if (this.nextShowAttackTime <= currentTime) {
                    ((AttackAnimMob)mob).showAttack((int)(((FallenWizardMob)mob).x + this.direction.x * 100.0f), (int)(((FallenWizardMob)mob).y + this.direction.y * 100.0f), false);
                    this.nextShowAttackTime = currentTime + 100L;
                    if (((Entity)mob).isServer()) {
                        ((Entity)mob).getLevel().getServer().network.sendToClientsWithEntity(new PacketMobAttack((Mob)mob, (int)(((FallenWizardMob)mob).x + this.direction.x * 100.0f), (int)(((FallenWizardMob)mob).y + this.direction.y * 100.0f), false), (RegionPositionGetter)mob);
                    }
                }
                long timeSinceLastProjectile = currentTime - this.lastSpawnTime;
                while (this.dragonsSpawned < this.dragons && timeSinceLastProjectile >= (long)this.currentTimeGap) {
                    this.lastSpawnTime += (long)this.currentTimeGap;
                    timeSinceLastProjectile = currentTime - this.lastSpawnTime;
                    Point2D.Float perpDir = GameMath.getPerpendicularDir(this.direction.x, this.direction.y);
                    int perpDistance = GameRandom.globalRandom.getIntBetween(-150, 150);
                    int behindDistance = GameRandom.globalRandom.getIntBetween(150, 300);
                    float startX = ((FallenWizardMob)mob).x + perpDir.x * (float)perpDistance - this.direction.x * (float)behindDistance;
                    float startY = ((FallenWizardMob)mob).y + perpDir.y * (float)perpDistance - this.direction.y * (float)behindDistance;
                    ++this.dragonsSpawned;
                    ((FallenWizardMob)mob).spawnDragonEffects.runAndSend((int)startX, (int)startY);
                    FallenDragonHead dragon = (FallenDragonHead)MobRegistry.getMob("fallendragon", ((Entity)mob).getLevel());
                    dragon.master = mob;
                    dragon.circlingAngleOffset = this.circlingOffset;
                    if (((FallenWizardMob)mob).isInArena()) {
                        dragon.centerPosition = TempleArenaLevel.getBossPosition();
                    }
                    ((Entity)mob).getLevel().entityManager.addMob(dragon, startX, startY);
                    this.circlingOffset += 360.0f / (float)this.dragons;
                }
                if (this.dragonsSpawned < this.dragons) {
                    return AINodeResult.RUNNING;
                }
                this.circlingOffset += 360.0f / (float)this.dragons / 2.0f + 18.0f;
                return AINodeResult.SUCCESS;
            }

            @Override
            public void onStarted(T mob, Blackboard<T> blackboard) {
                Mob target = blackboard.getObject(Mob.class, "currentTarget");
                Point2D.Float targetPos = target != null ? new Point2D.Float(target.x, target.y) : (((FallenWizardMob)mob).isInArena() ? TempleArenaLevel.getBossPosition() : new Point2D.Float(((FallenWizardMob)mob).x, ((FallenWizardMob)mob).y + 100.0f));
                this.direction = GameMath.normalize(targetPos.x - ((FallenWizardMob)mob).x, targetPos.y - ((FallenWizardMob)mob).y);
                if (this.direction.x == 0.0f && this.direction.y == 0.0f) {
                    this.direction = new Point2D.Float(0.0f, 1.0f);
                }
                this.nextShowAttackTime = 0L;
                this.dragonsSpawned = 0;
                this.currentTimeGap = FallenWizardAI.this.getHealthPercValue(mob, this.noHealthTimeGap, this.fullHealthTimeGap);
                int startDelay = FallenWizardAI.this.getHealthPercValue(mob, this.noHealthStartDelay, this.fullHealthStartDelay);
                this.lastSpawnTime = ((Entity)mob).getWorldEntity().getTime() - (long)(this.currentTimeGap - startDelay);
                ((FallenWizardMob)mob).humanSoundAbility.runAndSend(1);
            }

            @Override
            public void onEnded(T mob, Blackboard<T> blackboard) {
            }
        }

        protected class ScepterProjectilesStage
        extends AINode<T>
        implements AttackStageInterface<T> {
            private Mob target;
            private long startTime;
            private int currentTimeSpan;
            private float projectileBuffer;
            private long nextShowAttackTime;
            public int projectiles;
            public int noHealthTimeSpan;
            public int fullHealthTimeSpan;

            public ScepterProjectilesStage(int projectiles, int noHealthTimeSpan, int fullHealthTimeSpan) {
                this.projectiles = projectiles;
                this.noHealthTimeSpan = noHealthTimeSpan;
                this.fullHealthTimeSpan = fullHealthTimeSpan;
            }

            @Override
            protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
            }

            @Override
            public void init(T mob, Blackboard<T> blackboard) {
            }

            @Override
            public AINodeResult tick(T mob, Blackboard<T> blackboard) {
                long currentTime = ((Entity)mob).getWorldEntity().getTime();
                if (currentTime >= this.startTime + (long)this.currentTimeSpan) {
                    return AINodeResult.SUCCESS;
                }
                if (this.target == null || this.target.removed()) {
                    this.target = blackboard.getObject(Mob.class, "currentTarget");
                }
                if (this.target != null) {
                    if (this.nextShowAttackTime <= currentTime) {
                        ((AttackAnimMob)mob).showAttack(this.target.getX(), this.target.getY(), false);
                        this.nextShowAttackTime = currentTime + 100L;
                        if (((Entity)mob).isServer()) {
                            ((Entity)mob).getLevel().getServer().network.sendToClientsWithEntity(new PacketMobAttack((Mob)mob, this.target.getX(), this.target.getY(), false), (RegionPositionGetter)mob);
                        }
                    }
                    this.projectileBuffer += TickManager.getTickDelta((float)this.currentTimeSpan / 1000.0f / (float)this.projectiles);
                    if (this.projectileBuffer >= 1.0f) {
                        ((FallenWizardMob)mob).laserBlastSound.runAndSend();
                        while (this.projectileBuffer >= 1.0f) {
                            this.projectileBuffer -= 1.0f;
                            int targetX = this.target.getX() + GameRandom.globalRandom.getIntBetween(-40, 40);
                            int targetY = this.target.getY() + GameRandom.globalRandom.getIntBetween(-40, 40);
                            Point2D.Float dir = GameMath.normalize((float)targetX - ((FallenWizardMob)mob).x, (float)targetY - ((FallenWizardMob)mob).y);
                            Point2D.Float perpDir = GameMath.getPerpendicularDir(dir.x, dir.y);
                            int perpRange = GameRandom.globalRandom.getIntBetween(-20, 20);
                            int range = 1500;
                            FallenWizardScepterProjectile proj = new FallenWizardScepterProjectile(((FallenWizardMob)mob).x + perpDir.x * (float)perpRange, ((FallenWizardMob)mob).y + perpDir.y * (float)perpRange, ((FallenWizardMob)mob).x - dir.x * 100.0f, ((FallenWizardMob)mob).y - dir.y * 100.0f, 250, range, scepterDamage, 50, (Mob)mob);
                            proj.setAngle(proj.getAngle() + (float)GameRandom.globalRandom.getIntBetween(-140, 140));
                            proj.targetPos = new Point(targetX, targetY);
                            proj.turnSpeed = GameRandom.globalRandom.getFloatBetween(1.0f, 2.0f);
                            proj.angleLeftToTurn = 240.0f;
                            proj.resetUniqueID(GameRandom.globalRandom);
                            ((Entity)mob).getLevel().entityManager.projectiles.add(proj);
                        }
                    }
                }
                return AINodeResult.RUNNING;
            }

            @Override
            public void onStarted(T mob, Blackboard<T> blackboard) {
                this.target = null;
                this.startTime = ((Entity)mob).getWorldEntity().getTime();
                this.projectileBuffer = 0.0f;
                this.nextShowAttackTime = 0L;
                this.currentTimeSpan = FallenWizardAI.this.getHealthPercValue(mob, this.noHealthTimeSpan, this.fullHealthTimeSpan);
                ((FallenWizardMob)mob).humanSoundAbility.runAndSend(1);
            }

            @Override
            public void onEnded(T mob, Blackboard<T> blackboard) {
            }
        }
    }

    public static class PortalParticle
    extends Particle {
        public PortalParticle(Level level, int x, int y, long lifeTime) {
            super(level, x, y, 0.0f, 0.0f, lifeTime);
        }

        @Override
        public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
            if (this.removed()) {
                return;
            }
            GameLight light = level.getLightLevel(this);
            int drawX = camera.getDrawX(this.x);
            int drawY = camera.getDrawY(this.y);
            long lifeCycleTime = this.getLifeCycleTime();
            float alpha = 1.0f;
            if (lifeCycleTime >= this.lifeTime - 500L) {
                float fadeOutPerc = GameMath.limit((float)(lifeCycleTime - (this.lifeTime - 500L)) / 500.0f, 0.0f, 1.0f);
                alpha = Math.abs(fadeOutPerc - 1.0f);
            }
            float angle = (float)lifeCycleTime / 2.0f;
            final TextureDrawOptionsEnd drawOptions = GameResources.fallenWizardPortalParticle.initDraw().light(light).alpha(alpha).rotate(-angle).posMiddle(drawX, drawY - 15);
            list.add(new EntityDrawable(this){

                @Override
                public void draw(TickManager tickManager) {
                    drawOptions.draw();
                }
            });
        }
    }

    public static class TeleportParticle
    extends Particle {
        private final Point animSprite;
        private int dir;

        public TeleportParticle(Level level, int x, int y, FallenWizardMob owner) {
            super(level, x, y, 0.0f, 0.0f, 1000L);
            this.animSprite = owner.getAnimSprite(x, y, this.dir);
            this.dir = owner.getDir();
        }

        @Override
        public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
            float life = this.getLifeCyclePercent();
            if (this.removed()) {
                return;
            }
            GameLight light = level.getLightLevel(this);
            int drawX = camera.getDrawX(this.x) - 48;
            int drawY = camera.getDrawY(this.y) - 70;
            float alpha = Math.abs(life - 1.0f);
            final DrawOptions drawOptions = new HumanDrawOptions(level, MobRegistry.Textures.fallenWizard).sprite(this.animSprite, 96).size(96, 96).dir(this.dir).light(light).alpha(alpha).pos(drawX, drawY);
            list.add(new EntityDrawable(this){

                @Override
                public void draw(TickManager tickManager) {
                    drawOptions.draw();
                }
            });
        }
    }
}

