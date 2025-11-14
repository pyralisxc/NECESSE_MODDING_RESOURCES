/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.settlement.actions;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.job.EntityJobWorker;
import necesse.entity.mobs.job.JobType;
import necesse.entity.mobs.job.JobTypeHandler;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.inventory.container.settlement.actions.SettlementAccessRequiredContainerCustomAction;
import necesse.inventory.container.settlement.events.SettlementSettlerPrioritiesChangedEvent;
import necesse.inventory.container.settlement.events.SettlementSettlersChangedEvent;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;

public class SetSettlerPriorityAction
extends SettlementAccessRequiredContainerCustomAction {
    public SetSettlerPriorityAction(SettlementContainer container) {
        super(container);
    }

    public void runAndSend(int mobUniqueID, JobType jobType, int priority, boolean disabledByPlayer) {
        Packet content = new Packet();
        PacketWriter writer = new PacketWriter(content);
        writer.putNextInt(mobUniqueID);
        writer.putNextShortUnsigned(jobType.getID());
        writer.putNextInt(priority);
        writer.putNextBoolean(disabledByPlayer);
        this.runAndSendAction(content);
    }

    @Override
    public void executePacket(PacketReader reader, ServerSettlementData data, ServerClient client) {
        int mobUniqueID = reader.getNextInt();
        int typeID = reader.getNextShortUnsigned();
        int priority = reader.getNextInt();
        boolean disabledByPlayer = reader.getNextBoolean();
        Mob mob = data.getLevel().entityManager.mobs.get(mobUniqueID, false);
        if (mob instanceof EntityJobWorker) {
            EntityJobWorker worker = (EntityJobWorker)((Object)mob);
            JobTypeHandler.TypePriority typePriority = worker.getJobTypeHandler().getPriority(typeID);
            if (typePriority != null) {
                typePriority.priority = priority;
                typePriority.disabledByPlayer = disabledByPlayer;
                new SettlementSettlerPrioritiesChangedEvent(data, mobUniqueID, typePriority.type, typePriority.priority, typePriority.disabledByPlayer).applyAndSendToClientsAt(client);
            }
        } else {
            new SettlementSettlersChangedEvent(data).applyAndSendToClient(client);
        }
    }
}

