/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.objectEntity;

import java.awt.Point;
import java.io.IOException;
import java.util.zip.DataFormatException;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketMapData;
import necesse.engine.network.server.ServerClient;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.OneWorldMigration;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.level.maps.Level;
import necesse.level.maps.mapData.BasicDiscoveredMapManager;

public class CartographerTableObjectEntity
extends ObjectEntity {
    private BasicDiscoveredMapManager discoveredMapManager = new BasicDiscoveredMapManager();

    public CartographerTableObjectEntity(Level level, String type, int x, int y) {
        super(level, type, x, y);
    }

    @Override
    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        if (this.isServer()) {
            this.discoveredMapManager.clearRemovedLevelIdentifiers(this.getLevel().getServer().world);
        }
        SaveData mapsSave = new SaveData("MAPS");
        this.discoveredMapManager.addSaveData(mapsSave);
        save.addSaveData(mapsSave);
    }

    @Override
    public void applyLoadData(LoadData save) {
        super.applyLoadData(save);
        LoadData mapsSave = save.getFirstLoadDataByName("MAPS");
        if (mapsSave != null) {
            this.discoveredMapManager.applySaveData(mapsSave);
            this.discoveredMapManager.makeFinal();
        }
    }

    @Override
    public void onMouseHover(PlayerMob perspective, boolean debug) {
        super.onMouseHover(perspective, debug);
        StringTooltips tooltips = new StringTooltips();
        tooltips.add(this.getObject().getDisplayName());
        GameTooltipManager.addTooltip(tooltips, TooltipLocation.INTERACT_FOCUS);
    }

    public void onClientDataReceived(ServerClient client, PacketMapData mapData) {
        try {
            boolean changed = this.discoveredMapManager.readPacketData(mapData.getMapContentReader());
            client.sendPacket(new PacketMapData(this.getLevel().getIdentifierHashCode(), this.tileX, this.tileY, changed, this.discoveredMapManager));
        }
        catch (IOException | DataFormatException e) {
            e.printStackTrace();
        }
    }

    public void interact(PlayerMob player) {
        Client client;
        if (player.isClientClient() && (client = player.getClient()).getPlayer() == player) {
            BasicDiscoveredMapManager mapData = client.levelManager.loadAllMapData();
            try {
                client.network.sendPacket(new PacketMapData(client, this.tileX, this.tileY, mapData));
            }
            catch (IOException | DataFormatException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void migrateToOneWorld(OneWorldMigration migrationData, LevelIdentifier oldLevelIdentifier, Point tileOffset, Point positionOffset) {
        super.migrateToOneWorld(migrationData, oldLevelIdentifier, tileOffset, positionOffset);
        this.discoveredMapManager = new BasicDiscoveredMapManager();
    }
}

