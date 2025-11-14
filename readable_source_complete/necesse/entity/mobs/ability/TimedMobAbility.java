/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ability;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.entity.mobs.ability.MobAbility;

public abstract class TimedMobAbility
extends MobAbility {
    public void runAndSend(long time) {
        Packet content = new Packet();
        PacketWriter writer = new PacketWriter(content);
        writer.putNextLong(time);
        this.runAndSendAbility(content);
    }

    @Override
    public void executePacket(PacketReader reader) {
        long time = reader.getNextLong();
        this.run(time);
    }

    protected abstract void run(long var1);
}

