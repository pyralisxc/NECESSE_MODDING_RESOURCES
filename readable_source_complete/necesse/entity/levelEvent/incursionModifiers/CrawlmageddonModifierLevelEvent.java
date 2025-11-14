/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.incursionModifiers;

import necesse.engine.registries.MobRegistry;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.manager.OnMobAddedListenerEntityComponent;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.hostile.IncursionCrawlingZombieMob;

public class CrawlmageddonModifierLevelEvent
extends LevelEvent
implements OnMobAddedListenerEntityComponent {
    public CrawlmageddonModifierLevelEvent() {
        super(true);
        this.shouldSave = true;
    }

    @Override
    public boolean isNetworkImportant() {
        return true;
    }

    @Override
    public void onMobSpawned(Mob mob) {
        if (this.level.isServer() && mob.isHostile && !mob.isSummoned && !mob.isBoss() && !(mob instanceof IncursionCrawlingZombieMob)) {
            Mob incursionCrawlingZombieMob = MobRegistry.getMob("incursioncrawlingzombie", this.level);
            incursionCrawlingZombieMob.resetUniqueID();
            incursionCrawlingZombieMob.onSpawned(mob.getX(), mob.getY());
            this.level.entityManager.mobs.add(incursionCrawlingZombieMob);
        }
    }
}

