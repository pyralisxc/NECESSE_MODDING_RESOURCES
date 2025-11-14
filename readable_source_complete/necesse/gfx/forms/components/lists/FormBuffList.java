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
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.gfx.forms.components.lists.FormGeneralList;
import necesse.gfx.forms.components.lists.FormListElement;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;

public abstract class FormBuffList
extends FormGeneralList<BuffElement> {
    private String filter;
    private ArrayList<BuffElement> allElements;

    public FormBuffList(int x, int y, int width, int height) {
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
            for (Buff b : BuffRegistry.getBuffs()) {
                if (b == null || b.isPassive()) continue;
                this.allElements.add(new BuffElement(b));
            }
            this.setFilter(this.filter);
            this.resetScroll();
        }
    }

    public void setFilter(String filter) {
        if (filter == null) {
            return;
        }
        this.filter = filter.toLowerCase();
        this.elements = new ArrayList();
        this.allElements.stream().filter(e -> e.buff.getStringID().toLowerCase().contains(filter) || e.buff.getDisplayName().toLowerCase().contains(filter)).forEach(this.elements::add);
        this.limitMaxScroll();
    }

    public abstract void onClicked(Buff var1);

    public class BuffElement
    extends FormListElement<FormBuffList> {
        public Buff buff;

        public BuffElement(Buff buff) {
            this.buff = buff;
        }

        @Override
        protected void draw(FormBuffList parent, TickManager tickManager, PlayerMob perspective, int elementIndex) {
            Color col = this.isHovering() ? FormBuffList.this.getInterfaceStyle().highlightTextColor : FormBuffList.this.getInterfaceStyle().activeTextColor;
            String desc = this.buff.getID() + ":" + this.buff.getDisplayName();
            FontOptions options = new FontOptions(16).color(col);
            String str = GameUtils.maxString(desc, options, parent.width - 20);
            FontManager.bit.drawString(10.0f, 2.0f, str, options);
            if (this.isMouseOver(parent) && !str.equals(desc)) {
                GameTooltipManager.addTooltip(new StringTooltips(desc), TooltipLocation.FORM_FOCUS);
            }
        }

        @Override
        protected void onClick(FormBuffList parent, int elementIndex, InputEvent event, PlayerMob perspective) {
            if (event.getID() != -100) {
                return;
            }
            FormBuffList.this.playTickSound();
            FormBuffList.this.onClicked(this.buff);
        }

        @Override
        protected void onControllerEvent(FormBuffList parent, int elementIndex, ControllerEvent event, TickManager tickManager, PlayerMob perspective) {
            if (event.getState() != ControllerInput.MENU_SELECT) {
                return;
            }
            FormBuffList.this.playTickSound();
            FormBuffList.this.onClicked(this.buff);
        }

        @Override
        public void drawControllerFocus(ControllerFocus current) {
            super.drawControllerFocus(current);
            GameTooltipManager.addControllerGlyph(Localization.translate("ui", "selectbutton"), ControllerInput.MENU_SELECT);
        }
    }
}

