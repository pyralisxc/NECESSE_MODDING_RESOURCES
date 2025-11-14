/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.input.controller;

import java.awt.Color;
import java.util.HashSet;
import java.util.LinkedList;
import necesse.engine.input.Control;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.input.controller.ControllerInputState;
import necesse.gfx.Renderer;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTexture.GameTexture;

public class ControllerGlyphTip {
    public static FontOptions fontOptions = new FontOptions(20);
    public final String text;
    private LinkedList<ControllerInputState> states = new LinkedList();
    private HashSet<ControllerInputState> addedStates = new HashSet();
    private LinkedList<GameTexture> glyphs = new LinkedList();
    private int width;

    public static int getHeight() {
        return fontOptions.getSize() + 4;
    }

    public ControllerGlyphTip(String text, ControllerInputState ... states) {
        this.text = text;
        this.addGlyphs(states);
    }

    public boolean addGlyphs(ControllerInputState ... states) {
        boolean addedAny = false;
        for (ControllerInputState state : states) {
            if (this.addedStates.contains(state)) continue;
            this.addedStates.add(state);
            this.states.add(state);
            addedAny = true;
        }
        if (addedAny) {
            this.updateWidth();
        }
        return addedAny;
    }

    public void updateWidth() {
        this.glyphs.clear();
        this.width = FontManager.bit.getWidthCeil(this.text, fontOptions);
        for (ControllerInputState state : this.states) {
            GameTexture glyph = ControllerInput.getStateGlyph(state);
            if (glyph == null) continue;
            this.glyphs.add(glyph);
            TextureDrawOptionsEnd options = glyph.initDraw().size(fontOptions.getSize(), false);
            this.width += options.getWidth() + 4;
        }
        if (this.glyphs.isEmpty()) {
            this.width += Control.getControlIconWidth(fontOptions, null, null, "?", null);
        }
        this.width += 4;
    }

    public int getWidth() {
        return this.width;
    }

    public void draw(int x, int y) {
        Renderer.initQuadDraw(this.width, fontOptions.getSize() + 4).color(new Color(0, 0, 0, 100)).draw(x - 2, y - 2);
        if (this.glyphs.isEmpty()) {
            Control.DrawFlow drawControlLogic = Control.getDrawControlLogic(fontOptions, x, y - 2, null, null, "?", null);
            drawControlLogic.draw();
            x += drawControlLogic.width;
        } else {
            for (GameTexture glyph : this.glyphs) {
                TextureDrawOptionsEnd options = glyph.initDraw().size(fontOptions.getSize(), false);
                options.draw(x, y);
                x += options.getWidth() + 4;
            }
        }
        FontManager.bit.drawString(x, y, this.text, fontOptions);
    }
}

