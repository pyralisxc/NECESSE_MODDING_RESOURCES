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

public class PacketMobMount
extends Packet {
    public final int mounterUniqueID;
    public final int mountUniqueID;
    public final boolean setMounterPos;
    public final float mounterX;
    public final float mounterY;

    public PacketMobMount(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.mounterUniqueID = reader.getNextInt();
        this.mountUniqueID = reader.getNextInt();
        this.setMounterPos = reader.getNextBoolean();
        if (this.setMounterPos) {
            this.mounterX = reader.getNextFloat();
            this.mounterY = reader.getNextFloat();
        } else {
            this.mounterX = -1.0f;
            this.mounterY = -1.0f;
        }
    }

    public PacketMobMount(int mounterUniqueID, int mountUniqueID, boolean setMounterPos, float mounterX, float mounterY) {
        this.mounterUniqueID = mounterUniqueID;
        this.mountUniqueID = mountUniqueID;
        this.mounterX = mounterX;
        this.mounterY = mounterY;
        this.setMounterPos = setMounterPos;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(mounterUniqueID);
        writer.putNextInt(mountUniqueID);
        writer.putNextBoolean(setMounterPos);
        if (setMounterPos) {
            writer.putNextFloat(mounterX);
            writer.putNextFloat(mounterY);
        }
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        if (client.getLevel() == null) {
            return;
        }
        Mob mounter = GameUtils.getLevelMob(this.mounterUniqueID, client.getLevel());
        if (mounter != null) {
            if (this.mountUniqueID == -1) {
                mounter.dismount();
            } else {
                Mob mount = GameUtils.getLevelMob(this.mountUniqueID, client.getLevel());
                if (mount != null) {
                    mounter.mount(mount, this.setMounterPos, this.mounterX, this.mounterY, true);
                } else {
                    client.network.sendPacket(new PacketRequestMobData(this.mountUniqueID));
                    if (mounter.mount != -1) {
                        mounter.dismount();
                    }
                    mounter.mount = this.mountUniqueID;
                    mounter.buffManager.updateBuffs();
                }
            }
        } else {
            client.network.sendPacket(new PacketRequestMobData(this.mounterUniqueID));
        }
    }
}

