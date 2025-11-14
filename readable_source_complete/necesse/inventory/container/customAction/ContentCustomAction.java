/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.customAction;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.customAction.ContainerCustomAction;

public abstract class ContentCustomAction
extends ContainerCustomAction {
    public void runAndSend(Packet content) {
        Packet customContent = new Packet();
        PacketWriter writer = new PacketWriter(customContent);
        writer.putNextContentPacket(content);
        this.runAndSendAction(customContent);
    }

    @Override
    public void executePacket(PacketReader reader) {
        this.run(reader.getNextContentPacket());
    }

    protected abstract void run(Packet var1);
}

