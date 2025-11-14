/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.GameLog;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.PlayerMob;

public class PacketPlayerInventoryAction
extends Packet {
    public final int slot;
    public final int selectedSlot;
    public final boolean inventoryExtended;
    public final boolean creativeMenuExtended;

    public PacketPlayerInventoryAction(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.slot = reader.getNextByteUnsigned();
        this.selectedSlot = reader.getNextByteUnsigned();
        this.inventoryExtended = reader.getNextBoolean();
        this.creativeMenuExtended = reader.getNextBoolean();
    }

    public PacketPlayerInventoryAction(int slot, PlayerMob player) {
        player.resetUpdateInventoryAction();
        this.slot = slot;
        this.selectedSlot = player.getSelectedSlot();
        this.inventoryExtended = player.isInventoryExtended();
        this.creativeMenuExtended = player.isCreativeMenuExtended();
        PacketWriter writer = new PacketWriter(this);
        writer.putNextByteUnsigned(slot);
        writer.putNextByteUnsigned(this.selectedSlot);
        writer.putNextBoolean(this.inventoryExtended);
        writer.putNextBoolean(this.creativeMenuExtended);
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        if (client.slot == this.slot) {
            client.playerMob.setInventoryExtended(this.inventoryExtended);
            client.playerMob.setCreativeMenuExtended(this.creativeMenuExtended);
            client.playerMob.setSelectedSlot(this.selectedSlot);
            server.network.sendToClientsWithEntityExcept(this, client.playerMob, client);
        } else {
            GameLog.warn.println("Client " + client.getName() + " tried to manipulate wrong slot with inventory action");
        }
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        PlayerMob player = client.getPlayer(this.slot);
        if (player != null) {
            player.setInventoryExtended(this.inventoryExtended);
            player.setCreativeMenuExtended(this.creativeMenuExtended);
            player.setSelectedSlot(this.selectedSlot);
        }
    }
}

