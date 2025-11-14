/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.ItemInteractAction;

public class PacketShowItemMobInteract
extends Packet {
    public final int slot;
    public final float playerX;
    public final float playerY;
    public final int attackX;
    public final int attackY;
    public final int mobUniqueID;
    public final int seed;
    public final Packet itemContent;
    public final GNDItemMap mapContent;

    public PacketShowItemMobInteract(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.slot = reader.getNextByteUnsigned();
        this.playerX = reader.getNextFloat();
        this.playerY = reader.getNextFloat();
        this.attackX = reader.getNextInt();
        this.attackY = reader.getNextInt();
        this.mobUniqueID = reader.getNextInt();
        this.seed = reader.getNextShortUnsigned();
        this.itemContent = reader.getNextContentPacket();
        this.mapContent = new GNDItemMap(reader);
    }

    public PacketShowItemMobInteract(PlayerMob player, InventoryItem item, int x, int y, Mob mob, int shortSeed, GNDItemMap mapContent) {
        this.slot = player.getPlayerSlot();
        this.playerX = player.x;
        this.playerY = player.y;
        this.attackX = x;
        this.attackY = y;
        this.mobUniqueID = mob.getUniqueID();
        this.seed = shortSeed;
        this.itemContent = InventoryItem.getContentPacket(item);
        this.mapContent = mapContent;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextByteUnsigned(this.slot);
        writer.putNextFloat(this.playerX);
        writer.putNextFloat(this.playerY);
        writer.putNextInt(this.attackX);
        writer.putNextInt(this.attackY);
        writer.putNextInt(this.mobUniqueID);
        writer.putNextShortUnsigned(this.seed);
        writer.putNextContentPacket(this.itemContent);
        mapContent.writePacket(writer);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        PlayerMob player = client.getPlayer(this.slot);
        if (player != null && player.getLevel() != null) {
            player.setPos(this.playerX, this.playerY, false);
            Mob mob = client.getLevel().entityManager.mobs.get(this.mobUniqueID, false);
            InventoryItem item = InventoryItem.fromContentPacket(this.itemContent);
            if (item != null && item.item instanceof ItemInteractAction) {
                player.showItemMobInteract((ItemInteractAction)((Object)item.item), item, this.attackX, this.attackY, mob, this.seed, this.mapContent);
            }
        }
    }
}

