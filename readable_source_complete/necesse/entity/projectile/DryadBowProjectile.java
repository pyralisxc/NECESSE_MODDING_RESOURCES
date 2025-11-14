/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.projectile;

import java.awt.geom.Point2D;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.entity.mobs.itemAttacker.FollowPosition;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.DryadSpiritFollowingMob;
import necesse.entity.particle.Particle;
import necesse.entity.particle.ParticleOption;
import necesse.entity.particle.ProjectileHitStuckParticle;
import necesse.entity.projectile.Projectile;
import necesse.entity.trails.Trail;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.item.toolItem.summonToolItem.SummonToolItem;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;
import necesse.level.maps.light.GameLight;

public class DryadBowProjectile
extends Projectile {
    private int dryadHauntedStacksOnHit = 0;

    public DryadBowProjectile() {
    }

    public DryadBowProjectile(Mob owner, float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int dryadHauntedStacksOnHit, int knockback) {
        this.setOwner(owner);
        this.x = x;
        this.y = y;
        this.setTarget(targetX, targetY);
        this.setDamage(damage);
        this.dryadHauntedStacksOnHit = dryadHauntedStacksOnHit;
        this.speed = speed;
        this.setDistance(distance);
        this.knockback = knockback;
    }

    @Override
    public void init() {
        super.init();
        this.height = 18.0f;
        this.trailOffset = -6.0f;
    }

    @Override
    protected int getExtraSpinningParticles() {
        return super.getExtraSpinningParticles() + 2;
    }

    @Override
    protected void modifySpinningParticle(ParticleOption particle) {
        particle.lifeTime(1000);
    }

    @Override
    public Trail getTrail() {
        return new Trail(this, this.getLevel(), this.getParticleColor(), 12.0f, 200, this.getHeight());
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        if (this.removed()) {
            return;
        }
        GameLight light = level.getLightLevel(this);
        int drawX = camera.getDrawX(this.x) - this.texture.getWidth() / 2;
        int drawY = camera.getDrawY(this.y) - this.texture.getHeight() / 2;
        final TextureDrawOptionsEnd options = this.texture.initDraw().light(light).rotate(this.getAngle() + 45.0f, this.texture.getWidth() / 2, this.texture.getHeight() / 2).pos(drawX, drawY - (int)this.getHeight());
        list.add(new EntityDrawable(this){

            @Override
            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
        this.addShadowDrawables(tileList, drawX, drawY, light, this.getAngle() + 45.0f, 0);
    }

    @Override
    public void doHitLogic(Mob mob, LevelObjectHit object, float x, float y) {
        super.doHitLogic(mob, object, x, y);
        if (mob != null) {
            Buff dryadHaunted = BuffRegistry.Debuffs.DRYAD_HAUNTED;
            ActiveBuff ab = new ActiveBuff(dryadHaunted, mob, 10000, (Attacker)this.getAttackOwner());
            ab.setStacks(this.dryadHauntedStacksOnHit, 10000, this.getAttackOwner());
            mob.buffManager.addBuff(ab, this.isServer());
            if (mob.buffManager.getStacks(dryadHaunted) >= 10) {
                mob.buffManager.removeBuff(dryadHaunted, this.isServer());
                DryadBowProjectile.spawnDryadSpirit(this.getAttackOwner());
            }
        }
        if (this.isClient() && this.bounced == this.getTotalBouncing()) {
            this.getLevel().entityManager.addParticle(new ProjectileHitStuckParticle(mob, this, x, y, GameRandom.globalRandom.getIntBetween(10, 20), 1000L){

                @Override
                public void addDrawables(Mob target, float x, float y, float angle, List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
                    int fadeTime;
                    GameLight light = level.getLightLevel(this);
                    int drawX = camera.getDrawX(x) - 2;
                    int drawY = camera.getDrawY(y - DryadBowProjectile.this.height) - 2;
                    float alpha = 1.0f;
                    long lifeCycleTime = this.getLifeCycleTime();
                    if (lifeCycleTime >= this.lifeTime - (long)(fadeTime = 200)) {
                        alpha = Math.abs((float)(lifeCycleTime - (this.lifeTime - (long)fadeTime)) / (float)fadeTime - 1.0f);
                    }
                    int cut = target == null ? 8 : 0;
                    final TextureDrawOptionsEnd options = DryadBowProjectile.this.texture.initDraw().light(light).rotate(DryadBowProjectile.this.getAngle() + 45.0f, 2, 2).alpha(alpha).pos(drawX, drawY);
                    EntityDrawable drawable = new EntityDrawable(this){

                        @Override
                        public void draw(TickManager tickManager) {
                            options.draw();
                        }
                    };
                    if (target != null) {
                        topList.add(drawable);
                    } else {
                        list.add(drawable);
                    }
                }
            }, Particle.GType.IMPORTANT_COSMETIC);
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
    protected SoundSettings getSpawnSound() {
        return new SoundSettings(GameResources.wind1).volume(0.1f).basePitch(1.3f).pitchVariance(0.1f);
    }
}

