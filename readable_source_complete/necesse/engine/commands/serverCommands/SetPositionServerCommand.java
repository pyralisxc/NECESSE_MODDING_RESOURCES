/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.serverCommands;

import java.awt.Point;
import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.BoolParameterHandler;
import necesse.engine.commands.parameterHandlers.RelativeIntParameterHandler;
import necesse.engine.commands.parameterHandlers.ServerClientParameterHandler;
import necesse.engine.commands.parameterHandlers.StringParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.InvalidLevelIdentifierException;
import necesse.engine.util.LevelIdentifier;

public class SetPositionServerCommand
extends ModularChatCommand {
    public SetPositionServerCommand() {
        super("setposition", "Changes the position of the player", PermissionLevel.ADMIN, true, new CmdParameter("player", new ServerClientParameterHandler(true, false), true, new CmdParameter[0]), new CmdParameter("level", new StringParameterHandler(), true, new CmdParameter[0]), new CmdParameter("tileX", new RelativeIntParameterHandler()), new CmdParameter("tileY", new RelativeIntParameterHandler()), new CmdParameter("ignoreUnknownLevels", new BoolParameterHandler(false), true, new CmdParameter[0]));
    }

    @Override
    public void runModular(Client client, Server server, ServerClient serverClient, Object[] args, String[] errors, CommandLog logs) {
        LevelIdentifier levelIdentifier;
        ServerClient target = (ServerClient)args[0];
        if (target == null) {
            logs.add("Must specify <player>");
            return;
        }
        String levelIdentifierString = (String)args[1];
        if (levelIdentifierString == null) {
            levelIdentifier = target.getLevelIdentifier();
        } else {
            try {
                levelIdentifier = new LevelIdentifier(levelIdentifierString);
            }
            catch (InvalidLevelIdentifierException e) {
                logs.add("Invalid level: " + levelIdentifierString);
                return;
            }
        }
        int tileX = RelativeIntParameterHandler.handleRelativeInt(args[2], target.playerMob.getTileX());
        int tileY = RelativeIntParameterHandler.handleRelativeInt(args[3], target.playerMob.getTileY());
        boolean ignoreUnknownLevels = (Boolean)args[4];
        if (server.world.levelExists(levelIdentifier) || ignoreUnknownLevels) {
            target.changeLevel(levelIdentifier, level -> new Point(tileX * 32 + 16, tileY * 32 + 16), true);
            logs.add("Set " + target.getName() + " position to  " + levelIdentifier + ", tile " + tileX + "x" + tileY);
        } else {
            logs.add("Level " + levelIdentifier + " does not exist");
        }
    }
}

