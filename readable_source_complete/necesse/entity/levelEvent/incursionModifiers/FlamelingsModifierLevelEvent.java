/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.incursionModifiers;

import java.awt.Point;
import java.util.List;
import java.util.stream.Collectors;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.MobRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.manager.MobSpawnArea;
import necesse.entity.mobs.LevelMob;
import necesse.entity.mobs.Mob;

public class FlamelingsModifierLevelEvent
extends LevelEvent {
    public long flamelingSpawnInterval;
    public long flamelingSpawnOffset;
    public long flamelingUptime;
    public final LevelMob<Mob> flameling = new LevelMob(-1);
    public static MobSpawnArea SPAWN_AREA = new MobSpawnArea(128, 256);

    public FlamelingsModifierLevelEvent() {
        super(true);
        this.shouldSave = true;
    }

    public FlamelingsModifierLevelEvent(long flamelingSpawnInterval, long flamelingSpawnOffset, long flamelingUptime) {
        this();
        this.flamelingSpawnInterval = flamelingSpawnInterval;
        this.flamelingSpawnOffset = flamelingSpawnOffset;
        this.flamelingUptime = flamelingUptime;
    }

    @Override
    public boolean isNetworkImportant() {
        return true;
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextLong(this.flamelingSpawnInterval);
        writer.putNextLong(this.flamelingSpawnOffset);
        writer.putNextLong(this.flamelingUptime);
        writer.putNextInt(this.flameling.uniqueID);
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.flamelingSpawnInterval = reader.getNextLong();
        this.flamelingSpawnOffset = reader.getNextLong();
        this.flamelingUptime = reader.getNextLong();
        this.flameling.uniqueID = reader.getNextInt();
    }

    @Override
    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        save.addLong("flamelingSpawnInterval", this.flamelingSpawnInterval);
        save.addLong("flamelingSpawnOffset", this.flamelingSpawnOffset);
        save.addLong("flamelingUptime", this.flamelingUptime);
        save.addInt("flamelingUniqueID", this.flameling.uniqueID);
    }

    @Override
    public void applyLoadData(LoadData save) {
        super.applyLoadData(save);
        this.flamelingSpawnInterval = save.getLong("flamelingSpawnInterval", this.flamelingSpawnInterval, false);
        this.flamelingSpawnOffset = save.getLong("flamelingSpawnOffset", this.flamelingSpawnOffset, false);
        this.flamelingUptime = save.getLong("flamelingUptime", this.flamelingUptime, false);
        this.flameling.uniqueID = save.getInt("flamelingUniqueID", -1, false);
        if (this.flamelingUptime == 0L || this.flamelingSpawnInterval == 0L) {
            this.over();
        }
    }

    @Override
    public void serverTick() {
        Mob existing;
        super.serverTick();
        long currentProgress = this.getCurrentTimeInProgress();
        if (currentProgress <= this.flamelingSpawnInterval) {
            this.flameling.uniqueID = -1;
        } else if (this.flameling.uniqueID == -1 && ((existing = this.flameling.get(this.level)) == null || existing.removed())) {
            this.spawnFlameling();
        }
    }

    public long getCurrentTimeInProgress() {
        long fullTime = this.getFullFlamelingRunTime();
        return Math.floorMod(this.getTime() - this.flamelingSpawnOffset, fullTime);
    }

    public long getFullFlamelingRunTime() {
        return this.flamelingSpawnInterval + this.flamelingUptime;
    }

    public void spawnFlameling() {
        Mob flameling = MobRegistry.getMob("flameling", this.level);
        flameling.resetUniqueID();
        GameRandom random = new GameRandom();
        List players = GameUtils.streamServerClients(this.level).map(c -> c.playerMob).collect(Collectors.toList());
        if (players.isEmpty()) {
            return;
        }
        Mob playerChosen = (Mob)random.getOneOf(players);
        Point randomTile = null;
        for (int i = 0; i < 20; ++i) {
            randomTile = SPAWN_AREA.getRandomTile(random, playerChosen.getTileX(), playerChosen.getTileY());
            if (!this.level.isSolidTile(randomTile.x, randomTile.y)) break;
        }
        flameling.onSpawned(randomTile.x * 32 + 16, randomTile.y * 32 + 16);
        this.level.entityManager.mobs.add(flameling);
        this.level.onMobSpawned(flameling);
        this.flameling.uniqueID = flameling.getUniqueID();
    }
}

