/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.PlayerMob;

public class PacketPlayerHotbarLocked
extends Packet {
    public final int slot;
    public final boolean hotbarLocked;

    public PacketPlayerHotbarLocked(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.slot = reader.getNextByteUnsigned();
        this.hotbarLocked = reader.getNextBoolean();
    }

    public PacketPlayerHotbarLocked(int slot, boolean hotbarLocked) {
        this.slot = slot;
        this.hotbarLocked = hotbarLocked;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextByteUnsigned(slot);
        writer.putNextBoolean(hotbarLocked);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        PlayerMob player = client.getPlayer(this.slot);
        if (player != null) {
            player.hotbarLocked = this.hotbarLocked;
        }
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        if (client.playerMob != null && client.slot == this.slot) {
            client.playerMob.hotbarLocked = this.hotbarLocked;
            server.network.sendToAllClientsExcept(this, client);
        }
    }
}

