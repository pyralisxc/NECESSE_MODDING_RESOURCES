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
import necesse.entity.pickup.AscendedStarPickupEntity;
import necesse.entity.pickup.IncursionEmpowermentPickupEntity;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.entity.pickup.PickupEntity;
import necesse.entity.pickup.QuestItemPickupEntity;
import necesse.entity.pickup.StarBarrierPickupEntity;

public class PickupRegistry
extends ClassedGameRegistry<PickupEntity, PickupRegistryElement> {
    public static final PickupRegistry instance = new PickupRegistry();

    private PickupRegistry() {
        super("PickupEntity", 250);
    }

    @Override
    public void registerCore() {
        GameLoadingScreen.drawLoadingString(Localization.translate("loading", "pickups"));
        PickupRegistry.registerPickup("item", ItemPickupEntity.class);
        PickupRegistry.registerPickup("questitem", QuestItemPickupEntity.class);
        PickupRegistry.registerPickup("starbarrier", StarBarrierPickupEntity.class);
        PickupRegistry.registerPickup("ascendedstar", AscendedStarPickupEntity.class);
        PickupRegistry.registerPickup("incursionempowerment", IncursionEmpowermentPickupEntity.class);
    }

    @Override
    protected void onRegistryClose() {
    }

    public static int registerPickup(String stringID, Class<? extends PickupEntity> pickupClass) {
        if (LoadedMod.isRunningModClientSide()) {
            throw new IllegalStateException("Client/server only mods cannot register pickups");
        }
        try {
            return instance.register(stringID, new PickupRegistryElement(pickupClass));
        }
        catch (NoSuchMethodException e) {
            System.err.println("Could not register PickupEntity " + pickupClass.getSimpleName() + ": Missing constructor with no parameters");
            return -1;
        }
    }

    public static PickupEntity getPickup(int id) {
        try {
            return (PickupEntity)((PickupRegistryElement)instance.getElement(id)).newInstance(new Object[0]);
        }
        catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static PickupEntity getPickup(String stringID) {
        return PickupRegistry.getPickup(PickupRegistry.getPickupID(stringID));
    }

    public static int getPickupID(String stringID) {
        return instance.getElementID(stringID);
    }

    public static int getPickupID(Class<? extends PickupEntity> clazz) {
        return instance.getElementID(clazz);
    }

    public static String getPickupStringID(int id) {
        return instance.getElementStringID(id);
    }

    protected static class PickupRegistryElement
    extends ClassIDDataContainer<PickupEntity> {
        public PickupRegistryElement(Class<? extends PickupEntity> pickupClass) throws NoSuchMethodException {
            super(pickupClass, new Class[0]);
        }
    }
}

