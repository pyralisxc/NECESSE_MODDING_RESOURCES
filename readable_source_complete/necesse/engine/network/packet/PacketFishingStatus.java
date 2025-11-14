/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketRequestLevelEvent;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.levelEvent.fishingEvent.FishingEvent;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;

public class PacketFishingStatus
extends Packet {
    public final int secondType;
    public final int eventUniqueID;
    public final Packet extraContent;

    public PacketFishingStatus(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.secondType = reader.getNextByteUnsigned();
        this.eventUniqueID = reader.getNextInt();
        this.extraContent = reader.getNextContentPacket();
    }

    private PacketFishingStatus(int secondType, int eventUniqueID, Packet extraContent) {
        this.secondType = secondType;
        this.eventUniqueID = eventUniqueID;
        this.extraContent = extraContent;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextByteUnsigned(secondType);
        writer.putNextInt(eventUniqueID);
        writer.putNextContentPacket(extraContent);
    }

    public FishingEvent getEvent(Level level) {
        LevelEvent event = level.entityManager.events.get(this.eventUniqueID, false);
        if (event instanceof FishingEvent) {
            return (FishingEvent)event;
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void processClient(NetworkPacket packet, Client client) {
        if (client.getLevel() == null) {
            return;
        }
        Object object = client.getLevel().entityManager.lock;
        synchronized (object) {
            FishingEvent event = this.getEvent(client.getLevel());
            if (event != null) {
                if (this.secondType == 1) {
                    event.reel();
                } else if (this.secondType == 2) {
                    PacketReader reader = new PacketReader(this.extraContent);
                    int lineIndex = reader.getNextShortUnsigned();
                    int inTicks = reader.getNextShortUnsigned();
                    InventoryItem item = InventoryItem.fromContentPacket(reader.getNextContentPacket());
                    event.addCatch(lineIndex, inTicks, item);
                }
            } else {
                client.network.sendPacket(new PacketRequestLevelEvent(this.eventUniqueID));
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        Level level = server.world.getLevel(client);
        Object object = level.entityManager.lock;
        synchronized (object) {
            FishingEvent event = this.getEvent(level);
            if (event != null && event.getMob() == client.playerMob && this.secondType == 1) {
                event.reel();
                client.refreshAFKTimer();
                server.network.sendToClientsWithEntityExcept(this, event, client);
            }
        }
    }

    public static PacketFishingStatus getReelPacket(FishingEvent event) {
        return new PacketFishingStatus(1, event.getUniqueID(), new Packet());
    }

    public static PacketFishingStatus getUpcomingCatchPacket(FishingEvent event, int lineIndex, int inTicks, InventoryItem item) {
        Packet extraContent = new Packet();
        PacketWriter writer = new PacketWriter(extraContent);
        writer.putNextShortUnsigned(lineIndex);
        writer.putNextShortUnsigned(inTicks);
        writer.putNextContentPacket(InventoryItem.getContentPacket(item));
        return new PacketFishingStatus(2, event.getUniqueID(), extraContent);
    }
}

