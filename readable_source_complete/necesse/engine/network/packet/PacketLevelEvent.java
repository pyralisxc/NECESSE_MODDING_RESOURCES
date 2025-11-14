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
import necesse.engine.registries.LevelEventRegistry;
import necesse.entity.levelEvent.LevelEvent;
import necesse.level.maps.Level;

public class PacketLevelEvent
extends Packet {
    public final int levelIdentifierHashCode;
    public final int eventID;
    public final Packet spawnContent;

    public PacketLevelEvent(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.levelIdentifierHashCode = reader.getNextInt();
        this.eventID = reader.getNextShortUnsigned();
        this.spawnContent = reader.getNextContentPacket();
    }

    public PacketLevelEvent(LevelEvent event) {
        if (event.getID() == -1) {
            throw new IllegalArgumentException("Specific level event cannot be sent over network");
        }
        this.levelIdentifierHashCode = event.level == null ? 0 : event.level.getIdentifierHashCode();
        this.eventID = event.getID();
        this.spawnContent = new Packet();
        event.setupSpawnPacket(new PacketWriter(this.spawnContent));
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(this.levelIdentifierHashCode);
        writer.putNextShortUnsigned(this.eventID);
        writer.putNextContentPacket(this.spawnContent);
    }

    public LevelEvent getNewEvent(Level level) {
        LevelEvent event = LevelEventRegistry.getEvent(this.eventID);
        if (event != null) {
            event.level = level;
            event.applySpawnPacket(new PacketReader(this.spawnContent));
        }
        return event;
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        if (client.getPermissionLevel().getLevel() >= PermissionLevel.ADMIN.getLevel()) {
            if (server.world.settings.cheatsAllowedOrHidden()) {
                Level level = server.world.getLevel(client);
                if (level.getIdentifierHashCode() == this.levelIdentifierHashCode) {
                    LevelEvent event = this.getNewEvent(level);
                    level.entityManager.events.addHidden(event);
                    server.network.sendToClientsWithEntityExcept(this, event, client);
                } else {
                    System.out.println(client.getName() + " tried to spawn a level event on wrong level");
                }
            } else {
                System.out.println(client.getName() + " tried to spawn a level event, but cheats aren't allowed");
            }
        } else {
            System.out.println(client.getName() + " tried to spawn a level event, but isn't admin");
        }
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        if (client.getLevel() == null || this.levelIdentifierHashCode != 0 && client.getLevel().getIdentifierHashCode() != this.levelIdentifierHashCode) {
            return;
        }
        client.getLevel().entityManager.events.addHidden(this.getNewEvent(client.getLevel()));
    }
}

