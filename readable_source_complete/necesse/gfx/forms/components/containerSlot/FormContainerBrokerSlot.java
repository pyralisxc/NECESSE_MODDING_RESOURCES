/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components.containerSlot;

import necesse.engine.localization.Localization;
import necesse.engine.network.client.Client;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.fairType.TypeParsers;
import necesse.gfx.forms.components.containerSlot.FormContainerSlot;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.SpacerGameTooltip;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.Container;

public class FormContainerBrokerSlot
extends FormContainerSlot {
    public FormContainerBrokerSlot(Client client, Container container, int containerSlotIndex, int x, int y) {
        super(client, container, containerSlotIndex, x, y);
    }

    @Override
    public GameTooltips getItemTooltip(InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = new ListGameTooltips(super.getItemTooltip(item, perspective));
        float value = GameMath.toDecimals(item.getBrokerValue(), 2);
        String valueStr = (float)((int)value) == value ? String.valueOf((int)value) : String.valueOf(value);
        tooltips.add(new SpacerGameTooltip(10));
        tooltips.add(new StringTooltips(Localization.translate("ui", "brokervalue", "value", valueStr + TypeParsers.getItemParseString(new InventoryItem("coin")))));
        return tooltips;
    }
}

