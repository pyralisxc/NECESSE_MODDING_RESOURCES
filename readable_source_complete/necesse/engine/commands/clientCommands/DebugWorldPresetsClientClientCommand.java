/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.clientCommands;

import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.BoolParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.world.worldPresets.WorldPresetsRegion;

public class DebugWorldPresetsClientClientCommand
extends ModularChatCommand {
    public DebugWorldPresetsClientClientCommand() {
        super("debugworldpresets", "Sets debug variables", PermissionLevel.OWNER, false, new CmdParameter("debugGeneration", new BoolParameterHandler(false)), new CmdParameter("debugPlacing", new BoolParameterHandler(false)));
    }

    @Override
    public void runModular(Client client, Server server, ServerClient serverClient, Object[] args, String[] errors, CommandLog logs) {
        WorldPresetsRegion.DEBUG_GENERATING_PRESETS = (Boolean)args[0];
        WorldPresetsRegion.DEBUG_PLACING_PRESETS = (Boolean)args[1];
        logs.add("Updated debug variables. Generation: " + WorldPresetsRegion.DEBUG_GENERATING_PRESETS + ", Placing: " + WorldPresetsRegion.DEBUG_PLACING_PRESETS);
    }
}

