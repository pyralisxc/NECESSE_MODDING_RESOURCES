/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameLogicGate.entities;

import java.awt.Point;
import necesse.engine.GameTileRange;
import necesse.engine.localization.Localization;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketOpenContainer;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.level.gameLogicGate.GameLogicGate;
import necesse.level.gameLogicGate.entities.LogicGateEntity;
import necesse.level.maps.Level;
import necesse.level.maps.TilePosition;

public class SensorLogicGateEntity
extends LogicGateEntity {
    public static int MAX_RANGE = 15;
    public static GameTileRange[] TILE_RANGES = new GameTileRange[MAX_RANGE];
    public boolean[] wireOutputs = new boolean[4];
    private boolean active = false;
    public int range = 5;
    public boolean players = true;
    public boolean passiveMobs = true;
    public boolean hostileMobs = true;

    public static GameTileRange getTileRange(int range) {
        GameTileRange tileRange = TILE_RANGES[(range = GameMath.limit(range, 1, TILE_RANGES.length)) - 1];
        if (tileRange == null) {
            SensorLogicGateEntity.TILE_RANGES[range - 1] = tileRange = new GameTileRange(range, new Point[0]);
        }
        return tileRange;
    }

    public SensorLogicGateEntity(GameLogicGate logicGate, Level level, int tileX, int tileY) {
        super(logicGate, level, tileX, tileY);
    }

    public SensorLogicGateEntity(GameLogicGate logicGate, TilePosition pos) {
        this(logicGate, pos.level, pos.tileX, pos.tileY);
    }

    @Override
    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        save.addSmallBooleanArray("wireOutputs", this.wireOutputs);
        save.addBoolean("players", this.players);
        save.addBoolean("passiveMobs", this.passiveMobs);
        save.addBoolean("hostileMobs", this.hostileMobs);
        save.addInt("range", this.range);
    }

    @Override
    public void applyLoadData(LoadData save) {
        super.applyLoadData(save);
        this.wireOutputs = save.getSmallBooleanArray("wireOutputs", this.wireOutputs);
        this.players = save.getBoolean("players", true);
        this.passiveMobs = save.getBoolean("passiveMobs", true);
        this.hostileMobs = save.getBoolean("hostileMobs", true);
        this.range = save.getInt("range", 5);
        this.updateOutputs(true);
    }

    @Override
    public void writePacket(PacketWriter writer) {
        super.writePacket(writer);
        for (int i = 0; i < 4; ++i) {
            writer.putNextBoolean(this.wireOutputs[i]);
        }
        writer.putNextBoolean(this.players);
        writer.putNextBoolean(this.passiveMobs);
        writer.putNextBoolean(this.hostileMobs);
        writer.putNextByteUnsigned(this.range);
    }

    @Override
    public void applyPacket(PacketReader reader) {
        super.applyPacket(reader);
        for (int i = 0; i < 4; ++i) {
            this.wireOutputs[i] = reader.getNextBoolean();
        }
        this.players = reader.getNextBoolean();
        this.passiveMobs = reader.getNextBoolean();
        this.hostileMobs = reader.getNextBoolean();
        this.range = reader.getNextByteUnsigned();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.isServer()) {
            this.checkCollision();
        }
    }

    private void checkCollision() {
        GameTileRange tileRange = SensorLogicGateEntity.getTileRange(this.range);
        boolean foundAny = this.level.entityManager.mobs.getInRegionByTileRange(this.tileX, this.tileY, tileRange.maxRange).stream().anyMatch(m -> m.canLevelInteract() && (m.isHostile && this.hostileMobs || !m.isHostile && this.passiveMobs) && tileRange.isWithinRange(this.tileX, this.tileY, m.getTileX(), m.getTileY()));
        if (!foundAny && this.players && this.isServer()) {
            foundAny = this.level.entityManager.players.getInRegionByTileRange(this.tileX, this.tileY, tileRange.maxRange).stream().anyMatch(p -> (!p.isServerClient() || p.getServerClient().hasSpawned()) && p.canLevelInteract() && tileRange.isWithinRange(this.tileX, this.tileY, p.getTileX(), p.getTileY()));
        }
        if (this.active != foundAny) {
            this.active = foundAny;
            this.updateOutputs(false);
        }
    }

    public boolean isActive() {
        return this.active;
    }

    public void updateOutputs(boolean forceUpdate) {
        for (int i = 0; i < 4; ++i) {
            boolean desired = this.wireOutputs[i] && this.active;
            this.setOutput(i, desired, forceUpdate);
        }
    }

    @Override
    public ListGameTooltips getTooltips(PlayerMob perspective, boolean debug) {
        ListGameTooltips tooltips = super.getTooltips(perspective, debug);
        if (this.players) {
            tooltips.add(Localization.translate("logictooltips", "sensorplayeron"));
        } else {
            tooltips.add(Localization.translate("logictooltips", "sensorplayeroff"));
        }
        if (this.hostileMobs) {
            tooltips.add(Localization.translate("logictooltips", "sensorhostileon"));
        } else {
            tooltips.add(Localization.translate("logictooltips", "sensorhostileoff"));
        }
        if (this.passiveMobs) {
            tooltips.add(Localization.translate("logictooltips", "sensorpassiveon"));
        } else {
            tooltips.add(Localization.translate("logictooltips", "sensorpassiveoff"));
        }
        return tooltips;
    }

    @Override
    public void openContainer(ServerClient client) {
        ContainerRegistry.openAndSendContainer(client, PacketOpenContainer.LevelObject(ContainerRegistry.SENSOR_LOGIC_GATE_CONTAINER, this.tileX, this.tileY));
    }
}

