/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.Settings
 *  necesse.engine.gameLoop.tickManager.TickManager
 *  necesse.engine.input.InputEvent
 *  necesse.engine.input.controller.ControllerEvent
 *  necesse.engine.input.controller.ControllerInput
 *  necesse.engine.localization.message.GameMessage
 *  necesse.engine.localization.message.LocalMessage
 *  necesse.engine.network.client.Client
 *  necesse.entity.mobs.PlayerMob
 *  necesse.gfx.GameBackground
 *  necesse.gfx.forms.components.lists.FormGeneralGridList
 *  necesse.gfx.forms.components.lists.FormListGridElement
 *  necesse.gfx.gameTooltips.GameTooltipManager
 *  necesse.gfx.gameTooltips.GameTooltips
 *  necesse.gfx.gameTooltips.ListGameTooltips
 *  necesse.gfx.gameTooltips.TooltipLocation
 *  necesse.inventory.InventoryItem
 */
package aphorea.containers.initialrune;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;
import necesse.engine.Settings;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.InputEvent;
import necesse.engine.input.controller.ControllerEvent;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameBackground;
import necesse.gfx.forms.components.lists.FormGeneralGridList;
import necesse.gfx.forms.components.lists.FormListGridElement;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.inventory.InventoryItem;

public abstract class FormInitialRunesList
extends FormGeneralGridList<RuneGrid> {
    public FormInitialRunesList(int x, int y, int width, int height, Client client) {
        super(x, y, width, height, 40, 40);
    }

    public void setRunes(Collection<InventoryItem> runes) {
        this.elements = new ArrayList();
        if (runes != null) {
            this.elements.addAll(runes.stream().map(x$0 -> new RuneGrid((InventoryItem)x$0)).collect(Collectors.toList()));
        }
        this.limitMaxScroll();
    }

    public abstract void onRuneClicked(InventoryItem var1, InputEvent var2);

    public GameMessage getEmptyMessage() {
        return new LocalMessage("ui", "noworlds");
    }

    public class RuneGrid
    extends FormListGridElement<FormInitialRunesList> {
        public final InventoryItem rune;

        public RuneGrid(InventoryItem rune) {
            this.rune = rune;
        }

        protected void draw(FormInitialRunesList parent, TickManager tickManager, PlayerMob perspective, int elementIndex) {
            Color color = Settings.UI.activeElementColor;
            if (this.isMouseOver(parent)) {
                color = Settings.UI.highlightElementColor;
                ListGameTooltips tooltips = this.rune.item.getTooltips(this.rune, perspective, null);
                GameTooltipManager.addTooltip((GameTooltips)tooltips, (GameBackground)GameBackground.getItemTooltipBackground(), (TooltipLocation)TooltipLocation.FORM_FOCUS);
            }
            this.rune.drawIcon(perspective, 4, 4, 32, color);
        }

        protected void onClick(FormInitialRunesList formInitialRunesList, int i, InputEvent inputEvent, PlayerMob playerMob) {
            FormInitialRunesList.this.onRuneClicked(this.rune, inputEvent);
        }

        protected void onControllerEvent(FormInitialRunesList formInitialRunesList, int i, ControllerEvent controllerEvent, TickManager tickManager, PlayerMob playerMob) {
            if (controllerEvent.getState() == ControllerInput.MENU_SELECT) {
                FormInitialRunesList.this.onRuneClicked(this.rune, InputEvent.ControllerButtonEvent((ControllerEvent)controllerEvent, (TickManager)tickManager));
                controllerEvent.use();
            }
        }
    }
}

