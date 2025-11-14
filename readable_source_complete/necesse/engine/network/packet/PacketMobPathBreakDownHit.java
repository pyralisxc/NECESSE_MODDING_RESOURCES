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
import necesse.level.maps.LevelObject;

public class PacketMobPathBreakDownHit
extends Packet {
    public final int mobUniqueID;
    public final boolean dir;
    public final boolean horizontal;
    public final int tileX;
    public final int tileY;

    public PacketMobPathBreakDownHit(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.mobUniqueID = reader.getNextInt();
        this.dir = reader.getNextBoolean();
        this.horizontal = reader.getNextBoolean();
        this.tileX = reader.getNextInt();
        this.tileY = reader.getNextInt();
    }

    public PacketMobPathBreakDownHit(Mob mob, LevelObject levelObject, boolean dir, boolean horizontal) {
        this.mobUniqueID = mob.getUniqueID();
        this.dir = dir;
        this.horizontal = horizontal;
        this.tileX = levelObject.tileX;
        this.tileY = levelObject.tileY;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(this.mobUniqueID);
        writer.putNextBoolean(dir);
        writer.putNextBoolean(horizontal);
        writer.putNextInt(this.tileX);
        writer.putNextInt(this.tileY);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        if (client.getLevel() != null) {
            Mob mob = GameUtils.getLevelMob(this.mobUniqueID, client.getLevel());
            if (mob != null) {
                mob.onPathBreakDownHit(new LevelObject(client.getLevel(), this.tileX, this.tileY), this.dir, this.horizontal);
                mob.refreshClientUpdateTime();
            } else {
                client.network.sendPacket(new PacketRequestMobData(this.mobUniqueID));
            }
        }
    }
}

