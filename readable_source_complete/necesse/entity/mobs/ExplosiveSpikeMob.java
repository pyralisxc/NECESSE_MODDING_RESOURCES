/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.List;
import necesse.engine.CameraShakeValues;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.SoundPlayer;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameMath;
import necesse.entity.levelEvent.explosionEvent.BoneSpikeMobExplosionLevelEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ability.IntMobAbility;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class ExplosiveSpikeMob
extends Mob {
    public long startCrackingTime;
    public long spawnTime;
    public long forceDespawnTime;
    protected GameDamage damage;
    public Mob mobOwner;
    public boolean isCracking;
    protected CameraShakeValues crackShake;
    public final IntMobAbility startCrackAbility;
    public final IntMobAbility despawnSpikeAbility;
    protected long crackAnimationStartTime;
    protected long crackAnimationExplodeTime;
    protected float overlayAlpha = 0.0f;
    protected SoundPlayer startCrackingSound;

    public ExplosiveSpikeMob() {
        super(Integer.MAX_VALUE);
        this.setArmor(0);
        this.setSpeed(0.0f);
        this.setFriction(1000.0f);
        this.setKnockbackModifier(0.0f);
        this.collision = new Rectangle(-10, -7, 20, 14);
        this.hitBox = new Rectangle(-18, -15, 36, 30);
        this.selectBox = new Rectangle(-14, -41, 28, 48);
        this.shouldSave = false;
        this.isStatic = true;
        this.isCracking = false;
        this.startCrackAbility = this.registerAbility(new IntMobAbility(){

            @Override
            protected void run(int value) {
                ExplosiveSpikeMob.this.crackAnimationStartTime = ExplosiveSpikeMob.this.getTime();
                ExplosiveSpikeMob.this.crackAnimationExplodeTime = ExplosiveSpikeMob.this.crackAnimationStartTime + (long)value;
                ExplosiveSpikeMob.this.startCrackingTime = 0L;
                ExplosiveSpikeMob.this.crackShake = new CameraShakeValues(value, 25, 0.1f, 0.1f, true);
                ExplosiveSpikeMob.this.isCracking = true;
                if (ExplosiveSpikeMob.this.isClient()) {
                    ExplosiveSpikeMob.this.startCrackingSound = ExplosiveSpikeMob.this.playStartCrackingSound();
                }
            }
        });
        this.despawnSpikeAbility = this.registerAbility(new IntMobAbility(){

            @Override
            protected void run(int value) {
                if (ExplosiveSpikeMob.this.forceDespawnTime == 0L) {
                    ExplosiveSpikeMob.this.forceDespawnTime = ExplosiveSpikeMob.this.getTime() + (long)value;
                }
            }
        });
    }

    public ExplosiveSpikeMob(Mob mobOwner, GameDamage damage, long startCrackingTime) {
        this();
        this.damage = damage;
        this.mobOwner = mobOwner;
        this.startCrackingTime = startCrackingTime;
        this.spawnTime = startCrackingTime - 9000L;
    }

    @Override
    public void serverTick() {
        super.serverTick();
        int checkInRange = 960;
        if (this.mobOwner.removed()) {
            this.despawnSpikeAbility.runAndSend(150);
        }
        if (this.getDistance(this.mobOwner.x, this.mobOwner.y) > (float)checkInRange) {
            this.despawnSpikeAbility.runAndSend(150);
        }
        if (this.isCracking && this.startCrackingTime != 0L && this.startCrackingTime <= this.getTime()) {
            this.startCrackAbility.runAndSend(1000);
        }
        if (this.crackAnimationExplodeTime != 0L && this.crackAnimationExplodeTime <= this.getTime() && this.damage != null) {
            this.spawnExplosionEvent();
            this.remove(0.0f, 0.0f, null, true);
        }
        if (this.forceDespawnTime != 0L && this.forceDespawnTime <= this.getTime() || this.spawnTime + 10000L <= this.getTime()) {
            this.remove();
        }
    }

    public void spawnExplosionEvent() {
        BoneSpikeMobExplosionLevelEvent event = new BoneSpikeMobExplosionLevelEvent(this.x, this.y, 125, this.damage.modFinalMultiplier(2.0f), false, 0.0f, this.mobOwner);
        this.getLevel().entityManager.events.add(event);
    }

    @Override
    public void clientTick() {
        super.clientTick();
        if (this.crackAnimationStartTime != 0L) {
            long totalAnimationTime = this.crackAnimationExplodeTime - this.crackAnimationStartTime;
            long timeUntilExplosion = this.getTime() - this.crackAnimationStartTime;
            this.overlayAlpha = GameMath.limit((float)timeUntilExplosion / (float)totalAnimationTime, 0.0f, 1.0f);
            this.getLevel().lightManager.refreshParticleLightFloat(this.x, this.y, 166.0f, 0.7f, (int)(100.0f * this.overlayAlpha));
        }
    }

    @Override
    public void playHurtSound() {
    }

    @Override
    public void playDeathSound() {
    }

    @Override
    public void playHitDeathSound() {
    }

    protected SoundPlayer playStartCrackingSound() {
        return SoundManager.playSound(new SoundSettings(GameResources.firespell1).volume(0.6f).basePitch(0.7f), this);
    }

    @Override
    public boolean canBePushed(Mob other) {
        return false;
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for (int i = 0; i < 6; ++i) {
            this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), MobRegistry.Textures.bigBoneSpike, i, 3, 32, this.x, this.y, 10.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    @Override
    protected void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameLight light = level.getLightLevel(ExplosiveSpikeMob.getTileCoordinate(x), ExplosiveSpikeMob.getTileCoordinate(y));
        GameTexture texture = MobRegistry.Textures.bigBoneSpike;
        Point2D.Float shake = new Point2D.Float();
        if (this.crackShake != null) {
            shake = this.crackShake.getCurrentShake(this.crackAnimationStartTime, this.getTime());
        }
        int drawX = camera.getDrawX((float)x + shake.x);
        int drawY = camera.getDrawY((float)y + shake.y);
        float endY = this.getEndY(texture) - 32.0f;
        final TextureDrawOptionsEnd boneSpikeOptions = texture.initDraw().section(0, texture.getWidth() / 2, 0, (int)endY).light(light).pos(drawX - texture.getWidth() / 4, drawY - (int)endY);
        final TextureDrawOptionsEnd spikeOverlayOptions = texture.initDraw().section(texture.getWidth() / 2, texture.getWidth() * 2, 0, (int)endY).colorLight(new Color(159, 222, 201), light.minLevelCopy(150.0f * this.overlayAlpha)).alpha(this.overlayAlpha).pos(drawX - texture.getWidth() / 4, drawY - (int)endY);
        list.add(new MobDrawable(){

            @Override
            public void draw(TickManager tickManager) {
                boneSpikeOptions.draw();
                spikeOverlayOptions.draw();
            }
        });
    }

    protected float getEndY(GameTexture texture) {
        float heightMultiplier = 1.0f;
        if (this.forceDespawnTime != 0L) {
            long progress = this.forceDespawnTime - this.getTime();
            heightMultiplier = (float)progress / 150.0f;
        } else if (!this.isCracking) {
            long progress = this.getTime() - this.spawnTime;
            if (progress <= 150L) {
                heightMultiplier = (float)progress / 150.0f;
            } else if (progress >= 9850L) {
                heightMultiplier = (float)(10000L - progress) / 150.0f;
            }
        }
        return heightMultiplier * (float)texture.getHeight();
    }

    public void forceDespawnSpike() {
        if (this.isServer()) {
            this.despawnSpikeAbility.runAndSend(150);
        }
    }

    @Override
    public boolean isHealthBarVisible() {
        return false;
    }

    @Override
    public boolean canTakeDamage() {
        return false;
    }

    @Override
    public boolean countDamageDealt() {
        return false;
    }

    @Override
    public boolean canPushMob(Mob other) {
        return false;
    }

    @Override
    public boolean canGiveResilience(Attacker attacker) {
        return false;
    }

    @Override
    public boolean onMouseHover(GameCamera camera, PlayerMob perspective, boolean debug) {
        if (!debug) {
            return false;
        }
        return super.onMouseHover(camera, perspective, debug);
    }

    @Override
    public void remove(float knockbackX, float knockbackY, Attacker attacker, boolean isDeath) {
        super.remove(knockbackX, knockbackY, attacker, isDeath);
        if (this.startCrackingSound != null) {
            this.startCrackingSound.stop();
        }
    }
}

