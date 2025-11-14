/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.incursionModifiers;

import java.util.List;
import java.util.stream.Collectors;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.levelEvent.WaitForSecondsEvent;
import necesse.entity.levelEvent.actions.CoordinateLevelEventAction;
import necesse.entity.levelEvent.explosionEvent.StormingModifierExplosionLevelEvent;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.particle.Particle;
import necesse.entity.particle.StormingChargeUpParticle;
import necesse.entity.projectile.StormingIncursionModifierProjectile;
import necesse.level.maps.levelBuffManager.LevelModifiers;

public class StormingModifierLevelEvent
extends LevelEvent {
    public long stormingSpawnInterval;
    public long stormingSpawnTimeOffset;
    public long chargeUpTime;
    public boolean stormIncoming = false;
    final int lightningStrikeCounter = 5;
    final int stormCounter = 3;
    private final CoordinateLevelEventAction spawnChargeUpParticle;

    public StormingModifierLevelEvent() {
        super(true);
        this.shouldSave = true;
        this.spawnChargeUpParticle = this.registerAction(new CoordinateLevelEventAction(){

            @Override
            protected void run(int x, int y) {
                StormingModifierLevelEvent.this.chargeUpStorm(x, y);
            }
        });
    }

    public StormingModifierLevelEvent(long stormingSpawnInterval, long stormingSpawnTimeOffset, long chargeUpTime) {
        this();
        this.stormingSpawnInterval = stormingSpawnInterval;
        this.stormingSpawnTimeOffset = stormingSpawnTimeOffset;
        this.chargeUpTime = chargeUpTime;
    }

    @Override
    public boolean isNetworkImportant() {
        return true;
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextLong(this.stormingSpawnInterval);
        writer.putNextLong(this.stormingSpawnTimeOffset);
        writer.putNextLong(this.chargeUpTime);
        writer.putNextBoolean(this.stormIncoming);
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.stormingSpawnInterval = reader.getNextLong();
        this.stormingSpawnTimeOffset = reader.getNextLong();
        this.chargeUpTime = reader.getNextLong();
        this.stormIncoming = reader.getNextBoolean();
    }

    @Override
    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        save.addLong("stormingSpawnInterval", this.stormingSpawnInterval);
        save.addLong("stormingSpawnTimeOffset", this.stormingSpawnTimeOffset);
        save.addLong("chargeUpTime", this.chargeUpTime);
        save.addBoolean("stormIncoming", this.stormIncoming);
    }

    @Override
    public void applyLoadData(LoadData save) {
        super.applyLoadData(save);
        this.stormingSpawnInterval = save.getLong("stormingSpawnInterval", this.stormingSpawnInterval, false);
        this.stormingSpawnTimeOffset = save.getLong("stormingSpawnTimeOffset", this.stormingSpawnTimeOffset, false);
        this.chargeUpTime = save.getLong("chargeUpTime", this.chargeUpTime, false);
        this.stormIncoming = save.getBoolean("stormIncoming", this.stormIncoming, false);
    }

    @Override
    public void serverTick() {
        super.serverTick();
        long currentProgress = this.getCurrentTimeInProgress();
        if (currentProgress <= this.stormingSpawnInterval) {
            this.stormIncoming = false;
        } else if (!this.stormIncoming) {
            this.stormIncoming = true;
            GameRandom random = new GameRandom();
            int prevXPos = Integer.MAX_VALUE;
            int prevYPos = Integer.MAX_VALUE;
            for (int i = 0; i < 3; ++i) {
                int yPos;
                int xPos;
                List players = GameUtils.streamNetworkClients(this.level).map(c -> c.playerMob).collect(Collectors.toList());
                if (players.isEmpty()) {
                    return;
                }
                Mob playerChosen = (Mob)random.getOneOf(players);
                if (prevXPos == Integer.MAX_VALUE) {
                    xPos = GameRandom.globalRandom.getIntOffset((int)(playerChosen.x + playerChosen.dx * 3.0f), 50);
                    yPos = GameRandom.globalRandom.getIntOffset((int)(playerChosen.y + playerChosen.dy * 3.0f), 50);
                } else {
                    xPos = prevXPos + random.getIntBetween(-200, 200);
                    yPos = prevYPos + random.getIntBetween(-200, 200);
                }
                prevXPos = xPos;
                prevYPos = yPos;
                this.level.entityManager.events.addHidden(new WaitForSecondsEvent(0.5f * (float)i){

                    @Override
                    public void onWaitOver() {
                        StormingModifierLevelEvent.this.spawnChargeUpParticle.runAndSend(xPos, yPos);
                    }
                });
            }
        }
    }

    public long getCurrentTimeInProgress() {
        long fullTime = this.getFullStormingEventTime();
        return Math.floorMod(this.getTime() - this.stormingSpawnTimeOffset, fullTime);
    }

    public long getFullStormingEventTime() {
        return this.stormingSpawnInterval + this.chargeUpTime;
    }

    public void chargeUpStorm(final int xPos, final int yPos) {
        if (this.isClient()) {
            StormingChargeUpParticle particle = new StormingChargeUpParticle(this.level, xPos, yPos, 3000L);
            this.level.entityManager.addParticle(particle, Particle.GType.CRITICAL);
        }
        this.level.entityManager.events.addHidden(new WaitForSecondsEvent(2.0f){

            @Override
            public void onWaitOver() {
                StormingModifierLevelEvent.this.fireStorm(xPos, yPos);
            }
        });
    }

    public void fireStorm(final int xPos, final int yPos) {
        float timeBetweeningLightningStrikes = 0.05f;
        for (int i = 0; i < 5; ++i) {
            this.level.entityManager.events.addHidden(new WaitForSecondsEvent(timeBetweeningLightningStrikes * (float)i){

                @Override
                public void onWaitOver() {
                    if (this.isClient()) {
                        StormingIncursionModifierProjectile stormingProjectile = new StormingIncursionModifierProjectile(this.level, null, xPos, yPos - 800, xPos, yPos, 450.0f, 800);
                        stormingProjectile.resetUniqueID(new GameRandom(this.getUniqueID()).nextSeeded(67));
                        this.getLevel().entityManager.projectiles.addHidden(stormingProjectile);
                    }
                }
            });
        }
        this.level.entityManager.events.addHidden(new WaitForSecondsEvent(0.2f + 5.0f * timeBetweeningLightningStrikes){

            @Override
            public void onWaitOver() {
                GameDamage explosionDamage = new GameDamage(125.0f * this.getLevel().buffManager.getModifier(LevelModifiers.ENEMY_DAMAGE).floatValue());
                StormingModifierExplosionLevelEvent explosionLevelEvent = new StormingModifierExplosionLevelEvent(xPos, yPos, 65, explosionDamage, false, 0.0f, null);
                this.level.entityManager.events.addHidden(explosionLevelEvent);
            }
        });
        this.stormIncoming = false;
    }
}

