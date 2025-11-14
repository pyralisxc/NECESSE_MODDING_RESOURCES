/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketOpenContainer;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ContainerRegistry;
import necesse.inventory.container.travel.TravelContainer;
import necesse.inventory.container.travel.TravelDir;

public class PacketRequestTravel
extends Packet {
    public final TravelDir travelDir;

    public PacketRequestTravel(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        int index = reader.getNextByteUnsigned();
        TravelDir[] values = TravelDir.values();
        this.travelDir = index < 0 || index >= values.length ? null : values[index];
    }

    public PacketRequestTravel(TravelDir travelDir) {
        this.travelDir = travelDir;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextByteUnsigned(travelDir.ordinal());
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        if (client.playerMob == null) {
            return;
        }
        if (!TravelContainer.canOpen(client)) {
            client.sendChatMessage(new LocalMessage("ui", "travelopeninvalid"));
            return;
        }
        boolean validDir = true;
        if (this.travelDir != TravelDir.None) {
            boolean bl = validDir = this.travelDir == TravelContainer.getTravelDir(client.playerMob);
        }
        if (validDir) {
            PacketOpenContainer p = new PacketOpenContainer(ContainerRegistry.TRAVEL_CONTAINER, TravelContainer.getContainerContentPacket(server, client, this.travelDir));
            ContainerRegistry.openAndSendContainer(client, p);
        }
    }
}

