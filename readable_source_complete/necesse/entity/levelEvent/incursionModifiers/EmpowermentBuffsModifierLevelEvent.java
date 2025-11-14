/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.incursionModifiers;

import java.awt.Point;
import java.util.List;
import java.util.stream.Collectors;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.manager.MobSpawnArea;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.pickup.IncursionEmpowermentPickupEntity;

public class EmpowermentBuffsModifierLevelEvent
extends LevelEvent {
    public long empowermentPickUpsSpawnInterval;
    public long previousPickUpSpawnTime = 0L;
    public long pickupSpawnTimeOffset;
    public static MobSpawnArea SPAWN_AREA = new MobSpawnArea(128, 256);

    public EmpowermentBuffsModifierLevelEvent() {
        super(true);
        this.shouldSave = true;
    }

    public EmpowermentBuffsModifierLevelEvent(long empowermentPickUpsSpawnInterval, long pickupSpawnTimeOffset) {
        this();
        this.empowermentPickUpsSpawnInterval = empowermentPickUpsSpawnInterval;
        this.pickupSpawnTimeOffset = pickupSpawnTimeOffset;
    }

    @Override
    public boolean isNetworkImportant() {
        return true;
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextLong(this.empowermentPickUpsSpawnInterval);
        writer.putNextLong(this.pickupSpawnTimeOffset);
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.empowermentPickUpsSpawnInterval = reader.getNextLong();
        this.pickupSpawnTimeOffset = reader.getNextLong();
    }

    @Override
    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        save.addLong("empowermentPickUpsSpawnInterval", this.empowermentPickUpsSpawnInterval);
        save.addLong("pickupSpawnTimeOffset", this.pickupSpawnTimeOffset);
    }

    @Override
    public void applyLoadData(LoadData save) {
        super.applyLoadData(save);
        this.empowermentPickUpsSpawnInterval = save.getLong("empowermentPickUpsSpawnInterval", this.empowermentPickUpsSpawnInterval, false);
        this.pickupSpawnTimeOffset = save.getLong("pickupSpawnTimeOffset", this.pickupSpawnTimeOffset, false);
        if (this.empowermentPickUpsSpawnInterval == 0L) {
            this.over();
        }
    }

    @Override
    public void serverTick() {
        super.serverTick();
        if (this.getTime() >= this.previousPickUpSpawnTime + this.empowermentPickUpsSpawnInterval) {
            this.previousPickUpSpawnTime = this.getTime();
            this.spawnPickUp();
        }
    }

    public void spawnPickUp() {
        GameRandom random = new GameRandom();
        List players = GameUtils.streamServerClients(this.level).map(c -> c.playerMob).collect(Collectors.toList());
        if (!players.isEmpty()) {
            for (PlayerMob player : players) {
                Point randomTile = null;
                for (int i = 0; i < 20; ++i) {
                    randomTile = SPAWN_AREA.getRandomTile(random, player.getTileX(), player.getTileY());
                    if (!this.level.isSolidTile(randomTile.x, randomTile.y)) break;
                }
                IncursionEmpowermentPickupEntity pickup = new IncursionEmpowermentPickupEntity(this.getLevel(), randomTile.x * 32 + 16, randomTile.y * 32 + 16, 0.0f, 0.0f);
                this.level.entityManager.pickups.add(pickup);
            }
        }
    }
}

