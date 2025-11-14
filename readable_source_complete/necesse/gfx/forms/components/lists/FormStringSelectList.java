/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components.lists;

import java.awt.Color;
import java.util.Arrays;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.InputEvent;
import necesse.engine.input.controller.ControllerEvent;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.localization.Localization;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.components.lists.FormSelectedElement;
import necesse.gfx.forms.components.lists.FormSelectedList;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.events.FormEventListener;
import necesse.gfx.forms.events.FormEventsHandler;
import necesse.gfx.forms.events.FormStringIndexEvent;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;

public class FormStringSelectList
extends FormSelectedList<StringElement> {
    private FormEventsHandler<FormStringIndexEvent<FormStringSelectList>> onSelect;
    private FormEventsHandler<FormStringIndexEvent<FormStringSelectList>> beforeSelect;

    public FormStringSelectList(int x, int y, int width, int height, String[] list) {
        super(x, y, width, height, 20);
        this.reset(list);
        this.onSelect = new FormEventsHandler();
        this.beforeSelect = new FormEventsHandler();
    }

    public FormStringSelectList beforeSelect(FormEventListener<FormStringIndexEvent<FormStringSelectList>> listener) {
        this.beforeSelect.addListener(listener);
        return this;
    }

    public FormStringSelectList onSelect(FormEventListener<FormStringIndexEvent<FormStringSelectList>> listener) {
        this.onSelect.addListener(listener);
        return this;
    }

    public void reset(String[] list) {
        super.reset();
        Arrays.stream(list).forEach(s -> this.elements.add(new StringElement((String)s)));
    }

    public String getSelected() {
        StringElement el = (StringElement)this.getSelectedElement();
        return el == null ? null : el.str;
    }

    public class StringElement
    extends FormSelectedElement<FormStringSelectList> {
        public String str;

        public StringElement(String str) {
            this.str = str;
        }

        @Override
        protected void draw(FormStringSelectList parent, TickManager tickManager, PlayerMob perspective, int elementIndex) {
            Color col = FormStringSelectList.this.getInterfaceStyle().activeTextColor;
            if (this.isSelected()) {
                col = FormStringSelectList.this.getInterfaceStyle().highlightTextColor;
            }
            FontOptions options = new FontOptions(16).color(col);
            String wStr = GameUtils.maxString(this.str, options, parent.width - 10);
            int width = FontManager.bit.getWidthCeil(wStr, options);
            FontManager.bit.drawString(parent.width / 2 - width / 2, 2.0f, wStr, options);
            if (this.isMouseOver(parent) && !this.str.equals(wStr)) {
                GameTooltipManager.addTooltip(new StringTooltips(this.str), TooltipLocation.FORM_FOCUS);
            }
        }

        @Override
        protected void onClick(FormStringSelectList parent, int elementIndex, InputEvent event, PlayerMob perspective) {
            if (event.getID() != -100) {
                return;
            }
            FormStringIndexEvent<FormStringSelectList> beforeSelect = new FormStringIndexEvent<FormStringSelectList>(parent, this.str, elementIndex);
            parent.beforeSelect.onEvent(beforeSelect);
            if (!beforeSelect.hasPreventedDefault()) {
                super.onClick(parent, elementIndex, event, perspective);
                FormStringIndexEvent<FormStringSelectList> onSelect = new FormStringIndexEvent<FormStringSelectList>(parent, this.str, elementIndex);
                parent.onSelect.onEvent(onSelect);
                if (!onSelect.hasPreventedDefault()) {
                    FormStringSelectList.this.playTickSound();
                }
            }
        }

        @Override
        protected void onControllerEvent(FormStringSelectList parent, int elementIndex, ControllerEvent event, TickManager tickManager, PlayerMob perspective) {
            if (event.getState() != ControllerInput.MENU_SELECT) {
                return;
            }
            FormStringIndexEvent<FormStringSelectList> beforeSelect = new FormStringIndexEvent<FormStringSelectList>(parent, this.str, elementIndex);
            parent.beforeSelect.onEvent(beforeSelect);
            if (!beforeSelect.hasPreventedDefault()) {
                super.onControllerEvent(parent, elementIndex, event, tickManager, perspective);
                FormStringIndexEvent<FormStringSelectList> onSelect = new FormStringIndexEvent<FormStringSelectList>(parent, this.str, elementIndex);
                parent.onSelect.onEvent(onSelect);
                if (!onSelect.hasPreventedDefault()) {
                    FormStringSelectList.this.playTickSound();
                }
            }
            event.use();
        }

        @Override
        public void drawControllerFocus(ControllerFocus current) {
            super.drawControllerFocus(current);
            GameTooltipManager.addControllerGlyph(Localization.translate("ui", "selectbutton"), ControllerInput.MENU_SELECT);
        }
    }
}

