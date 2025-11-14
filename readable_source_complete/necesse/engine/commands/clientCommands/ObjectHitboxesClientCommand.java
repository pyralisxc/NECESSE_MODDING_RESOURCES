/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.clientCommands;

import necesse.engine.Settings;
import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.PresetStringParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class ObjectHitboxesClientCommand
extends ModularChatCommand {
    public ObjectHitboxesClientCommand() {
        super("objecthitboxes", "Changes object hitbox mode", PermissionLevel.USER, false, new CmdParameter("mode", new PresetStringParameterHandler("tile", "custom"), false, new CmdParameter[0]));
    }

    @Override
    public void runModular(Client client, Server server, ServerClient serverClient, Object[] args, String[] errors, CommandLog logs) {
        String mode = (String)args[0];
        if (mode.equals("tile")) {
            if (Settings.useTileObjectHitboxes) {
                logs.add("Object hitbox mode already set to \"" + mode + "\"");
            } else {
                Settings.useTileObjectHitboxes = true;
                logs.add("Object hitbox mode changed to \"" + mode + "\"");
                Settings.saveClientSettings();
            }
        } else if (mode.equals("custom")) {
            if (!Settings.useTileObjectHitboxes) {
                logs.add("Object hitbox mode already set to \"" + mode + "\"");
            } else {
                Settings.useTileObjectHitboxes = false;
                logs.add("Object hitbox mode changed to \"" + mode + "\"");
                Settings.saveClientSettings();
            }
        }
    }
}

