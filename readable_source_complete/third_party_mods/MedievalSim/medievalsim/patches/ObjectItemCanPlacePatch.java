/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.modLoader.annotations.ModMethodPatch
 *  necesse.engine.network.gameNetworkData.GNDItemMap
 *  necesse.entity.mobs.PlayerMob
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.item.placeableItem.objectItem.ObjectItem
 *  necesse.level.gameObject.ObjectPlaceOption
 *  necesse.level.maps.Level
 *  net.bytebuddy.asm.Advice$Argument
 *  net.bytebuddy.asm.Advice$OnMethodExit
 *  net.bytebuddy.asm.Advice$Return
 *  net.bytebuddy.asm.Advice$This
 */
package medievalsim.patches;

import java.awt.geom.Line2D;
import medievalsim.zones.AdminZonesLevelData;
import medievalsim.zones.ProtectedZone;
import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.entity.mobs.PlayerMob;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.placeableItem.objectItem.ObjectItem;
import necesse.level.gameObject.ObjectPlaceOption;
import necesse.level.maps.Level;
import net.bytebuddy.asm.Advice;

public class ObjectItemCanPlacePatch {

    @ModMethodPatch(target=ObjectItem.class, name="canPlace", arguments={Level.class, ObjectPlaceOption.class, PlayerMob.class, Line2D.class, InventoryItem.class, GNDItemMap.class})
    public static class CanPlace {
        @Advice.OnMethodExit
        static void onExit(@Advice.This ObjectItem objectItem, @Advice.Argument(value=0) Level level, @Advice.Argument(value=1) ObjectPlaceOption po, @Advice.Argument(value=2) PlayerMob player, @Advice.Argument(value=5) GNDItemMap mapContent, @Advice.Return(readOnly=false) String result) {
            ProtectedZone zone;
            AdminZonesLevelData zoneData;
            if (!level.isServer() || po == null) {
                return;
            }
            if (result != null && result.equals("outofrange") && mapContent != null && mapContent.getBoolean("medievalsim_buildmode")) {
                result = null;
            }
            if (result != null) {
                return;
            }
            if (player != null && player.isServerClient() && (zoneData = AdminZonesLevelData.getZoneData(level, false)) != null && (zone = zoneData.getProtectedZoneAt(po.tileX, po.tileY)) != null && !zone.canClientPlace(player.getServerClient(), level)) {
                result = "protectedzone";
            }
        }
    }
}

