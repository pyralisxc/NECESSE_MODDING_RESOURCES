/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components.containerSlot;

import necesse.engine.localization.Localization;
import necesse.engine.network.client.Client;
import necesse.gfx.forms.components.containerSlot.FormContainerSlot;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.inventory.container.Container;

public class FormContainerEnchantSlot
extends FormContainerSlot {
    public FormContainerEnchantSlot(Client client, Container container, int containerSlotIndex, int x, int y) {
        super(client, container, containerSlotIndex, x, y);
        this.setDecal(this.getInterfaceStyle().inventoryslot_icon_trinket);
    }

    @Override
    public GameTooltips getClearTooltips() {
        return new StringTooltips(Localization.translate("itemtooltip", "enchantslot"));
    }
}

