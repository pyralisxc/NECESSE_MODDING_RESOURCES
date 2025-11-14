/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientClient;
import necesse.engine.network.packet.PacketRequestPlayerData;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.LevelIdentifier;

public class PacketPlayerRespawn
extends Packet {
    public final int slot;
    public final LevelIdentifier levelIdentifier;
    public final int playerX;
    public final int playerY;

    public PacketPlayerRespawn(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.slot = reader.getNextByteUnsigned();
        this.levelIdentifier = new LevelIdentifier(reader);
        this.playerX = reader.getNextInt();
        this.playerY = reader.getNextInt();
    }

    public PacketPlayerRespawn(ServerClient client) {
        this.slot = client.slot;
        this.levelIdentifier = client.getLevelIdentifier();
        this.playerX = client.playerMob.getX();
        this.playerY = client.playerMob.getY();
        PacketWriter writer = new PacketWriter(this);
        writer.putNextByteUnsigned(this.slot);
        this.levelIdentifier.writePacket(writer);
        writer.putNextInt(this.playerX);
        writer.putNextInt(this.playerY);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        ClientClient target = client.getClient(this.slot);
        if (target == null) {
            client.network.sendPacket(new PacketRequestPlayerData(this.slot));
        } else {
            if (this.slot == client.getSlot()) {
                client.respawn(this);
                return;
            }
            target.respawn(this);
        }
    }
}

