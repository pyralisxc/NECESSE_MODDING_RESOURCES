/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.job;

import java.util.Collection;
import java.util.HashMap;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Stream;
import necesse.engine.registries.JobTypeRegistry;
import necesse.engine.registries.LevelJobRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.ComputedValue;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.job.EntityJobWorker;
import necesse.entity.mobs.job.FoundJob;
import necesse.entity.mobs.job.JobSequence;
import necesse.entity.mobs.job.JobType;
import necesse.level.maps.levelData.jobs.LevelJob;
import necesse.level.maps.levelData.settlementData.ZoneTester;

public class JobTypeHandler {
    public long globalCooldown;
    public int lastPerformedJobID = -1;
    public int prioritizeNextJobID = -1;
    public boolean resetPrioritizeNextJobIfFound = true;
    private final HashMap<Integer, SubHandler<?>> handlers = new HashMap();
    private final HashMap<String, TypePriority> priorities = new HashMap();

    public JobTypeHandler() {
        for (JobType type : JobTypeRegistry.getTypes()) {
            this.priorities.put(type.getStringID(), new TypePriority(type));
        }
    }

    public TypePriority getPriority(String jobType) {
        return this.priorities.get(jobType);
    }

    public TypePriority getPriority(int jobTypeID) {
        JobType type = JobTypeRegistry.getJobType(jobTypeID);
        if (type == null) {
            return null;
        }
        return this.priorities.get(type.getStringID());
    }

    public <T extends LevelJob> void setJobHandler(Class<T> jobClass, int minCooldownMS, int maxCooldownMS, int minWorkBreakBufferUsage, int maxWorkBreakBufferUsage, BiPredicate<SubHandler<T>, EntityJobWorker> canPerform, Function<FoundJob<T>, JobSequence> sequenceFunction) {
        this.setJobHandler(jobClass, minCooldownMS, maxCooldownMS, minWorkBreakBufferUsage, maxWorkBreakBufferUsage, canPerform, sequenceFunction, null);
    }

    public <T extends LevelJob> void setJobHandler(Class<T> jobClass, int minCooldownMS, int maxCooldownMS, int minWorkBreakBufferUsage, int maxWorkBreakBufferUsage, BiPredicate<SubHandler<T>, EntityJobWorker> canPerform, Function<FoundJob<T>, JobSequence> sequenceFunction, BiFunction<EntityJobWorker, SubHandler<T>, Object> preSequenceCompute) {
        int jobID = LevelJobRegistry.getJobID(jobClass);
        if (jobID == -1) {
            throw new IllegalArgumentException("Job class must be registered");
        }
        TypePriority priority = this.getPriority(LevelJobRegistry.getJobTypeID(jobClass));
        this.handlers.put(jobID, new SubHandler<T>(jobID, priority, minCooldownMS, maxCooldownMS, minWorkBreakBufferUsage, maxWorkBreakBufferUsage, canPerform, sequenceFunction, preSequenceCompute));
    }

    public <T extends LevelJob> SubHandler<T> getJobHandler(Class<T> jobClass) {
        int jobID = LevelJobRegistry.getJobID(jobClass);
        if (jobID == -1) {
            throw new IllegalArgumentException("Job class must be registered");
        }
        return this.handlers.get(jobID);
    }

    public SubHandler<?> getJobHandler(int jobID) {
        return this.handlers.get(jobID);
    }

    public void startGlobalCooldown(long currentTime, int milliseconds) {
        this.globalCooldown = currentTime + (long)milliseconds;
    }

    public boolean isOnGlobalCooldown(long currentTime) {
        return currentTime < this.globalCooldown;
    }

    public void startCooldowns(long currentTime) {
        for (SubHandler<?> value : this.handlers.values()) {
            value.startCooldown(currentTime);
        }
    }

    public Collection<SubHandler<?>> getJobHandlers() {
        return this.handlers.values();
    }

    public Collection<TypePriority> getTypePriorities() {
        return this.priorities.values();
    }

    public Stream<LevelJob> streamJobs(EntityJobWorker worker) {
        Stream<LevelJob> out = null;
        boolean searchInLevelJobData = false;
        for (SubHandler<?> value : this.handlers.values()) {
            Stream<LevelJob> stream;
            if (!value.canPerform(worker)) continue;
            if (value.searchInLevelJobData) {
                searchInLevelJobData = true;
            }
            if (value.extraJobStreamer == null || (stream = value.extraJobStreamer.stream(worker, value)) == null) continue;
            if (out == null) {
                out = stream.map(e -> e);
                continue;
            }
            out = Stream.concat(out, stream);
        }
        if (searchInLevelJobData) {
            Stream<LevelJob> stream = worker.streamJobsWithinRange();
            out = out == null ? stream : Stream.concat(out, stream);
        }
        if (out == null) {
            return Stream.empty();
        }
        return out;
    }

    public static class TypePriority {
        public final JobType type;
        public int priority;
        public boolean disabledBySettler;
        public boolean disabledByPlayer = false;

        public TypePriority(JobType type) {
            this.type = type;
            this.disabledBySettler = type.defaultDisabledBySettler;
        }

        public void loadSaveData(LoadData save) {
            this.priority = save.getInt("priority", this.priority, false);
            this.disabledByPlayer = save.getBoolean("disabledByPlayer", this.disabledByPlayer, false);
        }

        public void addSaveData(SaveData save) {
            save.addInt("priority", this.priority);
            save.addBoolean("disabledByPlayer", this.disabledByPlayer);
        }
    }

    public static class SubHandler<T extends LevelJob> {
        public final int jobID;
        public final TypePriority priority;
        public int minCooldownMS;
        public int maxCooldownMS;
        public int minWorkBreakBufferUsage;
        public int maxWorkBreakBufferUsage;
        public boolean disabledBySettler;
        public BiPredicate<SubHandler<T>, EntityJobWorker> canPerform;
        public Function<FoundJob<T>, JobSequence> sequenceFunction;
        public BiFunction<EntityJobWorker, SubHandler<T>, Object> preSequenceCompute;
        public long nextCooldownComplete;
        public JobStreamSupplier<? extends T> extraJobStreamer;
        public boolean searchInLevelJobData = true;

        public SubHandler(int jobID, TypePriority priority, int minCooldownMS, int maxCooldownMS, int minWorkBreakBufferUsage, int maxWorkBreakBufferUsage, BiPredicate<SubHandler<T>, EntityJobWorker> canPerform, Function<FoundJob<T>, JobSequence> sequenceFunction, BiFunction<EntityJobWorker, SubHandler<T>, Object> preSequenceCompute) {
            this.jobID = jobID;
            this.priority = priority;
            this.minCooldownMS = minCooldownMS;
            this.maxCooldownMS = maxCooldownMS;
            this.minWorkBreakBufferUsage = minWorkBreakBufferUsage;
            this.maxWorkBreakBufferUsage = maxWorkBreakBufferUsage;
            this.canPerform = canPerform;
            this.sequenceFunction = sequenceFunction;
            this.preSequenceCompute = preSequenceCompute;
        }

        public void startCooldown(long currentTime) {
            this.nextCooldownComplete = currentTime + (long)GameRandom.globalRandom.getIntBetween(this.minCooldownMS, this.maxCooldownMS);
        }

        public boolean isOnCooldown(long currentTime) {
            return currentTime <= this.nextCooldownComplete;
        }

        public int nextWorkBreakBufferUsage() {
            return GameRandom.globalRandom.getIntBetween(this.minWorkBreakBufferUsage, this.maxWorkBreakBufferUsage);
        }

        public boolean canPerform(EntityJobWorker worker) {
            if (this.disabledBySettler) {
                return false;
            }
            if (this.isOnCooldown(worker.getMobWorker().getWorldEntity().getTime())) {
                return false;
            }
            return this.canPerform.test(this, worker);
        }

        public Stream<? extends T> streamJobs(EntityJobWorker worker) {
            Stream<LevelJob> stream;
            ZoneTester restrictZone = worker.getJobRestrictZone();
            Stream<LevelJob> out = null;
            if (this.searchInLevelJobData) {
                out = worker.streamJobsWithinRange().filter(e -> e.getID() == this.jobID).filter(e -> e.isWithinRestrictZone(restrictZone)).map(e -> e);
            }
            if (this.extraJobStreamer != null && (stream = this.extraJobStreamer.stream(worker, this)) != null) {
                out = out == null ? stream : Stream.concat(out, stream);
            }
            if (out == null) {
                return Stream.empty();
            }
            return out;
        }

        public ComputedValue<Object> getPreSequenceCompute(EntityJobWorker worker) {
            return new ComputedValue<Object>(() -> this.preSequenceCompute == null ? null : this.preSequenceCompute.apply(worker, this));
        }

        public Stream<FoundJob<T>> streamFoundJobs(EntityJobWorker worker) {
            Mob mob = worker.getMobWorker();
            ComputedValue<Object> preSequenceCompute = this.getPreSequenceCompute(worker);
            return this.streamJobs(worker).filter(e -> e.reservable.isAvailable(mob)).map(e -> new FoundJob<LevelJob>(worker, (LevelJob)e, this, this.priority, preSequenceCompute));
        }

        public Stream<FoundJob<T>> streamFoundJobsFiltered(EntityJobWorker worker) {
            return this.streamFoundJobs(worker).filter(e -> (e.priority == null || !e.priority.disabledBySettler && !e.priority.disabledByPlayer) && e.handler.canPerform(worker));
        }
    }

    public static interface JobStreamSupplier<T extends LevelJob> {
        public Stream<T> stream(EntityJobWorker var1, SubHandler<?> var2);
    }
}

