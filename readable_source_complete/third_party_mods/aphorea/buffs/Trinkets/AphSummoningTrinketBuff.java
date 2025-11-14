/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.modifiers.ModifierContainer
 *  necesse.engine.registries.MobRegistry
 *  necesse.entity.mobs.GameDamage
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.mobs.buffs.ActiveBuff
 *  necesse.entity.mobs.buffs.BuffEventSubscriber
 *  necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.TrinketBuff
 *  necesse.entity.mobs.itemAttacker.FollowPosition
 *  necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.AttackingFollowingMob
 *  necesse.inventory.item.ItemStatTip
 *  necesse.level.maps.Level
 */
package aphorea.buffs.Trinkets;

import java.util.LinkedList;
import necesse.engine.modifiers.ModifierContainer;
import necesse.engine.registries.MobRegistry;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.TrinketBuff;
import necesse.entity.mobs.itemAttacker.FollowPosition;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.AttackingFollowingMob;
import necesse.inventory.item.ItemStatTip;
import necesse.level.maps.Level;

public abstract class AphSummoningTrinketBuff
extends TrinketBuff {
    public String buffId;
    public String mobId;
    public int mobQuantity;
    public GameDamage damage;

    public AphSummoningTrinketBuff(String buffId, String mobId, int mobQuantity, GameDamage damage) {
        this.buffId = buffId;
        this.mobId = mobId;
        this.mobQuantity = mobQuantity;
        this.damage = damage;
    }

    public void init(ActiveBuff activeBuff, BuffEventSubscriber buffEventSubscriber) {
    }

    public void serverTick(ActiveBuff buff) {
        super.serverTick(buff);
        if (buff.owner.isPlayer) {
            PlayerMob player = (PlayerMob)buff.owner;
            int summonMobs = this.mobQuantity - (int)player.serverFollowersManager.getFollowerCount(this.buffId);
            for (int i = 0; i < summonMobs; ++i) {
                AttackingFollowingMob mob = (AttackingFollowingMob)MobRegistry.getMob((String)this.mobId, (Level)buff.owner.getLevel());
                player.serverFollowersManager.addFollower(this.buffId, (Mob)mob, FollowPosition.WALK_CLOSE, this.buffId, 1.0f, this.mobQuantity, null, true);
                mob.updateDamage(this.damage);
                mob.getLevel().entityManager.addMob((Mob)mob, buff.owner.x, buff.owner.y);
            }
        }
    }

    public void addStatTooltips(LinkedList<ItemStatTip> list, ActiveBuff currentValues, ActiveBuff lastValues) {
        super.addStatTooltips(list, currentValues, lastValues);
        currentValues.getModifierTooltipsBuilder(true, true).addLastValues((ModifierContainer)lastValues).buildToStatList(list);
    }
}

