/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.gameNetworkData;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.gameNetworkData.GNDItem;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;

public class GNDItemGameMessage
extends GNDItem {
    public GameMessage message;

    public GNDItemGameMessage(GameMessage message) {
        this.message = message;
    }

    public GNDItemGameMessage(PacketReader reader) {
        this.readPacket(reader);
    }

    public GNDItemGameMessage(LoadData data) {
        this.message = GameMessage.loadSave(data, "value", false);
    }

    @Override
    public String toString() {
        if (this.message == null) {
            return "message{null}";
        }
        return this.message.toString();
    }

    @Override
    public boolean isDefault() {
        return this.message == null;
    }

    @Override
    public boolean equals(GNDItem item) {
        if (item instanceof GNDItemGameMessage) {
            GNDItemGameMessage other = (GNDItemGameMessage)item;
            return this == item || this.message.equals(other.message);
        }
        return false;
    }

    @Override
    public GNDItemGameMessage copy() {
        return new GNDItemGameMessage(this.message);
    }

    @Override
    public void addSaveData(SaveData data) {
        data.addSaveData(this.message.getSaveData("value"));
    }

    @Override
    public void writePacket(PacketWriter writer) {
        this.message.writePacket(writer);
    }

    @Override
    public void readPacket(PacketReader reader) {
        this.message = GameMessage.fromPacket(reader);
    }
}

