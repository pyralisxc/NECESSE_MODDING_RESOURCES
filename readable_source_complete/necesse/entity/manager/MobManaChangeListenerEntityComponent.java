/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.manager;

import necesse.entity.manager.EntityComponent;
import necesse.entity.mobs.Mob;

public interface MobManaChangeListenerEntityComponent
extends EntityComponent {
    public void onLevelMobManaChanged(Mob var1, float var2, float var3);
}

