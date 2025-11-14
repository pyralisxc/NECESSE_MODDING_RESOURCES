/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.client.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.function.BiConsumer;
import necesse.engine.GameLog;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.UnknownPacketException;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.network.ClientNetwork;
import necesse.engine.network.networkInfo.DatagramNetworkInfo;

public class ClientAddressNetwork
extends ClientNetwork {
    public final Client client;
    public final String addressString;
    public final int port;
    private String openError;
    private InetAddress address;
    private DatagramSocket socket;
    private Thread listenThread;

    public ClientAddressNetwork(Client client, String addressString, int port) {
        this.client = client;
        this.addressString = addressString;
        this.port = port;
    }

    @Override
    public boolean openConnection() {
        try {
            this.socket = new DatagramSocket();
            this.address = InetAddress.getByName(this.addressString);
            this.listenThread = new Thread("Client Socket"){

                @Override
                public void run() {
                    while (ClientAddressNetwork.this.isOpen()) {
                        byte[] data = new byte[1024];
                        DatagramPacket packet = new DatagramPacket(data, data.length);
                        try {
                            ClientAddressNetwork.this.socket.receive(packet);
                            NetworkPacket p = new NetworkPacket(ClientAddressNetwork.this.socket, packet);
                            ClientAddressNetwork.this.client.packetManager.submitInPacket(p);
                        }
                        catch (IOException e) {
                            if (ClientAddressNetwork.this.socket.isClosed()) break;
                            e.printStackTrace();
                        }
                        catch (UnknownPacketException e) {
                            GameLog.warn.println("Client received unknown packet from server " + packet.getAddress().toString() + ":" + packet.getPort());
                        }
                    }
                    if (ClientAddressNetwork.this.isOpen()) {
                        ClientAddressNetwork.this.socket.close();
                    }
                }
            };
            this.listenThread.start();
        }
        catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
            this.openError = e.getMessage();
            return false;
        }
        return true;
    }

    @Override
    public String getOpenError() {
        return this.openError;
    }

    @Override
    public boolean isOpen() {
        return this.socket != null && !this.socket.isClosed();
    }

    @Override
    public void sendPacket(Packet packet) {
        NetworkPacket networkPacket = new NetworkPacket(packet, new DatagramNetworkInfo(this.socket, this.address, this.port));
        this.client.packetManager.submitOutPacket(networkPacket);
        try {
            if (!this.socket.isClosed()) {
                networkPacket.sendPacket();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        if (this.socket != null) {
            this.socket.close();
        }
        if (this.listenThread != null) {
            this.listenThread.interrupt();
        }
    }

    public boolean isLocalAddress() {
        return this.addressString.equals("localhost") || this.address == null || this.address.isSiteLocalAddress();
    }

    @Override
    public String getDebugString() {
        String hostAddress = this.address == null ? "null" : this.address.getHostAddress();
        return this.addressString + ":" + this.port + (hostAddress.equals(this.addressString) ? "" : " (" + hostAddress + ")");
    }

    @Override
    public LocalMessage getPlayingMessage() {
        return new LocalMessage("richpresence", "playingonserver");
    }

    @Override
    public void writeLobbyConnectInfo(BiConsumer<String, String> writer) {
        writer.accept("serverPort", String.valueOf(this.port));
        writer.accept("serverAddress", String.valueOf(this.addressString));
    }
}

