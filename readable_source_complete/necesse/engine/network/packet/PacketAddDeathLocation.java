/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.util.LevelDeathLocation;

public class PacketAddDeathLocation
extends Packet {
    public final LevelDeathLocation location;

    public PacketAddDeathLocation(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        int secondsSince = reader.getNextInt();
        int x = reader.getNextInt();
        int y = reader.getNextInt();
        this.location = new LevelDeathLocation(secondsSince, x, y);
    }

    public PacketAddDeathLocation(LevelDeathLocation location) {
        this.location = location;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(location.secondsSince);
        writer.putNextInt(location.x);
        writer.putNextInt(location.y);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        if (client.getLevel() == null) {
            return;
        }
        client.levelManager.addDeathLocation(this.location);
    }
}

