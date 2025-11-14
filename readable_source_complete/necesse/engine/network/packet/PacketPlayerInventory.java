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
import necesse.engine.network.client.ClientClient;
import necesse.engine.network.packet.PacketRequestPlayerData;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class PacketPlayerInventory
extends Packet {
    public final int slot;
    public final Packet inventoryContent;

    public PacketPlayerInventory(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.slot = reader.getNextByteUnsigned();
        this.inventoryContent = reader.getNextContentPacket();
    }

    public PacketPlayerInventory(ServerClient client) {
        this.slot = client.slot;
        this.inventoryContent = new Packet();
        client.playerMob.getInv().setupContentPacket(new PacketWriter(this.inventoryContent));
        PacketWriter writer = new PacketWriter(this);
        writer.putNextByteUnsigned(this.slot);
        writer.putNextContentPacket(this.inventoryContent);
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        if (client.getPermissionLevel().getLevel() < PermissionLevel.ADMIN.getLevel()) {
            return;
        }
        if (!server.world.settings.cheatsAllowedOrHidden()) {
            return;
        }
        if (this.slot != client.slot || !client.checkHasRequestedSelf()) {
            return;
        }
        client.playerMob.applyInventoryPacket(this);
        server.network.sendToAllClientsExcept(this, client);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        ClientClient target = client.getClient(this.slot);
        if (target == null) {
            client.network.sendPacket(new PacketRequestPlayerData(this.slot));
        } else {
            target.applyInventoryPacket(this);
        }
    }
}

