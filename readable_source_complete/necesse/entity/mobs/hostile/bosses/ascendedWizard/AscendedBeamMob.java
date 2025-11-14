/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile.bosses.ascendedWizard;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ProjectileRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.SoundPlayer;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.mobs.AscendedPylonDummyMob;
import necesse.entity.mobs.DeathMessageTable;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.LevelMob;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.hostile.FlyingHostileMob;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class AscendedBeamMob
extends FlyingHostileMob {
    public final LevelMob<Mob> master = new LevelMob();
    public GameDamage damage = AscendedPylonDummyMob.ASCENDED_BEAM_DAMAGE;
    public int remainingLifeTime = 15000;
    protected long spawnTime;
    private SoundPlayer sound;
    private float particleBuffer;
    protected ParticleTypeSwitcher particleTypeSwitcher = new ParticleTypeSwitcher(Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC, Particle.GType.CRITICAL);

    public AscendedBeamMob() {
        super(100);
        this.moveAccuracy = 5;
        this.setSpeed(100.0f);
        this.setFriction(1.0f);
        this.setKnockbackModifier(0.0f);
        this.collision = new Rectangle(-25, -30, 50, 50);
        this.hitBox = new Rectangle(-30, -35, 60, 60);
        this.selectBox = new Rectangle(-20, -40, 40, 60);
        this.isSummoned = true;
        this.isStatic = true;
        this.shouldSave = false;
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextInt(this.remainingLifeTime);
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.remainingLifeTime = reader.getNextInt();
    }

    @Override
    public void init() {
        super.init();
        this.spawnTime = this.getTime();
    }

    @Override
    public void clientTick() {
        super.clientTick();
        if (this.sound == null || this.sound.isDone()) {
            this.sound = SoundManager.playSound(GameResources.laserBeam1, (SoundEffect)SoundEffect.effect(this).falloffDistance(600).volume(0.0f));
            if (this.sound != null) {
                this.sound.fadeIn(1.0f);
                this.sound.effect.volume(0.2f);
            }
        }
        if (this.sound != null) {
            this.sound.refreshLooping(1.0f);
        }
        this.remainingLifeTime -= 50;
        if (this.remainingLifeTime >= 500) {
            this.getLevel().entityManager.addParticle(this, (float)GameRandom.globalRandom.getIntBetween(-20, 20), 0.0f, Particle.GType.COSMETIC).sprite(GameResources.ascendedParticle.sprite(0, 0, 20)).ignoreLight(true).sizeFades(20, 40).movesConstant(GameRandom.globalRandom.getIntBetween(-20, 20), GameRandom.globalRandom.getIntBetween(-20, -40)).height(16.0f).lifeTime(500);
            float particlesPerSecond = 80.0f;
            this.particleBuffer += particlesPerSecond / 20.0f;
            while (this.particleBuffer >= 1.0f) {
                this.particleBuffer -= 1.0f;
                GameRandom random = GameRandom.globalRandom;
                AtomicReference<Float> currentAngle = new AtomicReference<Float>(Float.valueOf(random.nextFloat() * 360.0f));
                float distance = 75.0f;
                this.getLevel().entityManager.addParticle(this.x + GameMath.sin(currentAngle.get().floatValue()) * distance + (float)random.getIntBetween(-5, 5), this.y + GameMath.cos(currentAngle.get().floatValue()) * distance + (float)random.getIntBetween(-5, 5) * 0.85f, this.particleTypeSwitcher.next()).sprite(GameResources.puffParticles.sprite(random.getIntBetween(0, 4), 0, 12)).height(16.0f).moves((pos, delta, lifeTime, timeAlive, lifePercent) -> {
                    float angle = currentAngle.accumulateAndGet(Float.valueOf(delta * 150.0f / 250.0f), Float::sum).floatValue();
                    float distY = distance * lifePercent * 0.85f;
                    pos.x = this.x + GameMath.sin(angle) * (distance * lifePercent);
                    pos.y = this.y + GameMath.cos(angle) * distY * 0.85f;
                }).color((options, lifeTime, timeAlive, lifePercent) -> {
                    float clampedLifePercent = Math.max(0.0f, Math.min(1.0f, lifePercent));
                    options.color(new Color(255, (int)(255.0f - 255.0f * clampedLifePercent), (int)(255.0f - 24.0f * clampedLifePercent)));
                }).ignoreLight(true).lifeTime(1000).sizeFades(50, 24);
            }
        }
    }

    @Override
    public void serverTick() {
        super.serverTick();
        this.remainingLifeTime -= 50;
        if (this.remainingLifeTime <= 0) {
            this.remove(0.0f, 0.0f, null, true);
        }
    }

    @Override
    public boolean canBePushed(Mob other) {
        return false;
    }

    @Override
    public GameDamage getCollisionDamage(Mob target, boolean fromPacket, ServerClient packetSubmitter) {
        return AscendedPylonDummyMob.ASCENDED_BEAM_DAMAGE;
    }

    @Override
    public int getCollisionKnockback(Mob target) {
        return 150;
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
    }

    @Override
    protected SoundSettings getHitDeathSound() {
        return new SoundSettings(GameResources.magicbolt2).volume(0.8f).pitchVariance(0.1f);
    }

    @Override
    protected void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        long timeSinceSpawned;
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        int projectileID = ProjectileRegistry.getProjectileID("ascendedbeam");
        GameTexture texture = ProjectileRegistry.Textures.getTexture(projectileID);
        GameTexture shadowTexture = ProjectileRegistry.Textures.getShadowTexture(projectileID);
        GameLight light = level.getLightLevel(AscendedBeamMob.getTileCoordinate(x), AscendedBeamMob.getTileCoordinate(y));
        int drawX = camera.getDrawX(x) - texture.getWidth() / 2 + 16;
        int drawY = camera.getDrawY(y) - texture.getHeight();
        int anim = GameUtils.getAnim(this.getWorldEntity().getTime(), 2, 200);
        float alpha = 1.0f;
        if (this.remainingLifeTime < 500) {
            alpha *= (float)this.remainingLifeTime / 500.0f;
        }
        if ((timeSinceSpawned = this.getTime() - this.spawnTime) < 500L) {
            alpha *= (float)timeSinceSpawned / 500.0f;
        }
        final TextureDrawOptionsEnd options = texture.initDraw().sprite(anim, 0, 32, 640).light(light.minLevelCopy(150.0f)).alpha(alpha).pos(drawX, drawY);
        list.add(new MobDrawable(){

            @Override
            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
        TextureDrawOptionsEnd shadowOptions = shadowTexture.initDraw().light(light.minLevelCopy(150.0f)).pos(drawX - shadowTexture.getWidth() / 2 + 16, drawY + texture.getHeight() - shadowTexture.getHeight() / 2 - 16);
        tileList.add(tm -> shadowOptions.draw());
    }

    @Override
    public Mob getAttackOwner() {
        Mob master = this.master.get(this.getLevel());
        if (master != null) {
            return master;
        }
        return super.getAttackOwner();
    }

    @Override
    public GameMessage getAttackerName() {
        Mob master = this.master.get(this.getLevel());
        if (master != null) {
            return master.getAttackerName();
        }
        return super.getAttackerName();
    }

    @Override
    public DeathMessageTable getDeathMessages() {
        Mob master = this.master.get(this.getLevel());
        if (master != null) {
            return master.getDeathMessages();
        }
        return super.getDeathMessages();
    }
}

