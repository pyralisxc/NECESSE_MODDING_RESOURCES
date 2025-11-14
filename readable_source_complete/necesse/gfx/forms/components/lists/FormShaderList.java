/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components.lists;

import java.awt.Color;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.InputEvent;
import necesse.engine.input.controller.ControllerEvent;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.localization.Localization;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameResources;
import necesse.gfx.forms.components.lists.FormSelectedElement;
import necesse.gfx.forms.components.lists.FormSelectedList;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.events.FormEvent;
import necesse.gfx.forms.events.FormEventListener;
import necesse.gfx.forms.events.FormEventsHandler;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.gfx.shader.GameShader;

public class FormShaderList
extends FormSelectedList<ShaderElement> {
    private FormEventsHandler<FormShaderSelectEvent> shaderSelect = new FormEventsHandler();

    public FormShaderList(int x, int y, int width, int height) {
        super(x, y, width, height, 20);
    }

    public FormShaderList onShaderSelect(FormEventListener<FormShaderSelectEvent> listener) {
        this.shaderSelect.addListener(listener);
        return this;
    }

    @Override
    public void reset() {
        super.reset();
        GameResources.getShaders().forEach((name, shader) -> this.elements.add(new ShaderElement((String)name, (GameShader)shader)));
    }

    public class ShaderElement
    extends FormSelectedElement<FormShaderList> {
        public String name;
        public GameShader shader;

        public ShaderElement(String name, GameShader shader) {
            this.name = name;
            this.shader = shader;
        }

        @Override
        protected void draw(FormShaderList parent, TickManager tickManager, PlayerMob perspective, int elementIndex) {
            Color col = this.isSelected() ? FormShaderList.this.getInterfaceStyle().highlightTextColor : FormShaderList.this.getInterfaceStyle().activeTextColor;
            String desc = this.name;
            FontOptions options = new FontOptions(16).color(col);
            String str = GameUtils.maxString(desc, options, parent.width - 20);
            FontManager.bit.drawString(10.0f, 2.0f, str, options);
            if (this.isMouseOver(parent) && !str.equals(desc)) {
                GameTooltipManager.addTooltip(new StringTooltips(desc), TooltipLocation.FORM_FOCUS);
            }
        }

        @Override
        protected void onClick(FormShaderList parent, int elementIndex, InputEvent event, PlayerMob perspective) {
            if (event.getID() != -100) {
                return;
            }
            super.onClick(parent, elementIndex, event, perspective);
            if (this.isSelected()) {
                FormShaderList.this.playTickSound();
                parent.shaderSelect.onEvent(new FormShaderSelectEvent(parent, elementIndex, this.name, this.shader));
            }
        }

        @Override
        protected void onControllerEvent(FormShaderList parent, int elementIndex, ControllerEvent event, TickManager tickManager, PlayerMob perspective) {
            if (event.getState() != ControllerInput.MENU_SELECT) {
                return;
            }
            super.onControllerEvent(parent, elementIndex, event, tickManager, perspective);
            if (this.isSelected()) {
                FormShaderList.this.playTickSound();
                parent.shaderSelect.onEvent(new FormShaderSelectEvent(parent, elementIndex, this.name, this.shader));
            }
            event.use();
        }

        @Override
        public void drawControllerFocus(ControllerFocus current) {
            super.drawControllerFocus(current);
            GameTooltipManager.addControllerGlyph(Localization.translate("ui", "selectbutton"), ControllerInput.MENU_SELECT);
        }
    }

    public class FormShaderSelectEvent
    extends FormEvent<FormShaderList> {
        public final int index;
        public final String name;
        public final GameShader shader;

        public FormShaderSelectEvent(FormShaderList from, int index, String name, GameShader shader) {
            super(from);
            this.index = index;
            this.name = name;
            this.shader = shader;
        }
    }
}

