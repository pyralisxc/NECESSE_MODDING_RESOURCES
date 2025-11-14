/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.serverCommands;

import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.IntParameterHandler;
import necesse.engine.commands.parameterHandlers.ItemParameterHandler;
import necesse.engine.commands.parameterHandlers.ServerClientParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;

public class GiveServerCommand
extends ModularChatCommand {
    public GiveServerCommand() {
        super("give", "Gives item to player", PermissionLevel.ADMIN, true, new CmdParameter("player", new ServerClientParameterHandler(true, false), true, new CmdParameter[0]), new CmdParameter("item", new ItemParameterHandler()), new CmdParameter("amount", new IntParameterHandler(1), true, new CmdParameter[0]));
    }

    @Override
    public void runModular(Client client, Server server, ServerClient serverClient, Object[] args, String[] errors, CommandLog logs) {
        ServerClient player = (ServerClient)args[0];
        Item item = (Item)args[1];
        int amount = (Integer)args[2];
        if (player == null) {
            logs.add("Must specify <player>");
            return;
        }
        InventoryItem invItem = item.getDefaultItem(player.playerMob, amount);
        player.playerMob.getInv().addItem(invItem, true, "give", null);
        logs.add("Gave item " + invItem.getItemDisplayName() + " x" + amount + " to " + player.getName());
    }
}

