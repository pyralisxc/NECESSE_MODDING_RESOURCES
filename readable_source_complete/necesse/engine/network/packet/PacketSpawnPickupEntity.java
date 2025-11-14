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
import necesse.engine.registries.PickupRegistry;
import necesse.entity.pickup.PickupEntity;
import necesse.level.maps.Level;

public class PacketSpawnPickupEntity
extends Packet {
    public final int levelIdentifierHashCode;
    public final int pickupID;
    public final Packet spawnContent;

    public PacketSpawnPickupEntity(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.levelIdentifierHashCode = reader.getNextInt();
        this.pickupID = reader.getNextShortUnsigned();
        this.spawnContent = reader.getNextContentPacket();
    }

    public PacketSpawnPickupEntity(PickupEntity pickupEntity) {
        this.levelIdentifierHashCode = pickupEntity.getLevel().getIdentifierHashCode();
        this.pickupID = pickupEntity.getID();
        this.spawnContent = new Packet();
        pickupEntity.setupSpawnPacket(new PacketWriter(this.spawnContent));
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(this.levelIdentifierHashCode);
        writer.putNextShortUnsigned(this.pickupID);
        writer.putNextContentPacket(this.spawnContent);
    }

    public PickupEntity getPickupEntity(Level level) {
        PickupEntity out = PickupRegistry.getPickup(this.pickupID);
        if (out != null) {
            out.setLevel(level);
            out.applySpawnPacket(new PacketReader(this.spawnContent));
        }
        return out;
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        if (client.getPermissionLevel().getLevel() >= PermissionLevel.ADMIN.getLevel()) {
            if (server.world.settings.cheatsAllowedOrHidden()) {
                Level level = server.world.getLevel(client);
                if (level.getIdentifierHashCode() == this.levelIdentifierHashCode) {
                    level.entityManager.pickups.add(this.getPickupEntity(level));
                } else {
                    System.out.println(client.getName() + " tried to spawn pickup entity on wrong level");
                }
            } else {
                System.out.println(client.getName() + " tried to spawn pickup entity, but cheats aren't allowed");
            }
        } else {
            System.out.println(client.getName() + " tried to spawn pickup entity, but isn't admin");
        }
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        if (!client.levelManager.isLevelLoaded(this.levelIdentifierHashCode)) {
            return;
        }
        PickupEntity pickupEntity = this.getPickupEntity(client.getLevel());
        if (!client.levelManager.checkIfLoadedRegionAtTile(this.levelIdentifierHashCode, pickupEntity, true)) {
            PickupEntity foundPickupEntity = client.getLevel().entityManager.pickups.get(pickupEntity.getUniqueID(), false);
            if (foundPickupEntity != null) {
                foundPickupEntity.remove();
            }
            return;
        }
        client.getLevel().entityManager.pickups.add(pickupEntity);
    }
}

