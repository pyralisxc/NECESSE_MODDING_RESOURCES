/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.actions;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.entity.levelEvent.actions.LevelEventAction;

public abstract class BooleanLevelEventAction
extends LevelEventAction {
    public void runAndSend(boolean value) {
        Packet content = new Packet();
        PacketWriter writer = new PacketWriter(content);
        writer.putNextBoolean(value);
        this.runAndSendAction(content);
    }

    @Override
    public void executePacket(PacketReader reader) {
        this.run(reader.getNextBoolean());
    }

    protected abstract void run(boolean var1);
}

