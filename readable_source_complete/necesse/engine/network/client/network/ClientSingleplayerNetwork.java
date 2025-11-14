/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.client.network;

import java.util.function.BiConsumer;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.network.ClientNetwork;

public class ClientSingleplayerNetwork
extends ClientNetwork {
    private final Client client;
    private boolean isOpen;

    public ClientSingleplayerNetwork(Client client) {
        this.client = client;
    }

    @Override
    public boolean openConnection() {
        this.isOpen = true;
        return true;
    }

    @Override
    public String getOpenError() {
        return null;
    }

    @Override
    public boolean isOpen() {
        return this.isOpen;
    }

    @Override
    public void sendPacket(Packet packet) {
        NetworkPacket networkPacket = new NetworkPacket(packet, null);
        this.client.packetManager.submitOutPacket(networkPacket);
        this.client.submitSinglePlayerPacket(this.client.getLocalServer().packetManager, networkPacket);
    }

    @Override
    public void close() {
        this.isOpen = false;
    }

    @Override
    public String getDebugString() {
        return "LOCAL";
    }

    @Override
    public LocalMessage getPlayingMessage() {
        return new LocalMessage("richpresence", "playingsingleplayer");
    }

    @Override
    public String getRichPresenceGroup() {
        return super.getRichPresenceGroup();
    }

    @Override
    public void writeLobbyConnectInfo(BiConsumer<String, String> writer) {
    }
}

