/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.codedisaster.steamworks.SteamID
 */
package necesse.engine.platforms.steam.network.client;

import com.codedisaster.steamworks.SteamID;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import necesse.engine.GameLog;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.message.DuplicateRichPresenceKeyException;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketConnectApproved;
import necesse.engine.network.server.Server;
import necesse.engine.platforms.steam.SteamData;
import necesse.engine.platforms.steam.network.SteamNetworkManager;
import necesse.engine.platforms.steam.network.client.ClientSteamLobby;
import necesse.engine.platforms.steam.network.client.network.SteamClientNetworkMessages;
import necesse.engine.platforms.steam.network.client.network.SteamClientSingleplayerNetwork;
import necesse.engine.platforms.steam.network.server.SteamServerSettings;
import necesse.entity.mobs.PlayerMob;

public class SteamClient
extends Client {
    private ClientSteamLobby steamLobby;
    private SteamServerSettings.SteamLobbyType steamLobbyType;

    public SteamClient(TickManager tickManager, String address, int port, GameMessage playingOnDisplayName) {
        super(tickManager, address, port, playingOnDisplayName);
    }

    public SteamClient(TickManager tickManager, Server server, boolean isSingleplayer) {
        super(tickManager, server, isSingleplayer);
        this.network = new SteamClientSingleplayerNetwork(this);
    }

    public SteamClient(TickManager tickManager, SteamID remoteID, GameMessage playingOnDisplayName) {
        super(tickManager);
        this.network = new SteamClientNetworkMessages(this, remoteID);
        this.playingOnDisplayName = playingOnDisplayName;
    }

    @Override
    public void submitConnectionPacket(PacketConnectApproved p) {
        super.submitConnectionPacket(p);
        SteamNetworkManager.SteamConnectApprovedData steamConnectApprovedData = (SteamNetworkManager.SteamConnectApprovedData)p.platformConnectApprovedData;
        this.steamLobbyType = steamConnectApprovedData.getSteamLobbyType();
        if (this.steamLobby != null) {
            this.steamLobby.dispose();
        }
        this.steamLobby = null;
        if (!this.isSingleplayer() && this.steamLobbyType != null) {
            this.steamLobby = new ClientSteamLobby(this);
            this.steamLobby.createLobby(this.steamLobbyType);
        }
    }

    @Override
    public void startedHosting(Server server) {
        super.startedHosting(server);
        this.network = new SteamClientSingleplayerNetwork(this);
        SteamServerSettings serverSettings = (SteamServerSettings)server.getSettings();
        this.steamLobbyType = serverSettings.steamLobbyType;
        if (this.steamLobby != null) {
            this.steamLobby.dispose();
        }
        this.steamLobby = null;
        if (!this.isSingleplayer() && this.steamLobbyType != null) {
            this.steamLobby = new ClientSteamLobby(this);
            this.steamLobby.createLobby(this.steamLobbyType);
        }
        this.updateSteamRichPresence();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.hasDisconnected || this.disconnectCalled) {
            return;
        }
        if (this.steamLobby != null && !this.steamLobby.isLobbyCreated() && !this.steamLobby.isWaitingForLobbyCreate()) {
            this.steamLobby.createLobby(this.steamLobbyType);
            GameLog.debug.println("Creating lobby again");
        }
    }

    public boolean inviteToSteamLobby(SteamID steamID) {
        if (this.steamLobby != null) {
            return this.steamLobby.inviteUser(steamID);
        }
        return false;
    }

    @Override
    protected void privateDisconnect(boolean sendPacket) {
        if (this.steamLobby != null) {
            this.steamLobby.dispose();
        }
        super.privateDisconnect(sendPacket);
    }

    public void updateSteamRichPresence() {
        LocalMessage playingMessage;
        PlayerMob me = this.getPlayer();
        if (me != null && me.getLevel() != null && (playingMessage = this.network.getPlayingMessage()) != null) {
            try {
                playingMessage.addReplacement("location", me.getLevel().getLocationMessage(me.getTileX(), me.getTileY()));
                HashMap<String, String> keys = new HashMap<String, String>();
                String steamDisplay = playingMessage.setSteamRichPresence(keys, null, 0);
                for (Map.Entry<String, String> entry : keys.entrySet()) {
                    SteamData.setRichPresence(entry.getKey(), entry.getValue());
                }
                SteamData.setRichPresence("steam_display", steamDisplay);
            }
            catch (DuplicateRichPresenceKeyException e) {
                GameLog.debug.println(e.getMessage());
                SteamData.setRichPresence("location", "#richpresence_unknownlocation");
                SteamData.setRichPresence("steam_display", "#" + playingMessage.category + "_" + playingMessage.key);
            }
            String group = this.network.getRichPresenceGroup();
            if (group != null) {
                SteamData.setRichPresence("steam_player_group", group);
                long connectedPlayers = Arrays.stream(this.players).filter(Objects::nonNull).count();
                SteamData.setRichPresence("steam_player_group_size", String.valueOf(connectedPlayers));
            }
            return;
        }
        SteamData.clearRichPresence();
    }

    public SteamServerSettings.SteamLobbyType getSteamLobbyType() {
        return this.steamLobbyType;
    }
}

