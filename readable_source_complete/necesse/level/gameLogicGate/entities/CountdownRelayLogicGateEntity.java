/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameLogicGate.entities;

import necesse.engine.localization.Localization;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketOpenContainer;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.level.gameLogicGate.GameLogicGate;
import necesse.level.gameLogicGate.entities.CountdownLogicGateEntity;
import necesse.level.gameLogicGate.entities.LogicGateEntity;
import necesse.level.maps.Level;
import necesse.level.maps.TilePosition;
import necesse.level.maps.presets.PresetRotation;

public class CountdownRelayLogicGateEntity
extends LogicGateEntity {
    public boolean[] wireOutputs = new boolean[4];
    public boolean[] relayDirections = new boolean[4];
    private boolean isActive;

    public CountdownRelayLogicGateEntity(GameLogicGate logicGate, Level level, int tileX, int tileY) {
        super(logicGate, level, tileX, tileY);
    }

    public CountdownRelayLogicGateEntity(GameLogicGate logicGate, TilePosition pos) {
        this(logicGate, pos.level, pos.tileX, pos.tileY);
    }

    @Override
    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        save.addSmallBooleanArray("wireOutputs", this.wireOutputs);
        save.addSmallBooleanArray("relayDirections", this.relayDirections);
        save.addBoolean("isActive", this.isActive);
    }

    @Override
    public void applyLoadData(LoadData save) {
        super.applyLoadData(save);
        this.wireOutputs = save.getSmallBooleanArray("wireOutputs", this.wireOutputs);
        this.relayDirections = save.getSmallBooleanArray("relayDirections", this.relayDirections);
        this.isActive = save.getBoolean("isActive", this.isActive);
        this.updateOutputs(true);
    }

    @Override
    public void applyPresetLoadData(LoadData save, boolean mirrorX, boolean mirrorY, PresetRotation rotation) {
        super.applyPresetLoadData(save, mirrorX, mirrorY, rotation);
        CountdownLogicGateEntity.applyPresetRotationToDirectionArray(this.relayDirections, mirrorX, mirrorY, rotation);
    }

    @Override
    public void writePacket(PacketWriter writer) {
        super.writePacket(writer);
        for (int i = 0; i < 4; ++i) {
            writer.putNextBoolean(this.wireOutputs[i]);
        }
        for (boolean relayDirection : this.relayDirections) {
            writer.putNextBoolean(relayDirection);
        }
        writer.putNextBoolean(this.isActive);
    }

    @Override
    public void applyPacket(PacketReader reader) {
        int i;
        super.applyPacket(reader);
        for (i = 0; i < 4; ++i) {
            this.wireOutputs[i] = reader.getNextBoolean();
        }
        for (i = 0; i < this.relayDirections.length; ++i) {
            this.relayDirections[i] = reader.getNextBoolean();
        }
        this.isActive = reader.getNextBoolean();
        if (this.isServer()) {
            this.updateOutputs(true);
        }
    }

    public void setActive(boolean active) {
        if (this.isActive == active) {
            return;
        }
        this.isActive = active;
        this.updateOutputs(false);
    }

    @Override
    protected void onUpdate(int wireID, boolean active) {
    }

    public void updateOutputs(boolean forceUpdate) {
        for (int i = 0; i < 4; ++i) {
            boolean desired = this.wireOutputs[i] && this.isActive;
            this.setOutput(i, desired, forceUpdate);
        }
    }

    @Override
    public ListGameTooltips getTooltips(PlayerMob perspective, boolean debug) {
        ListGameTooltips tooltips = super.getTooltips(perspective, debug);
        tooltips.add(Localization.translate("logictooltips", "logicoutputs", "value", this.getWireTooltip(this.wireOutputs)));
        return tooltips;
    }

    @Override
    public void openContainer(ServerClient client) {
        ContainerRegistry.openAndSendContainer(client, PacketOpenContainer.LevelObject(ContainerRegistry.COUNTDOWN_RELAY_LOGIC_GATE_CONTAINER, this.tileX, this.tileY));
    }
}

