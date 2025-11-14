/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.events;

import java.util.function.Predicate;
import java.util.stream.Stream;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.container.BedContainer;
import necesse.inventory.container.events.ContainerEvent;

public class SleepUpdateContainerEvent
extends ContainerEvent {
    public final int totalSleeping;

    public SleepUpdateContainerEvent(Server server, Predicate<ServerClient> filter) {
        Stream<ServerClient> stream = server.streamClients();
        if (filter != null) {
            stream = stream.filter(filter);
        }
        this.totalSleeping = (int)stream.filter(c -> c.getContainer() instanceof BedContainer).count();
    }

    public SleepUpdateContainerEvent(Server server) {
        this(server, null);
    }

    public SleepUpdateContainerEvent(PacketReader reader) {
        super(reader);
        this.totalSleeping = reader.getNextInt();
    }

    @Override
    public void write(PacketWriter writer) {
        writer.putNextInt(this.totalSleeping);
    }
}

