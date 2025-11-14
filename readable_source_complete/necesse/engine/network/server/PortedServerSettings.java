/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.server;

import java.io.File;
import necesse.engine.Settings;
import necesse.engine.network.server.ServerCreationSettings;
import necesse.engine.network.server.ServerSettings;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;

public class PortedServerSettings
extends ServerSettings {
    public int port;
    public boolean allowConnectByIP = true;

    public PortedServerSettings(ServerCreationSettings serverCreationSettings, int slots, int port) {
        super(serverCreationSettings, slots);
        this.port = port;
    }

    public static PortedServerSettings createHostServerSettings(ServerCreationSettings serverCreationSettings, int slots, int port) {
        if (serverCreationSettings == null || serverCreationSettings.worldFilePath == null || serverCreationSettings.worldFilePath.getName().isEmpty()) {
            throw new IllegalArgumentException("Invalid world name");
        }
        if (slots < 0 || slots > 250) {
            throw new IllegalArgumentException("Invalid slots");
        }
        if (port < 0 || port > 65535) {
            throw new IllegalArgumentException("Invalid port");
        }
        return new PortedServerSettings(serverCreationSettings, slots, port);
    }

    public static PortedServerSettings SingleplayerServer(ServerCreationSettings serverCreationSettings) {
        if (serverCreationSettings == null || serverCreationSettings.worldFilePath == null || serverCreationSettings.worldFilePath.getName().isEmpty()) {
            throw new IllegalArgumentException("Invalid world name");
        }
        return new PortedServerSettings(serverCreationSettings, 1, -1);
    }

    public static PortedServerSettings createFromSave(File worldFilePath, LoadData save) {
        int slots = save.getInt("slots", Settings.serverSlots);
        int port = save.getInt("port", Settings.serverPort);
        PortedServerSettings settings = new PortedServerSettings(new ServerCreationSettings(worldFilePath), slots, port);
        settings.password = save.getSafeString("password", settings.password);
        settings.allowConnectByIP = save.getBoolean("allowConnectByIP", settings.allowConnectByIP);
        return settings;
    }

    @Override
    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        save.addInt("port", this.port);
        save.addBoolean("allowConnectByIP", this.allowConnectByIP);
    }

    @Override
    public boolean isSinglePlayer() {
        return this.port == -1;
    }
}

