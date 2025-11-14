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
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.DeepfrostSetBonusBuff;
import necesse.level.maps.Level;

public class PacketDeepFrostAimUpdate
extends Packet {
    public final int mobUniqueID;
    public final int levelX;
    public final int levelY;

    public PacketDeepFrostAimUpdate(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.mobUniqueID = reader.getNextInt();
        this.levelX = reader.getNextInt();
        this.levelY = reader.getNextInt();
    }

    public PacketDeepFrostAimUpdate(Mob mob, int levelX, int levelY) {
        this.mobUniqueID = mob.getUniqueID();
        this.levelX = levelX;
        this.levelY = levelY;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(this.mobUniqueID);
        writer.putNextInt(levelX);
        writer.putNextInt(levelY);
    }

    protected boolean update(Level level) {
        ActiveBuff buff;
        Mob mob = GameUtils.getLevelMob(this.mobUniqueID, level);
        if (mob != null && (buff = mob.buffManager.getBuff(BuffRegistry.SetBonuses.GHOSTLY_ARCHER)) != null) {
            DeepfrostSetBonusBuff.updateMousePos(buff, this.levelX, this.levelY);
            return true;
        }
        return false;
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        if (!client.checkHasRequestedSelf() || client.isDead()) {
            return;
        }
        client.checkSpawned();
        if (this.update(client.getLevel())) {
            server.network.sendToClientsWithEntity(this, client.playerMob);
        }
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        if (!this.update(client.getLevel())) {
            client.network.sendPacket(new PacketRequestMobData(this.mobUniqueID));
        }
    }
}

