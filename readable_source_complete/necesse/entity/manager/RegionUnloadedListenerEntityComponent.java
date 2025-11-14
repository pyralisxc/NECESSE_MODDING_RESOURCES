/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.manager;

import necesse.entity.manager.EntityComponent;
import necesse.level.maps.regionSystem.Region;

public interface RegionUnloadedListenerEntityComponent
extends EntityComponent {
    public void onRegionUnloaded(Region var1);
}

