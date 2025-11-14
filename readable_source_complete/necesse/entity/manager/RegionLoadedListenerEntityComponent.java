/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.manager;

import necesse.entity.manager.EntityComponent;
import necesse.level.maps.regionSystem.Region;

public interface RegionLoadedListenerEntityComponent
extends EntityComponent {
    public void onRegionLoaded(Region var1);
}

