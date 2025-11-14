/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.objectEntity;

import necesse.engine.registries.BuffRegistry;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.objectEntity.StandaloneBannerObjectEntity;
import necesse.level.maps.Level;

public class BannerOfPeaceObjectEntity
extends StandaloneBannerObjectEntity {
    public BannerOfPeaceObjectEntity(Level level, int x, int y) {
        super(level, "bannerofpeace", x, y);
    }

    @Override
    public int getBuffRange() {
        return 800;
    }

    @Override
    public void applyBuffs(Mob mob) {
        ActiveBuff ab = new ActiveBuff(BuffRegistry.Banners.PEACE, mob, 100, null);
        mob.buffManager.addBuff(ab, false);
    }
}

