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
import necesse.engine.world.worldEvent.AscendedFlashWorldEvent;
import necesse.engine.world.worldEvent.WorldEvent;

public class WorldEventRegistry
extends ClassedGameRegistry<WorldEvent, WorldEventRegistryElement> {
    public static final WorldEventRegistry instance = new WorldEventRegistry();

    private WorldEventRegistry() {
        super("WorldEvent", 32762);
    }

    @Override
    public void registerCore() {
        GameLoadingScreen.drawLoadingString(Localization.translate("loading", "worldevents"));
        WorldEventRegistry.registerEvent("ascendedflash", AscendedFlashWorldEvent.class);
    }

    @Override
    protected void onRegistryClose() {
    }

    public static int registerEvent(String stringID, Class<? extends WorldEvent> worldEvent) {
        if (LoadedMod.isRunningModClientSide()) {
            throw new IllegalStateException("Client/server only mods cannot register world events");
        }
        try {
            return instance.register(stringID, new WorldEventRegistryElement(worldEvent));
        }
        catch (NoSuchMethodException e) {
            System.err.println("Could not register WorldEvent " + worldEvent.getSimpleName() + ": Missing constructor with no parameters");
            return -1;
        }
    }

    public static WorldEvent getEvent(int id) {
        try {
            return (WorldEvent)((WorldEventRegistryElement)instance.getElement(id)).newInstance(new Object[0]);
        }
        catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static WorldEvent getEvent(String stringID) {
        return WorldEventRegistry.getEvent(WorldEventRegistry.getEventID(stringID));
    }

    public static int getEventID(String stringID) {
        return instance.getElementID(stringID);
    }

    public static int getEventID(Class<? extends WorldEvent> clazz) {
        return instance.getElementID(clazz);
    }

    public static String getEventStringID(int id) {
        return instance.getElementStringID(id);
    }

    protected static class WorldEventRegistryElement
    extends ClassIDDataContainer<WorldEvent> {
        public WorldEventRegistryElement(Class<? extends WorldEvent> worldEventClass) throws NoSuchMethodException {
            super(worldEventClass, new Class[0]);
        }
    }
}

