/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.itemAttacker;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PathDoorOption;
import necesse.entity.mobs.ai.path.RegionPathfinding;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.level.maps.Level;

public class FollowerTargetCooldown {
    public final ItemAttackerMob owner;
    protected HashMap<Integer, Cooldown> cache = new HashMap();
    protected int minCachedTime = 1000;
    protected int maxCachedTime = 2000;

    public FollowerTargetCooldown(ItemAttackerMob owner) {
        this.owner = owner;
    }

    public boolean canMoveTo(Mob target, int tileX, int tileY) {
        Level level = this.owner.getLevel();
        int regionID = level.getRegionID(tileX, tileY);
        PathDoorOption doorOption = level.regionManager.BASIC_DOOR_OPTIONS;
        if (doorOption != null) {
            return doorOption.canMoveToTile(this.owner.getTileX(), this.owner.getTileY(), tileX, tileY, target.canBeTargetedFromAdjacentTiles());
        }
        Cooldown result = this.cache.compute(regionID, (p, last) -> {
            if (last == null || last.cooldown <= this.owner.getTime()) {
                return new Cooldown(RegionPathfinding.canMoveToTile(this.owner.getLevel(), this.owner.getTileX(), this.owner.getTileY(), tileX, tileY, doorOption, target.canBeTargetedFromAdjacentTiles()), this.owner.getTime() + (long)GameRandom.globalRandom.getIntBetween(this.minCachedTime, this.maxCachedTime));
            }
            return last;
        });
        return result.canMoveTo;
    }

    public boolean canMoveTo(Mob target) {
        return this.canMoveTo(target, target.getTileX(), target.getTileY());
    }

    public void cleanCache() {
        HashSet<Integer> removes = new HashSet<Integer>();
        for (Map.Entry<Integer, Cooldown> e : this.cache.entrySet()) {
            Cooldown value = e.getValue();
            if (value.cooldown > this.owner.getTime()) continue;
            removes.add(e.getKey());
        }
        for (Integer remove : removes) {
            this.cache.remove(remove);
        }
    }

    protected static class Cooldown {
        public boolean canMoveTo;
        public long cooldown;

        public Cooldown(boolean canMoveTo, long cooldown) {
            this.canMoveTo = canMoveTo;
            this.cooldown = cooldown;
        }
    }
}

