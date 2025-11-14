/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.actions;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.entity.levelEvent.actions.LevelEventAction;

public abstract class DoubleIntLevelEventAction
extends LevelEventAction {
    public void runAndSend(int value1, int value2) {
        Packet content = new Packet();
        PacketWriter writer = new PacketWriter(content);
        writer.putNextInt(value1);
        writer.putNextInt(value2);
        this.runAndSendAction(content);
    }

    @Override
    public void executePacket(PacketReader reader) {
        int value1 = reader.getNextInt();
        int value2 = reader.getNextInt();
        this.run(value1, value2);
    }

    protected abstract void run(int var1, int var2);
}

