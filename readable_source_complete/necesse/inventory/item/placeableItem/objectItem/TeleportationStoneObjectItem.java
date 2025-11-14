/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.placeableItem.objectItem;

import java.awt.geom.Line2D;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.util.GameBlackboard;
import necesse.engine.world.worldData.SettlementsWorldData;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.placeableItem.objectItem.ObjectItem;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectPlaceOption;
import necesse.level.maps.Level;

public class TeleportationStoneObjectItem
extends ObjectItem {
    public TeleportationStoneObjectItem(GameObject object) {
        super(object);
    }

    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "teleportationstonetip"));
        tooltips.add(Localization.translate("itemtooltip", "placeinanysettlement"));
        return tooltips;
    }

    @Override
    public String canPlace(Level level, ObjectPlaceOption po, PlayerMob player, Line2D playerPositionLine, InventoryItem item, GNDItemMap mapContent) {
        String error = super.canPlace(level, po, player, playerPositionLine, item, mapContent);
        if (error != null) {
            return error;
        }
        boolean hasSettlement = SettlementsWorldData.getSettlementsData(level).hasSettlementAtTile(level, po.tileX, po.tileY);
        if (!hasSettlement) {
            return "notsettlement";
        }
        return null;
    }
}

