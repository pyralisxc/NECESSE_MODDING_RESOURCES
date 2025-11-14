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

public class PacketCraftUseNearbyInventories
extends Packet {
    public final boolean useNearby;

    public PacketCraftUseNearbyInventories(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.useNearby = reader.getNextBoolean();
    }

    public PacketCraftUseNearbyInventories() {
        this.useNearby = Settings.craftingUseNearby.get();
        PacketWriter writer = new PacketWriter(this);
        writer.putNextBoolean(this.useNearby);
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        client.craftingUsesNearbyInventories = this.useNearby;
    }
}

