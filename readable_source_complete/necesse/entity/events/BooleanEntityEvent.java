/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.events;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.entity.events.EntityEvent;

public abstract class BooleanEntityEvent
extends EntityEvent {
    public void runAndSend(boolean value) {
        Packet content = new Packet();
        PacketWriter writer = new PacketWriter(content);
        writer.putNextBoolean(value);
        this.runAndSendAbility(content);
    }

    @Override
    public void executePacket(PacketReader reader) {
        this.run(reader.getNextBoolean());
    }

    protected abstract void run(boolean var1);
}

