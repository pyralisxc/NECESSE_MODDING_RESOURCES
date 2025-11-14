/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.events;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.events.ContainerEvent;

public class AdventurePartyChangedEvent
extends ContainerEvent {
    public AdventurePartyChangedEvent() {
    }

    public AdventurePartyChangedEvent(PacketReader reader) {
        super(reader);
    }

    @Override
    public void write(PacketWriter writer) {
    }
}

