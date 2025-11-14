/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.GameLog;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.util.LevelIdentifier;
import necesse.level.maps.Level;

public class PacketLevelGNDData
extends Packet {
    public final LevelIdentifier levelIdentifier;
    public final Packet content;

    public PacketLevelGNDData(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.levelIdentifier = new LevelIdentifier(reader);
        this.content = reader.getNextContentPacket();
    }

    public PacketLevelGNDData(Level level) {
        this.levelIdentifier = level.getIdentifier();
        this.content = new Packet();
        level.gndData.writePacket(new PacketWriter(this.content));
        PacketWriter writer = new PacketWriter(this);
        this.levelIdentifier.writePacket(writer);
        writer.putNextContentPacket(this.content);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        if (client.getLevel() == null) {
            return;
        }
        if (client.getLevel().getIdentifier().equals(this.levelIdentifier)) {
            client.getLevel().gndData.readPacket(new PacketReader(this.content));
        } else {
            GameLog.warn.println("Received level GND data packet for wrong level identifier: " + this.levelIdentifier + ", my level: " + client.getLevel().getIdentifier());
        }
    }
}

