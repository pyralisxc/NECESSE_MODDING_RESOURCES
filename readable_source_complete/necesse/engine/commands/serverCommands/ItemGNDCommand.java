/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.serverCommands;

import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.IntParameterHandler;
import necesse.engine.commands.parameterHandlers.PresetStringParameterHandler;
import necesse.engine.commands.parameterHandlers.StringParameterHandler;
import necesse.engine.commands.parameterHandlers.gnd.GNDItemParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.gameNetworkData.GNDItem;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;

public class ItemGNDCommand
extends ModularChatCommand {
    public ItemGNDCommand() {
        super("itemgnd", "Gets or sets item GND data", PermissionLevel.OWNER, true, new CmdParameter("slot", new IntParameterHandler(-1), true, new CmdParameter[0]), new CmdParameter("set/get/clear", new PresetStringParameterHandler("set", "get", "clear")), new CmdParameter("key", new StringParameterHandler(), true, new CmdParameter("value", GNDItemParameterHandler.getMultiParameterHandler(), true, new CmdParameter[0])));
    }

    @Override
    public void runModular(Client client, Server server, ServerClient serverClient, Object[] args, String[] errors, CommandLog logs) {
        PlayerInventorySlot invSlot;
        if (serverClient == null) {
            logs.add("Cannot run inspect item command from server");
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
            logs.add("Empty item selected");
            return;
        }
        GNDItemMap gndData = item.getGndData();
        String action = (String)args[1];
        String key = (String)args[2];
        GNDItem setItem = GNDItemParameterHandler.getReturnedItem(args[3]);
        switch (action) {
            case "get": {
                if (key == null) {
                    if (gndData.getMapSize() == 0) {
                        logs.add(item.getItemDisplayName() + " has no GND data");
                        break;
                    }
                    logs.add(item.getItemDisplayName() + " GND data:");
                    for (String dataKey : gndData.getKeyStringSet()) {
                        logs.add(dataKey + ": " + gndData.getItem(dataKey).toString());
                    }
                    break;
                }
                GNDItem getItem = gndData.getItem(key);
                if (getItem != null) {
                    logs.add(key + ": " + gndData.getItem(key).toString());
                    break;
                }
                logs.add(item.getItemDisplayName() + " has no GND data with key " + key);
                break;
            }
            case "clear": {
                if (key == null) {
                    item.setGndData(new GNDItemMap());
                    logs.add("Cleared all GND data on " + item.getItemDisplayName());
                    invSlot.markDirty(serverClient.playerMob.getInv());
                    return;
                }
                gndData.setItem(key, null);
                logs.add("Cleared GND key " + key + " on " + item.getItemDisplayName());
                invSlot.markDirty(serverClient.playerMob.getInv());
                break;
            }
            case "set": {
                if (key == null) {
                    logs.add("Must supply key for set action");
                    return;
                }
                gndData.setItem(key, setItem);
                logs.add("Set key " + key + " to " + (setItem == null ? "null" : setItem.getStringID()) + " " + setItem + " on " + item.getItemDisplayName());
                invSlot.markDirty(serverClient.playerMob.getInv());
            }
        }
    }
}

