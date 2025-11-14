/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.events;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.entity.events.EntityEvent;

public abstract class EmptyEntityEvent
extends EntityEvent {
    public void runAndSend() {
        this.runAndSendAbility(new Packet());
    }

    @Override
    public void executePacket(PacketReader reader) {
        this.run();
    }

    protected abstract void run();
}

