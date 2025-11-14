/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketDisconnect;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class PacketRequestActiveSetBuffAbility
extends Packet {
    public final int slot;

    public PacketRequestActiveSetBuffAbility(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.slot = reader.getNextByteUnsigned();
    }

    public PacketRequestActiveSetBuffAbility(int slot) {
        this.slot = slot;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextByteUnsigned(slot);
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        ServerClient target = server.getClient(this.slot);
        if (target != null) {
            if (target.playerMob != null) {
                target.playerMob.sendActiveSetBuffAbilityState(server, client);
            }
        } else {
            client.sendPacket(new PacketDisconnect(this.slot, PacketDisconnect.Code.MISSING_CLIENT));
        }
    }
}

