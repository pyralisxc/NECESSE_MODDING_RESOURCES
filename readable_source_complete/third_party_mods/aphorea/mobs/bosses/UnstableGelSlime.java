/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.DifficultyBasedGetter
 *  necesse.engine.eventStatusBars.EventStatusBarManager
 *  necesse.engine.gameLoop.tickManager.TickManager
 *  necesse.engine.localization.Localization
 *  necesse.engine.localization.message.GameMessage
 *  necesse.engine.localization.message.LocalMessage
 *  necesse.engine.modifiers.ModifierValue
 *  necesse.engine.network.Packet
 *  necesse.engine.network.PacketReader
 *  necesse.engine.network.PacketWriter
 *  necesse.engine.network.client.Client
 *  necesse.engine.network.packet.PacketChatMessage
 *  necesse.engine.network.server.ServerClient
 *  necesse.engine.registries.BuffRegistry
 *  necesse.engine.registries.ItemRegistry
 *  necesse.engine.registries.MobRegistry
 *  necesse.engine.registries.MobRegistry$Textures
 *  necesse.engine.registries.MusicRegistry
 *  necesse.engine.sound.GameMusic
 *  necesse.engine.sound.PrimitiveSoundEmitter
 *  necesse.engine.sound.SoundEffect
 *  necesse.engine.sound.SoundManager
 *  necesse.engine.sound.SoundManager$MusicPriority
 *  necesse.engine.sound.gameSound.GameSound
 *  necesse.engine.util.GameRandom
 *  necesse.engine.util.GameUtils
 *  necesse.engine.util.gameAreaSearch.GameAreaStream
 *  necesse.entity.Entity
 *  necesse.entity.mobs.Attacker
 *  necesse.entity.mobs.GameDamage
 *  necesse.entity.mobs.MaxHealthGetter
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.MobDrawable
 *  necesse.entity.mobs.MobHealthScaling
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.mobs.ability.CoordinateMobAbility
 *  necesse.entity.mobs.ability.MobAbility
 *  necesse.entity.mobs.ai.behaviourTree.AINode
 *  necesse.entity.mobs.ai.behaviourTree.AINodeResult
 *  necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI
 *  necesse.entity.mobs.ai.behaviourTree.Blackboard
 *  necesse.entity.mobs.ai.behaviourTree.composites.SelectorAINode
 *  necesse.entity.mobs.ai.behaviourTree.composites.SequenceAINode
 *  necesse.entity.mobs.ai.behaviourTree.decorators.SucceederAINode
 *  necesse.entity.mobs.ai.behaviourTree.leaves.CollisionChaserAINode
 *  necesse.entity.mobs.ai.behaviourTree.leaves.LooseTargetTimerAINode
 *  necesse.entity.mobs.ai.behaviourTree.leaves.TargetFinderAINode
 *  necesse.entity.mobs.ai.behaviourTree.trees.CollisionPlayerChaserAI
 *  necesse.entity.mobs.ai.behaviourTree.util.AIMover
 *  necesse.entity.mobs.ai.behaviourTree.util.FlyingAIMover
 *  necesse.entity.mobs.ai.behaviourTree.util.TargetFinderDistance
 *  necesse.entity.mobs.buffs.ActiveBuff
 *  necesse.entity.mobs.buffs.BuffModifiers
 *  necesse.entity.mobs.buffs.staticBuffs.BossNearbyBuff
 *  necesse.entity.mobs.hostile.bosses.FlyingBossMob
 *  necesse.entity.particle.FleshParticle
 *  necesse.entity.particle.Particle
 *  necesse.entity.particle.Particle$GType
 *  necesse.entity.particle.ParticleOption
 *  necesse.entity.particle.SmokePuffParticle
 *  necesse.gfx.GameResources
 *  necesse.gfx.camera.GameCamera
 *  necesse.gfx.drawOptions.DrawOptions
 *  necesse.gfx.drawOptions.texture.TextureDrawOptions
 *  necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd
 *  necesse.gfx.drawables.OrderableDrawables
 *  necesse.gfx.gameTexture.GameTexture
 *  necesse.gfx.gameTooltips.GameTooltips
 *  necesse.gfx.gameTooltips.StringTooltips
 *  necesse.inventory.lootTable.LootItemInterface
 *  necesse.inventory.lootTable.LootTable
 *  necesse.inventory.lootTable.lootItem.ChanceLootItem
 *  necesse.inventory.lootTable.lootItem.ConditionLootItem
 *  necesse.inventory.lootTable.lootItem.LootItem
 *  necesse.inventory.lootTable.lootItem.RotationLootItem
 *  necesse.level.maps.Level
 *  necesse.level.maps.light.GameLight
 */
package aphorea.mobs.bosses;

import aphorea.mobs.bosses.minions.MiniUnstableGelSlime;
import aphorea.projectiles.mob.MiniUnstableGelSlimeProjectile;
import aphorea.registry.AphBuffs;
import aphorea.utils.AphColors;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import necesse.engine.DifficultyBasedGetter;
import necesse.engine.eventStatusBars.EventStatusBarManager;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketChatMessage;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.MusicRegistry;
import necesse.engine.sound.GameMusic;
import necesse.engine.sound.PrimitiveSoundEmitter;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.gameSound.GameSound;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.engine.util.gameAreaSearch.GameAreaStream;
import necesse.entity.Entity;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.MaxHealthGetter;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.MobHealthScaling;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ability.CoordinateMobAbility;
import necesse.entity.mobs.ability.MobAbility;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.composites.SelectorAINode;
import necesse.entity.mobs.ai.behaviourTree.composites.SequenceAINode;
import necesse.entity.mobs.ai.behaviourTree.decorators.SucceederAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.CollisionChaserAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.LooseTargetTimerAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.TargetFinderAINode;
import necesse.entity.mobs.ai.behaviourTree.trees.CollisionPlayerChaserAI;
import necesse.entity.mobs.ai.behaviourTree.util.AIMover;
import necesse.entity.mobs.ai.behaviourTree.util.FlyingAIMover;
import necesse.entity.mobs.ai.behaviourTree.util.TargetFinderDistance;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.BossNearbyBuff;
import necesse.entity.mobs.hostile.bosses.FlyingBossMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.entity.particle.ParticleOption;
import necesse.entity.particle.SmokePuffParticle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.inventory.lootTable.lootItem.ConditionLootItem;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.RotationLootItem;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class UnstableGelSlime
extends FlyingBossMob {
    public static int baseSpeed = 20;
    public static int speedPerAnger = 2;
    public static GameTexture icon;
    public static GameTexture texture;
    public static MaxHealthGetter MAX_HEALTH;
    public final CoordinateMobAbility teleportAbility;
    public static LootTable lootTable;
    public static LootTable privateLootTable;
    protected MobHealthScaling scaling = new MobHealthScaling((Mob)this);

    public UnstableGelSlime() {
        super(1500);
        this.difficultyChanges.setMaxHealth((DifficultyBasedGetter)MAX_HEALTH);
        this.setArmor(10);
        this.setSpeed(baseSpeed);
        this.setFriction(3.0f);
        this.setKnockbackModifier(0.0f);
        this.staySmoothSnapped = true;
        this.collision = new Rectangle(-45, -45, 90, 55);
        this.hitBox = new Rectangle(-50, -55, 100, 70);
        this.selectBox = new Rectangle(-55, -89, 110, 100);
        this.teleportAbility = (CoordinateMobAbility)this.registerAbility((MobAbility)new CoordinateMobAbility(){

            protected void run(int x, int y) {
                if (UnstableGelSlime.this.isClient()) {
                    UnstableGelSlime.this.getLevel().entityManager.addParticle((ParticleOption)new SmokePuffParticle(UnstableGelSlime.this.getLevel(), UnstableGelSlime.this.x, UnstableGelSlime.this.y, 92, AphColors.unstableGel), Particle.GType.CRITICAL);
                    UnstableGelSlime.this.getLevel().entityManager.addParticle((ParticleOption)new SmokePuffParticle(UnstableGelSlime.this.getLevel(), (float)x, (float)y, 92, AphColors.unstableGel), Particle.GType.CRITICAL);
                }
                UnstableGelSlime.this.setPos(x, y, true);
            }
        });
    }

    public void init() {
        super.init();
        SoundManager.playSound((GameSound)GameResources.roar, (SoundEffect)SoundEffect.effect((PrimitiveSoundEmitter)this).volume(0.7f).pitch(GameRandom.globalRandom.getFloatBetween(1.0f, 1.1f)));
        this.ai = new BehaviourTreeAI((Mob)this, new UnstableGelSlimeAI(), (AIMover)new FlyingAIMover());
    }

    public LootTable getLootTable() {
        return lootTable;
    }

    public LootTable getPrivateLootTable() {
        return privateLootTable;
    }

    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for (int i = 0; i < 4; ++i) {
            this.getLevel().entityManager.addParticle((Particle)new FleshParticle(this.getLevel(), texture == null ? GameTexture.fromFile((String)"mobs/unstablegelslime") : texture, GameRandom.globalRandom.nextInt(5), 8, 96, this.x, this.y, 20.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    protected void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(this.getTileX(), this.getTileY());
        int drawX = camera.getDrawX(x) - 96;
        int drawY = camera.getDrawY(y) - 153 - 9;
        Point sprite = this.getAnimSprite(x, y, this.getDir());
        drawY += this.getBobbing(x, y);
        drawY += this.getLevel().getTile(this.getTileX(), this.getTileY()).getMobSinkingAmount((Mob)this);
        if (this.inLiquid()) {
            drawY += 20;
        }
        TextureDrawOptionsEnd drawOptions = texture.initDraw().sprite(sprite.x, sprite.y, 192).light(light).alpha(this.buffManager.hasBuff(BuffRegistry.INVULNERABLE_ACTIVE) ? 0.6f : 1.0f).pos(drawX, drawY);
        list.add(new MobDrawable((DrawOptions)drawOptions){
            final /* synthetic */ DrawOptions val$drawOptions;
            {
                this.val$drawOptions = drawOptions;
            }

            public void draw(TickManager tickManager) {
                this.val$drawOptions.draw();
            }
        });
        if (!this.isWaterWalking()) {
            this.addShadowDrawables(tileList, level, x, y, light, camera);
        }
    }

    protected TextureDrawOptions getShadowDrawOptions(Level level, int x, int y, GameLight light, GameCamera camera) {
        GameTexture shadowTexture = MobRegistry.Textures.ancientVulture_shadow;
        int drawX = camera.getDrawX(x) - shadowTexture.getWidth() / 2;
        int drawY = camera.getDrawY(y) - shadowTexture.getHeight() / 2 - 9;
        return shadowTexture.initDraw().sprite(0, 0, shadowTexture.getWidth(), shadowTexture.getHeight()).light(light).pos(drawX, drawY += this.getBobbing(x, y));
    }

    public int getRockSpeed() {
        return 20;
    }

    public int getMaxHealth() {
        return super.getMaxHealth() + (int)((float)(this.scaling == null ? 0 : this.scaling.getHealthIncrease()) * this.getMaxHealthModifier());
    }

    public void setupHealthPacket(PacketWriter writer, boolean isFull) {
        this.scaling.setupHealthPacket(writer, isFull);
        super.setupHealthPacket(writer, isFull);
    }

    public void applyHealthPacket(PacketReader reader, boolean isFull) {
        this.scaling.applyHealthPacket(reader, isFull);
        super.applyHealthPacket(reader, isFull);
    }

    public void setMaxHealth(int maxHealth) {
        super.setMaxHealth(maxHealth);
        if (this.scaling != null) {
            this.scaling.updatedMaxHealth();
        }
    }

    public boolean shouldDrawOnMap() {
        return true;
    }

    public void drawOnMap(TickManager tickManager, Client client, int x, int y, double tileScale, Rectangle drawBounds, boolean isMinimap) {
        super.drawOnMap(tickManager, client, x, y, tileScale, drawBounds, isMinimap);
        int drawX = x - 16;
        int drawY = y - 16;
        icon.initDraw().sprite(0, 0, 32).size(32, 32).draw(drawX, drawY);
    }

    public Rectangle drawOnMapBox(double tileScale, boolean isMinimap) {
        return new Rectangle(-16, -16, 32, 32);
    }

    public GameTooltips getMapTooltips() {
        return new StringTooltips(this.getDisplayName() + " " + this.getHealth() + "/" + this.getMaxHealth());
    }

    protected void onDeath(Attacker attacker, HashSet<Attacker> attackers) {
        super.onDeath(attacker, attackers);
        attackers.stream().map(Attacker::getAttackOwner).filter(m -> m != null && m.isPlayer).distinct().forEach(m -> this.getServer().network.sendPacket((Packet)new PacketChatMessage((GameMessage)new LocalMessage("misc", "bossdefeat", "name", this.getLocalization())), ((PlayerMob)m).getServerClient()));
        for (int i = 0; i < 4; ++i) {
            Mob invocar = MobRegistry.getMob((String)"miniunstablegelslime", (Level)this.getLevel());
            this.getLevel().entityManager.addMob(invocar, this.randomPositionClose(this.x), this.randomPositionClose(this.y));
        }
    }

    public void clientTick() {
        super.clientTick();
        SoundManager.setMusic((GameMusic)MusicRegistry.TheFirstTrial, (SoundManager.MusicPriority)SoundManager.MusicPriority.EVENT, (float)1.5f);
        EventStatusBarManager.registerMobHealthStatusBar((Mob)this);
        BossNearbyBuff.applyAround((Mob)this);
    }

    public void serverTick() {
        super.serverTick();
        this.scaling.serverTick();
        BossNearbyBuff.applyAround((Mob)this);
    }

    public float randomPositionClose(float n) {
        return n + GameRandom.globalRandom.getFloatBetween(5.0f, 10.0f);
    }

    public void handleCollisionHit(Mob target, GameDamage damage, int knockback) {
        super.handleCollisionHit(target, damage, knockback);
        target.addBuff(new ActiveBuff(AphBuffs.STICKY, target, 1000, (Attacker)this), true);
    }

    static {
        MAX_HEALTH = new MaxHealthGetter(1200, 1400, 1600, 1800, 2000);
        lootTable = new LootTable(new LootItemInterface[]{LootItem.between((String)"unstablegel", (int)10, (int)18)});
        privateLootTable = new LootTable(new LootItemInterface[]{RotationLootItem.globalLootRotation((LootItemInterface[])new LootItemInterface[]{new LootItem("unstablegelsword"), new LootItem("unstablegelgreatbow"), new LootItem("unstablegelstaff"), new LootItem("volatilegelstaff")}), new LootItem("unstableperiapt"), new LootItem("runeofunstablegelslime"), new ChanceLootItem(0.01f, "unstablegelsaber"), new ConditionLootItem("gelslimenullifier", (r, o) -> {
            ServerClient client = (ServerClient)LootTable.expectExtra(ServerClient.class, (Object[])o, (int)1);
            return client != null && client.playerMob.getInv().getAmount(ItemRegistry.getItem((String)"gelslimenullifier"), false, false, true, true, "have") == 0;
        })});
    }

    public static class UnstableGelSlimeAI<T extends UnstableGelSlime>
    extends SelectorAINode<T> {
        static GameDamage collisionAttackDamage = new GameDamage(30.0f);
        static int collisionAttackKnockback = 300;
        static int defaultMaxAngerTeleportCooldownDuration = 3000;
        static GameDamage unstableGelSlimeProjectileDamage = new GameDamage(20.0f);
        static int unstableGelSlimeProjectileKnockback = 200;
        static int defaultThrowSlimesNumber = 2;
        static int defaultThrowSlimesCooldownDuration = 10000;
        int maxAngerTeleportCooldownTimer = defaultMaxAngerTeleportCooldownDuration / 50;
        int throwSlimesTimer;
        int throwSlimesCooldownTimer = defaultThrowSlimesCooldownDuration / 50;
        int throwSlimesNumber;
        int anger;
        int inactiveTimer;

        public UnstableGelSlimeAI() {
            this.addChild(new AINode<T>(){

                protected void onRootSet(AINode<T> aiNode, T t, Blackboard<T> blackboard) {
                }

                public void init(T t, Blackboard<T> blackboard) {
                }

                public AINodeResult tick(T mob, Blackboard<T> blackboard) {
                    if (this.streamPossibleTargets(mob).count() == 0L) {
                        ++inactiveTimer;
                        if (inactiveTimer > 100) {
                            mob.remove();
                        }
                        return AINodeResult.SUCCESS;
                    }
                    if (mob.getWorldEntity().isNight()) {
                        mob.remove();
                        PacketChatMessage message = new PacketChatMessage(Localization.translate((String)"message", (String)"unstablegelslime_night"));
                        GameUtils.streamServerClients((Level)mob.getLevel()).forEach(j -> j.sendPacket((Packet)message));
                        return AINodeResult.SUCCESS;
                    }
                    inactiveTimer = 0;
                    return AINodeResult.FAILURE;
                }
            });
            this.addChild(new AINode<T>(){

                protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
                }

                public void init(T mob, Blackboard<T> blackboard) {
                }

                public AINodeResult tick(T mob, Blackboard<T> blackboard) {
                    if (anger == 10) {
                        if (!((UnstableGelSlime)((Object)mob)).buffManager.hasBuff("unstablegelslimerushbuff")) {
                            int targets = (int)this.streamPossibleTargets(mob).count();
                            SoundManager.playSound((GameSound)GameResources.roar, (SoundEffect)SoundEffect.effect(mob).volume(0.7f).pitch(GameRandom.globalRandom.getFloatBetween(1.0f, 1.1f)));
                            maxAngerTeleportCooldownTimer = defaultMaxAngerTeleportCooldownDuration / 50;
                            this.spawnMiniUnstableGelSlimes(mob, Math.min(targets, 4));
                            blackboard.put((Object)"currentTarget", mob);
                            mob.addBuff(new ActiveBuff(BuffRegistry.getBuff((String)"unstablegelslimerushbuff"), mob, 3000, mob), true);
                            mob.addBuff(new ActiveBuff(BuffRegistry.INVULNERABLE_ACTIVE, mob, 500, mob), true);
                            Point point = this.getTeleportPoint(mob);
                            if (point != null) {
                                ((UnstableGelSlime)((Object)mob)).teleportAbility.runAndSend(point.x, point.y);
                            }
                            return AINodeResult.SUCCESS;
                        }
                        return AINodeResult.FAILURE;
                    }
                    if ((double)mob.getHealthPercent() < 1.0 - 0.09 * (double)(1 + anger)) {
                        ++anger;
                        ((UnstableGelSlime)((Object)mob)).buffManager.applyModifiers(BuffModifiers.SPEED_FLAT, (Object)Float.valueOf((float)anger * (float)speedPerAnger), new ModifierValue[0]);
                        SoundManager.playSound((GameSound)GameResources.roar, (SoundEffect)SoundEffect.effect(mob).volume(0.7f).pitch(GameRandom.globalRandom.getFloatBetween(1.0f, 1.1f)));
                        PacketChatMessage mensaje = new PacketChatMessage(Localization.translate((String)"message", (String)(anger == 10 ? "unstablegelslime_fullanger" : "unstablegelslime_anger"), (String)"anger", (Object)anger));
                        GameUtils.streamServerClients((Level)mob.getLevel()).forEach(j -> j.sendPacket((Packet)mensaje));
                        int targets = (int)this.streamPossibleTargets(mob).count();
                        int number = anger >= 8 ? Math.min(targets * 3, 12) : (anger >= 5 ? Math.min(targets * 2, 8) : (anger >= 2 ? Math.min(targets, 4) : Math.min(targets - 1, 2)));
                        if (number > 0) {
                            this.spawnMiniUnstableGelSlimes(mob, number);
                        }
                        blackboard.put((Object)"currentTarget", mob);
                        mob.addBuff(new ActiveBuff(BuffRegistry.getBuff((String)"unstablegelslimerushbuff"), mob, 3000, mob), true);
                        mob.addBuff(new ActiveBuff(BuffRegistry.INVULNERABLE_ACTIVE, mob, 500, mob), true);
                        Point point = this.getTeleportPoint(mob);
                        if (point == null) {
                            return AINodeResult.FAILURE;
                        }
                        ((UnstableGelSlime)((Object)mob)).teleportAbility.runAndSend(point.x, point.y);
                        return AINodeResult.SUCCESS;
                    }
                    return AINodeResult.FAILURE;
                }
            });
            this.addChild(new AINode<T>(){

                protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
                }

                public void init(T mob, Blackboard<T> blackboard) {
                }

                public AINodeResult tick(T mob, Blackboard<T> blackboard) {
                    if (anger >= 10) {
                        return AINodeResult.FAILURE;
                    }
                    if (!((UnstableGelSlime)((Object)mob)).buffManager.hasBuff("unstablegelslimerushbuff")) {
                        if (throwSlimesNumber > 0) {
                            this.processSlimeThrow(mob);
                            return AINodeResult.SUCCESS;
                        }
                        if (throwSlimesCooldownTimer <= 0) {
                            this.prepareSlimeThrow(mob);
                            return AINodeResult.SUCCESS;
                        }
                        --throwSlimesCooldownTimer;
                        return AINodeResult.FAILURE;
                    }
                    --throwSlimesCooldownTimer;
                    return AINodeResult.FAILURE;
                }

                private void processSlimeThrow(T mob) {
                    --throwSlimesTimer;
                    if (this.shouldThrowSlime()) {
                        --throwSlimesNumber;
                        this.playSlimeSound(mob);
                        this.launchSlimeProjectile(mob);
                        if (throwSlimesTimer == 0 && throwSlimesNumber > 0) {
                            this.applyTemporaryBuffs(mob, throwSlimesNumber * 100 + 100);
                        }
                    }
                }

                private boolean shouldThrowSlime() {
                    return throwSlimesTimer < 0 && throwSlimesTimer % 2 == 0 || throwSlimesTimer > 0 && throwSlimesTimer % 10 == 0;
                }

                private void playSlimeSound(T mob) {
                    SoundManager.playSound((GameSound)GameResources.slimeSplash1, (SoundEffect)SoundEffect.effect(mob).volume(0.7f).pitch(GameRandom.globalRandom.getFloatBetween(1.0f, 1.1f)));
                }

                private void applyTemporaryBuffs(T mob, int duration) {
                    mob.addBuff(new ActiveBuff(AphBuffs.STUN, mob, duration, mob), true);
                    mob.addBuff(new ActiveBuff(BuffRegistry.INVULNERABLE_ACTIVE, mob, duration, mob), true);
                }

                private void prepareSlimeThrow(T mob) {
                    int targets = (int)this.streamPossibleTargets(mob).count();
                    throwSlimesNumber = defaultThrowSlimesNumber + Math.min(targets - 1, 5);
                    if (anger >= 8) {
                        throwSlimesNumber += 2;
                    } else if (anger >= 4) {
                        ++throwSlimesNumber;
                    }
                    throwSlimesTimer = Math.min(throwSlimesNumber, 3) * 10;
                    throwSlimesCooldownTimer = defaultThrowSlimesCooldownDuration / 50 + throwSlimesTimer;
                    this.applyTemporaryBuffs(mob, throwSlimesTimer * 50 + 500);
                }
            });
            this.addChild((AINode)new UnstableGelSlimeChasePlayerAI(32768, collisionAttackDamage, collisionAttackKnockback));
        }

        public void spawnMiniUnstableGelSlimes(T mob, int number) {
            for (int i = 0; i < number; ++i) {
                MiniUnstableGelSlime summoned = (MiniUnstableGelSlime)MobRegistry.getMob((String)"miniunstablegelslime", (Level)mob.getLevel());
                summoned.setInitialTP(true);
                mob.getLevel().entityManager.addMob((Mob)summoned, ((UnstableGelSlime)((Object)mob)).randomPositionClose(((UnstableGelSlime)((Object)mob)).x), ((UnstableGelSlime)((Object)mob)).randomPositionClose(((UnstableGelSlime)((Object)mob)).y));
            }
        }

        public void launchSlimeProjectile(T mob) {
            Mob target = this.getRandomCloseTarget(mob);
            if (target == null) {
                target = this.getRandomTarget(mob);
            }
            if (target != null) {
                mob.getLevel().entityManager.projectiles.add((Entity)new MiniUnstableGelSlimeProjectile(mob.getLevel(), (Mob)mob, ((UnstableGelSlime)((Object)mob)).x, ((UnstableGelSlime)((Object)mob)).y, target.x, target.y, 120.0f, 500, unstableGelSlimeProjectileDamage, unstableGelSlimeProjectileKnockback));
            }
        }

        public Point getTeleportPoint(T mob) {
            Mob tpTarget;
            if (!mob.removed() && (tpTarget = this.getRandomTarget(mob)) != null) {
                float distance = 150.0f + 150.0f * mob.getHealthPercent();
                float angle = (float)Math.random() * 360.0f;
                float xExtra = (float)(Math.cos(angle) * (double)distance);
                float yExtra = (float)(Math.sin(angle) * (double)distance);
                return new Point((int)(tpTarget.x + xExtra), (int)(tpTarget.y + yExtra));
            }
            return null;
        }

        public Mob getRandomTarget(T mob) {
            ArrayList list = new ArrayList();
            this.streamPossibleTargets(mob).forEach(list::add);
            return (Mob)GameRandom.globalRandom.getOneOf(list);
        }

        public GameAreaStream<Mob> streamPossibleTargets(T mob) {
            return new TargetFinderDistance(32768).streamPlayersInRange(new Point((int)((UnstableGelSlime)((Object)mob)).x, (int)((UnstableGelSlime)((Object)mob)).y), mob).filter(m -> m != null && !m.removed() && (m.isHuman && m.getTeam() != -1 || m.isPlayer)).map(m -> m);
        }

        public Mob getRandomCloseTarget(T mob) {
            ArrayList list = new ArrayList();
            this.streamPossibleCloseTargets(mob).forEach(list::add);
            if (list.isEmpty()) {
                this.streamPossibleTargets(mob).forEach(list::add);
            }
            return (Mob)GameRandom.globalRandom.getOneOf(list);
        }

        public GameAreaStream<Mob> streamPossibleCloseTargets(T mob) {
            return new TargetFinderDistance(768).streamPlayersInRange(new Point((int)((UnstableGelSlime)((Object)mob)).x, (int)((UnstableGelSlime)((Object)mob)).y), mob).filter(m -> m != null && !m.removed() && (m.isHuman && m.getTeam() != -1 || m.isPlayer)).map(m -> m);
        }
    }

    public static class UnstableGelSlimeChasePlayerAI<T extends UnstableGelSlime>
    extends SequenceAINode<T> {
        CollisionPlayerChaserAI<T> collisionPlayerChaserAI;

        public UnstableGelSlimeChasePlayerAI(int searchDistance, GameDamage damage, int knockback) {
            this.addChild((AINode)new SucceederAINode((AINode)new LooseTargetTimerAINode()));
            this.addChild((AINode)new TargetFinderAINode<T>(searchDistance){

                public GameAreaStream<? extends Mob> streamPossibleTargets(T mob, Point base, TargetFinderDistance<T> distance) {
                    return distance.streamPlayersInRange(base, mob).filter(m -> m != null && !m.removed() && (m.isHuman && m.getTeam() != -1 || m.isPlayer)).map(m -> m);
                }
            });
            this.collisionPlayerChaserAI = new CollisionPlayerChaserAI<T>(searchDistance, damage, knockback){

                public boolean attackTarget(T mob, Mob target) {
                    return this.attackTarget(mob, target);
                }
            };
            this.addChild((AINode)this.collisionPlayerChaserAI);
        }

        public boolean attackTarget(T mob, Mob target) {
            return CollisionChaserAINode.simpleAttack(mob, (Mob)target, (GameDamage)this.collisionPlayerChaserAI.damage, (int)this.collisionPlayerChaserAI.knockback);
        }
    }
}

