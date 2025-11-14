/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.settlement.actions.zones;

import java.awt.Point;
import java.awt.Rectangle;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.container.customAction.ContainerCustomAction;
import necesse.inventory.container.settlement.SettlementDependantContainer;
import necesse.inventory.container.settlement.events.SettlementDataEvent;
import necesse.inventory.container.settlement.events.SettlementOpenWorkZoneConfigEvent;
import necesse.inventory.container.settlement.events.SettlementRemovedEvent;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;
import necesse.level.maps.levelData.settlementData.zones.SettlementWorkZone;

public class CreateNewWorkZoneAction
extends ContainerCustomAction {
    public final SettlementDependantContainer container;

    public CreateNewWorkZoneAction(SettlementDependantContainer container) {
        this.container = container;
    }

    public void runAndSend(int zoneID, int uniqueID, Rectangle rectangle, Point anchor) {
        Packet content = new Packet();
        PacketWriter writer = new PacketWriter(content);
        writer.putNextInt(zoneID);
        writer.putNextInt(uniqueID);
        writer.putNextInt(rectangle.x);
        writer.putNextInt(rectangle.y);
        writer.putNextShortUnsigned(rectangle.width);
        writer.putNextShortUnsigned(rectangle.height);
        writer.putNextBoolean(anchor != null);
        if (anchor != null) {
            writer.putNextInt(anchor.x);
            writer.putNextInt(anchor.y);
        }
        this.runAndSendAction(content);
    }

    @Override
    public void executePacket(PacketReader reader) {
        int zoneID = reader.getNextInt();
        int uniqueID = reader.getNextInt();
        int x = reader.getNextInt();
        int y = reader.getNextInt();
        int width = reader.getNextShortUnsigned();
        int height = reader.getNextShortUnsigned();
        Rectangle rectangle = new Rectangle(x, y, width, height);
        Point anchor = null;
        if (reader.getNextBoolean()) {
            int anchorX = reader.getNextInt();
            int anchorY = reader.getNextInt();
            anchor = new Point(anchorX, anchorY);
        }
        if (this.container.client.isServer()) {
            ServerClient client = this.container.client.getServerClient();
            ServerSettlementData serverData = this.container.getServerData();
            if (serverData != null) {
                if (!serverData.networkData.doesClientHaveAccess(client)) {
                    new SettlementDataEvent(serverData).applyAndSendToClient(client);
                    return;
                }
                SettlementWorkZone zone = serverData.getWorkZones().createZone(zoneID, uniqueID, rectangle, anchor, client);
                if (zone != null) {
                    new SettlementOpenWorkZoneConfigEvent(zone).applyAndSendToClient(client.getServerClient());
                }
            } else {
                new SettlementRemovedEvent(0).applyAndSendToClient(client);
            }
        }
    }
}

