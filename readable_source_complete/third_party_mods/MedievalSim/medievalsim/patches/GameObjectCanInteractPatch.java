/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.localization.Localization
 *  necesse.engine.modLoader.annotations.ModMethodPatch
 *  necesse.engine.network.server.ServerClient
 *  necesse.engine.util.GameMath
 *  necesse.entity.mobs.PlayerMob
 *  necesse.level.gameObject.GameObject
 *  necesse.level.gameObject.SignObject
 *  necesse.level.gameObject.container.CraftingStationObject
 *  necesse.level.gameObject.container.FueledCraftingStationObject
 *  necesse.level.gameObject.container.InventoryObject
 *  necesse.level.gameObject.furniture.FurnitureObject
 *  necesse.level.maps.Level
 *  net.bytebuddy.asm.Advice$Argument
 *  net.bytebuddy.asm.Advice$OnMethodEnter
 *  net.bytebuddy.asm.Advice$OnNonDefaultValue
 *  net.bytebuddy.asm.Advice$This
 */
package medievalsim.patches;

import medievalsim.zones.AdminZonesLevelData;
import medievalsim.zones.ProtectedZone;
import necesse.engine.localization.Localization;
import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.PlayerMob;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.SignObject;
import necesse.level.gameObject.container.CraftingStationObject;
import necesse.level.gameObject.container.FueledCraftingStationObject;
import necesse.level.gameObject.container.InventoryObject;
import necesse.level.gameObject.furniture.FurnitureObject;
import necesse.level.maps.Level;
import net.bytebuddy.asm.Advice;

@ModMethodPatch(target=GameObject.class, name="canInteract", arguments={Level.class, int.class, int.class, PlayerMob.class})
public class GameObjectCanInteractPatch {
    @Advice.OnMethodEnter(skipOn=Advice.OnNonDefaultValue.class)
    public static Boolean onEnter(@Advice.This GameObject gameObject, @Advice.Argument(value=0) Level level, @Advice.Argument(value=1) int x, @Advice.Argument(value=2) int y, @Advice.Argument(value=3) PlayerMob player) {
        if (!level.isServer() || player == null || !player.isPlayer) {
            return null;
        }
        ServerClient client = player.getServerClient();
        if (client == null) {
            return null;
        }
        int tileX = GameMath.getTileCoordinate((int)x);
        int tileY = GameMath.getTileCoordinate((int)y);
        AdminZonesLevelData zoneData = AdminZonesLevelData.getZoneData(level, false);
        if (zoneData == null) {
            return null;
        }
        ProtectedZone zone = zoneData.getProtectedZoneAt(tileX, tileY);
        if (zone == null) {
            return null;
        }
        if (!zone.canClientInteract(client, level, gameObject)) {
            String messageKey = GameObjectCanInteractPatch.getMessageKeyForObject(gameObject);
            String message = Localization.translate((String)"ui", (String)messageKey);
            client.sendChatMessage(message);
            return false;
        }
        return null;
    }

    private static String getMessageKeyForObject(GameObject gameObject) {
        if (gameObject.isDoor) {
            return "nopermissiondoors";
        }
        if (gameObject instanceof CraftingStationObject || gameObject instanceof FueledCraftingStationObject) {
            return "nopermissionstations";
        }
        if (gameObject instanceof InventoryObject) {
            return "nopermissioncontainers";
        }
        if (gameObject instanceof SignObject) {
            return "nopermissionsigns";
        }
        if (gameObject.isSwitch || gameObject.isPressurePlate) {
            return "nopermissionswitches";
        }
        if (gameObject instanceof FurnitureObject) {
            return "nopermissionfurniture";
        }
        return "nopermissioninteract";
    }
}

