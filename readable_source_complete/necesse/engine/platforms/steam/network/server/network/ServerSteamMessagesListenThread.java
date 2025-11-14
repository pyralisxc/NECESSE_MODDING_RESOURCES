/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.codedisaster.steamworks.SteamID
 *  com.codedisaster.steamworks.SteamNativeHandle
 *  com.codedisaster.steamworks.SteamNetworkingMessage
 *  com.codedisaster.steamworks.SteamNetworkingMessages
 *  com.codedisaster.steamworks.SteamNetworkingMessagesCallback
 */
package necesse.engine.platforms.steam.network.server.network;

import com.codedisaster.steamworks.SteamID;
import com.codedisaster.steamworks.SteamNativeHandle;
import com.codedisaster.steamworks.SteamNetworkingMessage;
import com.codedisaster.steamworks.SteamNetworkingMessages;
import com.codedisaster.steamworks.SteamNetworkingMessagesCallback;
import java.util.LinkedList;
import necesse.engine.GameLog;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.UnknownPacketException;
import necesse.engine.network.packet.PacketDisconnect;
import necesse.engine.network.server.network.ServerNetwork;
import necesse.engine.platforms.steam.network.networkInfo.SteamNetworkMessagesInfo;
import necesse.engine.platforms.steam.network.server.SteamServerSettings;
import necesse.engine.platforms.steam.network.server.network.ServerSteamDeniedConnections;
import necesse.engine.platforms.steam.network.server.network.ServerSteamInvitedUsers;

public class ServerSteamMessagesListenThread
extends Thread {
    private static final int requestDeclineTimeout = 5000;
    private final SteamNetworkingMessages steamNetwork;
    private final ServerNetwork serverNetwork;
    private final LinkedList<ConnectRequest> connectRequests = new LinkedList();
    private final ServerSteamDeniedConnections deniedConnections = new ServerSteamDeniedConnections();
    private final ServerSteamInvitedUsers invitedUsers = new ServerSteamInvitedUsers();

    public ServerSteamMessagesListenThread(String name, final ServerNetwork serverNetwork) {
        super(name);
        this.serverNetwork = serverNetwork;
        this.steamNetwork = new SteamNetworkingMessages(new SteamNetworkingMessagesCallback(){

            public void onSteamNetworkingMessagesSessionRequest(SteamID steamIDRemote) {
                PacketDisconnect error;
                switch (((SteamServerSettings)serverNetwork.server.getSettings()).steamLobbyType) {
                    case Open: {
                        error = null;
                        break;
                    }
                    case InviteOnly: {
                        if (ServerSteamMessagesListenThread.this.invitedUsers.isInvited(steamIDRemote)) {
                            error = null;
                            break;
                        }
                        error = new PacketDisconnect(-1, PacketDisconnect.Code.INVITE_ONLY);
                        break;
                    }
                    default: {
                        error = new PacketDisconnect(-1, new StaticMessage("Unknown Steam connection"));
                    }
                }
                if (ServerSteamMessagesListenThread.this.invitedUsers.isInvited(steamIDRemote)) {
                    error = null;
                }
                if (error == null) {
                    ServerSteamMessagesListenThread.this.deniedConnections.removeDeniedUser(steamIDRemote);
                    ServerSteamMessagesListenThread.this.steamNetwork.acceptSessionWithUser(steamIDRemote);
                    GameLog.out.println("Accepted Steam client P2P session request from " + SteamID.getNativeHandle((SteamNativeHandle)steamIDRemote));
                } else {
                    serverNetwork.sendPacket(new NetworkPacket(error, new SteamNetworkMessagesInfo(ServerSteamMessagesListenThread.this.steamNetwork, steamIDRemote)));
                    ServerSteamMessagesListenThread.this.deniedConnections.addDeniedUser(steamIDRemote);
                    ServerSteamMessagesListenThread.this.connectRequests.add(new ConnectRequest(steamIDRemote));
                }
            }

            public void onSteamNetworkingMessagesSessionFailed(SteamID steamIDRemote) {
                GameLog.warn.println("onSteamNetworkingMessagesSessionFailed: " + SteamID.getNativeHandle((SteamNativeHandle)steamIDRemote));
                ServerSteamMessagesListenThread.this.steamNetwork.closeSessionWithUser(steamIDRemote);
            }
        });
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void run() {
        while (this.serverNetwork.isOpen()) {
            while (!this.connectRequests.isEmpty()) {
                ConnectRequest first = this.connectRequests.getFirst();
                if (this.invitedUsers.isInvited(first.steamIDRemote)) {
                    this.deniedConnections.removeDeniedUser(first.steamIDRemote);
                    this.steamNetwork.acceptSessionWithUser(first.steamIDRemote);
                    this.connectRequests.removeFirst();
                    GameLog.debug.println("Accepted new Steam client P2P session request from " + SteamID.getNativeHandle((SteamNativeHandle)first.steamIDRemote));
                    continue;
                }
                if (first.time + 5000L >= System.currentTimeMillis()) continue;
                this.connectRequests.removeFirst();
                GameLog.debug.println("Timed out Steam client P2P session request from " + SteamID.getNativeHandle((SteamNativeHandle)first.steamIDRemote));
            }
            this.deniedConnections.runCleanup(arg_0 -> ((SteamNetworkingMessages)this.steamNetwork).closeSessionWithUser(arg_0));
            SteamNetworkingMessage[] messages = new SteamNetworkingMessage[32];
            int received = this.steamNetwork.receiveMessagesOnChannel(0, messages, messages.length);
            for (int i = 0; i < received; ++i) {
                SteamNetworkingMessage message = messages[i];
                try {
                    if (this.deniedConnections.isDenied(message.remoteSteamID)) continue;
                    byte[] data = new byte[message.size];
                    message.data.position(0);
                    message.data.get(data);
                    NetworkPacket p = new NetworkPacket(new SteamNetworkMessagesInfo(this.steamNetwork, message.remoteSteamID), data);
                    this.serverNetwork.server.packetManager.submitInPacket(p);
                    continue;
                }
                catch (UnknownPacketException e) {
                    GameLog.warn.println("Server received unknown Steam P2P packet from ID: " + SteamID.getNativeHandle((SteamNativeHandle)message.remoteSteamID) + " (" + e.getMessage() + ")");
                    continue;
                }
                finally {
                    message.free();
                }
            }
            try {
                if (received > 0) continue;
                Thread.sleep(2L);
            }
            catch (InterruptedException interruptedException) {}
        }
    }

    @Override
    public void interrupt() {
        super.interrupt();
        this.steamNetwork.dispose();
    }

    public void addInvitedUser(SteamID user) {
        this.invitedUsers.addInvitedUser(user);
        this.steamNetwork.closeSessionWithUser(user);
        this.steamNetwork.acceptSessionWithUser(user);
    }

    private static class ConnectRequest {
        public SteamID steamIDRemote;
        public final long time;

        public ConnectRequest(SteamID steamIDRemote) {
            this.steamIDRemote = steamIDRemote;
            this.time = System.currentTimeMillis();
        }
    }
}

