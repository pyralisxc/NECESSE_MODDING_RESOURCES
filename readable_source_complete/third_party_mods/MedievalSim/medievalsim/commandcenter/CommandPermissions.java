/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.commands.PermissionLevel
 *  necesse.engine.network.server.ServerClient
 */
package medievalsim.commandcenter;

import medievalsim.commandcenter.AdminCommand;
import medievalsim.commandcenter.wrapper.NecesseCommandMetadata;
import medievalsim.commandcenter.wrapper.NecesseCommandRegistry;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.network.server.ServerClient;

public class CommandPermissions {
    public static boolean canExecuteCommand(ServerClient client, String commandId) {
        if (client == null) {
            return false;
        }
        NecesseCommandMetadata cmd = NecesseCommandRegistry.getCommand(commandId);
        if (cmd == null) {
            return false;
        }
        return CommandPermissions.hasPermissionLevel(client, cmd.getPermission());
    }

    public static boolean canExecuteAdminCommand(ServerClient client, AdminCommand command) {
        if (client == null || command == null) {
            return false;
        }
        return CommandPermissions.hasPermissionLevel(client, command.getRequiredPermission());
    }

    public static boolean hasPermissionLevel(ServerClient client, PermissionLevel requiredLevel) {
        if (client == null || requiredLevel == null) {
            return false;
        }
        PermissionLevel clientLevel = client.getPermissionLevel();
        if (clientLevel == null) {
            return false;
        }
        return clientLevel.getLevel() >= requiredLevel.getLevel();
    }

    public static boolean canAccessCommandCenter(ServerClient client) {
        return CommandPermissions.hasPermissionLevel(client, PermissionLevel.ADMIN);
    }

    public static String getPermissionDeniedMessage(PermissionLevel required) {
        return String.format("Permission denied. Requires %s access or higher.", required.name());
    }
}

