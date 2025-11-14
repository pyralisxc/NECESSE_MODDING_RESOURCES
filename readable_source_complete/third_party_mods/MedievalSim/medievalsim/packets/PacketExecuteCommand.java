/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.commands.ParsedCommand
 *  necesse.engine.commands.PermissionLevel
 *  necesse.engine.network.NetworkPacket
 *  necesse.engine.network.Packet
 *  necesse.engine.network.PacketReader
 *  necesse.engine.network.PacketWriter
 *  necesse.engine.network.server.Server
 *  necesse.engine.network.server.ServerClient
 *  necesse.engine.util.GameRandom
 */
package medievalsim.packets;

import medievalsim.commandcenter.CommandPermissions;
import medievalsim.packets.PacketCommandResult;
import necesse.engine.commands.ParsedCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameRandom;

public class PacketExecuteCommand
extends Packet {
    public final String commandString;
    public final int requestId;

    public PacketExecuteCommand(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader((Packet)this);
        this.commandString = reader.getNextString();
        this.requestId = reader.getNextInt();
    }

    public PacketExecuteCommand(String commandString) {
        this.commandString = commandString;
        this.requestId = GameRandom.globalRandom.nextInt();
        PacketWriter writer = new PacketWriter((Packet)this);
        writer.putNextString(commandString);
        writer.putNextInt(this.requestId);
    }

    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        if (client == null) {
            return;
        }
        if (!CommandPermissions.canAccessCommandCenter(client)) {
            System.out.println("[PacketExecuteCommand] Permission denied for client: " + client.getName());
            server.network.sendPacket((Packet)new PacketCommandResult(this.requestId, false, CommandPermissions.getPermissionDeniedMessage(PermissionLevel.ADMIN), this.commandString), client);
            return;
        }
        if (this.commandString == null || this.commandString.trim().isEmpty()) {
            server.network.sendPacket((Packet)new PacketCommandResult(this.requestId, false, "Empty command string", this.commandString), client);
            return;
        }
        try {
            String trimmedCommand = this.commandString.trim();
            if (trimmedCommand.startsWith("/")) {
                trimmedCommand = trimmedCommand.substring(1);
            }
            System.out.println("[PacketExecuteCommand] Executing command: " + trimmedCommand + " (from " + client.getName() + ")");
            ParsedCommand parsedCommand = new ParsedCommand(trimmedCommand);
            boolean success = server.commandsManager.runServerCommand(parsedCommand, client);
            String resultMessage = success ? "Command executed" : "Command failed or invalid";
            server.network.sendPacket((Packet)new PacketCommandResult(this.requestId, success, resultMessage, this.commandString), client);
        }
        catch (Exception e) {
            System.err.println("[PacketExecuteCommand] Error executing command: " + e.getMessage());
            e.printStackTrace();
            server.network.sendPacket((Packet)new PacketCommandResult(this.requestId, false, "Error: " + e.getMessage(), this.commandString), client);
        }
    }
}

