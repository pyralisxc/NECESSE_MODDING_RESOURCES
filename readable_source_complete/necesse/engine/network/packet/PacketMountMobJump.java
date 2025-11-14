/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketMobJump;
import necesse.engine.network.packet.PacketRequestMobData;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.jumping.JumpingMobInterface;

public class PacketMountMobJump
extends PacketMobJump {
    public PacketMountMobJump(byte[] data) {
        super(data);
    }

    public PacketMountMobJump(Mob mob, float dx, float dy) {
        super(mob, dx, dy);
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        Mob mob = GameUtils.getLevelMob(this.mobUniqueID, client.getLevel());
        if (mob instanceof JumpingMobInterface && mob.getRider() == client.playerMob) {
            mob.updatePosFromServer(this.x, this.y, false);
            ((JumpingMobInterface)((Object)mob)).runJump(this.dx, this.dy);
            Mob rider = mob.getRider();
            if (rider != null && !rider.isAttacking) {
                rider.setDir(mob.getDir());
            }
            server.network.sendToAllClientsExcept(this, client);
        }
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        if (client.getLevel() == null) {
            return;
        }
        Mob mob = GameUtils.getLevelMob(this.mobUniqueID, client.getLevel());
        if (mob instanceof JumpingMobInterface) {
            mob.setPos(this.x, this.y, false);
            ((JumpingMobInterface)((Object)mob)).runJump(this.dx, this.dy);
            Mob rider = mob.getRider();
            if (rider != null && !rider.isAttacking) {
                rider.setDir(mob.getDir());
            }
        } else {
            client.network.sendPacket(new PacketRequestMobData(this.mobUniqueID));
        }
    }
}

