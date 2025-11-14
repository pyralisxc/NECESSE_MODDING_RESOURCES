/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.actions;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.entity.levelEvent.actions.LevelEventAction;

public abstract class CoordinateLevelEventAction
extends LevelEventAction {
    public void runAndSend(int x, int y) {
        Packet content = new Packet();
        PacketWriter writer = new PacketWriter(content);
        writer.putNextInt(x);
        writer.putNextInt(y);
        this.runAndSendAction(content);
    }

    @Override
    public void executePacket(PacketReader reader) {
        int x = reader.getNextInt();
        int y = reader.getNextInt();
        this.run(x, y);
    }

    protected abstract void run(int var1, int var2);
}

