/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.serverCommands;

import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.IntParameterHandler;
import necesse.engine.commands.parameterHandlers.ServerClientParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameMath;
import necesse.engine.world.worldData.SettlementsWorldData;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;

public class SetRaidDifficultyCommand
extends ModularChatCommand {
    public SetRaidDifficultyCommand() {
        super("setraiddifficulty", "Set the next raid difficulty of the settlement at players current location", PermissionLevel.ADMIN, true, new CmdParameter("player", new ServerClientParameterHandler(true, false), true, new CmdParameter[0]), new CmdParameter("percent", new IntParameterHandler(100)));
    }

    @Override
    public void runModular(Client client, Server server, ServerClient serverClient, Object[] args, String[] errors, CommandLog logs) {
        ServerClient target = (ServerClient)args[0];
        int difficultyPercent = (Integer)args[1];
        if (target == null) {
            logs.add("Must specify <player>");
            return;
        }
        Level level = target.getLevel();
        ServerSettlementData settlementData = SettlementsWorldData.getSettlementsData(level).getServerDataAtTile(level.getIdentifier(), target.playerMob.getTileX(), target.playerMob.getTileY());
        if (settlementData == null) {
            logs.add(target.getName() + " is not at a location with a settlement");
        } else {
            int limitedPercent = GameMath.limit(difficultyPercent, 50, 150);
            if (difficultyPercent != limitedPercent) {
                logs.add("Limited difficulty to " + limitedPercent + "%");
            }
            float mod = (float)limitedPercent / 100.0f;
            settlementData.setRaidDifficultyMod(mod);
            String settlementNameString = settlementData.networkData.getSettlementName().translate() + " (Owner: " + settlementData.networkData.getOwnerName() + ")";
            logs.add("Set raid difficulty modifier to " + (int)(settlementData.getNextRaidDifficultyMod() * 100.0f) + "% at " + settlementNameString);
        }
    }
}

