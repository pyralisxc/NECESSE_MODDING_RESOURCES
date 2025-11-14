/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.settlement.actions;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.inventory.container.settlement.actions.SettlementAccessRequiredContainerCustomAction;
import necesse.inventory.container.settlement.events.SettlementSettlersChangedEvent;
import necesse.level.maps.levelData.settlementData.LevelSettler;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;
import necesse.level.maps.levelData.settlementData.settler.SettlerMob;

public class SetSettlerSelfManageEquipmentAction
extends SettlementAccessRequiredContainerCustomAction {
    public SetSettlerSelfManageEquipmentAction(SettlementContainer container) {
        super(container);
    }

    public void runAndSend(int mobUniqueID, boolean selfManageEquipment) {
        Packet content = new Packet();
        PacketWriter writer = new PacketWriter(content);
        writer.putNextInt(mobUniqueID);
        writer.putNextBoolean(selfManageEquipment);
        this.runAndSendAction(content);
    }

    @Override
    public void executePacket(PacketReader reader, ServerSettlementData data, ServerClient client) {
        int mobUniqueID = reader.getNextInt();
        LevelSettler settler = data.getSettler(mobUniqueID);
        if (settler != null) {
            SettlerMob mob = settler.getMob();
            if (mob instanceof HumanMob) {
                ((HumanMob)mob).selfManageEquipment.set(reader.getNextBoolean());
            }
        } else {
            new SettlementSettlersChangedEvent(data).applyAndSendToClient(client);
        }
    }
}

