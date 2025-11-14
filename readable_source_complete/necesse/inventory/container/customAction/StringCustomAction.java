/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.customAction;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.customAction.ContainerCustomAction;

public abstract class StringCustomAction
extends ContainerCustomAction {
    public void runAndSend(String string) {
        Packet content = new Packet();
        PacketWriter writer = new PacketWriter(content);
        writer.putNextString(string);
        this.runAndSendAction(content);
    }

    @Override
    public void executePacket(PacketReader reader) {
        this.run(reader.getNextString());
    }

    protected abstract void run(String var1);
}

