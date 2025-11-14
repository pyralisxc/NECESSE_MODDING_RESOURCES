/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.entity.levelEvent.LevelEvent;

public class PacketLevelEventOver
extends Packet {
    public final int eventUniqueID;

    public PacketLevelEventOver(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.eventUniqueID = reader.getNextInt();
    }

    public PacketLevelEventOver(int uniqueID) {
        this.eventUniqueID = uniqueID;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(this.eventUniqueID);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        LevelEvent event;
        if (client.getLevel() != null && (event = client.getLevel().entityManager.events.get(this.eventUniqueID, false)) != null) {
            event.over();
        }
    }
}

