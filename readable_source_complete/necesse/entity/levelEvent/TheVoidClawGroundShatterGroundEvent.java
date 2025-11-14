/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent;

import java.awt.Rectangle;
import java.awt.Shape;
import necesse.engine.CameraShake;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.postProcessing.PostProcessingEffects;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameRandom;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.levelEvent.mobAbilityLevelEvent.GroundEffectEvent;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobHitCooldowns;
import necesse.entity.mobs.hostile.bosses.theVoid.TheVoidMob;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.AscendedFractureProjectile;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.level.maps.LevelObjectHit;

public class TheVoidClawGroundShatterGroundEvent
extends GroundEffectEvent {
    protected ParticleTypeSwitcher particleTypeSwitcher = new ParticleTypeSwitcher(Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC, Particle.GType.CRITICAL);
    protected MobHitCooldowns hitCooldowns = new MobHitCooldowns(50);
    protected int tickCounter;
    private int lifetime = 750;
    private GameDamage damage;
    private int range;
    private int fractureRange;
    private long startTime;

    public TheVoidClawGroundShatterGroundEvent() {
    }

    public TheVoidClawGroundShatterGroundEvent(Mob owner, int x, int y, GameDamage damage, int range, int fractureRange, GameRandom uniqueIDRandom) {
        super(owner, x, y, uniqueIDRandom);
        this.damage = damage;
        this.range = range;
        this.fractureRange = fractureRange;
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.lifetime = reader.getNextInt();
        this.x = reader.getNextInt();
        this.y = reader.getNextInt();
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextInt(this.lifetime);
        writer.putNextInt(this.x);
        writer.putNextInt(this.y);
    }

    @Override
    public void init() {
        super.init();
        this.tickCounter = 0;
        this.startTime = this.getTime();
        if (this.isClient() && this.lifetime != 0) {
            CameraShake cameraShake = this.getClient().startCameraShake(this.x, (float)this.y, 200, 40, 2.0f, 2.0f, true);
            cameraShake.minDistance = 200;
            cameraShake.listenDistance = 2000;
            SoundManager.playSound(GameResources.crackdeath, (SoundEffect)SoundEffect.effect(this.x, this.y).volume(3.0f).pitch(0.3f).falloffDistance(2000));
            PostProcessingEffects.addShockwaveEffect(new PostProcessingEffects.AbstractShockwaveEffect(){

                @Override
                public int getDrawX(GameCamera camera) {
                    return camera.getDrawX(TheVoidClawGroundShatterGroundEvent.this.x);
                }

                @Override
                public int getDrawY(GameCamera camera) {
                    return camera.getDrawY(TheVoidClawGroundShatterGroundEvent.this.y);
                }

                @Override
                public float getCurrentDistance() {
                    return 400.0f * TheVoidClawGroundShatterGroundEvent.this.getLifePercentage();
                }

                @Override
                public float getSize() {
                    return 100.0f * (1.0f - TheVoidClawGroundShatterGroundEvent.this.getLifePercentage());
                }

                @Override
                public float getEasingScale() {
                    return 1.5f;
                }

                @Override
                public float getEasingPower() {
                    return 1.5f;
                }

                @Override
                public boolean shouldRemove() {
                    return TheVoidClawGroundShatterGroundEvent.this.isOver();
                }
            });
            GameRandom random = GameRandom.globalRandom;
            float anglePerParticle = 36.0f;
            for (int i = 0; i < 30; ++i) {
                int angle = (int)((float)i * anglePerParticle + random.nextFloat() * anglePerParticle);
                float dx = (float)Math.sin(Math.toRadians(angle)) * 20.0f;
                float dy = (float)Math.cos(Math.toRadians(angle)) * 20.0f;
                this.getLevel().entityManager.addParticle(this.x, this.y, Particle.GType.IMPORTANT_COSMETIC).sprite(GameResources.voidPuffParticles.sprite(random.nextInt(5), 0, 12)).sizeFades(24, 48).movesFriction(dx * 6.0f, dy * 6.0f, 0.8f).heightMoves(0.0f, 30.0f).lifeTime(1500);
            }
        } else {
            int projectileCount = 10;
            for (int i = 0; i < projectileCount; ++i) {
                int angle = i * (360 / projectileCount);
                AscendedFractureProjectile p = new AscendedFractureProjectile(this.getLevel(), this.x, this.y, angle, (float)this.fractureRange / 4.0f, this.fractureRange, this.damage, this.owner);
                p.getUniqueID(GameRandom.globalRandom);
                this.getLevel().entityManager.projectiles.add(p);
                if (!(this.owner instanceof TheVoidMob)) continue;
                ((TheVoidMob)this.owner).spawnedProjectiles.add(p);
            }
        }
    }

    private float getLifePercentage() {
        return (float)(this.getTime() - this.startTime) / (float)this.lifetime;
    }

    @Override
    public Shape getHitBox() {
        int width = this.range;
        int height = (int)((float)this.range * 0.75f);
        return new Rectangle(this.x - width / 2, this.y - height / 2, width, height);
    }

    @Override
    public void clientHit(Mob target) {
        target.startHitCooldown();
        this.hitCooldowns.startCooldown(target);
    }

    @Override
    public void serverHit(Mob target, boolean clientSubmitted) {
        if (clientSubmitted || this.hitCooldowns.canHit(target)) {
            GameDamage damage = new GameDamage((float)target.getHealth() / 100.0f, 1000.0f);
            target.isServerHit(damage, 0.0f, 0.0f, 0.0f, this.owner);
            this.hitCooldowns.startCooldown(target);
        }
    }

    @Override
    public void hitObject(LevelObjectHit hit) {
        hit.getLevelObject().attackThrough(TheVoidMob.collisionDamage, this.owner);
    }

    @Override
    public boolean canHit(Mob mob) {
        return super.canHit(mob) && this.hitCooldowns.canHit(mob);
    }

    @Override
    public void clientTick() {
        ++this.tickCounter;
        if (this.tickCounter > 20 * (this.lifetime / 1000)) {
            this.over();
        } else {
            super.clientTick();
        }
    }

    @Override
    public void serverTick() {
        ++this.tickCounter;
        if (this.tickCounter > 20 * (this.lifetime / 1000)) {
            this.over();
        } else {
            super.serverTick();
        }
    }
}

