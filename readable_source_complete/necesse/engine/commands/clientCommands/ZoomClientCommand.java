/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.clientCommands;

import necesse.engine.GlobalData;
import necesse.engine.Settings;
import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.IntParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.window.WindowManager;

public class ZoomClientCommand
extends ModularChatCommand {
    public ZoomClientCommand() {
        super("zoom", "Sets zoom level", PermissionLevel.USER, false, new CmdParameter("percent", new IntParameterHandler(), false, new CmdParameter[0]));
    }

    @Override
    public void runModular(Client client, Server server, ServerClient serverClient, Object[] args, String[] errors, CommandLog logs) {
        int level = (Integer)args[0];
        if (!(GlobalData.isDevMode() || level >= 100 && level <= 200)) {
            logs.add("Zoom percent must be between 100 and 200");
            return;
        }
        Settings.sceneSize = (float)level / 100.0f;
        WindowManager.getWindow().updateSceneSize();
        Settings.saveClientSettings();
        logs.add("Changed zoom level to " + level + "%");
    }
}

