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
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;

public class PacketMobUseMana
extends Packet {
    public final int mobUniqueID;
    private final float currentMana;

    public PacketMobUseMana(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.mobUniqueID = reader.getNextInt();
        this.currentMana = reader.getNextFloat();
    }

    public PacketMobUseMana(Mob mob) {
        this.mobUniqueID = mob.getUniqueID();
        this.currentMana = mob.getMana();
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(this.mobUniqueID);
        writer.putNextFloat(this.currentMana);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        if (client.getLevel() != null) {
            Mob mob = GameUtils.getLevelMob(this.mobUniqueID, client.getLevel());
            if (mob == null) {
                client.network.sendPacket(new PacketRequestMobData(this.mobUniqueID));
            } else {
                mob.setManaHidden(this.currentMana);
                mob.lastManaSpentTime = mob.getWorldEntity().getTime();
                if (this.currentMana <= 0.0f) {
                    mob.isManaExhausted = true;
                    mob.buffManager.addBuff(new ActiveBuff(BuffRegistry.Debuffs.MANA_EXHAUSTION, mob, 1000, null), false);
                }
            }
        }
    }
}

