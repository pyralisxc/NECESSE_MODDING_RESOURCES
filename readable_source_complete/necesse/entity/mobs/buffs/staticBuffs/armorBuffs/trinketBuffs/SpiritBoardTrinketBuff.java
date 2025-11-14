/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs;

import java.awt.Color;
import java.awt.geom.Point2D;
import necesse.engine.localization.Localization;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobWasHitEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffManager;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.TrinketBuff;
import necesse.entity.mobs.itemAttacker.FollowPosition;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.DryadSpiritFollowingMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.SpacerGameTooltip;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.toolItem.summonToolItem.SummonToolItem;
import necesse.inventory.item.trinketItem.TrinketItem;

public class SpiritBoardTrinketBuff
extends TrinketBuff {
    @Override
    public ListGameTooltips getTrinketTooltip(TrinketItem trinketItem, InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getTrinketTooltip(trinketItem, item, perspective);
        tooltips.add(Localization.translate("itemtooltip", "spiritboardtip"), 400);
        tooltips.add(new SpacerGameTooltip(5));
        tooltips.add(new StringTooltips(Localization.translate("itemtooltip", "dryadhauntweapontip", "value", (Object)1), new Color(30, 177, 143), 400));
        return tooltips;
    }

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
    }

    @Override
    public void onHasAttacked(ActiveBuff buff, MobWasHitEvent event) {
        super.onHasAttacked(buff, event);
        if (!event.wasPrevented) {
            BuffManager targetBM = event.target.buffManager;
            targetBM.addBuff(new ActiveBuff(BuffRegistry.Debuffs.DRYAD_HAUNTED, event.target, 10.0f, event.attacker), event.target.isServer());
            if (targetBM.getStacks(BuffRegistry.Debuffs.DRYAD_HAUNTED) >= 10) {
                targetBM.removeBuff(BuffRegistry.Debuffs.DRYAD_HAUNTED, true);
                SpiritBoardTrinketBuff.spawnDryadSpirit(event.attacker.getAttackOwner());
            }
        }
    }

    public static void spawnDryadSpirit(Mob owner) {
        if (owner != null && owner.isServer()) {
            int maxSummons = 5;
            DryadSpiritFollowingMob summonedMob = (DryadSpiritFollowingMob)MobRegistry.getMob("dryadspirit", owner.getLevel());
            ((ItemAttackerMob)owner).serverFollowersManager.addFollower("summonedmobtemp", (Mob)summonedMob, FollowPosition.FLYING_CIRCLE_FAST, "summonedmob", 1.0f, p -> maxSummons, null, false);
            Point2D.Float spawnPoint = SummonToolItem.findSpawnLocation(summonedMob, owner.getLevel(), owner.x, owner.y);
            owner.getLevel().entityManager.addMob(summonedMob, spawnPoint.x, spawnPoint.y);
        }
    }
}

