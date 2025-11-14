/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.manager;

import necesse.entity.manager.EntityComponent;
import necesse.entity.mobs.Mob;

public interface MobSpawnedListenerEntityComponent
extends EntityComponent {
    public void onLevelMobSpawned(Mob var1);
}

