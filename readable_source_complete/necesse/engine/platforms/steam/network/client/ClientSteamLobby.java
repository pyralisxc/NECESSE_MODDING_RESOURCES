/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.codedisaster.steamworks.SteamID
 *  com.codedisaster.steamworks.SteamMatchmaking
 *  com.codedisaster.steamworks.SteamMatchmaking$ChatEntryType
 *  com.codedisaster.steamworks.SteamMatchmaking$ChatMemberStateChange
 *  com.codedisaster.steamworks.SteamMatchmaking$ChatRoomEnterResponse
 *  com.codedisaster.steamworks.SteamMatchmakingCallback
 *  com.codedisaster.steamworks.SteamNativeHandle
 *  com.codedisaster.steamworks.SteamResult
 */
package necesse.engine.platforms.steam.network.client;

import com.codedisaster.steamworks.SteamID;
import com.codedisaster.steamworks.SteamMatchmaking;
import com.codedisaster.steamworks.SteamMatchmakingCallback;
import com.codedisaster.steamworks.SteamNativeHandle;
import com.codedisaster.steamworks.SteamResult;
import java.io.IOException;
import java.net.URL;
import java.util.Scanner;
import java.util.function.Consumer;
import necesse.engine.GameLog;
import necesse.engine.network.client.Client;
import necesse.engine.platforms.steam.SteamData;
import necesse.engine.platforms.steam.network.packet.PacketAddSteamInvite;
import necesse.engine.platforms.steam.network.server.SteamServerSettings;

public class ClientSteamLobby {
    private final Client client;
    private final SteamMatchmaking matchmaking;
    private SteamServerSettings.SteamLobbyType lobbyType;
    private long waitingForLobbyCreate;
    private SteamID lobbySteamID;

    public ClientSteamLobby(final Client client) {
        this.client = client;
        this.matchmaking = new SteamMatchmaking(new SteamMatchmakingCallback(){

            public void onFavoritesListChanged(int ip, int queryPort, int connPort, int appID, int flags, boolean add, int accountID) {
            }

            public void onLobbyInvite(SteamID steamIDUser, SteamID steamIDLobby, long gameID) {
                GameLog.debug.println("onLobbyInvite " + steamIDUser + ", " + steamIDLobby + ", " + gameID);
            }

            public void onLobbyEnter(SteamID steamIDLobby, int chatPermissions, boolean blocked, SteamMatchmaking.ChatRoomEnterResponse response) {
                GameLog.debug.println("onLobbyEnter " + steamIDLobby + ", " + chatPermissions + ", " + blocked + ", " + response);
            }

            public void onLobbyDataUpdate(SteamID steamIDLobby, SteamID steamIDMember, boolean success) {
                GameLog.debug.println("onLobbyDataUpdate " + steamIDLobby + ", " + steamIDMember + ", " + success);
            }

            public void onLobbyChatUpdate(SteamID steamIDLobby, SteamID steamIDUserChanged, SteamID steamIDMakingChange, SteamMatchmaking.ChatMemberStateChange stateChange) {
                if (stateChange == SteamMatchmaking.ChatMemberStateChange.Entered) {
                    GameLog.debug.println("User " + steamIDUserChanged + " entered lobby " + steamIDLobby);
                    if (ClientSteamLobby.this.lobbyType == SteamServerSettings.SteamLobbyType.InviteOnly) {
                        client.network.sendPacket(new PacketAddSteamInvite(steamIDUserChanged));
                    }
                } else {
                    GameLog.debug.println("User " + steamIDUserChanged + " " + stateChange + " lobby " + steamIDLobby);
                }
            }

            public void onLobbyChatMessage(SteamID steamIDLobby, SteamID steamIDUser, SteamMatchmaking.ChatEntryType entryType, int chatID) {
            }

            public void onLobbyGameCreated(SteamID steamIDLobby, SteamID steamIDGameServer, int ip, short port) {
            }

            public void onLobbyMatchList(int lobbiesMatching) {
            }

            public void onLobbyKicked(SteamID steamIDLobby, SteamID steamIDAdmin, boolean kickedDueToDisconnect) {
                GameLog.debug.println("onLobbyKicked " + steamIDLobby + ", " + steamIDAdmin + ", " + kickedDueToDisconnect);
                if (ClientSteamLobby.this.lobbySteamID != null && ClientSteamLobby.this.lobbySteamID.equals((Object)steamIDLobby)) {
                    ClientSteamLobby.this.lobbySteamID = null;
                }
            }

            public void onLobbyCreated(SteamResult result, SteamID steamIDLobby) {
                if (result == SteamResult.OK) {
                    ClientSteamLobby.this.lobbySteamID = steamIDLobby;
                    GameLog.debug.println("Created Steam lobby " + steamIDLobby);
                    if (client.getLocalServer() == null) {
                        client.network.writeLobbyConnectInfo((key, value) -> ClientSteamLobby.this.matchmaking.setLobbyData(ClientSteamLobby.this.lobbySteamID, key, value));
                    } else {
                        GameLog.debug.println("Local game is hosting server");
                        SteamID steamID = SteamData.getSteamID();
                        if (steamID != null) {
                            ClientSteamLobby.this.matchmaking.setLobbyData(ClientSteamLobby.this.lobbySteamID, "serverHostSteamID", String.valueOf(SteamID.getNativeHandle((SteamNativeHandle)steamID)));
                        }
                    }
                } else {
                    GameLog.warn.println("Failed to create Steam lobby: " + result);
                    ClientSteamLobby.this.waitingForLobbyCreate = System.currentTimeMillis() + 5000L;
                }
            }

            public void onFavoritesListAccountsUpdated(SteamResult result) {
            }
        });
    }

    public void createLobby(SteamServerSettings.SteamLobbyType type) {
        if (this.isWaitingForLobbyCreate()) {
            return;
        }
        this.lobbyType = type;
        this.waitingForLobbyCreate = this.matchmaking.createLobby(type.steamLobbyType, 10).isValid() ? System.currentTimeMillis() + 60000L : System.currentTimeMillis() + 5000L;
    }

    public boolean isLobbyCreated() {
        return this.lobbySteamID != null;
    }

    public boolean isWaitingForLobbyCreate() {
        return System.currentTimeMillis() < this.waitingForLobbyCreate;
    }

    protected void leaveLobby() {
        if (this.isLobbyCreated()) {
            this.matchmaking.leaveLobby(this.lobbySteamID);
            GameLog.debug.println("Left lobby " + this.lobbySteamID);
        }
        this.lobbySteamID = null;
    }

    public boolean inviteUser(SteamID steamID) {
        if (this.isLobbyCreated()) {
            return this.matchmaking.inviteUserToLobby(this.lobbySteamID, steamID);
        }
        return false;
    }

    public static void requestPublicIP(Consumer<String> onReceived) {
        Thread thread = new Thread(() -> {
            try (Scanner s = new Scanner(new URL("https://api.ipify.org").openStream(), "UTF-8").useDelimiter("\\A");){
                onReceived.accept(s.next());
            }
            catch (IOException e) {
                onReceived.accept(null);
            }
        }, "Public IP Request");
        thread.start();
    }

    public void dispose() {
        this.leaveLobby();
        this.matchmaking.dispose();
    }
}

