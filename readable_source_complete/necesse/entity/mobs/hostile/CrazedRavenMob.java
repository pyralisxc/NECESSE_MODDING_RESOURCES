/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.WaitForSecondsEvent;
import necesse.entity.manager.EntityManager;
import necesse.entity.mobs.DeathMessageTable;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.MaskShaderOptions;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.ConfusedPlayerChaserWandererAI;
import necesse.entity.mobs.hostile.HostileMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.CrazedRavenFeatherProjectile;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class CrazedRavenMob
extends HostileMob {
    public static LootTable lootTable = new LootTable(LootItem.between("ravenfeather", 1, 2), ChanceLootItem.between(0.05f, "egg", 1, 1));
    public static GameDamage damage = new GameDamage(65.0f);

    public CrazedRavenMob() {
        super(400);
        this.setSpeed(70.0f);
        this.setFriction(3.0f);
        this.setKnockbackModifier(0.5f);
        this.setArmor(30);
        this.attackCooldown = 1500;
        this.collision = new Rectangle(-10, -7, 20, 14);
        this.hitBox = new Rectangle(-14, -12, 28, 24);
        this.selectBox = new Rectangle(-14, -41, 28, 48);
        this.swimMaskMove = 16;
        this.swimMaskOffset = -2;
        this.swimSinkOffset = -4;
    }

    @Override
    public void init() {
        super.init();
        this.ai = new BehaviourTreeAI<CrazedRavenMob>(this, new ConfusedPlayerChaserWandererAI<CrazedRavenMob>(() -> false, 480, 320, 20000, false, false){

            @Override
            public boolean attackTarget(CrazedRavenMob mob, Mob target) {
                if (mob.canAttack()) {
                    CrazedRavenMob.fireCrazedRavenProjectiles(mob, target);
                    this.wanderAfterAttack = GameRandom.globalRandom.getChance(0.75f);
                    return true;
                }
                return false;
            }
        });
    }

    private static void fireCrazedRavenProjectiles(final CrazedRavenMob mob, final Mob target) {
        final EntityManager entityManager = mob.getLevel().entityManager;
        mob.attack(target.getX(), target.getY(), false);
        for (int i = 0; i < 2; ++i) {
            CrazedRavenFeatherProjectile duoProjectile = new CrazedRavenFeatherProjectile(mob.getLevel(), mob.x, mob.y, target.x, target.y, 80.0f, 576, damage, mob, 50);
            duoProjectile.setAngle(duoProjectile.getAngle() - 30.0f + (float)(i * 60));
            entityManager.projectiles.add(duoProjectile);
        }
        entityManager.addLevelEventHidden(new WaitForSecondsEvent(0.5f){

            @Override
            public void onWaitOver() {
                mob.attack(target.getX(), target.getY(), false);
                CrazedRavenFeatherProjectile singleProjectile = new CrazedRavenFeatherProjectile(mob.getLevel(), mob.x, mob.y, target.x, target.y, 120.0f, 768, damage, mob, 50);
                entityManager.projectiles.add(singleProjectile);
            }
        });
        entityManager.addLevelEventHidden(new WaitForSecondsEvent(1.0f){

            @Override
            public void onWaitOver() {
                mob.attack(target.getX(), target.getY(), false);
                for (int i = 0; i < 3; ++i) {
                    CrazedRavenFeatherProjectile duoProjectile = new CrazedRavenFeatherProjectile(mob.getLevel(), mob.x, mob.y, target.x, target.y, 40.0f, 448, damage, mob, 50);
                    duoProjectile.setAngle(duoProjectile.getAngle() - 40.0f + (float)(i * 40));
                    entityManager.projectiles.add(duoProjectile);
                }
            }
        });
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
    public DeathMessageTable getDeathMessages() {
        return this.getDeathMessages("crazedraven", 3);
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for (int i = 0; i < 4; ++i) {
            this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), MobRegistry.Textures.crazedRaven.body, GameRandom.globalRandom.nextInt(6), 8, 32, this.x, this.y, 20.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    @Override
    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(GameMath.getTileCoordinate(x), GameMath.getTileCoordinate(y));
        int drawX = camera.getDrawX(x) - 22 - 10;
        int drawY = camera.getDrawY(y) - 44 - 7;
        int dir = this.getDir();
        Point sprite = this.getAnimSprite(x, y, dir);
        drawY += this.getBobbing(x, y);
        drawY += this.getLevel().getTile(GameMath.getTileCoordinate(x), GameMath.getTileCoordinate(y)).getMobSinkingAmount(this);
        MaskShaderOptions swimMask = this.getSwimMaskShaderOptions(this.inLiquidFloat(x, y));
        HumanDrawOptions humanDrawOptions = new HumanDrawOptions(level, MobRegistry.Textures.crazedRaven).sprite(sprite).dir(dir).mask(swimMask).light(light);
        float animProgress = this.getAttackAnimProgress();
        if (this.isAttacking) {
            ItemAttackDrawOptions attackOptions = ItemAttackDrawOptions.start(dir).armSprite(MobRegistry.Textures.crazedRaven.body, 0, 8, 32).swingRotation(animProgress).light(light);
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
    public int getRockSpeed() {
        return 20;
    }

    @Override
    public void showAttack(int x, int y, int seed, boolean showAllDirections) {
        super.showAttack(x, y, seed, showAllDirections);
        if (this.isClient()) {
            SoundManager.playSound(GameResources.magicbolt1, (SoundEffect)SoundEffect.effect(this).pitch(1.4f));
        }
    }
}

