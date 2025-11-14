/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameLogicGate.entities;

import java.awt.Point;
import java.util.ArrayList;
import necesse.engine.GameEventsHandler;
import necesse.engine.GameState;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketLogicGateOutputUpdate;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.LogicGateRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.OneWorldMigration;
import necesse.engine.world.WorldEntity;
import necesse.engine.world.WorldEntityGameClock;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.level.gameLogicGate.GameLogicGate;
import necesse.level.maps.Level;
import necesse.level.maps.presets.PresetRotation;

public abstract class LogicGateEntity
implements GameState,
WorldEntityGameClock {
    public final int logicGateID;
    public final Level level;
    public final int tileX;
    public final int tileY;
    private final ArrayList<UpdateCooldown> updateCooldowns;
    protected boolean updateWireOuts;
    private boolean[] outputs;
    private boolean isRemoved;
    public GameEventsHandler<ApplyPacketEvent> applyPacketEvents = new GameEventsHandler(true);

    public LogicGateEntity(GameLogicGate logicGate, Level level, int tileX, int tileY) {
        this.logicGateID = logicGate.getID();
        this.level = level;
        this.tileX = tileX;
        this.tileY = tileY;
        this.updateCooldowns = new ArrayList();
        this.updateWireOuts = false;
        this.outputs = new boolean[4];
    }

    public final SaveData getSaveData(String name) {
        SaveData data = new SaveData(name);
        data.addInt("tileX", this.tileX);
        data.addInt("tileY", this.tileY);
        data.addUnsafeString("stringID", this.getLogicGate().getStringID());
        this.addSaveData(data);
        return data;
    }

    public static LogicGateEntity loadEntity(Level level, LoadData data, boolean printWarnings) throws LogicGateLoadException {
        int tileX = data.getInt("tileX", Integer.MIN_VALUE, printWarnings);
        int tileY = data.getInt("tileY", Integer.MIN_VALUE, printWarnings);
        if (tileX == Integer.MIN_VALUE || tileY == Integer.MIN_VALUE) {
            throw new LogicGateLoadException("Failed to load a logic gate: Missing position");
        }
        String stringID = data.getUnsafeString("stringID", null, printWarnings);
        if (stringID == null) {
            throw new LogicGateLoadException("Failed to load logic gate: No stringID");
        }
        int logicGateID = LogicGateRegistry.getLogicGateID(stringID);
        if (logicGateID == -1) {
            throw new LogicGateLoadException("Failed to load logic gate: Invalid stringID " + stringID);
        }
        try {
            GameLogicGate logicGate = LogicGateRegistry.getLogicGate(logicGateID);
            LogicGateEntity entity = logicGate.getNewEntity(level, tileX, tileY);
            entity.applyLoadData(data);
            return entity;
        }
        catch (Exception e) {
            throw new LogicGateLoadException("Error loading logic gate entity: " + tileX + ", " + tileY + ", " + stringID, e);
        }
    }

    public void addSaveData(SaveData save) {
        save.addSmallBooleanArray("outputs", this.outputs);
    }

    public void applyLoadData(LoadData save) {
        this.outputs = save.getSmallBooleanArray("outputs");
    }

    public void addPresetSaveData(SaveData save) {
        this.addSaveData(save);
    }

    public void applyPresetLoadData(LoadData save, boolean mirrorX, boolean mirrorY, PresetRotation rotation) {
        this.applyLoadData(save);
    }

    public void writePacket(PacketWriter writer) {
        for (boolean output : this.outputs) {
            writer.putNextBoolean(output);
        }
    }

    public void applyPacket(PacketReader reader) {
        for (int i = 0; i < this.outputs.length; ++i) {
            boolean next = reader.getNextBoolean();
            if (this.outputs[i] == next) continue;
            this.outputs[i] = next;
            this.level.wireManager.updateWire(this.tileX, this.tileY, i, this.outputs[i]);
        }
    }

    public void setupOutputUpdate(PacketWriter writer) {
        for (int i = 0; i < 4; ++i) {
            writer.putNextBoolean(this.outputs[i]);
        }
    }

    public void applyOutputUpdate(PacketReader reader) {
        for (int i = 0; i < 4; ++i) {
            boolean nextOutput = reader.getNextBoolean();
            if (this.outputs[i] == nextOutput) continue;
            this.outputs[i] = nextOutput;
            this.level.wireManager.updateWire(this.tileX, this.tileY, i, nextOutput);
        }
    }

    public void init() {
    }

    public void tick() {
        this.updateCooldowns.clear();
        if (this.isServer() && this.updateWireOuts) {
            this.level.getServer().network.sendToClientsWithTile(new PacketLogicGateOutputUpdate(this), this.level, this.tileX, this.tileY);
            for (int i = 0; i < 4; ++i) {
                this.level.wireManager.updateWire(this.tileX, this.tileY, i, this.outputs[i]);
            }
            this.updateWireOuts = false;
        }
    }

    protected void onUpdate(int wireID, boolean active) {
    }

    public final void onWireUpdate(int wireID, boolean active) {
        UpdateCooldown uc = new UpdateCooldown(wireID, active);
        boolean onCooldown = this.updateCooldowns.contains(uc);
        if (onCooldown) {
            return;
        }
        this.updateCooldowns.add(uc);
        this.onUpdate(wireID, active);
    }

    public final void setOutput(int wire, boolean active) {
        this.setOutput(wire, active, false);
    }

    public final void setOutput(int wire, boolean active, boolean forceUpdate) {
        boolean shouldUpdate;
        boolean bl = shouldUpdate = this.outputs[wire] != active;
        if (shouldUpdate || forceUpdate) {
            this.outputs[wire] = active;
            if (shouldUpdate) {
                this.updateWireOuts = true;
            }
            this.level.wireManager.updateWire(this.tileX, this.tileY, wire, active);
        }
    }

    public final boolean getOutput(int wire) {
        return this.outputs[wire];
    }

    public final GameLogicGate getLogicGate() {
        return LogicGateRegistry.getLogicGate(this.logicGateID);
    }

    public boolean isWireActive(int wireID) {
        return this.level.wireManager.isWireActive(this.tileX, this.tileY, wireID);
    }

    @Override
    public WorldEntity getWorldEntity() {
        return this.level == null ? null : this.level.getWorldEntity();
    }

    @Override
    public boolean isClient() {
        return this.level != null && this.level.isClient();
    }

    @Override
    public Client getClient() {
        return this.level == null ? null : this.level.getClient();
    }

    @Override
    public boolean isServer() {
        return this.level != null && this.level.isServer();
    }

    @Override
    public Server getServer() {
        return this.level == null ? null : this.level.getServer();
    }

    public void remove() {
        this.isRemoved = true;
    }

    public boolean isRemoved() {
        return this.isRemoved;
    }

    public void sendUpdatePacket() {
        if (!this.isServer()) {
            return;
        }
        this.level.getServer().network.sendToClientsWithTile(this.level.logicLayer.getUpdatePacket(this.tileX, this.tileY), this.level, this.tileX, this.tileY);
    }

    public abstract void openContainer(ServerClient var1);

    public ListGameTooltips getTooltips(PlayerMob perspective, boolean debug) {
        return new ListGameTooltips(this.getLogicGate().getDisplayName());
    }

    protected String getWireTooltip(boolean[] active) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < 4; ++i) {
            if (!active[i]) continue;
            out.append("RGBY".charAt(i));
        }
        return out.toString();
    }

    public void migrateToOneWorld(OneWorldMigration migrationData, LevelIdentifier oldLevelIdentifier, Point tileOffset, Point positionOffset) {
    }

    public static class LogicGateLoadException
    extends Exception {
        public LogicGateLoadException(String message) {
            super(message);
        }

        public LogicGateLoadException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    private static class UpdateCooldown {
        public final int wireID;
        public final boolean active;

        public UpdateCooldown(int wireID, boolean active) {
            this.wireID = wireID;
            this.active = active;
        }

        public boolean equals(Object o) {
            if (o instanceof UpdateCooldown) {
                UpdateCooldown cd = (UpdateCooldown)o;
                return this.wireID == cd.wireID && this.active == cd.active;
            }
            return super.equals(o);
        }
    }

    public static class ApplyPacketEvent {
    }
}

