/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.codedisaster.steamworks.SteamMatchmaking$LobbyType
 */
package necesse.engine.platforms.steam.network.server;

import com.codedisaster.steamworks.SteamMatchmaking;
import java.io.File;
import necesse.engine.Settings;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.server.ServerCreationSettings;
import necesse.engine.network.server.ServerSettings;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;

public class SteamServerSettings
extends ServerSettings {
    public int port;
    public boolean allowConnectByIP = true;
    public SteamLobbyType steamLobbyType = SteamLobbyType.Open;

    public SteamServerSettings(ServerCreationSettings serverCreationSettings, int slots, int port, SteamLobbyType steamLobbyType) {
        super(serverCreationSettings, slots);
        this.port = port;
        this.steamLobbyType = steamLobbyType;
    }

    public static SteamServerSettings createHostServerSettings(ServerCreationSettings serverCreationSettings, int slots, int port, SteamLobbyType steamLobbyType) {
        if (serverCreationSettings == null || serverCreationSettings.worldFilePath == null || serverCreationSettings.worldFilePath.getName().isEmpty()) {
            throw new IllegalArgumentException("Invalid world name");
        }
        if (slots < 0 || slots > 250) {
            throw new IllegalArgumentException("Invalid slots");
        }
        if (port < 0 || port > 65535) {
            throw new IllegalArgumentException("Invalid port");
        }
        return new SteamServerSettings(serverCreationSettings, slots, port, steamLobbyType);
    }

    public static SteamServerSettings SingleplayerServer(ServerCreationSettings serverCreationSettings) {
        if (serverCreationSettings == null || serverCreationSettings.worldFilePath == null || serverCreationSettings.worldFilePath.getName().isEmpty()) {
            throw new IllegalArgumentException("Invalid world name");
        }
        return new SteamServerSettings(serverCreationSettings, 1, -1, null);
    }

    public static SteamServerSettings createFromSave(File worldFilePath, LoadData save) {
        int slots = save.getInt("slots", Settings.serverSlots);
        int port = save.getInt("port", Settings.serverPort);
        SteamLobbyType steamLobbyType = save.getEnum(SteamLobbyType.class, "steamLobbyType", SteamLobbyType.Open);
        SteamServerSettings settings = new SteamServerSettings(new ServerCreationSettings(worldFilePath), slots, port, steamLobbyType);
        settings.password = save.getSafeString("password", settings.password);
        settings.allowConnectByIP = save.getBoolean("allowConnectByIP", settings.allowConnectByIP);
        return settings;
    }

    @Override
    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        save.addInt("port", this.port);
        save.addBoolean("allowConnectByIP", this.allowConnectByIP);
        save.addEnum("steamLobbyType", this.steamLobbyType);
    }

    @Override
    public boolean isSinglePlayer() {
        return this.port == -1;
    }

    public static enum SteamLobbyType {
        InviteOnly(SteamMatchmaking.LobbyType.Private, new LocalMessage("ui", "steamlobbyinvite")),
        Open(SteamMatchmaking.LobbyType.FriendsOnly, new LocalMessage("ui", "steamlobbyopen"));

        public final SteamMatchmaking.LobbyType steamLobbyType;
        public final GameMessage displayName;

        private SteamLobbyType(SteamMatchmaking.LobbyType steamLobbyType, GameMessage displayName) {
            this.steamLobbyType = steamLobbyType;
            this.displayName = displayName;
        }
    }
}

