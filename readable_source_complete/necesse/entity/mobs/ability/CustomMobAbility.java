/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ability;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.entity.mobs.ability.MobAbility;

public abstract class CustomMobAbility
extends MobAbility {
    public void runAndSend(Packet content) {
        Packet customContent = new Packet();
        PacketWriter writer = new PacketWriter(customContent);
        writer.putNextContentPacket(content);
        this.runAndSendAbility(customContent);
    }

    @Override
    public void executePacket(PacketReader reader) {
        this.run(reader.getNextContentPacket());
    }

    protected abstract void run(Packet var1);
}

