/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.commands.PermissionLevel
 *  necesse.engine.network.NetworkPacket
 *  necesse.engine.network.Packet
 *  necesse.engine.network.PacketReader
 *  necesse.engine.network.PacketWriter
 *  necesse.engine.network.client.Client
 *  necesse.engine.network.server.Server
 *  necesse.engine.network.server.ServerClient
 */
package medievalsim.commandcenter.packets;

import medievalsim.commandcenter.settings.AdminSetting;
import medievalsim.commandcenter.settings.SettingsRegistry;
import medievalsim.util.ModLogger;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class PacketSyncSettings
extends Packet {
    private String settingId;
    private String valueStr;
    private boolean success;

    public PacketSyncSettings(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader((Packet)this);
        this.settingId = reader.getNextString();
        this.valueStr = reader.getNextString();
        this.success = reader.getNextBoolean();
    }

    public PacketSyncSettings(String settingId, String valueStr) {
        this(settingId, valueStr, true);
    }

    public PacketSyncSettings(String settingId, String valueStr, boolean success) {
        this.settingId = settingId;
        this.valueStr = valueStr;
        this.success = success;
        PacketWriter writer = new PacketWriter((Packet)this);
        writer.putNextString(settingId);
        writer.putNextString(valueStr);
        writer.putNextBoolean(success);
    }

    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        if (client.getPermissionLevel() == null || client.getPermissionLevel().getLevel() < PermissionLevel.ADMIN.getLevel()) {
            ModLogger.warn("Player " + client.getName() + " attempted to change settings without permission");
            client.sendPacket((Packet)new PacketSyncSettings(this.settingId, this.valueStr, false));
            return;
        }
        AdminSetting<?> setting = SettingsRegistry.getSetting(this.settingId);
        if (setting == null) {
            ModLogger.error("Unknown setting: " + this.settingId);
            client.sendPacket((Packet)new PacketSyncSettings(this.settingId, this.valueStr, false));
            return;
        }
        if (setting.isReadOnly()) {
            ModLogger.warn("Attempted to modify read-only setting: " + this.settingId);
            client.sendPacket((Packet)new PacketSyncSettings(this.settingId, this.valueStr, false));
            return;
        }
        try {
            boolean set = this.setSettingValue(setting, this.valueStr);
            if (set) {
                server.network.sendToAllClients((Packet)new PacketSyncSettings(this.settingId, this.valueStr, true));
                ModLogger.info("Player " + client.getName() + " changed setting " + this.settingId + " to " + this.valueStr);
            } else {
                client.sendPacket((Packet)new PacketSyncSettings(this.settingId, this.valueStr, false));
            }
        }
        catch (Exception e) {
            ModLogger.error("Error setting value for " + this.settingId + ": " + e.getMessage());
            client.sendPacket((Packet)new PacketSyncSettings(this.settingId, this.valueStr, false));
        }
    }

    public void processClient(NetworkPacket packet, Client client) {
        if (this.success) {
            client.chat.addMessage("\u00a7aSetting updated: " + this.settingId);
        } else {
            client.chat.addMessage("\u00a7cFailed to update setting: " + this.settingId);
        }
    }

    private boolean setSettingValue(AdminSetting<?> setting, String valueStr) {
        switch (setting.getType()) {
            case INTEGER: {
                return setting.setValue(Integer.parseInt(valueStr));
            }
            case LONG: {
                return setting.setValue(Long.parseLong(valueStr));
            }
            case FLOAT: {
                return setting.setValue(Float.valueOf(Float.parseFloat(valueStr)));
            }
            case BOOLEAN: {
                return setting.setValue(Boolean.parseBoolean(valueStr));
            }
            case STRING: {
                return setting.setValue(valueStr);
            }
        }
        return false;
    }
}

