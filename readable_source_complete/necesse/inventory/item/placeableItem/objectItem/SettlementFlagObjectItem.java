/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.placeableItem.objectItem;

import java.awt.geom.Line2D;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.packet.PacketChangeObject;
import necesse.engine.network.packet.PacketOpenContainer;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.world.worldData.SettlementsWorldData;
import necesse.entity.mobs.PlayerMob;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.SettlementNameContainer;
import necesse.inventory.item.placeableItem.objectItem.ObjectItem;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectPlaceOption;
import necesse.level.gameObject.SettlementFlagObject;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.OneWorldNPCVillageData;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;

public class SettlementFlagObjectItem
extends ObjectItem {
    public SettlementFlagObjectItem(SettlementFlagObject object) {
        super(object);
    }

    @Override
    public String canPlace(Level level, ObjectPlaceOption po, PlayerMob player, Line2D playerPositionLine, InventoryItem item, GNDItemMap mapContent) {
        if (level.isServer() && player != null) {
            int flagTier;
            long current;
            Server server = level.getServer();
            SettlementsWorldData settlementsData = SettlementsWorldData.getSettlementsData(server);
            ServerClient client = player.getServerClient();
            int max = server.world.settings.maxSettlementsPerPlayer;
            if (max >= 0 && (current = settlementsData.streamSettlements().filter(e -> e.getOwnerAuth() == client.authentication && !e.isTileWithinBounds(po.tileX, po.tileY)).count()) >= (long)max) {
                return "maxsettlements";
            }
            ServerSettlementData serverData = settlementsData.getServerDataAtTile(level.getIdentifier(), po.tileX, po.tileY);
            int n = flagTier = serverData != null ? serverData.getFlagTier() : 0;
            if (!settlementsData.canPlaceSettlementFlagAt(level.getIdentifier(), po.tileX, po.tileY, flagTier)) {
                return "closesettlement";
            }
            OneWorldNPCVillageData villageData = OneWorldNPCVillageData.getVillageData(level, false);
            if (villageData != null && !villageData.canPlaceSettlementFlagAt(po.tileX, po.tileY, flagTier)) {
                return "closevillage";
            }
        }
        return super.canPlace(level, po, player, playerPositionLine, item, mapContent);
    }

    @Override
    public InventoryItem onAttemptPlace(Level level, int x, int y, PlayerMob player, InventoryItem item, GNDItemMap mapContent, String error) {
        boolean sendUpdate = false;
        System.out.println("AFASF " + error);
        switch (error) {
            case "maxsettlements": {
                Server server = level.getServer();
                player.getServerClient().sendChatMessage(new LocalMessage("misc", "maxsettlementsreached", "count", server.world.settings.maxSettlementsPerPlayer));
                sendUpdate = true;
                break;
            }
            case "closesettlement": {
                player.getServerClient().sendChatMessage(new LocalMessage("misc", "tooclosesettlement"));
                sendUpdate = true;
                break;
            }
            case "closevillage": {
                player.getServerClient().sendChatMessage(new LocalMessage("misc", "tooclosevillage"));
                sendUpdate = true;
            }
        }
        if (sendUpdate) {
            ObjectPlaceOption po = this.getPlaceOptionFromMap(mapContent);
            if (po != null) {
                player.getServerClient().sendPacket(new PacketChangeObject(level, 0, po.tileX, po.tileY));
            }
            return item;
        }
        return super.onAttemptPlace(level, x, y, player, item, mapContent, error);
    }

    @Override
    public boolean onPlaceObject(GameObject object, Level level, int layerID, int tileX, int tileY, int rotation, ServerClient client, InventoryItem item) {
        SettlementsWorldData settlementsData;
        long current;
        Server server;
        if (client != null && ((server = level.getServer()).isSingleplayer() || server.isHosted()) && (current = (settlementsData = SettlementsWorldData.getSettlementsData(server)).streamSettlements().filter(e -> e.getOwnerAuth() == client.authentication && !e.isTileWithinBounds(tileX, tileY)).count()) > 0L) {
            client.sendChatMessage(new LocalMessage("misc", "multisettlementstip"));
        }
        boolean success = super.onPlaceObject(object, level, layerID, tileX, tileY, rotation, client, item);
        if (client != null) {
            SettlementsWorldData worldData = SettlementsWorldData.getSettlementsData(level);
            ServerSettlementData serverData = worldData.getOrCreateServerData(level, tileX, tileY);
            serverData.networkData.setFlagTile(tileX, tileY);
            worldData.updateSettlement(serverData);
            serverData.networkData.setOwner(client);
            serverData.clearOutsideBounds();
            serverData.networkData.markDirty(true);
            if (client.achievementsLoaded()) {
                client.achievements().START_SETTLEMENT.markCompleted(client);
            }
            if (!serverData.networkData.isSettlementNameSet()) {
                ContainerRegistry.openAndSendContainer(client, new PacketOpenContainer(ContainerRegistry.SETTLEMENT_NAME_CONTAINER, SettlementNameContainer.getContainerContent(serverData.uniqueID)));
            }
        }
        return success;
    }
}

