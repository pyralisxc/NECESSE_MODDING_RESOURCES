/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile.bosses;

import java.awt.Rectangle;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.SoundPlayer;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.hostile.FlyingHostileMob;
import necesse.entity.mobs.hostile.bosses.TheCursedCroneMob;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class SpiritTornadoMob
extends FlyingHostileMob {
    public int remainingLifeTime = 15000;
    protected long spawnTime;
    private SoundPlayer sound;
    public ParticleTypeSwitcher particleTypeSwitcher = new ParticleTypeSwitcher(Particle.GType.COSMETIC, Particle.GType.IMPORTANT_COSMETIC);
    public float particleBuffer;

    public SpiritTornadoMob() {
        super(100);
        this.moveAccuracy = 5;
        this.setSpeed(60.0f);
        this.setFriction(1.0f);
        this.setKnockbackModifier(0.0f);
        this.collision = new Rectangle(-15, -15, 30, 30);
        this.hitBox = new Rectangle(-20, -20, 40, 40);
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
            this.sound = SoundManager.playSound(GameResources.wind1, (SoundEffect)SoundEffect.effect(this).falloffDistance(500).volume(0.2f));
        }
        if (this.sound != null) {
            this.sound.refreshLooping(1.0f);
        }
        this.remainingLifeTime -= 50;
        if (this.remainingLifeTime >= 2000) {
            int particlesPerSecond = 10;
            this.particleBuffer += (float)(50 * particlesPerSecond) / 1000.0f;
            while (this.particleBuffer >= 1.0f) {
                this.particleBuffer -= 1.0f;
                int minHeight = 0;
                int maxHeight = 40;
                float height = GameMath.lerp(GameRandom.globalRandom.nextFloat(), minHeight, maxHeight);
                AtomicReference<Float> currentAngle = new AtomicReference<Float>(Float.valueOf(GameRandom.globalRandom.nextFloat() * 360.0f));
                float distance = 20.0f;
                this.getLevel().entityManager.addParticle(this.x + GameMath.sin(currentAngle.get().floatValue()) * distance, this.y + GameMath.cos(currentAngle.get().floatValue()) * distance * 0.75f, this.particleTypeSwitcher.next()).colorRandom(158.0f, 0.7f, 0.5f, 10.0f, 0.1f, 0.1f).heightMoves(height, height + 20.0f).moves((pos, delta, lifeTime, timeAlive, lifePercent) -> {
                    float angle = currentAngle.accumulateAndGet(Float.valueOf(delta * 150.0f / 250.0f), Float::sum).floatValue();
                    float distanceProgress = GameMath.lerp((float)Math.pow(lifePercent, 2.0), distance, distance + 60.0f);
                    float distY = distanceProgress * 0.75f;
                    pos.x = this.x + GameMath.sin(angle) * distanceProgress;
                    pos.y = this.y + GameMath.cos(angle) * distY * 0.75f;
                }).lifeTime(3000).sizeFades(12, 16);
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
        return TheCursedCroneMob.tornadoCollisionDamage;
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
        return new SoundSettings(GameResources.magicbolt2).volume(0.8f).basePitch(0.9f).pitchVariance(0.1f);
    }

    @Override
    protected void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        long timeSinceSpawned;
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(SpiritTornadoMob.getTileCoordinate(x), SpiritTornadoMob.getTileCoordinate(y));
        float alpha = 1.0f;
        if (this.remainingLifeTime < 1000) {
            alpha *= (float)this.remainingLifeTime / 1000.0f;
        }
        if ((timeSinceSpawned = this.getTime() - this.spawnTime) < 1000L) {
            alpha *= (float)timeSinceSpawned / 1000.0f;
        }
        int drawX = camera.getDrawX(x) - 64;
        int drawY = camera.getDrawY(y) - 96 - 32 - 16 - 8;
        int sprite = GameUtils.getAnim(Math.abs(this.getTime() + (long)this.getUniqueID()), 4, 500);
        final TextureDrawOptionsEnd shadow = MobRegistry.Textures.theCursedCrone_shadow.initDraw().sprite(sprite, 2, 128, 192).alpha(alpha).light(light).pos(drawX, drawY);
        final TextureDrawOptionsEnd backEffects = MobRegistry.Textures.theCursedCroneBackEffects.initDraw().sprite(sprite, 2, 128, 192).alpha(alpha).light(light).pos(drawX, drawY);
        final TextureDrawOptionsEnd frontEffects = MobRegistry.Textures.theCursedCroneFrontEffects.initDraw().sprite(sprite, 2, 128, 192).alpha(alpha).light(light).pos(drawX, drawY);
        list.add(new MobDrawable(){

            @Override
            public void draw(TickManager tickManager) {
                shadow.draw();
                backEffects.draw();
                frontEffects.draw();
            }
        });
    }

    @Override
    public Stream<ModifierValue<?>> getDefaultModifiers() {
        return Stream.of(new ModifierValue<Float>(BuffModifiers.SLOW, Float.valueOf(0.0f)));
    }
}

