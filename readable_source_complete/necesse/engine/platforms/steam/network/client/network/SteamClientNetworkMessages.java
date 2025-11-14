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
package necesse.engine.platforms.steam.network.client.network;

import com.codedisaster.steamworks.SteamID;
import com.codedisaster.steamworks.SteamNativeHandle;
import com.codedisaster.steamworks.SteamNetworkingMessage;
import com.codedisaster.steamworks.SteamNetworkingMessages;
import com.codedisaster.steamworks.SteamNetworkingMessagesCallback;
import java.io.IOException;
import java.util.function.BiConsumer;
import necesse.engine.GameLog;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.UnknownPacketException;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.network.ClientNetwork;
import necesse.engine.platforms.steam.network.networkInfo.SteamNetworkMessagesInfo;

public class SteamClientNetworkMessages
extends ClientNetwork {
    private final Client client;
    public final SteamID remoteID;
    private SteamNetworkingMessages networking;
    private Thread listenThread;

    public SteamClientNetworkMessages(Client client, SteamID remoteID) {
        this.client = client;
        this.remoteID = remoteID;
    }

    @Override
    public boolean openConnection() {
        this.networking = new SteamNetworkingMessages(new SteamNetworkingMessagesCallback(){

            public void onSteamNetworkingMessagesSessionRequest(SteamID steamIDRemote) {
                GameLog.debug.println("onSteamNetworkingMessagesSessionRequest: " + SteamID.getNativeHandle((SteamNativeHandle)steamIDRemote));
                if (SteamClientNetworkMessages.this.remoteID.equals((Object)steamIDRemote) || SteamClientNetworkMessages.this.remoteID.getAccountID() == steamIDRemote.getAccountID()) {
                    SteamClientNetworkMessages.this.networking.acceptSessionWithUser(steamIDRemote);
                    GameLog.debug.println("Accepted client Steam P2P request");
                }
            }

            public void onSteamNetworkingMessagesSessionFailed(SteamID steamIDRemote) {
                GameLog.debug.println("onSteamNetworkingMessagesSessionFailed: " + SteamID.getNativeHandle((SteamNativeHandle)steamIDRemote));
                SteamClientNetworkMessages.this.networking.closeSessionWithUser(steamIDRemote);
            }
        });
        this.networking.closeSessionWithUser(this.remoteID);
        this.listenThread = new Thread("Client Socket"){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public void run() {
                while (SteamClientNetworkMessages.this.isOpen()) {
                    SteamNetworkingMessage[] messages = new SteamNetworkingMessage[32];
                    int received = SteamClientNetworkMessages.this.networking.receiveMessagesOnChannel(0, messages, messages.length);
                    for (int i = 0; i < received; ++i) {
                        SteamNetworkingMessage message = messages[i];
                        try {
                            if (message.remoteSteamID.equals((Object)SteamClientNetworkMessages.this.remoteID)) {
                                try {
                                    byte[] data = new byte[message.size];
                                    message.data.position(0);
                                    message.data.get(data);
                                    NetworkPacket p = new NetworkPacket(new SteamNetworkMessagesInfo(SteamClientNetworkMessages.this.networking, SteamClientNetworkMessages.this.remoteID), data);
                                    ((SteamClientNetworkMessages)SteamClientNetworkMessages.this).client.packetManager.submitInPacket(p);
                                }
                                catch (UnknownPacketException e) {
                                    GameLog.warn.println("Client received unknown Steam P2P packet from ID: " + SteamID.getNativeHandle((SteamNativeHandle)message.remoteSteamID) + " (" + e.getMessage() + ")");
                                }
                                continue;
                            }
                            GameLog.warn.println("Client received Steam P2P packet from unknown ID: " + SteamID.getNativeHandle((SteamNativeHandle)message.remoteSteamID) + ", expected ID: " + SteamID.getNativeHandle((SteamNativeHandle)SteamClientNetworkMessages.this.remoteID));
                            continue;
                        }
                        finally {
                            message.free();
                        }
                    }
                    try {
                        Thread.sleep(2L);
                    }
                    catch (InterruptedException interruptedException) {}
                }
            }
        };
        this.listenThread.start();
        return true;
    }

    @Override
    public String getOpenError() {
        return null;
    }

    @Override
    public boolean isOpen() {
        return this.networking != null;
    }

    @Override
    public void sendPacket(Packet packet) {
        if (this.networking == null) {
            GameLog.warn.println("Tried to send packet on disposed SteamNetwork");
            return;
        }
        NetworkPacket networkPacket = new NetworkPacket(packet, new SteamNetworkMessagesInfo(this.networking, this.remoteID));
        this.client.packetManager.submitOutPacket(networkPacket);
        try {
            networkPacket.sendPacket();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        if (this.listenThread != null) {
            this.listenThread.interrupt();
        }
        if (this.networking != null) {
            this.networking.dispose();
        }
        this.networking = null;
    }

    @Override
    public String getDebugString() {
        return "STEAM:" + SteamID.getNativeHandle((SteamNativeHandle)this.remoteID);
    }

    @Override
    public LocalMessage getPlayingMessage() {
        return new LocalMessage("richpresence", "playingwithfriends");
    }

    @Override
    public String getRichPresenceGroup() {
        return this.remoteID.toString();
    }

    @Override
    public void writeLobbyConnectInfo(BiConsumer<String, String> writer) {
        writer.accept("serverHostSteamID", String.valueOf(SteamID.getNativeHandle((SteamNativeHandle)this.remoteID)));
    }
}

