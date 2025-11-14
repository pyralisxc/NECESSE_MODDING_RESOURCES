/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import java.io.IOException;
import java.util.LinkedList;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameUtils;
import necesse.level.maps.Level;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.PresetUtils;
import necesse.level.maps.regionSystem.RegionPosition;

public class PacketPlacePreset
extends Packet {
    public final int levelIdentifierHashCode;
    public final int undoUniqueID;
    public final int tileX;
    public final int tileY;
    public final byte[] compressedScriptBytes;

    public PacketPlacePreset(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.levelIdentifierHashCode = reader.getNextInt();
        this.undoUniqueID = reader.getNextInt();
        this.tileX = reader.getNextInt();
        this.tileY = reader.getNextInt();
        int length = reader.getNextInt();
        this.compressedScriptBytes = reader.getNextBytes(length);
    }

    public PacketPlacePreset(Level level, int undoUniqueID, int tileX, int tileY, Preset preset) throws IOException {
        this.levelIdentifierHashCode = level.getIdentifierHashCode();
        this.undoUniqueID = undoUniqueID;
        this.tileX = tileX;
        this.tileY = tileY;
        String compressedScript = preset.getSaveData().getScript(true);
        this.compressedScriptBytes = GameUtils.compressData(compressedScript.getBytes());
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(this.levelIdentifierHashCode);
        writer.putNextInt(undoUniqueID);
        writer.putNextInt(tileX);
        writer.putNextInt(tileY);
        writer.putNextInt(this.compressedScriptBytes.length);
        writer.putNextBytes(this.compressedScriptBytes);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        if (!client.levelManager.isLevelLoaded(this.levelIdentifierHashCode)) {
            return;
        }
        try {
            byte[] decompressedData = GameUtils.decompressData(this.compressedScriptBytes);
            String script = new String(decompressedData);
            Preset preset = new Preset(script);
            PresetUtils.applyPresetIfClientHasRegionsLoaded(client, this.levelIdentifierHashCode, preset, this.tileX, this.tileY);
        }
        catch (Exception e) {
            System.err.println("Failed to parse script data from server");
            e.printStackTrace();
        }
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        if (!(server.world.settings.creativeMode || client.getPermissionLevel().getLevel() > PermissionLevel.ADMIN.getLevel() && server.world.settings.cheatsAllowedOrHidden())) {
            return;
        }
        Level level = client.getLevel();
        if (level.getIdentifierHashCode() == this.levelIdentifierHashCode) {
            try {
                byte[] decompressedData = GameUtils.decompressData(this.compressedScriptBytes);
                String script = new String(decompressedData);
                Preset preset = new Preset(script);
                PresetUtils.placePresetFromServer(client, preset, level, this.tileX, this.tileY, this.undoUniqueID, true);
                LinkedList<RegionPosition> regionPositions = PresetUtils.getRegionPositions(level, preset, this.tileX, this.tileY);
                server.network.sendToClientsWithAnyRegionExcept(this, regionPositions, client);
            }
            catch (Exception e) {
                System.err.println("Failed to parse script data from client " + client.getName());
                e.printStackTrace();
            }
        }
    }
}

