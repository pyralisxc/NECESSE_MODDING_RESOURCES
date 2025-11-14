/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container;

import necesse.engine.GameLog;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.world.worldData.SettlementsWorldData;
import necesse.inventory.container.Container;
import necesse.inventory.container.customAction.ContentCustomAction;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;

public class SettlementNameContainer
extends Container {
    public final ContentCustomAction submitButton;
    public final int settlementUniqueID;

    public SettlementNameContainer(final NetworkClient client, int uniqueSeed, PacketReader reader) {
        super(client, uniqueSeed);
        this.settlementUniqueID = reader.getNextInt();
        if (this.settlementUniqueID == 0 && client.isServer()) {
            GameLog.warn.println("SettlementNameContainer did not get sent a settlementUniqueID");
            this.close();
        }
        this.submitButton = this.registerAction(new ContentCustomAction(){

            @Override
            protected void run(Packet content) {
                if (client.isServer()) {
                    GameMessage name = GameMessage.fromContentPacket(content);
                    ServerSettlementData settlement = SettlementsWorldData.getSettlementsData(client.getServerClient().getServer()).getOrLoadServerData(SettlementNameContainer.this.settlementUniqueID);
                    if (settlement != null) {
                        settlement.networkData.setName(name);
                    }
                    SettlementNameContainer.this.close();
                }
            }
        });
    }

    public static Packet getContainerContent(int settlementUniqueID) {
        Packet content = new Packet();
        new PacketWriter(content).putNextInt(settlementUniqueID);
        return content;
    }
}

