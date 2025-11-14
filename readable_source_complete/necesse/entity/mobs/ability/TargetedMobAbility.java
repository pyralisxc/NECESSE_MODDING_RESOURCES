/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ability;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ability.MobAbility;

public abstract class TargetedMobAbility
extends MobAbility {
    public void runAndSend(Mob target) {
        Packet content = new Packet();
        PacketWriter writer = new PacketWriter(content);
        writer.putNextInt(target.getUniqueID());
        this.runAndSendAbility(content);
    }

    @Override
    public void executePacket(PacketReader reader) {
        int uniqueID = reader.getNextInt();
        Mob target = GameUtils.getLevelMob(uniqueID, this.getMob().getLevel());
        this.run(target);
    }

    protected abstract void run(Mob var1);
}

