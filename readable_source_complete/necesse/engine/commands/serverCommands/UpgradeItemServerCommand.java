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
import necesse.inventory.item.upgradeUtils.UpgradableItem;

public class UpgradeItemServerCommand
extends ModularChatCommand {
    public UpgradeItemServerCommand() {
        super("upgrade", "Clears, sets or sets the tier on an item (use -1 slot for selected item)", PermissionLevel.ADMIN, true, new CmdParameter("slot", new IntParameterHandler(-1), true, new CmdParameter[0]), new CmdParameter("tier", new IntParameterHandler()));
    }

    @Override
    public void runModular(Client client, Server server, ServerClient serverClient, Object[] args, String[] errors, CommandLog logs) {
        PlayerInventorySlot invSlot;
        if (serverClient == null) {
            logs.add("Cannot run upgrade command from server");
            return;
        }
        int slot = (Integer)args[0] - 1;
        if (slot == -1) {
            slot = 9;
        }
        int tier = (Integer)args[1];
        PlayerInventorySlot playerInventorySlot = invSlot = slot < 0 ? serverClient.playerMob.getSelectedItemSlot() : new PlayerInventorySlot(serverClient.playerMob.getInv().main, slot);
        if (invSlot.slot >= serverClient.playerMob.getInv().main.getSize()) {
            logs.add("Slot must be below " + serverClient.playerMob.getInv().main.getSize());
            return;
        }
        InventoryItem item = serverClient.playerMob.getInv().getItem(invSlot);
        if (item == null || !(item.item instanceof UpgradableItem)) {
            logs.add("Invalid item selected");
            return;
        }
        item.item.setUpgradeTier(item, tier);
        logs.add("Upgraded " + item.getItemDisplayName() + " to tier " + tier);
        invSlot.getInv(serverClient.playerMob.getInv()).markDirty(invSlot.slot);
    }
}

