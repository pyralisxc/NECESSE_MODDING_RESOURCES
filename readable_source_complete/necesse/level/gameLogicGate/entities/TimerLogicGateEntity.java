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

public class TimerLogicGateEntity
extends LogicGateEntity {
    public boolean[] wireInputs = new boolean[4];
    public boolean[] wireOutputs = new boolean[4];
    public int timerTicks = 20;
    public int timer;
    private boolean isRunning;
    private boolean active;
    private boolean firstTick = true;

    public TimerLogicGateEntity(GameLogicGate logicGate, Level level, int tileX, int tileY) {
        super(logicGate, level, tileX, tileY);
    }

    public TimerLogicGateEntity(GameLogicGate logicGate, TilePosition pos) {
        this(logicGate, pos.level, pos.tileX, pos.tileY);
    }

    @Override
    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        save.addSmallBooleanArray("wireInputs", this.wireInputs);
        save.addSmallBooleanArray("wireOutputs", this.wireOutputs);
        save.addInt("timerTicks", this.timerTicks);
    }

    @Override
    public void applyLoadData(LoadData save) {
        super.applyLoadData(save);
        this.wireInputs = save.getSmallBooleanArray("wireInputs", this.wireInputs);
        this.wireOutputs = save.getSmallBooleanArray("wireOutputs", this.wireOutputs);
        this.timerTicks = save.getInt("timerTicks", this.timerTicks);
        this.updateOutputs(true);
    }

    @Override
    public void writePacket(PacketWriter writer) {
        super.writePacket(writer);
        for (int i = 0; i < 4; ++i) {
            writer.putNextBoolean(this.wireInputs[i]);
            writer.putNextBoolean(this.wireOutputs[i]);
        }
        writer.putNextShortUnsigned(this.timerTicks);
    }

    @Override
    public void applyPacket(PacketReader reader) {
        super.applyPacket(reader);
        for (int i = 0; i < 4; ++i) {
            this.wireInputs[i] = reader.getNextBoolean();
            this.wireOutputs[i] = reader.getNextBoolean();
        }
        this.timerTicks = reader.getNextShortUnsigned();
        this.updateRunning();
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
        if (this.firstTick) {
            this.updateRunning();
        }
    }

    private void tickActive() {
        if (this.isRunning) {
            ++this.timer;
            this.timer %= this.timerTicks;
            boolean oldActive = this.active;
            boolean bl = this.active = this.timer == 0;
            if (oldActive != this.active) {
                this.updateOutputs(false);
            }
        } else {
            this.timer = 0;
            boolean oldActive = this.active;
            this.active = false;
            if (oldActive) {
                this.updateOutputs(false);
            }
        }
    }

    @Override
    protected void onUpdate(int wireID, boolean active) {
        this.updateRunning();
        if (!this.isServer()) {
            return;
        }
        this.updateOutputs(false);
    }

    public void updateRunning() {
        this.isRunning = false;
        for (int i = 0; i < 4; ++i) {
            if (!this.wireInputs[i] || !this.isWireActive(i)) continue;
            this.isRunning = true;
            break;
        }
        if (!this.isRunning) {
            this.timer = 0;
            this.active = false;
        }
    }

    public void updateOutputs(boolean forceUpdate) {
        for (int i = 0; i < 4; ++i) {
            boolean desired = this.wireOutputs[i] && this.active;
            this.setOutput(i, desired, forceUpdate);
        }
    }

    public boolean isRunning() {
        return this.isRunning;
    }

    @Override
    public ListGameTooltips getTooltips(PlayerMob perspective, boolean debug) {
        ListGameTooltips tooltips = super.getTooltips(perspective, debug);
        tooltips.add(Localization.translate("logictooltips", "logicinputs", "value", this.getWireTooltip(this.wireInputs)));
        tooltips.add(Localization.translate("logictooltips", "logicoutputs", "value", this.getWireTooltip(this.wireOutputs)));
        tooltips.add(Localization.translate("logictooltips", "timerticks", "value", (Object)this.timerTicks));
        tooltips.add(Localization.translate("logictooltips", "timercurrent", "value", (Object)this.timer));
        if (this.isRunning) {
            tooltips.add(Localization.translate("logictooltips", "timeractive"));
        } else {
            tooltips.add(Localization.translate("logictooltips", "timerinactive"));
        }
        return tooltips;
    }

    @Override
    public void openContainer(ServerClient client) {
        ContainerRegistry.openAndSendContainer(client, PacketOpenContainer.LevelObject(ContainerRegistry.TIMER_LOGIC_GATE_CONTAINER, this.tileX, this.tileY));
    }
}

