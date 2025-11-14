/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.commands.ParsedCommand
 *  necesse.engine.network.NetworkPacket
 *  necesse.engine.network.Packet
 *  necesse.engine.network.PacketReader
 *  necesse.engine.network.PacketWriter
 *  necesse.engine.network.client.Client
 *  necesse.engine.network.server.Server
 *  necesse.engine.network.server.ServerClient
 */
package medievalsim.commandcenter.packets;

import java.util.HashMap;
import java.util.Map;
import medievalsim.commandcenter.AdminCommand;
import medievalsim.commandcenter.CommandRegistry;
import medievalsim.commandcenter.CommandResult;
import medievalsim.commandcenter.history.CommandHistory;
import medievalsim.util.ModLogger;
import necesse.engine.commands.ParsedCommand;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class PacketExecuteCommand
extends Packet {
    private String commandId;
    private Map<String, String> parameterStrings;
    private boolean success;
    private String resultMessage;

    public PacketExecuteCommand(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader((Packet)this);
        this.commandId = reader.getNextString();
        int paramCount = reader.getNextByteUnsigned();
        this.parameterStrings = new HashMap<String, String>();
        for (int i = 0; i < paramCount; ++i) {
            String paramName = reader.getNextString();
            String paramValue = reader.getNextString();
            this.parameterStrings.put(paramName, paramValue);
        }
        this.success = reader.getNextBoolean();
        this.resultMessage = reader.getNextString();
    }

    public PacketExecuteCommand(String commandString) {
        this.commandId = "string_command";
        this.parameterStrings = new HashMap<String, String>();
        this.parameterStrings.put("command", commandString);
        PacketWriter writer = new PacketWriter((Packet)this);
        writer.putNextString(this.commandId);
        writer.putNextByteUnsigned(1);
        writer.putNextString("command");
        writer.putNextString(commandString);
    }

    public PacketExecuteCommand(String commandId, Map<String, String> parameterStrings) {
        this.commandId = commandId;
        this.parameterStrings = parameterStrings;
        PacketWriter writer = new PacketWriter((Packet)this);
        writer.putNextString(commandId);
        writer.putNextByteUnsigned(parameterStrings.size());
        for (Map.Entry<String, String> entry : parameterStrings.entrySet()) {
            writer.putNextString(entry.getKey());
            writer.putNextString(entry.getValue());
        }
    }

    public PacketExecuteCommand(String commandId, boolean success, String resultMessage) {
        this.commandId = commandId;
        this.parameterStrings = new HashMap<String, String>();
        this.success = success;
        this.resultMessage = resultMessage;
        PacketWriter writer = new PacketWriter((Packet)this);
        writer.putNextString(commandId);
        writer.putNextByteUnsigned(0);
        writer.putNextBoolean(success);
        writer.putNextString(resultMessage);
    }

    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        if (client == null) {
            ModLogger.error("PacketExecuteCommand: client is null");
            return;
        }
        if ("string_command".equals(this.commandId)) {
            String commandString = this.parameterStrings.get("command");
            if (commandString == null) {
                client.sendPacket((Packet)new PacketExecuteCommand(this.commandId, false, "Missing command string"));
                return;
            }
            try {
                ModLogger.info("Player " + client.getName() + " executing string command: " + commandString);
                boolean success = server.commandsManager.runServerCommand(new ParsedCommand(commandString), client);
                HashMap<String, Object> paramMap = new HashMap<String, Object>();
                for (Map.Entry<String, String> entry : this.parameterStrings.entrySet()) {
                    paramMap.put(entry.getKey(), entry.getValue());
                }
                CommandHistory.recordExecution("string_command", commandString, paramMap);
                String message = success ? "Command executed: " + commandString : "Command failed: " + commandString;
                client.sendPacket((Packet)new PacketExecuteCommand(this.commandId, success, message));
            }
            catch (Exception e) {
                ModLogger.error("Failed to execute string command: " + commandString + " - " + e.getMessage());
                client.sendPacket((Packet)new PacketExecuteCommand(this.commandId, false, "Command failed: " + e.getMessage()));
            }
            return;
        }
        AdminCommand command = CommandRegistry.getCommand(this.commandId);
        if (command == null) {
            ModLogger.error("Unknown command: " + this.commandId);
            client.sendPacket((Packet)new PacketExecuteCommand(this.commandId, false, "Unknown command"));
            return;
        }
        if (!command.hasPermission(client)) {
            ModLogger.warn("Player " + client.getName() + " attempted to execute command without permission: " + this.commandId);
            client.sendPacket((Packet)new PacketExecuteCommand(this.commandId, false, "Permission denied"));
            return;
        }
        try {
            CommandResult result = command.execute(null, server, client, new Object[0]);
            HashMap<String, Object> paramMap = new HashMap<String, Object>();
            for (Map.Entry<String, String> entry : this.parameterStrings.entrySet()) {
                paramMap.put(entry.getKey(), entry.getValue());
            }
            CommandHistory.recordExecution(this.commandId, command.getDisplayName(), paramMap);
            client.sendPacket((Packet)new PacketExecuteCommand(this.commandId, result.isSuccess(), result.getMessage()));
            ModLogger.info("Player " + client.getName() + " executed command: " + this.commandId + " - " + (result.isSuccess() ? "SUCCESS" : "FAILED"));
        }
        catch (Exception e) {
            ModLogger.error("Error executing command " + this.commandId + ": " + e.getMessage());
            client.sendPacket((Packet)new PacketExecuteCommand(this.commandId, false, "Internal error: " + e.getMessage()));
        }
    }

    public void processClient(NetworkPacket packet, Client client) {
        if (this.resultMessage != null) {
            String color = this.success ? "\u00a7a" : "\u00a7c";
            client.chat.addMessage(color + this.resultMessage);
        }
    }
}

