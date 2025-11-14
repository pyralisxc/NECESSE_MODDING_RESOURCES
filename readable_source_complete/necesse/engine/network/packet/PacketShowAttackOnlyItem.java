/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.client.Client;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.packet.PacketShowAttack;
import necesse.entity.mobs.PlayerMob;
import necesse.inventory.InventoryItem;

public class PacketShowAttackOnlyItem
extends PacketShowAttack {
    public PacketShowAttackOnlyItem(byte[] data) {
        super(data);
    }

    public PacketShowAttackOnlyItem(PlayerMob player, InventoryItem item, int x, int y, int animAttack, int shortSeed, GNDItemMap mapContent) {
        super(player, item, x, y, animAttack, shortSeed, mapContent);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        PlayerMob player = client.getPlayer(this.slot);
        if (player != null && player.getLevel() != null) {
            player.setPos(this.playerX, this.playerY, false);
            InventoryItem item = InventoryItem.fromContentPacket(this.itemContent);
            if (item != null) {
                item.item.showAttack(player.getLevel(), this.attackX, this.attackY, player, player.getCurrentAttackHeight(), item, this.animAttack, this.seed, this.mapContent);
            }
        }
    }
}

