/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import java.util.stream.Stream;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.MaskShaderOptions;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ability.CoordinateMobAbility;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.decorators.FailerAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.TeleportOnProjectileHitAINode;
import necesse.entity.mobs.ai.behaviourTree.trees.ConfusedPlayerChaserWandererAI;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.hostile.HostileMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.entity.particle.SmokePuffParticle;
import necesse.entity.projectile.NinjaStarProjectile;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItemList;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.MobConditionLootItemList;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;
import necesse.level.maps.IncursionLevel;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class NinjaMob
extends HostileMob {
    public static LootTable lootTable = new LootTable(new MobConditionLootItemList(mob -> mob.getLevel() == null || !mob.getLevel().isIncursionLevel, new ChanceLootItemList(0.25f, new OneOfLootItems(new LootItem("ninjahood"), new LootItem("ninjarobe"), new LootItem("ninjashoes"))), LootItem.between("ninjastar", 15, 30)));
    public static GameDamage baseDamage = new GameDamage(70.0f);
    public static GameDamage incursionDamage = new GameDamage(115.0f);
    public final CoordinateMobAbility teleportAbility;

    public NinjaMob() {
        super(400);
        this.attackCooldown = 500;
        this.attackAnimTime = 200;
        this.setSpeed(40.0f);
        this.setFriction(3.0f);
        this.setArmor(10);
        this.moveAccuracy = 8;
        this.collision = new Rectangle(-10, -7, 20, 14);
        this.hitBox = new Rectangle(-14, -12, 28, 24);
        this.selectBox = new Rectangle(-14, -41, 28, 48);
        this.swimMaskMove = 16;
        this.swimMaskOffset = -2;
        this.swimSinkOffset = -4;
        this.teleportAbility = this.registerAbility(new CoordinateMobAbility(){

            @Override
            protected void run(int x, int y) {
                if (NinjaMob.this.isClient()) {
                    NinjaMob.this.getLevel().entityManager.addParticle(new SmokePuffParticle(NinjaMob.this.getLevel(), NinjaMob.this.x, NinjaMob.this.y), Particle.GType.CRITICAL);
                    NinjaMob.this.getLevel().entityManager.addParticle(new SmokePuffParticle(NinjaMob.this.getLevel(), x, y), Particle.GType.CRITICAL);
                }
                NinjaMob.this.setPos(x, y, true);
            }
        });
    }

    @Override
    public void init() {
        GameDamage damage;
        super.init();
        if (this.getLevel() instanceof IncursionLevel) {
            this.setMaxHealth(500);
            this.setHealthHidden(this.getMaxHealth());
            this.setArmor(30);
            damage = incursionDamage;
        } else {
            damage = baseDamage;
        }
        ConfusedPlayerChaserWandererAI<NinjaMob> playerChaserAI = new ConfusedPlayerChaserWandererAI<NinjaMob>(null, 640, 320, 40000, true, true){

            @Override
            public boolean attackTarget(NinjaMob mob, Mob target) {
                if (mob.canAttack() && !mob.isAccelerating() && !mob.hasCurrentMovement()) {
                    mob.attack(target.getX(), target.getY(), false);
                    mob.getLevel().entityManager.projectiles.add(new NinjaStarProjectile(mob.x, mob.y, target.x, target.y, damage, mob));
                    this.wanderAfterAttack = GameRandom.globalRandom.getChance(0.75f);
                    return true;
                }
                return false;
            }
        };
        playerChaserAI.addChildFirst(new FailerAINode<NinjaMob>(new TeleportOnProjectileHitAINode<NinjaMob>(5000, 7){

            @Override
            public boolean teleport(NinjaMob mob, int x, int y) {
                if (mob.isServer()) {
                    mob.teleportAbility.runAndSend(x, y);
                    this.getBlackboard().mover.stopMoving(mob);
                }
                return true;
            }
        }));
        this.ai = new BehaviourTreeAI<NinjaMob>(this, playerChaserAI);
    }

    @Override
    public void clientTick() {
        super.clientTick();
        if (this.isAttacking) {
            this.getAttackAnimProgress();
        }
    }

    @Override
    public void serverTick() {
        super.serverTick();
        if (this.isAttacking) {
            this.getAttackAnimProgress();
        }
    }

    @Override
    public LootTable getLootTable() {
        return lootTable;
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for (int i = 0; i < 4; ++i) {
            this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), MobRegistry.Textures.ninja.body, GameRandom.globalRandom.nextInt(5), 8, 32, this.x, this.y, 20.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    @Override
    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(NinjaMob.getTileCoordinate(x), NinjaMob.getTileCoordinate(y));
        int drawX = camera.getDrawX(x) - 22 - 10;
        int drawY = camera.getDrawY(y) - 44 - 7;
        int dir = this.getDir();
        Point sprite = this.getAnimSprite(x, y, dir);
        drawY += this.getBobbing(x, y);
        drawY += level.getTile(NinjaMob.getTileCoordinate(x), NinjaMob.getTileCoordinate(y)).getMobSinkingAmount(this);
        MaskShaderOptions swimMask = this.getSwimMaskShaderOptions(this.inLiquidFloat(x, y));
        HumanDrawOptions humanDrawOptions = new HumanDrawOptions(level, MobRegistry.Textures.ninja).sprite(sprite).dir(dir).mask(swimMask).light(light);
        float animProgress = this.getAttackAnimProgress();
        if (this.isAttacking) {
            humanDrawOptions.itemAttack(new InventoryItem("ninjastar"), null, animProgress, this.attackDir.x, this.attackDir.y);
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
    public void showAttack(int x, int y, int seed, boolean showAllDirections) {
        super.showAttack(x, y, seed, showAllDirections);
        if (this.isClient()) {
            SoundManager.playSound(GameResources.swing1, (SoundEffect)SoundEffect.effect(this));
        }
    }

    @Override
    public Stream<ModifierValue<?>> getDefaultModifiers() {
        return Stream.of(new ModifierValue<Float>(BuffModifiers.FRICTION, Float.valueOf(0.0f)).min(Float.valueOf(0.75f)));
    }
}

