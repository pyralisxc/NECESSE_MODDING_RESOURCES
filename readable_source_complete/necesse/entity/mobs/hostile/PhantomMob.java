/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.gameLoop.tickManager.TicksPerSecond;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ability.EmptyMobAbility;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.PlayerChaserWandererAI;
import necesse.entity.mobs.ai.behaviourTree.util.FlyingAIMover;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.hostile.FlyingHostileMob;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.PhantomBoltProjectile;
import necesse.entity.trails.Trail;
import necesse.entity.trails.TrailVector;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.lootTable.LootTable;
import necesse.level.maps.Level;
import necesse.level.maps.incursion.GraveyardIncursionBiome;
import necesse.level.maps.light.GameLight;

public class PhantomMob
extends FlyingHostileMob {
    public static LootTable lootTable = new LootTable(GraveyardIncursionBiome.graveyardMobDrops);
    public static GameDamage damage = new GameDamage(115.0f);
    public Trail trail;
    public TicksPerSecond particleTicks = TicksPerSecond.ticksPerSecond(20);
    public ParticleTypeSwitcher particleTypes = new ParticleTypeSwitcher(Particle.GType.COSMETIC, Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC);
    public final EmptyMobAbility playBoltSoundAbility;

    public PhantomMob() {
        super(450);
        this.setSpeed(70.0f);
        this.setFriction(0.5f);
        this.setArmor(30);
        this.setKnockbackModifier(0.0f);
        this.moveAccuracy = 20;
        this.attackCooldown = 4000;
        this.spawnLightThreshold = new ModifierValue<Integer>(BuffModifiers.MOB_SPAWN_LIGHT_THRESHOLD, 0).min(150, Integer.MAX_VALUE);
        this.collision = new Rectangle(-10, -7, 20, 14);
        this.hitBox = new Rectangle(-14, -12, 28, 24);
        this.selectBox = new Rectangle(-16, -40, 32, 32);
        this.playBoltSoundAbility = this.registerAbility(new EmptyMobAbility(){

            @Override
            protected void run() {
                if (PhantomMob.this.isClient()) {
                    SoundManager.playSound(GameResources.magicbolt2, SoundEffect.globalEffect().volume(0.5f).pitch(1.1f));
                }
            }
        });
    }

    @Override
    public void init() {
        super.init();
        PlayerChaserWandererAI<PhantomMob> chaserAI = new PlayerChaserWandererAI<PhantomMob>(null, 512, 512, 40000, true, false){

            @Override
            public boolean canHitTarget(PhantomMob mob, float fromX, float fromY, Mob target) {
                return true;
            }

            @Override
            public boolean attackTarget(PhantomMob mob, Mob target) {
                if (mob.canAttack()) {
                    mob.attack(target.getX(), target.getY(), false);
                    PhantomBoltProjectile projectile = new PhantomBoltProjectile(mob.getLevel(), mob, mob.x, mob.y, target.x, target.y, 60.0f, 768, damage, 50);
                    projectile.moveDist(15.0);
                    mob.getLevel().entityManager.projectiles.add(projectile);
                    PhantomMob.this.playBoltSoundAbility.runAndSend();
                    return true;
                }
                return false;
            }
        };
        chaserAI.playerChaserAI.chaserAINode.changePositionConstantly = true;
        this.ai = new BehaviourTreeAI<PhantomMob>(this, chaserAI, new FlyingAIMover());
        if (this.isClient()) {
            this.trail = new Trail(this, this.getLevel(), new Color(15, 24, 35), 24.0f, 1000, 20.0f);
            this.trail.drawOnTop = true;
            this.trail.removeOnFadeOut = false;
            this.getLevel().entityManager.addTrail(this.trail);
        }
    }

    @Override
    public void tickMovement(float delta) {
        super.tickMovement(delta);
        if (this.trail != null) {
            this.trail.addPoint(new TrailVector(this.x, this.y, this.dx, this.dy, this.trail.thickness, 20.0f));
        }
    }

    @Override
    public void clientTick() {
        super.clientTick();
        this.particleTicks.gameTick();
        while (this.particleTicks.shouldTick()) {
            this.getLevel().entityManager.addParticle(this.x + GameRandom.globalRandom.floatGaussian() * 5.0f, this.y + GameRandom.globalRandom.floatGaussian() * 5.0f, this.particleTypes.next()).movesConstant(this.dx / 5.0f + GameRandom.globalRandom.floatGaussian() * 4.0f, this.dy / 5.0f + GameRandom.globalRandom.floatGaussian() * 4.0f).color(new Color(25, 41, 58)).height(20.0f).lifeTime(1000);
        }
    }

    @Override
    public LootTable getLootTable() {
        return lootTable;
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for (int i = 0; i < 15; ++i) {
            this.getLevel().entityManager.addParticle(this.x, this.y, this.particleTypes.next()).movesConstant((float)GameRandom.globalRandom.nextGaussian() * 10.0f, (float)GameRandom.globalRandom.nextGaussian() * 10.0f).height(20.0f).color(new Color(25, 41, 58)).lifeTime(500);
        }
    }

    @Override
    protected SoundSettings getHitDeathSound() {
        return new SoundSettings(GameResources.fadedeath3);
    }

    @Override
    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(PhantomMob.getTileCoordinate(x), PhantomMob.getTileCoordinate(y)).minLevelCopy(100.0f);
        int drawX = camera.getDrawX(x) - 16;
        int drawY = camera.getDrawY(y) - 40;
        int dir = this.getDir();
        Point sprite = this.getAnimSprite(x, y, dir);
        float bobbing = GameUtils.getBobbing(this.getWorldEntity().getTime(), 1000) * 5.0f;
        drawY = (int)((float)drawY + bobbing);
        final TextureDrawOptionsEnd drawOptions = MobRegistry.Textures.phantom.body.initDraw().sprite(sprite.x, sprite.y, 32).light(light).pos(drawX, drawY += level.getTile(PhantomMob.getTileCoordinate(x), PhantomMob.getTileCoordinate(y)).getMobSinkingAmount(this));
        topList.add(new MobDrawable(){

            @Override
            public void draw(TickManager tickManager) {
                drawOptions.draw();
            }
        });
        TextureDrawOptionsEnd shadow = MobRegistry.Textures.phantom.shadow.initDraw().sprite(sprite.x, sprite.y, 32).light(light).pos(camera.getDrawX(x) - 16, camera.getDrawY(y) - 16);
        topList.add(tm -> shadow.draw());
    }

    @Override
    public Point getAnimSprite(int x, int y, int dir) {
        return new Point(GameUtils.getAnim(this.getWorldEntity().getTime(), 6, 400), 0);
    }

    @Override
    public void dispose() {
        super.dispose();
        if (this.trail != null) {
            this.trail.removeOnFadeOut = true;
        }
    }
}

