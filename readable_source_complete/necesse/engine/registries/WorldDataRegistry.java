/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.registries;

import necesse.engine.GameLoadingScreen;
import necesse.engine.localization.Localization;
import necesse.engine.registries.ClassIDDataContainer;
import necesse.engine.registries.ClassedGameRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.world.WorldEntity;
import necesse.engine.world.worldData.SettlementsWorldData;
import necesse.engine.world.worldData.SettlersWorldData;
import necesse.engine.world.worldData.WorldData;

public class WorldDataRegistry
extends ClassedGameRegistry<WorldData, WorldDataRegistryElement> {
    public static final WorldDataRegistry instance = new WorldDataRegistry();

    private WorldDataRegistry() {
        super("WorldData", 32762);
    }

    @Override
    public void registerCore() {
        GameLoadingScreen.drawLoadingString(Localization.translate("loading", "worlddata"));
        WorldDataRegistry.registerWorldData("settlements", SettlementsWorldData.class);
        WorldDataRegistry.registerWorldData("settlers", SettlersWorldData.class);
    }

    @Override
    protected void onRegistryClose() {
    }

    public static int registerWorldData(String stringID, Class<? extends WorldData> worldDataClass) {
        try {
            return instance.register(stringID, new WorldDataRegistryElement(worldDataClass));
        }
        catch (NoSuchMethodException e) {
            System.err.println("Could not register WorldData " + worldDataClass.getSimpleName() + ": Missing constructor with no parameters");
            return -1;
        }
    }

    public static WorldData loadWorldData(WorldEntity worldEntity, LoadData save) {
        String stringID = save.hasLoadDataByName("stringID") ? save.getFirstDataByName("stringID") : null;
        try {
            WorldData data = (WorldData)((WorldDataRegistryElement)instance.getElement(stringID)).newInstance(new Object[0]);
            data.setWorldEntity(worldEntity);
            data.applyLoadData(save);
            return data;
        }
        catch (Exception e) {
            System.err.println("Could not instantiate world data with id " + stringID);
            e.printStackTrace();
            return null;
        }
    }

    protected static class WorldDataRegistryElement
    extends ClassIDDataContainer<WorldData> {
        public WorldDataRegistryElement(Class<? extends WorldData> worldDataClass) throws NoSuchMethodException {
            super(worldDataClass, new Class[0]);
        }
    }
}

