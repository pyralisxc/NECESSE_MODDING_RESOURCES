/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.GameEvents;
import necesse.engine.GameLog;
import necesse.engine.events.players.ObjectInteractEvent;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientClient;
import necesse.engine.network.packet.PacketChangeObject;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ObjectLayerRegistry;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObject;

public class PacketObjectInteract
extends Packet {
    public final int levelIdentifierHashCode;
    public final int slot;
    public final int tileX;
    public final int tileY;

    public PacketObjectInteract(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.levelIdentifierHashCode = reader.getNextInt();
        this.slot = reader.getNextByteUnsigned();
        this.tileX = reader.getNextInt();
        this.tileY = reader.getNextInt();
    }

    public PacketObjectInteract(Level level, int slot, int tileX, int tileY) {
        this.levelIdentifierHashCode = level.getIdentifierHashCode();
        this.slot = slot;
        this.tileX = tileX;
        this.tileY = tileY;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(this.levelIdentifierHashCode);
        writer.putNextByteUnsigned(slot);
        writer.putNextInt(tileX);
        writer.putNextInt(tileY);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        if (!client.levelManager.checkIfLoadedRegionAtTile(this.levelIdentifierHashCode, this.tileX, this.tileY, true)) {
            return;
        }
        ClientClient target = client.getClient(this.slot);
        if (target != null && target.playerMob != null) {
            GameEvents.triggerEvent(new ObjectInteractEvent(client.getLevel(), this.tileX, this.tileY, target.playerMob), e -> client.getLevel().getLevelObject(this.tileX, this.tileY).interact(target.playerMob));
        }
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        if (client.slot == this.slot) {
            if (!client.checkHasRequestedSelf() || client.isDead()) {
                return;
            }
            client.checkSpawned();
            Level level = server.world.getLevel(client);
            if (level.getIdentifierHashCode() == this.levelIdentifierHashCode) {
                LevelObject obj = level.getLevelObject(this.tileX, this.tileY);
                if (obj.canInteract(client.playerMob)) {
                    GameEvents.triggerEvent(new ObjectInteractEvent(level, this.tileX, this.tileY, client.playerMob), e -> {
                        obj.interact(client.playerMob);
                        server.network.sendToClientsWithTileExcept(this, level, this.tileX, this.tileY, client);
                    });
                } else {
                    GameLog.warn.println("Client tried to interact with non interactable object " + obj.object.getStringID() + " at " + obj.tileX + ", " + obj.tileY);
                    client.sendPacket(new PacketChangeObject(level, ObjectLayerRegistry.BASE_LAYER, this.tileX, this.tileY));
                }
            } else {
                GameLog.warn.println("Client " + client.getName() + " tried call interact at wrong level");
            }
        } else {
            GameLog.warn.println("Client " + client.getName() + " tried call interact from wrong slot");
        }
    }
}

