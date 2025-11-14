/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.teams;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.events.ContainerEvent;

public class PvPOwnerUpdateEvent
extends ContainerEvent {
    public long ownerAuth;

    public PvPOwnerUpdateEvent(long newOwner) {
        this.ownerAuth = newOwner;
    }

    public PvPOwnerUpdateEvent(PacketReader reader) {
        super(reader);
        this.ownerAuth = reader.getNextLong();
    }

    @Override
    public void write(PacketWriter writer) {
        writer.putNextLong(this.ownerAuth);
    }
}

