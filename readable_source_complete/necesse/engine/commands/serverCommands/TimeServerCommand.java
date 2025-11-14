/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.serverCommands;

import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.IntParameterHandler;
import necesse.engine.commands.parameterHandlers.PresetStringParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class TimeServerCommand
extends ModularChatCommand {
    public TimeServerCommand() {
        super("time", "Sets/adds world time (can use (mid)day or (mid)night)", PermissionLevel.ADMIN, true, new CmdParameter("set/add", new PresetStringParameterHandler("set", "add", "day", "dawn", "morning", "noon", "midday", "dusk", "night", "midnight")), new CmdParameter("amount", new IntParameterHandler(-1), true, new CmdParameter[0]));
    }

    @Override
    public void runModular(Client client, Server server, ServerClient serverClient, Object[] args, String[] errors, CommandLog logs) {
        String mod = (String)args[0];
        int time = (Integer)args[1];
        long oldTime = server.world.worldEntity.getWorldTime();
        switch (mod) {
            case "set": {
                if (time < 0) {
                    logs.add("Amount must be a positive number.");
                    return;
                }
                long change = (long)(server.world.worldEntity.getDayTimeMax() - server.world.worldEntity.getDayTimeInt() + time) * 1000L;
                server.world.addWorldTime(change);
                break;
            }
            case "add": {
                if (time <= 0) {
                    logs.add("Amount must be above 0.");
                    return;
                }
                server.world.addWorldTime((long)time * 1000L);
                break;
            }
            case "dawn": {
                server.world.setDawn();
                break;
            }
            case "day": 
            case "morning": {
                server.world.setMorning();
                break;
            }
            case "noon": 
            case "midday": {
                server.world.setMidday();
                break;
            }
            case "dusk": {
                server.world.setDusk();
                break;
            }
            case "night": {
                server.world.setNight();
                break;
            }
            case "midnight": {
                server.world.setMidnight();
            }
        }
        if (server.world.worldEntity.getWorldTime() != oldTime) {
            server.world.simulateWorldTime(server.world.worldEntity.getWorldTime() - oldTime, true);
            logs.add("Time changed to " + server.world.worldEntity.getDayTimeInt() + ", day " + server.world.worldEntity.getDay());
        }
    }
}

