/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.server.network;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.IOException;
import java.util.Collection;
import java.util.function.Predicate;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.networkInfo.NetworkInfo;
import necesse.engine.network.packet.PacketRequestSession;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.PacketRegistry;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.util.PointSetAbstract;
import necesse.engine.util.WarningMessageCooldown;
import necesse.level.maps.Level;
import necesse.level.maps.regionSystem.RegionPosition;
import necesse.level.maps.regionSystem.RegionPositionGetter;

public abstract class ServerNetwork {
    public final Server server;
    protected WarningMessageCooldown<NetworkInfo> unknownPacketTimeouts = new WarningMessageCooldown(5000, 120000);

    public ServerNetwork(Server server) {
        this.server = server;
    }

    public abstract void open() throws IOException;

    public abstract boolean isOpen();

    public abstract String getAddress();

    public abstract void sendPacket(NetworkPacket var1);

    public void sendPacket(Packet packet, ServerClient client) {
        NetworkPacket networkPacket = new NetworkPacket(packet, client.networkInfo);
        this.sendPacket(networkPacket);
        client.submitOutPacket(networkPacket);
    }

    public void sendPacket(Packet packet, Predicate<ServerClient> filter) {
        this.server.streamClients().filter(filter).forEach(c -> this.sendPacket(packet, (ServerClient)c));
    }

    public void sendToAllClients(Packet packet) {
        this.sendPacket(packet, (ServerClient c) -> true);
    }

    public void sendToClientsWithAnyRegion(Packet packet, Collection<RegionPosition> regionPosition) {
        if (regionPosition.isEmpty()) {
            return;
        }
        this.sendPacket(packet, (ServerClient c) -> regionPosition.stream().anyMatch(c::hasRegionLoaded));
    }

    public void sendToClientsWithAnyRegion(Packet packet, Level level, PointSetAbstract<?> regionPosition) {
        if (regionPosition.isEmpty()) {
            return;
        }
        this.sendPacket(packet, (ServerClient c) -> regionPosition.stream().anyMatch(region -> c.hasRegionLoaded(level, region.x, region.y)));
    }

    public void sendToClientsWithAnyRegion(Packet packet, Level level, Rectangle regionRectangle) {
        if (regionRectangle.isEmpty()) {
            return;
        }
        this.sendPacket(packet, (ServerClient c) -> c.hasAnyRegionLoaded(level.getIdentifier(), p -> regionRectangle.contains(p.x, p.y)));
    }

    public void sendToClientsWithAnyRegion(Packet packet, Level level, Predicate<Point> regionPositionTester) {
        if (regionPositionTester == null) {
            return;
        }
        this.sendPacket(packet, (ServerClient c) -> c.hasAnyRegionLoaded(level.getIdentifier(), regionPositionTester));
    }

    public void sendToClientsWithRegion(Packet packet, LevelIdentifier levelIdentifier, int regionX, int regionY) {
        this.sendPacket(packet, (ServerClient c) -> c.hasRegionLoaded(levelIdentifier, regionX, regionY));
    }

    public void sendToClientsWithRegion(Packet packet, Level level, int regionX, int regionY) {
        this.sendPacket(packet, (ServerClient c) -> c.hasRegionLoaded(level, regionX, regionY));
    }

    public void sendToClientsWithRegion(Packet packet, RegionPosition regionPosition) {
        this.sendPacket(packet, (ServerClient c) -> c.hasRegionLoaded(regionPosition));
    }

    public void sendToClientsWithAnyRegionExcept(Packet packet, Collection<RegionPosition> regionPosition, ServerClient exception) {
        if (regionPosition.isEmpty()) {
            return;
        }
        this.sendPacket(packet, (ServerClient c) -> {
            if (c == exception) return false;
            if (regionPosition.isEmpty()) return true;
            if (!regionPosition.stream().anyMatch(c::hasRegionLoaded)) return false;
            return true;
        });
    }

    public void sendToClientsWithAnyRegionExcept(Packet packet, Level level, PointSetAbstract<?> regionPosition, ServerClient exception) {
        if (regionPosition.isEmpty()) {
            return;
        }
        this.sendPacket(packet, (ServerClient c) -> c != exception && (regionPosition.isEmpty() || regionPosition.stream().anyMatch(region -> c.hasRegionLoaded(level, region.x, region.y))));
    }

    public void sendToClientsWithRegionExcept(Packet packet, LevelIdentifier levelIdentifier, int regionX, int regionY, ServerClient exception) {
        this.sendPacket(packet, (ServerClient c) -> c != exception && c.hasRegionLoaded(levelIdentifier, regionX, regionY));
    }

    public void sendToClientsWithRegionExcept(Packet packet, Level level, int regionX, int regionY, ServerClient exception) {
        this.sendPacket(packet, (ServerClient c) -> c != exception && c.hasRegionLoaded(level, regionX, regionY));
    }

    public void sendToClientsWithRegionExcept(Packet packet, RegionPosition regionPosition, ServerClient exception) {
        this.sendPacket(packet, (ServerClient c) -> c != exception && c.hasRegionLoaded(regionPosition));
    }

    public void sendToClientsWithEntity(Packet packet, RegionPositionGetter getter) {
        PointSetAbstract<?> regionPositions = getter.getRegionPositions();
        if (regionPositions.isEmpty()) {
            this.sendToClientsAtEntireLevel(packet, getter.getLevel());
        } else {
            this.sendToClientsWithAnyRegion(packet, getter.getLevel(), regionPositions);
        }
    }

    public void sendToClientsWithEntityExcept(Packet packet, RegionPositionGetter getter, ServerClient exception) {
        PointSetAbstract<?> regionPositions = getter.getRegionPositions();
        if (regionPositions.isEmpty()) {
            this.sendToClientsAtEntireLevelExcept(packet, getter.getLevel(), exception);
        } else {
            this.sendToClientsWithAnyRegionExcept(packet, getter.getLevel(), regionPositions, exception);
        }
    }

    public void sendToClientsWithTile(Packet packet, Level level, int tileX, int tileY) {
        int regionX = level.regionManager.getRegionCoordByTile(tileX);
        int regionY = level.regionManager.getRegionCoordByTile(tileY);
        this.sendToClientsWithRegion(packet, level, regionX, regionY);
    }

    public void sendToClientsWithTileExcept(Packet packet, Level level, int tileX, int tileY, ServerClient exception) {
        int regionX = level.regionManager.getRegionCoordByTile(tileX);
        int regionY = level.regionManager.getRegionCoordByTile(tileY);
        this.sendToClientsWithRegionExcept(packet, level, regionX, regionY, exception);
    }

    public void sendToClientsAtEntireLevelExcept(Packet packet, LevelIdentifier levelIdentifier, ServerClient exception) {
        this.sendPacket(packet, (ServerClient c) -> c != exception && c.isSamePlace(levelIdentifier));
    }

    public void sendToClientsAtEntireLevelExcept(Packet packet, Level level, ServerClient exception) {
        if (level == null) {
            return;
        }
        this.sendToClientsAtEntireLevelExcept(packet, level.getIdentifier(), exception);
    }

    public void sendToClientsAtEntireLevel(Packet packet, LevelIdentifier levelIdentifier) {
        this.sendPacket(packet, (ServerClient c) -> c.hasAnyLoadedRegionAtLevel(levelIdentifier));
    }

    public void sendToClientsAtEntireLevel(Packet packet, int islandX, int islandY, int dimension) {
        this.sendPacket(packet, (ServerClient c) -> c.hasAnyLoadedRegionAtLevel(new LevelIdentifier(islandX, islandY, dimension)));
    }

    public void sendToClientsAtEntireLevel(Packet packet, Level level) {
        if (level == null) {
            return;
        }
        this.sendToClientsAtEntireLevel(packet, level.getIdentifier());
    }

    @Deprecated
    public void sendToClientsAtExcept(Packet packet, LevelIdentifier levelIdentifier, ServerClient exception) {
        this.sendPacket(packet, (ServerClient c) -> c != exception && c.isSamePlace(levelIdentifier));
    }

    @Deprecated
    public void sendToClientsAtExcept(Packet packet, Level level, ServerClient exception) {
        if (level == null) {
            return;
        }
        this.sendToClientsAtExcept(packet, level.getIdentifier(), exception);
    }

    @Deprecated
    public void sendToClientsAt(Packet packet, LevelIdentifier levelIdentifier) {
        this.sendPacket(packet, (ServerClient c) -> c.hasAnyLoadedRegionAtLevel(levelIdentifier));
    }

    @Deprecated
    public void sendToClientsAt(Packet packet, int islandX, int islandY, int dimension) {
        this.sendPacket(packet, (ServerClient c) -> c.hasAnyLoadedRegionAtLevel(new LevelIdentifier(islandX, islandY, dimension)));
    }

    @Deprecated
    public void sendToClientsAt(Packet packet, Level level) {
        if (level == null) {
            return;
        }
        this.sendToClientsAt(packet, level.getIdentifier());
    }

    public void sendToAllClientsExcept(Packet packet, ServerClient exception) {
        this.sendPacket(packet, (ServerClient c) -> c != exception);
    }

    public abstract void close();

    public abstract String getDebugString();

    public void tickUnknownPacketTimeouts() {
        this.unknownPacketTimeouts.tickTimeouts();
    }

    public void submitUnknownPacket(NetworkPacket packet) {
        this.unknownPacketTimeouts.submit(packet.networkInfo, countSinceLastWarning -> {
            String name = packet.getInfoDisplayName();
            System.out.println("Got packet " + PacketRegistry.getPacketSimpleName(packet.type) + " from unknown client \"" + name + "\" (" + countSinceLastWarning + "). Requesting session...");
            this.sendPacket(new NetworkPacket(new PacketRequestSession(), packet.networkInfo));
        });
    }
}

