/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData;

import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.entity.manager.EntityComponent;
import necesse.level.maps.regionSystem.Region;

public interface RegionLevelDataComponent
extends EntityComponent {
    public void addRegionSaveData(Region var1, SaveData var2);

    public void loadRegionSaveData(Region var1, LoadData var2);

    public void onUnloadedRegion(Region var1);
}

