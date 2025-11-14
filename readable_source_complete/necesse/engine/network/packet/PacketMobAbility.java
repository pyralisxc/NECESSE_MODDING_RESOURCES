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

public class PacketMobAbility
extends Packet {
    public final int levelIdentifierHashCode;
    public final int mobUniqueID;
    public final int abilityID;
    public final Packet abilityContent;

    public PacketMobAbility(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.levelIdentifierHashCode = reader.getNextInt();
        this.mobUniqueID = reader.getNextInt();
        this.abilityID = reader.getNextShort();
        this.abilityContent = reader.getNextContentPacket();
    }

    public PacketMobAbility(Mob mob, int abilityID, Packet content) {
        this.levelIdentifierHashCode = mob.getLevel().getIdentifierHashCode();
        this.mobUniqueID = mob.getUniqueID();
        this.abilityID = abilityID;
        this.abilityContent = content;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(this.levelIdentifierHashCode);
        writer.putNextInt(this.mobUniqueID);
        writer.putNextShort((short)abilityID);
        writer.putNextContentPacket(this.abilityContent);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        if (!client.levelManager.isLevelLoaded(this.levelIdentifierHashCode)) {
            return;
        }
        Mob mob = GameUtils.getLevelMob(this.mobUniqueID, client.getLevel(), false);
        if (mob != null) {
            mob.runAbility(this.abilityID, new PacketReader(this.abilityContent));
            mob.refreshClientUpdateTime();
        } else {
            client.network.sendPacket(new PacketRequestMobData(this.mobUniqueID));
        }
    }
}

