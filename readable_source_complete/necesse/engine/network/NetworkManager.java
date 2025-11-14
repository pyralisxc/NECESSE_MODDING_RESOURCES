/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network;

import java.io.File;
import java.io.IOException;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.modLoader.ModNetworkData;
import necesse.engine.network.HostSettingsForm;
import necesse.engine.network.Packet;
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
import necesse.engine.registries.PacketRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.state.MainMenu;
import necesse.engine.util.ObjectValue;
import necesse.engine.world.FileSystemClosedException;
import necesse.gfx.forms.FormSwitcher;
import necesse.gfx.forms.components.lists.FormGeneralList;

public abstract class NetworkManager {
    public abstract boolean allowsHosting();

    public abstract ServerSettings getDefaultHostSettings(ServerCreationSettings var1);

    public abstract HostSettingsForm getHostSettingsForm(GameMessage var1, Runnable var2, Runnable var3);

    public abstract boolean allowsFriendJoining();

    public abstract FormGeneralList getInviteForm(Client var1, int var2, int var3, int var4, int var5);

    public abstract boolean allowsServerJoining();

    public abstract PlatformSubForm getJoinFriendForm(MainMenu var1, Runnable var2);

    public abstract PlatformSubForm getJoinServerForm(FormSwitcher var1, MainMenu var2, Runnable var3);

    public abstract Client startHostClient(ServerSettings var1, ServerHostSettings var2) throws IOException, FileSystemClosedException;

    public abstract Client startSingleplayerClient(ServerCreationSettings var1) throws IOException, FileSystemClosedException;

    public abstract Client startJoinServerClient(String var1, String var2, int var3);

    public abstract void startupInstantConnect(String var1, MainMenu var2);

    public abstract ObjectValue<GameMessage, Runnable> getMainMenuContinueButtonForJoining(LoadData var1, MainMenu var2);

    public abstract ServerSettings getServerSettingsFromSave(File var1, LoadData var2);

    public abstract PlatformConnectApprovedData createPlatformConnectApprovedData();

    public abstract ModNetworkData tryGetModNetworkData(LoadedMod.SaveType var1, PacketReader var2);

    public abstract ServerNetwork createOpenServerNetwork(Server var1, ServerSettings var2);

    public void registerPacketAddSteamInvite() {
        PacketRegistry.registerPacket(UnsupportedPacketAddSteamInvite.class);
    }

    public abstract Server startServer(ServerSettings var1, ServerHostSettings var2) throws IOException, FileSystemClosedException;

    static class UnsupportedPacketAddSteamInvite
    extends Packet {
        public UnsupportedPacketAddSteamInvite(byte[] data) {
            super(data);
        }
    }

    public static abstract class PlatformConnectApprovedData {
        public abstract void writePlatformData(PacketWriter var1, Server var2, ServerClient var3);

        public abstract void readPlatformData(PacketReader var1);
    }
}

