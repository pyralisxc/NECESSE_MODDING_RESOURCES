/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.MaskShaderOptions;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.PlayerFollowerChaserAI;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.entity.mobs.itemAttacker.FollowPosition;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.AttackingFollowingMob;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.DryadSpiritFollowingMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.toolItem.summonToolItem.SummonToolItem;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class BabyDryadMob
extends AttackingFollowingMob {
    public BabyDryadMob() {
        super(10);
        this.setSpeed(65.0f);
        this.setFriction(3.0f);
        this.attackAnimTime = 200;
        this.attackCooldown = 1000;
        this.moveAccuracy = 10;
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
        this.ai = new BehaviourTreeAI<BabyDryadMob>(this, new PlayerFollowerChaserAI<BabyDryadMob>(576, 64, false, false, 640, 64){

            @Override
            public boolean attackTarget(BabyDryadMob mob, Mob target) {
                if (mob.canAttack()) {
                    mob.attack(target.getX(), target.getY(), false);
                    target.isServerHit(BabyDryadMob.this.summonDamage, mob.dx, mob.dy, 15.0f, mob);
                    Buff dryadHaunted = BuffRegistry.Debuffs.DRYAD_HAUNTED;
                    ActiveBuff ab = new ActiveBuff(dryadHaunted, target, 10000, (Attacker)BabyDryadMob.this.getAttackOwner());
                    target.buffManager.addBuff(ab, true);
                    if (target.buffManager.getStacks(dryadHaunted) >= 10) {
                        target.buffManager.removeBuff(dryadHaunted, true);
                        BabyDryadMob.spawnDryadSpirit(BabyDryadMob.this.getAttackOwner());
                    }
                    return true;
                }
                return false;
            }
        });
        if (this.isClient()) {
            GameRandom random = GameRandom.globalRandom;
            float anglePerParticle = 36.0f;
            for (int i = 0; i < 10; ++i) {
                int angle = (int)((float)i * anglePerParticle + random.nextFloat() * anglePerParticle);
                float dx = (float)Math.sin(Math.toRadians(angle)) * 20.0f;
                float dy = (float)Math.cos(Math.toRadians(angle)) * 20.0f;
                this.getLevel().entityManager.addParticle(this, Particle.GType.IMPORTANT_COSMETIC).sprite(GameResources.puffParticles.sprite(random.nextInt(5), 0, 12)).sizeFades(12, 24).movesFriction(dx * 2.0f, dy * 2.0f, 0.8f).color(new Color(30, 177, 143)).heightMoves(0.0f, 30.0f).lifeTime(1500);
            }
            SoundManager.playSound(GameResources.magicbolt4, (SoundEffect)SoundEffect.effect(this).volume(0.3f).pitch(GameRandom.globalRandom.getFloatBetween(1.4f, 1.5f)));
        }
    }

    public static void spawnDryadSpirit(Mob owner) {
        if (owner != null && owner.isServer()) {
            int maxSummons = 5;
            DryadSpiritFollowingMob summonedMob = (DryadSpiritFollowingMob)MobRegistry.getMob("dryadspirit", owner.getLevel());
            ((ItemAttackerMob)owner).serverFollowersManager.addFollower("summonedmobtemp", (Mob)summonedMob, FollowPosition.FLYING_CIRCLE_FAST, "summonedmob", 1.0f, p -> maxSummons, null, false);
            Point2D.Float spawnPoint = SummonToolItem.findSpawnLocation(summonedMob, owner.getLevel(), owner.x, owner.y);
            owner.getLevel().entityManager.addMob(summonedMob, spawnPoint.x, spawnPoint.y);
        }
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for (int i = 0; i < 4; ++i) {
            this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), MobRegistry.Textures.babyDryad.body, i, 8, 32, this.x, this.y, 20.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    @Override
    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(BabyDryadMob.getTileCoordinate(x), BabyDryadMob.getTileCoordinate(y));
        int drawX = camera.getDrawX(x) - 32;
        int drawY = camera.getDrawY(y) - 44 - 7;
        int dir = this.getDir();
        Point sprite = this.getAnimSprite(x, y, dir);
        drawY += this.getBobbing(x, y);
        MaskShaderOptions swimMask = this.getSwimMaskShaderOptions(this.inLiquidFloat(x, y));
        final TextureDrawOptionsEnd body = MobRegistry.Textures.babyDryad.body.initDraw().sprite(sprite.x, sprite.y, 64).addMaskShader(swimMask).light(light).pos(drawX, drawY += level.getTile(BabyDryadMob.getTileCoordinate(x), BabyDryadMob.getTileCoordinate(y)).getMobSinkingAmount(this));
        list.add(new MobDrawable(){

            @Override
            public void draw(TickManager tickManager) {
                body.draw();
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
            SoundManager.playSound(GameResources.swing1, (SoundEffect)SoundEffect.effect(this).volume(0.3f));
        }
    }
}

