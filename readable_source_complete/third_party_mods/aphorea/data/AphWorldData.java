/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.save.LoadData
 *  necesse.engine.save.SaveData
 *  necesse.engine.world.WorldEntity
 *  necesse.engine.world.worldData.WorldData
 */
package aphorea.data;

import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.world.WorldEntity;
import necesse.engine.world.worldData.WorldData;

public class AphWorldData
extends WorldData {
    public static final String DATA_KEY = "aphoreaworlddata";
    public boolean gelSlimesNulled;

    public AphWorldData getData(WorldEntity world) {
        WorldData customData = world.getWorldData(DATA_KEY);
        if (customData instanceof AphWorldData) {
            return (AphWorldData)customData;
        }
        AphWorldData newData = new AphWorldData();
        world.addWorldData(DATA_KEY, (WorldData)newData);
        return newData;
    }

    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        save.addBoolean("gelslimesnulled", this.gelSlimesNulled);
    }

    public void applyLoadData(LoadData save) {
        super.applyLoadData(save);
        this.gelSlimesNulled = save.getBoolean("gelslimesnulled", this.gelSlimesNulled);
    }
}

