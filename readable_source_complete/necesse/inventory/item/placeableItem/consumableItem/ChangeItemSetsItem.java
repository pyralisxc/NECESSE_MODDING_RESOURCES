/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.placeableItem.consumableItem;

import java.awt.geom.Line2D;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.packet.PacketUpdateTotalItemSets;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.placeableItem.consumableItem.ConsumableItem;
import necesse.level.maps.Level;

public class ChangeItemSetsItem
extends ConsumableItem {
    public int totalSets;

    public ChangeItemSetsItem(int totalSets) {
        super(1, true);
        this.totalSets = totalSets;
        this.worldDrawSize = 32;
    }

    @Override
    public InventoryItem onPlace(Level level, int x, int y, PlayerMob player, int seed, InventoryItem item, GNDItemMap mapContent) {
        if (level.isServer()) {
            player.getInv().equipment.changeTotalItemSets(this.totalSets);
            player.equipmentBuffManager.updateAll();
            ServerClient serverClient = player.getServerClient();
            serverClient.closeContainer(false);
            serverClient.updateInventoryContainer();
            level.getServer().network.sendToAllClients(new PacketUpdateTotalItemSets(serverClient));
        }
        if (this.isSingleUse(player)) {
            item.setAmount(item.getAmount() - 1);
        }
        return item;
    }

    @Override
    public String canPlace(Level level, int x, int y, PlayerMob player, Line2D playerPositionLine, InventoryItem item, GNDItemMap mapContent) {
        if (player.getInv().equipment.getTotalSets() >= this.totalSets) {
            return "incorrectslots";
        }
        return null;
    }

    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "consumetip"));
        return tooltips;
    }
}

