/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.events;

import java.util.function.Function;
import java.util.function.Predicate;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketContainerEvent;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.level.maps.Level;

public abstract class ContainerEvent {
    public ContainerEvent() {
    }

    public ContainerEvent(PacketReader reader) {
    }

    public abstract void write(PacketWriter var1);

    public static void constructApplyAndSendToClients(Server server, Function<ServerClient, ContainerEvent> constructor) {
        server.streamClients().forEach(c -> {
            ContainerEvent event = (ContainerEvent)constructor.apply((ServerClient)c);
            if (c.getContainer().shouldReceiveEvent(event)) {
                c.sendPacket(new PacketContainerEvent(event));
                c.getContainer().handleEvent(event);
            }
        });
    }

    public void applyAndSendToClients(Server server, Predicate<ServerClient> filter) {
        PacketContainerEvent packet = new PacketContainerEvent(this);
        server.streamClients().filter(filter).filter(c -> c.getContainer().shouldReceiveEvent(this)).forEach(c -> {
            c.sendPacket(packet);
            c.getContainer().handleEvent(this);
        });
    }

    public void applyAndSendToClient(ServerClient client) {
        client.sendPacket(new PacketContainerEvent(this));
        client.getContainer().handleEvent(this);
    }

    public void applyAndSendToAllClients(Server server) {
        this.applyAndSendToClients(server, c -> true);
    }

    public void applyAndSendToClientsAt(ServerClient client) {
        this.applyAndSendToClients(client.getServer(), c -> c.isSamePlace(client));
    }

    public void applyAndSendToClientsAt(Level level) {
        if (level.isServer()) {
            this.applyAndSendToClients(level.getServer(), c -> c.isSamePlace(level));
        }
    }

    public void applyAndSendToClientsAtExcept(ServerClient client) {
        this.applyAndSendToClients(client.getServer(), c -> c.isSamePlace(client) && c != client);
    }

    public void applyAndSendToClientsAtExcept(Level level, ServerClient exception) {
        if (level.isServer()) {
            this.applyAndSendToClients(level.getServer(), c -> c.isSamePlace(level) && c != exception);
        }
    }
}

