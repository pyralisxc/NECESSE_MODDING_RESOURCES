/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.travel;

import java.util.List;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.BiomeRegistry;
import necesse.engine.world.worldData.SettlementsWorldData;
import necesse.inventory.container.travel.TravelContainer;
import necesse.level.maps.levelData.settlementData.CachedSettlementData;

public class IslandData {
    public final int islandX;
    public final int islandY;
    public final int biome;
    public final boolean isOutsideWorldBorder;
    public final boolean discovered;
    public final boolean visited;
    public final boolean hasDeath;
    public final boolean canTravel;
    public final GameMessage settlementName;

    public IslandData(int islandX, int islandY, int biome, boolean isOutsideWorldBorder, boolean discovered, boolean visited, boolean hasDeath, boolean canTravel, GameMessage settlementName) {
        this.islandX = islandX;
        this.islandY = islandY;
        this.biome = biome;
        this.isOutsideWorldBorder = isOutsideWorldBorder;
        this.discovered = discovered;
        this.visited = visited;
        this.hasDeath = hasDeath;
        this.canTravel = canTravel;
        this.settlementName = settlementName;
    }

    public static IslandData generateIslandData(Server server, ServerClient client, TravelContainer container, int islandX, int islandY) {
        List<CachedSettlementData> settlements;
        boolean isOutsideWorldBorder = !server.world.worldEntity.isWithinWorldBorder(islandX, islandY);
        boolean discovered = !isOutsideWorldBorder;
        boolean visited = !isOutsideWorldBorder;
        boolean canTravel = !isOutsideWorldBorder && container.isWithinTravelRange(islandX, islandY);
        boolean hasDeath = !isOutsideWorldBorder && client.streamDeathLocations().anyMatch(l -> l.levelIdentifier.isSameIsland(islandX, islandY));
        int biomeID = BiomeRegistry.UNKNOWN.getID();
        GameMessage settlementName = null;
        if (!isOutsideWorldBorder && !(settlements = SettlementsWorldData.getSettlementsData(server).collectCachedSettlements(data -> data.levelIdentifier.isIslandPosition() && data.levelIdentifier.getIslandX() == islandX && data.levelIdentifier.getIslandY() == islandY)).isEmpty()) {
            settlementName = settlements.get(0).getName();
        }
        return new IslandData(islandX, islandY, biomeID, isOutsideWorldBorder, discovered, visited, hasDeath, canTravel, settlementName);
    }
}

