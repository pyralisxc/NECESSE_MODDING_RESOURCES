/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components.containerSlot;

import java.awt.Color;
import necesse.engine.localization.Localization;
import necesse.engine.network.client.Client;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameBackground;
import necesse.gfx.GameColor;
import necesse.gfx.forms.components.containerSlot.FormContainerSlot;
import necesse.gfx.forms.presets.containerComponent.object.ProcessingHelp;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.Container;

public class FormContainerProcessingRecipeSlot
extends FormContainerSlot {
    protected ProcessingHelp help;

    public FormContainerProcessingRecipeSlot(Client client, Container container, int containerSlotIndex, int x, int y, ProcessingHelp help) {
        super(client, container, containerSlotIndex, x, y);
        this.help = help;
    }

    @Override
    public void drawDecal(PlayerMob perspective) {
        InventoryItem ghostItem;
        InventoryItem item;
        super.drawDecal(perspective);
        if (this.help != null && (item = this.getContainerSlot().getItem()) == null && (ghostItem = this.help.getGhostItem(this.getContainerSlot().getInventorySlot())) != null) {
            ghostItem.drawIcon(perspective, this.getX() + 4, this.getY() + 4, 32, new Color(255, 255, 255, 60));
        }
    }

    @Override
    public void addItemTooltips(InventoryItem item, PlayerMob perspective) {
        if (this.help != null) {
            GameTooltips currentTooltip = this.help.getTooltip(this.getContainerSlot().getInventorySlot(), perspective);
            if (currentTooltip != null) {
                ListGameTooltips tooltips = new ListGameTooltips(currentTooltip);
                if (this.help.needsFuel()) {
                    tooltips.add(new StringTooltips(Localization.translate("ui", "needfuel"), GameColor.RED));
                }
                GameTooltipManager.addTooltip(tooltips, GameBackground.getItemTooltipBackground(), TooltipLocation.FORM_FOCUS);
            } else {
                super.addItemTooltips(item, perspective);
            }
        } else {
            super.addItemTooltips(item, perspective);
        }
    }

    @Override
    public void addClearTooltips(PlayerMob perspective) {
        GameTooltips currentTooltip;
        if (this.help != null && (currentTooltip = this.help.getTooltip(this.getContainerSlot().getInventorySlot(), perspective)) != null) {
            ListGameTooltips tooltips = new ListGameTooltips(currentTooltip);
            if (this.help.needsFuel()) {
                tooltips.add(new StringTooltips(Localization.translate("ui", "needfuel"), GameColor.RED));
            }
            GameTooltipManager.addTooltip(tooltips, GameBackground.getItemTooltipBackground(), TooltipLocation.FORM_FOCUS);
        }
        super.addClearTooltips(perspective);
    }
}

