/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs;

import java.awt.Point;
import necesse.engine.localization.Localization;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffManager;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.TrinketBuff;
import necesse.entity.mobs.itemAttacker.FollowPosition;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.trinketItem.TrinketItem;
import necesse.level.maps.Level;

public class WillOWispLanternBuff
extends TrinketBuff {
    @Override
    public ListGameTooltips getTrinketTooltip(TrinketItem trinketItem, InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getTrinketTooltip(trinketItem, item, perspective);
        tooltips.add(Localization.translate("itemtooltip", "willowisplanterntip"));
        return tooltips;
    }

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
    }

    @Override
    public void serverTick(ActiveBuff buff) {
        super.serverTick(buff);
        if (buff.owner.isItemAttacker) {
            ItemAttackerMob attackerMob = (ItemAttackerMob)buff.owner;
            float count = attackerMob.serverFollowersManager.getFollowerCount("willowisp");
            if (count <= 0.0f) {
                Level level = buff.owner.getLevel();
                Mob mob = MobRegistry.getMob("willowisp", level);
                attackerMob.serverFollowersManager.addFollower("willowisp", mob, FollowPosition.WALK_CLOSE, "summonedwillothewisp", 1.0f, 1, null, false);
                Point spawnPoint = new Point(attackerMob.getX() + GameRandom.globalRandom.getIntBetween(-5, 5), attackerMob.getY() + GameRandom.globalRandom.getIntBetween(-5, 5));
                level.entityManager.addMob(mob, spawnPoint.x, spawnPoint.y);
            }
        }
    }

    @Override
    public void onRemoved(ActiveBuff buff) {
        super.onRemoved(buff);
        BuffManager buffManager = buff.owner.buffManager;
        if (buff.owner.isServer() && buffManager.hasBuff("summonedwillothewisp")) {
            buffManager.removeBuff("summonedwillothewisp", true);
        }
    }
}

