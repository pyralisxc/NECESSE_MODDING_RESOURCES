/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.modLoader.annotations.ModMethodPatch
 *  necesse.engine.network.Packet
 *  necesse.engine.network.gameNetworkData.GNDItemMap
 *  necesse.engine.network.packet.PacketPlaceTile
 *  necesse.engine.util.GameMath
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.mobs.itemAttacker.ItemAttackSlot
 *  necesse.entity.mobs.itemAttacker.ItemAttackerMob
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.item.placeableItem.tileItem.TileItem
 *  necesse.level.gameTile.GameTile
 *  necesse.level.maps.Level
 *  net.bytebuddy.asm.Advice$Argument
 *  net.bytebuddy.asm.Advice$OnMethodEnter
 *  net.bytebuddy.asm.Advice$OnMethodExit
 *  net.bytebuddy.asm.Advice$OnNonDefaultValue
 *  net.bytebuddy.asm.Advice$Return
 *  net.bytebuddy.asm.Advice$This
 */
package medievalsim.patches;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import medievalsim.buildmode.ShapeCalculator;
import medievalsim.util.ModLogger;
import medievalsim.zones.AdminZonesLevelData;
import medievalsim.zones.ProtectedZone;
import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.engine.network.Packet;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.packet.PacketPlaceTile;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.placeableItem.tileItem.TileItem;
import necesse.level.gameTile.GameTile;
import necesse.level.maps.Level;
import net.bytebuddy.asm.Advice;

@ModMethodPatch(target=TileItem.class, name="onAttack", arguments={Level.class, int.class, int.class, ItemAttackerMob.class, int.class, InventoryItem.class, ItemAttackSlot.class, int.class, int.class, GNDItemMap.class})
public class TileItemOnAttackPatch {
    public static final ThreadLocal<InventoryItem> buildModeResult = new ThreadLocal();
    public static final ThreadLocal<Boolean> shouldSkip = new ThreadLocal();

    @Advice.OnMethodEnter(skipOn=Advice.OnNonDefaultValue.class)
    public static Boolean onEnter(@Advice.This TileItem tileItem, @Advice.Argument(value=0) Level level, @Advice.Argument(value=1) int x, @Advice.Argument(value=2) int y, @Advice.Argument(value=3) ItemAttackerMob attackerMob, @Advice.Argument(value=4) int attackHeight, @Advice.Argument(value=5, readOnly=false) InventoryItem item, @Advice.Argument(value=6) ItemAttackSlot slot, @Advice.Argument(value=7) int animAttack, @Advice.Argument(value=8) int seed, @Advice.Argument(value=9) GNDItemMap mapContent) {
        boolean hasBuildMode;
        if (!attackerMob.isPlayer) {
            return null;
        }
        PlayerMob player = (PlayerMob)attackerMob;
        if (level.isServer() && player.getServerClient() != null) {
            ProtectedZone zone;
            int tileX = GameMath.getTileCoordinate((int)x);
            int tileY = GameMath.getTileCoordinate((int)y);
            AdminZonesLevelData zoneData = AdminZonesLevelData.getZoneData(level, false);
            if (zoneData != null && (zone = zoneData.getProtectedZoneAt(tileX, tileY)) != null && !zone.canClientBreak(player.getServerClient(), level)) {
                player.getServerClient().sendChatMessage("You don't have permission to break tiles in this protected zone");
                return null;
            }
        }
        boolean bl = hasBuildMode = mapContent != null && mapContent.getBoolean("medievalsim_buildmode");
        if (!hasBuildMode) {
            return null;
        }
        if (!level.isServer()) {
            InventoryItem clientResult = slot.getItem();
            buildModeResult.set(clientResult);
            shouldSkip.set(true);
            return true;
        }
        try {
            GameTile tile;
            int selectedShape = mapContent.getInt("medievalsim_shape");
            boolean isHollow = mapContent.getBoolean("medievalsim_isHollow");
            int lineLength = mapContent.getInt("medievalsim_lineLength");
            int squareSize = mapContent.getInt("medievalsim_squareSize");
            int circleRadius = mapContent.getInt("medievalsim_circleRadius");
            int spacing = mapContent.getInt("medievalsim_spacing");
            int playerDir = player.isAttacking ? player.beforeAttackDir : player.getDir();
            int centerTileX = GameMath.getTileCoordinate((int)x);
            int centerTileY = GameMath.getTileCoordinate((int)y);
            List<Point> positions = ShapeCalculator.calculatePositions(centerTileX, centerTileY, selectedShape, isHollow, playerDir, lineLength, squareSize, circleRadius, spacing, 1, 1);
            if (positions.size() > 500) {
                ModLogger.warn("Attempted to place %d blocks, limiting to 500", positions.size());
                positions = positions.subList(0, 500);
            }
            if ((tile = tileItem.getTile()) == null) {
                return null;
            }
            int availableItems = item.getAmount();
            ArrayList<Point> validPositions = new ArrayList<Point>();
            for (Point point : positions) {
                if (validPositions.size() >= availableItems) break;
                String string = tile.canPlace(level, point.x, point.y, true);
                if (!level.isTileWithinBounds(point.x, point.y) || level.isProtected(point.x, point.y) || string != null) continue;
                validPositions.add(point);
            }
            if (validPositions.isEmpty()) {
                return null;
            }
            int placedCount = 0;
            for (Point point : validPositions) {
                tile.placeTile(level, point.x, point.y, true);
                level.tileLayer.setIsPlayerPlaced(point.x, point.y, true);
                if (level.isServer()) {
                    level.getServer().network.sendToClientsWithTile((Packet)new PacketPlaceTile(level, player.getServerClient(), tileItem.tileID, point.x, point.y), level, point.x, point.y);
                }
                level.onTilePlaced(tile, point.x, point.y, player.getServerClient());
                level.getLevelTile(point.x, point.y).checkAround();
                level.getLevelObject(point.x, point.y).checkAround();
                ++placedCount;
            }
            if (placedCount > 0) {
                item.setAmount(item.getAmount() - placedCount);
                if (player.getServerClient() != null) {
                    player.getServerClient().newStats.tiles_placed.increment(placedCount);
                }
            }
            player.currentAttackLastPlacePosition = new Point(x, y);
            InventoryItem inventoryItem = item.getAmount() <= 0 ? null : item;
            buildModeResult.set(inventoryItem);
            shouldSkip.set(true);
            return true;
        }
        catch (IllegalArgumentException e) {
            ModLogger.error("Invalid build mode parameters", e);
            return null;
        }
        catch (Exception e) {
            ModLogger.error("Unexpected error in build mode tile placement", e);
            return null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Advice.OnMethodExit(onThrowable=Throwable.class)
    public static void onExit(@Advice.This TileItem tileItem, @Advice.Argument(value=0) Level level, @Advice.Argument(value=6) ItemAttackSlot slot, @Advice.Return(readOnly=false) InventoryItem returnValue) {
        try {
            Boolean skip = shouldSkip.get();
            if (skip != null && skip.booleanValue()) {
                returnValue = buildModeResult.get();
            }
        }
        finally {
            buildModeResult.remove();
            shouldSkip.remove();
        }
    }
}

