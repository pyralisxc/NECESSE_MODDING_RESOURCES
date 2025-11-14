/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.settlement.actions.zones;

import java.awt.Rectangle;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.container.customAction.ContainerCustomAction;
import necesse.inventory.container.settlement.SettlementDependantContainer;
import necesse.inventory.container.settlement.events.SettlementDataEvent;
import necesse.inventory.container.settlement.events.SettlementRemovedEvent;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;

public class ShrinkWorkZoneAction
extends ContainerCustomAction {
    public final SettlementDependantContainer container;

    public ShrinkWorkZoneAction(SettlementDependantContainer container) {
        this.container = container;
    }

    public void runAndSend(int uniqueID, Rectangle rectangle) {
        Packet content = new Packet();
        PacketWriter writer = new PacketWriter(content);
        writer.putNextInt(uniqueID);
        writer.putNextInt(rectangle.x);
        writer.putNextInt(rectangle.y);
        writer.putNextShortUnsigned(rectangle.width);
        writer.putNextShortUnsigned(rectangle.height);
        this.runAndSendAction(content);
    }

    @Override
    public void executePacket(PacketReader reader) {
        int uniqueID = reader.getNextInt();
        int x = reader.getNextInt();
        int y = reader.getNextInt();
        int width = reader.getNextShortUnsigned();
        int height = reader.getNextShortUnsigned();
        Rectangle rectangle = new Rectangle(x, y, width, height);
        if (this.container.client.isServer()) {
            ServerClient client = this.container.client.getServerClient();
            ServerSettlementData serverData = this.container.getServerData();
            if (serverData != null) {
                if (!serverData.networkData.doesClientHaveAccess(client)) {
                    new SettlementDataEvent(serverData).applyAndSendToClient(client);
                    return;
                }
                serverData.getWorkZones().shrinkZone(uniqueID, rectangle, client);
            } else {
                new SettlementRemovedEvent(0).applyAndSendToClient(client);
            }
        }
    }
}

