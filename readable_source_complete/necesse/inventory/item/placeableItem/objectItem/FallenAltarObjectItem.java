/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.placeableItem.objectItem;

import necesse.engine.network.gameNetworkData.GNDAltarDataItem;
import necesse.engine.network.gameNetworkData.GNDItem;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.IncursionPerksRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.FallenAltarObjectEntity;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.placeableItem.objectItem.ObjectItem;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.incursion.AltarData;

public class FallenAltarObjectItem
extends ObjectItem {
    public FallenAltarObjectItem(GameObject object) {
        super(object);
    }

    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        AltarData altarData = FallenAltarObjectItem.getAltarData(item.getGndData());
        if (altarData != null && altarData.obtainedPerkIDs != null) {
            for (Integer obtainedPerkID : altarData.obtainedPerkIDs) {
                tooltips.add(IncursionPerksRegistry.getPerkStringID(obtainedPerkID) + " : Tier " + IncursionPerksRegistry.getPerk((int)obtainedPerkID.intValue()).tier);
            }
        }
        return tooltips;
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
        return me.getGndData().sameKeys(them.getGndData(), "altarData");
    }

    @Override
    public boolean onPlaceObject(GameObject object, Level level, int layerID, int tileX, int tileY, int rotation, ServerClient client, InventoryItem item) {
        AltarData altarData;
        FallenAltarObjectEntity objectEntity;
        boolean success = super.onPlaceObject(object, level, layerID, tileX, tileY, rotation, client, item);
        if (success && (objectEntity = level.entityManager.getObjectEntity(tileX, tileY, FallenAltarObjectEntity.class)) != null && (altarData = FallenAltarObjectItem.getAltarData(item.getGndData())) != null) {
            objectEntity.altarData = altarData.makeCopy();
        }
        return success;
    }

    public static InventoryItem setupAltarItem(InventoryItem item, AltarData altarData) {
        GNDItemMap gndData = item.getGndData();
        FallenAltarObjectItem.setupAltarGNDData(gndData, altarData);
        return item;
    }

    public static GNDItemMap altarGNDData(AltarData altarData) {
        GNDItemMap out = new GNDItemMap();
        FallenAltarObjectItem.setupAltarGNDData(out, altarData);
        return out;
    }

    public static void setupAltarGNDData(GNDItemMap gndData, AltarData altarData) {
        gndData.setItem("altarData", (GNDItem)new GNDAltarDataItem(altarData.makeCopy()));
    }

    public static AltarData getAltarData(GNDItemMap gndData) {
        GNDItem altarData = gndData.getItem("altarData");
        if (altarData instanceof GNDAltarDataItem) {
            return ((GNDAltarDataItem)altarData).altarData;
        }
        return null;
    }
}

