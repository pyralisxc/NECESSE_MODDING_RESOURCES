/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.Settings;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class PacketTrackNewQuests
extends Packet {
    public final boolean trackNewQuests;

    public PacketTrackNewQuests(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.trackNewQuests = reader.getNextBoolean();
    }

    public PacketTrackNewQuests() {
        this.trackNewQuests = Settings.trackNewQuests.get();
        PacketWriter writer = new PacketWriter(this);
        writer.putNextBoolean(this.trackNewQuests);
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        client.trackNewQuests = this.trackNewQuests;
    }
}

