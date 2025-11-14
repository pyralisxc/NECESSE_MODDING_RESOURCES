/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.customAction;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.customAction.ContainerCustomAction;

public abstract class IntCustomAction
extends ContainerCustomAction {
    public void runAndSend(int value) {
        Packet content = new Packet();
        PacketWriter writer = new PacketWriter(content);
        writer.putNextInt(value);
        this.runAndSendAction(content);
    }

    @Override
    public void executePacket(PacketReader reader) {
        this.run(reader.getNextInt());
    }

    protected abstract void run(int var1);
}

