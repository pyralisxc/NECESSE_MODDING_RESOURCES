/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.network.NetworkPacket
 *  necesse.engine.network.Packet
 *  necesse.engine.network.PacketReader
 *  necesse.engine.network.PacketWriter
 *  necesse.engine.network.client.Client
 *  necesse.engine.network.server.Server
 *  necesse.engine.network.server.ServerClient
 */
package customsettingslib.packets;

import customsettingslib.settings.CustomModSettings;
import java.util.HashMap;
import java.util.Map;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class PacketReadServerSettings
extends Packet {
    public Map<String, Map<String, Object>> newServerData = new HashMap<String, Map<String, Object>>();

    public PacketReadServerSettings(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader((Packet)this);
        if (!reader.getNextBoolean()) {
            for (CustomModSettings customModSettings : CustomModSettings.customModSettingsList) {
                HashMap newModServerData = new HashMap();
                for (String serverSetting : customModSettings.serverSettings) {
                    newModServerData.put(serverSetting, customModSettings.settingsMap.get(serverSetting).applyPacket(reader));
                }
                this.newServerData.put(customModSettings.mod.id, newModServerData);
            }
        }
    }

    public PacketReadServerSettings(boolean fromClient) {
        PacketWriter writer = new PacketWriter((Packet)this);
        writer.putNextBoolean(fromClient);
        if (!fromClient) {
            for (CustomModSettings customModSettings : CustomModSettings.customModSettingsList) {
                for (String serverSetting : customModSettings.serverSettings) {
                    customModSettings.settingsMap.get(serverSetting).setupPacket(writer);
                }
            }
        }
    }

    public void processClient(NetworkPacket packet, Client client) {
        for (CustomModSettings customModSettings : CustomModSettings.customModSettingsList) {
            customModSettings.serverDataSettings.putAll(this.newServerData.get(customModSettings.mod.id));
        }
    }

    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        if (client.checkHasRequestedSelf()) {
            for (CustomModSettings customModSettings : CustomModSettings.customModSettingsList) {
                for (String serverSetting : customModSettings.serverSettings) {
                    customModSettings.serverDataSettings.put(serverSetting, customModSettings.settingsMap.get(serverSetting).getValue());
                }
            }
            client.sendPacket((Packet)new PacketReadServerSettings(false));
        }
    }
}

