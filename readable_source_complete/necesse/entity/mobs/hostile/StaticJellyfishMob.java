/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.TileRegistry;
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
import necesse.entity.mobs.ai.behaviourTree.trees.ConfusedPlayerChaserWandererAI;
import necesse.entity.mobs.hostile.FlyingHostileMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.pathProjectile.StaticJellyfishProjectile;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.IncursionLevel;
import necesse.level.maps.Level;
import necesse.level.maps.TilePosition;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.light.GameLight;

public class StaticJellyfishMob
extends FlyingHostileMob {
    public static GameDamage baseDamage = new GameDamage(80.0f);
    public static GameDamage incursionDamage = new GameDamage(90.0f);
    public ParticleTypeSwitcher particleTypes = new ParticleTypeSwitcher(Particle.GType.COSMETIC, Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC);
    public final EmptyMobAbility playBoltSoundAbility;

    public StaticJellyfishMob() {
        super(500);
        this.setSpeed(70.0f);
        this.setFriction(0.5f);
        this.setKnockbackModifier(0.0f);
        this.setArmor(15);
        this.moveAccuracy = 20;
        this.attackCooldown = 4000;
        this.collision = new Rectangle(-10, -7, 20, 14);
        this.hitBox = new Rectangle(-16, -16, 32, 32);
        this.selectBox = new Rectangle(-16, -40, 32, 32);
        this.playBoltSoundAbility = this.registerAbility(new EmptyMobAbility(){

            @Override
            protected void run() {
                if (StaticJellyfishMob.this.isClient()) {
                    SoundManager.playSound(GameResources.magicbolt2, SoundEffect.globalEffect().volume(0.5f).pitch(1.1f));
                }
            }
        });
    }

    @Override
    public void init() {
        GameDamage damage;
        super.init();
        if (this.getLevel() instanceof IncursionLevel) {
            this.setMaxHealth(600);
            this.setHealthHidden(this.getMaxHealth());
            this.setArmor(20);
            damage = incursionDamage;
        } else {
            damage = baseDamage;
        }
        ConfusedPlayerChaserWandererAI<StaticJellyfishMob> aiNode = new ConfusedPlayerChaserWandererAI<StaticJellyfishMob>(null, 512, 320, 40000, false, false){

            @Override
            protected int getRandomConfuseTime() {
                return GameRandom.globalRandom.getIntBetween(2000, 3000);
            }

            @Override
            public boolean attackTarget(StaticJellyfishMob mob, Mob target) {
                if (StaticJellyfishMob.this.canAttack() && !StaticJellyfishMob.this.isOnGenericCooldown("attackCooldown")) {
                    mob.attack(target.getX(), target.getY(), false);
                    StaticJellyfishProjectile projectile = new StaticJellyfishProjectile(mob.x, mob.y, target.x, target.y, 70.0f, 35.0f, 80.0f, damage, mob);
                    mob.getLevel().entityManager.projectiles.add(projectile);
                    StaticJellyfishMob.this.startGenericCooldown("attackCooldown", StaticJellyfishMob.this.attackCooldown);
                    this.wanderAfterAttack = true;
                }
                return true;
            }
        };
        aiNode.playerChaserAI.chaserAINode.minimumAttackDistance = 128;
        this.ai = new BehaviourTreeAI<StaticJellyfishMob>(this, aiNode);
    }

    @Override
    public void clientTick() {
        super.clientTick();
        if (GameRandom.globalRandom.getChance(0.2f)) {
            float particleX = this.x + GameRandom.globalRandom.floatGaussian() * 10.0f;
            float particleY = this.y + GameRandom.globalRandom.floatGaussian() * 10.0f;
            float moveX = GameRandom.globalRandom.floatGaussian() * 10.0f;
            float moveY = GameRandom.globalRandom.floatGaussian() * 10.0f;
            this.getLevel().entityManager.addParticle(particleX, particleY, this.particleTypes.next()).sprite(GameResources.magicSparkParticles.sprite(GameRandom.globalRandom.nextInt(4), 0, 22)).sizeFades(10, 20).movesFriction(moveX, moveY, 0.8f).color(new Color(95, 205, 228)).givesLight(190.0f, 0.9f).height(24.0f).ignoreLight(true).lifeTime(500);
        }
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for (int i = 0; i < 4; ++i) {
            this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), MobRegistry.Textures.staticJellyfish.body, GameRandom.globalRandom.nextInt(4), 3, 32, this.x, this.y, 20.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    @Override
    protected SoundSettings getHitDeathSound() {
        return new SoundSettings(GameResources.waterblob);
    }

    @Override
    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(StaticJellyfishMob.getTileCoordinate(x), StaticJellyfishMob.getTileCoordinate(y)).minLevelCopy(100.0f);
        int drawX = camera.getDrawX(x) - 32;
        int drawY = camera.getDrawY(y) - 60;
        int dir = this.getDir();
        Point sprite = this.getAnimSprite(x, y, dir);
        drawY += (int)(GameUtils.getBobbing(this.getTime(), 1000) * 5.0f);
        final TextureDrawOptionsEnd drawOptions = MobRegistry.Textures.staticJellyfish.body.initDraw().sprite(sprite.x, sprite.y, 64).light(light).pos(drawX, drawY += level.getTile(StaticJellyfishMob.getTileCoordinate(x), StaticJellyfishMob.getTileCoordinate(y)).getMobSinkingAmount(this));
        list.add(new MobDrawable(){

            @Override
            public void draw(TickManager tickManager) {
                drawOptions.draw();
            }
        });
        TextureDrawOptionsEnd shadow = MobRegistry.Textures.small_shadow.initDraw().light(light).posMiddle(camera.getDrawX(x), camera.getDrawY(y));
        tileList.add(tm -> shadow.draw());
    }

    @Override
    public Point getAnimSprite(int x, int y, int dir) {
        return new Point(GameUtils.getAnim(this.getTime(), 4, 400), 0);
    }

    @Override
    public int getTileWanderPriority(TilePosition pos, Biome baseBiome) {
        if (pos.tileID() == TileRegistry.puddleCobble) {
            return 1000;
        }
        return super.getTileWanderPriority(pos, baseBiome);
    }
}

