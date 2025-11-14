/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.networkAction;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.networkAction.NetworkAction;

public abstract class EmptyNetworkAction<R>
extends NetworkAction<R> {
    public void runAndSend() {
        this.runAndSendAction(new Packet());
    }

    @Override
    public void executePacket(PacketReader reader) {
        this.run();
    }

    protected abstract void run();
}

