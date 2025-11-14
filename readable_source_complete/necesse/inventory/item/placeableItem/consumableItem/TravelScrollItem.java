/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.placeableItem.consumableItem;

import java.awt.geom.Line2D;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.packet.PacketOpenContainer;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.travel.TravelContainer;
import necesse.inventory.container.travel.TravelDir;
import necesse.inventory.item.Item;
import necesse.inventory.item.placeableItem.consumableItem.ConsumableItem;
import necesse.level.maps.Level;

public class TravelScrollItem
extends ConsumableItem {
    public TravelScrollItem() {
        super(100, false);
        this.rarity = Item.Rarity.RARE;
        this.itemCooldownTime.setBaseValue(1000);
        this.worldDrawSize = 32;
    }

    @Override
    public InventoryItem onPlace(Level level, int x, int y, PlayerMob player, int seed, InventoryItem item, GNDItemMap mapContent) {
        if (level.isServer()) {
            ServerClient client = player.getServerClient();
            if (!TravelContainer.canOpen(client)) {
                client.sendChatMessage(new LocalMessage("ui", "travelopeninvalid"));
            } else {
                PacketOpenContainer p = new PacketOpenContainer(ContainerRegistry.TRAVEL_SCROLL_CONTAINER, TravelContainer.getContainerContentPacket(level.getServer(), client, TravelDir.All));
                ContainerRegistry.openAndSendContainer(client, p);
            }
        }
        return item;
    }

    @Override
    public String canAttack(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item) {
        String out = super.canAttack(level, x, y, attackerMob, item);
        if (out != null) {
            return out;
        }
        return !attackerMob.buffManager.hasBuff("teleportsickness") ? null : "";
    }

    @Override
    public String canPlace(Level level, int x, int y, PlayerMob player, Line2D playerPositionLine, InventoryItem item, GNDItemMap mapContent) {
        if (player.buffManager.hasBuff("teleportsickness")) {
            return "teleportsickness";
        }
        return null;
    }

    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "consumetip"));
        tooltips.add(Localization.translate("itemtooltip", "traveltip"));
        return tooltips;
    }

    @Override
    public String getTranslatedTypeName() {
        return Localization.translate("item", "scroll");
    }
}

