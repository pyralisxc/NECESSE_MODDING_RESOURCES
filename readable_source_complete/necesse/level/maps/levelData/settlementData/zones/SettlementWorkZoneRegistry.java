/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData.settlementData.zones;

import java.lang.reflect.InvocationTargetException;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.registries.ClassIDDataContainer;
import necesse.engine.registries.ClassedGameRegistry;
import necesse.level.maps.levelData.settlementData.zones.SettlementFertilizeZone;
import necesse.level.maps.levelData.settlementData.zones.SettlementForestryZone;
import necesse.level.maps.levelData.settlementData.zones.SettlementHusbandryZone;
import necesse.level.maps.levelData.settlementData.zones.SettlementWorkZone;

public class SettlementWorkZoneRegistry
extends ClassedGameRegistry<SettlementWorkZone, ZoneRegistryElement> {
    public static int FORESTRY_ID;
    public static int HUSBANDRY_ID;
    public static int FERTILIZE_ID;
    public static final SettlementWorkZoneRegistry instance;

    public SettlementWorkZoneRegistry() {
        super("SettlementZone", Short.MAX_VALUE);
    }

    @Override
    public void registerCore() {
        FORESTRY_ID = SettlementWorkZoneRegistry.registerZone("forestry", SettlementForestryZone.class);
        HUSBANDRY_ID = SettlementWorkZoneRegistry.registerZone("husbandry", SettlementHusbandryZone.class);
        FERTILIZE_ID = SettlementWorkZoneRegistry.registerZone("fertilize", SettlementFertilizeZone.class);
    }

    @Override
    protected void onRegistryClose() {
    }

    public static int registerZone(String stringID, Class<? extends SettlementWorkZone> zoneClass) {
        if (LoadedMod.isRunningModClientSide()) {
            throw new IllegalStateException("Client/server only mods cannot register work zones");
        }
        try {
            return instance.register(stringID, new ZoneRegistryElement(zoneClass));
        }
        catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(zoneClass.getSimpleName() + " must have 3 different constructors: One with no parameters, one with LoadData parameter and one with PacketReader parameter.");
        }
    }

    public static int replaceZone(String stringID, Class<? extends SettlementWorkZone> zoneClass) {
        try {
            return instance.replace(stringID, new ZoneRegistryElement(zoneClass));
        }
        catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(zoneClass.getSimpleName() + " must have 3 different constructors: One with no parameters, one with LoadData parameter and one with PacketReader parameter.");
        }
    }

    public static int getZoneID(Class<? extends SettlementWorkZone> clazz) {
        return instance.getElementID(clazz);
    }

    public static int getZoneID(String stringID) {
        return instance.getElementID(stringID);
    }

    public static SettlementWorkZone getNewZone(int zoneID) {
        ZoneRegistryElement element = (ZoneRegistryElement)instance.getElement(zoneID);
        try {
            SettlementWorkZone zone = (SettlementWorkZone)element.newInstance(new Object[0]);
            zone.idData.setData(element.getID(), element.getStringID());
            return zone;
        }
        catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T extends SettlementWorkZone> T getNewZone(Class<T> zoneClass) {
        return (T)((SettlementWorkZone)zoneClass.cast(SettlementWorkZoneRegistry.getNewZone(SettlementWorkZoneRegistry.getZoneID(zoneClass))));
    }

    static {
        instance = new SettlementWorkZoneRegistry();
    }

    protected static class ZoneRegistryElement
    extends ClassIDDataContainer<SettlementWorkZone> {
        public ZoneRegistryElement(Class<? extends SettlementWorkZone> zoneClass) throws NoSuchMethodException {
            super(zoneClass, new Class[0]);
        }
    }
}

