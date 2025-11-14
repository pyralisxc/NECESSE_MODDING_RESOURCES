/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ability;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.entity.mobs.ability.MobAbility;

public abstract class BooleanMobAbility
extends MobAbility {
    public void runAndSend(boolean value) {
        Packet content = new Packet();
        PacketWriter writer = new PacketWriter(content);
        writer.putNextBoolean(value);
        this.runAndSendAbility(content);
    }

    @Override
    public void executePacket(PacketReader reader) {
        this.run(reader.getNextBoolean());
    }

    protected abstract void run(boolean var1);
}

