/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.projectile;

import java.awt.Color;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.TrainingDummyMob;
import necesse.entity.particle.ParticleOption;
import necesse.entity.projectile.Projectile;
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

public class GlacialBowProjectile
extends Projectile {
    private final HashSet<Integer> ignoredTargets = new HashSet();
    private int shatterNumber = 2;

    public GlacialBowProjectile() {
    }

    public GlacialBowProjectile(Mob owner, float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback) {
        this.setOwner(owner);
        this.x = x;
        this.y = y;
        this.setTarget(targetX, targetY);
        this.setDamage(damage);
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
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextShortUnsigned(this.ignoredTargets.size());
        for (Integer ignoredTarget : this.ignoredTargets) {
            writer.putNextInt(ignoredTarget);
        }
        writer.putNextShortUnsigned(this.shatterNumber);
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        int ignoredTargetsTotal = reader.getNextShortUnsigned();
        for (int i = 0; i < ignoredTargetsTotal; ++i) {
            this.ignoredTargets.add(reader.getNextInt());
        }
        this.shatterNumber = reader.getNextShortUnsigned();
    }

    @Override
    public Color getParticleColor() {
        return new Color(109, 137, 222);
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
        int drawY = camera.getDrawY(this.y);
        final TextureDrawOptionsEnd options = this.texture.initDraw().light(light).rotate(this.getAngle(), this.texture.getWidth() / 2, 0).pos(drawX, drawY - (int)this.getHeight());
        list.add(new EntityDrawable(this){

            @Override
            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
        this.addShadowDrawables(tileList, drawX, drawY, light, this.getAngle(), 0);
    }

    @Override
    public void doHitLogic(Mob mob, LevelObjectHit object, float x, float y) {
        Mob target;
        super.doHitLogic(mob, object, x, y);
        if (mob != null && this.isServer() && this.shatterNumber > 0 && (target = (Mob)GameUtils.streamTargets(this.getOwner(), GameUtils.rangeBounds(x, y, 350)).filter(m -> (m.isHostile || m instanceof TrainingDummyMob) && m != mob && !this.ignoredTargets.contains(m.getUniqueID())).filter(m -> m.getDistance(x, y) < 350.0f).min(Comparator.comparingInt(m -> (int)m.getDistance(x, y))).orElse(null)) != null) {
            --this.shatterNumber;
            GlacialBowProjectile projectile = new GlacialBowProjectile(this.getOwner(), x, y, target.x, target.y, this.speed, 600, this.getDamage(), this.knockback);
            if (this.modifier != null) {
                this.modifier.initChildProjectile(projectile, 1.0f, 1);
            }
            projectile.setTargetPrediction(target);
            projectile.ignoredTargets.addAll(this.ignoredTargets);
            projectile.ignoredTargets.add(mob.getUniqueID());
            projectile.shatterNumber = this.shatterNumber;
            this.getLevel().entityManager.projectiles.add(projectile);
        }
    }

    @Override
    public boolean canHit(Mob mob) {
        if (this.ignoredTargets.contains(mob.getUniqueID())) {
            return false;
        }
        return super.canHit(mob);
    }

    @Override
    protected void playHitSound(float x, float y) {
        SoundManager.playSound(GameResources.jinglehit, (SoundEffect)SoundEffect.effect(x, y));
    }

    @Override
    protected SoundSettings getSpawnSound() {
        return new SoundSettings(GameResources.fizz).volume(0.1f).basePitch(1.3f).pitchVariance(0.1f);
    }
}

