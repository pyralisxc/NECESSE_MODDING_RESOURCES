/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.projectile.bulletProjectile;

import java.awt.Color;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.mobAbilityLevelEvent.CrystallizeShatterEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffManager;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.entity.particle.Particle;
import necesse.entity.particle.ProjectileHitStuckParticle;
import necesse.entity.projectile.bulletProjectile.BulletProjectile;
import necesse.entity.trails.Trail;
import necesse.gfx.GameResources;
import necesse.gfx.ThemeColorRegistry;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameSprite;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;
import necesse.level.maps.light.GameLight;

public class CrystalBulletProjectile
extends BulletProjectile {
    protected int spriteX;

    public CrystalBulletProjectile() {
    }

    public CrystalBulletProjectile(float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback, Mob owner) {
        this();
        this.setLevel(owner.getLevel());
        this.x = x;
        this.y = y;
        this.setTarget(targetX, targetY);
        this.speed = speed;
        this.setDistance(distance);
        this.setDamage(damage);
        this.knockback = knockback;
        this.setOwner(owner);
        this.spriteX = GameRandom.globalRandom.getIntBetween(0, 2);
    }

    @Override
    public void init() {
        super.init();
        this.particleSpeedMod = 0.03f;
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextInt(this.spriteX);
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.spriteX = reader.getNextInt();
    }

    @Override
    public void doHitLogic(Mob mob, LevelObjectHit object, float x, float y) {
        BuffManager attackerBM;
        super.doHitLogic(mob, object, x, y);
        if (this.isServer() && mob != null && (attackerBM = this.getAttackOwner().buffManager) != null) {
            float thresholdMod = attackerBM.getModifier(BuffModifiers.CRIT_CHANCE).floatValue() + attackerBM.getModifier(BuffModifiers.MELEE_CRIT_CHANCE).floatValue();
            float crystallizeMod = attackerBM.getModifier(BuffModifiers.CRIT_DAMAGE).floatValue() + attackerBM.getModifier(BuffModifiers.MELEE_CRIT_CHANCE).floatValue();
            int stackThreshold = (int)GameMath.limit(10.0f - 7.0f * thresholdMod, 3.0f, 10.0f);
            float crystallizeDamageMultiplier = GameMath.limit(crystallizeMod, 2.0f, (float)stackThreshold);
            Buff crystallizeBuff = BuffRegistry.Debuffs.CRYSTALLIZE_BUFF;
            ActiveBuff ab = new ActiveBuff(crystallizeBuff, mob, 10000, (Attacker)this.getAttackOwner());
            mob.buffManager.addBuff(ab, true);
            ActiveBuff buff = mob.buffManager.getBuff(crystallizeBuff);
            if (buff != null && buff.getStacks() >= stackThreshold) {
                this.getLevel().entityManager.events.add(new CrystallizeShatterEvent(mob, CrystallizeShatterEvent.ParticleType.SAPPHIRE));
                mob.buffManager.removeBuff(crystallizeBuff, true);
                GameDamage finalDamage = this.getDamage().modDamage(crystallizeDamageMultiplier);
                mob.isServerHit(finalDamage, 0.0f, 0.0f, 0.0f, this);
            }
        }
        if (this.isClient() && this.bounced == this.getTotalBouncing()) {
            this.getLevel().entityManager.addParticle(new ProjectileHitStuckParticle(mob, this, x, y, GameRandom.globalRandom.getIntBetween(10, 20), 5000L){

                @Override
                public void addDrawables(Mob target, float x, float y, float angle, List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
                    int fadeTime;
                    GameLight light = level.getLightLevel(this);
                    int drawX = camera.getDrawX(x) - 2;
                    int drawY = camera.getDrawY(y - CrystalBulletProjectile.this.height) - 2;
                    float alpha = 1.0f;
                    long lifeCycleTime = this.getLifeCycleTime();
                    if (lifeCycleTime >= this.lifeTime - (long)(fadeTime = 1000)) {
                        alpha = Math.abs((float)(lifeCycleTime - (this.lifeTime - (long)fadeTime)) / (float)fadeTime - 1.0f);
                    }
                    final TextureDrawOptionsEnd options = CrystalBulletProjectile.this.texture.initDraw().sprite(CrystalBulletProjectile.this.spriteX, 0, 16, 32).light(light).rotate(CrystalBulletProjectile.this.getAngle(), 16, 0).pos(drawX, drawY).alpha(alpha);
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

    @Override
    public void onMaxMoveTick() {
        if (this.isClient()) {
            this.spawnSpinningParticle();
        }
    }

    @Override
    public Trail getTrail() {
        Trail trail = new Trail(this, this.getLevel(), ThemeColorRegistry.SAPPHIRE.getRandomColor(), 22.0f, 100, this.height);
        trail.sprite = new GameSprite(GameResources.chains, 7, 0, 32);
        return trail;
    }

    @Override
    protected Color getWallHitColor() {
        return ThemeColorRegistry.SAPPHIRE.getRandomColor();
    }

    @Override
    public void refreshParticleLight() {
        this.getLevel().lightManager.refreshParticleLightFloat(this.x, this.y, this.getWallHitColor(), this.lightSaturation);
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
        SoundManager.playSound(GameResources.crystalHit1, (SoundEffect)SoundEffect.effect(this).volume(2.0f).pitch(GameRandom.globalRandom.getFloatBetween(0.9f, 1.1f)));
    }
}

