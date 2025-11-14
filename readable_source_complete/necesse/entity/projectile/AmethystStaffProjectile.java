/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.projectile;

import java.awt.Color;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.Projectile;
import necesse.entity.trails.Trail;
import necesse.gfx.GameResources;
import necesse.gfx.ThemeColorRegistry;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameSprite;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;

public class AmethystStaffProjectile
extends Projectile {
    protected ParticleTypeSwitcher typeSwitcher = new ParticleTypeSwitcher(Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC, Particle.GType.CRITICAL);
    protected AtomicInteger givesLifeEssence = new AtomicInteger();

    public AmethystStaffProjectile() {
    }

    public AmethystStaffProjectile(Level level, Mob owner, float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback, AtomicInteger givesLifeEssence) {
        this.setLevel(level);
        this.setOwner(owner);
        this.x = x;
        this.y = y;
        this.setTarget(targetX, targetY);
        this.speed = speed;
        this.setDistance(distance);
        this.setDamage(damage);
        this.knockback = knockback;
        this.givesLifeEssence = givesLifeEssence;
    }

    @Override
    public void init() {
        super.init();
        this.particleSpeedMod = 0.03f;
        this.trailOffset = 0.0f;
        this.height = 8.0f;
    }

    @Override
    public void doHitLogic(Mob mob, LevelObjectHit object, float x, float y) {
        super.doHitLogic(mob, object, x, y);
        Mob owner = this.getOwner();
        if (this.isServer() && mob != null && owner != null && this.givesLifeEssence.get() > 0) {
            Float gainMod = owner.buffManager.getModifier(BuffModifiers.LIFE_ESSENCE_GAIN);
            Float durationMod = owner.buffManager.getModifier(BuffModifiers.LIFE_ESSENCE_DURATION);
            int i = 0;
            while (true) {
                double d = i;
                double d2 = Math.floor(gainMod.floatValue());
                boolean bl = GameRandom.globalRandom.getChance(gainMod.floatValue() % 1.0f);
                if (!(d < d2 + (double)bl)) break;
                owner.buffManager.addBuff(new ActiveBuff(BuffRegistry.LIFE_ESSENCE, owner, 60.0f * durationMod.floatValue(), null), true);
                ++i;
            }
            this.givesLifeEssence.decrementAndGet();
        }
        if (this.isClient()) {
            GameRandom clientRandom = GameRandom.globalRandom;
            for (int i = 0; i < 5; ++i) {
                this.getLevel().entityManager.addParticle(x + (float)clientRandom.getIntBetween(-10, 10), y + (float)clientRandom.getIntBetween(-10, 10), this.typeSwitcher.next()).sprite(GameResources.puffParticles.sprite(clientRandom.nextInt(4), 0, 12)).sizeFades(24, 48).height(18.0f).color(this.getWallHitColor());
            }
        }
    }

    @Override
    public void onMaxMoveTick() {
        if (this.isClient()) {
            this.spawnSpinningParticle();
        }
    }

    @Override
    public Trail getTrail() {
        Trail trail = new Trail(this, this.getLevel(), ThemeColorRegistry.AMETHYST.getRandomColor(), this.getTrailThickness(), 300, this.height);
        trail.sprite = new GameSprite(GameResources.chains, 7, 0, 32);
        return trail;
    }

    @Override
    public float getTrailThickness() {
        if (this.traveledDistance <= 50.0f) {
            return 0.0f;
        }
        return GameMath.lerp(GameMath.limit(this.traveledDistance / 150.0f, 0.0f, 1.0f), 2.0f, 24.0f);
    }

    @Override
    protected Color getWallHitColor() {
        return ThemeColorRegistry.AMETHYST.getRandomColor();
    }

    @Override
    public void refreshParticleLight() {
        this.getLevel().lightManager.refreshParticleLightFloat(this.x, this.y, this.getWallHitColor(), this.lightSaturation);
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
    }
}

