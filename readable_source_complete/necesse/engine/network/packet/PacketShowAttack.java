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
import necesse.entity.mobs.PlayerMob;
import necesse.inventory.InventoryItem;

public class PacketShowAttack
extends Packet {
    public final int slot;
    public final float playerX;
    public final float playerY;
    public final int attackX;
    public final int attackY;
    public final int animAttack;
    public final int seed;
    public final Packet itemContent;
    public final GNDItemMap mapContent;
    public final boolean forcePlayerPosition;

    public PacketShowAttack(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.slot = reader.getNextByteUnsigned();
        this.playerX = reader.getNextFloat();
        this.playerY = reader.getNextFloat();
        this.attackX = reader.getNextInt();
        this.attackY = reader.getNextInt();
        this.animAttack = reader.getNextShort();
        this.seed = reader.getNextShortUnsigned();
        this.itemContent = reader.getNextContentPacket();
        this.mapContent = new GNDItemMap(reader);
        this.forcePlayerPosition = reader.getNextBoolean();
    }

    public PacketShowAttack(PlayerMob player, InventoryItem item, int x, int y, int animAttack, int shortSeed, GNDItemMap mapContent) {
        this(player, item, x, y, animAttack, shortSeed, mapContent, false);
    }

    public PacketShowAttack(PlayerMob player, InventoryItem item, int x, int y, int animAttack, int shortSeed, GNDItemMap mapContent, boolean forcePlayerPosition) {
        this.slot = player.getPlayerSlot();
        this.playerX = player.x;
        this.playerY = player.y;
        this.attackX = x;
        this.attackY = y;
        this.animAttack = animAttack;
        this.seed = shortSeed;
        this.itemContent = InventoryItem.getContentPacket(item);
        this.mapContent = mapContent;
        this.forcePlayerPosition = forcePlayerPosition;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextByteUnsigned(this.slot);
        writer.putNextFloat(this.playerX);
        writer.putNextFloat(this.playerY);
        writer.putNextInt(this.attackX);
        writer.putNextInt(this.attackY);
        writer.putNextShort((short)animAttack);
        writer.putNextShortUnsigned(this.seed);
        writer.putNextContentPacket(this.itemContent);
        mapContent.writePacket(writer);
        writer.putNextBoolean(forcePlayerPosition);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        PlayerMob player = client.getPlayer(this.slot);
        if (player != null && player.getLevel() != null) {
            InventoryItem item;
            if (this.forcePlayerPosition || this.slot != client.getSlot()) {
                player.setPos(this.playerX, this.playerY, false);
            }
            if ((item = InventoryItem.fromContentPacket(this.itemContent)) != null) {
                player.showItemAttack(item, this.attackX, this.attackY, this.animAttack, this.seed, this.mapContent);
            }
        }
    }
}

