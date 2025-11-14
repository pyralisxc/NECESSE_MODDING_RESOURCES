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

import aphorea.buffs.Runes.AphModifierRuneTrinketBuff;
import necesse.engine.localization.Localization;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;

public class AphModifierRune
extends Item {
    private final String buffID;
    private final int tooltipsNumber;
    private final String[] extraToolTips;

    public AphModifierRune(Item.Rarity rarity, String buffID, int tooltipsNumber, String ... extraToolTips) {
        super(1);
        this.buffID = buffID;
        this.tooltipsNumber = tooltipsNumber;
        this.rarity = rarity;
        this.extraToolTips = extraToolTips;
        this.setItemCategory(new String[]{"misc", "runes", "modifierrunes"});
        this.setItemCategory(ItemCategory.craftingManager, new String[]{"runes", "modifierrunes"});
    }

    public AphModifierRune(String buffID, int tooltipsNumber, String ... extraToolTips) {
        this(Item.Rarity.COMMON, buffID, tooltipsNumber, extraToolTips);
    }

    public AphModifierRune(Item.Rarity rarity, int tooltipsNumber, String ... extraToolTips) {
        this(rarity, null, tooltipsNumber, extraToolTips);
    }

    public AphModifierRune(int tooltipsNumber, String ... extraToolTips) {
        this(Item.Rarity.COMMON, null, tooltipsNumber, extraToolTips);
    }

    public AphModifierRuneTrinketBuff getBuff() {
        return (AphModifierRuneTrinketBuff)BuffRegistry.getBuff((String)(this.buffID == null ? this.getStringID() : this.buffID));
    }

    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate((String)"itemtooltip", (String)"modifierrune"));
        this.addToolTips(tooltips, item, perspective);
        tooltips.add(Localization.translate((String)"global", (String)"aphorea"));
        return tooltips;
    }

    public void addToolTips(ListGameTooltips tooltips, InventoryItem item, PlayerMob perspective) {
        AphModifierRuneTrinketBuff buff = this.getBuff();
        float effectNumberVariation = buff.getEffectNumberVariation();
        float effectCooldownVariation = buff.getCooldownVariation();
        float healthCost = buff.getHealthCost();
        if (effectNumberVariation > 0.0f) {
            tooltips.add(Localization.translate((String)"itemtooltip", (String)"increaseruneeffectnumber", (String)"variation", (Object)Math.round(effectNumberVariation * 100.0f)));
        } else if (effectNumberVariation < 0.0f) {
            tooltips.add(Localization.translate((String)"itemtooltip", (String)"decreaseruneeffectnumber", (String)"variation", (Object)Math.round(-effectNumberVariation * 100.0f)));
        }
        if (effectCooldownVariation > 0.0f) {
            tooltips.add(Localization.translate((String)"itemtooltip", (String)"increaserunecooldown", (String)"variation", (Object)Math.round(effectCooldownVariation * 100.0f)));
        } else if (effectCooldownVariation < 0.0f) {
            tooltips.add(Localization.translate((String)"itemtooltip", (String)"decreaserunecooldown", (String)"variation", (Object)Math.round(-effectCooldownVariation * 100.0f)));
        }
        if (healthCost > 0.0f) {
            tooltips.add(Localization.translate((String)"itemtooltip", (String)"increaserunehealthcost", (String)"health", (Object)Math.round(healthCost * 100.0f)));
        } else if (healthCost < 0.0f) {
            tooltips.add(Localization.translate((String)"itemtooltip", (String)"increaserunehealthhealing", (String)"health", (Object)Math.round(-healthCost * 100.0f)));
        }
        for (int i = 0; i < this.tooltipsNumber; ++i) {
            String tooltipNumber = i == 0 ? "" : String.valueOf(i + 1);
            tooltips.add(Localization.translate((String)"itemtooltip", (String)(this.getStringID() + tooltipNumber)));
        }
        for (String extraToolTip : this.getExtraToolTips()) {
            tooltips.add(Localization.translate((String)"itemtooltip", (String)extraToolTip));
        }
    }

    public int getTooltipsNumber() {
        return this.tooltipsNumber;
    }

    public String[] getExtraToolTips() {
        return this.extraToolTips;
    }

    protected void loadItemTextures() {
        this.itemTexture = GameTexture.fromFile((String)("items/runes/" + this.getStringID()));
    }

    public String getTranslatedTypeName() {
        return Localization.translate((String)"item", (String)"modifierrune");
    }
}

