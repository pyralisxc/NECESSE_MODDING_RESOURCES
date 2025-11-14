/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.save.levelData;

import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.level.maps.Level;

public class ObjectEntitySave {
    public static ObjectEntity loadSave(LoadData save, Level level) {
        try {
            int x = save.getInt("x");
            int y = save.getInt("y");
            level.regionManager.ensureTileIsLoaded(x, y);
            ObjectEntity out = level.getLevelObject(x, y).getNewObjectEntity();
            if (out == null) {
                System.err.println("Loaded object entity at " + x + ", " + y + " on level " + level.getIdentifier() + " was invalid.");
                return null;
            }
            if (!out.type.equals(save.getUnsafeString("stringID"))) {
                System.err.println("Loaded object entity type at " + x + ", " + y + " on level " + level.getIdentifier() + " was invalid.");
                return null;
            }
            out.applyLoadData(save);
            return out;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static SaveData getSave(ObjectEntity objectEntity) {
        SaveData save = new SaveData("OBJENT");
        objectEntity.addSaveData(save);
        save.addUnsafeString("stringID", objectEntity.type);
        save.addInt("x", objectEntity.tileX);
        save.addInt("y", objectEntity.tileY);
        return save;
    }
}

