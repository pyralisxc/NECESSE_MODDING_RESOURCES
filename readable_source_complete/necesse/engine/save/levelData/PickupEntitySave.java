/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.save.levelData;

import necesse.engine.registries.PickupRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.entity.pickup.PickupEntity;
import necesse.level.maps.Level;

public class PickupEntitySave {
    public static PickupEntity loadSave(LoadData save, Level level) {
        try {
            String type = save.getUnsafeString("stringID");
            PickupEntity pickup = PickupRegistry.getPickup(type);
            pickup.setLevel(level);
            pickup.applyLoadData(save);
            return pickup;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static SaveData getSave(String saveName, PickupEntity pickup) {
        SaveData save = new SaveData(saveName);
        pickup.addSaveData(save);
        save.addUnsafeString("stringID", pickup.getStringID());
        return save;
    }

    public static SaveData getSave(PickupEntity pickup) {
        return PickupEntitySave.getSave("PICKUP", pickup);
    }
}

