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
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.jumping.JumpingMobInterface;

public class PacketMobJump
extends Packet {
    public final int mobUniqueID;
    public final float x;
    public final float y;
    public final float dx;
    public final float dy;

    public PacketMobJump(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.mobUniqueID = reader.getNextInt();
        this.x = reader.getNextFloat();
        this.y = reader.getNextFloat();
        this.dx = reader.getNextFloat();
        this.dy = reader.getNextFloat();
    }

    public PacketMobJump(Mob mob, float dx, float dy) {
        if (!(mob instanceof JumpingMobInterface)) {
            throw new IllegalArgumentException("Mob must implement JumpingMobInterface");
        }
        this.mobUniqueID = mob.getUniqueID();
        this.x = mob.x;
        this.y = mob.y;
        this.dx = dx;
        this.dy = dy;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(this.mobUniqueID);
        writer.putNextFloat(this.x);
        writer.putNextFloat(this.y);
        writer.putNextFloat(dx);
        writer.putNextFloat(dy);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        if (client.getLevel() == null) {
            return;
        }
        Mob mob = client.getLevel().entityManager.mobs.get(this.mobUniqueID, false);
        if (mob instanceof JumpingMobInterface) {
            mob.updatePosFromServer(this.x, this.y, false);
            ((JumpingMobInterface)((Object)mob)).runJump(this.dx, this.dy);
            mob.refreshClientUpdateTime();
        } else {
            client.network.sendPacket(new PacketRequestMobData(this.mobUniqueID));
        }
    }
}

