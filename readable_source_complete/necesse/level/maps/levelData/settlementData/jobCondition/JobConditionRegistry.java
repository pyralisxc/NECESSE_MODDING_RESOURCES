/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData.settlementData.jobCondition;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.registries.ClassIDDataContainer;
import necesse.engine.registries.ClassedGameRegistry;
import necesse.level.maps.levelData.settlementData.jobCondition.DoForeverJobCondition;
import necesse.level.maps.levelData.settlementData.jobCondition.DoWhenThresholdsJobCondition;
import necesse.level.maps.levelData.settlementData.jobCondition.DoXTimesJobCondition;
import necesse.level.maps.levelData.settlementData.jobCondition.JobCondition;

public class JobConditionRegistry
extends ClassedGameRegistry<JobCondition, JobConditionRegistryElement> {
    public static final JobConditionRegistry instance = new JobConditionRegistry();

    public JobConditionRegistry() {
        super("JobCondition", Short.MAX_VALUE);
    }

    @Override
    public void registerCore() {
        JobConditionRegistry.registerJobCondition("doforever", DoForeverJobCondition.class, new LocalMessage("ui", "conditiondoforever"));
        JobConditionRegistry.registerJobCondition("doxtimes", DoXTimesJobCondition.class, new LocalMessage("ui", "conditiondocount", "count", "X"));
        JobConditionRegistry.registerJobCondition("dowhenthreshold", DoWhenThresholdsJobCondition.class, new LocalMessage("ui", "conditiondowhen", "condition", "X"));
    }

    @Override
    protected void onRegistryClose() {
    }

    public static int registerJobCondition(String stringID, Class<? extends JobCondition> jobConditionClass, GameMessage listedMessage) {
        try {
            return instance.register(stringID, new JobConditionRegistryElement(jobConditionClass, listedMessage));
        }
        catch (NoSuchMethodException e) {
            System.err.println("Could not register job condition " + jobConditionClass.getSimpleName() + ": Missing constructor with no parameters");
            return -1;
        }
    }

    public static JobCondition getNewJobCondition(int id) {
        try {
            return (JobCondition)((JobConditionRegistryElement)instance.getElement(id)).newInstance(new Object[0]);
        }
        catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JobCondition getNewJobCondition(String stringID) {
        return JobConditionRegistry.getNewJobCondition(JobConditionRegistry.getJobConditionID(stringID));
    }

    public static int getJobConditionID(String stringID) {
        return instance.getElementID(stringID);
    }

    public static int getJobConditionID(Class<? extends JobCondition> clazz) {
        return instance.getElementID(clazz);
    }

    public static GameMessage getJobConditionListedMessage(int id) {
        if (id == -1) {
            return null;
        }
        return ((JobConditionRegistryElement)JobConditionRegistry.instance.getElement((int)id)).listedMessage;
    }

    public static GameMessage getJobConditionListedMessage(String stringID) {
        return JobConditionRegistry.getJobConditionListedMessage(JobConditionRegistry.getJobConditionID(stringID));
    }

    public static boolean doesJobConditionExist(String stringID) {
        try {
            return instance.getElementIDRaw(stringID) >= 0;
        }
        catch (NoSuchElementException e) {
            return false;
        }
    }

    public static String getJobConditionStringID(int id) {
        return instance.getElementStringID(id);
    }

    public static Stream<ClassIDDataContainer<JobCondition>> streamMobs() {
        return instance.streamElements().map(e -> e);
    }

    public static List<ClassIDDataContainer<JobCondition>> getMobs() {
        return JobConditionRegistry.streamMobs().collect(Collectors.toList());
    }

    protected static class JobConditionRegistryElement
    extends ClassIDDataContainer<JobCondition> {
        public GameMessage listedMessage;

        public JobConditionRegistryElement(Class<? extends JobCondition> jobConditionClass, GameMessage listedMessage) throws NoSuchMethodException {
            super(jobConditionClass, new Class[0]);
            this.listedMessage = listedMessage;
        }
    }
}

