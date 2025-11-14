/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.serverCommands;

import java.io.IOException;
import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.BoolParameterHandler;
import necesse.engine.commands.parameterHandlers.LevelIdentifierParameterHandler;
import necesse.engine.network.Packet;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketPlayerLevelChange;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.WorldGenerator;
import necesse.engine.world.worldData.SettlementsWorldData;
import necesse.level.maps.Level;

public class RegenServerCommand
extends ModularChatCommand {
    public RegenServerCommand() {
        super("regen", "Regenerates the entire level", PermissionLevel.OWNER, true, new CmdParameter("seeded", new BoolParameterHandler(true), true, new CmdParameter[0]), new CmdParameter("level", new LevelIdentifierParameterHandler(null), true, new CmdParameter[0]));
    }

    @Override
    public void runModular(Client client, Server server, ServerClient serverClient, Object[] args, String[] errors, CommandLog logs) {
        boolean seeded = (Boolean)args[0];
        LevelIdentifier levelIdentifier = (LevelIdentifier)args[1];
        if (levelIdentifier == null) {
            if (serverClient == null) {
                logs.add("Please specify level");
                return;
            }
            levelIdentifier = serverClient.getLevelIdentifier();
        }
        try {
            server.world.fileSystem.deleteAllLevelFiles(levelIdentifier);
            SettlementsWorldData.getSettlementsData(server).deleteSettlementsAt(levelIdentifier);
            GameBlackboard blackboard = new GameBlackboard();
            if (!seeded) {
                blackboard.set("seed", GameRandom.globalRandom.nextInt());
            }
            Level newLevel = WorldGenerator.generateNewLevel(levelIdentifier, server, blackboard);
            newLevel.makeServerLevel(server);
            newLevel.overwriteIdentifier(levelIdentifier);
            server.world.levelManager.overwriteLevel(newLevel);
            LevelIdentifier finalLevelIdentifier = levelIdentifier;
            server.streamClients().filter(c -> c.isSamePlace(finalLevelIdentifier)).forEach(c -> {
                c.reset();
                server.network.sendPacket((Packet)new PacketPlayerLevelChange(c.slot, finalLevelIdentifier, true), (ServerClient)c);
            });
            logs.add("Regenerated level " + levelIdentifier + (seeded ? "" : " (not seeded)") + ".");
        }
        catch (IOException e) {
            System.err.println("Could not delete level regions folder for " + levelIdentifier);
            logs.add("Error deleting regions folder for " + levelIdentifier + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}

