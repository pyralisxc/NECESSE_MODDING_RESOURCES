/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.registries;

import necesse.engine.GameLoadingScreen;
import necesse.engine.localization.Localization;
import necesse.engine.registries.ClassIDDataContainer;
import necesse.engine.registries.ClassedGameRegistry;
import necesse.engine.save.LoadData;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.CursedCroneArenasLevelData;
import necesse.level.maps.levelData.DeprecatedCursedCroneArenasLevelData;
import necesse.level.maps.levelData.DeprecatedNPCVillageLevelData;
import necesse.level.maps.levelData.LevelData;
import necesse.level.maps.levelData.OneWorldNPCVillageData;
import necesse.level.maps.levelData.OneWorldPirateVillageData;
import necesse.level.maps.levelData.PathBreakDownLevelData;
import necesse.level.maps.levelData.settlementData.DeprecatedJobsLevelData;
import necesse.level.maps.levelData.settlementData.DeprecatedSettlementLevelData;
import necesse.level.maps.levelData.villageShops.DeprecatedVillageShopsData;

public class LevelDataRegistry
extends ClassedGameRegistry<LevelData, LevelDataRegistryElement> {
    public static final LevelDataRegistry instance = new LevelDataRegistry();

    private LevelDataRegistry() {
        super("LevelData", 32762);
    }

    @Override
    public void registerCore() {
        GameLoadingScreen.drawLoadingString(Localization.translate("loading", "leveldata"));
        LevelDataRegistry.registerLevelData("settlement", DeprecatedSettlementLevelData.class);
        LevelDataRegistry.registerLevelData("jobs", DeprecatedJobsLevelData.class);
        LevelDataRegistry.registerLevelData("villageshops", DeprecatedVillageShopsData.class);
        LevelDataRegistry.registerLevelData("pathbreak", PathBreakDownLevelData.class);
        LevelDataRegistry.registerLevelData("npcvillagedata", DeprecatedNPCVillageLevelData.class);
        LevelDataRegistry.registerLevelData("oneworldnpcvillagedata", OneWorldNPCVillageData.class);
        LevelDataRegistry.registerLevelData("oneworldpiratevillagedata", OneWorldPirateVillageData.class);
        LevelDataRegistry.registerLevelData("cursedcronearenas", DeprecatedCursedCroneArenasLevelData.class);
        LevelDataRegistry.registerLevelData("oneworldcursedcronearenas", CursedCroneArenasLevelData.class);
    }

    @Override
    protected void onRegistryClose() {
    }

    public static int registerLevelData(String stringID, Class<? extends LevelData> levelDataClass) {
        try {
            return instance.register(stringID, new LevelDataRegistryElement(levelDataClass));
        }
        catch (NoSuchMethodException e) {
            System.err.println("Could not register LevelData " + levelDataClass.getSimpleName() + ": Missing constructor with no parameters");
            return -1;
        }
    }

    public static LevelData loadLevelData(Level level, LoadData save) {
        String stringID = save.hasLoadDataByName("stringID") ? save.getUnsafeString("stringID") : null;
        try {
            int elementID = instance.getElementID(stringID);
            if (elementID == -1) {
                System.err.println("Could not find LevelData with stringID: " + stringID);
                return null;
            }
            LevelData data = (LevelData)((LevelDataRegistryElement)instance.getElement(elementID)).newInstance(new Object[0]);
            data.setLevel(level);
            data.applyLoadData(save);
            return data;
        }
        catch (Exception e) {
            System.err.println("Could not instantiate level data with id " + stringID);
            e.printStackTrace();
            return null;
        }
    }

    protected static class LevelDataRegistryElement
    extends ClassIDDataContainer<LevelData> {
        public LevelDataRegistryElement(Class<? extends LevelData> levelDataClass) throws NoSuchMethodException {
            super(levelDataClass, new Class[0]);
        }
    }
}

