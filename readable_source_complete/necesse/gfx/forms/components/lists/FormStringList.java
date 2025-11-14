/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components.lists;

import java.awt.Color;
import java.util.Objects;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.InputEvent;
import necesse.engine.input.controller.ControllerEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.components.lists.FormGeneralList;
import necesse.gfx.forms.components.lists.FormListElement;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;

public class FormStringList
extends FormGeneralList<StringElement> {
    public FormStringList(int x, int y, int width, int height) {
        super(x, y, width, height, 20);
    }

    public void addString(String str, Color color, StringTooltips tooltips) {
        this.elements.add(new StringElement(str, color, tooltips));
    }

    public void addString(String str, Color color, String ... tooltips) {
        this.addString(str, color, new StringTooltips(tooltips));
    }

    public void addString(String str, Color color) {
        this.addString(str, color, (StringTooltips)null);
    }

    public void addString(String str) {
        this.addString(str, this.getInterfaceStyle().activeTextColor);
    }

    public void addString(String str, StringTooltips tooltips) {
        this.addString(str, this.getInterfaceStyle().activeTextColor, tooltips);
    }

    public void addString(String str, String ... tooltips) {
        this.addString(str, new StringTooltips(tooltips));
    }

    public class StringElement
    extends FormListElement<FormStringList> {
        private final String str;
        private final FontOptions fontOptions;
        private final StringTooltips tooltips;

        public StringElement(String str, Color color, StringTooltips tooltips) {
            Objects.requireNonNull(str);
            Objects.requireNonNull(color);
            this.str = str;
            this.fontOptions = new FontOptions(16).color(color);
            this.tooltips = tooltips;
        }

        @Override
        protected void draw(FormStringList parent, TickManager tickManager, PlayerMob perspective, int elementIndex) {
            int strLength = FontManager.bit.getWidthCeil(this.str, this.fontOptions);
            StringTooltips tt = new StringTooltips();
            if (this.isMouseOver(parent)) {
                if (parent.width < strLength) {
                    tt.add(this.str);
                }
                if (this.tooltips != null) {
                    tt.addAll(this.tooltips);
                }
            }
            if (tt.getSize() != 0) {
                GameTooltipManager.addTooltip(tt, TooltipLocation.FORM_FOCUS);
            }
            FontManager.bit.drawString(0.0f, 2.0f, this.str, this.fontOptions);
        }

        @Override
        protected void onClick(FormStringList parent, int elementIndex, InputEvent event, PlayerMob perspective) {
        }

        @Override
        protected void onControllerEvent(FormStringList parent, int elementIndex, ControllerEvent event, TickManager tickManager, PlayerMob perspective) {
        }
    }
}

