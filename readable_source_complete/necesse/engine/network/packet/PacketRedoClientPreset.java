/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import java.io.IOException;
import java.util.LinkedList;
import java.util.ListIterator;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketPlacePreset;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.level.maps.Level;
import necesse.level.maps.presets.PresetRedoData;
import necesse.level.maps.presets.PresetUndoData;
import necesse.level.maps.presets.PresetUtils;
import necesse.level.maps.regionSystem.RegionPosition;

public class PacketRedoClientPreset
extends Packet {
    public final int redoUniqueID;

    public PacketRedoClientPreset(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.redoUniqueID = reader.getNextInt();
    }

    public PacketRedoClientPreset(int redoUniqueID) {
        this.redoUniqueID = redoUniqueID;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(redoUniqueID);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        client.presetRedoUniqueIDs.remove((Object)this.redoUniqueID);
        client.presetUndoData.removeIf(data -> data.uniqueID == this.redoUniqueID);
        client.presetUndoData.add(new PresetUndoData(this.redoUniqueID, null, null, 0, 0));
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        if (!(server.world.settings.creativeMode || client.getPermissionLevel().getLevel() > PermissionLevel.ADMIN.getLevel() && server.world.settings.cheatsAllowedOrHidden())) {
            return;
        }
        ListIterator<PresetRedoData> iterator = client.presetRedoData.listIterator();
        while (iterator.hasNext()) {
            PresetRedoData redoData = iterator.next();
            if (redoData.uniqueID != this.redoUniqueID) continue;
            iterator.remove();
            Level level = server.world.getLevel(redoData.levelIdentifier);
            if (level != null) {
                PresetUtils.placePresetFromServer(client, redoData.preset, level, redoData.tileX, redoData.tileY, this.redoUniqueID, false);
                LinkedList<RegionPosition> regionPositions = PresetUtils.getRegionPositions(level, redoData.preset, redoData.tileX, redoData.tileY);
                try {
                    PacketPlacePreset placePacket = new PacketPlacePreset(level, this.redoUniqueID, redoData.tileX, redoData.tileY, redoData.preset);
                    server.network.sendToClientsWithAnyRegion(placePacket, regionPositions);
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                client.sendPacket(this);
            }
            return;
        }
    }
}

