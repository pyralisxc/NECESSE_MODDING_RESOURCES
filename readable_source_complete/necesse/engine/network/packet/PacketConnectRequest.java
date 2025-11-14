/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.GameAuth;
import necesse.engine.GlobalData;
import necesse.engine.Settings;
import necesse.engine.dlc.DLCProvider;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.modLoader.ModLoader;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketClientInstalledDLC;
import necesse.engine.network.packet.PacketDisconnect;
import necesse.engine.network.packet.PacketModsMismatch;
import necesse.engine.network.packet.PacketRequestPassword;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class PacketConnectRequest
extends Packet {
    public final long auth;
    public final int modHash;
    public final int passwordHash;
    public final String version;
    public final boolean craftingUsesNearbyInventories;
    public final boolean trackNewQuests;
    public final PacketClientInstalledDLC installedDLC;

    public PacketConnectRequest(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.auth = reader.getNextLong();
        this.modHash = reader.getNextInt();
        this.passwordHash = reader.getNextInt();
        this.version = reader.getNextString();
        this.craftingUsesNearbyInventories = reader.getNextBoolean();
        this.trackNewQuests = reader.getNextBoolean();
        this.installedDLC = new PacketClientInstalledDLC(reader.getNextContentPacket().getPacketData());
    }

    public PacketConnectRequest(String password) {
        this.auth = GameAuth.getAuthentication();
        this.modHash = ModLoader.getModsHash();
        this.passwordHash = password.hashCode();
        this.version = "1.0.1";
        this.craftingUsesNearbyInventories = Settings.craftingUseNearby.get();
        this.trackNewQuests = Settings.trackNewQuests.get();
        this.installedDLC = new PacketClientInstalledDLC(-1, DLCProvider.getInstalledDLCs());
        PacketWriter writer = new PacketWriter(this);
        writer.putNextLong(this.auth);
        writer.putNextInt(this.modHash);
        writer.putNextInt(this.passwordHash);
        writer.putNextString(this.version);
        writer.putNextBoolean(this.craftingUsesNearbyInventories);
        writer.putNextBoolean(this.trackNewQuests);
        writer.putNextContentPacket(this.installedDLC);
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        String hostDisplayName;
        String string = hostDisplayName = packet.networkInfo == null ? null : packet.networkInfo.getDisplayName();
        if (this.auth > 0L && this.auth <= 500L && !GlobalData.isDevMode()) {
            server.network.sendPacket(new NetworkPacket(new PacketDisconnect(-1, new StaticMessage("Denied: Invalid dev auth")), packet.networkInfo));
            System.out.println("Denied connection from auth " + this.auth + " (" + hostDisplayName + ") because server is not running as dev");
        } else if (!server.getSettings().password.equals("") && this.passwordHash == 0) {
            server.network.sendPacket(new NetworkPacket(new PacketRequestPassword(server), packet.networkInfo));
            System.out.println("Auth " + this.auth + " (" + hostDisplayName + ") connected with no password");
        } else if (!server.getSettings().password.equals("") && this.passwordHash != server.getSettings().password.hashCode()) {
            server.network.sendPacket(new NetworkPacket(PacketDisconnect.wrongPassword(server), packet.networkInfo));
            System.out.println("Auth " + this.auth + " (" + hostDisplayName + ") connected with wrong password");
        } else if (ModLoader.getModsHash() != this.modHash) {
            server.network.sendPacket(new NetworkPacket(new PacketModsMismatch(), packet.networkInfo));
            System.out.println("Auth " + this.auth + " (" + hostDisplayName + ") connected with wrong mods");
        } else {
            server.addClient(packet.networkInfo, this.auth, this.version, this.craftingUsesNearbyInventories, this.trackNewQuests, this.installedDLC);
        }
    }
}

