/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.networkField;

import necesse.engine.network.Packet;
import necesse.engine.network.packet.PacketMobNetworkFields;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.networkField.NetworkFieldRegistry;

public class MobNetworkFieldRegistry
extends NetworkFieldRegistry {
    private final Mob mob;

    public MobNetworkFieldRegistry(Mob mob) {
        this.mob = mob;
    }

    @Override
    public void sendUpdatePacket(Packet content) {
        this.mob.getLevel().getServer().network.sendToClientsWithEntity(new PacketMobNetworkFields(this.mob, content), this.mob);
    }

    @Override
    public String getDebugIdentifierString() {
        return this.mob.toString();
    }
}

