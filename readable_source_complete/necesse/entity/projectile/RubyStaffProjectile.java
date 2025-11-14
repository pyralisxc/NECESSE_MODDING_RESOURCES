/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.projectile;

import java.awt.Color;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.levelEvent.explosionEvent.RubyStaffExplosionEvent;
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

public class RubyStaffProjectile
extends Projectile {
    protected float startSpeed;
    protected int explosionRange;
    private final ParticleTypeSwitcher typeSwitcher = new ParticleTypeSwitcher(Particle.GType.CRITICAL, Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC);
    protected AtomicInteger givesLifeEssence = new AtomicInteger();

    public RubyStaffProjectile() {
    }

    public RubyStaffProjectile(Level level, Mob owner, float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback, int explosionRange, AtomicInteger givesLifeEssence) {
        this.setLevel(level);
        this.setOwner(owner);
        this.x = x;
        this.y = y;
        this.setTarget(targetX, targetY);
        this.speed = speed;
        this.startSpeed = speed;
        this.distance = distance;
        this.setDamage(damage);
        this.knockback = knockback;
        this.explosionRange = explosionRange;
        this.givesLifeEssence = givesLifeEssence;
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextFloat(this.startSpeed);
        writer.putNextShortUnsigned(this.explosionRange);
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.startSpeed = reader.getNextFloat();
        this.explosionRange = reader.getNextShortUnsigned();
    }

    @Override
    public void init() {
        super.init();
        this.height = 6.0f;
        this.piercing = 0;
        this.width = 32.0f;
    }

    @Override
    public void clientTick() {
        super.clientTick();
        this.getLevel().entityManager.addParticle(this.x + GameRandom.globalRandom.getFloatBetween(-10.0f, 10.0f), this.y + GameRandom.globalRandom.getFloatBetween(-10.0f, 10.0f), this.typeSwitcher.next()).sprite(GameResources.magicSparkParticles.sprite(GameRandom.globalRandom.nextInt(4), 0, 22)).color(this.getParticleColor()).height(6.0f).sizeFades(22, 44).fadesAlpha(0.1f, 0.1f);
    }

    @Override
    public Color getParticleColor() {
        return ThemeColorRegistry.RUBY.getRandomColor();
    }

    @Override
    public Trail getTrail() {
        return new Trail(this, this.getLevel(), ThemeColorRegistry.RUBY.getRandomColor(), this.getTrailThickness(), 600, this.getHeight());
    }

    @Override
    public float getTrailThickness() {
        if (this.traveledDistance <= 50.0f) {
            return 0.0f;
        }
        return GameMath.lerp(GameMath.limit(this.traveledDistance / 300.0f, 0.0f, 1.0f), 2.0f, 12.0f);
    }

    @Override
    public float tickMovement(float delta) {
        float distanceProgress = GameMath.limit(this.traveledDistance / (float)this.distance, 0.0f, 1.0f);
        this.speed = GameMath.lerp((float)Math.pow(distanceProgress, 0.3f), this.startSpeed, 30.0f);
        return super.tickMovement(delta);
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
        if (this.isServer()) {
            this.triggerExplosion(mob);
        }
    }

    public void triggerExplosion(Mob target) {
        GameDamage explosionDamage = this.getDamage().modDamage(2.0f);
        RubyStaffExplosionEvent event = new RubyStaffExplosionEvent(this.x, this.y, this.explosionRange, explosionDamage, false, 0.0f, this.getOwner(), this.givesLifeEssence, target);
        this.getLevel().entityManager.events.add(event);
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
    }
}

