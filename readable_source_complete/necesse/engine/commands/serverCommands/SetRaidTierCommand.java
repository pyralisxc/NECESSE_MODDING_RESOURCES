/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.serverCommands;

import java.util.stream.Stream;
import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.PresetStringParameterHandler;
import necesse.engine.commands.parameterHandlers.ServerClientParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameUtils;
import necesse.engine.world.worldData.SettlementsWorldData;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;
import necesse.level.maps.levelData.settlementData.settlementQuestTiers.SettlementQuestTier;

public class SetRaidTierCommand
extends ModularChatCommand {
    public SetRaidTierCommand() {
        super("setraidtier", "Set the next raid tier of the settlement at players current location", PermissionLevel.ADMIN, true, new CmdParameter("player", new ServerClientParameterHandler(true, false), true, new CmdParameter[0]), new CmdParameter("tier", new PresetStringParameterHandler((String[])GameUtils.concat(SettlementQuestTier.questTiers.stream().map(qt -> qt.stringID), Stream.of("all")).toArray(String[]::new))));
    }

    @Override
    public void runModular(Client client, Server server, ServerClient serverClient, Object[] args, String[] errors, CommandLog logs) {
        Level level;
        ServerSettlementData settlementData;
        ServerClient target = (ServerClient)args[0];
        String questTierStringID = (String)args[1];
        if (target == null) {
            logs.add("Must specify <player>");
            return;
        }
        SettlementQuestTier foundTier = null;
        for (SettlementQuestTier questTier : SettlementQuestTier.questTiers) {
            if (!questTier.stringID.equalsIgnoreCase(questTierStringID)) continue;
            foundTier = questTier;
            break;
        }
        if ((settlementData = SettlementsWorldData.getSettlementsData(level = target.getLevel()).getServerDataAtTile(level.getIdentifier(), target.playerMob.getTileX(), target.playerMob.getTileY())) == null) {
            logs.add(target.getName() + " is not at a location with a settlement");
        } else {
            settlementData.setCurrentQuestTierDebug(target, foundTier);
            settlementData.resetQuestsDebug();
            SettlementQuestTier currentQuestTier = settlementData.getCurrentQuestTier();
            String settlementNameString = settlementData.networkData.getSettlementName().translate() + " (Owner: " + settlementData.networkData.getOwnerName() + ")";
            if (currentQuestTier != null) {
                logs.add("Set quest tier to " + currentQuestTier.stringID + " at " + settlementNameString);
            } else {
                logs.add("Completed all quests at " + settlementNameString);
            }
        }
    }
}

