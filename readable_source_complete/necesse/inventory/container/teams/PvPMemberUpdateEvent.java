/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.teams;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.events.ContainerEvent;

public class PvPMemberUpdateEvent
extends ContainerEvent {
    public long auth;
    public boolean added;
    public String name;

    public PvPMemberUpdateEvent(long auth, boolean added, String name) {
        this.auth = auth;
        this.added = added;
        this.name = name;
    }

    public PvPMemberUpdateEvent(PacketReader reader) {
        super(reader);
        this.auth = reader.getNextLong();
        this.added = reader.getNextBoolean();
        if (this.added) {
            this.name = reader.getNextString();
        }
    }

    @Override
    public void write(PacketWriter writer) {
        writer.putNextLong(this.auth);
        writer.putNextBoolean(this.added);
        if (this.added) {
            writer.putNextString(this.name);
        }
    }
}

