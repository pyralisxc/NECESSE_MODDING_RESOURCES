/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.settlement.data;

import java.util.HashMap;
import java.util.Map;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.JobTypeRegistry;
import necesse.entity.mobs.job.EntityJobWorker;
import necesse.entity.mobs.job.JobType;
import necesse.entity.mobs.job.JobTypeHandler;
import necesse.inventory.container.settlement.data.SettlementSettlerData;
import necesse.level.maps.levelData.settlementData.LevelSettler;

public class SettlementSettlerPrioritiesData
extends SettlementSettlerData {
    public final HashMap<JobType, TypePriority> priorities;

    public SettlementSettlerPrioritiesData(LevelSettler settler, EntityJobWorker worker) {
        super(settler);
        this.priorities = new HashMap();
        for (JobTypeHandler.TypePriority priority : worker.getJobTypeHandler().getTypePriorities()) {
            if (!priority.type.canChangePriority) continue;
            this.priorities.put(priority.type, new TypePriority(priority.disabledBySettler, priority.priority, priority.disabledByPlayer));
        }
    }

    public SettlementSettlerPrioritiesData(PacketReader reader) {
        super(reader);
        int size = reader.getNextShortUnsigned();
        this.priorities = new HashMap(size);
        for (int i = 0; i < size; ++i) {
            int typeID = reader.getNextShortUnsigned();
            JobType jobType = JobTypeRegistry.getJobType(typeID);
            boolean disabledBySettler = reader.getNextBoolean();
            int priority = reader.getNextInt();
            boolean disabledByPlayer = reader.getNextBoolean();
            this.priorities.put(jobType, new TypePriority(disabledBySettler, priority, disabledByPlayer));
        }
    }

    @Override
    public void writeContentPacket(PacketWriter writer) {
        super.writeContentPacket(writer);
        writer.putNextShortUnsigned(this.priorities.size());
        for (Map.Entry<JobType, TypePriority> e : this.priorities.entrySet()) {
            writer.putNextShortUnsigned(e.getKey().getID());
            TypePriority priority = e.getValue();
            writer.putNextBoolean(priority.disabledBySettler);
            writer.putNextInt(priority.priority);
            writer.putNextBoolean(priority.disabledByPlayer);
        }
    }

    public static class TypePriority {
        public final boolean disabledBySettler;
        public int priority;
        public boolean disabledByPlayer;

        public TypePriority(boolean disabledBySettler, int priority, boolean disabledByPlayer) {
            this.disabledBySettler = disabledBySettler;
            this.priority = priority;
            this.disabledByPlayer = disabledByPlayer;
        }
    }
}

