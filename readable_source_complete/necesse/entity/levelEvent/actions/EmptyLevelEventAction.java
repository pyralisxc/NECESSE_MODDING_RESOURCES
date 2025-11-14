/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.actions;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.entity.levelEvent.actions.LevelEventAction;

public abstract class EmptyLevelEventAction
extends LevelEventAction {
    public void runAndSend() {
        this.runAndSendAction(new Packet());
    }

    @Override
    public void executePacket(PacketReader reader) {
        this.run();
    }

    protected abstract void run();
}

