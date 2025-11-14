/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.serverCommands;

import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.EnchantmentParameterHandler;
import necesse.engine.commands.parameterHandlers.IntParameterHandler;
import necesse.engine.commands.parameterHandlers.PresetStringParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ItemRegistry;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.enchants.Enchantable;
import necesse.inventory.enchants.ItemEnchantment;

public class EnchantServerCommand
extends ModularChatCommand {
    public EnchantServerCommand() {
        super("enchant", "Clears, sets or gives a random enchant (use -1 slot for selected item)", PermissionLevel.ADMIN, true, new CmdParameter("clear/set/random", new PresetStringParameterHandler("clear", "set", "random")), new CmdParameter("slot", new IntParameterHandler(-1), true, new CmdParameter[0]), new CmdParameter("enchantID", new EnchantmentParameterHandler(), true, new CmdParameter[0]));
    }

    @Override
    public void runModular(Client client, Server server, ServerClient serverClient, Object[] args, String[] errors, CommandLog logs) {
        PlayerInventorySlot invSlot;
        if (serverClient == null) {
            logs.add("Cannot run enchant command from server");
            return;
        }
        String mode = (String)args[0];
        int slot = (Integer)args[1] - 1;
        if (slot == -1) {
            slot = 9;
        }
        ItemEnchantment enchantment = (ItemEnchantment)args[2];
        PlayerInventorySlot playerInventorySlot = invSlot = slot < 0 ? serverClient.playerMob.getSelectedItemSlot() : new PlayerInventorySlot(serverClient.playerMob.getInv().main, slot);
        if (invSlot.slot >= serverClient.playerMob.getInv().main.getSize()) {
            logs.add("Slot must be below " + serverClient.playerMob.getInv().main.getSize());
            return;
        }
        InventoryItem item = serverClient.playerMob.getInv().getItem(invSlot);
        if (item == null || !item.item.isEnchantable(item)) {
            logs.add("Invalid item selected");
            return;
        }
        Enchantable enchantItem = (Enchantable)((Object)item.item);
        switch (mode) {
            case "clear": {
                enchantItem.clearEnchantment(item);
                logs.add("Cleared enchant on " + ItemRegistry.getDisplayName(item.item.getID()));
                break;
            }
            case "set": {
                if (enchantment == null) {
                    logs.add("Must specify enchantID on set mode");
                    return;
                }
                enchantItem.setEnchantment(item, enchantment.getID());
                logs.add("Gave enchantment " + enchantment.getDisplayName() + " to " + ItemRegistry.getDisplayName(item.item.getID()));
                break;
            }
            case "random": {
                enchantItem.addRandomEnchantment(item);
                logs.add(ItemRegistry.getDisplayName(item.item.getID()) + " got enchantment " + enchantItem.getEnchantName(item));
            }
        }
        invSlot.getInv(serverClient.playerMob.getInv()).markDirty(invSlot.slot);
    }
}

