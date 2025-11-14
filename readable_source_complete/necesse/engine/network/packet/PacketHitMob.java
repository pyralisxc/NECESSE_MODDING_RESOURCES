/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobWasHitEvent;

public class PacketHitMob
extends Packet {
    public final int uniqueID;
    public final float mobX;
    public final float mobY;
    public final float mobDx;
    public final float mobDy;
    public final int mobHealth;
    public final Packet hitEventContent;
    public final int attackerUniqueID;

    public PacketHitMob(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.uniqueID = reader.getNextInt();
        this.mobX = reader.getNextFloat();
        this.mobY = reader.getNextFloat();
        this.mobDx = reader.getNextFloat();
        this.mobDy = reader.getNextFloat();
        this.mobHealth = reader.getNextInt();
        this.hitEventContent = reader.getNextContentPacket();
        this.attackerUniqueID = reader.getNextInt();
    }

    public PacketHitMob(Mob mob, MobWasHitEvent event, Attacker attacker) {
        this.uniqueID = mob.getUniqueID();
        this.mobX = mob.x;
        this.mobY = mob.y;
        this.mobDx = mob.dx;
        this.mobDy = mob.dy;
        this.mobHealth = mob.getHealth();
        this.hitEventContent = new Packet();
        event.writePacket(new PacketWriter(this.hitEventContent));
        this.attackerUniqueID = attacker == null ? -1 : attacker.getAttackerUniqueID();
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(this.uniqueID);
        writer.putNextFloat(this.mobX);
        writer.putNextFloat(this.mobY);
        writer.putNextFloat(this.mobDx);
        writer.putNextFloat(this.mobDy);
        writer.putNextInt(this.mobHealth);
        writer.putNextContentPacket(this.hitEventContent);
        writer.putNextInt(this.attackerUniqueID);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        Mob mob;
        if (client.getLevel() != null && (mob = GameUtils.getLevelMob(this.uniqueID, client.getLevel())) != null && mob.getLevel() != null) {
            if (mob != client.getPlayer()) {
                mob.updatePosFromServer(this.mobX, this.mobY, this.mobDx, this.mobDy, false);
            }
            mob.refreshClientUpdateTime();
            mob.setHealthHidden(this.mobHealth);
            Attacker attacker = this.attackerUniqueID == -1 ? null : GameUtils.getLevelAttacker(this.attackerUniqueID, client.getLevel());
            MobWasHitEvent event = new MobWasHitEvent(mob, attacker, new PacketReader(this.hitEventContent));
            mob.isHit(event, attacker);
        }
    }
}

