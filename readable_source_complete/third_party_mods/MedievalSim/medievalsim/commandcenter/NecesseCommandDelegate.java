/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.commands.ParsedCommand
 *  necesse.engine.network.server.Server
 *  necesse.engine.network.server.ServerClient
 */
package medievalsim.commandcenter;

import medievalsim.commandcenter.CommandResult;
import necesse.engine.commands.ParsedCommand;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class NecesseCommandDelegate {
    public static CommandResult executeNecesseCommand(Server server, String commandString, ServerClient executor) {
        try {
            boolean success = server.commandsManager.runServerCommand(new ParsedCommand(commandString), executor);
            if (success) {
                return CommandResult.success("Command executed: " + commandString);
            }
            return CommandResult.error("Command failed: " + commandString);
        }
        catch (Exception e) {
            return CommandResult.error("Error executing command: " + e.getMessage());
        }
    }
}

