/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.projectileToolItem.throwToolItem;

import necesse.engine.sound.SoundSettings;
import necesse.gfx.GameResources;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.toolItem.projectileToolItem.throwToolItem.ThrowToolItem;

public class SettlerIgnoredThrowToolItem
extends ThrowToolItem {
    public SettlerIgnoredThrowToolItem(int enchantCost) {
        super(enchantCost, null);
    }

    public SettlerIgnoredThrowToolItem() {
    }

    @Override
    public float getItemAttackerWeaponValueFlat(InventoryItem item) {
        return 0.0f;
    }

    @Override
    protected SoundSettings getSwingSound() {
        return new SoundSettings(GameResources.swing1).volume(0.8f).basePitch(1.1f).pitchVariance(0.1f);
    }
}

