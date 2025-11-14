/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.job;

import java.util.Comparator;
import java.util.HashMap;
import java.util.stream.Stream;
import necesse.engine.util.ComputedValue;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.job.EntityJobWorker;
import necesse.entity.mobs.job.FoundJob;
import necesse.entity.mobs.job.JobTypeHandler;
import necesse.level.maps.levelData.jobs.LevelJob;
import necesse.level.maps.levelData.settlementData.ZoneTester;

public class JobFinder {
    public final EntityJobWorker worker;
    public final JobTypeHandler handler;
    public final Mob mob;
    protected final HashMap<Integer, ComputedValue<Object>> preSequenceComputes = new HashMap();

    public JobFinder(EntityJobWorker worker) {
        this.worker = worker;
        this.mob = worker.getMobWorker();
        this.handler = worker.getJobTypeHandler();
    }

    public Stream<FoundJob> streamFoundJobs() {
        if (this.handler == null) {
            return Stream.empty();
        }
        ZoneTester restrictZone = this.worker.getJobRestrictZone();
        return this.handler.streamJobs(this.worker).filter(e -> e.reservable.isAvailable(this.mob)).filter(e -> e.isWithinRestrictZone(restrictZone)).map(e -> {
            ComputedValue preSequenceCompute = this.preSequenceComputes.compute(e.getID(), (id, last) -> {
                if (last != null) {
                    return last;
                }
                JobTypeHandler.SubHandler<?> handler = this.handler.getJobHandler(e.getID());
                return handler.getPreSequenceCompute(this.worker);
            });
            return new FoundJob<LevelJob>(this.worker, (LevelJob)e, this.handler, preSequenceCompute);
        }).filter(e -> (e.priority == null || !e.priority.disabledBySettler && !e.priority.disabledByPlayer) && e.handler.canPerform(this.worker));
    }

    public FoundJob<?> findJob() {
        Comparator<FoundJob> comparator = Comparator.comparingInt(j -> -((LevelJob)j.job).getFirstPriority());
        comparator = comparator.thenComparingInt(j -> this.handler.prioritizeNextJobID == ((LevelJob)j.job).getID() ? 0 : 1).thenComparingInt(j -> this.handler.lastPerformedJobID == ((LevelJob)j.job).getID() ? 0 : 1).thenComparingInt(j -> -((LevelJob)j.job).getAfterPrioritizedPriority()).thenComparingInt(e -> e.priority == null ? 100000 : -e.priority.priority).thenComparingInt(e -> e.priority == null ? 100000 : -e.priority.type.getID()).thenComparingInt(e -> -((LevelJob)e.job).getSameTypePriority()).thenComparing((o1, o2) -> -Integer.compare(((LevelJob)o1.job).getSameJobPriority(), ((LevelJob)o2.job).getSameJobPriority())).thenComparingDouble(FoundJob::getDistanceFromWorker);
        return this.streamFoundJobs().sorted(comparator).filter(FoundJob::canMoveTo).filter(e -> e.getSequence() != null).findFirst().orElse(null);
    }
}

