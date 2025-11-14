/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketRequestLevelEvent;
import necesse.entity.levelEvent.LevelEvent;

public class PacketLevelEventAction
extends Packet {
    public final int eventUniqueID;
    public final int actionID;
    public final Packet actionContent;

    public PacketLevelEventAction(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.eventUniqueID = reader.getNextInt();
        this.actionID = reader.getNextShort();
        this.actionContent = reader.getNextContentPacket();
    }

    public PacketLevelEventAction(LevelEvent event, int actionID, Packet content) {
        this.eventUniqueID = event.getUniqueID();
        this.actionID = actionID;
        this.actionContent = content;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(this.eventUniqueID);
        writer.putNextShort((short)actionID);
        writer.putNextContentPacket(this.actionContent);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        if (client.getLevel() == null) {
            return;
        }
        LevelEvent event = client.getLevel().entityManager.events.get(this.eventUniqueID, false);
        if (event != null) {
            event.runAction(this.actionID, new PacketReader(this.actionContent));
        } else {
            client.network.sendPacket(new PacketRequestLevelEvent(this.eventUniqueID));
        }
    }
}

