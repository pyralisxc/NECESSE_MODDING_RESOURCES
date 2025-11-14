/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.attackHandler.SixShooterAttackHandler;

public class PacketFireSixShooter
extends Packet {
    public final int mobUniqueID;

    public PacketFireSixShooter(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.mobUniqueID = reader.getNextInt();
    }

    public PacketFireSixShooter(Mob target) {
        this.mobUniqueID = target.getUniqueID();
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(this.mobUniqueID);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        if (client.getLevel() == null) {
            return;
        }
        Mob target = GameUtils.getLevelMob(this.mobUniqueID, client.getLevel(), false);
        if (target != null) {
            SixShooterAttackHandler.playFireSound(target);
        }
    }
}

