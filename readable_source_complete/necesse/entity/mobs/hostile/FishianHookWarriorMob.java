/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.DeathMessageTable;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.MaskShaderOptions;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.MobWasHitEvent;
import necesse.entity.mobs.PathDoorOption;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ability.BooleanMobAbility;
import necesse.entity.mobs.ability.IntMobAbility;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.ConfusedPlayerChaserWandererAI;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.hostile.HostileMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.Projectile;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.maps.IncursionLevel;
import necesse.level.maps.Level;
import necesse.level.maps.TilePosition;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.light.GameLight;

public class FishianHookWarriorMob
extends HostileMob {
    public static LootTable lootTable = new LootTable(LootItem.between("coin", 10, 40).splitItems(4), LootItem.between("bamboo", 5, 10).splitItems(4));
    public static GameDamage baseDamage = new GameDamage(90.0f);
    public static GameDamage incursionDamage = new GameDamage(100.0f);
    private boolean isStunned;
    private long stunStartTime;
    private int projectileUniqueID;
    private Projectile projectile;
    private final IntMobAbility setProjectileUniqueID;
    private final BooleanMobAbility triggerStunAbility;

    public FishianHookWarriorMob() {
        super(1200);
        this.setSpeed(45.0f);
        this.setFriction(3.0f);
        this.setKnockbackModifier(0.5f);
        this.setArmor(30);
        this.attackCooldown = 3000;
        this.collision = new Rectangle(-10, -7, 20, 14);
        this.hitBox = new Rectangle(-14, -12, 28, 24);
        this.selectBox = new Rectangle(-14, -41, 28, 48);
        this.swimMaskMove = 16;
        this.swimMaskOffset = -2;
        this.swimSinkOffset = -4;
        this.setProjectileUniqueID = this.registerAbility(new IntMobAbility(){

            @Override
            protected void run(int value) {
                FishianHookWarriorMob.this.projectileUniqueID = value;
                if (FishianHookWarriorMob.this.projectileUniqueID != 0) {
                    FishianHookWarriorMob.this.projectile = FishianHookWarriorMob.this.getLevel().entityManager.projectiles.get(FishianHookWarriorMob.this.projectileUniqueID, false);
                } else {
                    FishianHookWarriorMob.this.projectile = null;
                }
            }
        });
        this.triggerStunAbility = this.registerAbility(new BooleanMobAbility(){

            @Override
            protected void run(boolean value) {
                FishianHookWarriorMob.this.isStunned = value;
                if (value) {
                    FishianHookWarriorMob.this.stunStartTime = FishianHookWarriorMob.this.getLocalTime();
                    FishianHookWarriorMob.this.setSpeed(0.0f);
                    if (FishianHookWarriorMob.this.isClient()) {
                        FishianHookWarriorMob.this.spawnStunnedParticles();
                    }
                } else {
                    FishianHookWarriorMob.this.setSpeed(40.0f);
                }
            }
        });
    }

    @Override
    public void init() {
        GameDamage damage;
        super.init();
        if (this.getLevel() instanceof IncursionLevel) {
            this.setMaxHealth(1500);
            this.setHealthHidden(this.getMaxHealth());
            this.setArmor(30);
            damage = incursionDamage;
        } else {
            damage = baseDamage;
        }
        this.ai = new BehaviourTreeAI<FishianHookWarriorMob>(this, new ConfusedPlayerChaserWandererAI<FishianHookWarriorMob>(null, 640, 256, 40000, false, false){

            @Override
            protected int getRandomConfuseTime() {
                return GameRandom.globalRandom.getIntBetween(3000, 5000);
            }

            @Override
            public boolean attackTarget(FishianHookWarriorMob mob, Mob target) {
                Projectile projectile = this.shootAndGetSimpleProjectile(mob, target, "fishianwarriorhook", damage, 150, 480, 10);
                if (projectile != null) {
                    this.wanderAfterAttack = true;
                    FishianHookWarriorMob.this.setProjectileUniqueID.runAndSend(projectile.getUniqueID());
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void showAttack(int x, int y, int seed, boolean showAllDirections) {
        super.showAttack(x, y, seed, showAllDirections);
        SoundManager.playSound(GameResources.fishianWarriorHook, (SoundEffect)SoundEffect.effect(this));
    }

    @Override
    public LootTable getLootTable() {
        return lootTable;
    }

    @Override
    public PathDoorOption getPathDoorOption() {
        if (this.getLevel() != null) {
            if (this.buffManager.getModifier(BuffModifiers.CAN_BREAK_OBJECTS).booleanValue()) {
                return this.getLevel().regionManager.CAN_BREAK_OBJECTS_OPTIONS;
            }
            return this.getLevel().regionManager.CAN_OPEN_DOORS_OPTIONS;
        }
        return null;
    }

    public Projectile getProjectile() {
        if (this.projectile != null) {
            if (this.projectile.getUniqueID() != this.projectileUniqueID) {
                this.projectile = null;
            } else if (this.projectile.removed()) {
                this.projectile = null;
            }
        }
        if (this.projectile == null && this.projectileUniqueID != 0) {
            this.projectile = this.getLevel().entityManager.projectiles.get(this.projectileUniqueID, false);
        }
        return this.projectile;
    }

    @Override
    public void serverTick() {
        super.serverTick();
        Projectile projectile = this.getProjectile();
        if (projectile == null) {
            this.setProjectileUniqueID.runAndSend(0);
        }
        if (this.getLocalTime() > this.stunStartTime + 4000L) {
            this.triggerStunAbility.runAndSend(false);
        }
    }

    @Override
    public boolean canAttack() {
        return !this.isStunned && this.getProjectile() == null && super.canAttack();
    }

    @Override
    protected void doWasHitLogic(MobWasHitEvent event) {
        super.doWasHitLogic(event);
        int stunThreshold = (event.damage + this.getHealth()) / 2;
        if (event.damage > stunThreshold && !event.wasPrevented && this.isServer() && !this.isStunned && this.getHealth() > 0) {
            this.triggerStunAbility.runAndSend(true);
        }
    }

    public void spawnStunnedParticles() {
        GameRandom random = GameRandom.globalRandom;
        AtomicReference<Float> currentAngle = new AtomicReference<Float>(Float.valueOf(random.nextFloat() * 360.0f));
        float distance = 20.0f;
        this.getLevel().entityManager.addTopParticle(this.x + GameMath.sin(currentAngle.get().floatValue()) * distance, this.y + GameMath.cos(currentAngle.get().floatValue()) * distance, Particle.GType.CRITICAL).sprite(GameResources.starParticles.sprite(0, 0, 18)).moves((pos, delta, lifeTime, timeAlive, lifePercent) -> {
            float angle = currentAngle.accumulateAndGet(Float.valueOf(delta * 50.0f / 250.0f), Float::sum).floatValue();
            pos.x = this.x - GameMath.sin(angle) * distance;
            pos.y = this.y - 12.0f - (this.x - pos.x) / 4.0f - angle / 36.0f + GameMath.cos(angle) / 2.0f * distance;
        }).lifeTime(4000).sizeFades(22, 44).removeIf(this::removed);
        this.getLevel().entityManager.addTopParticle(this.x + GameMath.sin(currentAngle.get().floatValue()) * distance, this.y + GameMath.cos(currentAngle.get().floatValue()) * distance, Particle.GType.CRITICAL).sprite(GameResources.starParticles.sprite(0, 0, 18)).moves((pos, delta, lifeTime, timeAlive, lifePercent) -> {
            float angle = currentAngle.accumulateAndGet(Float.valueOf(delta * 50.0f / 250.0f), Float::sum).floatValue();
            pos.x = this.x + GameMath.sin(angle) * distance;
            pos.y = this.y - 12.0f + (this.x - pos.x) / 4.0f - angle / 36.0f + GameMath.cos(angle) / 2.0f * distance;
        }).lifeTime(4000).sizeFades(22, 44).removeIf(this::removed);
    }

    @Override
    public DeathMessageTable getDeathMessages() {
        return this.getDeathMessages("fishian", 3);
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for (int i = 0; i < 4; ++i) {
            this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), MobRegistry.Textures.fishianHookWarrior.body, GameRandom.globalRandom.nextInt(5), 8, 32, this.x, this.y, 20.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    @Override
    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(FishianHookWarriorMob.getTileCoordinate(x), FishianHookWarriorMob.getTileCoordinate(y));
        int drawX = camera.getDrawX(x) - 22 - 10;
        int drawY = camera.getDrawY(y) - 44 - 7;
        int dir = this.getDir();
        float animProgress = this.getAttackAnimProgress();
        Point sprite = this.getAnimSprite(x, y, dir);
        drawY += this.getBobbing(x, y);
        drawY += level.getTile(FishianHookWarriorMob.getTileCoordinate(x), FishianHookWarriorMob.getTileCoordinate(y)).getMobSinkingAmount(this);
        MaskShaderOptions swimMask = this.getSwimMaskShaderOptions(this.inLiquidFloat(x, y));
        HumanDrawOptions humanDrawOptions = new HumanDrawOptions(level, MobRegistry.Textures.fishianHookWarrior).sprite(sprite).dir(dir).mask(swimMask).light(light);
        if (!this.isAttacking && this.canAttack()) {
            humanDrawOptions.itemAttack(new InventoryItem("fishianwarriorhook"), null, animProgress, 0.0f, 0.0f);
        } else {
            Projectile projectile = this.getProjectile();
            if (projectile != null) {
                Point2D.Float attackDir = GameMath.normalize(projectile.x - (float)x, projectile.y - (float)y);
                ItemAttackDrawOptions attackOptions = ItemAttackDrawOptions.start(dir).armSprite(MobRegistry.Textures.fishianHookWarrior.body, 0, 8, 32).pointRotation(attackDir.x, attackDir.y).light(light);
                humanDrawOptions.attackAnim(attackOptions, animProgress);
            }
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
    public int getRockSpeed() {
        return 20;
    }

    @Override
    public Stream<ModifierValue<?>> getDefaultModifiers() {
        return Stream.of(new ModifierValue<Float>(BuffModifiers.FRICTION, Float.valueOf(0.0f)).min(Float.valueOf(0.75f)));
    }

    @Override
    public int getTileWanderPriority(TilePosition pos, Biome baseBiome) {
        if (pos.tileID() == TileRegistry.puddleCobble) {
            return 1000;
        }
        return super.getTileWanderPriority(pos, baseBiome);
    }
}

