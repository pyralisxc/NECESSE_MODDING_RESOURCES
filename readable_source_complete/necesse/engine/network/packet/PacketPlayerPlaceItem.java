/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientClient;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.placeableItem.PlaceableItem;
import necesse.level.maps.Level;

public class PacketPlayerPlaceItem
extends Packet {
    public final int levelIdentifierHashCode;
    public final int clientSlot;
    public final InventoryItem item;
    public final int attackX;
    public final int attackY;
    public final String error;
    public final GNDItemMap mapContent;

    public PacketPlayerPlaceItem(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.levelIdentifierHashCode = reader.getNextInt();
        this.clientSlot = reader.getNextByteUnsigned();
        this.item = InventoryItem.fromContentPacket(reader);
        this.attackX = reader.getNextInt();
        this.attackY = reader.getNextInt();
        this.error = reader.getNextBoolean() ? reader.getNextString() : null;
        this.mapContent = new GNDItemMap(reader);
    }

    public PacketPlayerPlaceItem(Level level, ServerClient client, InventoryItem item, int x, int y, String error, GNDItemMap mapContent) {
        if (!(item.item instanceof PlaceableItem)) {
            throw new IllegalArgumentException("Item must be PlaceableItem");
        }
        this.levelIdentifierHashCode = level.getIdentifierHashCode();
        this.clientSlot = client.slot;
        this.item = item;
        this.attackX = x;
        this.attackY = y;
        this.error = error;
        this.mapContent = mapContent;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(this.levelIdentifierHashCode);
        writer.putNextByteUnsigned(this.clientSlot);
        InventoryItem.addPacketContent(item, writer);
        writer.putNextInt(this.attackX);
        writer.putNextInt(this.attackY);
        writer.putNextBoolean(error != null);
        if (error != null) {
            writer.putNextString(error);
        }
        mapContent.writePacket(writer);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        if (!client.levelManager.isLevelLoaded(this.levelIdentifierHashCode)) {
            return;
        }
        ClientClient target = client.getClient(this.clientSlot);
        if (target != null && target.isSamePlace(client.getLevel()) && this.item != null && this.item.item instanceof PlaceableItem) {
            PlaceableItem placeableItem = (PlaceableItem)this.item.item;
            if (this.error == null) {
                placeableItem.onOtherPlayerPlace(client.getLevel(), this.attackX, this.attackY, target.playerMob, this.item, this.mapContent);
            } else {
                placeableItem.onOtherPlayerPlaceAttempt(client.getLevel(), this.attackX, this.attackY, target.playerMob, this.item, this.mapContent, this.error);
            }
        }
    }
}

