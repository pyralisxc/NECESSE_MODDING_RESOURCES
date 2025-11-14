/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.travel;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.container.customAction.ContainerCustomAction;
import necesse.inventory.container.travel.IslandsResponseEvent;
import necesse.inventory.container.travel.TravelContainer;

public class RequestIslandsAction
extends ContainerCustomAction {
    public final TravelContainer travelContainer;

    public RequestIslandsAction(TravelContainer travelContainer) {
        this.travelContainer = travelContainer;
    }

    public void runAndSend(int startX, int startY, int width, int height) {
        Packet content = new Packet();
        PacketWriter writer = new PacketWriter(content);
        writer.putNextInt(startX);
        writer.putNextInt(startY);
        writer.putNextInt(width);
        writer.putNextInt(height);
        this.runAndSendAction(content);
    }

    @Override
    public void executePacket(PacketReader reader) {
        int startX = reader.getNextInt();
        int startY = reader.getNextInt();
        int width = reader.getNextInt();
        int height = reader.getNextInt();
        if (this.travelContainer.client.isServer()) {
            ServerClient client = this.travelContainer.client.getServerClient();
            new IslandsResponseEvent(client.getServer(), client, this.travelContainer, startX, startY, width, height).applyAndSendToClient(client);
        }
    }
}

