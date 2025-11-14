/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.teams;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.events.ContainerEvent;

public class PvPPublicUpdateEvent
extends ContainerEvent {
    public boolean isPublic;

    public PvPPublicUpdateEvent(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public PvPPublicUpdateEvent(PacketReader reader) {
        super(reader);
        this.isPublic = reader.getNextBoolean();
    }

    @Override
    public void write(PacketWriter writer) {
        writer.putNextBoolean(this.isPublic);
    }
}

