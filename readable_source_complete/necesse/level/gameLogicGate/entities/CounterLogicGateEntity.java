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
import necesse.engine.util.GameMath;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.level.gameLogicGate.GameLogicGate;
import necesse.level.gameLogicGate.entities.LogicGateEntity;
import necesse.level.maps.Level;
import necesse.level.maps.TilePosition;

public class CounterLogicGateEntity
extends LogicGateEntity {
    public boolean[] incInputs = new boolean[4];
    public boolean[] decInputs = new boolean[4];
    public boolean[] resetInputs = new boolean[4];
    public boolean[] wireOutputs = new boolean[4];
    public int currentValue = 0;
    protected int maxValue = 1;

    public CounterLogicGateEntity(GameLogicGate logicGate, Level level, int tileX, int tileY) {
        super(logicGate, level, tileX, tileY);
    }

    public CounterLogicGateEntity(GameLogicGate logicGate, TilePosition pos) {
        this(logicGate, pos.level, pos.tileX, pos.tileY);
    }

    @Override
    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        save.addSmallBooleanArray("incInputs", this.incInputs);
        save.addSmallBooleanArray("decInputs", this.decInputs);
        save.addSmallBooleanArray("resetInputs", this.resetInputs);
        save.addSmallBooleanArray("wireOutputs", this.wireOutputs);
        save.addInt("currentValue", this.currentValue);
        save.addInt("maxValue", this.maxValue);
    }

    @Override
    public void applyLoadData(LoadData save) {
        super.applyLoadData(save);
        this.incInputs = save.getSmallBooleanArray("incInputs", this.incInputs);
        this.decInputs = save.getSmallBooleanArray("decInputs", this.decInputs);
        this.resetInputs = save.getSmallBooleanArray("resetInputs", this.resetInputs);
        this.wireOutputs = save.getSmallBooleanArray("wireOutputs", this.wireOutputs);
        this.currentValue = save.getInt("currentValue", this.currentValue);
        this.maxValue = save.getInt("maxValue", this.maxValue);
        this.updateOutputs(true);
    }

    @Override
    public void writePacket(PacketWriter writer) {
        super.writePacket(writer);
        for (int i = 0; i < 4; ++i) {
            writer.putNextBoolean(this.incInputs[i]);
            writer.putNextBoolean(this.decInputs[i]);
            writer.putNextBoolean(this.resetInputs[i]);
            writer.putNextBoolean(this.wireOutputs[i]);
        }
        writer.putNextShortUnsigned(this.currentValue);
        writer.putNextShortUnsigned(this.maxValue);
    }

    @Override
    public void applyPacket(PacketReader reader) {
        super.applyPacket(reader);
        for (int i = 0; i < 4; ++i) {
            this.incInputs[i] = reader.getNextBoolean();
            this.decInputs[i] = reader.getNextBoolean();
            this.resetInputs[i] = reader.getNextBoolean();
            this.wireOutputs[i] = reader.getNextBoolean();
        }
        this.currentValue = reader.getNextShortUnsigned();
        this.setMaxValue(reader.getNextShortUnsigned());
        if (this.isServer()) {
            this.updateOutputs(true);
        }
    }

    @Override
    public void setupOutputUpdate(PacketWriter writer) {
        super.setupOutputUpdate(writer);
        writer.putNextShortUnsigned(this.currentValue);
    }

    @Override
    public void applyOutputUpdate(PacketReader reader) {
        super.applyOutputUpdate(reader);
        this.currentValue = reader.getNextShortUnsigned();
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = GameMath.limit(maxValue, 1, 256);
        if (this.currentValue > this.maxValue) {
            this.currentValue %= this.maxValue + 1;
            this.updateWireOuts = true;
        }
    }

    public int getMaxValue() {
        return this.maxValue;
    }

    @Override
    protected void onUpdate(int wireID, boolean active) {
        if (!this.isServer()) {
            return;
        }
        if (active && this.resetInputs[wireID]) {
            if (this.currentValue != 0) {
                this.currentValue = 0;
                this.updateWireOuts = true;
            }
        } else {
            if (active && this.incInputs[wireID]) {
                ++this.currentValue;
                if (this.currentValue > this.maxValue) {
                    this.currentValue %= this.maxValue + 1;
                }
                this.updateWireOuts = true;
            }
            if (active && this.decInputs[wireID]) {
                --this.currentValue;
                if (this.currentValue < 0) {
                    this.currentValue = this.maxValue + 1 + this.currentValue % (this.maxValue + 1);
                }
                this.updateWireOuts = true;
            }
        }
        this.updateOutputs(false);
    }

    public void updateOutputs(boolean forceUpdate) {
        for (int i = 0; i < 4; ++i) {
            boolean desired = this.wireOutputs[i] && this.currentValue == this.maxValue;
            this.setOutput(i, desired, forceUpdate);
        }
    }

    @Override
    public ListGameTooltips getTooltips(PlayerMob perspective, boolean debug) {
        ListGameTooltips tooltips = super.getTooltips(perspective, debug);
        tooltips.add(Localization.translate("logictooltips", "counterinc", "value", this.getWireTooltip(this.incInputs)));
        tooltips.add(Localization.translate("logictooltips", "counterdec", "value", this.getWireTooltip(this.decInputs)));
        tooltips.add(Localization.translate("logictooltips", "rsreset", "value", this.getWireTooltip(this.resetInputs)));
        tooltips.add(Localization.translate("logictooltips", "countervalue", "value", this.currentValue, "max", this.maxValue));
        tooltips.add(Localization.translate("logictooltips", "logicoutputs", "value", this.getWireTooltip(this.wireOutputs)));
        return tooltips;
    }

    @Override
    public void openContainer(ServerClient client) {
        ContainerRegistry.openAndSendContainer(client, PacketOpenContainer.LevelObject(ContainerRegistry.COUNTER_LOGIC_GATE_CONTAINER, this.tileX, this.tileY));
    }
}

