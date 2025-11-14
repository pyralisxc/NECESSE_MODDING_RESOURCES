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

public class SRLatchLogicGateEntity
extends LogicGateEntity {
    public boolean[] activateInputs = new boolean[4];
    public boolean[] resetInputs = new boolean[4];
    public boolean[] wireOutputs = new boolean[4];
    boolean active = false;

    public SRLatchLogicGateEntity(GameLogicGate logicGate, Level level, int tileX, int tileY) {
        super(logicGate, level, tileX, tileY);
    }

    public SRLatchLogicGateEntity(GameLogicGate logicGate, TilePosition pos) {
        this(logicGate, pos.level, pos.tileX, pos.tileY);
    }

    @Override
    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        save.addSmallBooleanArray("activateInputs", this.activateInputs);
        save.addSmallBooleanArray("resetInputs", this.resetInputs);
        save.addSmallBooleanArray("wireOutputs", this.wireOutputs);
        save.addBoolean("active", this.active);
    }

    @Override
    public void applyLoadData(LoadData save) {
        super.applyLoadData(save);
        this.activateInputs = save.getSmallBooleanArray("activateInputs", this.activateInputs);
        this.resetInputs = save.getSmallBooleanArray("resetInputs", this.resetInputs);
        this.wireOutputs = save.getSmallBooleanArray("wireOutputs", this.wireOutputs);
        this.active = save.getBoolean("active", false);
        this.updateOutputs(true);
    }

    @Override
    public void writePacket(PacketWriter writer) {
        super.writePacket(writer);
        for (int i = 0; i < 4; ++i) {
            writer.putNextBoolean(this.activateInputs[i]);
            writer.putNextBoolean(this.resetInputs[i]);
            writer.putNextBoolean(this.wireOutputs[i]);
        }
        writer.putNextBoolean(this.active);
    }

    @Override
    public void applyPacket(PacketReader reader) {
        super.applyPacket(reader);
        for (int i = 0; i < 4; ++i) {
            this.activateInputs[i] = reader.getNextBoolean();
            this.resetInputs[i] = reader.getNextBoolean();
            this.wireOutputs[i] = reader.getNextBoolean();
        }
        this.active = reader.getNextBoolean();
        if (this.isServer()) {
            this.updateOutputs(true);
        }
    }

    @Override
    protected void onUpdate(int wireID, boolean active) {
        int i;
        if (!this.isServer()) {
            return;
        }
        for (i = 0; i < 4; ++i) {
            if (!this.isWireActive(i) || !this.activateInputs[i]) continue;
            this.active = true;
            break;
        }
        for (i = 0; i < 4; ++i) {
            if (!this.isWireActive(i) || !this.resetInputs[i]) continue;
            this.active = false;
            break;
        }
        this.updateOutputs(false);
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
        tooltips.add(Localization.translate("logictooltips", "rsactivate", "value", this.getWireTooltip(this.activateInputs)));
        tooltips.add(Localization.translate("logictooltips", "rsreset", "value", this.getWireTooltip(this.resetInputs)));
        tooltips.add(Localization.translate("logictooltips", "logicoutputs", "value", this.getWireTooltip(this.wireOutputs)));
        if (this.active) {
            tooltips.add(Localization.translate("logictooltips", "logicactive"));
        } else {
            tooltips.add(Localization.translate("logictooltips", "logicinactive"));
        }
        return tooltips;
    }

    @Override
    public void openContainer(ServerClient client) {
        ContainerRegistry.openAndSendContainer(client, PacketOpenContainer.LevelObject(ContainerRegistry.SRLATCH_LOGIC_GATE_CONTAINER, this.tileX, this.tileY));
    }
}

