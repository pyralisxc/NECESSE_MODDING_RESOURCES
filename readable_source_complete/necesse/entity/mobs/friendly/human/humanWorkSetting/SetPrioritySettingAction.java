/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.friendly.human.humanWorkSetting;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.entity.mobs.job.JobType;
import necesse.entity.mobs.job.JobTypeHandler;
import necesse.inventory.container.customAction.ContainerCustomAction;
import necesse.inventory.container.mob.ShopContainer;
import necesse.inventory.container.settlement.events.SettlementSettlerPrioritiesChangedEvent;
import necesse.level.maps.levelData.settlementData.LevelSettler;

public class SetPrioritySettingAction
extends ContainerCustomAction {
    public final ShopContainer container;

    public SetPrioritySettingAction(ShopContainer container) {
        this.container = container;
    }

    public void runAndSendChange(JobType jobType, int priority, boolean disabledByPlayer) {
        Packet content = new Packet();
        PacketWriter writer = new PacketWriter(content);
        writer.putNextShortUnsigned(jobType.getID());
        writer.putNextInt(priority);
        writer.putNextBoolean(disabledByPlayer);
        this.runAndSendAction(content);
    }

    @Override
    public void executePacket(PacketReader reader) {
        JobTypeHandler.TypePriority typePriority;
        LevelSettler settler;
        int typeID = reader.getNextShortUnsigned();
        int priority = reader.getNextInt();
        boolean disabledByPlayer = reader.getNextBoolean();
        if (this.container.client.isServer() && (settler = this.container.humanShop.levelSettler) != null && (typePriority = this.container.humanShop.jobTypeHandler.getPriority(typeID)) != null) {
            typePriority.priority = priority;
            typePriority.disabledByPlayer = disabledByPlayer;
            new SettlementSettlerPrioritiesChangedEvent(settler.data, this.container.humanShop.getUniqueID(), typePriority.type, priority, disabledByPlayer).applyAndSendToClientsAt(this.container.client.getServerClient());
        }
    }
}

