/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobInventory;
import necesse.inventory.Inventory;

public class PacketMobInventory
extends Packet {
    public final int mobUniqueID;
    public final Packet inventoryContent;

    public PacketMobInventory(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.mobUniqueID = reader.getNextInt();
        this.inventoryContent = reader.getNextContentPacket();
    }

    public PacketMobInventory(MobInventory inventory) {
        this.mobUniqueID = ((Mob)((Object)inventory)).getUniqueID();
        this.inventoryContent = inventory.getInventory().getContentPacket();
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(this.mobUniqueID);
        writer.putNextContentPacket(this.inventoryContent);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        if (client.getLevel() == null) {
            return;
        }
        Mob mob = GameUtils.getLevelMob(this.mobUniqueID, client.getLevel());
        if (mob instanceof MobInventory) {
            ((MobInventory)((Object)mob)).getInventory().override(Inventory.getInventory(this.inventoryContent));
        }
    }
}

