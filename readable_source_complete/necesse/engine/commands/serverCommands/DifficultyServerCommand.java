/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.serverCommands;

import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;
import necesse.engine.GameDifficulty;
import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.EnumParameterHandler;
import necesse.engine.commands.parameterHandlers.MultiParameterHandler;
import necesse.engine.commands.parameterHandlers.PresetStringParameterHandler;
import necesse.engine.localization.message.GameMessageBuilder;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class DifficultyServerCommand
extends ModularChatCommand {
    public DifficultyServerCommand() {
        super("difficulty", "Changes difficulty setting", PermissionLevel.ADMIN, false, new CmdParameter("list/difficulty", new MultiParameterHandler(new PresetStringParameterHandler("list"), new EnumParameterHandler((Enum[])GameDifficulty.values())), false, new CmdParameter[0]));
    }

    @Override
    public void runModular(Client client, Server server, ServerClient serverClient, Object[] args, String[] errors, CommandLog logs) {
        Object[] subArgs = (Object[])args[0];
        String list = (String)subArgs[0];
        GameDifficulty difficulty = (GameDifficulty)((Object)subArgs[1]);
        if (list != null) {
            logs.add("List of difficulties: " + Arrays.stream(GameDifficulty.values()).map(e -> e.name().toLowerCase(Locale.ENGLISH)).collect(Collectors.joining(", ")));
        } else {
            if (difficulty == null) {
                throw new NullPointerException("This should never happen");
            }
            if (server.world.settings.difficulty == difficulty) {
                GameMessageBuilder msg = new GameMessageBuilder();
                msg.append("Difficulty is already ");
                msg.append(difficulty.displayName);
                logs.add(msg);
            } else {
                server.world.settings.difficulty = difficulty;
                server.world.settings.saveSettings();
                server.world.settings.sendSettingsPacket();
                GameMessageBuilder msg = new GameMessageBuilder();
                msg.append("Changed difficulty to ");
                msg.append(difficulty.displayName);
                logs.add(msg);
            }
        }
    }
}

