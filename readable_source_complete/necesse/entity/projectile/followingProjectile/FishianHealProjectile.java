/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.projectile.followingProjectile;

import java.awt.Color;
import java.awt.Shape;
import java.util.List;
import java.util.stream.Stream;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.mobAbilityLevelEvent.MobHealthChangeEvent;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.followingProjectile.FollowingProjectile;
import necesse.entity.trails.Trail;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;
import necesse.level.maps.light.GameLight;

public class FishianHealProjectile
extends FollowingProjectile {
    public FishianHealProjectile() {
    }

    public FishianHealProjectile(Level level, Mob owner, float x, float y, Mob target, GameDamage damage) {
        this.setLevel(level);
        this.x = x;
        this.y = y;
        this.speed = 150.0f;
        this.setTarget(target.x, target.y);
        this.target = target;
        this.setDamage(damage);
        this.knockback = 0;
        this.setDistance(2000);
        this.setOwner(owner);
    }

    @Override
    public void init() {
        super.init();
        this.turnSpeed = 0.75f;
        this.givesLight = true;
        this.height = 16.0f;
        this.piercing = 0;
        this.isSolid = false;
        this.particleDirOffset = -24.0f;
        this.trailOffset = 0.0f;
        this.setWidth(8.0f);
    }

    @Override
    public void serverTick() {
        super.serverTick();
        if (this.target == null) {
            this.remove();
        }
    }

    @Override
    public void clientTick() {
        super.clientTick();
        if (this.target == null) {
            this.remove();
        }
    }

    @Override
    protected int getExtraSpinningParticles() {
        return 5;
    }

    @Override
    public Color getParticleColor() {
        return new Color(32, 165, 22);
    }

    @Override
    public Trail getTrail() {
        return new Trail(this, this.getLevel(), new Color(32, 165, 22), 6.0f, 500, 18.0f);
    }

    @Override
    public boolean canHit(Mob mob) {
        return mob == this.target;
    }

    @Override
    protected Stream<Mob> streamTargets(Mob owner, Shape hitBounds) {
        if (this.target instanceof Mob) {
            return Stream.of((Mob)this.target);
        }
        return super.streamTargets(owner, hitBounds);
    }

    @Override
    public void applyDamage(Mob mob, float x, float y) {
        int totalHeal = (int)this.getDamage().damage;
        int finalHealth = mob.getHealth() + totalHeal;
        this.getLevel().entityManager.events.add(new MobHealthChangeEvent(mob, finalHealth, totalHeal));
    }

    @Override
    public void doHitLogic(Mob mob, LevelObjectHit object, float x, float y) {
        super.doHitLogic(mob, object, x, y);
        if (this.isClient() && mob != null) {
            for (int i = 0; i < 2; ++i) {
                this.getLevel().entityManager.addParticle(mob.x + (float)(GameRandom.globalRandom.nextGaussian() * 8.0), mob.y + (float)(GameRandom.globalRandom.nextGaussian() * 8.0), Particle.GType.CRITICAL).sprite(GameResources.healingParticles.sprite(0, 0, 20)).movesConstant(mob.dx / 10.0f, mob.dy / 10.0f).size((options, lifeTime, timeAlive, lifePercent) -> options.size(20 - (int)(20.0f * lifePercent), 20 - (int)(20.0f * lifePercent))).fadesAlpha(0.1f, 0.1f).dontRotate().lifeTime(500).heightMoves(10.0f, 34.0f);
            }
        }
    }

    @Override
    public float getTurnSpeed(int targetX, int targetY, float delta) {
        return this.getTurnSpeed(delta) * this.dynamicTurnSpeedMod(targetX, targetY, this.getTurnRadius());
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        if (this.removed()) {
            return;
        }
        GameLight light = level.getLightLevel(this);
        int drawX = camera.getDrawX(this.x) - this.texture.getWidth() / 2;
        int drawY = camera.getDrawY(this.y);
        final TextureDrawOptionsEnd options = this.texture.initDraw().light(light).rotate(this.getAngle(), this.texture.getWidth() / 2, 0).pos(drawX, drawY - (int)this.getHeight());
        list.add(new EntityDrawable(this){

            @Override
            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
    }
}

