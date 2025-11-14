/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.customAction;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.inventory.container.customAction.ContainerCustomAction;

public abstract class EmptyCustomAction
extends ContainerCustomAction {
    public void runAndSend() {
        this.runAndSendAction(new Packet());
    }

    @Override
    public void executePacket(PacketReader reader) {
        this.run();
    }

    protected abstract void run();
}

