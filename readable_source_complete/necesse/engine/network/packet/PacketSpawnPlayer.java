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
import necesse.engine.network.packet.PacketPlayerLevelChange;
import necesse.engine.network.packet.PacketRequestPlayerData;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.LevelIdentifier;

public class PacketSpawnPlayer
extends Packet {
    public final int slot;
    public final LevelIdentifier levelIdentifier;
    public final int remainingSpawnInvincibilityTime;

    public PacketSpawnPlayer(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.slot = reader.getNextByteUnsigned();
        this.levelIdentifier = new LevelIdentifier(reader);
        this.remainingSpawnInvincibilityTime = reader.getNextBoolean() ? reader.getNextInt() : 0;
    }

    public PacketSpawnPlayer(Client client) {
        this.slot = client.getSlot();
        this.levelIdentifier = client.getLevel().getIdentifier();
        this.remainingSpawnInvincibilityTime = 0;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextByteUnsigned(this.slot);
        this.levelIdentifier.writePacket(writer);
        writer.putNextBoolean(false);
    }

    public PacketSpawnPlayer(ServerClient client) {
        this.slot = client.slot;
        this.levelIdentifier = client.getLevelIdentifier();
        this.remainingSpawnInvincibilityTime = client.playerMob == null ? 0 : client.playerMob.getRemainingSpawnInvincibilityTime();
        PacketWriter writer = new PacketWriter(this);
        writer.putNextByteUnsigned(this.slot);
        this.levelIdentifier.writePacket(writer);
        writer.putNextBoolean(true);
        writer.putNextInt(this.remainingSpawnInvincibilityTime);
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        if (this.slot != client.slot) {
            return;
        }
        if (!client.isSamePlace(this.levelIdentifier)) {
            client.sendPacket(new PacketPlayerLevelChange(client.slot, client.getLevelIdentifier(), true));
            return;
        }
        client.submitSpawnPacket(this);
        server.pauseForSpawnedPlayer = false;
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        ClientClient target = client.getClient(this.slot);
        if (target == null || this.levelIdentifier == null) {
            client.network.sendPacket(new PacketRequestPlayerData(this.slot));
        } else {
            target.applySpawned(this.remainingSpawnInvincibilityTime);
            target.setLevelIdentifier(this.levelIdentifier);
        }
    }
}

