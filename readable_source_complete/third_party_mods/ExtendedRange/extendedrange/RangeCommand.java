/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.commands.CmdParameter
 *  necesse.engine.commands.CommandLog
 *  necesse.engine.commands.ModularChatCommand
 *  necesse.engine.commands.PermissionLevel
 *  necesse.engine.commands.parameterHandlers.IntParameterHandler
 *  necesse.engine.commands.parameterHandlers.ParameterHandler
 *  necesse.engine.network.Packet
 *  necesse.engine.network.client.Client
 *  necesse.engine.network.server.Server
 *  necesse.engine.network.server.ServerClient
 *  necesse.inventory.container.object.CraftingStationContainer
 */
package extendedrange;

import extendedrange.Settings;
import extendedrange.UpdateRangePacket;
import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.IntParameterHandler;
import necesse.engine.commands.parameterHandlers.ParameterHandler;
import necesse.engine.network.Packet;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.container.object.CraftingStationContainer;

public class RangeCommand
extends ModularChatCommand {
    public RangeCommand() {
        super("craftingStationsRange", "Set the range of the Crafting stations 'use nearby inventory' property to the given value, returns the current value if empty. The vanilla default value is 15, the default of the mod is 30", PermissionLevel.ADMIN, false, new CmdParameter[]{new CmdParameter("new range", (ParameterHandler)new IntParameterHandler(Integer.valueOf(-1)), true, new CmdParameter[0])});
    }

    public void runModular(Client client, Server server, ServerClient serverClient, Object[] args, String[] errors, CommandLog commandLog) {
        try {
            int amount = (Integer)args[0];
            int oldValue = CraftingStationContainer.nearbyCraftTileRange;
            if (amount < 0) {
                if (amount == -1) {
                    commandLog.add("The current Crafting stations range is: " + oldValue);
                } else {
                    commandLog.add("Using a negative value is a good way to crash the game\n-The one who is preventing the crash");
                }
            } else {
                Settings.CraftingStationsRange = amount;
                server.network.sendToAllClients((Packet)new UpdateRangePacket(amount));
                CraftingStationContainer.nearbyCraftTileRange = amount;
                commandLog.add("Set the Crafting stations range from: " + oldValue + " to: " + amount);
            }
        }
        catch (Exception e) {
            System.err.println("[Extended range mod] An error has occurred while running the craftingStationsRange command.\nError:\n" + e);
        }
    }
}

