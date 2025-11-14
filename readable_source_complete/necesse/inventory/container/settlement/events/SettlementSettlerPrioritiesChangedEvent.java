/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.settlement.events;

import java.util.HashMap;
import java.util.Map;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.JobTypeRegistry;
import necesse.entity.mobs.job.JobType;
import necesse.inventory.container.events.ContainerEvent;
import necesse.inventory.container.settlement.data.SettlementSettlerPrioritiesData;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;

public class SettlementSettlerPrioritiesChangedEvent
extends ContainerEvent {
    public final int settlementUniqueID;
    public final int mobUniqueID;
    public final boolean includeDisabledBySettler;
    public final HashMap<JobType, SettlementSettlerPrioritiesData.TypePriority> priorities;

    public SettlementSettlerPrioritiesChangedEvent(ServerSettlementData data, int mobUniqueID, JobType jobType, int priority, boolean disabledByPlayer) {
        this.settlementUniqueID = data.uniqueID;
        this.mobUniqueID = mobUniqueID;
        this.includeDisabledBySettler = false;
        this.priorities = new HashMap();
        this.priorities.put(jobType, new SettlementSettlerPrioritiesData.TypePriority(false, priority, disabledByPlayer));
    }

    public SettlementSettlerPrioritiesChangedEvent(ServerSettlementData data, int mobUniqueID, boolean includeDisabledBySettler, HashMap<JobType, SettlementSettlerPrioritiesData.TypePriority> priorities) {
        this.settlementUniqueID = data.uniqueID;
        this.mobUniqueID = mobUniqueID;
        this.includeDisabledBySettler = includeDisabledBySettler;
        this.priorities = priorities;
    }

    public SettlementSettlerPrioritiesChangedEvent(ServerSettlementData data, int mobUniqueID, HashMap<JobType, SettlementSettlerPrioritiesData.TypePriority> priorities) {
        this(data, mobUniqueID, false, priorities);
    }

    public SettlementSettlerPrioritiesChangedEvent(PacketReader reader) {
        super(reader);
        this.settlementUniqueID = reader.getNextInt();
        this.mobUniqueID = reader.getNextInt();
        this.includeDisabledBySettler = reader.getNextBoolean();
        int prioritiesSize = reader.getNextShortUnsigned();
        this.priorities = new HashMap(prioritiesSize);
        for (int i = 0; i < prioritiesSize; ++i) {
            int jobTypeID = reader.getNextShortUnsigned();
            boolean disabledBySettler = this.includeDisabledBySettler && reader.getNextBoolean();
            JobType jobType = JobTypeRegistry.getJobType(jobTypeID);
            int priority = reader.getNextInt();
            boolean disabledByPlayer = reader.getNextBoolean();
            if (jobType == null) continue;
            this.priorities.put(jobType, new SettlementSettlerPrioritiesData.TypePriority(disabledBySettler, priority, disabledByPlayer));
        }
    }

    @Override
    public void write(PacketWriter writer) {
        writer.putNextInt(this.settlementUniqueID);
        writer.putNextInt(this.mobUniqueID);
        writer.putNextBoolean(this.includeDisabledBySettler);
        writer.putNextShortUnsigned(this.priorities.size());
        for (Map.Entry<JobType, SettlementSettlerPrioritiesData.TypePriority> e : this.priorities.entrySet()) {
            writer.putNextShortUnsigned(e.getKey().getID());
            if (this.includeDisabledBySettler) {
                writer.putNextBoolean(e.getValue().disabledBySettler);
            }
            writer.putNextInt(e.getValue().priority);
            writer.putNextBoolean(e.getValue().disabledByPlayer);
        }
    }
}

