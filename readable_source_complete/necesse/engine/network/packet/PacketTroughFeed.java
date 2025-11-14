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
import necesse.entity.mobs.friendly.FeedingTroughMob;
import necesse.inventory.InventoryItem;

public class PacketTroughFeed
extends Packet {
    public final int mobUniqueID;
    public final Packet itemContent;

    public PacketTroughFeed(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.mobUniqueID = reader.getNextInt();
        this.itemContent = reader.getNextContentPacket();
    }

    public PacketTroughFeed(Mob mob, InventoryItem item) {
        this.mobUniqueID = mob.getUniqueID();
        this.itemContent = InventoryItem.getContentPacket(item);
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(this.mobUniqueID);
        writer.putNextContentPacket(this.itemContent);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        if (client.getLevel() == null) {
            return;
        }
        Mob mob = GameUtils.getLevelMob(this.mobUniqueID, client.getLevel());
        if (mob instanceof FeedingTroughMob) {
            ((FeedingTroughMob)((Object)mob)).onFed(InventoryItem.fromContentPacket(this.itemContent));
        }
    }
}

