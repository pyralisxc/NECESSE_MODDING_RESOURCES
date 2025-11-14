/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.GameLog;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketDisconnect;
import necesse.engine.network.packet.PacketRegionData;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.level.maps.Level;
import necesse.level.maps.regionSystem.Region;

public class PacketRequestRegionData
extends Packet {
    public final int levelIdentifierHashCode;
    public final int regionX;
    public final int regionY;

    public PacketRequestRegionData(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.levelIdentifierHashCode = reader.getNextInt();
        this.regionX = reader.getNextInt();
        this.regionY = reader.getNextInt();
    }

    public PacketRequestRegionData(Level level, int regionX, int regionY) {
        this.levelIdentifierHashCode = level.getIdentifierHashCode();
        this.regionX = regionX;
        this.regionY = regionY;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(this.levelIdentifierHashCode);
        writer.putNextInt(regionX);
        writer.putNextInt(regionY);
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        Level level = server.world.getLevel(client);
        if (level != null && level.getIdentifierHashCode() == this.levelIdentifierHashCode) {
            Region region = level.regionManager.getRegion(this.regionX, this.regionY, true);
            if (region != null) {
                server.network.sendPacket((Packet)new PacketRegionData(region), client);
                client.addLoadedRegion(level, this.regionX, this.regionY, true);
            } else {
                if (!client.checkHasRequestedSelf()) {
                    return;
                }
                GameLog.warn.println("Client requested invalid region data resulting in a kick.");
                server.disconnectClient(client, PacketDisconnect.Code.INTERNAL_ERROR);
            }
        }
    }
}

