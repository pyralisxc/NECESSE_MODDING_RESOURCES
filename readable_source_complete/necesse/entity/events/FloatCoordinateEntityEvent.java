/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.events;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.entity.events.EntityEvent;

public abstract class FloatCoordinateEntityEvent
extends EntityEvent {
    public void runAndSend(float x, float y) {
        Packet content = new Packet();
        PacketWriter writer = new PacketWriter(content);
        writer.putNextFloat(x);
        writer.putNextFloat(y);
        this.runAndSendAbility(content);
    }

    @Override
    public void executePacket(PacketReader reader) {
        float x = reader.getNextFloat();
        float y = reader.getNextFloat();
        this.run(x, y);
    }

    protected abstract void run(float var1, float var2);
}

