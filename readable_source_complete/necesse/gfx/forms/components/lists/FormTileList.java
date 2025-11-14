/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components.lists;

import java.awt.Color;
import java.util.ArrayList;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.InputEvent;
import necesse.engine.input.controller.ControllerEvent;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.localization.Localization;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.components.lists.FormGeneralList;
import necesse.gfx.forms.components.lists.FormListElement;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.level.gameTile.GameTile;

public abstract class FormTileList
extends FormGeneralList<TileElement> {
    private String filter;
    private ArrayList<TileElement> allElements;

    public FormTileList(int x, int y, int width, int height) {
        super(x, y, width, height, 20);
        this.setFilter("");
    }

    @Override
    public void reset() {
        super.reset();
        this.allElements = new ArrayList();
    }

    public void populateIfNotAlready() {
        if (this.allElements.isEmpty()) {
            for (GameTile t : TileRegistry.getTiles()) {
                this.allElements.add(new TileElement(t));
            }
            this.setFilter(this.filter);
            this.resetScroll();
        }
    }

    public void setFilter(String filter) {
        if (filter == null) {
            return;
        }
        this.filter = filter;
        this.elements = new ArrayList();
        for (TileElement t : this.allElements) {
            if (!t.tile.getDisplayName().contains(filter) && !t.tile.getStringID().contains(filter)) continue;
            this.elements.add(t);
        }
        this.limitMaxScroll();
    }

    public abstract void onClicked(GameTile var1);

    public class TileElement
    extends FormListElement<FormTileList> {
        public GameTile tile;

        public TileElement(GameTile tile) {
            this.tile = tile;
        }

        @Override
        protected void draw(FormTileList parent, TickManager tickManager, PlayerMob perspective, int elementIndex) {
            Color col = this.isHovering() ? FormTileList.this.getInterfaceStyle().highlightTextColor : FormTileList.this.getInterfaceStyle().activeTextColor;
            String desc = this.tile.getID() + ":" + this.tile.getDisplayName();
            FontOptions options = new FontOptions(16).color(col);
            String str = GameUtils.maxString(desc, options, parent.width - 20);
            FontManager.bit.drawString(10.0f, 2.0f, str, options);
            if (this.isMouseOver(parent) && !str.equals(desc)) {
                GameTooltipManager.addTooltip(new StringTooltips(desc), TooltipLocation.FORM_FOCUS);
            }
        }

        @Override
        protected void onClick(FormTileList parent, int elementIndex, InputEvent event, PlayerMob perspective) {
            if (event.getID() != -100) {
                return;
            }
            FormTileList.this.playTickSound();
            FormTileList.this.onClicked(this.tile);
        }

        @Override
        protected void onControllerEvent(FormTileList parent, int elementIndex, ControllerEvent event, TickManager tickManager, PlayerMob perspective) {
            if (event.getState() != ControllerInput.MENU_SELECT) {
                return;
            }
            FormTileList.this.playTickSound();
            FormTileList.this.onClicked(this.tile);
            event.use();
        }

        @Override
        public void drawControllerFocus(ControllerFocus current) {
            super.drawControllerFocus(current);
            GameTooltipManager.addControllerGlyph(Localization.translate("ui", "selectbutton"), ControllerInput.MENU_SELECT);
        }
    }
}

