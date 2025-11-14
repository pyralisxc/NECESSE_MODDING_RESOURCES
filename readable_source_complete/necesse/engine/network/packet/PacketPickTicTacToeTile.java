/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketLevelEvent;
import necesse.engine.network.packet.PacketLevelEventOver;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.levelEvent.TicTacToeLevelEvent;

public class PacketPickTicTacToeTile
extends Packet {
    public final int eventUniqueID;
    public final int tileIndex;

    public PacketPickTicTacToeTile(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.eventUniqueID = reader.getNextInt();
        this.tileIndex = reader.getNextByteUnsigned();
    }

    public PacketPickTicTacToeTile(TicTacToeLevelEvent event, int tileIndex) {
        this.eventUniqueID = event.getUniqueID();
        this.tileIndex = tileIndex;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(this.eventUniqueID);
        writer.putNextByteUnsigned(tileIndex);
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        LevelEvent event = client.getLevel().entityManager.events.get(this.eventUniqueID, false);
        if (event instanceof TicTacToeLevelEvent) {
            ((TicTacToeLevelEvent)event).onClientPickedIndex(client, this.tileIndex);
        } else if (event != null && event.getID() != -1) {
            client.sendPacket(new PacketLevelEvent(event));
        } else {
            client.sendPacket(new PacketLevelEventOver(this.eventUniqueID));
        }
    }
}

