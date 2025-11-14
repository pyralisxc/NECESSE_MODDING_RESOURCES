/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.network.Packet
 *  necesse.engine.registries.PacketRegistry
 */
package medievalsim.registries;

import java.util.Arrays;
import java.util.List;
import medievalsim.commandcenter.packets.PacketSyncSettings;
import medievalsim.packets.PacketCommandResult;
import medievalsim.packets.PacketConfigureProtectedZone;
import medievalsim.packets.PacketConfigurePvPZone;
import medievalsim.packets.PacketCreateZone;
import medievalsim.packets.PacketDeleteZone;
import medievalsim.packets.PacketExecuteCommand;
import medievalsim.packets.PacketExpandZone;
import medievalsim.packets.PacketForceClean;
import medievalsim.packets.PacketPvPZoneEntryDialog;
import medievalsim.packets.PacketPvPZoneEntryResponse;
import medievalsim.packets.PacketPvPZoneExitDialog;
import medievalsim.packets.PacketPvPZoneExitResponse;
import medievalsim.packets.PacketPvPZoneSpawnDialog;
import medievalsim.packets.PacketPvPZoneSpawnResponse;
import medievalsim.packets.PacketRenameZone;
import medievalsim.packets.PacketRequestPlayerList;
import medievalsim.packets.PacketRequestZoneSync;
import medievalsim.packets.PacketShrinkZone;
import medievalsim.packets.PacketZoneChanged;
import medievalsim.packets.PacketZoneRemoved;
import medievalsim.packets.PacketZoneSync;
import medievalsim.util.ModLogger;
import necesse.engine.network.Packet;
import necesse.engine.registries.PacketRegistry;

public class MedievalSimPackets {
    private static final List<Class<? extends Packet>> PACKETS = Arrays.asList(PacketCreateZone.class, PacketExpandZone.class, PacketShrinkZone.class, PacketDeleteZone.class, PacketRenameZone.class, PacketConfigurePvPZone.class, PacketConfigureProtectedZone.class, PacketZoneSync.class, PacketRequestZoneSync.class, PacketZoneChanged.class, PacketZoneRemoved.class, PacketPvPZoneEntryDialog.class, PacketPvPZoneEntryResponse.class, PacketPvPZoneExitDialog.class, PacketPvPZoneExitResponse.class, PacketPvPZoneSpawnDialog.class, PacketPvPZoneSpawnResponse.class, PacketForceClean.class, PacketRequestPlayerList.class, PacketExecuteCommand.class, PacketCommandResult.class, PacketSyncSettings.class);

    public static void registerCore() {
        PACKETS.forEach(PacketRegistry::registerPacket);
        ModLogger.info("Registered %d network packets", PACKETS.size());
    }
}

