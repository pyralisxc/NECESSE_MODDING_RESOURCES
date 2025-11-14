/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.manager;

import necesse.engine.events.loot.ObjectLootTableDropsEvent;
import necesse.entity.manager.EntityComponent;

public interface ObjectLootTableDroppedListenerEntityComponent
extends EntityComponent {
    public void onObjectLootTableDropped(ObjectLootTableDropsEvent var1);
}

