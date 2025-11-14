/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.misc;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.entity.mobs.ability.MobAbility;
import necesse.entity.mobs.misc.MobProcessObjectHandler;
import necesse.level.gameObject.ProcessObjectHandler;

public class StartMobProcessObjectMobAbility
extends MobAbility {
    public final MobProcessObjectHandler handler;

    public StartMobProcessObjectMobAbility(MobProcessObjectHandler handler) {
        this.handler = handler;
    }

    public void runAndSend(ProcessObjectHandler target) {
        Packet content = new Packet();
        PacketWriter writer = new PacketWriter(content);
        boolean isOnTile = target.isOnTargetTile();
        writer.putNextBoolean(isOnTile);
        if (isOnTile) {
            writer.putNextInt(target.tileX);
            writer.putNextInt(target.tileY);
        }
        writer.putNextShortUnsigned(target.getTimeItTakesInMilliseconds());
        this.handler.target = target;
        this.runAndSendAbility(content);
    }

    @Override
    public void executePacket(PacketReader reader) {
        this.handler.startTime = this.getMob().getTime();
        this.handler.isTargetOnTile = reader.getNextBoolean();
        if (this.handler.isTargetOnTile) {
            this.handler.tileX = reader.getNextInt();
            this.handler.tileY = reader.getNextInt();
        }
        this.handler.processTime = reader.getNextShortUnsigned();
    }
}

