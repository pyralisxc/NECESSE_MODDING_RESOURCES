/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.manager;

import necesse.engine.events.loot.TileLootTableDropsEvent;
import necesse.entity.manager.EntityComponent;

public interface TileLootTableDroppedListenerEntityComponent
extends EntityComponent {
    public void onTileLootTableDropped(TileLootTableDropsEvent var1);
}

