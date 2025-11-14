/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.codedisaster.steamworks.SteamID
 *  com.codedisaster.steamworks.SteamNativeHandle
 */
package necesse.engine.platforms.steam.network;

import com.codedisaster.steamworks.SteamID;
import com.codedisaster.steamworks.SteamNativeHandle;
import java.io.File;
import java.io.IOException;
import necesse.engine.GameCache;
import necesse.engine.GlobalData;
import necesse.engine.Settings;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.modLoader.ModNetworkData;
import necesse.engine.network.HostSettingsForm;
import necesse.engine.network.NetworkManager;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.PlatformSubForm;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.network.server.ServerCreationSettings;
import necesse.engine.network.server.ServerHostSettings;
import necesse.engine.network.server.ServerSettings;
import necesse.engine.network.server.network.ServerNetwork;
import necesse.engine.platforms.sharedOnPC.forms.FormJoinServerForm;
import necesse.engine.platforms.steam.SteamData;
import necesse.engine.platforms.steam.forms.FormSteamFriendsInviteList;
import necesse.engine.platforms.steam.forms.FormSteamHostSettings;
import necesse.engine.platforms.steam.forms.JoinSteamFriendForm;
import necesse.engine.platforms.steam.modding.SteamModNetworkData;
import necesse.engine.platforms.steam.network.client.SteamClient;
import necesse.engine.platforms.steam.network.packet.PacketAddSteamInvite;
import necesse.engine.platforms.steam.network.server.SteamServerSettings;
import necesse.engine.platforms.steam.network.server.network.SteamServerOpenNetwork;
import necesse.engine.registries.PacketRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.state.MainMenu;
import necesse.engine.util.ObjectValue;
import necesse.engine.world.FileSystemClosedException;
import necesse.gfx.forms.FormSwitcher;
import necesse.gfx.forms.components.lists.FormGeneralList;

public class SteamNetworkManager
extends NetworkManager {
    @Override
    public boolean allowsHosting() {
        return true;
    }

    @Override
    public boolean allowsFriendJoining() {
        return true;
    }

    @Override
    public boolean allowsServerJoining() {
        return true;
    }

    @Override
    public ServerSettings getDefaultHostSettings(ServerCreationSettings serverCreationSettings) {
        return SteamServerSettings.createHostServerSettings(serverCreationSettings, Settings.serverSlots, Settings.serverPort, SteamServerSettings.SteamLobbyType.Open);
    }

    @Override
    public HostSettingsForm getHostSettingsForm(GameMessage backButtonName, Runnable backButtonPressed, Runnable onHostStarted) {
        return new FormSteamHostSettings(backButtonName, backButtonPressed, onHostStarted);
    }

    @Override
    public FormGeneralList getInviteForm(Client client, int x, int y, int width, int height) {
        return new FormSteamFriendsInviteList((SteamClient)client, 0, 0, width, height);
    }

    @Override
    public PlatformSubForm getJoinFriendForm(MainMenu mainMenu, final Runnable onBackButton) {
        return new JoinSteamFriendForm(400, 400, mainMenu){

            @Override
            public void onBackPressed() {
                onBackButton.run();
            }
        };
    }

    @Override
    public PlatformSubForm getJoinServerForm(FormSwitcher parent, MainMenu mainMenu, final Runnable onBackButton) {
        return new FormJoinServerForm(400, 460, mainMenu){

            @Override
            public void onBackPressed() {
                onBackButton.run();
            }
        };
    }

    public Client startJoinFriendClient(String name, SteamID remoteID) {
        SaveData continueSave = MainMenu.getContinueCacheSaveBase(MainMenu.ContinueMode.JOIN);
        continueSave.addSafeString("name", name);
        continueSave.addLong("remoteID", SteamNativeHandle.getNativeHandle((SteamNativeHandle)remoteID));
        GameCache.cacheSave(continueSave, "continueLast");
        SteamClient client = new SteamClient((TickManager)GlobalData.getCurrentGameLoop(), remoteID, new LocalMessage("ui", "characterlastfriendsworld"));
        client.start();
        System.out.println("Started client connecting to " + client.network.getDebugString() + ", game version " + "1.0.1");
        return client;
    }

    @Override
    public Client startSingleplayerClient(ServerCreationSettings serverCreationSettings) throws IOException, FileSystemClosedException {
        SteamServerSettings settings = SteamServerSettings.SingleplayerServer(serverCreationSettings);
        Server server = new Server(settings);
        server.pauseForSpawnedPlayer = true;
        SteamClient client = new SteamClient((TickManager)GlobalData.getCurrentGameLoop(), server, true);
        server.makeSingleplayer(client);
        server.start(null, true);
        client.start();
        System.out.println("Started singleplayer server on world " + server.world.filePath.getName() + ", game version " + "1.0.1");
        System.out.println("Found " + server.usedNames.size() + " saved players.");
        return client;
    }

    @Override
    public Client startJoinServerClient(String name, String address, int port) {
        SaveData continueSave = MainMenu.getContinueCacheSaveBase(MainMenu.ContinueMode.JOIN);
        continueSave.addSafeString("name", name);
        continueSave.addSafeString("address", address);
        continueSave.addInt("port", port);
        GameCache.cacheSave(continueSave, "continueLast");
        StaticMessage playingOnDisplayName = new StaticMessage(name);
        SteamClient client = new SteamClient(GlobalData.getCurrentGameLoop(), address, port, playingOnDisplayName);
        client.start();
        System.out.println("Started client connecting to " + client.network.getDebugString() + ", game version " + "1.0.1");
        return client;
    }

    @Override
    public ServerSettings getServerSettingsFromSave(File worldFilePath, LoadData data) {
        return SteamServerSettings.createFromSave(worldFilePath, data);
    }

    @Override
    public ObjectValue<GameMessage, Runnable> getMainMenuContinueButtonForJoining(LoadData continueSave, MainMenu mainMenu) {
        String address = continueSave.getSafeString("address", null, false);
        long remoteID = continueSave.getLong("remoteID", -1L, false);
        if (address != null) {
            int port = continueSave.getInt("port", -1);
            if (port != -1) {
                String name = continueSave.getSafeString("name", null, false);
                if (name == null) {
                    name = port == 14159 ? address : address + ":" + port;
                }
                String finalName = name;
                return new ObjectValue<GameMessage, Runnable>(new LocalMessage("ui", "continuejoin", "name", name), () -> mainMenu.startConnection(this.startJoinServerClient(finalName, address, port), null));
            }
        } else if (remoteID != -1L) {
            String name = continueSave.getSafeString("name", null);
            SteamID friendSteamID = SteamID.createFromNativeHandle((long)remoteID);
            String friendName = SteamData.getFriendName(friendSteamID);
            if (friendName != null && !friendName.isEmpty() && !friendName.equals("[unknown]")) {
                return new ObjectValue<GameMessage, Runnable>(new LocalMessage("ui", "continuejoin", "name", name), () -> mainMenu.startConnection(this.startJoinFriendClient(friendName, friendSteamID), null));
            }
        }
        return null;
    }

    @Override
    public NetworkManager.PlatformConnectApprovedData createPlatformConnectApprovedData() {
        return new SteamConnectApprovedData();
    }

    @Override
    public ModNetworkData tryGetModNetworkData(LoadedMod.SaveType type, PacketReader reader) {
        switch (type) {
            case FILE_MOD: 
            case DEV_MOD: {
                return new ModNetworkData(type, reader);
            }
            case STEAM_MOD: {
                return new SteamModNetworkData(type, reader);
            }
        }
        return null;
    }

    @Override
    public ServerNetwork createOpenServerNetwork(Server server, ServerSettings serverSettings) {
        return new SteamServerOpenNetwork(server, serverSettings);
    }

    @Override
    public void startupInstantConnect(String connectTo, MainMenu mainMenu) {
        try {
            long handle = Long.parseLong(connectTo);
            SteamID steamID = SteamID.createFromNativeHandle((long)handle);
            if (!steamID.isValid()) {
                throw new NumberFormatException(steamID + " was not a valid Steam lobby id");
            }
            SteamData.connectLobby(steamID);
        }
        catch (NumberFormatException e) {
            String[] split = connectTo.split(":");
            String address = connectTo;
            int port = 14159;
            if (split.length > 1) {
                address = split[0];
                try {
                    port = Integer.parseInt(split[1]);
                    if (port < 0 || port > 65535) {
                        throw new Exception("Port out of range");
                    }
                }
                catch (Exception ex) {
                    System.err.println("Invalid instant connect port, using default");
                    port = 14159;
                }
            }
            mainMenu.startConnection(this.startJoinServerClient(address, address, port), null);
        }
    }

    @Override
    public void registerPacketAddSteamInvite() {
        PacketRegistry.registerPacket(PacketAddSteamInvite.class);
    }

    @Override
    public Server startServer(ServerSettings settings, ServerHostSettings hostSettings) throws IOException, FileSystemClosedException {
        return null;
    }

    @Override
    public Client startHostClient(ServerSettings settings, ServerHostSettings hostSettings) throws IOException, FileSystemClosedException {
        Server server = new Server(settings);
        server.pauseForSpawnedPlayer = true;
        SteamClient client = new SteamClient((TickManager)GlobalData.getCurrentGameLoop(), server, false);
        server.makeHosted(client);
        server.start(hostSettings, true);
        client.start();
        String passwordString = settings.password == null || settings.password.isEmpty() ? "" : " with password \"" + settings.password + "\"";
        System.out.println("Started hosting using " + server.network.getDebugString() + " with " + server.getSlots() + " slots on world \"" + server.world.filePath.getName() + "\"" + passwordString + ", game version " + "1.0.1" + ".");
        System.out.println("Found " + server.usedNames.size() + " saved players.");
        String address = server.network.getAddress();
        if (address != null) {
            System.out.println("Local address: " + address);
        }
        return client;
    }

    public static class SteamConnectApprovedData
    extends NetworkManager.PlatformConnectApprovedData {
        private SteamServerSettings.SteamLobbyType steamLobbyType;

        @Override
        public void writePlatformData(PacketWriter writer, Server server, ServerClient client) {
            SteamServerSettings.SteamLobbyType steamLobbyType = this.steamLobbyType = SteamData.isCreated() ? ((SteamServerSettings)server.getSettings()).steamLobbyType : null;
            if (this.steamLobbyType != null) {
                writer.putNextBoolean(true);
                writer.putNextByteUnsigned(this.steamLobbyType.ordinal());
            } else {
                writer.putNextBoolean(false);
            }
        }

        @Override
        public void readPlatformData(PacketReader reader) {
            this.steamLobbyType = reader.getNextBoolean() ? SteamServerSettings.SteamLobbyType.values()[reader.getNextByteUnsigned()] : null;
        }

        public SteamServerSettings.SteamLobbyType getSteamLobbyType() {
            return this.steamLobbyType;
        }
    }
}

