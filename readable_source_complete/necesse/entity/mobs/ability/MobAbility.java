/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ability;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.packet.PacketMobAbility;
import necesse.entity.mobs.Mob;

public abstract class MobAbility {
    private Mob mob;
    private int id = -1;

    protected void onRegister(Mob mob, int id) {
        if (this.mob != null) {
            throw new IllegalStateException("Cannot register same ability twice");
        }
        this.mob = mob;
        this.id = id;
    }

    public Mob getMob() {
        return this.mob;
    }

    public abstract void executePacket(PacketReader var1);

    protected void runAndSendAbility(Packet content) {
        if (this.mob != null) {
            if (this.mob.isServer()) {
                this.executePacket(new PacketReader(content));
                this.mob.getLevel().getServer().network.sendToClientsWithEntity(new PacketMobAbility(this.mob, this.id, content), this.mob);
            } else if (!this.mob.isClient()) {
                this.executePacket(new PacketReader(content));
            } else {
                System.err.println("Cannot send mob abilities from client. Only server handles when mob abilities are ran");
            }
        } else {
            System.err.println("Cannot run ability that hasn't been registered");
        }
    }
}

