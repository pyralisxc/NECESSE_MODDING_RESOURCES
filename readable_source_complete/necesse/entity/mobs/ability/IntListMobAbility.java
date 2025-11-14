/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ability;

import java.util.ArrayList;
import java.util.List;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.entity.mobs.ability.MobAbility;

public abstract class IntListMobAbility
extends MobAbility {
    public void runAndSend(List<Integer> list) {
        Packet content = new Packet();
        PacketWriter writer = new PacketWriter(content);
        writer.putNextShortUnsigned(list.size());
        for (Integer value : list) {
            writer.putNextInt(value);
        }
        this.runAndSendAbility(content);
    }

    @Override
    public void executePacket(PacketReader reader) {
        int size = reader.getNextShortUnsigned();
        ArrayList<Integer> list = new ArrayList<Integer>(size);
        for (int i = 0; i < size; ++i) {
            list.add(reader.getNextInt());
        }
        this.run(list);
    }

    protected abstract void run(List<Integer> var1);
}

