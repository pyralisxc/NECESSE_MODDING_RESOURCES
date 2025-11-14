/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.serverCommands;

import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.BoolParameterHandler;
import necesse.engine.commands.parameterHandlers.EnumParameterHandler;
import necesse.engine.commands.parameterHandlers.ServerClientParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameRandom;
import necesse.engine.world.worldData.SettlementsWorldData;
import necesse.entity.levelEvent.settlementRaidEvent.SettlementRaidLevelEvent;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;

public class StartRaidCommand
extends ModularChatCommand {
    public StartRaidCommand() {
        super("startraid", "Starts a raid at the players current location", PermissionLevel.ADMIN, true, new CmdParameter("player", new ServerClientParameterHandler(true, false), true, new CmdParameter[0]), new CmdParameter("direction", new EnumParameterHandler((Enum[])SettlementRaidLevelEvent.RaidDir.values()), true, new CmdParameter[0]), new CmdParameter("dontAttack", new BoolParameterHandler(false), true, new CmdParameter[0]));
    }

    @Override
    public void runModular(Client client, Server server, ServerClient serverClient, Object[] args, String[] errors, CommandLog logs) {
        Level level;
        ServerSettlementData settlementData;
        ServerClient target = (ServerClient)args[0];
        SettlementRaidLevelEvent.RaidDir direction = (SettlementRaidLevelEvent.RaidDir)((Object)args[1]);
        boolean wait = (Boolean)args[2];
        if (target == null) {
            logs.add("Must specify <player>");
            return;
        }
        if (direction == null) {
            direction = GameRandom.globalRandom.getOneOf(SettlementRaidLevelEvent.RaidDir.values());
        }
        if ((settlementData = SettlementsWorldData.getSettlementsData(level = target.getLevel()).getServerDataAtTile(level.getIdentifier(), target.playerMob.getTileX(), target.playerMob.getTileY())) == null) {
            logs.add(target.getName() + " is not at a location with a settlement");
            return;
        }
        String settlementNameString = settlementData.networkData.getSettlementName().translate() + " (Owner: " + settlementData.networkData.getOwnerName() + ")";
        if (settlementData.spawnRaid(direction, wait)) {
            logs.add("Spawned a raid at " + settlementNameString + " from " + direction.displayName.translate());
        } else {
            logs.add("Could not spawn a raid at " + settlementNameString);
            logs.add("This is usually because a criteria is not met, like a minimum of 3 or more settlers");
        }
    }
}

