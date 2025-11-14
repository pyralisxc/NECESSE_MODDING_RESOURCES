/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile.bosses;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import necesse.engine.achievements.AchievementManager;
import necesse.engine.eventStatusBars.EventStatusBarManager;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.packet.PacketChatMessage;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.MusicRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.Entity;
import necesse.entity.levelEvent.mobAbilityLevelEvent.EvilsProtectorBombAttackEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.MaxHealthGetter;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.MobHealthScaling;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ability.CoordinateMobAbility;
import necesse.entity.mobs.ability.EmptyMobAbility;
import necesse.entity.mobs.ability.IntMobAbility;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.composites.SelectorAINode;
import necesse.entity.mobs.ai.behaviourTree.decorators.IsolateRunningAINode;
import necesse.entity.mobs.buffs.staticBuffs.BossNearbyBuff;
import necesse.entity.mobs.hostile.bosses.BossMob;
import necesse.entity.mobs.hostile.bosses.EvilsPortalMob;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.AttackStageInterface;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.AttackStageManagerNode;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.AttackStageSkipTo;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.IdleTimeAttackStage;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.entity.particle.SmokePuffParticle;
import necesse.entity.projectile.EvilsProtectorAttack1Projectile;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.followingProjectile.EvilsProtectorAttack2Projectile;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.inventory.lootTable.lootItem.ConditionLootItem;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.RotationLootItem;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.regionSystem.RegionPositionGetter;

public class EvilsProtectorMob
extends BossMob {
    public static LootTable lootTable = new LootTable(new LootItem("demonicbar", 12), new ChanceLootItem(0.2f, "firsttrialvinyl"), new ChanceLootItem(0.1f, "eyeinaportal"));
    public static LootTable privateLootTable = new LootTable(new ConditionLootItem("demonheart", (r, o) -> {
        ServerClient client = LootTable.expectExtra(ServerClient.class, o, 1);
        return client != null && client.playerMob.healthUpgradeManager.canUpgrade("demonheart") && client.playerMob.getInv().getAmount(ItemRegistry.getItem("demonheart"), false, false, true, true, "have") == 0;
    }), new ConditionLootItem("demoncloak", (r, o) -> {
        ServerClient client = LootTable.expectExtra(ServerClient.class, o, 1);
        return client != null && client.playerMob.getInv().equipment.getTotalSets() < 2 && client.playerMob.getInv().getAmount(ItemRegistry.getItem("demoncloak"), false, false, true, true, "have") == 0;
    }), new LootItem("forceofwind", new GNDItemMap().setString("enchantment", "")), RotationLootItem.privateLootRotation(new LootItem("magicfoci", new GNDItemMap().setString("enchantment", "")), new LootItem("rangefoci", new GNDItemMap().setString("enchantment", "")), new LootItem("meleefoci", new GNDItemMap().setString("enchantment", "")), new LootItem("summonfoci", new GNDItemMap().setString("enchantment", ""))));
    public static GameDamage landDamage = new GameDamage(50.0f);
    public static GameDamage bombDamage = new GameDamage(19.0f);
    public static GameDamage boltDamage = new GameDamage(25.0f);
    public static GameDamage volleyFireballDamage = new GameDamage(19.0f);
    public static GameDamage waveFireballDamage = new GameDamage(19.0f);
    public static GameDamage minionDamage = new GameDamage(15.0f);
    public static MaxHealthGetter MAX_HEALTH = new MaxHealthGetter(1500, 2500, 3000, 3500, 4000);
    private static final int[] flyUpAnimFrames = new int[]{800, 700, 50, 50};
    private static final int flyUpAnimFramesTotal = Arrays.stream(flyUpAnimFrames).reduce(0, Integer::sum);
    private static final int[] flyAnimFrames = new int[]{50, 200, 100, 100};
    private static final float[] attackAnimFrameParts = new float[]{0.4f, 0.2f, 0.4f};
    private final int[] attackAnimFrames = new int[]{200, 100, 200};
    protected MobHealthScaling scaling = new MobHealthScaling(this);
    public ArrayList<Mob> spawnedPortals = new ArrayList();
    public ArrayList<Projectile> spawnedProjectiles = new ArrayList();
    protected boolean isAttack2;
    protected boolean isFlying;
    protected boolean isFlyingUp;
    private long flyUpTime;
    protected boolean isLanding;
    protected boolean playedLandingSound;
    private long landingTime;
    public final EmptyMobAbility flyUpAbility;
    public final EmptyMobAbility fireSoundAbility;
    public final CoordinateMobAbility landAbility;
    public final IntMobAbility attackAbility;

    public EvilsProtectorMob() {
        super(100);
        this.difficultyChanges.setMaxHealth(MAX_HEALTH);
        this.setArmor(10);
        this.setFriction(10.0f);
        this.collision = new Rectangle(-40, -40, 80, 60);
        this.hitBox = new Rectangle(-40, -40, 80, 60);
        this.selectBox = new Rectangle(-60, -50, 120, 70);
        this.setDir(0);
        this.attackAnimTime = Arrays.stream(this.attackAnimFrames).reduce(0, Integer::sum);
        this.setKnockbackModifier(0.0f);
        this.flyUpAbility = this.registerAbility(new EmptyMobAbility(){

            @Override
            protected void run() {
                EvilsProtectorMob.this.isFlyingUp = true;
                EvilsProtectorMob.this.flyUpTime = EvilsProtectorMob.this.getWorldEntity().getTime();
                if (EvilsProtectorMob.this.isClient()) {
                    SoundManager.playSound(GameResources.dragonfly1, (SoundEffect)SoundEffect.effect(EvilsProtectorMob.this));
                }
            }
        });
        this.landAbility = this.registerAbility(new CoordinateMobAbility(){

            @Override
            protected void run(int x, int y) {
                EvilsProtectorMob.this.setPos(x, y, true);
                EvilsProtectorMob.this.isLanding = true;
                EvilsProtectorMob.this.landingTime = EvilsProtectorMob.this.getWorldEntity().getTime();
                EvilsProtectorMob.this.playedLandingSound = false;
            }
        });
        this.fireSoundAbility = this.registerAbility(new EmptyMobAbility(){

            @Override
            protected void run() {
                if (EvilsProtectorMob.this.isClient()) {
                    SoundManager.playSound(GameResources.magicbolt2, (SoundEffect)SoundEffect.effect(EvilsProtectorMob.this).pitch(GameRandom.globalRandom.getFloatBetween(0.8f, 1.2f)));
                }
            }
        });
        this.attackAbility = this.registerAbility(new IntMobAbility(){

            @Override
            protected void run(int value) {
                if (value <= 0) {
                    value = 500;
                }
                EvilsProtectorMob.this.attackAnimTime = value;
                for (int i = 0; i < attackAnimFrameParts.length; ++i) {
                    ((EvilsProtectorMob)EvilsProtectorMob.this).attackAnimFrames[i] = (int)((float)EvilsProtectorMob.this.attackAnimTime * attackAnimFrameParts[i]);
                }
                EvilsProtectorMob.this.attack(0, 0, false);
            }
        });
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextBoolean(this.isFlyingUp);
        if (this.isFlyingUp) {
            writer.putNextLong(this.flyUpTime);
        }
        writer.putNextBoolean(this.isLanding);
        if (this.isLanding) {
            writer.putNextLong(this.landingTime);
        }
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.isFlyingUp = reader.getNextBoolean();
        if (this.isFlyingUp) {
            this.flyUpTime = reader.getNextLong();
        }
        this.isLanding = reader.getNextBoolean();
        if (this.isLanding) {
            this.landingTime = reader.getNextLong();
        }
    }

    @Override
    public void setupMovementPacket(PacketWriter writer) {
        super.setupMovementPacket(writer);
        writer.putNextBoolean(this.isFlying);
    }

    @Override
    public void applyMovementPacket(PacketReader reader, boolean isDirect) {
        super.applyMovementPacket(reader, isDirect);
        this.isFlying = reader.getNextBoolean();
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
        this.ai = new BehaviourTreeAI<EvilsProtectorMob>(this, new BalorAINew());
        if (this.isClient()) {
            SoundManager.playSound(GameResources.evilsprotectorbegin, (SoundEffect)SoundEffect.effect(this).volume(1.2f).falloffDistance(4000));
        } else if (this.isServer()) {
            this.isLanding = true;
            this.landingTime = this.getWorldEntity().getTime();
        }
    }

    @Override
    public int getFlyingHeight() {
        if (this.isFlying) {
            return 1000;
        }
        return super.getFlyingHeight();
    }

    @Override
    public boolean canTakeDamage() {
        return !this.isFlying() && !this.isLanding;
    }

    @Override
    public boolean canLevelInteract() {
        return !this.isFlying();
    }

    @Override
    public boolean canPushMob(Mob other) {
        if (this.isFlying()) {
            return false;
        }
        return super.canPushMob(other);
    }

    @Override
    public boolean isVisible() {
        return !this.isFlying();
    }

    @Override
    public boolean canBePushed(Mob other) {
        return false;
    }

    @Override
    public int getMaxHealth() {
        return super.getMaxHealth() + (int)((float)(this.scaling == null ? 0 : this.scaling.getHealthIncrease()) * this.getMaxHealthModifier());
    }

    @Override
    public void clientTick() {
        super.clientTick();
        if (this.isClientPlayerNearby()) {
            SoundManager.setMusic(MusicRegistry.TheFirstTrial, SoundManager.MusicPriority.EVENT, 1.5f);
            EventStatusBarManager.registerMobHealthStatusBar(this);
        }
        BossNearbyBuff.applyAround(this);
        if (!this.isFlying || this.isLanding && this.playedLandingSound) {
            this.getLevel().lightManager.refreshParticleLightFloat(this.x, this.y, 270.0f, 0.7f);
        }
        if (this.isFlyingUp) {
            long flyUpSince = this.getWorldEntity().getTime() - this.flyUpTime;
            if (flyUpSince >= (long)(flyUpAnimFramesTotal + 1000)) {
                this.isFlyingUp = false;
            } else if (!this.isFlying && flyUpSince >= (long)flyUpAnimFramesTotal) {
                this.isFlying = true;
            }
        } else if (this.isLanding) {
            long landSince = this.getWorldEntity().getTime() - this.landingTime;
            if (landSince >= 2000L) {
                this.isFlying = false;
                this.isLanding = false;
                this.playedLandingSound = false;
                int particles = 200;
                float anglePerParticle = 360.0f / (float)particles;
                for (int i = 0; i < particles; ++i) {
                    int angle = (int)((float)i * anglePerParticle + GameRandom.globalRandom.nextFloat() * anglePerParticle);
                    float dx = (float)Math.sin(Math.toRadians(angle)) * (float)GameRandom.globalRandom.getIntBetween(50, 150);
                    float dy = (float)Math.cos(Math.toRadians(angle)) * (float)GameRandom.globalRandom.getIntBetween(50, 150) * 0.8f;
                    this.getLevel().entityManager.addParticle(this.x, this.y, i % 4 == 0 ? Particle.GType.IMPORTANT_COSMETIC : Particle.GType.COSMETIC).movesFriction(dx, dy, 0.8f).color(new Color(50, 50, 50)).heightMoves(0.0f, 30.0f).lifeTime(1000);
                }
                SoundManager.playSound(GameResources.magicbolt3, (SoundEffect)SoundEffect.effect(this));
                this.getLevel().getClient().startCameraShake(this, 400, 40, 12.0f, 12.0f, false);
            } else if (!this.playedLandingSound && landSince >= 1000L) {
                SoundManager.playSound(GameResources.swoosh, (SoundEffect)SoundEffect.effect(this).volume(0.6f));
                this.playedLandingSound = true;
            }
        }
    }

    @Override
    public void serverTick() {
        long landSince;
        super.serverTick();
        this.scaling.serverTick();
        BossNearbyBuff.applyAround(this);
        if (this.isFlyingUp) {
            long flyUpSince = this.getWorldEntity().getTime() - this.flyUpTime;
            if (flyUpSince >= (long)flyUpAnimFramesTotal) {
                this.isFlying = true;
                this.isFlyingUp = false;
            }
        } else if (this.isLanding && (landSince = this.getWorldEntity().getTime() - this.landingTime) >= 2000L) {
            this.isFlying = false;
            this.isLanding = false;
            int size = 300;
            int halfSize = size / 2;
            Ellipse2D.Float hitBox = new Ellipse2D.Float(this.x - (float)halfSize, this.y - (float)halfSize * 0.8f, size, (float)size * 0.8f);
            GameUtils.streamTargets(this, GameUtils.rangeTileBounds(this.getX(), this.getY(), 5)).filter(m -> m.canBeHit(this) && hitBox.intersects(m.getHitBox())).forEach(m -> m.isServerHit(landDamage, (float)m.getX() - this.x, (float)m.getY() - this.y, 100.0f, this));
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
    public void playHurtSound() {
        float pitch = GameRandom.globalRandom.getOneOf(Float.valueOf(0.95f), Float.valueOf(1.0f), Float.valueOf(1.05f)).floatValue();
        SoundManager.playSound(GameResources.evilsprotectorhurt, (SoundEffect)SoundEffect.effect(this).volume(0.3f).pitch(pitch).falloffDistance(1500));
    }

    @Override
    public void playDeathSound() {
        SoundManager.playSound(GameResources.evilsprotectordeath, (SoundEffect)SoundEffect.effect(this).falloffDistance(4000));
    }

    @Override
    public void showAttack(int x, int y, int seed, boolean showAllDirections) {
        super.showAttack(x, y, seed, showAllDirections);
        this.isAttack2 = !this.isAttack2;
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for (int i = 0; i < 5; ++i) {
            this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), MobRegistry.Textures.evilsProtector2, i, 2, 32, this.x, this.y, 10.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    @Override
    public void spawnRemoveParticles(float knockbackX, float knockbackY) {
        this.getLevel().entityManager.addParticle(new SmokePuffParticle(this.getLevel(), this.getX(), this.getY(), 160, new Color(50, 50, 50)), Particle.GType.CRITICAL);
    }

    @Override
    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        int drawY;
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(EvilsProtectorMob.getTileCoordinate(x), EvilsProtectorMob.getTileCoordinate(y));
        int drawX = camera.getDrawX(x) - 96;
        int bodyDrawY = drawY = camera.getDrawY(y) - 130;
        float alpha = 1.0f;
        float shadowAlpha = 1.0f;
        int spriteX = 0;
        int spriteY = 0;
        int shadowSpriteX = 0;
        int shadowSpriteY = 0;
        float attackProgress = this.getAttackAnimProgress();
        if (this.isAttacking) {
            int anim = GameUtils.getAnim((long)(attackProgress * (float)this.attackAnimTime), this.attackAnimFrames);
            spriteX = anim % 3;
            spriteY = (this.isAttack2 ? 2 : 1) + anim / 3;
        }
        if (this.isFlyingUp) {
            long flyUpSince = this.getWorldEntity().getTime() - this.flyUpTime;
            if (flyUpSince <= (long)(flyUpAnimFramesTotal + 1000)) {
                if (flyUpSince < (long)flyUpAnimFramesTotal) {
                    spriteX = GameUtils.getAnim(flyUpSince, flyUpAnimFrames);
                    spriteY = 0;
                    shadowSpriteX = spriteX;
                    shadowSpriteY = spriteY;
                } else {
                    long ascendTime = GameMath.limit(flyUpSince - (long)flyUpAnimFramesTotal, 0L, 1000L);
                    float ascendPercent = (float)ascendTime / 1000.0f;
                    shadowAlpha = alpha = Math.abs(ascendPercent - 1.0f);
                    bodyDrawY = (int)((float)bodyDrawY - ascendPercent * 500.0f);
                    spriteX = GameUtils.getAnimContinuous(ascendTime, flyAnimFrames);
                    spriteY = 0;
                    shadowSpriteX = spriteX;
                    shadowSpriteY = 1;
                }
            } else {
                shadowAlpha = 0.0f;
                alpha = 0.0f;
            }
        } else if (this.isLanding) {
            long landSince = this.getWorldEntity().getTime() - this.landingTime;
            if (landSince < 1000L) {
                alpha = 0.0f;
                shadowAlpha = (float)landSince / 1000.0f;
                shadowSpriteX = GameUtils.getAnimContinuous(landSince, flyAnimFrames);
                shadowSpriteY = 1;
            } else if (landSince < 2000L) {
                float landPerc;
                alpha = landPerc = (float)(landSince - 1000L) / 1000.0f;
                bodyDrawY = (int)((float)bodyDrawY - Math.abs(landPerc - 1.0f) * 500.0f);
                spriteX = GameUtils.getAnimContinuous(landSince, flyAnimFrames);
                spriteY = 0;
            }
        } else if (this.isFlying) {
            return;
        }
        final TextureDrawOptionsEnd body = MobRegistry.Textures.evilsProtector.initDraw().sprite(spriteX, spriteY, 192).light(light).alpha(alpha).pos(drawX, bodyDrawY);
        list.add(new MobDrawable(){

            @Override
            public void draw(TickManager tickManager) {
                body.draw();
            }
        });
        TextureDrawOptionsEnd shadow = MobRegistry.Textures.evilsProtector_shadow.initDraw().sprite(shadowSpriteX, shadowSpriteY, 192).light(light).alpha(shadowAlpha).pos(drawX, drawY);
        tileList.add(tm -> shadow.draw());
    }

    @Override
    public boolean shouldDrawOnMap() {
        return !this.isFlying;
    }

    @Override
    public void drawOnMap(TickManager tickManager, Client client, int x, int y, double tileScale, Rectangle drawBounds, boolean isMinimap) {
        super.drawOnMap(tickManager, client, x, y, tileScale, drawBounds, isMinimap);
        int drawX = x - 32;
        int drawY = y - 24;
        MobRegistry.Textures.evilsProtector2.initDraw().sprite(0, 0, 64).size(32, 32).draw(drawX, drawY);
        MobRegistry.Textures.evilsProtector2.initDraw().sprite(1, 0, 64).size(32, 32).draw(drawX + 32, drawY);
    }

    @Override
    public Rectangle drawOnMapBox(double tileScale, boolean isMinimap) {
        return new Rectangle(-23, -18, 46, 21);
    }

    @Override
    public GameTooltips getMapTooltips() {
        return new StringTooltips(this.getDisplayName() + " " + this.getHealth() + "/" + this.getMaxHealth());
    }

    @Override
    public void remove(float knockbackX, float knockbackY, Attacker attacker, boolean isDeath) {
        super.remove(knockbackX, knockbackY, attacker, isDeath);
        this.spawnedPortals.forEach(Mob::remove);
        this.spawnedProjectiles.forEach(Projectile::remove);
        this.spawnedPortals.clear();
        this.spawnedProjectiles.clear();
    }

    @Override
    protected void onDeath(Attacker attacker, HashSet<Attacker> attackers) {
        super.onDeath(attacker, attackers);
        attackers.stream().map(Attacker::getFirstPlayerOwner).filter(Objects::nonNull).filter(PlayerMob::isServerClient).map(PlayerMob::getServerClient).distinct().forEach(c -> c.sendChatMessage(new LocalMessage("misc", "bossdefeat", "name", this.getLocalization())));
        if (!this.isDamagedByPlayers) {
            AchievementManager.checkMeAndThisArmyKill(this.getLevel(), attackers);
        }
    }

    public static class BalorAINew<T extends EvilsProtectorMob>
    extends SelectorAINode<T> {
        private int escapeCounter;

        public BalorAINew() {
            this.addChild(new AINode<T>(){

                @Override
                protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
                    blackboard.onEvent("refreshBossDespawn", event -> escapeCounter = 0);
                }

                @Override
                public void init(T mob, Blackboard<T> blackboard) {
                }

                @Override
                public AINodeResult tick(T mob, Blackboard<T> blackboard) {
                    if (!((Entity)mob).getWorldEntity().isNight()) {
                        ((Mob)mob).remove();
                        ((Entity)mob).getLevel().getServer().network.sendToClientsWithEntity(new PacketChatMessage(new LocalMessage("misc", "epescape")), (RegionPositionGetter)mob);
                        return AINodeResult.SUCCESS;
                    }
                    ArrayList targets = GameUtils.streamServerClients(((Entity)mob).getLevel()).filter(c -> !c.isDead()).map(c -> c.playerMob).filter(t -> !t.removed() && t.getDistance((Mob)mob) < 1280.0f).collect(Collectors.toCollection(ArrayList::new));
                    blackboard.put("balorTargets", targets);
                    if (targets.isEmpty()) {
                        escapeCounter++;
                        if (escapeCounter >= 100) {
                            ((Mob)mob).remove();
                        }
                        return AINodeResult.FAILURE;
                    }
                    escapeCounter = 0;
                    return AINodeResult.FAILURE;
                }
            });
            AttackStageManagerNode attackStages = new AttackStageManagerNode();
            attackStages.allowSkippingBack = false;
            this.addChild(new IsolateRunningAINode(attackStages));
            attackStages.addChild(new IdleTimeAttackStage(3000));
            AttackStageManagerNode rotation = new PortalSpawningManagerNode(30000);
            rotation.addChild(new IdleTimeAttackStage(2500));
            rotation.addChild(new VolleyAttackStage());
            attackStages.addChild(rotation);
            attackStages.addChild(new FlyingBombAttackStage(0.75f, 0.8f, 10));
            attackStages.addChild(new LandAttackStage());
            rotation = new PortalSpawningManagerNode(30000);
            rotation.addChild(new IdleTimeAttackStage(2000));
            rotation.addChild(new VolleyAttackStage());
            rotation.addChild(new IdleTimeAttackStage(500));
            rotation.addChild(new HomingProjectileAttackStage());
            attackStages.addChild(rotation);
            attackStages.addChild(new FlyingBombAttackStage(0.5f, 0.55f, 10));
            attackStages.addChild(new LandAttackStage());
            rotation = new AttackStageManagerNode(AINodeResult.RUNNING);
            rotation.addChild(new IdleTimeAttackStage(2000));
            rotation.addChild(new VolleyWaveAttackStage(-65.0f, 200.0f, 2000, 12.5f));
            rotation.addChild(new IdleTimeAttackStage(300));
            rotation.addChild(new HomingProjectileAttackStage());
            attackStages.addChild(rotation);
            attackStages.addChild(new FlyingBombAttackStage(0.25f, 0.3f, 10));
            attackStages.addChild(new LandAttackStage());
            rotation = new PortalSpawningManagerNode(30000);
            rotation.addChild(new IdleTimeAttackStage(1000));
            rotation.addChild(new VolleyWaveAttackStage(-75.0f, 220.0f, 1500, 12.5f));
            rotation.addChild(new IdleTimeAttackStage(500));
            rotation.addChild(new HomingProjectileAttackStage());
            attackStages.addChild(rotation);
            attackStages.addChild(new AINode<T>(){

                @Override
                protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
                }

                @Override
                public void init(T mob, Blackboard<T> blackboard) {
                }

                @Override
                public AINodeResult tick(T mob, Blackboard<T> blackboard) {
                    return AINodeResult.RUNNING;
                }
            });
        }

        public ArrayList<Mob> getAttackers(T mob) {
            return ((Mob)mob).streamAttackers().map(Attacker::getAttackOwner).filter(m -> m != null && !m.removed()).filter(m -> m.isPlayer).filter(m -> m.isSamePlace((Entity)mob)).distinct().collect(Collectors.toCollection(ArrayList::new));
        }

        public Mob getRandomAttacker(T mob, List<PlayerMob> targets) {
            ArrayList<Mob> list = this.getAttackers(mob);
            if (list.isEmpty()) {
                return GameRandom.globalRandom.getOneOf(targets);
            }
            return GameRandom.globalRandom.getOneOf(list);
        }

        public void findLandingArea(T mob) {
            ArrayList<Point> possiblePositions = new ArrayList<Point>();
            ((Mob)mob).streamAttackers().map(Attacker::getAttackOwner).filter(m -> m != null && !m.removed()).filter(m -> m.isPlayer).filter(m -> m.isSamePlace((Entity)mob)).distinct().forEach(m -> {
                for (int x = -2; x <= 2; ++x) {
                    for (int y = -2; y <= 2; ++y) {
                        int levelX = m.getX() + x * 32;
                        int levelY = m.getY() + y * 32;
                        if (mob.collidesWith(mob.getLevel(), levelX, levelY)) continue;
                        possiblePositions.add(new Point(levelX, levelY));
                    }
                }
            });
            if (!possiblePositions.isEmpty()) {
                Point pos = (Point)GameRandom.globalRandom.getOneOf(possiblePositions);
                ((EvilsProtectorMob)mob).landAbility.runAndSend(pos.x, pos.y);
            } else {
                for (int x = -8; x < 8; ++x) {
                    for (int y = -8; y <= 8; ++y) {
                        int levelX = ((Entity)mob).getX() + x * 32;
                        int levelY = ((Entity)mob).getY() + y * 32;
                        if (((Mob)mob).collidesWith(((Entity)mob).getLevel(), levelX, levelY)) continue;
                        possiblePositions.add(new Point(levelX, levelY));
                    }
                }
                if (!possiblePositions.isEmpty()) {
                    Point pos = (Point)GameRandom.globalRandom.getOneOf(possiblePositions);
                    ((EvilsProtectorMob)mob).landAbility.runAndSend(pos.x, pos.y);
                } else {
                    ((EvilsProtectorMob)mob).landAbility.runAndSend(((Entity)mob).getX(), ((Entity)mob).getY());
                }
            }
        }

        public void spawnRandomGates(T mob, int count) {
            ((EvilsProtectorMob)mob).spawnedPortals.forEach(Mob::remove);
            ArrayList<Point> possibleSpawns = new ArrayList<Point>();
            EvilsPortalMob gate = new EvilsPortalMob();
            for (int x = -15; x <= 15; ++x) {
                for (int y = -15; y <= 15; ++y) {
                    if (x >= -2 && x <= 2 && y >= -2 && y <= 2) continue;
                    int levelX = x + ((Entity)mob).getTileX();
                    int levelY = y + ((Entity)mob).getTileY();
                    if (((Entity)mob).getLevel().isSolidTile(levelX, levelY) || gate.collidesWith(((Entity)mob).getLevel(), levelX * 32 + 16, levelY * 32 + 16)) continue;
                    possibleSpawns.add(new Point(levelX, levelY));
                }
            }
            for (int i = 0; i < count && !possibleSpawns.isEmpty(); ++i) {
                Point randomPoint = (Point)GameRandom.globalRandom.getOneOf(possibleSpawns);
                gate = new EvilsPortalMob();
                ((Entity)mob).getLevel().entityManager.addMob(gate, randomPoint.x * 32 + 16, randomPoint.y * 32 + 16);
                ((EvilsProtectorMob)mob).spawnedPortals.add(gate);
                possibleSpawns.remove(randomPoint);
            }
        }

        public class PortalSpawningManagerNode
        extends AttackStageManagerNode<T> {
            public int portalsCooldown;
            public int portalTimer;

            public PortalSpawningManagerNode(int portalsCooldownMS) {
                super(AINodeResult.RUNNING);
                this.portalsCooldown = portalsCooldownMS;
            }

            @Override
            public void onStarted(T mob, Blackboard<T> blackboard) {
                super.onStarted(mob, blackboard);
                this.portalTimer = this.portalsCooldown;
            }

            @Override
            public AINodeResult tick(T mob, Blackboard<T> blackboard) {
                ArrayList targets = blackboard.getObject(ArrayList.class, "balorTargets");
                float cooldownMod = GameMath.limit(1.0f + (float)(targets.size() - 1) / 2.0f, 1.0f, 3.0f);
                this.portalTimer = (int)((float)this.portalTimer + 50.0f * cooldownMod);
                if (this.portalTimer >= this.portalsCooldown) {
                    int totalGates = GameMath.limit(2 + (targets.size() - 1) / 2, 2, 10);
                    BalorAINew.this.spawnRandomGates(mob, totalGates);
                    this.portalTimer = 0;
                }
                return super.tick(mob, blackboard);
            }
        }

        public class VolleyAttackStage
        extends AINode<T> {
            @Override
            protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
            }

            @Override
            public void init(T mob, Blackboard<T> blackboard) {
            }

            @Override
            public AINodeResult tick(T mob, Blackboard<T> blackboard) {
                ArrayList targets = blackboard.getObject(ArrayList.class, "balorTargets");
                LinkedList<Float> firedAngles = new LinkedList<Float>();
                for (PlayerMob target : targets) {
                    float angleToTarget = Projectile.getAngleToTarget(((EvilsProtectorMob)mob).x, ((EvilsProtectorMob)mob).y, target.getX(), target.getY());
                    if (!firedAngles.stream().noneMatch(f -> Math.abs(GameMath.getAngleDifference(angleToTarget, f.floatValue())) < 20.0f)) continue;
                    float randomizedAngle = GameRandom.globalRandom.getFloatOffset(angleToTarget, 12.5f);
                    for (int i = -1; i <= 1; ++i) {
                        EvilsProtectorAttack1Projectile projectile = new EvilsProtectorAttack1Projectile(((EvilsProtectorMob)mob).x, ((EvilsProtectorMob)mob).y, randomizedAngle + (float)(i * 10), 70.0f, 800, volleyFireballDamage, (Mob)mob);
                        ((Entity)mob).getLevel().entityManager.projectiles.add(projectile);
                        ((EvilsProtectorMob)mob).spawnedProjectiles.add(projectile);
                    }
                    firedAngles.add(Float.valueOf(angleToTarget));
                }
                ((EvilsProtectorMob)mob).fireSoundAbility.runAndSend();
                ((EvilsProtectorMob)mob).attackAbility.runAndSend(500);
                return AINodeResult.SUCCESS;
            }
        }

        public class FlyingBombAttackStage
        extends FlyingAttackStage
        implements AttackStageSkipTo<T>,
        AttackStageInterface<T> {
            public float startHealthPerc;
            public float bombsPerSecond;
            public int secondsToStayFlying;
            public int landTicker;
            public float bombBuffer;
            public boolean started;

            public FlyingBombAttackStage(float startHealthPerc, float bombsPerSecond, int secondsToStayFlying) {
                this.startHealthPerc = startHealthPerc;
                this.bombsPerSecond = bombsPerSecond;
                this.secondsToStayFlying = secondsToStayFlying;
            }

            @Override
            protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
            }

            @Override
            public void init(T mob, Blackboard<T> blackboard) {
            }

            @Override
            public boolean shouldSkipTo(T mob, boolean isNextStage) {
                if (this.started) {
                    return false;
                }
                float healthPerc = (float)((Mob)mob).getHealth() / (float)((EvilsProtectorMob)mob).getMaxHealth();
                return healthPerc <= this.startHealthPerc;
            }

            @Override
            public void onStarted(T mob, Blackboard<T> blackboard) {
                this.landTicker = 20 * this.secondsToStayFlying;
                this.bombBuffer = 0.0f;
                this.started = true;
            }

            @Override
            public void onEnded(T mob, Blackboard<T> blackboard) {
            }

            @Override
            public AINodeResult tickFlying(T mob, Blackboard<T> blackboard) {
                this.bombBuffer += 1.0f / this.bombsPerSecond / 20.0f;
                if (this.bombBuffer >= 1.0f) {
                    this.bombBuffer -= 1.0f;
                    ArrayList<Mob> attackers = BalorAINew.this.getAttackers(mob);
                    for (int i = 0; i < Math.min(5, attackers.size()); ++i) {
                        int index = GameRandom.globalRandom.nextInt(attackers.size());
                        Mob target = attackers.remove(index);
                        if (target == null) continue;
                        Mob mount = target.getMount();
                        HashSet<Mob> mounts = new HashSet<Mob>();
                        mounts.add(target);
                        while (mount != null) {
                            mounts.add(target);
                            target = mount;
                            if (!mounts.contains(mount = target.getMount())) continue;
                        }
                        ((Entity)mob).getLevel().entityManager.events.add(new EvilsProtectorBombAttackEvent((Mob)mob, GameRandom.globalRandom.getIntOffset((int)(target.x + target.dx * 3.0f), 50), GameRandom.globalRandom.getIntOffset((int)(target.y + target.dy * 3.0f), 50), GameRandom.globalRandom, bombDamage));
                    }
                }
                --this.landTicker;
                if (this.landTicker <= 0) {
                    BalorAINew.this.findLandingArea(mob);
                    return AINodeResult.SUCCESS;
                }
                return AINodeResult.RUNNING;
            }
        }

        public class LandAttackStage
        extends LandedAttackStage {
            @Override
            protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
            }

            @Override
            public void init(T mob, Blackboard<T> blackboard) {
            }

            @Override
            public AINodeResult tickLanded(T mob, Blackboard<T> blackboard) {
                return AINodeResult.SUCCESS;
            }
        }

        public class HomingProjectileAttackStage
        extends AINode<T> {
            protected float projectilesBuffer;

            @Override
            protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
            }

            @Override
            public void init(T mob, Blackboard<T> blackboard) {
            }

            @Override
            public AINodeResult tick(T mob, Blackboard<T> blackboard) {
                ArrayList targets = blackboard.getObject(ArrayList.class, "balorTargets");
                int targetsSize = targets.size();
                this.projectilesBuffer = (float)((double)this.projectilesBuffer + Math.pow(1.0f / (float)(targetsSize + 1), 0.5) * (double)targetsSize);
                if (this.projectilesBuffer >= 1.0f) {
                    int projectiles = (int)this.projectilesBuffer;
                    this.projectilesBuffer -= (float)projectiles;
                    ArrayList homingTargets = GameRandom.globalRandom.getCountOf(projectiles, targets);
                    for (PlayerMob target : homingTargets) {
                        EvilsProtectorAttack2Projectile projectile = new EvilsProtectorAttack2Projectile(((Entity)mob).getLevel(), (Mob)mob, target, boltDamage);
                        ((Entity)mob).getLevel().entityManager.projectiles.add(projectile);
                        ((EvilsProtectorMob)mob).spawnedProjectiles.add(projectile);
                    }
                    ((EvilsProtectorMob)mob).fireSoundAbility.runAndSend();
                    ((EvilsProtectorMob)mob).attackAbility.runAndSend(500);
                }
                return AINodeResult.SUCCESS;
            }
        }

        public class VolleyWaveAttackStage
        extends AINode<T>
        implements AttackStageInterface<T> {
            public float targetStartAngleOffset;
            public float totalAngle;
            public float anglePerProjectile;
            public int totalTime;
            public boolean initial;
            public float currentAngle;
            public float remainingAngle;
            public float angleBuffer;
            public int direction;

            public VolleyWaveAttackStage(float targetStartAngleOffset, float totalAngle, int totalTimeMS, float anglePerProjectile) {
                this.targetStartAngleOffset = targetStartAngleOffset;
                this.totalAngle = totalAngle;
                this.totalTime = totalTimeMS;
                this.anglePerProjectile = anglePerProjectile;
            }

            @Override
            protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
            }

            @Override
            public void init(T mob, Blackboard<T> blackboard) {
            }

            @Override
            public void onStarted(T mob, Blackboard<T> blackboard) {
                this.initial = true;
                this.angleBuffer = 0.0f;
            }

            @Override
            public void onEnded(T mob, Blackboard<T> blackboard) {
            }

            @Override
            public AINodeResult tick(T mob, Blackboard<T> blackboard) {
                if (this.initial) {
                    ArrayList targets = blackboard.getObject(ArrayList.class, "balorTargets");
                    PlayerMob target = (PlayerMob)GameRandom.globalRandom.getOneOf(targets);
                    if (target != null) {
                        this.direction = GameRandom.globalRandom.getOneOf(-1, 1);
                        this.currentAngle = GameMath.getAngle(new Point2D.Float(target.x - ((EvilsProtectorMob)mob).x, target.y - ((EvilsProtectorMob)mob).y)) + this.targetStartAngleOffset * (float)this.direction + 90.0f;
                        this.remainingAngle = this.totalAngle;
                        this.initial = false;
                    } else {
                        return AINodeResult.SUCCESS;
                    }
                }
                this.angleBuffer += this.totalAngle * 50.0f / (float)this.totalTime;
                while (this.angleBuffer >= this.anglePerProjectile) {
                    this.currentAngle += this.anglePerProjectile * (float)this.direction;
                    EvilsProtectorAttack1Projectile projectile = new EvilsProtectorAttack1Projectile(((EvilsProtectorMob)mob).x, ((EvilsProtectorMob)mob).y, this.currentAngle, 50.0f, 800, waveFireballDamage, (Mob)mob);
                    ((Entity)mob).getLevel().entityManager.projectiles.add(projectile);
                    ((EvilsProtectorMob)mob).spawnedProjectiles.add(projectile);
                    this.angleBuffer -= this.anglePerProjectile;
                    this.remainingAngle -= this.anglePerProjectile;
                    if (!(this.remainingAngle <= 0.0f)) continue;
                    break;
                }
                if (!((EvilsProtectorMob)mob).isAttacking) {
                    ((EvilsProtectorMob)mob).attackAbility.runAndSend(300);
                    ((EvilsProtectorMob)mob).fireSoundAbility.runAndSend();
                }
                if (this.angleBuffer >= this.remainingAngle) {
                    return AINodeResult.SUCCESS;
                }
                return AINodeResult.RUNNING;
            }
        }

        public abstract class LandedAttackStage
        extends AINode<T> {
            @Override
            public AINodeResult tick(T mob, Blackboard<T> blackboard) {
                if (((EvilsProtectorMob)mob).isFlying) {
                    if (!((EvilsProtectorMob)mob).isLanding) {
                        BalorAINew.this.findLandingArea(mob);
                    }
                } else if (!((EvilsProtectorMob)mob).isFlyingUp) {
                    return this.tickLanded(mob, blackboard);
                }
                return AINodeResult.RUNNING;
            }

            public abstract AINodeResult tickLanded(T var1, Blackboard<T> var2);
        }

        public abstract class FlyingAttackStage
        extends AINode<T> {
            @Override
            public AINodeResult tick(T mob, Blackboard<T> blackboard) {
                if (!((EvilsProtectorMob)mob).isFlyingUp && !((EvilsProtectorMob)mob).isFlying) {
                    ((EvilsProtectorMob)mob).flyUpAbility.runAndSend();
                } else if (((EvilsProtectorMob)mob).isFlying) {
                    return this.tickFlying(mob, blackboard);
                }
                return AINodeResult.RUNNING;
            }

            public abstract AINodeResult tickFlying(T var1, Blackboard<T> var2);
        }
    }
}

