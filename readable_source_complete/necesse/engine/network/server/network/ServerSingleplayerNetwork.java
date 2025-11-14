/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.server.network;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.client.Client;
import necesse.engine.network.networkInfo.InvalidNetworkInfo;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.network.ServerNetwork;

public class ServerSingleplayerNetwork
extends ServerNetwork {
    private boolean isOpen;

    public ServerSingleplayerNetwork(Server server) {
        super(server);
    }

    @Override
    public void open() {
        this.isOpen = true;
    }

    @Override
    public boolean isOpen() {
        return this.isOpen;
    }

    @Override
    public String getAddress() {
        return null;
    }

    @Override
    public void sendPacket(NetworkPacket packet) {
        this.server.packetManager.submitOutPacket(packet);
        Client c = this.server.getLocalClient();
        if (c != null && packet.networkInfo == null) {
            c.submitSinglePlayerPacket(c.packetManager, packet);
        } else if (!(packet.networkInfo instanceof InvalidNetworkInfo)) {
            System.err.println("Tried to send singleplayer packet to invalid network: " + packet.networkInfo.getDisplayName());
        }
    }

    @Override
    public void close() {
        this.isOpen = false;
    }

    @Override
    public String getDebugString() {
        return "LOCAL";
    }
}

