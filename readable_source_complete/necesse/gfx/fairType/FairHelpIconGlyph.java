/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.fairType;

import java.awt.Color;
import necesse.engine.Settings;
import necesse.engine.input.InputEvent;
import necesse.engine.window.GameWindow;
import necesse.gfx.Renderer;
import necesse.gfx.fairType.FairButtonGlyph;
import necesse.gfx.fairType.TypeParsers;
import necesse.gfx.forms.presets.HelpForms;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.ui.ButtonState;

public class FairHelpIconGlyph
extends FairButtonGlyph {
    private final int size;
    private final String helpKey;

    public FairHelpIconGlyph(int size, String helpKey) {
        super(size, size);
        this.size = size;
        this.helpKey = helpKey;
    }

    @Override
    public void handleEvent(float drawX, float drawY, InputEvent event) {
        if (event.getID() == -100) {
            if (event.state) {
                HelpForms.openHelpForm(this.helpKey, new Object[0]);
            }
            event.use();
        }
    }

    @Override
    public void draw(float x, float y, Color defaultColor) {
        GameTexture texture = Settings.UI.button_help_20.texture;
        Color color = (Color)Settings.UI.button_help_20.colorGetter.apply(this.isHovering() ? ButtonState.HIGHLIGHTED : ButtonState.ACTIVE);
        texture.initDraw().color(color).size(this.size, true).draw((int)x, (int)y - this.size);
        if (this.isHovering()) {
            Renderer.setCursor(GameWindow.CURSOR.INTERACT);
        }
    }

    @Override
    public String getParseString() {
        return TypeParsers.getHelpParseString(this.helpKey);
    }
}

