/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.incursionModifiers;

import java.util.stream.Stream;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.SoundPlayer;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.gfx.GameResources;
import necesse.level.maps.levelBuffManager.LevelModifiers;
import necesse.level.maps.levelBuffManager.MobBuffsEntityComponent;

public class TremorsModifierLevelEvent
extends LevelEvent
implements MobBuffsEntityComponent {
    private long tremorTimeOffset;
    private long tremorInterval;
    private long tremorDuration;
    private SoundPlayer tremorSound;

    public TremorsModifierLevelEvent() {
        super(true);
        this.shouldSave = true;
    }

    public TremorsModifierLevelEvent(long currentTime, long tremorInterval, long tremorDuration) {
        this();
        this.tremorTimeOffset = currentTime;
        this.tremorInterval = tremorInterval;
        this.tremorDuration = tremorDuration;
    }

    @Override
    public boolean isNetworkImportant() {
        return true;
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextLong(this.tremorInterval);
        writer.putNextLong(this.tremorDuration);
        writer.putNextLong(this.tremorTimeOffset);
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.tremorInterval = reader.getNextLong();
        this.tremorDuration = reader.getNextLong();
        this.tremorTimeOffset = reader.getNextLong();
    }

    @Override
    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        save.addLong("tremorInterval", this.tremorInterval);
        save.addLong("tremorDuration", this.tremorDuration);
        save.addLong("tremorTimeOffset", this.tremorTimeOffset);
    }

    @Override
    public void applyLoadData(LoadData save) {
        super.applyLoadData(save);
        this.tremorInterval = save.getLong("tremorInterval", this.tremorInterval, false);
        this.tremorDuration = save.getLong("tremorDuration", this.tremorDuration, false);
        this.tremorTimeOffset = save.getLong("tremorTimeOffset", this.tremorTimeOffset, false);
    }

    @Override
    public void clientTick() {
        PlayerMob player;
        super.clientTick();
        int timeToTremorEnd = this.getTimeToTremorEnd();
        if (timeToTremorEnd > 0) {
            if (this.tremorSound == null || this.tremorSound.isDone()) {
                this.tremorSound = SoundManager.playSound(GameResources.rumble, SoundEffect.globalEffect());
            }
            if (this.tremorSound != null && this.tremorSound.isPlaying()) {
                this.tremorSound.refreshLooping(0.5f);
            }
        }
        if (this.isClient() && (player = this.getClient().getPlayer()) != null && player.isSamePlace(this.getLevel())) {
            if (timeToTremorEnd > 0) {
                if (!player.buffManager.hasBuff(BuffRegistry.TREMOR_HAPPENING)) {
                    this.getClient().startCameraShake(player, timeToTremorEnd, 60, 3.0f, 3.0f, false);
                }
                ActiveBuff ab = new ActiveBuff(BuffRegistry.TREMOR_HAPPENING, (Mob)player, 0.5f, null);
                ab.getGndData().setInt("levelEventUniqueID", this.getUniqueID());
                player.buffManager.addBuff(ab, false);
                player.buffManager.removeBuff(BuffRegistry.TREMORS_INCOMING, true);
            } else {
                ActiveBuff ab = new ActiveBuff(BuffRegistry.TREMORS_INCOMING, (Mob)player, 0.5f, null);
                ab.getGndData().setInt("levelEventUniqueID", this.getUniqueID());
                player.buffManager.addBuff(ab, false);
                player.buffManager.removeBuff(BuffRegistry.TREMOR_HAPPENING, true);
            }
        }
    }

    public int getTimeToNextTremor() {
        long currentProgress = this.getCurrentTimeInProgress();
        if (currentProgress <= this.tremorInterval) {
            return (int)(this.tremorInterval - currentProgress);
        }
        return 0;
    }

    public int getTimeToTremorEnd() {
        long currentProgress = this.getCurrentTimeInProgress();
        if (currentProgress > this.tremorInterval) {
            long currentTremorProgress = currentProgress - this.tremorInterval;
            return (int)(this.tremorDuration - currentTremorProgress);
        }
        return 0;
    }

    public long getCurrentTimeInProgress() {
        long fullTime = this.getFullTremorDurationRunTime();
        return Math.floorMod(this.getTime() - this.tremorTimeOffset, fullTime);
    }

    public long getFullTremorDurationRunTime() {
        return this.tremorInterval + this.tremorDuration;
    }

    @Override
    public Stream<ModifierValue<?>> getLevelModifiers(Mob mob) {
        if (this.getTimeToTremorEnd() > 0) {
            boolean affectsMob = mob.isPlayer;
            if (!affectsMob && this.getLevel().buffManager.getModifier(LevelModifiers.MODIFIERS_AFFECT_ENEMIES).booleanValue()) {
                affectsMob = mob.isHostile;
            }
            if (affectsMob) {
                return Stream.of(new ModifierValue<Float>(BuffModifiers.SLOW, Float.valueOf(0.5f)));
            }
        }
        return Stream.empty();
    }

    @Override
    public void over() {
        super.over();
        if (this.level.isServer()) {
            this.level.getServer().streamClients().filter(client -> client.isSamePlace(this.level) && client.hasSpawned()).forEach(serverClient -> {
                if (serverClient.playerMob.buffManager.hasBuff(BuffRegistry.TREMORS_INCOMING)) {
                    serverClient.playerMob.buffManager.removeBuff(BuffRegistry.TREMORS_INCOMING, true);
                }
                if (serverClient.playerMob.buffManager.hasBuff(BuffRegistry.TREMOR_HAPPENING)) {
                    serverClient.playerMob.buffManager.removeBuff(BuffRegistry.TREMOR_HAPPENING, true);
                }
            });
        }
    }
}

