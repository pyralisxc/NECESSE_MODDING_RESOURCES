/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.localization.Localization
 *  necesse.engine.registries.DamageTypeRegistry
 *  necesse.engine.registries.MobRegistry
 *  necesse.entity.mobs.GameDamage
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.MobWasHitEvent
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.mobs.buffs.ActiveBuff
 *  necesse.entity.mobs.buffs.BuffEventSubscriber
 *  necesse.entity.mobs.buffs.BuffModifiers
 *  necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.TrinketBuff
 *  necesse.entity.mobs.itemAttacker.FollowPosition
 *  necesse.entity.mobs.itemAttacker.MobFollower
 *  necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.AttackingFollowingMob
 *  necesse.gfx.gameTooltips.ListGameTooltips
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.item.trinketItem.TrinketItem
 *  necesse.level.maps.Level
 */
package aphorea.buffs.Trinkets.Charm;

import aphorea.buffs.AdrenalineBuff;
import java.util.Objects;
import necesse.engine.localization.Localization;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobWasHitEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.TrinketBuff;
import necesse.entity.mobs.itemAttacker.FollowPosition;
import necesse.entity.mobs.itemAttacker.MobFollower;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.AttackingFollowingMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.trinketItem.TrinketItem;
import necesse.level.maps.Level;

public class BloomrushCharmBuff
extends TrinketBuff {
    public static String mobId = "livingsapling";
    public static GameDamage damage = new GameDamage(DamageTypeRegistry.SUMMON, 8.0f);

    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        buff.addModifier(BuffModifiers.STAMINA_CAPACITY, (Object)Float.valueOf(0.5f));
    }

    public ListGameTooltips getTrinketTooltip(TrinketItem trinketItem, InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getTrinketTooltip(trinketItem, item, perspective);
        tooltips.add(Localization.translate((String)"itemtooltip", (String)"zephyrcharmtip"));
        tooltips.add(Localization.translate((String)"itemtooltip", (String)"adrenalinecharm"));
        tooltips.add(Localization.translate((String)"itemtooltip", (String)"bloomrushcharm"));
        tooltips.add(Localization.translate((String)"itemtooltip", (String)"adrenaline"));
        tooltips.add(Localization.translate((String)"itemtooltip", (String)"livingsapling"));
        return tooltips;
    }

    public void onWasHit(ActiveBuff buff, MobWasHitEvent event) {
        super.onWasHit(buff, event);
        AdrenalineBuff.giveAdrenaline(buff.owner, 20000, true);
    }

    public void clientTick(ActiveBuff buff) {
        super.clientTick(buff);
        this.updateBuffs(buff, AdrenalineBuff.getAdrenalineLevel(buff.owner));
    }

    public void serverTick(ActiveBuff buff) {
        block2: {
            int summonMobs;
            PlayerMob player;
            block3: {
                super.serverTick(buff);
                int level = AdrenalineBuff.getAdrenalineLevel(buff.owner);
                this.updateBuffs(buff, level);
                if (!buff.owner.isPlayer) break block2;
                player = (PlayerMob)buff.owner;
                summonMobs = level - (int)player.serverFollowersManager.getFollowerCount(buff.buff.getStringID());
                if (summonMobs <= 0) break block3;
                for (int i = 0; i < summonMobs; ++i) {
                    AttackingFollowingMob mob = (AttackingFollowingMob)MobRegistry.getMob((String)mobId, (Level)buff.owner.getLevel());
                    player.serverFollowersManager.addFollower(buff.buff.getStringID(), (Mob)mob, FollowPosition.WALK_CLOSE, buff.buff.getStringID(), 1.0f, 5, null, true);
                    mob.updateDamage(damage);
                    mob.getLevel().entityManager.addMob((Mob)mob, buff.owner.x, buff.owner.y);
                }
                break block2;
            }
            if (summonMobs >= 0) break block2;
            MobFollower[] followers = (MobFollower[])player.serverFollowersManager.streamFollowers().filter(m -> Objects.equals(m.summonType, buff.buff.getStringID())).toArray(MobFollower[]::new);
            for (int i = 0; i < Math.abs(summonMobs); ++i) {
                player.serverFollowersManager.removeFollower(followers[i].mob, true, false);
            }
        }
    }

    public void updateBuffs(ActiveBuff buff, int level) {
        buff.setModifier(BuffModifiers.STAMINA_USAGE, (Object)Float.valueOf(-0.1f * (float)level));
        buff.setModifier(BuffModifiers.STAMINA_USAGE, (Object)Float.valueOf(-0.1f * (float)level));
    }
}

