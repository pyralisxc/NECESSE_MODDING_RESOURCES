/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ability;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.entity.mobs.ability.MobAbility;

public abstract class IntMobAbility
extends MobAbility {
    public void runAndSend(int value) {
        Packet content = new Packet();
        PacketWriter writer = new PacketWriter(content);
        writer.putNextInt(value);
        this.runAndSendAbility(content);
    }

    @Override
    public void executePacket(PacketReader reader) {
        int value = reader.getNextInt();
        this.run(value);
    }

    protected abstract void run(int var1);
}

