/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import java.io.IOException;
import java.util.zip.DataFormatException;
import necesse.engine.localization.Localization;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketIterator;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.entity.objectEntity.CartographerTableObjectEntity;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.mapData.BasicDiscoveredMapManager;

public class PacketMapData
extends Packet {
    public final int levelIdentifierHashCode;
    public final int cartographerTableTileX;
    public final int cartographerTableTileY;
    public final boolean tableMapChanged;
    private final PacketIterator mapContentReader;

    public PacketMapData(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.levelIdentifierHashCode = reader.getNextInt();
        this.cartographerTableTileX = reader.getNextInt();
        this.cartographerTableTileY = reader.getNextInt();
        this.tableMapChanged = reader.getNextBoolean();
        this.mapContentReader = new PacketIterator(reader);
    }

    public PacketMapData(int levelIdentifierHashCode, int cartographerTableTileX, int cartographerTableTileY, boolean tableMapChanged, BasicDiscoveredMapManager mapData) throws DataFormatException, IOException {
        this.levelIdentifierHashCode = levelIdentifierHashCode;
        this.cartographerTableTileX = cartographerTableTileX;
        this.cartographerTableTileY = cartographerTableTileY;
        this.tableMapChanged = tableMapChanged;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(levelIdentifierHashCode);
        writer.putNextInt(cartographerTableTileX);
        writer.putNextInt(cartographerTableTileY);
        writer.putNextBoolean(tableMapChanged);
        this.mapContentReader = new PacketIterator(writer);
        mapData.writePacketData(writer);
    }

    public PacketMapData(Client client, int tileX, int tileY, BasicDiscoveredMapManager mapData) throws DataFormatException, IOException {
        this(client.getLevel().getIdentifierHashCode(), tileX, tileY, false, mapData);
    }

    public PacketReader getMapContentReader() {
        return new PacketReader(this.mapContentReader);
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        Level level = client.getLevel();
        if (level.getIdentifierHashCode() == this.levelIdentifierHashCode) {
            CartographerTableObjectEntity objectEntity = level.entityManager.getObjectEntity(this.cartographerTableTileX, this.cartographerTableTileY, CartographerTableObjectEntity.class);
            if (objectEntity != null) {
                int range;
                GameObject object = objectEntity.getObject();
                if (object.isInInteractRange(level, objectEntity.tileX, objectEntity.tileY, client.playerMob, range = object.getInteractRange(level, objectEntity.tileX, objectEntity.tileY) + 100)) {
                    objectEntity.onClientDataReceived(client, this);
                } else {
                    System.out.println(client.getName() + " tried to interact with cartographer table out of range");
                }
            } else {
                System.out.println(client.getName() + " tried to send map data to invalid cartographer table");
            }
        } else {
            System.out.println(client.getName() + " tried to send map data to wrong level");
        }
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        if (client.getLevel() == null) {
            return;
        }
        if (this.tableMapChanged) {
            client.chat.addMessage(Localization.translate("ui", "cartographeradded"));
        }
        try {
            BasicDiscoveredMapManager currentData = client.levelManager.loadAllMapData();
            boolean myMapChanged = currentData.readPacketData(this.getMapContentReader());
            client.levelManager.applyMapData(currentData);
            if (myMapChanged) {
                client.chat.addMessage(Localization.translate("ui", "cartographerdiscovered"));
            } else if (!this.tableMapChanged) {
                client.chat.addMessage(Localization.translate("ui", "cartographernothing"));
            }
        }
        catch (IOException | DataFormatException e) {
            e.printStackTrace();
        }
    }
}

