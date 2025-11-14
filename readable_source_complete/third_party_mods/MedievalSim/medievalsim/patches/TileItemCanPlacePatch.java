/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.modLoader.annotations.ModMethodPatch
 *  necesse.engine.network.gameNetworkData.GNDItemMap
 *  necesse.engine.util.GameMath
 *  necesse.entity.mobs.PlayerMob
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.item.placeableItem.tileItem.TileItem
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
import necesse.engine.util.GameMath;
import necesse.entity.mobs.PlayerMob;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.placeableItem.tileItem.TileItem;
import necesse.level.maps.Level;
import net.bytebuddy.asm.Advice;

public class TileItemCanPlacePatch {

    @ModMethodPatch(target=TileItem.class, name="canPlace", arguments={Level.class, int.class, int.class, PlayerMob.class, Line2D.class, InventoryItem.class, GNDItemMap.class})
    public static class CanPlace {
        @Advice.OnMethodExit
        static void onExit(@Advice.This TileItem tileItem, @Advice.Argument(value=0) Level level, @Advice.Argument(value=1) int x, @Advice.Argument(value=2) int y, @Advice.Argument(value=3) PlayerMob player, @Advice.Argument(value=6) GNDItemMap mapContent, @Advice.Return(readOnly=false) String result) {
            if (!level.isServer()) {
                return;
            }
            if (result != null && result.equals("outofrange") && mapContent != null && mapContent.getBoolean("medievalsim_buildmode")) {
                result = null;
            }
            if (result != null) {
                return;
            }
            if (player != null && player.isServerClient()) {
                ProtectedZone zone;
                int tileX = GameMath.getTileCoordinate((int)x);
                int tileY = GameMath.getTileCoordinate((int)y);
                AdminZonesLevelData zoneData = AdminZonesLevelData.getZoneData(level, false);
                if (zoneData != null && (zone = zoneData.getProtectedZoneAt(tileX, tileY)) != null && !zone.canClientPlace(player.getServerClient(), level)) {
                    result = "protectedzone";
                }
            }
        }
    }
}

