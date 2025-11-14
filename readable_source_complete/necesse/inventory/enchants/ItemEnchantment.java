/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.enchants;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.modifiers.Modifier;
import necesse.engine.modifiers.ModifierContainer;
import necesse.engine.modifiers.ModifierList;
import necesse.engine.registries.IDData;
import necesse.engine.registries.IDDataContainer;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.ListGameTooltips;

public class ItemEnchantment
extends ModifierContainer
implements IDDataContainer {
    public final IDData idData = new IDData();
    private GameMessage displayName;
    private float enchantCostMod;

    @Override
    public IDData getIDData() {
        return this.idData;
    }

    public ItemEnchantment(ModifierList list, int enchantCostModPercent) {
        super(list);
        this.enchantCostMod = (float)enchantCostModPercent / 100.0f;
    }

    public void onEnchantmentRegistryClosed() {
    }

    public GameMessage getLocalization() {
        if (this.displayName == null) {
            this.displayName = new LocalMessage("enchantment", this.getStringID());
        }
        return this.displayName;
    }

    public String getDisplayName() {
        return this.getLocalization().translate();
    }

    public float getEnchantCostMod() {
        return this.enchantCostMod + 1.0f;
    }

    public ItemEnchantment modEnchantment(int mod) {
        ItemEnchantment container = new ItemEnchantment(this.list, (int)(this.enchantCostMod * 100.0f * (float)mod));
        for (int i = 0; i < mod; ++i) {
            for (Modifier modifier : BuffModifiers.LIST) {
                container.addModifier(modifier, this.getModifier(modifier));
                container.addModifierLimits(modifier, this.getLimits(modifier));
            }
        }
        return container;
    }

    public GameTooltips getTooltips() {
        ListGameTooltips tooltips = new ListGameTooltips();
        this.getModifierTooltips().stream().map(mft -> mft.toTooltip(true)).forEach(tooltips::add);
        return tooltips;
    }
}

