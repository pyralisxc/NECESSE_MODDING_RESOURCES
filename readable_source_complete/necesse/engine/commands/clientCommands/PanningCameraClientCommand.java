/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.clientCommands;

import necesse.engine.GlobalData;
import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.IntParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.state.MainGame;
import necesse.gfx.camera.MainGameCamera;
import necesse.gfx.camera.MainGamePanningCamera;

public class PanningCameraClientCommand
extends ModularChatCommand {
    public PanningCameraClientCommand() {
        super("camerapan", "Start panning camera mode", PermissionLevel.ADMIN, false, new CmdParameter("xDir", new IntParameterHandler(), false, new CmdParameter[0]), new CmdParameter("yDir", new IntParameterHandler(), false, new CmdParameter[0]), new CmdParameter("speed", new IntParameterHandler(100), true, new CmdParameter[0]));
    }

    @Override
    public void runModular(Client client, Server server, ServerClient serverClient, Object[] args, String[] errors, CommandLog logs) {
        MainGame mainGame;
        int xDir = (Integer)args[0];
        int yDir = (Integer)args[1];
        int speed = (Integer)args[2];
        MainGame mainGame2 = mainGame = GlobalData.getCurrentState() instanceof MainGame ? (MainGame)GlobalData.getCurrentState() : null;
        if (mainGame != null) {
            MainGameCamera oldCamera = mainGame.getCamera();
            MainGamePanningCamera newCamera = new MainGamePanningCamera(oldCamera.getX(), oldCamera.getY());
            newCamera.setDirection(xDir, yDir);
            newCamera.setSpeed(speed);
            mainGame.setCamera(newCamera);
        }
    }
}

