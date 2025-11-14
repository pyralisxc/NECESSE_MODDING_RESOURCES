/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.commands.PermissionLevel;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.level.maps.Level;

public class PacketChangeBiome
extends Packet {
    public final int levelIdentifierHashCode;
    public final int tileX;
    public final int tileY;
    public final int biomeID;

    public PacketChangeBiome(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.levelIdentifierHashCode = reader.getNextInt();
        this.tileX = reader.getNextInt();
        this.tileY = reader.getNextInt();
        this.biomeID = reader.getNextShortUnsigned();
    }

    public PacketChangeBiome(Level level, int tileX, int tileY, int biomeID) {
        this.levelIdentifierHashCode = level.getIdentifierHashCode();
        this.tileX = tileX;
        this.tileY = tileY;
        this.biomeID = biomeID;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(this.levelIdentifierHashCode);
        writer.putNextInt(tileX);
        writer.putNextInt(tileY);
        writer.putNextShortUnsigned(biomeID);
    }

    public void updateLevel(Level level) {
        level.biomeLayer.setBiome(this.tileX, this.tileY, this.biomeID);
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        if (client.getPermissionLevel().getLevel() >= PermissionLevel.ADMIN.getLevel()) {
            if (server.world.settings.cheatsAllowedOrHidden()) {
                Level level = server.world.getLevel(client);
                if (level.getIdentifierHashCode() == this.levelIdentifierHashCode) {
                    this.updateLevel(level);
                    server.network.sendToClientsWithTile(this, level, this.tileX, this.tileY);
                } else {
                    System.out.println(client.getName() + " tried to tile object on wrong level");
                }
            } else {
                System.out.println(client.getName() + " tried to change tile, but cheats aren't allowed");
            }
        } else {
            System.out.println(client.getName() + " tried to change tile, but isn't admin");
        }
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        if (!client.levelManager.checkIfLoadedRegionAtTile(this.levelIdentifierHashCode, this.tileX, this.tileY, true)) {
            return;
        }
        this.updateLevel(client.getLevel());
    }
}

