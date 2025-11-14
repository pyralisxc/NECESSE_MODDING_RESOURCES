/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.actions;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.entity.levelEvent.actions.LevelEventAction;

public abstract class FloatLevelEventAction
extends LevelEventAction {
    public void runAndSend(float value) {
        Packet content = new Packet();
        PacketWriter writer = new PacketWriter(content);
        writer.putNextFloat(value);
        this.runAndSendAction(content);
    }

    @Override
    public void executePacket(PacketReader reader) {
        float value = reader.getNextFloat();
        this.run(value);
    }

    protected abstract void run(float var1);
}

