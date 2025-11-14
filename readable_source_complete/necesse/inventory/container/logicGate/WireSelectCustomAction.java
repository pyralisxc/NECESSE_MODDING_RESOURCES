/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.logicGate;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.customAction.ContainerCustomAction;

public abstract class WireSelectCustomAction
extends ContainerCustomAction {
    public void runAndSend(boolean[] wires) {
        Packet content = new Packet();
        PacketWriter writer = new PacketWriter(content);
        for (boolean wire : wires) {
            writer.putNextBoolean(wire);
        }
        this.runAndSendAction(content);
    }

    @Override
    public void executePacket(PacketReader reader) {
        boolean[] wires = new boolean[4];
        for (int i = 0; i < wires.length; ++i) {
            wires[i] = reader.getNextBoolean();
        }
        this.run(wires);
    }

    protected abstract void run(boolean[] var1);
}

