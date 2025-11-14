/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.serverCommands;

import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.ServerClientParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.levelEvent.settlementRaidEvent.SettlementRaidLevelEvent;
import necesse.level.maps.Level;

public class EndRaidCommand
extends ModularChatCommand {
    public EndRaidCommand() {
        super("endraid", "Ends a raid at the players current location", PermissionLevel.ADMIN, true, new CmdParameter("player", new ServerClientParameterHandler(true, false), true, new CmdParameter[0]));
    }

    @Override
    public void runModular(Client client, Server server, ServerClient serverClient, Object[] args, String[] errors, CommandLog logs) {
        ServerClient target = (ServerClient)args[0];
        if (target == null) {
            logs.add("Must specify <player>");
            return;
        }
        Level level = target.getLevel();
        boolean found = false;
        int playerRegionX = level.regionManager.getRegionCoordByTile(target.playerMob.getTileX());
        int playerRegionY = level.regionManager.getRegionCoordByTile(target.playerMob.getTileY());
        for (LevelEvent event : level.entityManager.events.regionList.getInRegion(playerRegionX, playerRegionY)) {
            if (!(event instanceof SettlementRaidLevelEvent) || event.isOver()) continue;
            event.over();
            found = true;
        }
        if (found) {
            logs.add("Ended raid events at " + target.getName() + "'s location");
        } else {
            logs.add("Could not find any raid events at " + target.getName() + "'s location");
        }
    }
}

