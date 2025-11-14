/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.localization.Localization
 *  necesse.engine.registries.BuffRegistry
 *  necesse.engine.util.GameBlackboard
 *  necesse.entity.mobs.PlayerMob
 *  necesse.gfx.gameTexture.GameTexture
 *  necesse.gfx.gameTooltips.ListGameTooltips
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.item.Item
 *  necesse.inventory.item.Item$Rarity
 *  necesse.inventory.item.ItemCategory
 */
package aphorea.items.runes;

import aphorea.buffs.Runes.AphBaseRuneActiveBuff;
import aphorea.buffs.Runes.AphBaseRuneTrinketBuff;
import aphorea.items.runes.AphRunesInjector;
import aphorea.registry.AphItems;
import necesse.engine.localization.Localization;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;

public class AphBaseRune
extends Item {
    private final String buffID;
    private final int tooltipsNumber;
    private final String[] extraToolTips;

    public AphBaseRune(Item.Rarity rarity, String buffID, int tooltipsNumber, String ... extraToolTips) {
        super(1);
        this.buffID = buffID;
        this.tooltipsNumber = tooltipsNumber;
        this.rarity = rarity;
        this.extraToolTips = extraToolTips;
        this.setItemCategory(new String[]{"misc", "runes", "baserunes"});
        this.setItemCategory(ItemCategory.craftingManager, new String[]{"runes", "baserunes"});
    }

    public AphBaseRune(String buffID, int tooltipsNumber, String ... extraToolTips) {
        this(Item.Rarity.NORMAL, buffID, tooltipsNumber, extraToolTips);
    }

    public AphBaseRune(Item.Rarity rarity, int tooltipsNumber, String ... extraToolTips) {
        this(rarity, null, tooltipsNumber, extraToolTips);
    }

    public AphBaseRune(int tooltipsNumber, String ... extraToolTips) {
        this(Item.Rarity.NORMAL, null, tooltipsNumber, extraToolTips);
    }

    public AphBaseRune setInitialRune() {
        AphItems.initialRunes.add(this);
        return this;
    }

    public AphBaseRuneTrinketBuff getTrinketBuff() {
        return (AphBaseRuneTrinketBuff)BuffRegistry.getBuff((String)(this.buffID == null ? this.getStringID() : this.buffID));
    }

    public AphBaseRuneActiveBuff getActiveBuff() {
        return (AphBaseRuneActiveBuff)BuffRegistry.getBuff((String)this.getTrinketBuff().getBuff());
    }

    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate((String)"itemtooltip", (String)"baserune"));
        String runeOwner = item.getGndData().getString("runeOwner", null);
        if (runeOwner != null) {
            tooltips.add(Localization.translate((String)"itemtooltip", (String)"linkedrune", (String)"player", (String)runeOwner));
        }
        this.addToolTips(tooltips, item, null, perspective, false);
        tooltips.add(Localization.translate((String)"global", (String)"aphorea"));
        return tooltips;
    }

    public float getBrokerValue(InventoryItem item) {
        return item.getGndData().getString("runeOwner", null) == null ? super.getBrokerValue(item) : 0.0f;
    }

    public void addToolTips(ListGameTooltips tooltips, InventoryItem item, AphRunesInjector runesInjector, PlayerMob perspective, boolean isFinalBuff) {
        int cooldown;
        float healthCost;
        AphBaseRuneTrinketBuff buff = this.getTrinketBuff();
        float effectNumber = buff.getBaseEffectNumber() * (isFinalBuff ? buff.getEffectNumberVariation(item, runesInjector) : 1.0f);
        float f = healthCost = isFinalBuff ? buff.getFinalHealthCost(item, runesInjector) : buff.getBaseHealthCost();
        if (buff.isTemporary()) {
            AphBaseRuneActiveBuff activeBuff = this.getActiveBuff();
            cooldown = (int)((float)activeBuff.getBaseCooldownDuration() * (isFinalBuff ? AphBaseRuneTrinketBuff.getCooldownVariation(item, runesInjector) : 1.0f));
        } else {
            cooldown = (int)((float)this.getTrinketBuff().getBaseCooldown() * (isFinalBuff ? AphBaseRuneTrinketBuff.getCooldownVariation(item, runesInjector) : 1.0f));
        }
        float cooldownSeconds = (float)cooldown / 1000.0f;
        String baseRunePrefix = isFinalBuff ? "\u00a7i[B]\u00a70 " : "";
        for (int i = 0; i < this.tooltipsNumber; ++i) {
            String tooltipNumber = i == 0 ? "" : String.valueOf(i + 1);
            tooltips.add(baseRunePrefix + Localization.translate((String)"itemtooltip", (String)(this.getStringID() + tooltipNumber), (Object[])new Object[]{"effectNumber", Float.valueOf((float)Math.round(effectNumber * 100.0f) / 100.0f), "F0effectNumber", Float.valueOf(effectNumber)}));
        }
        for (String extraToolTip : this.extraToolTips) {
            tooltips.add(baseRunePrefix + Localization.translate((String)"itemtooltip", (String)extraToolTip));
        }
        float extraEffectNumberMod = buff.getExtraEffectNumberMod() - 1.0f;
        if (extraEffectNumberMod != 0.0f) {
            if (extraEffectNumberMod > 0.0f) {
                tooltips.add(baseRunePrefix + Localization.translate((String)"itemtooltip", (String)"moreextraeffectmod", (String)"mod", (Object)Float.valueOf(extraEffectNumberMod * 100.0f)));
            } else {
                tooltips.add(baseRunePrefix + Localization.translate((String)"itemtooltip", (String)"lessextraeffectmod", (String)"mod", (Object)Float.valueOf(Math.abs(extraEffectNumberMod) * 100.0f)));
            }
        }
        if (healthCost > 0.0f) {
            tooltips.add(Localization.translate((String)"itemtooltip", (String)"runehealthcost", (String)"health", (Object)Math.round(healthCost * 100.0f)));
        } else if (healthCost < 0.0f) {
            tooltips.add(Localization.translate((String)"itemtooltip", (String)"runehealthhealing", (String)"health", (Object)Math.round(-healthCost * 100.0f)));
        }
        if (isFinalBuff) {
            for (int i = 0; i < runesInjector.getTooltipsNumber(); ++i) {
                String tooltipNumber = i == 0 ? "" : String.valueOf(i + 1);
                tooltips.add(Localization.translate((String)"itemtooltip", (String)(runesInjector.getStringID() + "_mod" + tooltipNumber)));
            }
            runesInjector.getModifierRunes(item).forEach(b -> {
                for (int i = 0; i < b.getTooltipsNumber(); ++i) {
                    String tooltipNumber = i == 0 ? "" : String.valueOf(i + 1);
                    tooltips.add("\u00a7a[M]\u00a70 " + Localization.translate((String)"itemtooltip", (String)(b.getStringID() + tooltipNumber)));
                }
                for (String extraToolTip : b.getExtraToolTips()) {
                    tooltips.add("\u00a7a[M]\u00a70 " + Localization.translate((String)"itemtooltip", (String)extraToolTip));
                }
            });
        }
        tooltips.add(Localization.translate((String)"itemtooltip", (String)"runecooldown", (String)"seconds", (Object)Float.valueOf(cooldownSeconds)));
    }

    protected void loadItemTextures() {
        this.itemTexture = GameTexture.fromFile((String)("items/runes/" + this.getStringID()));
    }

    public String getTranslatedTypeName() {
        return Localization.translate((String)"item", (String)"baserune");
    }
}

