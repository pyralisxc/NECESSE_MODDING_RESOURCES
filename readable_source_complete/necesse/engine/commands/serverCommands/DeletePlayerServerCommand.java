/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.serverCommands;

import java.io.IOException;
import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.StoredPlayerParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class DeletePlayerServerCommand
extends ModularChatCommand {
    public DeletePlayerServerCommand() {
        super("deleteplayer", "Deletes a players files in the saved players folder", PermissionLevel.ADMIN, false, new CmdParameter("authentication/fullname", new StoredPlayerParameterHandler()));
    }

    @Override
    public boolean autocompleteOnServer() {
        return true;
    }

    @Override
    public void runModular(Client client, Server server, ServerClient serverClient, Object[] args, String[] errors, CommandLog logs) {
        StoredPlayerParameterHandler.StoredPlayer player = (StoredPlayerParameterHandler.StoredPlayer)args[0];
        boolean deleteFile = false;
        boolean deleteMap = false;
        try {
            deleteFile = player.file.delete();
            deleteMap = player.mapFile.delete();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        if (deleteFile && deleteMap) {
            server.usedNames.remove(player.authentication);
            logs.add("Deleted player " + player.authentication + " - \"" + player.name + "\".");
        } else if (deleteFile) {
            logs.add("Error in deleting player map " + player.authentication + " - \"" + player.name + "\".");
        } else {
            logs.add("Error in deleting player " + player.authentication + " - \"" + player.name + "\".");
        }
    }
}

