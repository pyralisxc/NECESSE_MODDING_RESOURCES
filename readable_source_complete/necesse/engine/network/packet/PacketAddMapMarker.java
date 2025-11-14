/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.registries.MapIconRegistry;
import necesse.engine.util.LevelIdentifier;
import necesse.level.maps.mapData.GameMapIcon;

public class PacketAddMapMarker
extends Packet {
    public final int mapIconID;
    public final GameMessage name;
    public final LevelIdentifier levelIdentifier;
    public final int tileX;
    public final int tileY;

    public PacketAddMapMarker(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.mapIconID = reader.getNextShortUnsigned();
        this.name = GameMessage.fromPacket(reader);
        this.levelIdentifier = new LevelIdentifier(reader);
        this.tileX = reader.getNextInt();
        this.tileY = reader.getNextInt();
    }

    public PacketAddMapMarker(GameMapIcon icon, GameMessage name, LevelIdentifier levelIdentifier, int tileX, int tileY) {
        this.mapIconID = icon.getID();
        this.name = name;
        this.levelIdentifier = levelIdentifier;
        this.tileX = tileX;
        this.tileY = tileY;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextShortUnsigned(this.mapIconID);
        name.writePacket(writer);
        levelIdentifier.writePacket(writer);
        writer.putNextInt(tileX);
        writer.putNextInt(tileY);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        if (client.getLevel() == null) {
            return;
        }
        client.levelManager.addMapMarker(MapIconRegistry.getIcon(this.mapIconID), this.name, this.levelIdentifier, this.tileX, this.tileY);
    }
}

