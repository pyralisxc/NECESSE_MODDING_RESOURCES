/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.serverCommands;

import java.util.List;
import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.BoolParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.team.PlayerTeam;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.worldData.SettlementsWorldData;
import necesse.gfx.fairType.TypeParsers;
import necesse.level.maps.levelData.settlementData.CachedSettlementData;

public class SettlementsServerCommand
extends ModularChatCommand {
    public SettlementsServerCommand() {
        super("settlements", "Lists found settlements", PermissionLevel.MODERATOR, false, new CmdParameter("onlyLoaded", new BoolParameterHandler(true), true, new CmdParameter[0]));
    }

    @Override
    public void runModular(Client client, Server server, ServerClient serverClient, Object[] args, String[] errors, CommandLog logs) {
        boolean onlyActive = (Boolean)args[0];
        SettlementsWorldData worldData = SettlementsWorldData.getSettlementsData(server);
        List<CachedSettlementData> settlements = worldData.collectCachedSettlements(data -> true);
        logs.add("Total settlements: " + worldData.getTotalSettlements());
        logs.add("Loaded settlements: " + worldData.getTotalLoadedSettlements());
        for (CachedSettlementData settlement : settlements) {
            if (!settlement.isLoaded() && onlyActive) continue;
            PlayerTeam team = settlement.getTeamID() == -1 ? null : server.world.getTeams().getTeam(settlement.getTeamID());
            String teamName = team == null ? "PRIVATE" : team.getName();
            String ownerName = settlement.getOwnerAuth() == -1L ? null : server.usedNames.get(settlement.getOwnerAuth());
            LevelIdentifier levelIdentifier = settlement.levelIdentifier;
            boolean loaded = server.world.levelManager.isLoaded(levelIdentifier);
            String positionString = serverClient == null ? levelIdentifier.toString() + " tile " + settlement.getTileY() + "x" + settlement.getTileY() : TypeParsers.getTeleportParseString(levelIdentifier, settlement.getTileX(), settlement.getTileY());
            logs.add("Settlement with uniqueID " + settlement.uniqueID + " at " + positionString + ": Settlement loaded: " + settlement.isLoaded() + ", Level loaded: " + loaded + ", Name: " + (settlement.getName() == null ? "NULL" : settlement.getName().translate()) + ", Team: " + teamName + ", Owner: " + ownerName + " (" + settlement.getOwnerAuth() + ")");
        }
    }
}

