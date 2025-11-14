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
import necesse.level.maps.levelData.settlementData.zones.SettlementHusbandryZone;
import necesse.level.maps.levelData.settlementData.zones.SettlementWorkZone;

public class HusbandryZoneConfigCustomAction
extends ContainerCustomAction {
    public final SettlementDependantContainer container;

    public HusbandryZoneConfigCustomAction(SettlementDependantContainer container) {
        this.container = container;
    }

    public void runAndSendSetMaxAnimals(int zoneUniqueID, int maxAnimals) {
        Packet content = new Packet();
        PacketWriter writer = new PacketWriter(content);
        writer.putNextInt(zoneUniqueID);
        writer.putNextMaxValue(0, 2);
        writer.putNextInt(maxAnimals);
        this.runAndSendAction(content);
    }

    public void runAndSendSetMaleRatio(int zoneUniqueID, float maleRatio) {
        Packet content = new Packet();
        PacketWriter writer = new PacketWriter(content);
        writer.putNextInt(zoneUniqueID);
        writer.putNextMaxValue(1, 2);
        writer.putNextFloat(maleRatio);
        this.runAndSendAction(content);
    }

    @Override
    public void executePacket(PacketReader reader) {
        ServerSettlementData serverData;
        int zoneUniqueID = reader.getNextInt();
        int packetType = reader.getNextMaxValue(2);
        if (this.container.client.isServer() && (serverData = this.container.getServerData()) != null) {
            SettlementWorkZone zone = serverData.getWorkZones().getZone(zoneUniqueID);
            if (zone == null) {
                new SettlementWorkZoneRemovedEvent(serverData, zoneUniqueID).applyAndSendToClient(this.container.client.getServerClient());
            } else if (zone instanceof SettlementHusbandryZone) {
                SettlementHusbandryZone husbandryZone = (SettlementHusbandryZone)zone;
                if (packetType == 0) {
                    int maxAnimals = reader.getNextInt();
                    husbandryZone.setMaxAnimalsBeforeSlaughter(maxAnimals);
                } else if (packetType == 1) {
                    float maleRatio = reader.getNextFloat();
                    husbandryZone.setSlaughterMaleRatio(maleRatio);
                }
            } else {
                new SettlementWorkZoneChangedEvent(serverData, zone).applyAndSendToClient(this.container.client.getServerClient());
            }
        }
    }
}

