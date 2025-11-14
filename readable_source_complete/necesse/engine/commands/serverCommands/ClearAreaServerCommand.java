/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.serverCommands;

import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;
import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.IntParameterHandler;
import necesse.engine.commands.parameterHandlers.ServerClientParameterHandler;
import necesse.engine.commands.parameterHandlers.TileParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketRegionData;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ObjectLayerRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.level.gameTile.GameTile;
import necesse.level.maps.Level;
import necesse.level.maps.regionSystem.Region;

public class ClearAreaServerCommand
extends ModularChatCommand {
    public ClearAreaServerCommand() {
        super("cleararea", "Clears an area around the player", PermissionLevel.ADMIN, true, new CmdParameter("player", new ServerClientParameterHandler(true, false), true, new CmdParameter[0]), new CmdParameter("radius", new IntParameterHandler(50), true, new CmdParameter[0]), new CmdParameter("tile", new TileParameterHandler(TileRegistry.sandID), true, new CmdParameter[0]));
    }

    @Override
    public void runModular(Client client, Server server, ServerClient serverClient, Object[] args, String[] errors, CommandLog logs) {
        ServerClient target = (ServerClient)args[0];
        if (target == null) {
            logs.add("Must specify <player>");
            return;
        }
        int radius = (Integer)args[1];
        if (radius < 1) {
            logs.add("Radius must be greater than 0");
            return;
        }
        GameTile tile = (GameTile)args[2];
        if (tile == null) {
            logs.add("A valid tile must be specified");
            return;
        }
        int tileID = tile.getID();
        logs.add("Clearing area around " + target.getName() + " with radius " + radius + " with " + tile.getStringID());
        int playerX = target.playerMob.getTileX();
        int playerY = target.playerMob.getTileY();
        Ellipse2D.Float ellipse = new Ellipse2D.Float(playerX - radius, playerY - radius, radius * 2, radius * 2);
        Level level = serverClient.getLevel();
        int startRegionX = level.regionManager.getRegionCoordByTile(playerX - radius);
        int startRegionY = level.regionManager.getRegionCoordByTile(playerY - radius);
        int endRegionX = level.regionManager.getRegionCoordByTile(playerX + radius);
        int endRegionY = level.regionManager.getRegionCoordByTile(playerY + radius);
        int regionSize = 16;
        for (int regionY = startRegionY; regionY <= endRegionY; ++regionY) {
            for (int regionX = startRegionX; regionX <= endRegionX; ++regionX) {
                Rectangle regionRectangle = new Rectangle(regionX * regionSize, regionY * regionSize, regionSize, regionSize);
                boolean fillAll = false;
                boolean wasRegionLoaded = level.regionManager.isRegionLoaded(regionX, regionY);
                if (ellipse.contains(regionRectangle)) {
                    level.regionManager.ensureRegionIsLoadedButDontGenerate(regionX, regionY);
                    fillAll = true;
                }
                if (fillAll || ellipse.intersects(regionRectangle)) {
                    Region region = level.regionManager.getRegion(regionX, regionY, true);
                    for (int tileY = regionY * regionSize; tileY < (regionY + 1) * regionSize; ++tileY) {
                        for (int tileX = regionX * regionSize; tileX < (regionX + 1) * regionSize; ++tileX) {
                            if (!fillAll && !ellipse.contains(tileX, tileY)) continue;
                            int regionTileX = tileX - region.tileXOffset;
                            int regionTileY = tileY - region.tileYOffset;
                            region.tileLayer.setTileByRegion(regionTileX, regionTileY, tileID, true);
                            for (int i = 0; i < ObjectLayerRegistry.getTotalLayers(); ++i) {
                                region.objectLayer.setObjectByRegion(i, regionTileX, regionTileY, 0, true);
                            }
                            region.wireLayer.setWireDataByRegion(regionTileX, regionTileY, (byte)0);
                        }
                    }
                    region.updateLiquidManager();
                    region.updateSplattingManager();
                    region.updateSubRegions();
                    region.updateLight();
                    int finalRegionX = regionX;
                    int finalRegionY = regionY;
                    ServerClient[] clientsWithRegion = (ServerClient[])server.streamClients().filter(c -> c.hasRegionLoaded(level, finalRegionX, finalRegionY)).toArray(ServerClient[]::new);
                    if (clientsWithRegion.length > 0) {
                        PacketRegionData packet = new PacketRegionData(region);
                        for (ServerClient c2 : clientsWithRegion) {
                            c2.sendPacket(packet);
                        }
                    }
                }
                if (wasRegionLoaded) continue;
                level.regionManager.unloadRegion(regionX, regionY);
            }
        }
    }
}

