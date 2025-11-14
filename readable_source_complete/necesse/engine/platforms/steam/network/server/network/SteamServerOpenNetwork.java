/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.codedisaster.steamworks.SteamAPI
 *  com.codedisaster.steamworks.SteamID
 */
package necesse.engine.platforms.steam.network.server.network;

import com.codedisaster.steamworks.SteamAPI;
import com.codedisaster.steamworks.SteamID;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import necesse.engine.GameLog;
import necesse.engine.Settings;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.UnknownPacketException;
import necesse.engine.network.networkInfo.DatagramNetworkInfo;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.network.server.ServerSettings;
import necesse.engine.network.server.network.ServerNetwork;
import necesse.engine.platforms.steam.network.server.SteamServerSettings;
import necesse.engine.platforms.steam.network.server.network.ServerSteamMessagesListenThread;

public class SteamServerOpenNetwork
extends ServerNetwork {
    final int port;
    private Thread listenThread;
    private Thread lanListenThread;
    private ServerSteamMessagesListenThread steamMessagesListenThread;
    private DatagramSocket socket;
    private DatagramSocket lanSocket;
    private boolean isOpen;

    public SteamServerOpenNetwork(Server server, ServerSettings serverSettings) {
        super(server);
        this.port = ((SteamServerSettings)serverSettings).port;
    }

    @Override
    public void open() throws IOException {
        SteamServerSettings serverSettings = (SteamServerSettings)this.server.getSettings();
        if (serverSettings.allowConnectByIP) {
            for (int i = 0; i < Settings.lanPorts.length; ++i) {
                try {
                    this.lanSocket = new DatagramSocket(Settings.lanPorts[i]);
                    System.out.println("Started lan socket at port " + Settings.lanPorts[i]);
                    break;
                }
                catch (SocketException e) {
                    if (i != Settings.lanPorts.length - 1) continue;
                    System.err.println("Could not start server LAN socket.");
                    continue;
                }
            }
        }
        try {
            if (serverSettings.allowConnectByIP) {
                InetAddress address;
                if (Settings.bindIP != null) {
                    try {
                        address = InetAddress.getByName(Settings.bindIP);
                        System.out.println("Binding on address " + address.getHostAddress());
                    }
                    catch (UnknownHostException e) {
                        GameLog.warn.println("Could not parse bind IP " + Settings.bindIP);
                        address = null;
                    }
                } else {
                    address = null;
                }
                this.socket = new DatagramSocket(this.port, address);
                this.listenThread = new ServerListenThread("Server Socket", this.socket);
                this.listenThread.start();
                if (this.lanSocket != null) {
                    this.lanListenThread = new ServerListenThread("Server LAN Socket", this.lanSocket);
                    this.lanListenThread.start();
                }
            }
            if (SteamAPI.isSteamRunning()) {
                this.steamMessagesListenThread = new ServerSteamMessagesListenThread("Steam P2P Socket", this);
                this.steamMessagesListenThread.start();
            }
            this.isOpen = true;
        }
        catch (SocketException e) {
            if (this.listenThread != null) {
                this.listenThread.interrupt();
            }
            if (this.lanListenThread != null) {
                this.lanListenThread.interrupt();
            }
            e.printStackTrace();
            this.isOpen = false;
            throw e;
        }
    }

    @Override
    public boolean isOpen() {
        return this.isOpen;
    }

    @Override
    public String getAddress() {
        if (this.socket != null) {
            InetAddress address = this.socket.getLocalAddress();
            if (address == null || address.isAnyLocalAddress()) {
                address = this.socket.getInetAddress();
            }
            if (address == null) {
                try {
                    address = InetAddress.getLocalHost();
                }
                catch (UnknownHostException unknownHostException) {
                    // empty catch block
                }
            }
            if (address == null) {
                return "Unknown:" + this.port;
            }
            return address.getHostAddress() + ":" + this.port;
        }
        if (this.steamMessagesListenThread != null) {
            return "Steam P2P Messages";
        }
        return null;
    }

    @Override
    public void close() {
        if (this.lanSocket != null) {
            this.lanSocket.close();
        }
        if (this.lanListenThread != null) {
            this.lanListenThread.interrupt();
        }
        if (this.socket != null) {
            this.socket.close();
        }
        if (this.listenThread != null) {
            this.listenThread.interrupt();
        }
        if (this.steamMessagesListenThread != null) {
            this.steamMessagesListenThread.interrupt();
        }
        this.isOpen = false;
    }

    @Override
    public void sendPacket(NetworkPacket packet) {
        this.server.packetManager.submitOutPacket(packet);
        if (packet.networkInfo == null) {
            this.server.getLocalClient().submitSinglePlayerPacket(this.server.getLocalClient().packetManager, packet);
            return;
        }
        try {
            packet.sendPacket();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getPort() {
        return this.port;
    }

    public void addInvitedUser(SteamID user) {
        if (this.steamMessagesListenThread != null) {
            this.steamMessagesListenThread.addInvitedUser(user);
        }
        GameLog.debug.println("Added invited user : " + user);
    }

    @Override
    public String getDebugString() {
        return "port " + this.port;
    }

    private class ServerListenThread
    extends Thread {
        private final DatagramSocket socket;

        public ServerListenThread(String name, DatagramSocket socket) {
            super(name);
            this.socket = socket;
            this.setPriority(Math.min(10, this.getPriority() + 1));
        }

        public boolean isSocketOpen() {
            return this.socket != null && !this.socket.isClosed();
        }

        @Override
        public void run() {
            while (this.isSocketOpen()) {
                byte[] data = new byte[1024];
                DatagramPacket packet = new DatagramPacket(data, data.length);
                try {
                    this.socket.receive(packet);
                    NetworkPacket p = new NetworkPacket(this.socket, packet);
                    SteamServerOpenNetwork.this.server.packetManager.submitInPacket(p);
                }
                catch (IOException e) {
                    if (this.socket.isClosed()) break;
                    e.printStackTrace();
                }
                catch (UnknownPacketException e) {
                    DatagramNetworkInfo networkInfo = new DatagramNetworkInfo(this.socket, packet.getAddress(), packet.getPort());
                    ServerClient client = SteamServerOpenNetwork.this.server.getPacketClient(networkInfo);
                    String clientName = client == null ? "N/A" : client.getName();
                    GameLog.warn.println("Server received unknown packet from client " + clientName + ": " + e.getMessage());
                }
            }
            if (this.isSocketOpen()) {
                this.socket.close();
            }
        }
    }
}

