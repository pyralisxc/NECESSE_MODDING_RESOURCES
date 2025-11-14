/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.settlement.actions;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.customAction.ContainerCustomAction;
import necesse.inventory.container.settlement.SettlementDependantContainer;
import necesse.inventory.container.settlement.events.SettlementWorkZoneChangedEvent;
import necesse.inventory.container.settlement.events.SettlementWorkZoneRemovedEvent;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;
import necesse.level.maps.levelData.settlementData.zones.SettlementForestryZone;
import necesse.level.maps.levelData.settlementData.zones.SettlementWorkZone;

public class ForestryZoneConfigCustomAction
extends ContainerCustomAction {
    public final SettlementDependantContainer container;

    public ForestryZoneConfigCustomAction(SettlementDependantContainer container) {
        this.container = container;
    }

    public void runAndSendSetAllowChopping(int zoneUniqueID, boolean allowChopping) {
        Packet content = new Packet();
        PacketWriter writer = new PacketWriter(content);
        writer.putNextInt(zoneUniqueID);
        writer.putNextMaxValue(0, 3);
        writer.putNextBoolean(allowChopping);
        this.runAndSendAction(content);
    }

    public void runAndSendSetReplantTrees(int zoneUniqueID, boolean replantTrees) {
        Packet content = new Packet();
        PacketWriter writer = new PacketWriter(content);
        writer.putNextInt(zoneUniqueID);
        writer.putNextMaxValue(1, 3);
        writer.putNextBoolean(replantTrees);
        this.runAndSendAction(content);
    }

    public void runAndSendSetAutoPlantSapling(int zoneUniqueID, int saplingID) {
        Packet content = new Packet();
        PacketWriter writer = new PacketWriter(content);
        writer.putNextInt(zoneUniqueID);
        writer.putNextMaxValue(2, 3);
        writer.putNextInt(saplingID);
        this.runAndSendAction(content);
    }

    @Override
    public void executePacket(PacketReader reader) {
        ServerSettlementData serverData;
        int zoneUniqueID = reader.getNextInt();
        int packetType = reader.getNextMaxValue(3);
        if (this.container.client.isServer() && (serverData = this.container.getServerData()) != null) {
            SettlementWorkZone zone = serverData.getWorkZones().getZone(zoneUniqueID);
            if (zone == null) {
                new SettlementWorkZoneRemovedEvent(serverData, zoneUniqueID).applyAndSendToClient(this.container.client.getServerClient());
            } else if (zone instanceof SettlementForestryZone) {
                SettlementForestryZone forestryZone = (SettlementForestryZone)zone;
                if (packetType == 0) {
                    boolean allowChopping = reader.getNextBoolean();
                    forestryZone.setChoppingAllowed(allowChopping);
                } else if (packetType == 1) {
                    boolean replantTrees = reader.getNextBoolean();
                    forestryZone.setReplantChoppedDownTrees(replantTrees);
                } else if (packetType == 2) {
                    int saplingID = reader.getNextInt();
                    forestryZone.setAutoPlantSaplingID(saplingID);
                }
            } else {
                new SettlementWorkZoneChangedEvent(serverData, zone).applyAndSendToClient(this.container.client.getServerClient());
            }
        }
    }
}

