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

public class TFlipFlopLogicGateEntity
extends LogicGateEntity {
    public boolean[] wireInputs = new boolean[4];
    public boolean[] wireOutputs1 = new boolean[4];
    public boolean[] wireOutputs2 = new boolean[4];
    private boolean changeCooldown;
    private boolean flipped = false;

    public TFlipFlopLogicGateEntity(GameLogicGate logicGate, Level level, int tileX, int tileY) {
        super(logicGate, level, tileX, tileY);
    }

    public TFlipFlopLogicGateEntity(GameLogicGate logicGate, TilePosition pos) {
        this(logicGate, pos.level, pos.tileX, pos.tileY);
    }

    @Override
    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        save.addSmallBooleanArray("wireInputs", this.wireInputs);
        save.addSmallBooleanArray("wireOutputs1", this.wireOutputs1);
        save.addSmallBooleanArray("wireOutputs2", this.wireOutputs2);
        save.addBoolean("flipped", this.flipped);
    }

    @Override
    public void applyLoadData(LoadData save) {
        super.applyLoadData(save);
        this.wireInputs = save.getSmallBooleanArray("wireInputs", this.wireInputs);
        this.wireOutputs1 = save.getSmallBooleanArray("wireOutputs1", this.wireOutputs1);
        this.wireOutputs2 = save.getSmallBooleanArray("wireOutputs2", this.wireOutputs2);
        this.flipped = save.getBoolean("flipped", false);
        this.updateOutputs(true);
    }

    @Override
    public void writePacket(PacketWriter writer) {
        super.writePacket(writer);
        for (int i = 0; i < 4; ++i) {
            writer.putNextBoolean(this.wireInputs[i]);
            writer.putNextBoolean(this.wireOutputs1[i]);
            writer.putNextBoolean(this.wireOutputs2[i]);
        }
        writer.putNextBoolean(this.flipped);
    }

    @Override
    public void applyPacket(PacketReader reader) {
        super.applyPacket(reader);
        for (int i = 0; i < 4; ++i) {
            this.wireInputs[i] = reader.getNextBoolean();
            this.wireOutputs1[i] = reader.getNextBoolean();
            this.wireOutputs2[i] = reader.getNextBoolean();
        }
        this.flipped = reader.getNextBoolean();
        if (this.isServer()) {
            this.updateOutputs(true);
        }
    }

    @Override
    public void tick() {
        super.tick();
        this.changeCooldown = false;
    }

    @Override
    protected void onUpdate(int wireID, boolean active) {
        if (!this.isServer()) {
            return;
        }
        if (this.changeCooldown) {
            return;
        }
        this.changeCooldown = true;
        if (this.wireInputs[wireID] && active) {
            this.flipped = !this.flipped;
        }
        this.updateOutputs(false);
    }

    public void updateOutputs(boolean forceUpdate) {
        for (int i = 0; i < 4; ++i) {
            boolean desired = this.wireOutputs1[i] && !this.flipped || this.wireOutputs2[i] && this.flipped;
            this.setOutput(i, desired, forceUpdate);
        }
    }

    @Override
    public ListGameTooltips getTooltips(PlayerMob perspective, boolean debug) {
        ListGameTooltips tooltips = super.getTooltips(perspective, debug);
        tooltips.add(Localization.translate("logictooltips", "logicinputs", "value", this.getWireTooltip(this.wireInputs)));
        tooltips.add(Localization.translate("logictooltips", "logicoutputs", "value", this.getWireTooltip(this.wireOutputs1) + ", " + this.getWireTooltip(this.wireOutputs2)));
        if (this.flipped) {
            tooltips.add(Localization.translate("logictooltips", "tflipflopout1"));
        } else {
            tooltips.add(Localization.translate("logictooltips", "tflipflopout2"));
        }
        return tooltips;
    }

    @Override
    public void openContainer(ServerClient client) {
        ContainerRegistry.openAndSendContainer(client, PacketOpenContainer.LevelObject(ContainerRegistry.TFLIPFLOP_LOGIC_GATE_CONTAINER, this.tileX, this.tileY));
    }
}

