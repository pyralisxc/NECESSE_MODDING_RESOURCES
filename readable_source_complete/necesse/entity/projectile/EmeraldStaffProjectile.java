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
import necesse.entity.mobs.Attacker;
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
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;

public class EmeraldStaffProjectile
extends Projectile {
    protected ParticleTypeSwitcher typeSwitcher = new ParticleTypeSwitcher(Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC, Particle.GType.CRITICAL);
    protected AtomicInteger givesLifeEssence = new AtomicInteger();

    public EmeraldStaffProjectile() {
    }

    public EmeraldStaffProjectile(Level level, Mob owner, float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, AtomicInteger givesLifeEssence) {
        this.setLevel(level);
        this.setOwner(owner);
        this.x = x;
        this.y = y;
        this.setTarget(targetX, targetY);
        this.speed = speed;
        this.distance = distance;
        this.setDamage(damage);
        this.knockback = 0;
        this.givesLifeEssence = givesLifeEssence;
    }

    @Override
    public void init() {
        super.init();
        this.height = -6.0f;
        this.piercing = 6;
        this.bouncing = 6;
        this.width = 40.0f;
    }

    @Override
    public Color getParticleColor() {
        return null;
    }

    @Override
    public Trail getTrail() {
        return null;
    }

    @Override
    public void clientTick() {
        super.clientTick();
        GameRandom rnd = GameRandom.globalRandom;
        Color c = ThemeColorRegistry.EMERALD.getRandomColor();
        float colorMod = rnd.getFloatBetween(0.7f, 1.0f);
        this.getLevel().entityManager.addParticle(this.x + (float)rnd.getIntBetween(-10, 10), this.y + (float)rnd.getIntBetween(-10, 10), this.typeSwitcher.next()).sprite(GameResources.puffParticles.sprite(rnd.nextInt(4), 0, 12)).sizeFades(24, 48).height(18.0f).fadesAlpha(0.2f, 0.2f).color(new Color((int)((float)c.getRed() * colorMod), (int)((float)c.getGreen() * colorMod), (int)((float)c.getBlue() * colorMod)));
    }

    @Override
    public void doHitLogic(Mob mob, LevelObjectHit object, float x, float y) {
        super.doHitLogic(mob, object, x, y);
        if (this.bounced < this.bouncing) {
            this.speed = GameMath.max(this.speed / 2.0f, 10.0f);
        }
        Mob owner = this.getOwner();
        if (mob != null && owner != null) {
            ActiveBuff ab = new ActiveBuff(BuffRegistry.Debuffs.EMERALD_POISON, mob, 5000, (Attacker)owner);
            mob.buffManager.addBuff(ab, this.isServer());
            if (this.isServer() && this.amountHit() == 0 && this.givesLifeEssence.get() > 0) {
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
        }
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
    }
}

