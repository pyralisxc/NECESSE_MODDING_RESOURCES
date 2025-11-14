/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ability;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.entity.mobs.ability.MobAbility;

public abstract class FloatPosDirMobAbility
extends MobAbility {
    public void runAndSend(float x, float y, float dx, float dy) {
        Packet content = new Packet();
        PacketWriter writer = new PacketWriter(content);
        writer.putNextFloat(x);
        writer.putNextFloat(y);
        writer.putNextFloat(dx);
        writer.putNextFloat(dy);
        this.runAndSendAbility(content);
    }

    @Override
    public void executePacket(PacketReader reader) {
        float x = reader.getNextFloat();
        float y = reader.getNextFloat();
        float dx = reader.getNextFloat();
        float dy = reader.getNextFloat();
        this.run(x, y, dx, dy);
    }

    protected abstract void run(float var1, float var2, float var3, float var4);
}

