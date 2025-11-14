/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.save;

import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.world.World;
import necesse.engine.world.WorldEntity;

public class WorldEntitySave {
    public static WorldEntity loadSave(LoadData save, boolean isSimple, World serverWorld) {
        try {
            WorldEntity world = WorldEntity.getPlainWorldEntity(serverWorld);
            world.applyLoadData(save, isSimple);
            return world;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static WorldEntity loadSave(LoadData save, World serverWorld) {
        return WorldEntitySave.loadSave(save, false, serverWorld);
    }

    public static SaveData getSave(WorldEntity worldEntity) {
        return worldEntity.getSave();
    }
}

