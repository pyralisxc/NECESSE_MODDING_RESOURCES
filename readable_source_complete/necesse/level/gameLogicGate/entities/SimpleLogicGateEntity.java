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
import necesse.level.gameLogicGate.entities.LogicGateEntity;
import necesse.level.maps.Level;
import necesse.level.maps.TilePosition;

public abstract class SimpleLogicGateEntity
extends LogicGateEntity {
    public boolean[] wireInputs = new boolean[4];
    public boolean[] wireOutputs = new boolean[4];

    public SimpleLogicGateEntity(GameLogicGate logicGate, Level level, int x, int y) {
        super(logicGate, level, x, y);
    }

    public SimpleLogicGateEntity(GameLogicGate logicGate, TilePosition pos) {
        this(logicGate, pos.level, pos.tileX, pos.tileY);
    }

    @Override
    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        save.addSmallBooleanArray("wireInputs", this.wireInputs);
        save.addSmallBooleanArray("wireOutputs", this.wireOutputs);
    }

    @Override
    public void applyLoadData(LoadData save) {
        super.applyLoadData(save);
        this.wireInputs = save.getSmallBooleanArray("wireInputs", this.wireInputs);
        this.wireOutputs = save.getSmallBooleanArray("wireOutputs", this.wireOutputs);
        this.updateOutputs(true);
    }

    @Override
    public void writePacket(PacketWriter writer) {
        super.writePacket(writer);
        for (int i = 0; i < 4; ++i) {
            writer.putNextBoolean(this.wireInputs[i]);
            writer.putNextBoolean(this.wireOutputs[i]);
        }
    }

    @Override
    public void applyPacket(PacketReader reader) {
        super.applyPacket(reader);
        for (int i = 0; i < 4; ++i) {
            this.wireInputs[i] = reader.getNextBoolean();
            this.wireOutputs[i] = reader.getNextBoolean();
        }
        if (this.isServer()) {
            this.updateOutputs(true);
        }
    }

    @Override
    public void onUpdate(int wireID, boolean active) {
        if (!this.isServer()) {
            return;
        }
        this.updateOutputs(false);
    }

    public void updateOutputs(boolean forceUpdate) {
        boolean condition = this.condition();
        for (int i = 0; i < 4; ++i) {
            boolean desired = this.wireOutputs[i] && condition;
            this.setOutput(i, desired, forceUpdate);
        }
    }

    public abstract boolean condition();

    @Override
    public void openContainer(ServerClient client) {
        ContainerRegistry.openAndSendContainer(client, PacketOpenContainer.LevelObject(ContainerRegistry.SIMPLE_LOGIC_GATE_CONTAINER, this.tileX, this.tileY));
    }

    @Override
    public ListGameTooltips getTooltips(PlayerMob perspective, boolean debug) {
        ListGameTooltips tooltips = super.getTooltips(perspective, debug);
        tooltips.add(Localization.translate("logictooltips", "logicinputs", "value", this.getWireTooltip(this.wireInputs)));
        tooltips.add(Localization.translate("logictooltips", "logicoutputs", "value", this.getWireTooltip(this.wireOutputs)));
        return tooltips;
    }
}

