/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.modLoader.ModLoader;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.Server;
import necesse.engine.world.WorldSettings;

public class PacketServerStatus
extends Packet {
    public final int state;
    public final long uniqueID;
    public final int playersOnline;
    public final int slots;
    public final boolean passwordProtected;
    public final int modsHash;
    public final String version;
    public final WorldSettings worldSettings;

    public PacketServerStatus(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.state = reader.getNextInt();
        this.uniqueID = reader.getNextLong();
        this.playersOnline = reader.getNextByteUnsigned();
        this.slots = reader.getNextByteUnsigned();
        this.passwordProtected = reader.getNextBoolean();
        this.modsHash = reader.getNextInt();
        this.version = reader.getNextString();
        this.worldSettings = this.version.equals("1.0.1") ? new WorldSettings(null, reader, true) : null;
    }

    public PacketServerStatus(Server server, int state) {
        this.state = state;
        this.uniqueID = server.world.getUniqueID();
        this.playersOnline = server.getPlayersOnline();
        this.slots = server.getSlots();
        this.passwordProtected = server.getSettings().password != null && !server.getSettings().password.isEmpty();
        this.modsHash = ModLoader.getModsHash();
        this.version = "1.0.1";
        this.worldSettings = server.world.settings;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(state);
        writer.putNextLong(this.uniqueID);
        writer.putNextByteUnsigned(this.playersOnline);
        writer.putNextByteUnsigned(this.slots);
        writer.putNextBoolean(this.passwordProtected);
        writer.putNextInt(this.modsHash);
        writer.putNextString(this.version);
        this.worldSettings.setupBeforeConnectedPacket(writer);
    }
}

