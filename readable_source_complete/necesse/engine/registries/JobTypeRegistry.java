/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.registries;

import java.util.stream.Stream;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.registries.StaticObjectGameRegistry;
import necesse.entity.mobs.job.JobType;

public class JobTypeRegistry
extends StaticObjectGameRegistry<JobType> {
    public static final JobTypeRegistry instance = new JobTypeRegistry();

    private JobTypeRegistry() {
        super("JobType", 32766);
    }

    @Override
    public void registerCore() {
        JobTypeRegistry.registerType("needs", new JobType(false, false, null, null));
        JobTypeRegistry.registerType("expeditions", new JobType(false, false, null, null));
        JobTypeRegistry.registerType("tradingmission", new JobType(false, true, null, null));
        JobTypeRegistry.registerType("hauling", new JobType(true, false, new LocalMessage("jobs", "haulingname"), new LocalMessage("jobs", "haulingtip")));
        JobTypeRegistry.registerType("crafting", new JobType(true, false, new LocalMessage("jobs", "craftingname"), new LocalMessage("jobs", "craftingtip")));
        JobTypeRegistry.registerType("forestry", new JobType(true, false, new LocalMessage("jobs", "forestryname"), new LocalMessage("jobs", "forestrytip")));
        JobTypeRegistry.registerType("farming", new JobType(true, false, new LocalMessage("jobs", "farmingname"), new LocalMessage("jobs", "farmingtip")));
        JobTypeRegistry.registerType("fertilize", new JobType(true, true, new LocalMessage("jobs", "fertilizename"), new LocalMessage("jobs", "fertilizetip")));
        JobTypeRegistry.registerType("husbandry", new JobType(true, true, new LocalMessage("jobs", "husbandryname"), new LocalMessage("jobs", "husbandrytip")));
        JobTypeRegistry.registerType("fishing", new JobType(true, true, new LocalMessage("jobs", "fishingname"), null));
        JobTypeRegistry.registerType("hunting", new JobType(true, true, new LocalMessage("jobs", "huntingname"), new LocalMessage("jobs", "huntingtip")));
    }

    @Override
    protected void onRegister(JobType object, int id, String stringID, boolean isReplace) {
    }

    @Override
    protected void onRegistryClose() {
        for (JobType element : this.getElements()) {
            element.onJobTypeRegistryClosed();
        }
    }

    public static int registerType(String stringID, JobType type) {
        return instance.register(stringID, type);
    }

    public static JobType getJobType(int id) {
        return (JobType)instance.getElement(id);
    }

    public static JobType getJobType(String stringID) {
        return (JobType)instance.getElement(stringID);
    }

    public static int getJobTypeID(String stringID) {
        return instance.getElementID(stringID);
    }

    public static Iterable<JobType> getTypes() {
        return instance.getElements();
    }

    public static Stream<JobType> streamTypes() {
        return instance.streamElements();
    }
}

