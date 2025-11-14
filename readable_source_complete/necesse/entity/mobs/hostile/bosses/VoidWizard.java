/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile.bosses;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
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
import necesse.engine.network.server.ServerClient;
import necesse.engine.postProcessing.PostProcessingEffects;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.MusicRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.Entity;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.levelEvent.WaitForSecondsEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.voidWizard.VoidWizardHomingEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.voidWizard.VoidWizardMissileEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.DeathMessageTable;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.MaskShaderOptions;
import necesse.entity.mobs.MaxHealthGetter;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.MobHealthScaling;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ability.BooleanMobAbility;
import necesse.entity.mobs.ability.MobAbility;
import necesse.entity.mobs.ability.TimedMobAbility;
import necesse.entity.mobs.ability.VolumePitchSoundMobAbility;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.composites.SelectorAINode;
import necesse.entity.mobs.ai.behaviourTree.decorators.ConditionAINode;
import necesse.entity.mobs.ai.behaviourTree.decorators.IsolateRunningAINode;
import necesse.entity.mobs.ai.behaviourTree.decorators.MoveTaskAINode;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.BossNearbyBuff;
import necesse.entity.mobs.hostile.bosses.BossMob;
import necesse.entity.mobs.hostile.bosses.VoidWizardClone;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.AttackStageInterface;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.AttackStageManagerNode;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.IdleTimeAttackStage;
import necesse.entity.particle.Particle;
import necesse.entity.particle.SmokePuffParticle;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.VoidWizardWaveProjectile;
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
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.inventory.lootTable.lootItem.ConditionLootItem;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;
import necesse.inventory.lootTable.lootItem.RotationLootItem;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.dungeon.DungeonArenaLevel;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.regionSystem.RegionPositionGetter;

public class VoidWizard
extends BossMob {
    public static LootTable lootTable = new LootTable(new LootItem("voidshard", 25), new OneOfLootItems(LootItem.between("recallscroll", 12, 20), LootItem.between("teleportationscroll", 2, 5)), new ChanceLootItem(0.25f, "wizardsawakeningvinyl"));
    public static RotationLootItem uniqueDrops = RotationLootItem.privateLootRotation(new LootItem("voidstaff"), new LootItem("voidmissile"), new LootItem("magicstilts"));
    public static LootTable privateLootTable = new LootTable(new ConditionLootItem("emptypendant", (r, o) -> {
        ServerClient client = LootTable.expectExtra(ServerClient.class, o, 1);
        return client != null && client.playerMob.getInv().equipment.getTrinketSlotsSize() < 5 && client.playerMob.getInv().getAmount(ItemRegistry.getItem("emptypendant"), false, false, true, true, "have") == 0;
    }), uniqueDrops);
    public static GameDamage cloneProjectile = new GameDamage(45.0f);
    public static GameDamage missile = new GameDamage(40.0f);
    public static GameDamage waveProjectile = new GameDamage(38.0f);
    public static GameDamage homingExplosion = new GameDamage(50.0f);
    public static int homingExplosionRange = 55;
    public static MaxHealthGetter MAX_HEALTH = new MaxHealthGetter(4000, 4600, 5200, 5800, 6400);
    protected boolean itemSpawned;
    protected MobHealthScaling scaling = new MobHealthScaling(this);
    public boolean swingAttack;
    protected ArrayList<Projectile> projectiles;
    protected boolean inSecondStageTransition;
    protected long secondStageTransitionStartTime;
    protected boolean inDeathTransition;
    protected boolean allowDeath;
    protected long deathTransitionStartTime;
    protected boolean isSecondStage;
    public final VoidTeleportAbility teleportAbility;
    public final BooleanMobAbility changeHostile;
    public final TimedMobAbility startSecondStageAbility;
    public final TimedMobAbility startDeathStageAbility;
    public final VolumePitchSoundMobAbility playBoltSoundAbility;

    public VoidWizard() {
        super(100);
        this.difficultyChanges.setMaxHealth(MAX_HEALTH);
        this.attackAnimTime = 200;
        this.setSpeed(50.0f);
        this.setFriction(3.0f);
        this.setArmor(10);
        this.setKnockbackModifier(0.0f);
        this.setDir(2);
        this.updateBoxes();
        this.swimMaskMove = 16;
        this.swimMaskOffset = -8;
        this.swimSinkOffset = -8;
        this.shouldSave = true;
        this.isHostile = false;
        this.teleportAbility = this.registerAbility(new VoidTeleportAbility());
        this.changeHostile = this.registerAbility(new BooleanMobAbility(){

            @Override
            protected void run(boolean value) {
                if (!VoidWizard.this.isHostile && value && VoidWizard.this.isClient()) {
                    SoundManager.playSound(GameResources.voidwizardbegin, (SoundEffect)SoundEffect.effect(VoidWizard.this).volume(1.2f).falloffDistance(4000));
                }
                VoidWizard.this.isHostile = value;
                if (!VoidWizard.this.isHostile) {
                    VoidWizard.this.isSecondStage = false;
                    VoidWizard.this.updateBoxes();
                    VoidWizard.this.inSecondStageTransition = false;
                    VoidWizard.this.resetAI();
                }
            }
        });
        this.startSecondStageAbility = this.registerAbility(new TimedMobAbility(){

            @Override
            protected void run(long time) {
                VoidWizard.this.inSecondStageTransition = true;
                VoidWizard.this.secondStageTransitionStartTime = time;
                VoidWizard.this.isSecondStage = true;
                VoidWizard.this.updateBoxes();
                VoidWizard.this.moveX = 0.0f;
                VoidWizard.this.moveY = 0.0f;
                if (VoidWizard.this.isClient()) {
                    SoundManager.playSound(GameResources.glyphChargeUp, (SoundEffect)SoundEffect.effect(VoidWizard.this).volume(0.5f).pitch(0.5f).falloffDistance(4000));
                    VoidWizard.this.getLevel().entityManager.addLevelEventHidden(new WaitForSecondsEvent(3.1f){

                        @Override
                        public void onWaitOver() {
                            SoundManager.playSound(GameResources.petEvilMinion, (SoundEffect)SoundEffect.effect(VoidWizard.this).volume(0.6f).pitch(0.6f).falloffDistance(4000));
                        }
                    });
                    VoidWizard.this.getLevel().entityManager.addLevelEventHidden(new WaitForSecondsEvent(4.5f){

                        @Override
                        public void onWaitOver() {
                            SoundManager.playSound(GameResources.magicroar, (SoundEffect)SoundEffect.effect(VoidWizard.this).falloffDistance(4000));
                        }
                    });
                }
            }
        });
        this.playBoltSoundAbility = this.registerAbility(new VolumePitchSoundMobAbility(){

            @Override
            protected void run(float volume, float pitch) {
                if (VoidWizard.this.isClient()) {
                    SoundManager.playSound(GameResources.magicbolt1, (SoundEffect)SoundEffect.effect(VoidWizard.this).volume(volume).pitch(pitch).falloffDistance(4000));
                }
            }
        });
        this.startDeathStageAbility = this.registerAbility(new TimedMobAbility(){

            @Override
            protected void run(long time) {
                VoidWizard.this.inDeathTransition = true;
                VoidWizard.this.deathTransitionStartTime = time;
                VoidWizard.this.moveX = 0.0f;
                VoidWizard.this.moveY = 0.0f;
                if (VoidWizard.this.isClient()) {
                    SoundManager.playSound(GameResources.glyphChargeUp, (SoundEffect)SoundEffect.effect(VoidWizard.this).volume(0.3f).pitch(0.7f).falloffDistance(4000));
                }
            }
        });
    }

    @Override
    public void init() {
        super.init();
        VoidWizardAI<VoidWizard> voidWizardAI = this.resetAI();
        this.projectiles = new ArrayList();
        if (this.isServer() && this.getLevel() instanceof DungeonArenaLevel) {
            Point2D.Float pos = DungeonArenaLevel.getBossPosition();
            this.setPos(pos.x, pos.y, true);
            this.setHealth(this.getMaxHealth());
            this.setDir(2);
        }
        if (this.itemSpawned) {
            if (this.isClient()) {
                this.getLevel().entityManager.addParticle(new SmokePuffParticle(this.getLevel(), this.getX(), this.getY(), new Color(100, 22, 22)), Particle.GType.CRITICAL);
                if (this.isHostile) {
                    SoundManager.playSound(GameResources.magicroar, SoundEffect.globalEffect());
                }
            } else if (this.isServer()) {
                voidWizardAI.makeHostile(this);
            }
        }
    }

    protected VoidWizardAI<VoidWizard> resetAI() {
        VoidWizardAI<VoidWizard> voidWizardAI = new VoidWizardAI<VoidWizard>();
        this.ai = new BehaviourTreeAI<VoidWizard>(this, voidWizardAI);
        return voidWizardAI;
    }

    public void updateBoxes() {
        if (this.isSecondStage) {
            this.collision = new Rectangle(-20, -50, 40, 45);
            this.hitBox = new Rectangle(-20, -50, 40, 50);
            this.selectBox = new Rectangle(-20, -61, 40, 60);
        } else {
            this.collision = new Rectangle(-10, -7, 20, 14);
            this.hitBox = new Rectangle(-18, -15, 36, 30);
            this.selectBox = new Rectangle(-18, -41, 36, 48);
        }
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextBoolean(this.itemSpawned);
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.itemSpawned = reader.getNextBoolean();
    }

    @Override
    public void setupMovementPacket(PacketWriter writer) {
        super.setupMovementPacket(writer);
        writer.putNextBoolean(this.isHostile);
        writer.putNextBoolean(this.isSecondStage);
        writer.putNextBoolean(this.inSecondStageTransition);
        if (this.inSecondStageTransition) {
            writer.putNextLong(this.secondStageTransitionStartTime);
        }
        writer.putNextBoolean(this.inDeathTransition);
        if (this.inDeathTransition) {
            writer.putNextLong(this.deathTransitionStartTime);
        }
    }

    @Override
    public void applyMovementPacket(PacketReader reader, boolean isDirect) {
        super.applyMovementPacket(reader, isDirect);
        this.isHostile = reader.getNextBoolean();
        this.isSecondStage = reader.getNextBoolean();
        this.updateBoxes();
        this.inSecondStageTransition = reader.getNextBoolean();
        if (this.inSecondStageTransition) {
            this.secondStageTransitionStartTime = reader.getNextLong();
        }
        this.inDeathTransition = reader.getNextBoolean();
        if (this.inDeathTransition) {
            this.deathTransitionStartTime = reader.getNextLong();
        }
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
    public LootTable getLootTable() {
        return lootTable;
    }

    @Override
    public LootTable getPrivateLootTable() {
        return privateLootTable;
    }

    public void makeItemSpawned() {
        this.itemSpawned = true;
    }

    public boolean canAddProjectile() {
        return this.projectiles.size() < 50;
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
        long timePassed;
        super.clientTick();
        if (this.isHostile) {
            if (this.isClientPlayerNearby()) {
                SoundManager.setMusic(MusicRegistry.WizardsAwakening, SoundManager.MusicPriority.EVENT, 1.5f);
                EventStatusBarManager.registerMobHealthStatusBar(this);
                Color shade = VoidWizard.getWizardShade(this);
                float red = (float)shade.getRed() / 255.0f;
                float green = (float)shade.getGreen() / 255.0f;
                float blue = (float)shade.getBlue() / 255.0f;
                float mod = 1.0f / GameMath.min(red, green, blue);
                PostProcessingEffects.setSceneShade(red * mod, green * mod, blue * mod);
            }
            BossNearbyBuff.applyAround(this);
        }
        this.getLevel().lightManager.refreshParticleLightFloat(this.x, this.y, 270.0f, 0.5f);
        if (this.inDeathTransition) {
            this.setHealthHidden(1);
        } else if (this.inSecondStageTransition && (timePassed = this.getWorldEntity().getTime() - this.secondStageTransitionStartTime) >= 5000L) {
            this.inSecondStageTransition = false;
        }
    }

    @Override
    public void serverTick() {
        long timePassed;
        super.serverTick();
        this.scaling.serverTick();
        if (this.isHostile) {
            BossNearbyBuff.applyAround(this);
        }
        if (this.inDeathTransition) {
            this.setHealthHidden(1);
            long timePassed2 = this.getWorldEntity().getTime() - this.deathTransitionStartTime;
            if (timePassed2 >= 3000L && !this.removed()) {
                this.allowDeath = true;
                this.setHealthHidden(0);
            }
        } else if (this.inSecondStageTransition && (timePassed = this.getWorldEntity().getTime() - this.secondStageTransitionStartTime) >= 5000L) {
            this.inSecondStageTransition = false;
        }
    }

    @Override
    public int getFlyingHeight() {
        return this.isSecondStage ? 50 : super.getFlyingHeight();
    }

    @Override
    public boolean canHitThroughCollision() {
        if (this.isSecondStage) {
            return true;
        }
        return super.canHitThroughCollision();
    }

    @Override
    public CollisionFilter getLevelCollisionFilter() {
        if (this.isSecondStage) {
            return null;
        }
        return super.getLevelCollisionFilter().addFilter(tp -> tp.object().object.isWall || tp.object().object.isRock);
    }

    @Override
    public boolean canBePushed(Mob other) {
        return false;
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
    }

    @Override
    protected SoundSettings getHurtSound() {
        if (!this.isHostile) {
            return super.getHurtSound();
        }
        return new SoundSettings(GameResources.voidwizardhurt).volume(0.3f).fallOffDistance(1500);
    }

    @Override
    protected SoundSettings getDeathSound() {
        return new SoundSettings(GameResources.voidwizarddeath).volume(0.8f).fallOffDistance(3000);
    }

    @Override
    public void playHitDeathSound() {
    }

    @Override
    public void setHealthHidden(int health, float knockbackX, float knockbackY, Attacker attacker, boolean fromNetworkUpdate) {
        if (health <= 0 && !this.allowDeath) {
            health = 1;
            if (this.isServer() && !this.inDeathTransition) {
                this.startDeathStageAbility.runAndSend(this.getWorldEntity().getTime());
            }
        }
        super.setHealthHidden(health, knockbackX, knockbackY, attacker, fromNetworkUpdate);
    }

    @Override
    protected void onDeath(Attacker attacker, HashSet<Attacker> attackers) {
        super.onDeath(attacker, attackers);
        attackers.stream().map(Attacker::getFirstPlayerOwner).filter(Objects::nonNull).filter(PlayerMob::isServerClient).map(PlayerMob::getServerClient).distinct().forEach(c -> c.sendChatMessage(new LocalMessage("misc", "bossdefeat", "name", this.getLocalization())));
        if (!this.isDamagedByPlayers) {
            AchievementManager.checkMeAndThisArmyKill(this.getLevel(), attackers);
        }
    }

    @Override
    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(VoidWizard.getTileCoordinate(x), VoidWizard.getTileCoordinate(y));
        if (this.inDeathTransition) {
            this.setDir(2);
            int drawX = camera.getDrawX(x) - 48;
            int drawY = camera.getDrawY(y) - 80;
            long timePassed = this.getWorldEntity().getTime() - this.deathTransitionStartTime;
            int frames = 16;
            int animTime = 3000;
            int timePerFrame = animTime / frames;
            int anim = (int)Math.min(GameMath.limit(timePassed, 0L, (long)animTime) / (long)timePerFrame, (long)(frames - 1));
            final TextureDrawOptionsEnd options1 = MobRegistry.Textures.voidWizard2.initDraw().sprite(anim % 8, 2 + anim / 8, 96).light(light).pos(drawX, drawY);
            final TextureDrawOptionsEnd options2 = MobRegistry.Textures.voidWizard3.initDraw().sprite(anim % 8, 2 + anim / 8, 96).light(light.minLevelCopy(Math.min(light.getLevel() + 100.0f, 150.0f))).pos(drawX, drawY);
            list.add(new MobDrawable(){

                @Override
                public void draw(TickManager tickManager) {
                    options1.draw();
                    options2.draw();
                }
            });
            this.addShadowDrawables(tileList, level, x, y, light, camera);
        } else if (this.inSecondStageTransition) {
            this.setDir(2);
            int drawX = camera.getDrawX(x) - 48;
            int drawY = camera.getDrawY(y) - 80;
            long timePassed = this.getWorldEntity().getTime() - this.secondStageTransitionStartTime;
            int frames = 6;
            int waitTime = 3000;
            int animTime = 2000;
            int timePerFrame = animTime / frames;
            int anim = (int)Math.min(GameMath.limit(timePassed - (long)waitTime, 0L, (long)animTime) / (long)timePerFrame, (long)(frames - 1));
            final TextureDrawOptionsEnd options1 = MobRegistry.Textures.voidWizard2.initDraw().sprite(anim, 1, 96).light(light).pos(drawX, drawY);
            final TextureDrawOptionsEnd options2 = MobRegistry.Textures.voidWizard3.initDraw().sprite(anim, 1, 96).light(light.minLevelCopy(Math.min(light.getLevel() + 100.0f, 150.0f))).pos(drawX, drawY);
            list.add(new MobDrawable(){

                @Override
                public void draw(TickManager tickManager) {
                    options1.draw();
                    options2.draw();
                }
            });
            this.addShadowDrawables(tileList, level, x, y, light, camera);
        } else if (this.isSecondStage) {
            int drawX = camera.getDrawX(x) - 48;
            int drawY = camera.getDrawY(y) - 80;
            int dir = this.getDir();
            int anim = GameUtils.getAnim(this.getWorldEntity().getTime(), 4, 400);
            float rotate = Math.min(30.0f, this.dx / 5.0f);
            final TextureDrawOptionsEnd options1 = MobRegistry.Textures.voidWizard2.initDraw().sprite(anim, 0, 96).light(light).mirror(dir == 0, false).rotate(rotate, 48, 64).pos(drawX, drawY);
            final TextureDrawOptionsEnd options2 = MobRegistry.Textures.voidWizard3.initDraw().sprite(anim, 0, 96).light(light.minLevelCopy(Math.min(light.getLevel() + 100.0f, 150.0f))).mirror(dir == 0, false).rotate(rotate, 48, 64).pos(drawX, drawY);
            list.add(new MobDrawable(){

                @Override
                public void draw(TickManager tickManager) {
                    options1.draw();
                    options2.draw();
                }
            });
            int shadowRes = MobRegistry.Textures.human_big_shadow.getHeight();
            TextureDrawOptionsEnd shadowOptions = MobRegistry.Textures.human_big_shadow.initDraw().sprite(0, 0, shadowRes).light(light).posMiddle(camera.getDrawX(x), camera.getDrawY(y) + 10);
            tileList.add(tm -> shadowOptions.draw());
            this.addShadowDrawables(tileList, level, x, y, light, camera);
        } else {
            int drawX = camera.getDrawX(x) - 22 - 10;
            int drawY = camera.getDrawY(y) - 44 - 7;
            int dir = this.getDir();
            Point sprite = this.getAnimSprite(x, y, dir);
            drawY += this.getBobbing(x, y);
            drawY += level.getTile(VoidWizard.getTileCoordinate(x), VoidWizard.getTileCoordinate(y)).getMobSinkingAmount(this);
            MaskShaderOptions swimMask = this.getSwimMaskShaderOptions(this.inLiquidFloat(x, y));
            HumanDrawOptions humanDrawOptions = new HumanDrawOptions(level, MobRegistry.Textures.voidWizard).sprite(sprite).mask(swimMask).dir(dir).light(light);
            float animProgress = this.getAttackAnimProgress();
            if (this.isAttacking) {
                ItemAttackDrawOptions attackOptions = ItemAttackDrawOptions.start(dir).itemSprite(MobRegistry.Textures.voidWizard.body, 0, 9, 32).itemRotatePoint(3, 3).itemEnd().armSprite(MobRegistry.Textures.voidWizard.body, 0, 8, 32).light(light);
                if (this.swingAttack) {
                    attackOptions.swingRotation(animProgress);
                } else {
                    attackOptions.pointRotation(this.attackDir.x, this.attackDir.y);
                }
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
    }

    @Override
    protected TextureDrawOptions getShadowDrawOptions(Level level, int x, int y, GameLight light, GameCamera camera) {
        GameTexture shadowTexture = MobRegistry.Textures.voidWizard_shadow;
        int drawX = camera.getDrawX(x) - shadowTexture.getWidth() / 2;
        int drawY = camera.getDrawY(y) - shadowTexture.getHeight() / 2 + 5;
        return shadowTexture.initDraw().light(light).pos(drawX, drawY);
    }

    public static Color getWizardShade(Mob mob) {
        Color shade = new Color(255, 255, 255);
        if (mob == null) {
            return shade;
        }
        float time = (float)mob.getWorldEntity().getTime() / 5000.0f;
        float v = time - (float)Math.floor(time);
        return Color.getHSBColor(v, 0.2f, 1.0f);
    }

    public static Color getWizardColor(Mob mob) {
        return new Color(100, 22, 22);
    }

    public static Color getWizardProjectileColor(Mob mob) {
        return new Color(50, 0, 102);
    }

    @Override
    public boolean shouldDrawOnMap() {
        return true;
    }

    @Override
    public Rectangle drawOnMapBox(double tileScale, boolean isMinimap) {
        return new Rectangle(-8, -22, 16, 25);
    }

    @Override
    public GameTooltips getMapTooltips() {
        return new StringTooltips(this.getDisplayName() + " " + this.getHealth() + "/" + this.getMaxHealth());
    }

    @Override
    public void drawOnMap(TickManager tickManager, Client client, int x, int y, double tileScale, Rectangle drawBounds, boolean isMinimap) {
        super.drawOnMap(tickManager, client, x, y, tileScale, drawBounds, isMinimap);
        if (this.isSecondStage) {
            int drawX = x - 24;
            int drawY = y - 32;
            int anim = GameUtils.getAnim(this.getWorldEntity().getTime(), 4, 400);
            MobRegistry.Textures.voidWizard2.initDraw().sprite(anim, 0, 96).size(48, 48).draw(drawX, drawY);
        } else {
            int drawX = x - 16;
            int drawY = y - 26;
            int dir = this.getDir();
            Point sprite = this.getAnimSprite(this.getDrawX(), this.getDrawY(), dir);
            new HumanDrawOptions(this.getLevel(), MobRegistry.Textures.voidWizard).sprite(sprite).dir(dir).size(32, 32).draw(drawX, drawY);
        }
    }

    @Override
    public Stream<ModifierValue<?>> getDefaultModifiers() {
        return Stream.of(new ModifierValue<Float>(BuffModifiers.SLOW, Float.valueOf(0.0f)).max(Float.valueOf(0.2f)), new ModifierValue<Float>(BuffModifiers.ATTACK_MOVEMENT_MOD, Float.valueOf(0.0f)));
    }

    @Override
    public float getSpeed() {
        return super.getSpeed() * (this.isSecondStage ? 1.4f : 1.0f);
    }

    @Override
    public DeathMessageTable getDeathMessages() {
        return this.getDeathMessages("voidwiz", 4);
    }

    public class VoidTeleportAbility
    extends MobAbility {
        public void runAndSend(int x, int y, int dir, boolean spawnParticles) {
            Packet content = new Packet();
            PacketWriter writer = new PacketWriter(content);
            writer.putNextInt(x);
            writer.putNextInt(y);
            writer.putNextInt(dir);
            writer.putNextBoolean(spawnParticles);
            this.runAndSendAbility(content);
        }

        @Override
        public void executePacket(PacketReader reader) {
            int x = reader.getNextInt();
            int y = reader.getNextInt();
            int dir = reader.getNextInt();
            boolean spawnParticles = reader.getNextBoolean();
            if (spawnParticles && VoidWizard.this.isClient()) {
                VoidWizard.this.getLevel().entityManager.addParticle(new TeleportParticle(VoidWizard.this.getLevel(), VoidWizard.this.getX(), VoidWizard.this.getY(), VoidWizard.this), Particle.GType.CRITICAL);
                VoidWizard.this.getLevel().lightManager.refreshParticleLightFloat((float)x, (float)y, 270.0f, 0.5f);
            }
            VoidWizard.this.setDir(dir);
            VoidWizard.this.setPos(x, y, true);
        }
    }

    public static class VoidWizardAI<T extends VoidWizard>
    extends SelectorAINode<T> {
        private int inActiveTimer;
        private Point middlePoint;

        public VoidWizardAI() {
            this.addChild(new AINode<T>(){

                @Override
                protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
                }

                @Override
                public void init(T mob, Blackboard<T> blackboard) {
                }

                @Override
                public AINodeResult tick(T mob, Blackboard<T> blackboard) {
                    if (((VoidWizard)mob).isHostile) {
                        if (((VoidWizard)mob).inDeathTransition || ((VoidWizard)mob).inSecondStageTransition) {
                            blackboard.mover.stopMoving((Mob)mob);
                            return AINodeResult.SUCCESS;
                        }
                    } else {
                        blackboard.mover.stopMoving((Mob)mob);
                        return AINodeResult.SUCCESS;
                    }
                    return AINodeResult.FAILURE;
                }
            });
            this.addChild(new AINode<T>(){

                @Override
                protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
                    blackboard.onEvent("refreshBossDespawn", event -> inActiveTimer = 0);
                }

                @Override
                public void init(T mob, Blackboard<T> blackboard) {
                }

                @Override
                public AINodeResult tick(T mob, Blackboard<T> blackboard) {
                    boolean hasTargets = GameUtils.streamServerClients(((Entity)mob).getLevel()).anyMatch(c -> !c.isDead() && c.playerMob.canBeTargeted((Mob)mob, null));
                    if (!hasTargets) {
                        inActiveTimer++;
                        if (inActiveTimer > 100) {
                            Point2D.Float tpPos = DungeonArenaLevel.getBossPosition();
                            ((VoidWizard)mob).teleportAbility.runAndSend((int)tpPos.x, (int)tpPos.y, 2, true);
                            ((VoidWizard)mob).changeHostile.runAndSend(false);
                            inActiveTimer = 0;
                            ((VoidWizard)mob).clearProjectiles();
                            ((Mob)mob).setHealth(((VoidWizard)mob).getMaxHealth());
                        }
                        return AINodeResult.SUCCESS;
                    }
                    inActiveTimer = 0;
                    return AINodeResult.FAILURE;
                }
            });
            AttackStageManagerNode firstStageAttacks = new AttackStageManagerNode();
            firstStageAttacks.allowSkippingBack = true;
            this.addChild(new ConditionAINode<VoidWizard>(new IsolateRunningAINode(firstStageAttacks), m -> !m.isSecondStage, AINodeResult.FAILURE));
            firstStageAttacks.addChild(new FindNewPosStage(true, false));
            firstStageAttacks.addChild(new CheckSwitchSecondStage());
            firstStageAttacks.addChild(new HomingAttacksStage());
            firstStageAttacks.addChild(new CheckSwitchSecondStage());
            firstStageAttacks.addChild(new FindNewPosStage(true, false));
            firstStageAttacks.addChild(new CheckSwitchSecondStage());
            firstStageAttacks.addChild(new MissileAttacksStage());
            firstStageAttacks.addChild(new CheckSwitchSecondStage());
            firstStageAttacks.addChild(new IdleTimeAttackStage(500));
            firstStageAttacks.addChild(new CheckSwitchSecondStage());
            firstStageAttacks.addChild(new SpawnClonesStage());
            firstStageAttacks.addChild(new CheckSwitchSecondStage());
            AttackStageManagerNode secondStageAttacks = new AttackStageManagerNode();
            secondStageAttacks.allowSkippingBack = true;
            this.addChild(new ConditionAINode<VoidWizard>(new IsolateRunningAINode(secondStageAttacks), m -> m.isSecondStage, AINodeResult.FAILURE));
            secondStageAttacks.addChild(new FindNewPosStage(false, true));
            secondStageAttacks.addChild(new WaveAttacksStage());
            secondStageAttacks.addChild(new IdleTimeAttackStage(1000));
            secondStageAttacks.addChild(new FindNewPosStage(false, true));
            secondStageAttacks.addChild(new HomingAttacksStage());
            secondStageAttacks.addChild(new IdleTimeAttackStage(1000));
            secondStageAttacks.addChild(new FindNewPosStage(false, true));
            secondStageAttacks.addChild(new MissileAttacksStage());
            secondStageAttacks.addChild(new IdleTimeAttackStage(1000));
        }

        @Override
        public void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
            super.onRootSet(root, mob, blackboard);
            blackboard.onWasHit(e -> this.makeHostile(mob));
        }

        public void makeHostile(T mob) {
            if (!((VoidWizard)mob).isHostile) {
                Level level = ((Entity)mob).getLevel();
                if (((Entity)mob).isServer()) {
                    level.getServer().network.sendToClientsWithEntity(new PacketChatMessage(new LocalMessage("misc", "bossawoke", "name", ((Mob)mob).getLocalization())), (RegionPositionGetter)mob);
                }
                this.middlePoint = level instanceof DungeonArenaLevel ? new Point(level.tileWidth > 0 ? level.tileWidth / 2 : ((Entity)mob).getTileX(), level.tileHeight > 0 ? level.tileHeight / 2 : ((Entity)mob).getTileY()) : new Point(((Entity)mob).getTileX(), ((Entity)mob).getTileY());
                ((VoidWizard)mob).changeHostile.runAndSend(true);
            }
        }

        public Point findNewPosition(Mob mob) {
            ArrayList<Point> positions = new ArrayList<Point>();
            int searchRadius = 10;
            int minRadius = 5;
            int mobTileX = mob.getTileX();
            int mobTileY = mob.getTileY();
            int xOffset = (int)Math.signum(this.middlePoint.x - mobTileX) * searchRadius / 2;
            int yOffset = (int)Math.signum(this.middlePoint.y - mobTileY) * searchRadius / 2;
            for (int x = -searchRadius; x <= searchRadius; ++x) {
                if (x > -minRadius && x < minRadius) continue;
                for (int y = -searchRadius; y <= searchRadius; ++y) {
                    if (y > -minRadius && y < minRadius) continue;
                    Point p = new Point(mobTileX + xOffset + x, mobTileY + yOffset + y);
                    if (mob.getLevel().isSolidTile(p.x, p.y)) continue;
                    positions.add(p);
                }
            }
            return (Point)GameRandom.globalRandom.getOneOf(positions);
        }

        protected class FindNewPosStage
        extends MoveTaskAINode<T>
        implements AttackStageInterface<T> {
            public boolean waitForArrive;
            public boolean directMovement;
            private boolean hasStartedMoving;

            public FindNewPosStage(boolean waitForArrive, boolean directMovement) {
                this.waitForArrive = waitForArrive;
                this.directMovement = directMovement;
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
                    Point pos = VoidWizardAI.this.findNewPosition((Mob)mob);
                    if (pos != null) {
                        if (this.directMovement) {
                            blackboard.mover.directMoveTo(this, pos.x * 32 + 16, pos.y * 32 + 16);
                        } else {
                            return this.moveToTileTask(pos.x, pos.y, null, path -> {
                                path.move(null);
                                return null;
                            });
                        }
                    }
                }
                if (this.waitForArrive && blackboard.mover.isCurrentlyMovingFor(this) && blackboard.mover.isMoving()) {
                    return AINodeResult.RUNNING;
                }
                return AINodeResult.SUCCESS;
            }

            @Override
            public void onStarted(T mob, Blackboard<T> blackboard) {
                this.hasStartedMoving = false;
            }

            @Override
            public void onEnded(T mob, Blackboard<T> blackboard) {
            }
        }

        protected class CheckSwitchSecondStage
        extends AINode<T> {
            protected CheckSwitchSecondStage() {
            }

            @Override
            protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
            }

            @Override
            public void init(T mob, Blackboard<T> blackboard) {
            }

            @Override
            public AINodeResult tick(T mob, Blackboard<T> blackboard) {
                if (!((VoidWizard)mob).isSecondStage && ((Mob)mob).getHealth() <= ((VoidWizard)mob).getMaxHealth() / 2) {
                    ((VoidWizard)mob).startSecondStageAbility.runAndSend(((Entity)mob).getWorldEntity().getTime());
                    return AINodeResult.RUNNING;
                }
                return AINodeResult.SUCCESS;
            }
        }

        protected class HomingAttacksStage
        extends AINode<T>
        implements AttackStageInterface<T> {
            public LevelEvent event;

            protected HomingAttacksStage() {
            }

            @Override
            protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
            }

            @Override
            public void init(T mob, Blackboard<T> blackboard) {
            }

            @Override
            public AINodeResult tick(T mob, Blackboard<T> blackboard) {
                if (this.event == null) {
                    List targets = GameUtils.streamServerClients(((Entity)mob).getLevel()).filter(c -> !c.isDead() && c.playerMob.canBeTargeted((Mob)mob, null)).collect(Collectors.toList());
                    ServerClient target = (ServerClient)GameRandom.globalRandom.getOneOf(targets);
                    if (target == null) {
                        return AINodeResult.SUCCESS;
                    }
                    this.event = new VoidWizardHomingEvent((Mob)mob, target.playerMob, ((VoidWizard)mob).isSecondStage, !((VoidWizard)mob).isSecondStage);
                    ((Entity)mob).getLevel().entityManager.events.add(this.event);
                }
                if (!this.event.isOver()) {
                    return AINodeResult.RUNNING;
                }
                return AINodeResult.SUCCESS;
            }

            @Override
            public void onStarted(T mob, Blackboard<T> blackboard) {
                this.event = null;
            }

            @Override
            public void onEnded(T mob, Blackboard<T> blackboard) {
            }
        }

        protected class MissileAttacksStage
        extends AINode<T>
        implements AttackStageInterface<T> {
            public LevelEvent event;

            protected MissileAttacksStage() {
            }

            @Override
            protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
            }

            @Override
            public void init(T mob, Blackboard<T> blackboard) {
            }

            @Override
            public AINodeResult tick(T mob, Blackboard<T> blackboard) {
                if (this.event == null) {
                    List targets = GameUtils.streamServerClients(((Entity)mob).getLevel()).filter(c -> !c.isDead() && c.playerMob.canBeTargeted((Mob)mob, null)).collect(Collectors.toList());
                    ServerClient target = (ServerClient)GameRandom.globalRandom.getOneOf(targets);
                    if (target == null) {
                        return AINodeResult.SUCCESS;
                    }
                    this.event = new VoidWizardMissileEvent((Mob)mob, target.playerMob);
                    ((Entity)mob).getLevel().entityManager.events.add(this.event);
                }
                if (!this.event.isOver()) {
                    return AINodeResult.RUNNING;
                }
                return AINodeResult.SUCCESS;
            }

            @Override
            public void onStarted(T mob, Blackboard<T> blackboard) {
                this.event = null;
            }

            @Override
            public void onEnded(T mob, Blackboard<T> blackboard) {
            }
        }

        protected class SpawnClonesStage
        extends AINode<T>
        implements AttackStageInterface<T> {
            public LinkedList<VoidWizardClone> clones = new LinkedList();
            public int ticker;

            protected SpawnClonesStage() {
            }

            @Override
            protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
            }

            @Override
            public void init(T mob, Blackboard<T> blackboard) {
            }

            @Override
            public AINodeResult tick(T mob, Blackboard<T> blackboard) {
                if (this.ticker == 0) {
                    this.clones.clear();
                    List targets = GameUtils.streamServerClients(((Entity)mob).getLevel()).filter(c -> !c.isDead() && c.playerMob.canBeTargeted((Mob)mob, null)).map(c -> c.playerMob).collect(Collectors.toList());
                    Mob target = (Mob)GameRandom.globalRandom.getOneOf(targets);
                    if (target == null) {
                        target = mob;
                    }
                    GameUtils.streamServerClients(((Entity)mob).getLevel()).filter(client -> client.playerMob.serverFollowersManager.summonFocusMob == mob).forEach(client -> client.playerMob.serverFollowersManager.clearSummonFocus());
                    ArrayList<Point> clonePos = new ArrayList<Point>();
                    int cloneCount = Math.min(3 + targets.size(), 8);
                    float anglePerClone = 360.0f / (float)cloneCount;
                    for (int i = 0; i < cloneCount; ++i) {
                        float angle = anglePerClone * (float)i;
                        Point2D.Float dir = GameMath.getAngleDir(angle - 90.0f);
                        clonePos.add(new Point(target.getX() - (int)(dir.x * 80.0f), target.getY() - (int)(dir.y * 80.0f)));
                    }
                    Point originalPoint = null;
                    while (!clonePos.isEmpty()) {
                        int r = GameRandom.globalRandom.nextInt(clonePos.size());
                        Point p = (Point)clonePos.remove(r);
                        if (((Mob)mob).collidesWith(((Entity)mob).getLevel(), p.x, p.y)) continue;
                        if (originalPoint == null) {
                            originalPoint = p;
                            ((Mob)mob).setFacingDir(target.x - (float)p.x, target.y - (float)p.y);
                            continue;
                        }
                        VoidWizardClone clone = (VoidWizardClone)MobRegistry.getMob("voidwizardclone", ((Entity)mob).getLevel());
                        clone.setOriginal((VoidWizard)mob);
                        clone.setFacingDir(target.x - (float)p.x, target.y - (float)p.y);
                        ((Entity)mob).getLevel().entityManager.addMob(clone, p.x, p.y);
                        this.clones.add(clone);
                    }
                    if (originalPoint == null) {
                        originalPoint = new Point(((Entity)mob).getX(), ((Entity)mob).getY());
                    }
                    ((VoidWizard)mob).teleportAbility.runAndSend(originalPoint.x, originalPoint.y, ((Mob)mob).getDir(), true);
                }
                ++this.ticker;
                if (this.ticker <= 20) {
                    return AINodeResult.RUNNING;
                }
                for (VoidWizardClone clone : this.clones) {
                    Point pos = VoidWizardAI.this.findNewPosition(clone);
                    if (pos != null) {
                        clone.moveToPos(pos.x, pos.y);
                        continue;
                    }
                    clone.remove();
                }
                return AINodeResult.SUCCESS;
            }

            @Override
            public void onStarted(T mob, Blackboard<T> blackboard) {
                this.ticker = 0;
            }

            @Override
            public void onEnded(T mob, Blackboard<T> blackboard) {
            }
        }

        protected class WaveAttacksStage
        extends AINode<T>
        implements AttackStageInterface<T> {
            public Mob target;
            public int tick;

            protected WaveAttacksStage() {
            }

            @Override
            protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
            }

            @Override
            public void init(T mob, Blackboard<T> blackboard) {
            }

            @Override
            public AINodeResult tick(T mob, Blackboard<T> blackboard) {
                if (this.target == null) {
                    List targets = GameUtils.streamServerClients(((Entity)mob).getLevel()).filter(c -> !c.isDead() && c.playerMob.canBeTargeted((Mob)mob, null)).map(c -> c.playerMob).collect(Collectors.toList());
                    this.target = (Mob)GameRandom.globalRandom.getOneOf(targets);
                    if (this.target == null) {
                        return AINodeResult.SUCCESS;
                    }
                }
                int targetX = this.target.getX();
                int targetY = this.target.getY();
                int throwSpeed = 6;
                if (this.tick == 0) {
                    VoidWizardWaveProjectile projectile = new VoidWizardWaveProjectile((Mob)mob, ((Entity)mob).getX(), ((Entity)mob).getY(), targetX, targetY, waveProjectile);
                    projectile.setLevel(((Entity)mob).getLevel());
                    projectile.moveDist(20.0);
                    ((VoidWizard)mob).addProjectile(projectile);
                    ((Entity)mob).getLevel().entityManager.projectiles.add(projectile);
                    ((VoidWizard)mob).playBoltSoundAbility.runAndSend(1.0f, 1.0f);
                } else if (this.tick == throwSpeed) {
                    for (int i = -1; i <= 1; ++i) {
                        if (i == 0) continue;
                        Point2D.Float targetDir = GameMath.normalize(((Entity)mob).getX() - targetX, ((Entity)mob).getY() - targetY);
                        Point2D.Float perpPoint = GameMath.getPerpendicularPoint(targetX, (float)targetY, (float)(i * 100), targetDir);
                        VoidWizardWaveProjectile projectile = new VoidWizardWaveProjectile((Mob)mob, ((Entity)mob).getX(), ((Entity)mob).getY(), (int)perpPoint.x, (int)perpPoint.y, waveProjectile);
                        projectile.setLevel(((Entity)mob).getLevel());
                        projectile.moveDist(20.0);
                        ((VoidWizard)mob).addProjectile(projectile);
                        ((Entity)mob).getLevel().entityManager.projectiles.add(projectile);
                    }
                    ((VoidWizard)mob).playBoltSoundAbility.runAndSend(1.0f, 1.0f);
                } else if (this.tick == throwSpeed * 2) {
                    for (int i = -1; i <= 1; ++i) {
                        if (i == 0) continue;
                        Point2D.Float targetDir = GameMath.normalize(((Entity)mob).getX() - targetX, ((Entity)mob).getY() - targetY);
                        Point2D.Float perpPoint = GameMath.getPerpendicularPoint(targetX, (float)targetY, (float)(i * 200), targetDir);
                        VoidWizardWaveProjectile projectile = new VoidWizardWaveProjectile((Mob)mob, ((Entity)mob).getX(), ((Entity)mob).getY(), (int)perpPoint.x, (int)perpPoint.y, waveProjectile);
                        projectile.setLevel(((Entity)mob).getLevel());
                        projectile.moveDist(20.0);
                        ((VoidWizard)mob).addProjectile(projectile);
                        ((Entity)mob).getLevel().entityManager.projectiles.add(projectile);
                    }
                    ((VoidWizard)mob).playBoltSoundAbility.runAndSend(1.0f, 1.0f);
                }
                ++this.tick;
                if (this.tick > throwSpeed * 3) {
                    return AINodeResult.SUCCESS;
                }
                return AINodeResult.RUNNING;
            }

            @Override
            public void onStarted(T mob, Blackboard<T> blackboard) {
                this.target = null;
                this.tick = 0;
            }

            @Override
            public void onEnded(T mob, Blackboard<T> blackboard) {
            }
        }
    }

    public static class TeleportParticle
    extends Particle {
        private final VoidWizard owner;
        private final Point animSprite;
        private int dir;

        public TeleportParticle(Level level, int x, int y, VoidWizard owner) {
            super(level, x, y, 0.0f, 0.0f, 1000L);
            this.owner = owner;
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
            int drawX = this.getX() - camera.getX() - 22 - 10;
            int drawY = this.getY() - camera.getY() - 44 - 7;
            float alpha = Math.abs(life - 1.0f);
            final DrawOptions drawOptions = new HumanDrawOptions(level, MobRegistry.Textures.voidWizard).sprite(this.animSprite).dir(this.dir).light(light).alpha(alpha).pos(drawX, drawY);
            list.add(new EntityDrawable(this){

                @Override
                public void draw(TickManager tickManager) {
                    drawOptions.draw();
                }
            });
        }
    }
}

