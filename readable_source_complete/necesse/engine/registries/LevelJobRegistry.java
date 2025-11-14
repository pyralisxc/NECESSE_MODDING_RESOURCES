/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.registries;

import java.lang.reflect.InvocationTargetException;
import necesse.engine.registries.ClassIDDataContainer;
import necesse.engine.registries.ClassedGameRegistry;
import necesse.engine.registries.JobTypeRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.LoadDataException;
import necesse.level.maps.levelData.jobs.ConsumeFoodLevelJob;
import necesse.level.maps.levelData.jobs.FertilizeLevelJob;
import necesse.level.maps.levelData.jobs.FillApiaryFrameLevelJob;
import necesse.level.maps.levelData.jobs.FishingPositionLevelJob;
import necesse.level.maps.levelData.jobs.ForestryLevelJob;
import necesse.level.maps.levelData.jobs.HarvestApiaryLevelJob;
import necesse.level.maps.levelData.jobs.HarvestCropLevelJob;
import necesse.level.maps.levelData.jobs.HarvestFruitLevelJob;
import necesse.level.maps.levelData.jobs.HasStorageLevelJob;
import necesse.level.maps.levelData.jobs.HaulFromLevelJob;
import necesse.level.maps.levelData.jobs.HuntMobLevelJob;
import necesse.level.maps.levelData.jobs.LevelJob;
import necesse.level.maps.levelData.jobs.ManageEquipmentLevelJob;
import necesse.level.maps.levelData.jobs.MilkHusbandryMobLevelJob;
import necesse.level.maps.levelData.jobs.PlantCropLevelJob;
import necesse.level.maps.levelData.jobs.PlantSaplingLevelJob;
import necesse.level.maps.levelData.jobs.ShearHusbandryMobLevelJob;
import necesse.level.maps.levelData.jobs.ShippingChestLevelJob;
import necesse.level.maps.levelData.jobs.SlaughterHusbandryMobLevelJob;
import necesse.level.maps.levelData.jobs.StartExpeditionLevelJob;
import necesse.level.maps.levelData.jobs.StorePickupItemLevelJob;
import necesse.level.maps.levelData.jobs.UseWorkstationLevelJob;

public class LevelJobRegistry
extends ClassedGameRegistry<LevelJob, RegistryItem> {
    public static final LevelJobRegistry instance = new LevelJobRegistry();
    public static int consumeFoodID;
    public static int equipArmorID;
    public static int hasStorageID;
    public static int startExpeditionID;
    public static int startTradingMissionID;
    public static int haulFromID;
    public static int storePickupItemID;
    public static int useWorkstationID;
    public static int forestryID;
    public static int plantSaplingID;
    public static int fertilizeID;
    public static int slaughterHusbandryMobID;
    public static int milkHusbandryMobID;
    public static int shearHusbandryMobID;
    public static int harvestApiaryID;
    public static int fillApiaryFrameID;
    public static int harvestCropID;
    public static int plantCropID;
    public static int harvestFruitID;
    public static int fishingPositionID;
    public static int huntMobID;

    private LevelJobRegistry() {
        super("LevelJob", 32762);
    }

    @Override
    public void registerCore() {
        consumeFoodID = LevelJobRegistry.registerJob("consumefood", ConsumeFoodLevelJob.class, "needs");
        equipArmorID = LevelJobRegistry.registerJob("manageequipment", ManageEquipmentLevelJob.class, "needs");
        hasStorageID = LevelJobRegistry.registerJob("hasstorage", HasStorageLevelJob.class, "needs", 100);
        startExpeditionID = LevelJobRegistry.registerJob("startexpedition", StartExpeditionLevelJob.class, "expeditions");
        startTradingMissionID = LevelJobRegistry.registerJob("starttrading", ShippingChestLevelJob.class, "tradingmission");
        haulFromID = LevelJobRegistry.registerJob("haulfrom", HaulFromLevelJob.class, "hauling");
        storePickupItemID = LevelJobRegistry.registerJob("storepickupitem", StorePickupItemLevelJob.class, "hauling", 100);
        useWorkstationID = LevelJobRegistry.registerJob("useworkstation", UseWorkstationLevelJob.class, "crafting", 0);
        forestryID = LevelJobRegistry.registerJob("forestry", ForestryLevelJob.class, "forestry");
        plantSaplingID = LevelJobRegistry.registerJob("plantsapling", PlantSaplingLevelJob.class, "forestry", 100);
        fertilizeID = LevelJobRegistry.registerJob("fertilize", FertilizeLevelJob.class, "fertilize");
        slaughterHusbandryMobID = LevelJobRegistry.registerJob("slaughterhusbandry", SlaughterHusbandryMobLevelJob.class, "husbandry");
        milkHusbandryMobID = LevelJobRegistry.registerJob("milkhusbandry", MilkHusbandryMobLevelJob.class, "husbandry");
        shearHusbandryMobID = LevelJobRegistry.registerJob("shearhusbandry", ShearHusbandryMobLevelJob.class, "husbandry");
        harvestApiaryID = LevelJobRegistry.registerJob("harvestapiary", HarvestApiaryLevelJob.class, "husbandry");
        fillApiaryFrameID = LevelJobRegistry.registerJob("fillapiaryframe", FillApiaryFrameLevelJob.class, "husbandry");
        fishingPositionID = LevelJobRegistry.registerJob("fishingposition", FishingPositionLevelJob.class, "fishing");
        harvestFruitID = LevelJobRegistry.registerJob("harvestfruit", HarvestFruitLevelJob.class, "farming");
        harvestCropID = LevelJobRegistry.registerJob("harvestcrop", HarvestCropLevelJob.class, "farming");
        plantCropID = LevelJobRegistry.registerJob("plantcrop", PlantCropLevelJob.class, "farming", 100);
        huntMobID = LevelJobRegistry.registerJob("huntmob", HuntMobLevelJob.class, "hunting");
    }

    @Override
    protected void onRegistryClose() {
    }

    public static int registerJob(String stringID, Class<? extends LevelJob> jobClass, String jobTypeStringID) {
        return LevelJobRegistry.registerJob(stringID, jobClass, jobTypeStringID, 0);
    }

    public static int registerJob(String stringID, Class<? extends LevelJob> jobClass, String jobTypeStringID, int sameTypePriority) {
        try {
            return instance.register(stringID, new RegistryItem(jobClass, jobTypeStringID, sameTypePriority));
        }
        catch (NoSuchMethodException e) {
            throw new RuntimeException(LevelJobRegistry.instance.objectCallName + " must have a constructor with LoadData parameter", e);
        }
    }

    public static LevelJob loadJob(String stringID, LoadData save) {
        RegistryItem element = (RegistryItem)instance.getElement(stringID);
        if (element == null) {
            throw new LoadDataException(LevelJobRegistry.instance.objectCallName + " not found with stringID \"" + stringID + "\"");
        }
        try {
            return (LevelJob)element.newInstance(save);
        }
        catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new LoadDataException("Could not load " + LevelJobRegistry.instance.objectCallName, e);
        }
    }

    public static String getJobStringID(int jobID) {
        return instance.getElementStringID(jobID);
    }

    public static int getJobID(String stringID) {
        return instance.getElementID(stringID);
    }

    public static int getJobID(Class<? extends LevelJob> clazz) {
        return instance.getElementID(clazz);
    }

    public static int getJobTypeID(Class<? extends LevelJob> clazz) {
        int id = instance.getElementID(clazz);
        if (id != -1) {
            return ((RegistryItem)LevelJobRegistry.instance.getElement((int)id)).jobTypeID;
        }
        return -1;
    }

    protected static class RegistryItem
    extends ClassIDDataContainer<LevelJob> {
        public final int jobTypeID;
        public final int sameTypePriority;

        public RegistryItem(Class<? extends LevelJob> jobClass, String jobTypeStringID, int sameTypePriority) throws NoSuchMethodException {
            super(jobClass, LoadData.class);
            this.jobTypeID = JobTypeRegistry.getJobTypeID(jobTypeStringID);
            if (this.jobTypeID == -1) {
                throw new IllegalArgumentException("Must first register job type with stringID \"" + jobTypeStringID + "\"");
            }
            this.sameTypePriority = sameTypePriority;
        }
    }
}

