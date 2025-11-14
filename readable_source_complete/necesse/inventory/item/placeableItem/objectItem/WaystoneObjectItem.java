/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.placeableItem.objectItem;

import java.awt.geom.Line2D;
import java.util.ArrayList;
import necesse.engine.GlobalData;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameBlackboard;
import necesse.engine.world.worldData.SettlementsWorldData;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.WaystoneObjectEntity;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.object.HomestoneUpdateEvent;
import necesse.inventory.item.placeableItem.objectItem.ObjectItem;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;
import necesse.level.maps.levelData.settlementData.Waystone;

public class WaystoneObjectItem
extends ObjectItem {
    public WaystoneObjectItem(GameObject object) {
        super(object);
        this.itemCooldownTime.setBaseValue(2000);
    }

    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        GNDItemMap gndData = item.getGndData();
        if (GlobalData.debugActive()) {
            tooltips.add("Settlement uniqueID: " + gndData.getInt("settlementUniqueID"));
        }
        return tooltips;
    }

    @Override
    public String canPlace(Level level, int x, int y, PlayerMob player, Line2D playerPositionLine, InventoryItem item, GNDItemMap mapContent) {
        int settlementUniqueID = item.getGndData().getInt("settlementUniqueID");
        if (settlementUniqueID == 0) {
            return "nosettlementinfo";
        }
        return super.canPlace(level, x, y, player, playerPositionLine, item, mapContent);
    }

    @Override
    public boolean onPlaceObject(GameObject object, Level level, int layerID, int tileX, int tileY, int rotation, ServerClient client, InventoryItem item) {
        GNDItemMap gndData = item.getGndData();
        int settlementUniqueID = gndData.getInt("settlementUniqueID");
        ServerSettlementData settlement = SettlementsWorldData.getSettlementsData(level).getOrLoadServerData(settlementUniqueID);
        LocalMessage registerError = new LocalMessage("ui", "waystoneinvalidhome");
        if (settlement != null) {
            int max = settlement.getMaxWaystones();
            ArrayList<Waystone> waystones = settlement.getWaystones();
            registerError = new LocalMessage("ui", "waystonenoslots");
            if (waystones.size() < max) {
                Waystone newWaystone = new Waystone(level.getIdentifier(), tileX, tileY);
                newWaystone.name = level.getBiome(tileX, tileY).getDisplayName() + " waystone";
                waystones.add(newWaystone);
                settlement.sendEvent(HomestoneUpdateEvent.class);
                registerError = null;
            }
        }
        if (registerError == null) {
            boolean success = super.onPlaceObject(object, level, layerID, tileX, tileY, rotation, client, item);
            if (!success) {
                return false;
            }
            WaystoneObjectEntity waystoneEntity = level.entityManager.getObjectEntity(tileX, tileY, WaystoneObjectEntity.class);
            if (waystoneEntity != null) {
                waystoneEntity.settlementUniqueID = settlementUniqueID;
            }
            return true;
        }
        if (client != null) {
            client.sendChatMessage(registerError);
        }
        return false;
    }

    @Override
    public boolean canCombineItem(Level level, PlayerMob player, InventoryItem me, InventoryItem them, String purpose) {
        if (!super.canCombineItem(level, player, me, them, purpose)) {
            return false;
        }
        return this.isSameGNDData(level, me, them, purpose);
    }

    @Override
    public boolean isSameGNDData(Level level, InventoryItem me, InventoryItem them, String purpose) {
        return me.getGndData().sameKeys(them.getGndData(), "homeX", "homeY");
    }

    public static InventoryItem setupWaystoneItem(InventoryItem item, int settlementUniqueID) {
        GNDItemMap gndData = item.getGndData();
        WaystoneObjectItem.setupWaystoneGNDData(gndData, settlementUniqueID);
        return item;
    }

    public static GNDItemMap waystoneGNDData(int settlementUniqueID) {
        GNDItemMap out = new GNDItemMap();
        WaystoneObjectItem.setupWaystoneGNDData(out, settlementUniqueID);
        return out;
    }

    public static void setupWaystoneGNDData(GNDItemMap gndData, int settlementUniqueID) {
        gndData.setInt("settlementUniqueID", settlementUniqueID);
    }
}

