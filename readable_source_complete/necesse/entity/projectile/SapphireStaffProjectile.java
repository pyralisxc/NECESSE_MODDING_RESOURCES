/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.projectile;

import java.awt.Color;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.TrainingDummyMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.projectile.Projectile;
import necesse.entity.trails.Trail;
import necesse.gfx.GameResources;
import necesse.gfx.ThemeColorRegistry;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;
import necesse.level.maps.light.GameLight;

public class SapphireStaffProjectile
extends Projectile {
    protected int spriteX;
    protected HashSet<Integer> ignoredTargets = new HashSet();
    protected int remainingRicochets;
    protected AtomicInteger givesLifeEssence = new AtomicInteger();

    public SapphireStaffProjectile() {
    }

    public SapphireStaffProjectile(Level level, Mob owner, float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback, int ricochets, AtomicInteger givesLifeEssence) {
        this.setLevel(level);
        this.setOwner(owner);
        this.x = x;
        this.y = y;
        this.setTarget(targetX, targetY);
        this.speed = speed;
        this.distance = distance;
        this.setDamage(damage);
        this.knockback = knockback;
        this.remainingRicochets = ricochets;
        this.givesLifeEssence = givesLifeEssence;
        this.spriteX = GameRandom.globalRandom.getIntBetween(0, 2);
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextShortUnsigned(this.ignoredTargets.size());
        for (Integer ignoredTarget : this.ignoredTargets) {
            writer.putNextInt(ignoredTarget);
        }
        writer.putNextShortUnsigned(this.remainingRicochets);
        this.spriteX = GameRandom.globalRandom.getIntBetween(0, 2);
        writer.putNextInt(this.spriteX);
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        int ignoredTargetsTotal = reader.getNextShortUnsigned();
        for (int i = 0; i < ignoredTargetsTotal; ++i) {
            this.ignoredTargets.add(reader.getNextInt());
        }
        this.remainingRicochets = reader.getNextShortUnsigned();
        this.spriteX = reader.getNextInt();
    }

    @Override
    public void init() {
        super.init();
        this.height = 6.0f;
        this.width = 6.0f;
        this.trailOffset = -5.0f;
    }

    @Override
    public Color getParticleColor() {
        return ThemeColorRegistry.SAPPHIRE.getRandomColor();
    }

    @Override
    public Trail getTrail() {
        return new Trail(this, this.getLevel(), ThemeColorRegistry.SAPPHIRE.getRandomColor(), this.getTrailThickness(), 800, this.getHeight());
    }

    @Override
    public float getTrailThickness() {
        if (!this.ignoredTargets.isEmpty()) {
            return 12.0f;
        }
        if (this.traveledDistance <= 50.0f) {
            return 0.0f;
        }
        return GameMath.lerp(GameMath.limit(this.traveledDistance / 200.0f, 0.0f, 1.0f), 0.0f, 12.0f);
    }

    @Override
    public boolean canHit(Mob mob) {
        if (this.ignoredTargets.contains(mob.getUniqueID())) {
            return false;
        }
        return super.canHit(mob);
    }

    @Override
    public void doHitLogic(Mob mob, LevelObjectHit object, float x, float y) {
        super.doHitLogic(mob, object, x, y);
        Mob owner = this.getOwner();
        if (mob != null && owner != null) {
            Mob ricochetTarget;
            if (this.isServer() && this.givesLifeEssence.get() > 0) {
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
            this.ignoredTargets.add(mob.getUniqueID());
            if (this.isServer() && this.remainingRicochets > 0 && (ricochetTarget = this.getValidRicochetTarget(mob)) != null) {
                --this.remainingRicochets;
                SapphireStaffProjectile projectile = new SapphireStaffProjectile(this.getLevel(), this.getOwner(), x, y, ricochetTarget.x, ricochetTarget.y, this.speed, 600, this.getDamage(), this.knockback, this.remainingRicochets, this.givesLifeEssence);
                if (this.modifier != null) {
                    this.modifier.initChildProjectile(projectile, 1.0f, 1);
                }
                projectile.setTargetPrediction(ricochetTarget);
                projectile.ignoredTargets.addAll(this.ignoredTargets);
                projectile.ignoredTargets.add(mob.getUniqueID());
                this.getLevel().entityManager.projectiles.add(projectile);
                this.setAngle(SapphireStaffProjectile.getAngleToTarget(x, y, ricochetTarget.x, ricochetTarget.y));
            }
        }
    }

    private Mob getValidRicochetTarget(Mob mob) {
        int checkForMobsRange = 160;
        Mob owner = this.getOwner();
        return GameUtils.streamTargets(this.getOwner(), GameUtils.rangeBounds(this.x, this.y, checkForMobsRange)).filter(m -> owner.isHostile || m.isHostile || m instanceof TrainingDummyMob).filter(m -> !this.ignoredTargets.contains(m.getUniqueID())).filter(m -> m.getDistance(mob) <= (float)checkForMobsRange).min(Comparator.comparing(m -> Float.valueOf(m.getDistance(this.x, this.y)))).orElse(null);
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        if (this.removed()) {
            return;
        }
        GameLight light = level.getLightLevel(this);
        int drawX = camera.getDrawX(this.x) - 8;
        int drawY = camera.getDrawY(this.y) - 18;
        final TextureDrawOptionsEnd options = this.texture.initDraw().sprite(this.spriteX, 0, 18, 32).light(light).rotate(this.getAngle(), 8, 18).pos(drawX, drawY - (int)this.getHeight());
        list.add(new EntityDrawable(this){

            @Override
            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
        this.addShadowDrawables(tileList, drawX, drawY, light, this.getAngle(), 18);
    }

    @Override
    public void playHitSound(float x, float y) {
        SoundManager.playSound(GameResources.crystalHit1, (SoundEffect)SoundEffect.effect(this).pitch(GameRandom.globalRandom.getFloatBetween(0.9f, 1.1f)));
    }
}

