/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.commands.PermissionLevel;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;

public class PacketPermissionUpdate
extends Packet {
    public final PermissionLevel permissionLevel;

    public PacketPermissionUpdate(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.permissionLevel = PermissionLevel.getLevel(reader.getNextByteUnsigned());
    }

    public PacketPermissionUpdate(PermissionLevel permissionLevel) {
        this.permissionLevel = permissionLevel;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextByteUnsigned(permissionLevel.getLevel());
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        client.permissionUpdate(this);
    }
}

