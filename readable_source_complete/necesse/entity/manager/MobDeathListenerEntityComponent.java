/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.manager;

import java.util.HashSet;
import necesse.entity.manager.EntityComponent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;

public interface MobDeathListenerEntityComponent
extends EntityComponent {
    public void onLevelMobDied(Mob var1, Attacker var2, HashSet<Attacker> var3);
}

