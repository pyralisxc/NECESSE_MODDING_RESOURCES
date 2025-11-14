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

public class DelayLogicGateEntity
extends LogicGateEntity {
    public boolean[] wireInputs = new boolean[4];
    public boolean[] wireOutputs = new boolean[4];
    public int delayTicks = 20;
    public int ticksToFlip;
    private boolean active;

    public DelayLogicGateEntity(GameLogicGate logicGate, Level level, int tileX, int tileY) {
        super(logicGate, level, tileX, tileY);
    }

    public DelayLogicGateEntity(GameLogicGate logicGate, TilePosition pos) {
        this(logicGate, pos.level, pos.tileX, pos.tileY);
    }

    @Override
    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        save.addSmallBooleanArray("wireInputs", this.wireInputs);
        save.addSmallBooleanArray("wireOutputs", this.wireOutputs);
        save.addInt("delayTicks", this.delayTicks);
        save.addInt("ticksToFlip", this.ticksToFlip);
        save.addBoolean("active", this.active);
    }

    @Override
    public void applyLoadData(LoadData save) {
        super.applyLoadData(save);
        this.wireInputs = save.getSmallBooleanArray("wireInputs", this.wireInputs);
        this.wireOutputs = save.getSmallBooleanArray("wireOutputs", this.wireOutputs);
        this.delayTicks = save.getInt("delayTicks", this.delayTicks);
        this.ticksToFlip = save.getInt("ticksToFlip", this.ticksToFlip);
        this.active = save.getBoolean("active", this.active);
        this.updateOutputs(true);
    }

    @Override
    public void writePacket(PacketWriter writer) {
        super.writePacket(writer);
        for (int i = 0; i < 4; ++i) {
            writer.putNextBoolean(this.wireInputs[i]);
            writer.putNextBoolean(this.wireOutputs[i]);
        }
        writer.putNextShortUnsigned(this.delayTicks);
        writer.putNextBoolean(this.active);
    }

    @Override
    public void applyPacket(PacketReader reader) {
        super.applyPacket(reader);
        for (int i = 0; i < 4; ++i) {
            this.wireInputs[i] = reader.getNextBoolean();
            this.wireOutputs[i] = reader.getNextBoolean();
        }
        this.delayTicks = reader.getNextShortUnsigned();
        this.active = reader.getNextBoolean();
        if (this.isServer()) {
            this.updateOutputs(true);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.isServer()) {
            this.tickActive();
        }
    }

    private void tickActive() {
        if (this.ticksToFlip > 0) {
            --this.ticksToFlip;
            if (this.ticksToFlip <= 0) {
                this.active = !this.active;
                this.updateOutputs(false);
            }
        }
    }

    @Override
    protected void onUpdate(int wireID, boolean active) {
        if (!this.isServer()) {
            return;
        }
        if (this.wireInputs[wireID]) {
            boolean toggleActive = false;
            for (int i = 0; i < 4; ++i) {
                if (!this.wireInputs[i] || !this.isWireActive(i)) continue;
                toggleActive = true;
                break;
            }
            this.ticksToFlip = toggleActive != this.active && this.ticksToFlip == 0 ? this.delayTicks : 0;
        }
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
        tooltips.add(Localization.translate("logictooltips", "logicinputs", "value", this.getWireTooltip(this.wireInputs)));
        tooltips.add(Localization.translate("logictooltips", "logicoutputs", "value", this.getWireTooltip(this.wireOutputs)));
        tooltips.add(Localization.translate("logictooltips", "bufferdelay", "value", (Object)this.delayTicks));
        if (this.active) {
            tooltips.add(Localization.translate("logictooltips", "logicactive"));
        } else {
            tooltips.add(Localization.translate("logictooltips", "logicinactive"));
        }
        return tooltips;
    }

    @Override
    public void openContainer(ServerClient client) {
        ContainerRegistry.openAndSendContainer(client, PacketOpenContainer.LevelObject(ContainerRegistry.DELAY_LOGIC_GATE_CONTAINER, this.tileX, this.tileY));
    }
}

