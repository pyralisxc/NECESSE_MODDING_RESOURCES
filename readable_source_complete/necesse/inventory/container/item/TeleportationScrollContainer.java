/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.item;

import necesse.engine.network.NetworkClient;
import necesse.engine.network.Packet;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ItemRegistry;
import necesse.inventory.container.TeleportToTeamContainer;

public class TeleportationScrollContainer
extends TeleportToTeamContainer {
    public TeleportationScrollContainer(NetworkClient client, int uniqueSeed, Packet contentPacket) {
        super(client, uniqueSeed, contentPacket);
    }

    @Override
    public void performTeleport(ServerClient client, ServerClient target) {
        if (client.playerMob.getInv().removeItems(ItemRegistry.getItem("teleportationscroll"), 1, false, false, false, false, "teleportationscroll") > 0) {
            client.getLevel().entityManager.events.add(this.getTeleportEvent(client, target, 0, 10.0f));
        } else {
            System.out.println(client.getName() + " tried to teleport but had no teleport scroll");
        }
        client.closeContainer(false);
    }
}

