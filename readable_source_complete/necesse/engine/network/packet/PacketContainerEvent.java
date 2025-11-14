/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.inventory.container.Container;
import necesse.inventory.container.events.ContainerEvent;
import necesse.inventory.container.events.ContainerEventRegistry;

public class PacketContainerEvent
extends Packet {
    public final int eventID;
    public final Packet content;

    public PacketContainerEvent(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.eventID = reader.getNextShort();
        this.content = reader.getNextContentPacket();
    }

    public PacketContainerEvent(ContainerEvent event) {
        this.eventID = ContainerEventRegistry.getID(event);
        if (this.eventID == -1) {
            throw new IllegalStateException("Cannot send unregistered event");
        }
        this.content = new Packet();
        event.write(new PacketWriter(this.content));
        PacketWriter writer = new PacketWriter(this);
        writer.putNextShort((short)this.eventID);
        writer.putNextContentPacket(this.content);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        Container container = client.getContainer();
        if (container != null) {
            Constructor<? extends ContainerEvent> readerConstructor = ContainerEventRegistry.getReaderConstructor(this.eventID);
            try {
                ContainerEvent event = readerConstructor.newInstance(new PacketReader(this.content));
                container.handleEvent(event);
            }
            catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
                System.err.println("Failed in constructing received container event");
                e.printStackTrace();
            }
        }
    }
}

