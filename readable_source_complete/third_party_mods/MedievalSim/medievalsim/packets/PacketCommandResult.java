/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.Settings
 *  necesse.engine.modLoader.LoadedMod
 *  necesse.engine.modLoader.ModLoader
 *  necesse.engine.network.NetworkPacket
 *  necesse.engine.network.Packet
 *  necesse.engine.network.PacketReader
 *  necesse.engine.network.PacketWriter
 *  necesse.engine.network.client.Client
 */
package medievalsim.packets;

import medievalsim.MedievalSimSettings;
import necesse.engine.Settings;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.modLoader.ModLoader;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;

public class PacketCommandResult
extends Packet {
    public final int requestId;
    public final boolean success;
    public final String message;
    public final String commandString;

    public PacketCommandResult(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader((Packet)this);
        this.requestId = reader.getNextInt();
        this.success = reader.getNextBoolean();
        this.message = reader.getNextString();
        this.commandString = reader.getNextString();
    }

    public PacketCommandResult(int requestId, boolean success, String message, String commandString) {
        this.requestId = requestId;
        this.success = success;
        this.message = message;
        this.commandString = commandString;
        PacketWriter writer = new PacketWriter((Packet)this);
        writer.putNextInt(requestId);
        writer.putNextBoolean(success);
        writer.putNextString(message);
        writer.putNextString(commandString);
    }

    public void processClient(NetworkPacket packet, Client client) {
        System.out.println("[CommandResult] " + (this.success ? "\u2713" : "\u2717") + " " + this.message);
        if (this.success && this.commandString != null && !this.commandString.trim().isEmpty()) {
            try {
                LoadedMod mod = ModLoader.getEnabledMods().stream().filter(m -> m.id.equals("medieval.sim")).findFirst().orElse(null);
                if (mod != null) {
                    MedievalSimSettings settings = (MedievalSimSettings)mod.getSettings();
                    settings.commandHistory.add(0, this.commandString);
                    while (settings.commandHistory.size() > 20) {
                        settings.commandHistory.remove(settings.commandHistory.size() - 1);
                    }
                    Settings.saveClientSettings();
                }
            }
            catch (Exception e) {
                System.err.println("[PacketCommandResult] Failed to save command history: " + e.getMessage());
            }
        }
        if (client.getLevel() != null && client.getLevel().getClient() != null) {
            String chatMessage = (this.success ? "\u00a7a" : "\u00a7c") + this.message;
            client.getLevel().getClient().chat.addMessage(chatMessage);
        }
    }
}

