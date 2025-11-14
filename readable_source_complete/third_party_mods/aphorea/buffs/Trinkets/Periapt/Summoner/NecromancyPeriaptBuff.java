/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.localization.Localization
 *  necesse.engine.modifiers.ModifierContainer
 *  necesse.engine.registries.DamageTypeRegistry
 *  necesse.engine.registries.MobRegistry
 *  necesse.entity.mobs.GameDamage
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.MobWasHitEvent
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.mobs.buffs.ActiveBuff
 *  necesse.entity.mobs.buffs.BuffEventSubscriber
 *  necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.TrinketBuff
 *  necesse.entity.mobs.itemAttacker.FollowPosition
 *  necesse.entity.mobs.itemAttacker.MobFollower
 *  necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.AttackingFollowingMob
 *  necesse.gfx.gameTooltips.ListGameTooltips
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.item.ItemStatTip
 *  necesse.inventory.item.trinketItem.TrinketItem
 *  necesse.level.maps.Level
 */
package aphorea.buffs.Trinkets.Periapt.Summoner;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import necesse.engine.localization.Localization;
import necesse.engine.modifiers.ModifierContainer;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobWasHitEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.TrinketBuff;
import necesse.entity.mobs.itemAttacker.FollowPosition;
import necesse.entity.mobs.itemAttacker.MobFollower;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.AttackingFollowingMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.ItemStatTip;
import necesse.inventory.item.trinketItem.TrinketItem;
import necesse.level.maps.Level;

public class NecromancyPeriaptBuff
extends TrinketBuff {
    static String mobId = "undeadskeleton";
    static GameDamage damage = new GameDamage(DamageTypeRegistry.SUMMON, 14.0f);

    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
    }

    public void onHasAttacked(ActiveBuff buff, MobWasHitEvent event) {
        if (buff.owner.isServer() && (event.target.removed() || event.target.getHealth() <= 0) && event.target.isHostile) {
            PlayerMob player = (PlayerMob)buff.owner;
            float spawnX = player.x;
            float spawnY = player.y;
            List skeletonList = this.getUndeadSkeletons(player).collect(Collectors.toList());
            if (skeletonList.size() >= 3) {
                MobFollower firstSkeleton = (MobFollower)skeletonList.get(0);
                if (!firstSkeleton.mob.removed()) {
                    spawnX = firstSkeleton.mob.x;
                    spawnY = firstSkeleton.mob.y;
                    player.serverFollowersManager.removeFollower(firstSkeleton.mob, true, false);
                }
            }
            AttackingFollowingMob mob = (AttackingFollowingMob)MobRegistry.getMob((String)mobId, (Level)player.getLevel());
            player.serverFollowersManager.addFollower("necromancyperiapt", (Mob)mob, FollowPosition.PYRAMID, "necromancyperiapt", Float.MIN_VALUE, Integer.MAX_VALUE, (MserverClient, Mmob) -> ((AttackingFollowingMob)Mmob).updateDamage(damage), true);
            mob.getLevel().entityManager.addMob((Mob)mob, spawnX, spawnY);
        }
    }

    public Stream<MobFollower> getUndeadSkeletons(PlayerMob player) {
        return player.serverFollowersManager.streamFollowers().filter(m -> m.mob.getStringID().equals(mobId));
    }

    public void addStatTooltips(LinkedList<ItemStatTip> list, ActiveBuff currentValues, ActiveBuff lastValues) {
        super.addStatTooltips(list, currentValues, lastValues);
        currentValues.getModifierTooltipsBuilder(true, true).addLastValues((ModifierContainer)lastValues).buildToStatList(list);
    }

    public ListGameTooltips getTrinketTooltip(TrinketItem trinketItem, InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getTrinketTooltip(trinketItem, item, perspective);
        tooltips.add(Localization.translate((String)"itemtooltip", (String)"necromancyperiapt"));
        tooltips.add(Localization.translate((String)"itemtooltip", (String)"necromancyperiapt2"));
        return tooltips;
    }
}

