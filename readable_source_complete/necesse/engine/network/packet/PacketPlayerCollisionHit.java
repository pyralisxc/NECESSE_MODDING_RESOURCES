/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.GameLog;
import necesse.engine.Settings;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketRemoveMob;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;
import necesse.level.maps.Level;

public class PacketPlayerCollisionHit
extends Packet {
    public final int mobUniqueID;

    public PacketPlayerCollisionHit(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.mobUniqueID = reader.getNextInt();
    }

    public PacketPlayerCollisionHit(Mob hitter) {
        this.mobUniqueID = hitter.getUniqueID();
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(this.mobUniqueID);
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        if (!Settings.strictServerAuthority) {
            Level level = client.getLevel();
            Mob mob = GameUtils.getLevelMob(this.mobUniqueID, level);
            if (mob != null) {
                mob.handleCollisionHit((Mob)client.playerMob, true, client);
            } else {
                GameLog.warn.println(client.getName() + " tried to submit collision hit from unknown mob " + this.mobUniqueID);
                client.sendPacket(new PacketRemoveMob(this.mobUniqueID));
            }
        } else {
            GameLog.warn.println(client.getName() + " tried to submit collision hit while not allowed");
        }
    }
}

