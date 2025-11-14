/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.mob;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.inventory.container.events.ContainerEvent;
import necesse.inventory.container.mob.ShopContainer;

public class ShopContainerPartyUpdateEvent
extends ContainerEvent {
    public int mobUniqueID;
    public boolean canJoinAdventureParties;
    public boolean isInAdventureParty;
    public boolean isInYourAdventureParty;

    public ShopContainerPartyUpdateEvent(HumanMob mob, ServerClient client) {
        this.mobUniqueID = mob.getUniqueID();
        this.canJoinAdventureParties = mob.canJoinAdventureParties;
        this.isInAdventureParty = mob.adventureParty.isInAdventureParty();
        this.isInYourAdventureParty = mob.adventureParty.getServerClient() == client;
    }

    public ShopContainerPartyUpdateEvent(PacketReader reader) {
        super(reader);
        this.canJoinAdventureParties = reader.getNextBoolean();
        this.isInAdventureParty = reader.getNextBoolean();
        this.isInYourAdventureParty = reader.getNextBoolean();
    }

    @Override
    public void write(PacketWriter writer) {
        writer.putNextBoolean(this.canJoinAdventureParties);
        writer.putNextBoolean(this.isInAdventureParty);
        writer.putNextBoolean(this.isInYourAdventureParty);
    }

    public void applyUpdate(ShopContainer container) {
        container.canJoinAdventureParties = this.canJoinAdventureParties;
        container.isInAdventureParty = this.isInAdventureParty;
        container.isInYourAdventureParty = this.isInYourAdventureParty;
    }

    public static void sendAndApplyUpdate(HumanMob mob) {
        if (mob.isServer()) {
            ShopContainerPartyUpdateEvent.constructApplyAndSendToClients(mob.getLevel().getServer(), client -> new ShopContainerPartyUpdateEvent(mob, (ServerClient)client));
        }
    }
}

