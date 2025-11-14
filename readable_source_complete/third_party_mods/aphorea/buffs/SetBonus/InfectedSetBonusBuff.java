/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.localization.Localization
 *  necesse.engine.registries.DamageTypeRegistry
 *  necesse.engine.registries.MobRegistry
 *  necesse.engine.util.GameBlackboard
 *  necesse.entity.mobs.GameDamage
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.mobs.buffs.ActiveBuff
 *  necesse.entity.mobs.buffs.BuffEventSubscriber
 *  necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.SetBonusBuff
 *  necesse.entity.mobs.itemAttacker.FollowPosition
 *  necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.AttackingFollowingMob
 *  necesse.gfx.gameTooltips.ListGameTooltips
 *  necesse.gfx.gameTooltips.StringTooltips
 *  necesse.level.maps.Level
 */
package aphorea.buffs.SetBonus;

import necesse.engine.localization.Localization;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.SetBonusBuff;
import necesse.entity.mobs.itemAttacker.FollowPosition;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.AttackingFollowingMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.level.maps.Level;

public class InfectedSetBonusBuff
extends SetBonusBuff {
    public static String mobId = "livingsapling";
    public static GameDamage damage = new GameDamage(DamageTypeRegistry.SUMMON, 8.0f);

    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
    }

    public ListGameTooltips getTooltip(ActiveBuff ab, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltip(ab, blackboard);
        tooltips.add((Object)new StringTooltips(Localization.translate((String)"itemtooltip", (String)"infectedsetbonus")));
        tooltips.add((Object)new StringTooltips(Localization.translate((String)"itemtooltip", (String)"livingsapling")));
        return tooltips;
    }

    public void serverTick(ActiveBuff buff) {
        super.serverTick(buff);
        if (buff.owner.isPlayer) {
            PlayerMob player = (PlayerMob)buff.owner;
            int summonMobs = 3 - (int)player.serverFollowersManager.getFollowerCount(buff.buff.getStringID());
            if (summonMobs > 0) {
                for (int i = 0; i < summonMobs; ++i) {
                    AttackingFollowingMob mob = (AttackingFollowingMob)MobRegistry.getMob((String)mobId, (Level)buff.owner.getLevel());
                    player.serverFollowersManager.addFollower(buff.buff.getStringID(), (Mob)mob, FollowPosition.WALK_CLOSE, buff.buff.getStringID(), 1.0f, 3, null, true);
                    mob.updateDamage(damage);
                    mob.getLevel().entityManager.addMob((Mob)mob, buff.owner.x, buff.owner.y);
                }
            }
        }
    }
}

