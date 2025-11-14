/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.placeableItem.objectItem;

import java.awt.geom.Line2D;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.world.worldData.SettlementsWorldData;
import necesse.entity.mobs.PlayerMob;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.placeableItem.objectItem.ObjectItem;
import necesse.level.gameObject.ChallengersBannerObject;
import necesse.level.gameObject.ObjectPlaceOption;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.NetworkSettlementData;

public class ChallengersBannerObjectItem
extends ObjectItem {
    public ChallengersBannerObjectItem(ChallengersBannerObject object) {
        super(object);
    }

    @Override
    public String canPlace(Level level, ObjectPlaceOption po, PlayerMob player, Line2D playerPositionLine, InventoryItem item, GNDItemMap mapContent) {
        NetworkSettlementData networkData;
        String error = super.canPlace(level, po, player, playerPositionLine, item, mapContent);
        if (error != null) {
            return error;
        }
        boolean hasSettlement = SettlementsWorldData.getSettlementsData(level).hasSettlementAtTile(level, po.tileX, po.tileY);
        if (!hasSettlement) {
            return "notsettlement";
        }
        if (level.isServer() && player != null && player.isServerClient() && (networkData = SettlementsWorldData.getSettlementsData(level).getNetworkDataAtTile(level.getIdentifier(), po.tileX, po.tileY)) != null && !networkData.doesClientHaveAccess(player.getServerClient())) {
            return "noaccess";
        }
        return null;
    }

    @Override
    public InventoryItem onAttemptPlace(Level level, int x, int y, PlayerMob player, InventoryItem item, GNDItemMap mapContent, String error) {
        if (error.equals("noaccess")) {
            player.getServerClient().sendChatMessage(new LocalMessage("misc", "settlementnoaccess"));
            return item;
        }
        return super.onAttemptPlace(level, x, y, player, item, mapContent, error);
    }
}

