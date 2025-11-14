/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.serverCommands;

import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.FloatParameterHandler;
import necesse.engine.commands.parameterHandlers.IntParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.util.GameRandom;
import necesse.level.maps.Level;

public class MowServerCommand
extends ModularChatCommand {
    public MowServerCommand() {
        super("mow", "Mows ground of grass in range with percent chance", PermissionLevel.ADMIN, true, new CmdParameter("range", new IntParameterHandler()), new CmdParameter("chance", new FloatParameterHandler(Float.valueOf(100.0f)), true, new CmdParameter[0]));
    }

    @Override
    public void runModular(Client client, Server server, ServerClient serverClient, Object[] args, String[] errors, CommandLog logs) {
        if (serverClient == null) {
            logs.add("Cannot run mow from server console.");
            return;
        }
        int range = (Integer)args[0];
        float chance = ((Float)args[1]).floatValue();
        float actualChance = chance / 100.0f;
        Level level = server.world.getLevel(serverClient);
        int startX = level.limitTileXToBounds(serverClient.playerMob.getTileX() - range);
        int startY = level.limitTileXToBounds(serverClient.playerMob.getTileY() - range);
        int endX = level.limitTileXToBounds(serverClient.playerMob.getTileX() + range);
        int endY = level.limitTileXToBounds(serverClient.playerMob.getTileY() + range);
        int grassID = ObjectRegistry.getObjectID("grass");
        for (int x = startX; x <= endX; ++x) {
            for (int y = startY; y <= endY; ++y) {
                if (level.getObjectID(x, y) != grassID || !GameRandom.globalRandom.getChance(actualChance)) continue;
                level.sendObjectChangePacket(server, x, y, 0);
            }
        }
        logs.add("Mowed grass in a range of " + range + " around you.");
    }
}

