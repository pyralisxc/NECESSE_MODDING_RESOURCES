/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.commands.CmdParameter
 *  necesse.engine.commands.CommandLog
 *  necesse.engine.commands.ModularChatCommand
 *  necesse.engine.commands.PermissionLevel
 *  necesse.engine.network.client.Client
 *  necesse.engine.network.server.Server
 *  necesse.engine.network.server.ServerClient
 *  necesse.inventory.InventoryItem
 */
package tomeofpower.commands;

import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.InventoryItem;

public class TomeOfPowerCommand
extends ModularChatCommand {
    public TomeOfPowerCommand() {
        super("tomeofpower", "Give yourself a Tome of Power for testing", PermissionLevel.ADMIN, false, new CmdParameter[0]);
    }

    public void runModular(Client client, Server server, ServerClient serverClient, Object[] args, String[] errors, CommandLog commandLog) {
        if (serverClient != null) {
            InventoryItem tome = new InventoryItem("tomeofpower", 1);
            serverClient.playerMob.getInv().addItem(tome, true, "admin_command", null);
            commandLog.add("Given Tome of Power!");
            InventoryItem scroll1 = new InventoryItem("enchantingscroll", 19);
            InventoryItem scroll2 = new InventoryItem("enchantingscroll", 18);
            InventoryItem scroll3 = new InventoryItem("enchantingscroll", 20);
            serverClient.playerMob.getInv().addItem(scroll1, true, "admin_command", null);
            serverClient.playerMob.getInv().addItem(scroll2, true, "admin_command", null);
            serverClient.playerMob.getInv().addItem(scroll3, true, "admin_command", null);
            commandLog.add("Also given 3 enchantment scrolls for testing!");
        } else {
            commandLog.add("This command can only be used by players");
        }
    }
}

