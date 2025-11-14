/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.misc;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.entity.Entity;
import necesse.entity.mobs.LevelMob;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ability.MobAbility;
import necesse.entity.mobs.misc.MobProcessMobHandler;
import necesse.entity.mobs.misc.ProcessMobHandler;

public class StartMobProcessMobMobAbility<C extends Mob, T extends Mob>
extends MobAbility {
    public final MobProcessMobHandler<C, T> handler;

    public StartMobProcessMobMobAbility(MobProcessMobHandler<C, T> handler) {
        this.handler = handler;
    }

    public void runAndSend(ProcessMobHandler<C, T> handler) {
        Packet content = new Packet();
        PacketWriter writer = new PacketWriter(content);
        writer.putNextInt(((Entity)handler.target).getUniqueID());
        writer.putNextShortUnsigned(handler.getTimeItTakesInMilliseconds());
        this.handler.target = handler;
        this.runAndSendAbility(content);
    }

    @Override
    public void executePacket(PacketReader reader) {
        this.handler.startTime = this.getMob().getTime();
        this.handler.targetMob = new LevelMob<int>(reader.getNextInt());
        this.handler.processTime = reader.getNextShortUnsigned();
    }
}

