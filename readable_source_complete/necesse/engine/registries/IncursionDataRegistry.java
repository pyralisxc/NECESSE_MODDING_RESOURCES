/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.registries;

import java.lang.reflect.InvocationTargetException;
import necesse.engine.GameLoadingScreen;
import necesse.engine.localization.Localization;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.registries.ClassIDDataContainer;
import necesse.engine.registries.ClassedGameRegistry;
import necesse.level.maps.incursion.BiomeAscendedIncursionData;
import necesse.level.maps.incursion.BiomeExtractionIncursionData;
import necesse.level.maps.incursion.BiomeHuntIncursionData;
import necesse.level.maps.incursion.BiomeTrialIncursionData;
import necesse.level.maps.incursion.IncursionData;

public class IncursionDataRegistry
extends ClassedGameRegistry<IncursionData, IncursionDataRegistryElement> {
    public static final IncursionDataRegistry instance = new IncursionDataRegistry();

    private IncursionDataRegistry() {
        super("IncursionData", 250);
    }

    @Override
    public void registerCore() {
        GameLoadingScreen.drawLoadingString(Localization.translate("loading", "incursions"));
        IncursionDataRegistry.registerIncursionData("hunt", BiomeHuntIncursionData.class);
        IncursionDataRegistry.registerIncursionData("extraction", BiomeExtractionIncursionData.class);
        IncursionDataRegistry.registerIncursionData("trial", BiomeTrialIncursionData.class);
        IncursionDataRegistry.registerIncursionData("ascended", BiomeAscendedIncursionData.class);
    }

    @Override
    protected void onRegistryClose() {
    }

    public static int registerIncursionData(String stringID, Class<? extends IncursionData> pickupClass) {
        if (LoadedMod.isRunningModClientSide()) {
            throw new IllegalStateException("Client/server only mods cannot register pickups");
        }
        try {
            return instance.register(stringID, new IncursionDataRegistryElement(pickupClass));
        }
        catch (NoSuchMethodException e) {
            System.err.println("Could not register PickupEntity " + pickupClass.getSimpleName() + ": Missing constructor with no parameters");
            return -1;
        }
    }

    public static IncursionData getNewIncursionData(int id) {
        try {
            return (IncursionData)((IncursionDataRegistryElement)instance.getElement(id)).newInstance(new Object[0]);
        }
        catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static IncursionData getNewIncursionData(String stringID) {
        return IncursionDataRegistry.getNewIncursionData(IncursionDataRegistry.getIncursionDataID(stringID));
    }

    public static <C extends IncursionData> void applyIncursionDataIDData(C incursionData) {
        instance.applyIDData(incursionData.getClass(), incursionData.idData);
    }

    public static int getIncursionDataID(String stringID) {
        return instance.getElementID(stringID);
    }

    public static int getIncursionDataID(Class<? extends IncursionData> clazz) {
        return instance.getElementID(clazz);
    }

    public static String getIncursionDataStringID(int id) {
        return instance.getElementStringID(id);
    }

    protected static class IncursionDataRegistryElement
    extends ClassIDDataContainer<IncursionData> {
        public IncursionDataRegistryElement(Class<? extends IncursionData> incursionDataClass) throws NoSuchMethodException {
            super(incursionDataClass, new Class[0]);
        }
    }
}

