/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.commands.CmdParameter
 *  necesse.engine.commands.CommandLog
 *  necesse.engine.commands.ModularChatCommand
 *  necesse.engine.commands.PermissionLevel
 *  necesse.engine.commands.parameterHandlers.IntParameterHandler
 *  necesse.engine.commands.parameterHandlers.ParameterHandler
 *  necesse.engine.localization.Localization
 *  necesse.engine.network.Packet
 *  necesse.engine.network.client.Client
 *  necesse.engine.network.packet.PacketChatMessage
 *  necesse.engine.network.server.Server
 *  necesse.engine.network.server.ServerClient
 */
package increasedStackSize.commands;

import increasedStackSize.IncreasedStackSize;
import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.IntParameterHandler;
import necesse.engine.commands.parameterHandlers.ParameterHandler;
import necesse.engine.localization.Localization;
import necesse.engine.network.Packet;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketChatMessage;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class ChangeStackSizeCommand
extends ModularChatCommand {
    public ChangeStackSizeCommand() {
        super("setstacksizemultiplier", "Set multiplier for stack size", PermissionLevel.OWNER, false, new CmdParameter[]{new CmdParameter("stacksize", (ParameterHandler)new IntParameterHandler())});
    }

    public void runModular(Client client, Server server, ServerClient serverClient, Object[] args, String[] errors, CommandLog commandLog) {
        commandLog.add(Localization.translate((String)"increasedstacksize", (String)"newcommand"));
        int newStackSize = (Integer)args[0];
        if (newStackSize > 0) {
            String name = serverClient == null ? "Server" : serverClient.playerMob.getDisplayName();
            String message = Localization.translate((String)"increasedstacksize", (String)"changedwarning", (Object[])new Object[]{"name", name, "stacksize", newStackSize});
            server.network.sendToAllClients((Packet)new PacketChatMessage(message));
            IncreasedStackSize.setStackSizeMultiplier(newStackSize, false);
        } else {
            commandLog.add(Localization.translate((String)"increasedstacksize", (String)"errorsmaller"));
        }
    }
}

