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
 *  necesse.engine.commands.parameterHandlers.PresetStringParameterHandler
 *  necesse.engine.localization.Localization
 *  necesse.engine.network.Packet
 *  necesse.engine.network.client.Client
 *  necesse.engine.network.packet.PacketChatMessage
 *  necesse.engine.network.server.Server
 *  necesse.engine.network.server.ServerClient
 */
package increasedStackSize.commands;

import increasedStackSize.IncreasedStackSize;
import java.util.Objects;
import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.IntParameterHandler;
import necesse.engine.commands.parameterHandlers.ParameterHandler;
import necesse.engine.commands.parameterHandlers.PresetStringParameterHandler;
import necesse.engine.localization.Localization;
import necesse.engine.network.Packet;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketChatMessage;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class StackSizeCommand
extends ModularChatCommand {
    public StackSizeCommand() {
        super("stacksize", "Commands for configuring stack size multiplier", PermissionLevel.OWNER, false, new CmdParameter[]{new CmdParameter("command", (ParameterHandler)new PresetStringParameterHandler(new String[]{"get", "set"}), false, new CmdParameter[0]), new CmdParameter("size", (ParameterHandler)new IntParameterHandler(), true, new CmdParameter[0])});
    }

    public void runModular(Client client, Server server, ServerClient serverClient, Object[] args, String[] errors, CommandLog commandLog) {
        String mode = (String)args[0];
        if (Objects.equals(mode, "get")) {
            String message = Localization.translate((String)"increasedstacksize", (String)"getstacksize", (String)"stacksize", (Object)IncreasedStackSize.stackSizeMultiplier);
            commandLog.add(message);
            return;
        }
        int newStackSize = (Integer)args[1];
        if (newStackSize <= 0) {
            commandLog.add(Localization.translate((String)"increasedstacksize", (String)"errorsmaller"));
            return;
        }
        String name = serverClient == null ? "Server" : serverClient.playerMob.getDisplayName();
        String message = Localization.translate((String)"increasedstacksize", (String)"changedwarning", (Object[])new Object[]{"name", name, "stacksize", newStackSize});
        server.network.sendToAllClients((Packet)new PacketChatMessage(message));
        IncreasedStackSize.setStackSizeMultiplier(newStackSize, false);
    }
}

