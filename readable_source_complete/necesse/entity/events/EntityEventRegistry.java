/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.events;

import java.util.ArrayList;
import necesse.engine.GameState;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.server.Server;
import necesse.entity.events.EntityEvent;

public abstract class EntityEventRegistry<C extends GameState> {
    protected C entity;
    private boolean registryOpen = true;
    private final ArrayList<EntityEvent> events = new ArrayList();

    public EntityEventRegistry(C entity) {
        this.entity = entity;
    }

    public void closeRegistry() {
        this.registryOpen = false;
    }

    public abstract String getDebugIdentifierString();

    protected String getClosedErrorMessage() {
        return "Cannot register entity events after initialization, must be done in constructor";
    }

    public final void runEvent(int id, PacketReader reader) {
        if (id < 0 || id >= this.events.size()) {
            System.err.println("Could not find and run event " + id + " for " + this.getDebugIdentifierString());
        } else {
            this.events.get(id).executePacket(reader);
        }
    }

    public abstract void sendEventPacket(Server var1, EntityEvent var2, Packet var3);

    protected void runAndSendAbility(EntityEvent event, Packet content) {
        if (this.entity.isServer()) {
            event.executePacket(new PacketReader(content));
            this.sendEventPacket(this.entity.getServer(), event, content);
        } else if (!this.entity.isClient()) {
            event.executePacket(new PacketReader(content));
        } else {
            System.err.println("Cannot send object entity events from client. Only server handles when object entity events are ran");
        }
    }

    public final <T extends EntityEvent> T registerEvent(T event) {
        if (!this.registryOpen) {
            throw new IllegalStateException(this.getClosedErrorMessage());
        }
        if (this.events.size() >= Short.MAX_VALUE) {
            throw new IllegalStateException("Cannot register any more events for " + this.getDebugIdentifierString());
        }
        this.events.add(event);
        event.onRegister(this, this.events.size() - 1);
        return event;
    }
}

