/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container;

import necesse.engine.network.NetworkClient;
import necesse.engine.network.Packet;
import necesse.engine.network.server.AdventureParty;
import necesse.inventory.PlayerInventoryManager;
import necesse.inventory.container.Container;
import necesse.inventory.container.customAction.CommandPartyAttackAction;
import necesse.inventory.container.customAction.CommandPartyDisbandAction;
import necesse.inventory.container.customAction.CommandPartyFollowMeAction;
import necesse.inventory.container.customAction.CommandPartyGuardAction;
import necesse.inventory.container.slots.PartyInventoryContainerSlot;

public class AdventurePartyConfigContainer
extends Container {
    public int PARTY_SLOTS_START = -1;
    public int PARTY_SLOTS_END = -1;
    public final CommandPartyFollowMeAction commandFollowMeAction;
    public final CommandPartyDisbandAction commandDisbandAction;
    public final CommandPartyGuardAction commandGuardAction;
    public final CommandPartyAttackAction commandAttackAction;

    public AdventurePartyConfigContainer(NetworkClient client, int uniqueSeed, Packet content) {
        super(client, uniqueSeed);
        AdventureParty party = null;
        if (client.isServer()) {
            party = client.getServerClient().adventureParty;
        } else if (client.isClient() && client.getClientClient().isLocalClient()) {
            party = client.getClientClient().getClient().adventureParty;
        }
        for (int i = 0; i < PlayerInventoryManager.MAX_PARTY_INVENTORY_SIZE; ++i) {
            int index = this.addSlot(new PartyInventoryContainerSlot(client.playerMob.getInv().party, i, party));
            if (this.PARTY_SLOTS_START == -1) {
                this.PARTY_SLOTS_START = index;
            }
            if (this.PARTY_SLOTS_END == -1) {
                this.PARTY_SLOTS_END = index;
            }
            this.PARTY_SLOTS_START = Math.min(this.PARTY_SLOTS_START, index);
            this.PARTY_SLOTS_END = Math.max(this.PARTY_SLOTS_END, index);
        }
        this.addInventoryQuickTransfer(this.PARTY_SLOTS_START, this.PARTY_SLOTS_END);
        this.commandFollowMeAction = this.registerAction(new CommandPartyFollowMeAction(this));
        this.commandDisbandAction = this.registerAction(new CommandPartyDisbandAction(this));
        this.commandGuardAction = this.registerAction(new CommandPartyGuardAction(this));
        this.commandAttackAction = this.registerAction(new CommandPartyAttackAction(this));
    }
}

