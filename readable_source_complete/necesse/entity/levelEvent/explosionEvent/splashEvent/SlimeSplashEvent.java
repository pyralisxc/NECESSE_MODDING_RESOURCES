/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.explosionEvent.splashEvent;

import java.awt.Color;
import java.awt.geom.Point2D;
import necesse.engine.registries.MobRegistry;
import necesse.entity.levelEvent.explosionEvent.splashEvent.SplashEvent;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.hostile.SwampSlimeMob;
import necesse.entity.mobs.itemAttacker.FollowPosition;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.ToolItemSummonedMob;
import necesse.gfx.ThemeColorRegistry;
import necesse.inventory.item.toolItem.summonToolItem.SummonToolItem;

public class SlimeSplashEvent
extends SplashEvent {
    public SlimeSplashEvent() {
        this(0.0f, 0.0f, 64, new GameDamage(0.0f), 0.0f, null);
    }

    public SlimeSplashEvent(float x, float y, int range, GameDamage damage, float toolTier, Mob owner) {
        super(x, y, range, damage, false, toolTier, owner);
    }

    @Override
    public void init() {
        super.init();
        if (this.isClient()) {
            return;
        }
        ItemAttackerMob attackerMob = (ItemAttackerMob)this.ownerMob;
        if (attackerMob != null) {
            for (int i = 0; i < 5; ++i) {
                ToolItemSummonedMob slime = (ToolItemSummonedMob)((Object)MobRegistry.getMob("playerpoisonslime", this.level));
                Mob castedMob = (Mob)((Object)slime);
                ((ItemAttackerMob)this.ownerMob).serverFollowersManager.addFollower("summonedmob", castedMob, FollowPosition.WALK_CLOSE, "summonedmob", 1.0f, p -> 5, null, false);
                Point2D.Float spawnPoint = SummonToolItem.findSpawnLocation(castedMob, castedMob.getLevel(), this.x, this.y);
                slime.updateDamage(this.damage);
                this.getLevel().entityManager.addMob(castedMob, spawnPoint.x, spawnPoint.y);
            }
        } else if (this.ownerMob.isHostile) {
            SwampSlimeMob swampSlimeMob = new SwampSlimeMob();
            this.getLevel().entityManager.addMob(swampSlimeMob, this.x, this.y);
        }
    }

    @Override
    protected Color getInnerSplashColor() {
        return ThemeColorRegistry.SLIME.getRandomColor();
    }

    @Override
    protected Color getOuterSplashColor() {
        return this.getInnerSplashColor().brighter().brighter().brighter();
    }
}

