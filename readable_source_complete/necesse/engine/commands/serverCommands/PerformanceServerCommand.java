/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.serverCommands;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import necesse.engine.GameSystemInfo;
import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.BoolParameterHandler;
import necesse.engine.commands.parameterHandlers.IntParameterHandler;
import necesse.engine.gameLoop.tickManager.PerformanceTimerUtils;
import necesse.engine.gameLoop.tickManager.PerformanceTotal;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketPerformanceResult;
import necesse.engine.network.packet.PacketPerformanceStart;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;

public class PerformanceServerCommand
extends ModularChatCommand {
    public PerformanceServerCommand() {
        super("performance", "Records server performance over some seconds and creates a file with the results", PermissionLevel.USER, false, new CmdParameter("includeServer", new BoolParameterHandler(true), true, new CmdParameter[0]), new CmdParameter("seconds", new IntParameterHandler(10), true, new CmdParameter[0]));
    }

    @Override
    public void runModular(Client client, Server server, ServerClient serverClient, Object[] args, String[] errors, CommandLog logs) {
        boolean includeServer = (Boolean)args[0];
        int seconds = (Integer)args[1];
        if (seconds > 60) {
            logs.add("Cannot record performance for more than 60 seconds");
            return;
        }
        int uniqueID = GameRandom.globalRandom.nextInt();
        if (includeServer && serverClient != null && serverClient.getPermissionLevel().getLevel() < PermissionLevel.ADMIN.getLevel()) {
            logs.add("Only admins can record server performance.");
            includeServer = false;
        }
        if (serverClient == null) {
            includeServer = true;
        }
        if (includeServer) {
            String dateFormat = new SimpleDateFormat("yyyy-MM-dd HH'h'mm'm'ss's'").format(new Date());
            server.tickManager().runPerformanceDump(seconds, history -> {
                ByteArrayOutputStream text = new ByteArrayOutputStream();
                PrintStream printStream = new PrintStream(text);
                printStream.println("Server performance recording ran for " + seconds + " seconds starting at " + dateFormat);
                PerformanceTotal total = PerformanceTimerUtils.combineTimers(history);
                if (total != null) {
                    printStream.println("A total of " + total.getTotalFrames() + " frames were recorded.");
                    printStream.println();
                    total.print(printStream);
                    if (serverClient == null || server.getLocalServerClient() != serverClient) {
                        printStream.println();
                        printStream.println();
                        GameSystemInfo.printSystemInfo(printStream);
                    }
                }
                if (serverClient != null) {
                    serverClient.sendPacket(new PacketPerformanceResult(uniqueID, text.toString()));
                } else {
                    File file = new File("server performance " + dateFormat + ".txt");
                    try {
                        GameUtils.saveByteFile(text.toByteArray(), file);
                        logs.add("Printed performance to file:");
                        logs.add(file.getAbsolutePath());
                    }
                    catch (IOException e) {
                        logs.add("Error printing performance file: " + e);
                    }
                }
            });
            if (serverClient == null) {
                logs.add("Recording server performance for the next " + seconds + " seconds...");
            }
        }
        if (serverClient != null) {
            serverClient.sendPacket(new PacketPerformanceStart(uniqueID, seconds, includeServer));
        }
    }
}

