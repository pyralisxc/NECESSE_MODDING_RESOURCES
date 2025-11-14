/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ability;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.entity.mobs.ability.MobAbility;

public abstract class EmptyMobAbility
extends MobAbility {
    public void runAndSend() {
        this.runAndSendAbility(new Packet());
    }

    @Override
    public void executePacket(PacketReader reader) {
        this.run();
    }

    protected abstract void run();
}

