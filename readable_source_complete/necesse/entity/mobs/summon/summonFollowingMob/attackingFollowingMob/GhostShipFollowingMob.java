/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.ProjectileRegistry;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ability.BooleanMobAbility;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.leaves.CooldownAttackTargetAINode;
import necesse.entity.mobs.ai.behaviourTree.trees.PlayerFlyingFollowerChargeChaserAI;
import necesse.entity.mobs.ai.behaviourTree.trees.PlayerFollowerChaserAI;
import necesse.entity.mobs.ai.behaviourTree.util.FlyingAIMover;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.FlyingAttackingFollowingMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.Projectile;
import necesse.entity.trails.Trail;
import necesse.gfx.GameResources;
import necesse.gfx.HumanGender;
import necesse.gfx.HumanLook;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class GhostShipFollowingMob
extends FlyingAttackingFollowingMob {
    public Trail trail;
    public float moveAngle;
    public float currentAngle;
    protected int settlerSeed = GameRandom.globalRandom.nextInt();
    protected HumanLook look;
    boolean isBroadsideAttacking;
    protected BooleanMobAbility chargeBroardsideAttack;

    public GhostShipFollowingMob() {
        super(10);
        this.moveAccuracy = 15;
        this.setSpeed(50.0f);
        this.setFriction(2.0f);
        this.collision = new Rectangle(-18, -15, 36, 30);
        this.hitBox = new Rectangle(-18, -15, 36, 36);
        this.selectBox = new Rectangle();
        this.chargeBroardsideAttack = this.registerAbility(new BooleanMobAbility(){

            @Override
            protected void run(boolean value) {
            }
        });
    }

    @Override
    public void clientTick() {
        super.clientTick();
        Level level = this.getLevel();
        if (level.tickManager().getTotalTicks() % 2L == 0L) {
            level.entityManager.addParticle(this.x + (float)(GameRandom.globalRandom.nextGaussian() * 14.0), this.y + (float)(GameRandom.globalRandom.nextGaussian() * 30.0), Particle.GType.COSMETIC).movesConstant(this.dx / 10.0f, this.dy / 10.0f).color(new Color(176, 234, 190)).sizeFades(6, 10).height(42.0f);
        }
        level.lightManager.refreshParticleLightFloat(this.x, this.y, 200.0f, 0.6f, 10);
    }

    @Override
    public void init() {
        super.init();
        this.updateLook();
        this.ai = new BehaviourTreeAI<GhostShipFollowingMob>(this, new PlayerFollowerChaserAI<GhostShipFollowingMob>(576, 320, false, false, 640, 64){

            @Override
            public boolean attackTarget(GhostShipFollowingMob mob, Mob target) {
                if (mob.canAttack()) {
                    mob.attack(target.getX(), target.getY(), false);
                    Projectile projectile = ProjectileRegistry.getProjectile("ghostskull", mob.getLevel(), mob.x, mob.y, target.x, target.y, 110.0f, 640, GhostShipFollowingMob.this.summonDamage, (Mob)mob);
                    projectile.setTargetPrediction(target, -20.0f);
                    projectile.moveDist(20.0);
                    mob.getLevel().entityManager.projectiles.add(projectile);
                    return true;
                }
                return false;
            }
        }, new FlyingAIMover());
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for (int i = 0; i < 7; ++i) {
            this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), MobRegistry.Textures.ghostShip, i, 10, 32, this.x, this.y, 20.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    @Override
    protected SoundSettings getHitDeathSound() {
        return new SoundSettings(GameResources.fadedeath3).volume(0.5f).basePitch(1.5f);
    }

    @Override
    protected void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(GameMath.getTileCoordinate(x), GameMath.getTileCoordinate(y));
        float rotate = GameMath.limit(this.dx / 10.0f, -10.0f, 10.0f);
        int drawX = camera.getDrawX(x) - 64;
        int drawY = camera.getDrawY(y) - 80;
        int timePerFrame = 150;
        int spriteIndex = (int)(this.getWorldEntity().getTime() / (long)timePerFrame) % 4;
        final TextureDrawOptionsEnd options = MobRegistry.Textures.ghostShip.initDraw().sprite(spriteIndex, this.isBroadsideAttacking ? 1 : 0, 128, 160).size(128, 160).rotate(rotate).light(light.minLevelCopy(150.0f)).pos(drawX, drawY -= this.getFlyingHeight()).alpha(0.75f);
        topList.add(new MobDrawable(){

            @Override
            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
    }

    public HumanDrawOptions getHumanDrawOptions(Level level) {
        HumanDrawOptions humanDrawOptions = new HumanDrawOptions(level, this.look, true);
        humanDrawOptions.helmet(new InventoryItem("captainshat"));
        humanDrawOptions.chestplate(new InventoryItem("captainsshirt"));
        humanDrawOptions.boots(new InventoryItem("captainsboots"));
        return humanDrawOptions;
    }

    public void updateLook() {
        Mob mob = MobRegistry.getMob("piratehuman", this.getLevel());
        if (mob instanceof HumanMob) {
            HumanMob humanMob = (HumanMob)mob;
            humanMob.setSettlerSeed(this.settlerSeed, false);
            this.look = humanMob.look;
        } else {
            this.look = new HumanLook();
            GameRandom random = new GameRandom(this.settlerSeed);
            this.look.randomizeLook(random, true, HumanGender.MALE, true, true, true, true);
            this.look.setFacialFeature(random.getOneOf(1, 3, 4));
        }
        this.look.setEyeType(7);
    }

    static class GhostShipAI<T extends GhostShipFollowingMob>
    extends PlayerFlyingFollowerChargeChaserAI<T> {
        public GhostShipAI(int searchDistance, CooldownAttackTargetAINode.CooldownTimer cooldownTimer, int chargeCooldown, int targetStoppingDistance, int teleportDistance, int stoppingDistance) {
            super(searchDistance, cooldownTimer, chargeCooldown, targetStoppingDistance, teleportDistance, stoppingDistance);
        }
    }
}

