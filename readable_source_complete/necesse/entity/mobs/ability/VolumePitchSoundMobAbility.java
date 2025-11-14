/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ability;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.entity.mobs.ability.MobAbility;

public abstract class VolumePitchSoundMobAbility
extends MobAbility {
    public void runAndSend(float volume, float pitch) {
        Packet content = new Packet();
        PacketWriter writer = new PacketWriter(content);
        writer.putNextFloat(volume);
        writer.putNextFloat(pitch);
        this.runAndSendAbility(content);
    }

    @Override
    public void executePacket(PacketReader reader) {
        float volume = reader.getNextFloat();
        float pitch = reader.getNextFloat();
        this.run(volume, pitch);
    }

    protected abstract void run(float var1, float var2);
}

