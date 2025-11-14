/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.commands.PermissionLevel;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;

public class PacketMobDebugMove
extends Packet {
    public final int mobUniqueID;
    public final int x;
    public final int y;
    public final int dir;

    public PacketMobDebugMove(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.mobUniqueID = reader.getNextInt();
        this.x = reader.getNextInt();
        this.y = reader.getNextInt();
        this.dir = reader.getNextInt();
    }

    public PacketMobDebugMove(Mob mob, int x, int y, int dir) {
        this.mobUniqueID = mob.getUniqueID();
        this.x = x;
        this.y = y;
        this.dir = dir;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(this.mobUniqueID);
        writer.putNextInt(x);
        writer.putNextInt(y);
        writer.putNextInt(dir);
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        if (client.getPermissionLevel().getLevel() >= PermissionLevel.ADMIN.getLevel()) {
            if (server.world.settings.cheatsAllowedOrHidden()) {
                Mob mob = GameUtils.getLevelMob(this.mobUniqueID, client.getLevel());
                if (mob != null) {
                    mob.setPos(this.x, this.y, true);
                    mob.setDir(this.dir);
                    mob.sendMovementPacket(true);
                } else {
                    System.out.println(client.getName() + " tried to move mob, but couldn't find mob with uniqueID: " + this.mobUniqueID);
                }
            } else {
                System.out.println(client.getName() + " tried to move a mob, but cheats aren't allowed");
            }
        } else {
            System.out.println(client.getName() + " tried to move a mob, but isn't admin");
        }
    }
}

