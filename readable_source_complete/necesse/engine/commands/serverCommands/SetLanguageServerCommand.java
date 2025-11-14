/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.serverCommands;

import necesse.engine.Settings;
import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.LanguageParameterHandler;
import necesse.engine.localization.Language;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class SetLanguageServerCommand
extends ModularChatCommand {
    public SetLanguageServerCommand() {
        super("language", "Sets server language settings", PermissionLevel.SERVER, false, new CmdParameter("language", new LanguageParameterHandler(), false, new CmdParameter[0]));
    }

    @Override
    public void runModular(Client client, Server server, ServerClient serverClient, Object[] args, String[] errors, CommandLog logs) {
        Language lang = (Language)args[0];
        if (Settings.language.equals(lang.stringID)) {
            logs.add("Server language already set to " + lang.stringID);
        } else {
            Settings.language = lang.stringID;
            Settings.saveServerSettings();
            lang.setCurrent();
            logs.add("Changed server language to " + lang.stringID);
        }
    }
}

