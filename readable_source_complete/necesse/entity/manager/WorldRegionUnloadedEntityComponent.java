/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.manager;

import necesse.entity.manager.EntityComponent;
import necesse.level.maps.Level;
import necesse.level.maps.regionSystem.Region;

public interface WorldRegionUnloadedEntityComponent
extends EntityComponent {
    public void onLevelRegionUnloaded(Level var1, Region var2);
}

