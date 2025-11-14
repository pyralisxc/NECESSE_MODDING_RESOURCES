/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketPlayerInventory;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.PlayerMob;

public class PacketSwapInventorySlots
extends Packet {
    public final int itemIDInSlot1;
    public final int itemIDInSlot2;
    public final int slot1;
    public final int slot2;

    public PacketSwapInventorySlots(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.itemIDInSlot1 = reader.getNextShortUnsigned() - 1;
        this.itemIDInSlot2 = reader.getNextShortUnsigned() - 1;
        this.slot1 = reader.getNextShortUnsigned();
        this.slot2 = reader.getNextShortUnsigned();
    }

    public PacketSwapInventorySlots(PlayerMob player, int slot1, int slot2) {
        this(player.getInv().main.getItemID(slot1), player.getInv().main.getItemID(slot2), slot1, slot2);
    }

    public PacketSwapInventorySlots(int itemIDInSlot1, int itemIDInSlot2, int slot1, int slot2) {
        this.itemIDInSlot1 = itemIDInSlot1;
        this.itemIDInSlot2 = itemIDInSlot2;
        this.slot1 = slot1;
        this.slot2 = slot2;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextShortUnsigned(itemIDInSlot1 + 1);
        writer.putNextShortUnsigned(itemIDInSlot2 + 1);
        writer.putNextShortUnsigned(slot1);
        writer.putNextShortUnsigned(slot2);
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        int itemIDInSlot1 = client.playerMob.getInv().main.getItemID(this.slot1);
        int itemIDInSlot2 = client.playerMob.getInv().main.getItemID(this.slot2);
        if (this.itemIDInSlot1 != itemIDInSlot1 || this.itemIDInSlot2 != itemIDInSlot2) {
            client.sendPacket(new PacketPlayerInventory(client));
        } else {
            client.playerMob.getInv().main.swapItems(this.slot1, this.slot2);
        }
    }
}

