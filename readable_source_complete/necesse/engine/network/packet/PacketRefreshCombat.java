/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketRequestMobData;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;

public class PacketRefreshCombat
extends Packet {
    public final int mobUniqueID;
    public final long lastCombatTime;

    public PacketRefreshCombat(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.mobUniqueID = reader.getNextInt();
        this.lastCombatTime = reader.getNextLong();
    }

    public PacketRefreshCombat(Mob mob) {
        this.mobUniqueID = mob.getUniqueID();
        this.lastCombatTime = mob.lastCombatTime;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(this.mobUniqueID);
        writer.putNextLong(this.lastCombatTime);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        if (client.getLevel() == null) {
            return;
        }
        Mob mob = GameUtils.getLevelMob(this.mobUniqueID, client.getLevel());
        if (mob == null) {
            client.network.sendPacket(new PacketRequestMobData(this.mobUniqueID));
        } else {
            mob.lastCombatTime = this.lastCombatTime;
            mob.refreshClientUpdateTime();
        }
    }
}

