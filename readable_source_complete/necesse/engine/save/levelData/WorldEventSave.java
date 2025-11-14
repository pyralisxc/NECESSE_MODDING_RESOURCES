/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.save.levelData;

import necesse.engine.registries.WorldEventRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.world.worldEvent.WorldEvent;

public class WorldEventSave {
    public static WorldEvent loadSave(LoadData save) {
        try {
            String type = save.getUnsafeString("stringID");
            WorldEvent event = WorldEventRegistry.getEvent(type);
            event.applyLoadData(save);
            return event;
        }
        catch (Exception e) {
            String type = save.getUnsafeString("stringID", "N/A", false);
            System.err.println("Could not load world event with stringID " + type);
            e.printStackTrace();
            return null;
        }
    }

    public static SaveData getSave(WorldEvent event) {
        SaveData save = new SaveData("EVENT");
        save.addUnsafeString("stringID", event.getStringID());
        event.addSaveData(save);
        return save;
    }
}

