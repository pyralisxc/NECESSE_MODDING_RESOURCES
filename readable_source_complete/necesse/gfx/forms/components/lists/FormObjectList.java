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
import necesse.engine.registries.ObjectRegistry;
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
import necesse.level.gameObject.GameObject;

public abstract class FormObjectList
extends FormGeneralList<ObjectElement> {
    private String filter;
    private ArrayList<ObjectElement> allElements;

    public FormObjectList(int x, int y, int width, int height) {
        super(x, y, width, height, 20);
        this.setFilter("");
    }

    @Override
    public void reset() {
        super.reset();
        this.allElements = new ArrayList();
        this.setFilter(this.filter);
    }

    public void populateIfNotAlready() {
        if (this.allElements.isEmpty()) {
            for (GameObject o : ObjectRegistry.getObjects()) {
                this.allElements.add(new ObjectElement(o));
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
        for (ObjectElement o : this.allElements) {
            if (!o.object.getDisplayName().toLowerCase().contains(filter) && !o.object.getStringID().toLowerCase().contains(filter)) continue;
            this.elements.add(o);
        }
        this.limitMaxScroll();
    }

    public abstract void onClicked(GameObject var1);

    public class ObjectElement
    extends FormListElement<FormObjectList> {
        public GameObject object;

        public ObjectElement(GameObject tile) {
            this.object = tile;
        }

        @Override
        protected void draw(FormObjectList parent, TickManager tickManager, PlayerMob perspective, int elementIndex) {
            Color col = this.isHovering() ? FormObjectList.this.getInterfaceStyle().highlightTextColor : FormObjectList.this.getInterfaceStyle().activeTextColor;
            String desc = this.object.getID() + ":" + this.object.getDisplayName();
            FontOptions options = new FontOptions(16).color(col);
            String str = GameUtils.maxString(desc, options, parent.width - 20);
            FontManager.bit.drawString(10.0f, 2.0f, str, options);
            if (this.isMouseOver(parent) && !str.equals(desc)) {
                GameTooltipManager.addTooltip(new StringTooltips(desc), TooltipLocation.FORM_FOCUS);
            }
        }

        @Override
        protected void onClick(FormObjectList parent, int elementIndex, InputEvent event, PlayerMob perspective) {
            if (event.getID() != -100) {
                return;
            }
            FormObjectList.this.playTickSound();
            FormObjectList.this.onClicked(this.object);
        }

        @Override
        protected void onControllerEvent(FormObjectList parent, int elementIndex, ControllerEvent event, TickManager tickManager, PlayerMob perspective) {
            if (event.getState() != ControllerInput.MENU_SELECT) {
                return;
            }
            FormObjectList.this.playTickSound();
            FormObjectList.this.onClicked(this.object);
            event.use();
        }

        @Override
        public void drawControllerFocus(ControllerFocus current) {
            super.drawControllerFocus(current);
            GameTooltipManager.addControllerGlyph(Localization.translate("ui", "selectbutton"), ControllerInput.MENU_SELECT);
        }
    }
}

