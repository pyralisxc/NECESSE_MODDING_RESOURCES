/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent;

import java.awt.Point;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.MobRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.mobs.hostile.bosses.FallenWizardMob;
import necesse.entity.particle.Particle;

public class FallenWizardRespawnEvent
extends LevelEvent {
    public float posX;
    public float posY;
    public long spawnWorldTime;
    public double particleBuffer;

    public FallenWizardRespawnEvent() {
        super(true);
        this.shouldSave = true;
    }

    public FallenWizardRespawnEvent(float posX, float posY, long spawnWorldTime) {
        this();
        this.posX = posX;
        this.posY = posY;
        this.spawnWorldTime = spawnWorldTime;
    }

    @Override
    public boolean isNetworkImportant() {
        return true;
    }

    @Override
    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        save.addFloat("posX", this.posX);
        save.addFloat("posY", this.posY);
        save.addLong("spawnWorldTime", this.spawnWorldTime);
    }

    @Override
    public void applyLoadData(LoadData save) {
        super.applyLoadData(save);
        this.posX = save.getFloat("posX", Float.NEGATIVE_INFINITY);
        this.posY = save.getFloat("posY", Float.NEGATIVE_INFINITY);
        this.spawnWorldTime = save.getLong("spawnWorldTime", -1L);
        if (this.posX == Float.NEGATIVE_INFINITY || this.posY == Float.NEGATIVE_INFINITY || this.spawnWorldTime == -1L) {
            this.over();
        }
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextFloat(this.posX);
        writer.putNextFloat(this.posY);
        writer.putNextLong(this.spawnWorldTime);
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.posX = reader.getNextFloat();
        this.posY = reader.getNextFloat();
        this.spawnWorldTime = reader.getNextLong();
    }

    @Override
    public void clientTick() {
        super.clientTick();
        if (this.spawnWorldTime <= this.level.getWorldEntity().getWorldTime()) {
            this.over();
            return;
        }
        long timeToSpawn = this.spawnWorldTime - this.level.getWorldEntity().getWorldTime();
        long maxTimeAtMinParticles = 120000L;
        double maxParticlesPerSeconds = 20.0;
        double minParticlesPerSecond = 0.2f;
        if (timeToSpawn < maxTimeAtMinParticles) {
            double perc = (double)timeToSpawn / (double)maxTimeAtMinParticles;
            double percInv = Math.abs(perc - 1.0);
            percInv = Math.pow(percInv, 2.0);
            this.particleBuffer += 0.05 * (minParticlesPerSecond + (maxParticlesPerSeconds - minParticlesPerSecond) * percInv);
        } else {
            this.particleBuffer += 0.05 * minParticlesPerSecond;
        }
        while (this.particleBuffer > 0.0 && GameRandom.globalRandom.getChance(this.particleBuffer)) {
            this.particleBuffer -= 1.0;
            int lifeTime = GameRandom.globalRandom.getIntBetween(500, 1000);
            float lifePerc = (float)lifeTime / 1000.0f;
            float startHeight = 0.0f;
            float height = startHeight + (float)GameRandom.globalRandom.getIntBetween(40, 50) * lifePerc;
            this.level.entityManager.addParticle(this.posX + GameRandom.globalRandom.getFloatBetween(-10.0f, 10.0f), this.posY + GameRandom.globalRandom.getFloatBetween(-10.0f, 10.0f), Particle.GType.IMPORTANT_COSMETIC).sizeFades(10, 16).movesFriction(GameRandom.globalRandom.getFloatBetween(-10.0f, 10.0f), GameRandom.globalRandom.getFloatBetween(-10.0f, 10.0f), 0.5f).heightMoves(startHeight, height).givesLight(260.0f, 0.5f).colorRandom(270.0f, 0.8f, 0.5f, 10.0f, 0.1f, 0.1f).lifeTime(lifeTime);
        }
    }

    @Override
    public void serverTick() {
        super.serverTick();
        if (this.level.tickManager().getTick() == 1 && this.level.entityManager.mobs.stream().anyMatch(m -> m instanceof FallenWizardMob)) {
            this.over();
            return;
        }
        if (this.spawnWorldTime <= this.level.getWorldEntity().getWorldTime()) {
            if (this.level.entityManager.mobs.stream().noneMatch(m -> m instanceof FallenWizardMob)) {
                FallenWizardMob mob = (FallenWizardMob)MobRegistry.getMob("fallenwizard", this.level);
                mob.spawnParticles = true;
                this.level.entityManager.addMob(mob, this.posX, this.posY);
            }
            this.over();
        }
    }

    @Override
    public Point getSaveToRegionPos() {
        return new Point(this.level.regionManager.getRegionCoordByTile(GameMath.getTileCoordinate(this.posX)), this.level.regionManager.getRegionCoordByTile(GameMath.getTileCoordinate(this.posY)));
    }
}

