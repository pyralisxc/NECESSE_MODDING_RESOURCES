/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.actions;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.entity.levelEvent.actions.LevelEventAction;

public abstract class CustomIteratorLevelEventAction
extends LevelEventAction {
    public void runAndSend() {
        Packet content = new Packet();
        this.write(new PacketWriter(content));
        this.runAndSendAction(content);
    }

    @Override
    public void executePacket(PacketReader reader) {
        this.read(reader);
    }

    protected abstract void write(PacketWriter var1);

    protected abstract void read(PacketReader var1);
}

