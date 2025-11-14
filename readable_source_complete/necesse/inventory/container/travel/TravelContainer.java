/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.travel;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import necesse.engine.GlobalData;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.state.MainGame;
import necesse.engine.util.HashMapArrayList;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.util.LevelIdentifierTilePos;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.inventory.container.Container;
import necesse.inventory.container.customAction.PointCustomAction;
import necesse.inventory.container.travel.IslandData;
import necesse.inventory.container.travel.IslandsResponseEvent;
import necesse.inventory.container.travel.RequestIslandsAction;
import necesse.inventory.container.travel.TravelDir;
import necesse.level.maps.Level;

public class TravelContainer
extends Container {
    public final RequestIslandsAction requestIslandsAction;
    public final PointCustomAction travelToDestination;
    public final HashMap<Point, IslandData> destinations;
    public final TravelDir travelDir;
    public final int knowRange;
    public final int travelRange;
    public final LevelIdentifier travelLevel;
    public final LevelIdentifier playerSpawnLevel;
    public final LevelIdentifier worldSpawnLevel;
    public final HashMapArrayList<Point, Integer> friendlyClientSlots = new HashMapArrayList();
    public static final int edgeTravelRangeTiles = 10;

    public TravelContainer(final NetworkClient client, int uniqueSeed, Packet contentPacket) {
        super(client, uniqueSeed);
        PacketReader reader = new PacketReader(contentPacket);
        this.travelLevel = new LevelIdentifier(reader);
        this.destinations = new HashMap();
        this.travelDir = TravelDir.values()[reader.getNextByteUnsigned()];
        this.knowRange = reader.getNextInt();
        this.travelRange = reader.getNextInt();
        this.playerSpawnLevel = new LevelIdentifier(reader);
        this.worldSpawnLevel = new LevelIdentifier(reader);
        int friendlyClientsSize = reader.getNextByteUnsigned();
        for (int i = 0; i < friendlyClientsSize; ++i) {
            int islandX = reader.getNextInt();
            int islandY = reader.getNextInt();
            Point islandPos = new Point(islandX, islandY);
            int slots = reader.getNextByteUnsigned();
            for (int j = 0; j < slots; ++j) {
                int slot = reader.getNextByteUnsigned();
                this.friendlyClientSlots.add(islandPos, slot);
            }
        }
        this.requestIslandsAction = this.registerAction(new RequestIslandsAction(this));
        this.travelToDestination = this.registerAction(new PointCustomAction(){

            @Override
            protected void run(int x, int y) {
                if (!client.isServer()) {
                    return;
                }
                ServerClient serverClient = client.getServerClient();
                IslandData islandData = IslandData.generateIslandData(serverClient.getServer(), serverClient, TravelContainer.this, x, y);
                if (islandData.canTravel) {
                    TravelContainer.this.travelTo(islandData, 0);
                } else {
                    serverClient.closeContainer(true);
                    serverClient.sendChatMessage(new LocalMessage("ui", "travelinvaliddestination"));
                }
            }
        });
        this.subscribeEvent(IslandsResponseEvent.class, e -> true, () -> true);
    }

    public static int dist(int x1, int y1, int x2, int y2) {
        return Math.max(Math.abs(x1 - x2), Math.abs(y1 - y2));
    }

    public boolean isWithinTravelRange(int islandX, int islandY) {
        int travelRangeX = this.travelRange;
        int travelRangeY = this.travelRange;
        if (this.travelDir == TravelDir.All || this.travelDir == TravelDir.North || this.travelDir == TravelDir.South) {
            travelRangeX = (int)Math.ceil((double)this.travelRange / 2.0);
        }
        if (this.travelDir == TravelDir.All || this.travelDir == TravelDir.West || this.travelDir == TravelDir.East) {
            travelRangeY = (int)Math.ceil((double)this.travelRange / 2.0);
        }
        int destStartX = this.travelLevel.getIslandX() - travelRangeX;
        int destStartY = this.travelLevel.getIslandY() - travelRangeY;
        int destEndX = this.travelLevel.getIslandX() + travelRangeX;
        int destEndY = this.travelLevel.getIslandY() + travelRangeY;
        if (this.travelDir == TravelDir.NorthWest || this.travelDir == TravelDir.North || this.travelDir == TravelDir.NorthEast) {
            destEndY = this.travelLevel.getIslandY();
        } else if (this.travelDir == TravelDir.SouthWest || this.travelDir == TravelDir.South || this.travelDir == TravelDir.SouthEast) {
            destStartY = this.travelLevel.getIslandY();
        }
        if (this.travelDir == TravelDir.NorthWest || this.travelDir == TravelDir.West || this.travelDir == TravelDir.SouthWest) {
            destEndX = this.travelLevel.getIslandX();
        } else if (this.travelDir == TravelDir.NorthEast || this.travelDir == TravelDir.East || this.travelDir == TravelDir.SouthEast) {
            destStartX = this.travelLevel.getIslandX();
        }
        return destStartX <= islandX && islandX <= destEndX && destStartY <= islandY && islandY <= destEndY;
    }

    public void travelTo(IslandData destination, int dimension) {
        if (this.client.isClient()) {
            throw new IllegalStateException("Cannot be called client side, send a request travel packet instead");
        }
        this.client.getServerClient().changeIsland(destination.islandX, destination.islandY, dimension);
        this.client.getServerClient().newStats.island_travels.increment(1);
    }

    @Override
    public boolean isValid(ServerClient client) {
        if (!super.isValid(client)) {
            return false;
        }
        if (!this.travelLevel.isIslandPosition()) {
            return false;
        }
        if (client.isDead()) {
            return false;
        }
        if (this.travelDir == TravelDir.None || this.travelDir == TravelDir.All) {
            return true;
        }
        return TravelContainer.getTravelDir(client.playerMob) == this.travelDir;
    }

    @Override
    public void onClose() {
        super.onClose();
        if (this.client.isClient() && GlobalData.getCurrentState() instanceof MainGame) {
            ((MainGame)GlobalData.getCurrentState()).formManager.resetTravelCooldown();
        }
    }

    public static TravelDir getTravelDir(PlayerMob player) {
        Level level = player.getLevel();
        int playerTileX = player.getTileX();
        int playerTileY = player.getTileY();
        if (level.tileWidth > 0) {
            if (level.tileHeight > 0) {
                if (playerTileX < 10) {
                    if (playerTileY < 10) {
                        return TravelDir.NorthWest;
                    }
                    if (playerTileY > level.tileHeight - 10) {
                        return TravelDir.SouthWest;
                    }
                } else if (playerTileX > level.tileWidth - 10) {
                    if (playerTileY < 10) {
                        return TravelDir.NorthEast;
                    }
                    if (playerTileY > level.tileHeight - 10) {
                        return TravelDir.SouthEast;
                    }
                }
            }
            if (playerTileX < 10) {
                return TravelDir.West;
            }
            if (playerTileX > level.tileWidth - 10) {
                return TravelDir.East;
            }
        }
        if (level.tileHeight > 0) {
            if (playerTileY < 10) {
                return TravelDir.North;
            }
            if (playerTileY > level.tileHeight - 10) {
                return TravelDir.South;
            }
        }
        return null;
    }

    public static Packet getContainerContentPacket(Server server, ServerClient client, TravelDir travelDir) {
        int travelRange;
        int knowRange;
        if (client.playerMob == null) {
            knowRange = 0;
            travelRange = 0;
        } else {
            knowRange = Math.max(0, client.playerMob.buffManager.getModifier(BuffModifiers.BIOME_VIEW_DISTANCE));
            travelRange = Math.max(1, client.playerMob.buffManager.getModifier(BuffModifiers.TRAVEL_DISTANCE));
        }
        return TravelContainer.getContainerContentPacket(server, client, travelDir, knowRange, travelRange);
    }

    public static Packet getContainerContentPacket(Server server, ServerClient client, TravelDir travelDir, int knowRange, int travelRange) {
        LevelIdentifierTilePos fallbackIsland;
        Packet out = new Packet();
        PacketWriter writer = new PacketWriter(out);
        LevelIdentifier levelIdentifier = client.getLevelIdentifier();
        if (!levelIdentifier.isIslandPosition() && (fallbackIsland = client.getFallbackIsland(false)) != null && fallbackIsland.identifier.isIslandPosition()) {
            levelIdentifier = fallbackIsland.identifier;
        }
        levelIdentifier.writePacket(writer);
        writer.putNextByteUnsigned(travelDir.ordinal());
        writer.putNextInt(knowRange);
        writer.putNextInt(travelRange);
        client.spawnLevelIdentifier.writePacket(writer);
        server.world.worldEntity.spawnLevelIdentifier.writePacket(writer);
        HashMapArrayList<Point, Integer> friendlyClients = new HashMapArrayList<Point, Integer>();
        for (ServerClient serverClient : server.getClients()) {
            LevelIdentifierTilePos fallbackIsland2;
            if (!serverClient.isSameTeam(client)) continue;
            LevelIdentifier otherLevelIdentifier = serverClient.getLevelIdentifier();
            if (!otherLevelIdentifier.isIslandPosition() && (fallbackIsland2 = serverClient.getFallbackIsland(false)) != null) {
                otherLevelIdentifier = fallbackIsland2.identifier;
            }
            if (!otherLevelIdentifier.isIslandPosition()) continue;
            Point islandPos = new Point(otherLevelIdentifier.getIslandX(), otherLevelIdentifier.getIslandY());
            friendlyClients.add(islandPos, serverClient.slot);
        }
        writer.putNextByteUnsigned(friendlyClients.getSize());
        for (Map.Entry entry : friendlyClients.entrySet()) {
            Point islandPos = (Point)entry.getKey();
            writer.putNextInt(islandPos.x);
            writer.putNextInt(islandPos.y);
            ArrayList slots = (ArrayList)entry.getValue();
            writer.putNextByteUnsigned(slots.size());
            for (Integer slot : slots) {
                writer.putNextByteUnsigned(slot);
            }
        }
        return out;
    }

    public static boolean canOpen(ServerClient client) {
        LevelIdentifierTilePos fallbackIsland;
        LevelIdentifier levelIdentifier = client.getLevelIdentifier();
        if (!levelIdentifier.isIslandPosition() && (fallbackIsland = client.getFallbackIsland(false)) != null) {
            levelIdentifier = fallbackIsland.identifier;
        }
        return levelIdentifier.isIslandPosition();
    }
}

