/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import java.util.ArrayList;
import necesse.engine.dlc.DLC;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketClientInstalledDLC;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.LevelIdentifier;

public class PacketPlayerGeneral
extends Packet {
    public final int slot;
    public final long authentication;
    public final int characterUniqueID;
    public final LevelIdentifier levelIdentifier;
    public final boolean pvpEnabled;
    public final boolean isDead;
    public final boolean hasSpawned;
    public final int remainingRespawnTime;
    public final int remainingSpawnInvincibilityTime;
    public final int team;
    public final String name;
    public final Packet playerSpawnContent;
    public final PacketClientInstalledDLC installedDLC;

    public PacketPlayerGeneral(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.slot = reader.getNextByteUnsigned();
        this.authentication = reader.getNextLong();
        this.characterUniqueID = reader.getNextInt();
        this.levelIdentifier = new LevelIdentifier(reader);
        this.pvpEnabled = reader.getNextBoolean();
        this.isDead = reader.getNextBoolean();
        this.remainingRespawnTime = this.isDead ? reader.getNextInt() : 0;
        this.hasSpawned = reader.getNextBoolean();
        boolean hasRemainingSpawnInvincibilityTime = reader.getNextBoolean();
        this.remainingSpawnInvincibilityTime = hasRemainingSpawnInvincibilityTime ? reader.getNextInt() : 0;
        this.team = reader.getNextShort();
        this.name = reader.getNextString();
        this.playerSpawnContent = reader.getNextContentPacket();
        this.installedDLC = new PacketClientInstalledDLC(reader.getNextContentPacket().getPacketData());
    }

    public PacketPlayerGeneral(ServerClient client) {
        this.slot = client.slot;
        this.authentication = client.authentication;
        this.characterUniqueID = client.getCharacterUniqueID();
        this.levelIdentifier = client.getLevelIdentifier();
        this.pvpEnabled = client.pvpEnabled;
        this.isDead = client.isDead();
        this.remainingRespawnTime = client.getRespawnTimeRemaining();
        this.hasSpawned = client.hasSpawned();
        this.remainingSpawnInvincibilityTime = client.playerMob == null ? 0 : client.playerMob.getRemainingSpawnInvincibilityTime();
        this.team = client.getTeamID();
        this.name = client.getName();
        this.playerSpawnContent = new Packet();
        this.installedDLC = new PacketClientInstalledDLC(client.slot, new ArrayList<DLC>(client.installedDLC.values()));
        client.playerMob.setupSpawnPacket(new PacketWriter(this.playerSpawnContent));
        PacketWriter writer = new PacketWriter(this);
        writer.putNextByteUnsigned(this.slot);
        writer.putNextLong(this.authentication);
        writer.putNextInt(this.characterUniqueID);
        this.levelIdentifier.writePacket(writer);
        writer.putNextBoolean(this.pvpEnabled);
        writer.putNextBoolean(this.isDead);
        if (this.isDead) {
            writer.putNextInt(this.remainingRespawnTime);
        }
        writer.putNextBoolean(this.hasSpawned);
        if (this.remainingSpawnInvincibilityTime > 0) {
            writer.putNextBoolean(true);
            writer.putNextInt(this.remainingSpawnInvincibilityTime);
        } else {
            writer.putNextBoolean(false);
        }
        writer.putNextShort((short)this.team);
        writer.putNextString(this.name);
        writer.putNextContentPacket(this.playerSpawnContent);
        writer.putNextContentPacket(this.installedDLC);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        client.applyPlayerGeneralPacket(this);
    }
}

