/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketRemoveMob;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;

public class PacketSummonFocus
extends Packet {
    public final int mobUniqueID;

    public PacketSummonFocus(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.mobUniqueID = reader.getNextInt();
    }

    public PacketSummonFocus(Mob mob) {
        this.mobUniqueID = mob == null ? -1 : mob.getUniqueID();
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(this.mobUniqueID);
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        if (!client.checkHasRequestedSelf() || client.isDead()) {
            return;
        }
        Mob lastFocus = client.playerMob.serverFollowersManager.summonFocusMob;
        if (this.mobUniqueID == -1) {
            client.playerMob.serverFollowersManager.summonFocusMob = null;
        } else {
            if (client.getLevel() == null) {
                return;
            }
            client.playerMob.serverFollowersManager.summonFocusMob = GameUtils.getLevelMob(this.mobUniqueID, client.getLevel());
            if (client.playerMob.serverFollowersManager.summonFocusMob == null) {
                client.sendPacket(new PacketRemoveMob(this.mobUniqueID));
            }
        }
        Mob newFocus = client.playerMob.serverFollowersManager.summonFocusMob;
        if (lastFocus != newFocus) {
            client.playerMob.serverFollowersManager.setSummonFocus(newFocus);
            if (newFocus != null) {
                newFocus.onFocussedBySummons(client.playerMob);
            }
        }
    }
}

