/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Shape;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.mobAbilityLevelEvent.GroundEffectEvent;
import necesse.entity.mobs.DeathMessageTable;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.particle.FallingIcicleParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.level.gameObject.FallenIcicleObject;
import necesse.level.maps.LevelObjectHit;

public class FallingIcicleEvent
extends GroundEffectEvent {
    protected Color particleColor = new Color(166, 230, 255);
    protected long spawnTime = -1L;
    protected long timeToFall;
    protected long delay;
    protected boolean spawnedHitParticles = false;
    protected boolean started = false;

    public FallingIcicleEvent() {
        this.allowNullOwner = true;
    }

    public FallingIcicleEvent(int x, int y, long fallTime, long delay) {
        super(null, x, y, GameRandom.globalRandom);
        this.timeToFall = fallTime;
        this.delay = delay;
        this.allowNullOwner = true;
    }

    @Override
    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        save.addInt("x", this.x);
        save.addInt("y", this.y);
        save.addLong("timeToFall", this.timeToFall);
        save.addLong("spawnTime", this.spawnTime);
        save.addLong("delay", this.delay);
    }

    @Override
    public void applyLoadData(LoadData save) {
        super.applyLoadData(save);
        this.x = save.getInt("x");
        this.y = save.getInt("y");
        this.timeToFall = save.getLong("timeToFall");
        this.spawnTime = save.getLong("spawnTime");
        this.delay = save.getLong("delay");
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.timeToFall = reader.getNextLong();
        this.spawnTime = reader.getNextLong();
        this.delay = reader.getNextLong();
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextLong(this.timeToFall);
        writer.putNextLong(this.spawnTime);
        writer.putNextLong(this.delay);
    }

    @Override
    public void init() {
        super.init();
        if (this.isClient()) {
            long relativeTime = this.getTime() - this.spawnTime - this.delay;
            if (relativeTime < 0L) {
                SoundManager.playSound(GameResources.icicleRumble, (SoundEffect)SoundEffect.effect(this.x, this.y + 96).minFalloffDistance(0).pitch(GameRandom.globalRandom.getFloatBetween(0.8f, 1.0f)));
            }
            this.getLevel().setObject(FallingIcicleEvent.getTileCoordinate(this.x), FallingIcicleEvent.getTileCoordinate(this.y), 0);
            this.level.entityManager.addParticle(new FallingIcicleParticle(this.level, (float)this.x, (float)this.y, relativeTime, this.timeToFall, FallenIcicleObject.getGeneratedSpriteIndex(FallingIcicleEvent.getTileCoordinate(this.x), FallingIcicleEvent.getTileCoordinate(this.y))), true, Particle.GType.CRITICAL);
        }
        if (!this.isClient() && this.spawnTime == -1L) {
            this.spawnTime = this.getTime();
        }
    }

    @Override
    public void clientTick() {
        if (this.isOver()) {
            return;
        }
        super.clientTick();
        long relativeTime = this.getTime() - this.spawnTime - this.delay;
        if (!this.started && relativeTime >= 0L) {
            this.started = true;
            SoundManager.playSound(GameResources.icicleFall, (SoundEffect)SoundEffect.effect(this.x, this.y + 96).minFalloffDistance(0).pitch(GameRandom.globalRandom.getFloatBetween(0.8f, 1.0f)));
        }
        if (relativeTime >= this.timeToFall && !this.spawnedHitParticles) {
            this.spawnedHitParticles = true;
            for (int i = 0; i < 7; ++i) {
                float offsetX = GameRandom.globalRandom.floatGaussian() * 4.0f - 2.0f;
                float offsetY = GameRandom.globalRandom.floatGaussian() * 2.0f - 1.0f;
                float strength = GameRandom.globalRandom.floatGaussian() + 0.75f;
                this.level.entityManager.addParticle((float)(this.x + 16) + offsetX, (float)(this.y + 27) + offsetY, Particle.GType.COSMETIC).lifeTime(GameRandom.globalRandom.getIntBetween(400, 600)).color(this.particleColor).movesFriction(15.0f * Math.signum(offsetX) * strength, 0.0f, 1.0f).sizeFades(0, 20).heightMoves(0.0f, 25.0f * strength, -50.0f, 0.0f, 0.0f, 0.5f);
            }
            SoundManager.playSound(GameResources.icicleImpact, (SoundEffect)SoundEffect.effect(this.x, this.y).minFalloffDistance(0).pitch(GameRandom.globalRandom.getFloatBetween(0.5f, 1.0f)));
        }
        if (relativeTime >= this.timeToFall + 200L) {
            this.over();
        }
    }

    @Override
    public void serverTick() {
        if (this.isOver()) {
            return;
        }
        super.serverTick();
        long relativeTime = this.getTime() - this.spawnTime - this.delay;
        if (!this.started && relativeTime >= 0L) {
            this.started = true;
            this.getLevel().sendObjectChangePacket(this.getServer(), FallingIcicleEvent.getTileCoordinate(this.x), FallingIcicleEvent.getTileCoordinate(this.y), 0);
        }
        if (relativeTime >= this.timeToFall + 200L) {
            this.level.sendObjectChangePacket(this.getServer(), FallingIcicleEvent.getTileCoordinate(this.x), FallingIcicleEvent.getTileCoordinate(this.y), ObjectRegistry.getObjectID("fallenicicle"));
            this.over();
        }
    }

    @Override
    public Shape getHitBox() {
        long relativeTime = this.getTime() - this.spawnTime - this.delay;
        if (relativeTime >= this.timeToFall) {
            return new Rectangle(this.x + 4, this.y + 4, 24, 24);
        }
        return null;
    }

    @Override
    public boolean canHit(Mob mob) {
        return !mob.isOnGenericCooldown("icicleHit");
    }

    @Override
    public void clientHit(Mob target) {
        target.startHitCooldown();
        target.startGenericCooldown("icicleHit", 1000L);
    }

    @Override
    public void serverHit(Mob target, boolean clientSubmitted) {
        if (clientSubmitted || this.canHit(target)) {
            float damage = Math.max((float)target.getMaxHealth() / 2.0f, 150.0f);
            target.isServerHit(new GameDamage(DamageTypeRegistry.TRUE, damage), 0.0f, 0.0f, 0.0f, this);
            target.startHitCooldown();
            target.startGenericCooldown("icicleHit", 1000L);
        }
    }

    @Override
    public void hitObject(LevelObjectHit hit) {
    }

    @Override
    public boolean isNetworkImportant() {
        return true;
    }

    @Override
    public GameMessage getAttackerName() {
        return new LocalMessage("deaths", "fallingiciclename");
    }

    @Override
    public DeathMessageTable getDeathMessages() {
        return this.getDeathMessages("fallingicicle", 3);
    }
}

