/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.customAction;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.customAction.ContainerCustomAction;

public abstract class LongBooleanCustomAction
extends ContainerCustomAction {
    public void runAndSend(long value, boolean bool) {
        Packet content = new Packet();
        PacketWriter writer = new PacketWriter(content);
        writer.putNextLong(value);
        writer.putNextBoolean(bool);
        this.runAndSendAction(content);
    }

    @Override
    public void executePacket(PacketReader reader) {
        long value = reader.getNextLong();
        boolean bool = reader.getNextBoolean();
        this.run(value, bool);
    }

    protected abstract void run(long var1, boolean var3);
}

