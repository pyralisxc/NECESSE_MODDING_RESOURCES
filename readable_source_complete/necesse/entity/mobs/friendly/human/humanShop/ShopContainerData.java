/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.friendly.human.humanShop;

import necesse.engine.network.Packet;
import necesse.engine.network.packet.PacketOpenContainer;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.friendly.human.humanShop.ShopManager;

public class ShopContainerData {
    public final Packet content;
    public final ShopManager shopManager;

    public ShopContainerData(Packet content, ShopManager shopManager) {
        this.content = content;
        this.shopManager = shopManager;
    }

    public PacketOpenContainer getPacket(int containerID, Mob mob) {
        return PacketOpenContainer.Mob(containerID, mob, this.content, this);
    }
}

