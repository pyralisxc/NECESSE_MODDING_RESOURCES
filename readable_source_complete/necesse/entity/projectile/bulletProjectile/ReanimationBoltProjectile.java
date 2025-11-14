/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.projectile.bulletProjectile;

import java.awt.geom.Point2D;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.WitchRobesSetBonusBuff;
import necesse.entity.mobs.itemAttacker.FollowPosition;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.AttackingFollowingMob;
import necesse.entity.projectile.bulletProjectile.NecroticBoltProjectile;
import necesse.inventory.item.toolItem.summonToolItem.SummonToolItem;
import necesse.level.maps.Level;

public class ReanimationBoltProjectile
extends NecroticBoltProjectile {
    public ReanimationBoltProjectile() {
    }

    public ReanimationBoltProjectile(Level level, Mob owner, float x, float y, float targetX, float targetY, float speed, int range, GameDamage damage) {
        super(level, owner, x, y, targetX, targetY, speed, range, damage);
    }

    @Override
    protected void spawnIfServer(Mob mob) {
        if (mob.removed()) {
            this.spawnSkeleton(this.getOwner(), mob);
        } else {
            WitchRobesSetBonusBuff.spawnCrawlingZombie(this.getOwner(), mob, this.getDamage().modDamage(0.3f));
        }
    }

    private void spawnSkeleton(Mob owner, Mob target) {
        int maxSummons = 3;
        GameDamage skeletonDamage = new GameDamage(DamageTypeRegistry.SUMMON, this.getDamage().damage);
        AttackingFollowingMob skeleton = (AttackingFollowingMob)MobRegistry.getMob("skeletonfollowing", owner.getLevel());
        ((ItemAttackerMob)owner).serverFollowersManager.addFollower("skeletonfollowing", (Mob)skeleton, FollowPosition.PYRAMID, "summonedmob", 1.0f, p -> maxSummons, null, false);
        Point2D.Float spawnPoint = SummonToolItem.findSpawnLocation(skeleton, owner.getLevel(), target.x, target.y);
        skeleton.updateDamage(skeletonDamage);
        owner.getLevel().entityManager.addMob(skeleton, spawnPoint.x, spawnPoint.y);
    }
}

