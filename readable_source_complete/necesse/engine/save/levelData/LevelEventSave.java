/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.save.levelData;

import necesse.engine.registries.LevelEventRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.entity.levelEvent.LevelEvent;
import necesse.level.maps.Level;

public class LevelEventSave {
    public static LevelEvent loadSaveData(LoadData save, Level level) {
        try {
            String type = save.getUnsafeString("stringID");
            LevelEvent event = LevelEventRegistry.getEvent(type);
            event.level = level;
            event.applyLoadData(save);
            return event;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static SaveData getSave(LevelEvent event) {
        SaveData save = new SaveData("EVENT");
        event.addSaveData(save);
        save.addUnsafeString("stringID", event.getStringID());
        return save;
    }
}

