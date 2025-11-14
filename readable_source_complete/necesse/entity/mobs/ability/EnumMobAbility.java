/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ability;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.entity.mobs.ability.MobAbility;

public abstract class EnumMobAbility<T extends Enum<T>>
extends MobAbility {
    private final Class<T> clazz;

    public EnumMobAbility(Class<T> clazz) {
        this.clazz = clazz;
    }

    public void runAndSend(T value) {
        Packet content = new Packet();
        PacketWriter writer = new PacketWriter(content);
        writer.putNextEnum((Enum)value);
        this.runAndSendAbility(content);
    }

    @Override
    public void executePacket(PacketReader reader) {
        this.run(reader.getNextEnum(this.clazz));
    }

    protected abstract void run(T var1);
}

