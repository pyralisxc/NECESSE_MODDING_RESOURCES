/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.manager;

import necesse.entity.manager.EntityComponent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;

public interface MobHealthChangeListenerEntityComponent
extends EntityComponent {
    public void onLevelMobHealthChanged(Mob var1, int var2, int var3, float var4, float var5, Attacker var6);
}

