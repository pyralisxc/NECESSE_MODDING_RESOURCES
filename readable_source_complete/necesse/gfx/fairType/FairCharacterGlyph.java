/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.system.Platform
 */
package necesse.gfx.fairType;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.io.File;
import java.util.HashMap;
import java.util.function.Function;
import java.util.function.Supplier;
import necesse.engine.input.InputEvent;
import necesse.engine.localization.Localization;
import necesse.engine.util.FloatDimension;
import necesse.engine.util.GameUtils;
import necesse.engine.window.GameWindow;
import necesse.gfx.Renderer;
import necesse.gfx.fairType.FairGlyph;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import org.lwjgl.system.Platform;

public class FairCharacterGlyph
implements FairGlyph {
    private static final char[] INVISIBLE_CHARACTERS = new char[]{'\n', '\t', '\b', '\r'};
    private static final HashMap<Character, Function<FontOptions, FloatDimension>> SPECIAL_CHARACTERS = new HashMap();
    private final FontOptions fontOptions;
    private final char character;
    private final boolean invisibleChar;
    private FloatDimension dimension;
    private final Function<InputEvent, Boolean> onEvent;
    private final Supplier<GameTooltips> tooltipsSupplier;
    private boolean isHovering;
    public int drawYOffset;

    public FairCharacterGlyph(FontOptions fontOptions, char character, Function<InputEvent, Boolean> onEvent, Supplier<GameTooltips> tooltipsSupplier) {
        this.fontOptions = fontOptions;
        this.character = character;
        this.onEvent = onEvent;
        this.tooltipsSupplier = tooltipsSupplier;
        boolean isInvis = false;
        for (char invisibleCharacter : INVISIBLE_CHARACTERS) {
            if (character != invisibleCharacter) continue;
            isInvis = true;
            break;
        }
        this.invisibleChar = isInvis;
        this.updateDimensions();
    }

    public FairCharacterGlyph(FontOptions fontOptions, char character, Function<InputEvent, Boolean> onEvent) {
        this(fontOptions, character, onEvent, null);
    }

    public FairCharacterGlyph(FontOptions fontOptions, char character, Supplier<GameTooltips> tooltipsSupplier) {
        this(fontOptions, character, null, tooltipsSupplier);
    }

    public FairCharacterGlyph(FontOptions fontOptions, char character) {
        this(fontOptions, character, null, null);
    }

    @Override
    public FloatDimension getDimensions() {
        return this.dimension;
    }

    @Override
    public void updateDimensions() {
        Function<FontOptions, FloatDimension> special = SPECIAL_CHARACTERS.get(Character.valueOf(this.character));
        this.dimension = special == null ? new FloatDimension(this.invisibleChar ? 0.0f : FontManager.bit.getWidth(this.character, this.fontOptions), FontManager.bit.getHeight(this.character, this.fontOptions)) : special.apply(this.fontOptions);
    }

    @Override
    public void handleInputEvent(float drawX, float drawY, InputEvent event) {
        if (event.isMouseMoveEvent()) {
            Dimension dim = this.getDimensions().toInt();
            this.isHovering = new Rectangle((int)drawX, (int)drawY - dim.height + this.drawYOffset, dim.width, dim.height).contains(event.pos.hudX, event.pos.hudY);
        }
        if (this.onEvent != null && this.isHovering && this.onEvent.apply(event).booleanValue()) {
            event.use();
        }
    }

    @Override
    public void draw(float x, float y, Color defaultColor) {
        if (!this.invisibleChar) {
            this.fontOptions.defaultColor(defaultColor);
            FontManager.bit.drawStringNoShadow(x, y - this.dimension.height + (float)this.drawYOffset, String.valueOf(this.character), this.fontOptions);
        }
        if (this.isHovering) {
            GameTooltips tooltips;
            if (this.onEvent != null) {
                Renderer.setCursor(GameWindow.CURSOR.INTERACT);
            }
            if (this.tooltipsSupplier != null && (tooltips = this.tooltipsSupplier.get()) != null) {
                GameTooltipManager.addTooltip(tooltips, TooltipLocation.FORM_FOCUS);
            }
        }
    }

    @Override
    public void drawShadow(float x, float y) {
        if (!this.invisibleChar && this.fontOptions.hasShadow()) {
            FontManager.bit.drawStringShadow(x, y - this.dimension.height + (float)this.drawYOffset, String.valueOf(this.character), this.fontOptions);
        }
    }

    @Override
    public FairGlyph getTextBoxCharacter() {
        return new FairCharacterGlyph(new FontOptions(this.fontOptions).clearOutline(), this.character, this.onEvent, this.tooltipsSupplier);
    }

    @Override
    public boolean isWhiteSpaceGlyph() {
        return this.character == ' ' || this.isNewLineGlyph();
    }

    @Override
    public boolean isNewLineGlyph() {
        return this.character == '\n';
    }

    @Override
    public char getCharacter() {
        return this.character;
    }

    public String toString() {
        return String.valueOf(this.character);
    }

    public static FairCharacterGlyph[] fromString(String str, Function<Character, FairCharacterGlyph> constructor) {
        FairCharacterGlyph[] glyphs = new FairCharacterGlyph[str.length()];
        for (int i = 0; i < str.length(); ++i) {
            glyphs[i] = constructor.apply(Character.valueOf(str.charAt(i)));
        }
        return glyphs;
    }

    public static FairCharacterGlyph[] fromString(FontOptions fontOptions, String str, Function<InputEvent, Boolean> onEvent, Supplier<GameTooltips> tooltipsSupplier) {
        return FairCharacterGlyph.fromString(str, (Character c) -> new FairCharacterGlyph(fontOptions, c.charValue(), onEvent, tooltipsSupplier));
    }

    public static FairCharacterGlyph[] fromString(FontOptions fontOptions, String str) {
        return FairCharacterGlyph.fromString(fontOptions, str, null, null);
    }

    public static FairCharacterGlyph[] fromStringToOpenFile(FontOptions fontOptions, String str, File file) {
        return FairCharacterGlyph.fromString(fontOptions, str, e -> {
            if (e.getID() == -100) {
                if (!e.state) {
                    GameUtils.openExplorerAtFile(file);
                }
                return true;
            }
            return false;
        }, () -> {
            if (Platform.get() == Platform.WINDOWS) {
                return new StringTooltips(Localization.translate("misc", "shotexplore"));
            }
            return new StringTooltips(Localization.translate("misc", "shotopen"));
        });
    }

    @Override
    public boolean canBeParsed() {
        return this.onEvent == null && this.tooltipsSupplier == null;
    }

    static {
        SPECIAL_CHARACTERS.put(Character.valueOf('\t'), fontOptions -> new FloatDimension(FontManager.bit.getWidth(' ', (FontOptions)fontOptions) * 2.0f, FontManager.bit.getHeight(' ', (FontOptions)fontOptions)));
    }
}

