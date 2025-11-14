/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs;

import necesse.engine.localization.Localization;
import necesse.engine.network.Packet;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.SoundSettings;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffAbility;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.TrinketBuff;
import necesse.gfx.GameResources;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.trinketItem.TrinketItem;

public class FoolsGambitTrinketBuff
extends TrinketBuff
implements BuffAbility {
    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        buff.setModifier(BuffModifiers.ARMOR_FLAT, 10);
        buff.setModifier(BuffModifiers.ALL_DAMAGE, Float.valueOf(0.1f));
        buff.setModifier(BuffModifiers.CRIT_CHANCE, Float.valueOf(0.1f));
        buff.setModifier(BuffModifiers.MAX_SUMMONS, 1);
    }

    @Override
    public void runAbility(PlayerMob player, ActiveBuff buff, Packet content) {
        SoundManager.playSound(new SoundSettings(GameResources.jinglehit).volume(0.2f).basePitch(1.02f).pitchVariance(0.01f), player);
    }

    @Override
    public boolean canRunAbility(PlayerMob player, ActiveBuff buff, Packet content) {
        return true;
    }

    @Override
    public ListGameTooltips getTrinketTooltip(TrinketItem trinketItem, InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getTrinketTooltip(trinketItem, item, perspective);
        tooltips.add(Localization.translate("itemtooltip", "foolsgambittip"), 400);
        return tooltips;
    }
}

