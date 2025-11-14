/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.serverCommands;

import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.LevelIdentifierParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.LevelIdentifier;
import necesse.entity.levelEvent.IncursionLevelEvent;
import necesse.entity.levelEvent.LevelEvent;
import necesse.level.maps.Level;

public class CompleteIncursionServerCommand
extends ModularChatCommand {
    public CompleteIncursionServerCommand() {
        super("completeincursion", "Force completes an incursion objective. Use multiple times to fully complete", PermissionLevel.OWNER, true, new CmdParameter("level", new LevelIdentifierParameterHandler(), true, new CmdParameter[0]));
    }

    @Override
    public void runModular(Client client, Server server, ServerClient serverClient, Object[] args, String[] errors, CommandLog logs) {
        LevelIdentifier target = (LevelIdentifier)args[0];
        if (target == null) {
            if (serverClient != null) {
                target = serverClient.getLevelIdentifier();
            } else {
                logs.add("Must specify <level>");
                return;
            }
        }
        if (server.world.levelExists(target)) {
            Level level = server.world.getLevel(target);
            for (LevelEvent event : level.entityManager.events) {
                if (!(event instanceof IncursionLevelEvent)) continue;
                ((IncursionLevelEvent)event).forceObjectiveComplete();
                logs.add("Successfully completed incursion in level: " + target + ", event uniqueID: " + event.getUniqueID());
            }
        } else {
            logs.add("Level does not exist: " + target);
        }
    }
}

