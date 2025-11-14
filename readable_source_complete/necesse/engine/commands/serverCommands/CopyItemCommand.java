/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.serverCommands;

import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.IntParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;

public class CopyItemCommand
extends ModularChatCommand {
    public CopyItemCommand() {
        super("copyitem", "Copies an item and all of its data", PermissionLevel.ADMIN, true, new CmdParameter("slot", new IntParameterHandler(-1), true, new CmdParameter[0]));
    }

    @Override
    public void runModular(Client client, Server server, ServerClient serverClient, Object[] args, String[] errors, CommandLog logs) {
        PlayerInventorySlot invSlot;
        if (serverClient == null) {
            logs.add("Cannot run enchant command from server");
            return;
        }
        int slot = (Integer)args[0];
        PlayerInventorySlot playerInventorySlot = invSlot = slot < 0 ? serverClient.playerMob.getSelectedItemSlot() : new PlayerInventorySlot(serverClient.playerMob.getInv().main, slot);
        if (invSlot.slot >= serverClient.playerMob.getInv().main.getSize()) {
            logs.add("Slot must be below " + serverClient.playerMob.getInv().main.getSize());
            return;
        }
        InventoryItem item = serverClient.playerMob.getInv().getItem(invSlot);
        if (item == null) {
            logs.add("Could not find item in selected slot");
            return;
        }
        serverClient.playerMob.getInv().addItemsDropRemaining(item.copy(), "addloot", serverClient.playerMob, true, true);
        logs.add("Copied " + item.getItemDisplayName());
    }
}

