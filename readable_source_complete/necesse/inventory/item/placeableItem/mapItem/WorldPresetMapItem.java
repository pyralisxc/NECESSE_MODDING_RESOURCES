/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.placeableItem.mapItem;

import java.awt.geom.Line2D;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.packet.PacketAddMapMarker;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.MapIconRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameMath;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.worldPresets.LevelPresetsRegion;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.travel.TravelDir;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.placeableItem.mapItem.MapItem;
import necesse.level.maps.Level;

public class WorldPresetMapItem
extends MapItem {
    public LevelIdentifier levelIdentifier;
    public String[] targetWorldPresets;
    public int maxTileDistance;
    public String mapIconStringID;
    public GameMessage mapIconName;

    public WorldPresetMapItem(Item.Rarity rarity, LevelIdentifier levelIdentifier, int maxTileDistance, String mapIconStringID, GameMessage mapIconName, String ... targetWorldPresets) {
        super(1);
        this.rarity = rarity;
        this.levelIdentifier = levelIdentifier;
        this.maxTileDistance = maxTileDistance;
        this.mapIconStringID = mapIconStringID;
        this.mapIconName = mapIconName;
        this.targetWorldPresets = targetWorldPresets;
        this.setItemCategory(ItemCategory.craftingManager, "consumable");
    }

    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "presetmaptip"));
        return tooltips;
    }

    @Override
    public String canPlace(Level level, int x, int y, PlayerMob player, Line2D playerPositionLine, InventoryItem item, GNDItemMap mapContent) {
        if (!level.isOneWorldLevel()) {
            return "notoneworld";
        }
        return null;
    }

    @Override
    public InventoryItem onPlace(Level level, int x, int y, PlayerMob player, int seed, InventoryItem item, GNDItemMap mapContent) {
        ServerClient client;
        if (level.isServer() && (client = player.getServerClient()) != null && level.isOneWorldLevel()) {
            Level levelToLookAt = client.getServer().world.getLevel(this.levelIdentifier);
            if (levelToLookAt == null || !levelToLookAt.isOneWorldLevel()) {
                client.sendChatMessage(new LocalMessage("itemtooltip", "mapfail"));
            } else {
                levelToLookAt.executor().submit(() -> {
                    LevelPresetsRegion.FoundPresetData found = client.getServer().world.worldEntity.findClosestWorldPreset(levelToLookAt, client.playerMob.getTileX(), client.playerMob.getTileY(), this.maxTileDistance, data -> {
                        for (String targetWorldPreset : this.targetWorldPresets) {
                            if (!data.getPreset().getStringID().equals(targetWorldPreset)) continue;
                            return !client.isPresetDiscovered(this.levelIdentifier, data.getTileX(), data.getTileY());
                        }
                        return false;
                    });
                    if (found != null) {
                        TravelDir dir = TravelDir.getDeltaDirAngled(client.playerMob.getTileX(), client.playerMob.getTileY(), found.getTileX(), found.getTileY());
                        LocalMessage result = new LocalMessage("itemtooltip", "mapresult");
                        result.addReplacement("dir", dir.dirMessage);
                        float distance = client.playerMob.getDistance(found.getTileX() * 32 + 16, found.getTileY() * 32 + 16);
                        int meters = (int)GameMath.pixelsToMeters(distance);
                        result.addReplacement("coord", meters + "m");
                        client.sendChatMessage(result);
                        if (this.mapIconStringID != null) {
                            GameMessage name = this.mapIconName == null ? new StaticMessage("") : this.mapIconName;
                            client.sendPacket(new PacketAddMapMarker(MapIconRegistry.getIcon(this.mapIconStringID), name, this.levelIdentifier, found.getTileX(), found.getTileY()));
                            client.sendChatMessage(new LocalMessage("itemtooltip", "mapresultplaced"));
                        }
                        client.addDiscoveredPreset(this.levelIdentifier, found.getTileX(), found.getTileY());
                        System.out.println("Found preset " + found.getPreset().getStringID() + " at " + found.getTileX() + ", " + found.getTileY() + " for player " + client.getName());
                    } else {
                        client.sendChatMessage(new LocalMessage("itemtooltip", "mapfail"));
                    }
                });
                client.sendChatMessage(new LocalMessage("itemtooltip", "maplooking"));
            }
        }
        item = super.onPlace(level, x, y, player, seed, item, mapContent);
        return item;
    }

    @Override
    public String getTranslatedTypeName() {
        return Localization.translate("item", "map");
    }
}

