/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.gameLoop.tickManager.TickManager
 *  necesse.engine.registries.MobRegistry$Textures
 *  necesse.engine.sound.PrimitiveSoundEmitter
 *  necesse.engine.sound.SoundEffect
 *  necesse.engine.sound.SoundManager
 *  necesse.engine.sound.gameSound.GameSound
 *  necesse.engine.util.GameMath
 *  necesse.engine.util.GameRandom
 *  necesse.engine.util.GameUtils
 *  necesse.engine.util.RayLinkedList
 *  necesse.engine.util.gameAreaSearch.GameAreaStream
 *  necesse.entity.Entity
 *  necesse.entity.ParticleBeamHandler
 *  necesse.entity.mobs.GameDamage
 *  necesse.entity.mobs.MaskShaderOptions
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.MobDrawable
 *  necesse.entity.mobs.PathDoorOption
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.mobs.ability.CoordinateMobAbility
 *  necesse.entity.mobs.ability.MobAbility
 *  necesse.entity.mobs.ability.TargetedMobAbility
 *  necesse.entity.mobs.ai.behaviourTree.AINode
 *  necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI
 *  necesse.entity.mobs.ai.behaviourTree.Blackboard
 *  necesse.entity.mobs.ai.behaviourTree.composites.SequenceAINode
 *  necesse.entity.mobs.ai.behaviourTree.decorators.InverterAINode
 *  necesse.entity.mobs.ai.behaviourTree.leaves.ChaserAINode
 *  necesse.entity.mobs.ai.behaviourTree.leaves.CooldownAttackTargetAINode
 *  necesse.entity.mobs.ai.behaviourTree.leaves.CooldownAttackTargetAINode$CooldownTimer
 *  necesse.entity.mobs.ai.behaviourTree.leaves.EscapeAINode
 *  necesse.entity.mobs.ai.behaviourTree.leaves.TargetFinderAINode
 *  necesse.entity.mobs.ai.behaviourTree.util.TargetFinderDistance
 *  necesse.entity.mobs.buffs.BuffModifiers
 *  necesse.entity.mobs.hostile.HostileMob
 *  necesse.entity.particle.FleshParticle
 *  necesse.entity.particle.Particle
 *  necesse.entity.particle.Particle$GType
 *  necesse.gfx.GameResources
 *  necesse.gfx.camera.GameCamera
 *  necesse.gfx.drawOptions.DrawOptions
 *  necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd
 *  necesse.gfx.drawables.OrderableDrawables
 *  necesse.gfx.gameTexture.GameSprite
 *  necesse.gfx.gameTexture.GameTexture
 *  necesse.inventory.lootTable.LootItemInterface
 *  necesse.inventory.lootTable.LootTable
 *  necesse.inventory.lootTable.lootItem.LootItem
 *  necesse.inventory.lootTable.lootItem.OneOfTicketLootItems
 *  necesse.level.maps.CollisionFilter
 *  necesse.level.maps.Level
 *  necesse.level.maps.levelBuffManager.LevelModifiers
 *  necesse.level.maps.light.GameLight
 */
package aphorea.mobs.hostile;

import aphorea.projectiles.mob.SpinelGolemBeamProjectile;
import aphorea.utils.AphColors;
import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.PrimitiveSoundEmitter;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.gameSound.GameSound;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.engine.util.RayLinkedList;
import necesse.engine.util.gameAreaSearch.GameAreaStream;
import necesse.entity.Entity;
import necesse.entity.ParticleBeamHandler;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.MaskShaderOptions;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PathDoorOption;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ability.CoordinateMobAbility;
import necesse.entity.mobs.ability.MobAbility;
import necesse.entity.mobs.ability.TargetedMobAbility;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.composites.SequenceAINode;
import necesse.entity.mobs.ai.behaviourTree.decorators.InverterAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.ChaserAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.CooldownAttackTargetAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.EscapeAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.TargetFinderAINode;
import necesse.entity.mobs.ai.behaviourTree.util.TargetFinderDistance;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.hostile.HostileMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.OneOfTicketLootItems;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;
import necesse.level.maps.levelBuffManager.LevelModifiers;
import necesse.level.maps.light.GameLight;

public class SpinelGolem
extends HostileMob {
    public static GameTexture texture;
    public static GameDamage damage;
    public static int chargeTime;
    public static int stickTime;
    protected long shootTime;
    protected Mob shootTarget;
    protected int shootX;
    protected int shootY;
    public ParticleBeamHandler warningBeam;
    public final TargetedMobAbility startShootingAbility;
    public final CoordinateMobAbility stickShootAbility;
    public final CoordinateMobAbility shootAbility;
    public static LootTable lootTable;

    public SpinelGolem() {
        super(200);
        this.setArmor(20);
        this.setSpeed(20.0f);
        this.setFriction(3.0f);
        this.setKnockbackModifier(0.4f);
        this.collision = new Rectangle(-10, -7, 20, 14);
        this.hitBox = new Rectangle(-14, -12, 28, 24);
        this.selectBox = new Rectangle(-14, -41, 28, 48);
        this.swimMaskMove = 32;
        this.swimMaskOffset = -6;
        this.swimSinkOffset = -4;
        this.startShootingAbility = (TargetedMobAbility)this.registerAbility((MobAbility)new TargetedMobAbility(){

            protected void run(Mob target) {
                SpinelGolem.this.attackAnimTime = chargeTime + stickTime + 500;
                SpinelGolem.this.attackCooldown = chargeTime + stickTime;
                SpinelGolem.this.shootTime = SpinelGolem.this.getWorldEntity().getTime() + (long)chargeTime + (long)stickTime;
                SpinelGolem.this.shootTarget = target;
                if (SpinelGolem.this.warningBeam != null) {
                    SpinelGolem.this.warningBeam.dispose();
                }
                SpinelGolem.this.warningBeam = null;
                SpinelGolem.this.startAttackCooldown();
                if (target != null) {
                    SpinelGolem.this.showAttack(target.getX(), target.getY(), true);
                } else {
                    SpinelGolem.this.showAttack(SpinelGolem.this.getX() + 100, SpinelGolem.this.getY(), true);
                }
            }
        });
        this.stickShootAbility = (CoordinateMobAbility)this.registerAbility((MobAbility)new CoordinateMobAbility(){

            protected void run(int x, int y) {
                SpinelGolem.this.shootX = x;
                SpinelGolem.this.shootY = y;
                SpinelGolem.this.shootTarget = null;
            }
        });
        this.shootAbility = (CoordinateMobAbility)this.registerAbility((MobAbility)new CoordinateMobAbility(){

            protected void run(int x, int y) {
                SpinelGolem.this.shootAbilityProjectile(x, y);
            }
        });
    }

    public PathDoorOption getPathDoorOption() {
        if (this.getLevel() != null) {
            return (Boolean)this.buffManager.getModifier(BuffModifiers.CAN_BREAK_OBJECTS) != false ? this.getLevel().regionManager.CAN_BREAK_OBJECTS_OPTIONS : this.getLevel().regionManager.CAN_OPEN_DOORS_OPTIONS;
        }
        return null;
    }

    public void init() {
        super.init();
        this.ai = new BehaviourTreeAI((Mob)this, new CrystalGolemAI(544, 320, this.isSummoned ? 960 : 384));
    }

    public float getAttackingMovementModifier() {
        return 0.0f;
    }

    public void tickMovement(float delta) {
        super.tickMovement(delta);
        if (!this.isServer()) {
            if (this.shootTime != 0L) {
                long timer = this.shootTime - this.getWorldEntity().getTime();
                Point2D.Float target = this.shootTarget != null ? new Point2D.Float(this.shootTarget.x, this.shootTarget.y) : new Point2D.Float(this.shootX, this.shootY);
                this.setFacingDir(target.x - this.x, target.y - this.y);
                RayLinkedList rays = GameUtils.castRay((Level)this.getLevel(), (double)this.x, (double)this.y, (double)(target.x - this.x), (double)(target.y - this.y), (double)1000.0, (int)0, (CollisionFilter)new CollisionFilter().projectileCollision());
                if (this.warningBeam == null) {
                    this.warningBeam = new ParticleBeamHandler(this.getLevel()).particleSize(10, 12).particleThicknessMod(0.2f).endParticleSize(8, 12).distPerParticle(20.0f).thickness(10, 2).speed(50.0f).height(24.0f).sprite(new GameSprite(GameResources.chains, 7, 0, 32));
                }
                int alpha = GameMath.limit((int)GameMath.lerp((float)((float)timer / (float)(chargeTime + stickTime)), (int)255, (int)0), (int)0, (int)255);
                this.warningBeam.color(AphColors.withAlpha(AphColors.spinel, alpha));
                this.warningBeam.update(rays, delta);
            } else if (this.warningBeam != null) {
                this.warningBeam.dispose();
                this.warningBeam = null;
            }
        }
    }

    public void clientTick() {
        super.clientTick();
        this.tickShooting();
    }

    public void serverTick() {
        super.serverTick();
        this.tickShooting();
    }

    private void tickShooting() {
        if (this.shootTime != 0L) {
            long timer = this.shootTime - this.getWorldEntity().getTime();
            if (!this.isServer()) {
                if (timer <= 0L) {
                    this.shootTime = 0L;
                    return;
                }
                this.spawnChargeParticles();
            } else if (timer <= (long)stickTime && this.shootTarget != null && this.isSamePlace((Entity)this.shootTarget)) {
                this.stickShootAbility.runAndSend(this.shootTarget.getX(), this.shootTarget.getY());
            } else if (timer <= 0L) {
                this.shootAbility.runAndSend(this.shootX, this.shootY);
                this.shootTime = 0L;
            }
        }
    }

    public void shootAbilityProjectile(int x, int y) {
        if (this.isServer()) {
            SpinelGolemBeamProjectile p = new SpinelGolemBeamProjectile(this.getLevel(), (Mob)this, this.x, this.y, x, y, 1000, damage, 20);
            this.getLevel().entityManager.projectiles.add((Entity)p);
        }
        if (this.isClient()) {
            if (this.warningBeam != null) {
                this.warningBeam.dispose();
            }
            this.warningBeam = null;
            SoundManager.playSound((GameSound)GameResources.firespell1, (SoundEffect)SoundEffect.effect((PrimitiveSoundEmitter)this).pitch(1.8f).volume(0.8f));
        }
        this.shootTime = 0L;
    }

    public void spawnChargeParticles() {
        for (int i = 0; i < 2; ++i) {
            int angle = GameRandom.globalRandom.nextInt(360);
            Point2D.Float dir = GameMath.getAngleDir((float)angle);
            float range = GameRandom.globalRandom.getFloatBetween(25.0f, 40.0f);
            float startX = this.x + dir.x * range;
            float startY = this.y + 4.0f;
            float endHeight = 29.0f;
            float startHeight = endHeight + dir.y * range;
            int lifeTime = GameRandom.globalRandom.getIntBetween(200, 500);
            float speed = dir.x * range * 250.0f / (float)lifeTime;
            Color color = (Color)GameRandom.globalRandom.getOneOf((Object[])new Color[]{AphColors.spinel_lighter, AphColors.spinel_light, AphColors.spinel, AphColors.spinel_dark});
            float hueMod = (float)this.getLevel().getWorldEntity().getLocalTime() / 10.0f % 240.0f;
            float glowHue = hueMod < 120.0f ? hueMod + 200.0f : 440.0f - hueMod;
            this.getLevel().entityManager.addParticle(startX, startY, Particle.GType.IMPORTANT_COSMETIC).sprite(GameResources.puffParticles.sprite(GameRandom.globalRandom.nextInt(5), 0, 12)).sizeFades(10, 16).rotates().heightMoves(startHeight, endHeight).movesConstant(-speed, 0.0f).color(color).givesLight(glowHue, 1.0f).ignoreLight(true).fadesAlphaTime(100, 50).lifeTime(lifeTime);
        }
    }

    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for (int i = 0; i < 4; ++i) {
            this.getLevel().entityManager.addParticle((Particle)new FleshParticle(this.getLevel(), MobRegistry.Textures.crystalGolem, GameRandom.globalRandom.nextInt(5), 8, 32, this.x, this.y, 20.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(x / 32, y / 32);
        int drawX = camera.getDrawX(x) - 22 - 10;
        int drawY = camera.getDrawY(y) - 44 - 7 - 6;
        int dir = this.getDir();
        Point sprite = this.getAnimSprite(x, y, dir);
        drawY += this.getBobbing(x, y);
        drawY += this.getLevel().getTile(x / 32, y / 32).getMobSinkingAmount((Mob)this);
        if (this.isAttacking) {
            sprite.x = 0;
        }
        final MaskShaderOptions swimMask = this.getSwimMaskShaderOptions(this.inLiquidFloat(x, y));
        TextureDrawOptionsEnd drawOptions = texture.initDraw().sprite(sprite.x, sprite.y, 64).addMaskShader(swimMask).light(light.minLevelCopy(100.0f)).pos(drawX, drawY);
        list.add(new MobDrawable((DrawOptions)drawOptions){
            final /* synthetic */ DrawOptions val$drawOptions;
            {
                this.val$drawOptions = drawOptions;
            }

            public void draw(TickManager tickManager) {
                swimMask.use();
                this.val$drawOptions.draw();
                swimMask.stop();
            }
        });
        this.addShadowDrawables(tileList, level, x, y, light, camera);
    }

    public void dispose() {
        super.dispose();
        if (this.warningBeam != null) {
            this.warningBeam.dispose();
        }
        this.warningBeam = null;
    }

    public LootTable getLootTable() {
        return lootTable;
    }

    static {
        damage = new GameDamage(20.0f, 40.0f);
        chargeTime = 600;
        stickTime = 100;
        lootTable = new LootTable(new LootItemInterface[]{new OneOfTicketLootItems(new Object[]{7, new LootItem("spinel"), 1, new LootItem("lifespinel")})});
    }

    public static class CrystalGolemAI<T extends SpinelGolem>
    extends SequenceAINode<T> {
        public final EscapeAINode<T> escapeAINode = new EscapeAINode<T>(){

            public boolean shouldEscape(T mob, Blackboard<T> blackboard) {
                return ((SpinelGolem)((Object)mob)).isHostile && !((SpinelGolem)((Object)mob)).isSummoned && (Boolean)mob.getLevel().buffManager.getModifier(LevelModifiers.ENEMIES_RETREATING) != false;
            }
        };
        public final CooldownAttackTargetAINode<T> shootAtTargetNode;
        public final TargetFinderAINode<T> targetFinderNode;
        public final ChaserAINode<T> chaserNode;

        public CrystalGolemAI(int shootDistance, int meleeDistance, int searchDistance) {
            this.addChild((AINode)new InverterAINode((AINode)this.escapeAINode));
            if (shootDistance > 0) {
                this.shootAtTargetNode = new CooldownAttackTargetAINode<T>(CooldownAttackTargetAINode.CooldownTimer.TICK, chargeTime + stickTime + 500, shootDistance){

                    public boolean attackTarget(T mob, Mob target) {
                        if (mob.canAttack() && ((SpinelGolem)((Object)mob)).shootTime == 0L) {
                            ((SpinelGolem)((Object)mob)).startShootingAbility.runAndSend(target);
                            return true;
                        }
                        return false;
                    }
                };
                this.addChild((AINode)this.shootAtTargetNode);
                this.shootAtTargetNode.attackTimer = this.shootAtTargetNode.attackCooldown;
            } else {
                this.shootAtTargetNode = null;
            }
            TargetFinderDistance targetFinder = new TargetFinderDistance(searchDistance);
            targetFinder.targetLostAddedDistance = searchDistance * 2;
            this.targetFinderNode = new TargetFinderAINode<T>(targetFinder){

                public GameAreaStream<? extends Mob> streamPossibleTargets(T mob, Point base, TargetFinderDistance<T> distance) {
                    return TargetFinderAINode.streamPlayersAndHumans(mob, (Point)base, distance);
                }
            };
            this.addChild((AINode)this.targetFinderNode);
            this.chaserNode = new ChaserAINode<T>(meleeDistance, false, true){

                public boolean attackTarget(T mob, Mob target) {
                    return false;
                }
            };
            this.addChild((AINode)this.chaserNode);
        }
    }
}

