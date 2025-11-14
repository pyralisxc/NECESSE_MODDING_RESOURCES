/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.stream.Collectors;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.PlayerFollowerChaserAI;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.AttackingFollowingMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.entity.particle.WanderbotGlyphParticle;
import necesse.entity.projectile.AscendedBoltSoundProjectile;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class WanderbotFollowingMob
extends AttackingFollowingMob {
    protected int spriteRes = 128;
    protected int eggTime = 3000;
    protected long timeBetweenEggFrames = 50L;
    protected boolean isEgg = true;
    protected long timeAtSpawn;
    protected int eggFrame;
    protected int tileRange = 20;
    protected int shootingTickCounter = 0;
    protected boolean bulletHellChargedUp = false;
    protected final int bulletsBeforeReload = 25;
    protected int bulletsLeft = 0;
    List<Mob> currentTargets;
    protected boolean isCatchingUp = false;
    protected int teleportDistance = 3200;
    protected int startCatchingUpDistance = 640;
    protected int stopCatchingUpDistance = 256;
    protected int maxBulletsShotAtOnce = 3;

    public WanderbotFollowingMob() {
        super(1000);
        this.setSpeed(50.0f);
        this.setFriction(3.0f);
        this.moveAccuracy = 10;
        this.collision = new Rectangle(-15, -22, 30, 24);
        this.hitBox = new Rectangle(-20, -20, 40, 28);
        this.selectBox = new Rectangle(-26, -51, 52, 62);
        this.isEgg = true;
        this.eggFrame = 0;
    }

    @Override
    public void init() {
        super.init();
        this.ai = new BehaviourTreeAI<WanderbotFollowingMob>(this, new PlayerFollowerChaserAI<WanderbotFollowingMob>(640, 320, false, false, this.teleportDistance, 160){

            @Override
            public boolean attackTarget(WanderbotFollowingMob mob, Mob target) {
                return false;
            }
        });
        this.timeAtSpawn = this.getLocalTime();
        if (!this.isClient()) {
            return;
        }
        this.getLevel().entityManager.addParticle(new WanderbotGlyphParticle(this.getLevel(), this.x, this.y, this.eggTime + 1000), Particle.GType.CRITICAL);
        this.spawnSummonParticles();
    }

    protected void spawnSummonParticles() {
        for (int i = 0; i < 12; ++i) {
            float xDir = GameRandom.globalRandom.getFloatBetween(-1.0f, 1.0f);
            float xSpawnVariance = GameRandom.globalRandom.getFloatBetween(-25.0f, 25.0f);
            float ySpawnVariance = GameRandom.globalRandom.getFloatBetween(-25.0f, 25.0f);
            this.getLevel().entityManager.addParticle(this.x + xSpawnVariance, this.y - 32.0f + ySpawnVariance, Particle.GType.CRITICAL).sprite(GameResources.smokePuff.sprite(0, 0, 32)).movesConstant(xDir * 3.0f, 0.0f).color(GameRandom.globalRandom.getOneOfWeighted(Color.class, 5, new Color(113, 113, 113), 5, new Color(71, 71, 71), 4, new Color(158, 158, 158), 1, new Color(215, 108, 255))).heightMoves(10.0f, 60.0f).sizeFades(42, 68).fadesAlpha(0.0f, 0.6f).lifeTime(2000);
        }
    }

    @Override
    public void clientTick() {
        super.clientTick();
        this.globalTick();
    }

    @Override
    public void serverTick() {
        super.serverTick();
        this.globalTick();
    }

    protected void globalTick() {
        long nowTime = this.getLocalTime();
        long timer = nowTime - this.timeAtSpawn;
        long fullEggTime = (long)this.eggTime + ((long)this.eggFrame + 1L) * this.timeBetweenEggFrames;
        if (timer > (long)this.eggTime && timer - (long)this.eggFrame * this.timeBetweenEggFrames > fullEggTime) {
            ++this.eggFrame;
            if (this.eggFrame >= 4) {
                this.isEgg = false;
            }
        } else if (timer > fullEggTime) {
            ++this.shootingTickCounter;
            if (!this.bulletHellChargedUp) {
                if (this.isClient()) {
                    this.spawnTelegraphBulletHellParticles();
                }
                if (this.shootingTickCounter >= 20) {
                    this.bulletsLeft = 25;
                    this.bulletHellChargedUp = true;
                    this.currentTargets = this.streamTargets();
                }
            } else if (this.shootingTickCounter % 3 == 0) {
                if (this.currentTargets.isEmpty()) {
                    this.currentTargets = this.streamTargets();
                } else {
                    for (int i = 0; i < GameMath.min(this.currentTargets.size(), this.maxBulletsShotAtOnce); ++i) {
                        if (!this.isClient() && !this.currentTargets.get(i).removed()) {
                            this.fireNextProjectiles(this.currentTargets.get(i));
                        }
                        --this.bulletsLeft;
                        if (this.bulletsLeft > 0) continue;
                        this.bulletHellChargedUp = false;
                        this.shootingTickCounter = 0;
                    }
                }
            }
        }
        if (this.isFollowing()) {
            float dist = this.getDistance(this.getFollowingMob());
            if (dist > (float)this.startCatchingUpDistance) {
                this.isCatchingUp = true;
            } else if (dist < (float)this.stopCatchingUpDistance) {
                this.isCatchingUp = false;
            }
        }
    }

    private void fireNextProjectiles(Mob target) {
        if (!this.bulletHellChargedUp) {
            return;
        }
        AscendedBoltSoundProjectile projectile = new AscendedBoltSoundProjectile(this.getLevel(), this.x, this.y, 0.0f, 250.0f, this.tileRange * 32 * 2, this.summonDamage, this);
        projectile.setTargetPrediction(target, 0.0f);
        projectile.getUniqueID(GameRandom.globalRandom);
        this.getLevel().entityManager.projectiles.add(projectile);
    }

    protected List<Mob> streamTargets() {
        Mob focusTarget;
        List<Mob> list = this.getLevel().entityManager.streamAreaMobsAndPlayersTileRange(this.getX(), this.getY(), this.tileRange).filter(m -> m.isHostile && !m.isHuman).filter(m -> m.getDiagonalMoveDistance(this) < (float)(this.tileRange * 32)).collect(Collectors.toList());
        ItemAttackerMob followingAttacker = this.getFollowingItemAttacker();
        if (followingAttacker != null && (focusTarget = followingAttacker.getSummonFocusMob()) != null && focusTarget.getDiagonalMoveDistance(this) < (float)(this.tileRange * 32)) {
            list.add(focusTarget);
        }
        return list;
    }

    @Override
    public void setPos(float x, float y, boolean isDirect) {
        if (this.isInitialized() && this.isEgg) {
            return;
        }
        super.setPos(x, y, isDirect);
    }

    @Override
    protected void moveX(float mod) {
        if (this.isEgg) {
            return;
        }
        super.moveX(mod);
    }

    @Override
    protected void moveY(float mod) {
        if (this.isEgg) {
            return;
        }
        super.moveY(mod);
    }

    @Override
    public boolean canAttack() {
        if (this.isEgg) {
            return false;
        }
        return super.canAttack();
    }

    @Override
    public boolean canTakeDamage() {
        if (this.isEgg) {
            return false;
        }
        return super.canTakeDamage();
    }

    @Override
    public float getSpeed() {
        Mob followingMob = this.getFollowingMob();
        if (this.isFollowing() && this.isCatchingUp) {
            return followingMob.getSpeed() * 1.5f;
        }
        return super.getSpeed();
    }

    @Override
    public float getSpeedModifier() {
        if (this.isEgg) {
            return 0.0f;
        }
        if (this.isRobotFlying()) {
            return super.getSpeedModifier() * 2.0f;
        }
        return super.getSpeedModifier();
    }

    protected boolean isRobotFlying() {
        if (this.isFollowing()) {
            return this.isCatchingUp || this.inLiquidFloat() > 0.0f;
        }
        return this.inLiquidFloat() > 0.0f;
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for (int i = 0; i < 6; ++i) {
            this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), MobRegistry.Textures.wanderBot, i, 6, 64, this.x, this.y, 20.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    private void spawnTelegraphBulletHellParticles() {
        GameRandom random = GameRandom.globalRandom;
        int angle = random.nextInt(360);
        Point2D.Float dir = GameMath.getAngleDir(angle);
        float range = random.getFloatBetween(25.0f, 75.0f);
        float startX = this.x + dir.x * range;
        float startY = this.y - 16.0f;
        float endHeight = 0.0f;
        float startHeight = endHeight + dir.y * range;
        this.getLevel().entityManager.addTopParticle(startX, startY, Particle.GType.IMPORTANT_COSMETIC).sprite(GameResources.ascendedParticle.sprite(0, 0, 20)).sizeFades(40, 80).rotates().moves((pos, delta, particlelifeTime, timeAlive, lifePercent) -> {
            Point2D.Float direction = GameMath.normalize(this.x - pos.x, this.y - pos.y - 36.0f);
            pos.x += direction.x * 4.0f + this.dx * 0.02f;
            pos.y += direction.y * 1.5f + this.dy * 0.02f;
        }).heightMoves(startHeight, endHeight).fadesAlphaTime(100, 50).ignoreLight(true).lifeTime(random.getIntBetween(200, 300));
    }

    @Override
    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(WanderbotFollowingMob.getTileCoordinate(x), WanderbotFollowingMob.getTileCoordinate(y));
        int drawX = camera.getDrawX(x) - this.spriteRes / 2;
        int drawY = camera.getDrawY(y) - (int)((float)this.spriteRes * 0.75f);
        int dir = this.getDir();
        Point sprite = this.getAnimSprite(x, y, dir);
        final TextureDrawOptionsEnd options = MobRegistry.Textures.wanderBot.initDraw().sprite(sprite.x, sprite.y, this.spriteRes).light(light).mirror(dir == 3, false).pos(drawX, drawY);
        final TextureDrawOptionsEnd orbOptions = MobRegistry.Textures.wanderBot_front.initDraw().sprite(sprite.x, sprite.y, this.spriteRes).light(new GameLight(GameMath.max(light.getLevel(), 50.0f))).mirror(dir == 3, false).pos(drawX, drawY);
        list.add(new MobDrawable(){

            @Override
            public void draw(TickManager tickManager) {
                options.draw();
                orbOptions.draw();
            }
        });
        this.addShadowDrawables(tileList, level, x, y, light, camera);
    }

    @Override
    public Point getAnimSprite(int x, int y, int dir) {
        int spriteX;
        int spriteY;
        if (this.isEgg) {
            return new Point(this.eggFrame, 0);
        }
        if (this.isRobotFlying()) {
            spriteY = 2;
            spriteX = GameUtils.getAnim(this.getWorldEntity().getTime(), 4, 400);
        } else {
            spriteY = 1;
            spriteX = (int)(this.getDistanceRan() / (double)this.getRockSpeed()) % 4;
        }
        return new Point(spriteX, spriteY);
    }

    @Override
    protected TextureDrawOptions getShadowDrawOptions(Level level, int x, int y, GameLight light, GameCamera camera) {
        GameTexture shadowTexture = MobRegistry.Textures.wanderBot_shadow;
        int drawX = camera.getDrawX(x) - this.spriteRes / 2;
        int drawY = camera.getDrawY(y) - (int)((float)this.spriteRes * 0.75f);
        Point shadowSprite = this.getAnimSprite(x, y, 0);
        return shadowTexture.initDraw().sprite(shadowSprite.x, shadowSprite.y, this.spriteRes).light(light).mirror(this.getDir() == 3, false).pos(drawX, drawY);
    }

    @Override
    public void showAttack(int x, int y, int seed, boolean showAllDirections) {
        super.showAttack(x, y, seed, showAllDirections);
        if (this.isClient()) {
            SoundManager.playSound(GameResources.swing1, (SoundEffect)SoundEffect.effect(this).volume(0.3f));
        }
    }
}

