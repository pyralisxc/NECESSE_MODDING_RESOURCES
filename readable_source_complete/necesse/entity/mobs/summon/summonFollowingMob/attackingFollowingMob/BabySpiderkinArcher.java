/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.ProjectileRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.MaskShaderOptions;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.PlayerFollowerChaserAI;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.AttackingFollowingMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.Projectile;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class BabySpiderkinArcher
extends AttackingFollowingMob {
    protected int shotsRemaining;

    public BabySpiderkinArcher() {
        super(10);
        this.setSpeed(55.0f);
        this.setFriction(3.0f);
        this.moveAccuracy = 10;
        this.attackAnimTime = 250;
        this.attackCooldown = 1000;
        this.collision = new Rectangle(-10, -7, 20, 14);
        this.hitBox = new Rectangle(-12, -14, 24, 24);
        this.selectBox = new Rectangle(-13, -30, 26, 40);
        this.swimMaskMove = 16;
        this.swimMaskOffset = 0;
        this.swimSinkOffset = -4;
    }

    @Override
    public void init() {
        super.init();
        this.ai = new BehaviourTreeAI<BabySpiderkinArcher>(this, new PlayerFollowerChaserAI<BabySpiderkinArcher>(576, 320, false, false, 640, 64){

            @Override
            public boolean attackTarget(BabySpiderkinArcher mob, Mob target) {
                if (mob.canAttack()) {
                    if (BabySpiderkinArcher.this.shotsRemaining <= 0) {
                        BabySpiderkinArcher.this.shotsRemaining = 3;
                    }
                    --BabySpiderkinArcher.this.shotsRemaining;
                    mob.attack(target.getX(), target.getY(), false);
                    Projectile projectile = ProjectileRegistry.getProjectile("spideritearrow", mob.getLevel(), mob.x, mob.y, target.x, target.y, 160.0f, 640, BabySpiderkinArcher.this.summonDamage, (Mob)mob);
                    projectile.setTargetPrediction(target, -20.0f);
                    projectile.moveDist(20.0);
                    mob.getLevel().entityManager.projectiles.add(projectile);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public boolean canAttack() {
        if (this.buffManager.getModifier(BuffModifiers.INTIMIDATED).booleanValue() || this.buffManager.getModifier(BuffModifiers.PARALYZED).booleanValue()) {
            return false;
        }
        return super.canAttack() || this.shotsRemaining > 0 && this.getTimeSinceLastAttack() >= 150L;
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for (int i = 0; i < 5; ++i) {
            this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), MobRegistry.Textures.babySpiderkinArcher.body, i, 8, 32, this.x, this.y, 20.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }
        this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), MobRegistry.Textures.babySpiderkinArcher.body, (int)GameRandom.globalRandom.getOneOf(0, 1), 9, 32, this.x, this.y, 20.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
    }

    @Override
    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(BabySpiderkinArcher.getTileCoordinate(x), BabySpiderkinArcher.getTileCoordinate(y));
        int drawX = camera.getDrawX(x) - 32;
        int drawY = camera.getDrawY(y) - 44 - 7;
        int dir = this.getDir();
        Point sprite = this.getAnimSprite(x, y, dir);
        drawY += this.getBobbing(x, y);
        drawY += level.getTile(BabySpiderkinArcher.getTileCoordinate(x), BabySpiderkinArcher.getTileCoordinate(y)).getMobSinkingAmount(this);
        float animProgress = this.getAttackAnimProgress();
        MaskShaderOptions swimMask = this.getSwimMaskShaderOptions(this.inLiquidFloat(x, y));
        HumanDrawOptions humanDrawOptions = new HumanDrawOptions(level, MobRegistry.Textures.babySpiderkinArcher).sprite(sprite).mask(swimMask).dir(dir).light(light);
        if (this.isAttacking) {
            ItemAttackDrawOptions attackOptions = ItemAttackDrawOptions.start(dir).itemSprite(MobRegistry.Textures.babySpiderkinArcher.body, 2, 9, 32).itemRotatePoint(20, 20).itemEnd().armSprite(MobRegistry.Textures.babySpiderkinArcher.body, 0, 8, 32).pointRotation(this.attackDir.x, this.attackDir.y).light(light);
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
    protected TextureDrawOptions getShadowDrawOptions(Level level, int x, int y, GameLight light, GameCamera camera) {
        GameTexture shadowTexture = MobRegistry.Textures.human_baby_shadow;
        int res = shadowTexture.getHeight();
        int drawX = camera.getDrawX(x) - res / 2;
        int drawY = camera.getDrawY(y) - res / 2;
        int dir = this.getDir();
        return shadowTexture.initDraw().sprite(dir, 0, res).light(light).pos(drawX, drawY += this.getBobbing(x, y));
    }

    @Override
    public void showAttack(int x, int y, int seed, boolean showAllDirections) {
        super.showAttack(x, y, seed, showAllDirections);
        if (this.isClient()) {
            SoundManager.playSound(GameResources.bow, (SoundEffect)SoundEffect.effect(this).volume(0.3f));
        }
    }
}

