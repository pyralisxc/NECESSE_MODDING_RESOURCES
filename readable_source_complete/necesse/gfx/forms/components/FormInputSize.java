/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components;

import java.awt.Color;
import java.awt.Rectangle;
import necesse.gfx.GameBackground;
import necesse.gfx.Renderer;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.ui.ButtonColor;
import necesse.gfx.ui.ButtonState;
import necesse.gfx.ui.GameInterfaceStyle;

public abstract class FormInputSize {
    public static FormInputSize SIZE_32_TO_40 = new FormInputSize(32, 4, 10, 2){

        @Override
        public DrawOptions getButtonDrawOptions(GameInterfaceStyle style, ButtonColor color, ButtonState state, int x, int y, int width, Color drawColor) {
            return () -> this.drawWidthComponent(style.button_32.getButtonTexture(color, state), x, y, width, drawColor);
        }

        @Override
        public DrawOptions getButtonDownDrawOptions(GameInterfaceStyle style, ButtonColor color, ButtonState state, int x, int y, int width, Color drawColor) {
            return () -> this.drawWidthComponent(style.button_32.getButtonDownTexture(color, state), x, y, width, drawColor);
        }

        @Override
        public DrawOptions getButtonEdgeDrawOptions(GameInterfaceStyle style, ButtonColor color, ButtonState state, int x, int y, int width, Color drawColor) {
            return () -> {};
        }

        @Override
        public DrawOptions getButtonDownEdgeDrawOptions(GameInterfaceStyle style, ButtonColor color, ButtonState state, int x, int y, int width, Color drawColor) {
            return () -> {};
        }

        @Override
        public DrawOptions getFormTabDrawOptions(GameInterfaceStyle style, ButtonColor color, ButtonState state, int x, int y, int width, Color drawColor) {
            return () -> this.drawTabWidthComponentFull(state.textureGetter.apply(style.formtab_32), x, y, width, drawColor);
        }

        @Override
        public DrawOptions getFormTabDownDrawOptions(GameInterfaceStyle style, ButtonColor color, ButtonState state, int x, int y, int width, Color drawColor) {
            return () -> this.drawTabWidthComponentFull(state.downTextureGetter.apply(style.formtab_32), x, y, width, drawColor);
        }

        @Override
        public DrawOptions getFormTabEdgeDrawOptions(GameInterfaceStyle style, ButtonColor color, ButtonState state, int x, int y, int width, Color drawColor) {
            return () -> this.drawTabWidthComponentEdgeFull(state.downTextureGetter.apply(style.formtab_32), state.textureGetter.apply(style.formtabedge_32), x, y, width, drawColor);
        }

        @Override
        public DrawOptions getFormTabDownEdgeDrawOptions(GameInterfaceStyle style, ButtonColor color, ButtonState state, int x, int y, int width, Color drawColor) {
            return () -> this.drawTabWidthComponentEdgeFull(state.downTextureGetter.apply(style.formtab_32), state.textureGetter.apply(style.formtabedge_32), x, y, width, drawColor);
        }

        @Override
        public Color getButtonColor(GameInterfaceStyle style, ButtonState state) {
            return style.button_32.getButtonColor(style, state);
        }

        @Override
        public Color getTextColor(GameInterfaceStyle style, ButtonState state) {
            return style.button_32.getTextColor(style, state);
        }

        @Override
        public DrawOptions getInputDrawOptions(GameInterfaceStyle style, int x, int y, int width) {
            return () -> this.drawWidthComponent(style.textinput_32, x, y, width, Color.WHITE);
        }

        @Override
        public FontOptions getFontOptions() {
            return new FontOptions(16);
        }

        @Override
        public Rectangle getContentRectangle(int width) {
            return new Rectangle(3, 7, width - 6, 26);
        }
    };
    public static FormInputSize SIZE_32 = new FormInputSize(32, 0, 6, 2){

        @Override
        public DrawOptions getButtonDrawOptions(GameInterfaceStyle style, ButtonColor color, ButtonState state, int x, int y, int width, Color drawColor) {
            return () -> this.drawWidthComponent(style.button_32.getButtonTexture(color, state), x, y, width, drawColor);
        }

        @Override
        public DrawOptions getButtonDownDrawOptions(GameInterfaceStyle style, ButtonColor color, ButtonState state, int x, int y, int width, Color drawColor) {
            return () -> this.drawWidthComponent(style.button_32.getButtonDownTexture(color, state), x, y, width, drawColor);
        }

        @Override
        public DrawOptions getButtonEdgeDrawOptions(GameInterfaceStyle style, ButtonColor color, ButtonState state, int x, int y, int width, Color drawColor) {
            return () -> {};
        }

        @Override
        public DrawOptions getButtonDownEdgeDrawOptions(GameInterfaceStyle style, ButtonColor color, ButtonState state, int x, int y, int width, Color drawColor) {
            return () -> {};
        }

        @Override
        public DrawOptions getFormTabDrawOptions(GameInterfaceStyle style, ButtonColor color, ButtonState state, int x, int y, int width, Color drawColor) {
            return () -> this.drawTabWidthComponentFull(state.textureGetter.apply(style.formtab_32), x, y, width, drawColor);
        }

        @Override
        public DrawOptions getFormTabDownDrawOptions(GameInterfaceStyle style, ButtonColor color, ButtonState state, int x, int y, int width, Color drawColor) {
            return () -> this.drawTabWidthComponentFull(state.downTextureGetter.apply(style.formtab_32), x, y, width, drawColor);
        }

        @Override
        public DrawOptions getFormTabEdgeDrawOptions(GameInterfaceStyle style, ButtonColor color, ButtonState state, int x, int y, int width, Color drawColor) {
            return () -> this.drawTabWidthComponentEdgeFull(state.downTextureGetter.apply(style.formtab_32), state.textureGetter.apply(style.formtabedge_32), x, y, width, drawColor);
        }

        @Override
        public DrawOptions getFormTabDownEdgeDrawOptions(GameInterfaceStyle style, ButtonColor color, ButtonState state, int x, int y, int width, Color drawColor) {
            return () -> this.drawTabWidthComponentEdgeFull(state.downTextureGetter.apply(style.formtab_32), state.textureGetter.apply(style.formtabedge_32), x, y, width, drawColor);
        }

        @Override
        public Color getButtonColor(GameInterfaceStyle style, ButtonState state) {
            return style.button_32.getButtonColor(style, state);
        }

        @Override
        public Color getTextColor(GameInterfaceStyle style, ButtonState state) {
            return style.button_32.getTextColor(style, state);
        }

        @Override
        public DrawOptions getInputDrawOptions(GameInterfaceStyle style, int x, int y, int width) {
            return () -> this.drawWidthComponent(style.textinput_32, x, y, width, Color.WHITE);
        }

        @Override
        public FontOptions getFontOptions() {
            return new FontOptions(16);
        }

        @Override
        public Rectangle getContentRectangle(int width) {
            return new Rectangle(3, 3, width - 6, 26);
        }
    };
    public static FormInputSize SIZE_24 = new FormInputSize(24, 0, 4, 1){

        @Override
        public DrawOptions getButtonDrawOptions(GameInterfaceStyle style, ButtonColor color, ButtonState state, int x, int y, int width, Color drawColor) {
            return () -> this.drawWidthComponent(style.button_24.getButtonTexture(color, state), x, y, width, drawColor);
        }

        @Override
        public DrawOptions getButtonDownDrawOptions(GameInterfaceStyle style, ButtonColor color, ButtonState state, int x, int y, int width, Color drawColor) {
            return () -> this.drawWidthComponent(style.button_24.getButtonDownTexture(color, state), x, y, width, drawColor);
        }

        @Override
        public DrawOptions getButtonEdgeDrawOptions(GameInterfaceStyle style, ButtonColor color, ButtonState state, int x, int y, int width, Color drawColor) {
            return () -> {};
        }

        @Override
        public DrawOptions getButtonDownEdgeDrawOptions(GameInterfaceStyle style, ButtonColor color, ButtonState state, int x, int y, int width, Color drawColor) {
            return () -> {};
        }

        @Override
        public DrawOptions getFormTabDrawOptions(GameInterfaceStyle style, ButtonColor color, ButtonState state, int x, int y, int width, Color drawColor) {
            return () -> this.drawTabWidthComponentFull(state.textureGetter.apply(style.formtab_24), x, y, width, drawColor);
        }

        @Override
        public DrawOptions getFormTabDownDrawOptions(GameInterfaceStyle style, ButtonColor color, ButtonState state, int x, int y, int width, Color drawColor) {
            return () -> this.drawTabWidthComponentFull(state.downTextureGetter.apply(style.formtab_24), x, y, width, drawColor);
        }

        @Override
        public DrawOptions getFormTabEdgeDrawOptions(GameInterfaceStyle style, ButtonColor color, ButtonState state, int x, int y, int width, Color drawColor) {
            return () -> this.drawTabWidthComponentEdgeFull(state.downTextureGetter.apply(style.formtab_24), state.textureGetter.apply(style.formtabedge_24), x, y, width, drawColor);
        }

        @Override
        public DrawOptions getFormTabDownEdgeDrawOptions(GameInterfaceStyle style, ButtonColor color, ButtonState state, int x, int y, int width, Color drawColor) {
            return () -> this.drawTabWidthComponentEdgeFull(state.downTextureGetter.apply(style.formtab_24), state.textureGetter.apply(style.formtabedge_24), x, y, width, drawColor);
        }

        @Override
        public Color getButtonColor(GameInterfaceStyle style, ButtonState state) {
            return style.button_24.getButtonColor(style, state);
        }

        @Override
        public Color getTextColor(GameInterfaceStyle style, ButtonState state) {
            return style.button_24.getTextColor(style, state);
        }

        @Override
        public DrawOptions getInputDrawOptions(GameInterfaceStyle style, int x, int y, int width) {
            return () -> this.drawWidthComponent(style.textinput_24, x, y, width, Color.WHITE);
        }

        @Override
        public FontOptions getFontOptions() {
            return new FontOptions(16);
        }

        @Override
        public Rectangle getContentRectangle(int width) {
            return new Rectangle(2, 2, width - 4, 20);
        }
    };
    public static FormInputSize SIZE_20 = new FormInputSize(20, 0, 2, 1){

        @Override
        public DrawOptions getButtonDrawOptions(GameInterfaceStyle style, ButtonColor color, ButtonState state, int x, int y, int width, Color drawColor) {
            return () -> this.drawWidthComponent(style.button_20.getButtonTexture(color, state), x, y, width, drawColor);
        }

        @Override
        public DrawOptions getButtonDownDrawOptions(GameInterfaceStyle style, ButtonColor color, ButtonState state, int x, int y, int width, Color drawColor) {
            return () -> this.drawWidthComponent(style.button_20.getButtonDownTexture(color, state), x, y, width, drawColor);
        }

        @Override
        public DrawOptions getButtonEdgeDrawOptions(GameInterfaceStyle style, ButtonColor color, ButtonState state, int x, int y, int width, Color drawColor) {
            return () -> {};
        }

        @Override
        public DrawOptions getButtonDownEdgeDrawOptions(GameInterfaceStyle style, ButtonColor color, ButtonState state, int x, int y, int width, Color drawColor) {
            return () -> {};
        }

        @Override
        public DrawOptions getFormTabDrawOptions(GameInterfaceStyle style, ButtonColor color, ButtonState state, int x, int y, int width, Color drawColor) {
            return () -> this.drawTabWidthComponentFull(state.textureGetter.apply(style.formtab_20), x, y, width, drawColor);
        }

        @Override
        public DrawOptions getFormTabDownDrawOptions(GameInterfaceStyle style, ButtonColor color, ButtonState state, int x, int y, int width, Color drawColor) {
            return () -> this.drawTabWidthComponentFull(state.downTextureGetter.apply(style.formtab_20), x, y, width, drawColor);
        }

        @Override
        public DrawOptions getFormTabEdgeDrawOptions(GameInterfaceStyle style, ButtonColor color, ButtonState state, int x, int y, int width, Color drawColor) {
            return () -> this.drawTabWidthComponentEdgeFull(state.downTextureGetter.apply(style.formtab_20), state.textureGetter.apply(style.formtabedge_20), x, y, width, drawColor);
        }

        @Override
        public DrawOptions getFormTabDownEdgeDrawOptions(GameInterfaceStyle style, ButtonColor color, ButtonState state, int x, int y, int width, Color drawColor) {
            return () -> this.drawTabWidthComponentEdgeFull(state.downTextureGetter.apply(style.formtab_20), state.textureGetter.apply(style.formtabedge_20), x, y, width, drawColor);
        }

        @Override
        public Color getButtonColor(GameInterfaceStyle style, ButtonState state) {
            return style.button_20.getButtonColor(style, state);
        }

        @Override
        public Color getTextColor(GameInterfaceStyle style, ButtonState state) {
            return style.button_20.getTextColor(style, state);
        }

        @Override
        public DrawOptions getInputDrawOptions(GameInterfaceStyle style, int x, int y, int width) {
            return () -> this.drawWidthComponent(style.textinput_20, x, y, width, Color.WHITE);
        }

        @Override
        public FontOptions getFontOptions() {
            return new FontOptions(16);
        }

        @Override
        public Rectangle getContentRectangle(int width) {
            return new Rectangle(2, 2, width - 4, 16);
        }
    };
    public static FormInputSize SIZE_16 = new FormInputSize(16, 0, 2, 1){

        @Override
        public DrawOptions getButtonDrawOptions(GameInterfaceStyle style, ButtonColor color, ButtonState state, int x, int y, int width, Color drawColor) {
            return () -> this.drawWidthComponent(style.button_16.getButtonTexture(color, state), x, y, width, drawColor);
        }

        @Override
        public DrawOptions getButtonDownDrawOptions(GameInterfaceStyle style, ButtonColor color, ButtonState state, int x, int y, int width, Color drawColor) {
            return () -> this.drawWidthComponent(style.button_16.getButtonDownTexture(color, state), x, y, width, drawColor);
        }

        @Override
        public DrawOptions getButtonEdgeDrawOptions(GameInterfaceStyle style, ButtonColor color, ButtonState state, int x, int y, int width, Color drawColor) {
            return () -> {};
        }

        @Override
        public DrawOptions getButtonDownEdgeDrawOptions(GameInterfaceStyle style, ButtonColor color, ButtonState state, int x, int y, int width, Color drawColor) {
            return () -> {};
        }

        @Override
        public DrawOptions getFormTabDrawOptions(GameInterfaceStyle style, ButtonColor color, ButtonState state, int x, int y, int width, Color drawColor) {
            return () -> this.drawTabWidthComponentFull(state.textureGetter.apply(style.formtab_16), x, y, width, drawColor);
        }

        @Override
        public DrawOptions getFormTabDownDrawOptions(GameInterfaceStyle style, ButtonColor color, ButtonState state, int x, int y, int width, Color drawColor) {
            return () -> this.drawTabWidthComponentFull(state.downTextureGetter.apply(style.formtab_16), x, y, width, drawColor);
        }

        @Override
        public DrawOptions getFormTabEdgeDrawOptions(GameInterfaceStyle style, ButtonColor color, ButtonState state, int x, int y, int width, Color drawColor) {
            return () -> this.drawTabWidthComponentEdgeFull(state.downTextureGetter.apply(style.formtab_16), state.textureGetter.apply(style.formtabedge_16), x, y, width, drawColor);
        }

        @Override
        public DrawOptions getFormTabDownEdgeDrawOptions(GameInterfaceStyle style, ButtonColor color, ButtonState state, int x, int y, int width, Color drawColor) {
            return () -> this.drawTabWidthComponentEdgeFull(state.textureGetter.apply(style.formtab_16), state.textureGetter.apply(style.formtabedge_16), x, y, width, drawColor);
        }

        @Override
        public Color getButtonColor(GameInterfaceStyle style, ButtonState state) {
            return style.button_16.getButtonColor(style, state);
        }

        @Override
        public Color getTextColor(GameInterfaceStyle style, ButtonState state) {
            return style.button_16.getTextColor(style, state);
        }

        @Override
        public DrawOptions getInputDrawOptions(GameInterfaceStyle style, int x, int y, int width) {
            return () -> this.drawWidthComponent(style.textinput_16, x, y, width, Color.WHITE);
        }

        @Override
        public FontOptions getFontOptions() {
            return new FontOptions(12);
        }

        @Override
        public Rectangle getContentRectangle(int width) {
            return new Rectangle(2, 2, width - 4, 12);
        }
    };
    public final int height;
    public final int textureDrawOffset;
    public final int fontDrawOffset;
    public final int buttonDownContentDrawOffset;

    public static FormInputSize background(int height, GameBackground background, int fontSize) {
        return FormInputSize.background(height, background, new FontOptions(fontSize), height / 2 - fontSize / 2);
    }

    public static FormInputSize background(int height, final GameBackground background, final FontOptions fontOptions, int fontDrawOffset) {
        return new FormInputSize(height, 0, fontDrawOffset, 0){

            @Override
            public DrawOptions getButtonDrawOptions(GameInterfaceStyle style, ButtonColor color, ButtonState state, int x, int y, int width, Color drawColor) {
                SharedTextureDrawOptions drawOptions = background.getDrawOptions(x, y, width, this.height);
                return drawOptions::draw;
            }

            @Override
            public DrawOptions getButtonDownDrawOptions(GameInterfaceStyle style, ButtonColor color, ButtonState state, int x, int y, int width, Color drawColor) {
                SharedTextureDrawOptions drawOptions = background.getDrawOptions(x, y, width, this.height);
                return drawOptions::draw;
            }

            @Override
            public DrawOptions getButtonEdgeDrawOptions(GameInterfaceStyle style, ButtonColor color, ButtonState state, int x, int y, int width, Color drawColor) {
                SharedTextureDrawOptions drawOptions = background.getEdgeDrawOptions(x, y, width, this.height);
                return drawOptions::draw;
            }

            @Override
            public DrawOptions getButtonDownEdgeDrawOptions(GameInterfaceStyle style, ButtonColor color, ButtonState state, int x, int y, int width, Color drawColor) {
                SharedTextureDrawOptions drawOptions = background.getEdgeDrawOptions(x, y, width, this.height);
                return drawOptions::draw;
            }

            @Override
            public DrawOptions getFormTabDrawOptions(GameInterfaceStyle style, ButtonColor color, ButtonState state, int x, int y, int width, Color drawColor) {
                SharedTextureDrawOptions drawOptions = background.getDrawOptions(x, y, width, this.height);
                return drawOptions::draw;
            }

            @Override
            public DrawOptions getFormTabDownDrawOptions(GameInterfaceStyle style, ButtonColor color, ButtonState state, int x, int y, int width, Color drawColor) {
                SharedTextureDrawOptions drawOptions = background.getDrawOptions(x, y, width, this.height);
                return drawOptions::draw;
            }

            @Override
            public DrawOptions getFormTabEdgeDrawOptions(GameInterfaceStyle style, ButtonColor color, ButtonState state, int x, int y, int width, Color drawColor) {
                SharedTextureDrawOptions drawOptions = background.getEdgeDrawOptions(x, y, width, this.height);
                return drawOptions::draw;
            }

            @Override
            public DrawOptions getFormTabDownEdgeDrawOptions(GameInterfaceStyle style, ButtonColor color, ButtonState state, int x, int y, int width, Color drawColor) {
                SharedTextureDrawOptions drawOptions = background.getDrawOptions(x, y, width, this.height);
                return drawOptions::draw;
            }

            @Override
            public Color getButtonColor(GameInterfaceStyle style, ButtonState state) {
                return Color.WHITE;
            }

            @Override
            public Color getTextColor(GameInterfaceStyle style, ButtonState state) {
                return Color.WHITE;
            }

            @Override
            public DrawOptions getInputDrawOptions(GameInterfaceStyle style, int x, int y, int width) {
                SharedTextureDrawOptions drawOptions = background.getDrawOptions(x, y, width, this.height);
                return drawOptions::draw;
            }

            @Override
            public FontOptions getFontOptions() {
                return fontOptions;
            }

            @Override
            public Rectangle getContentRectangle(int width) {
                int padding = background.getContentPadding();
                return new Rectangle(padding, padding, width - padding * 2, this.height - padding * 2);
            }
        };
    }

    public static FormInputSize empty(int height, int fontSize) {
        return FormInputSize.empty(height, new FontOptions(fontSize), height / 2 - fontSize / 2);
    }

    public static FormInputSize empty(int height, final FontOptions fontOptions, int fontDrawOffset) {
        return new FormInputSize(height, 0, fontDrawOffset, 0){

            @Override
            public DrawOptions getButtonDrawOptions(GameInterfaceStyle style, ButtonColor color, ButtonState state, int x, int y, int width, Color drawColor) {
                return () -> {};
            }

            @Override
            public DrawOptions getButtonDownDrawOptions(GameInterfaceStyle style, ButtonColor color, ButtonState state, int x, int y, int width, Color drawColor) {
                return () -> {};
            }

            @Override
            public DrawOptions getButtonEdgeDrawOptions(GameInterfaceStyle style, ButtonColor color, ButtonState state, int x, int y, int width, Color drawColor) {
                return () -> {};
            }

            @Override
            public DrawOptions getButtonDownEdgeDrawOptions(GameInterfaceStyle style, ButtonColor color, ButtonState state, int x, int y, int width, Color drawColor) {
                return () -> {};
            }

            @Override
            public DrawOptions getFormTabDrawOptions(GameInterfaceStyle style, ButtonColor color, ButtonState state, int x, int y, int width, Color drawColor) {
                return () -> {};
            }

            @Override
            public DrawOptions getFormTabDownDrawOptions(GameInterfaceStyle style, ButtonColor color, ButtonState state, int x, int y, int width, Color drawColor) {
                return () -> {};
            }

            @Override
            public DrawOptions getFormTabEdgeDrawOptions(GameInterfaceStyle style, ButtonColor color, ButtonState state, int x, int y, int width, Color drawColor) {
                return () -> {};
            }

            @Override
            public DrawOptions getFormTabDownEdgeDrawOptions(GameInterfaceStyle style, ButtonColor color, ButtonState state, int x, int y, int width, Color drawColor) {
                return () -> {};
            }

            @Override
            public Color getButtonColor(GameInterfaceStyle style, ButtonState state) {
                return Color.WHITE;
            }

            @Override
            public Color getTextColor(GameInterfaceStyle style, ButtonState state) {
                if (state == ButtonState.HIGHLIGHTED) {
                    return Color.WHITE;
                }
                return new Color(50, 50, 50);
            }

            @Override
            public DrawOptions getInputDrawOptions(GameInterfaceStyle style, int x, int y, int width) {
                return () -> {};
            }

            @Override
            public FontOptions getFontOptions() {
                return fontOptions;
            }

            @Override
            public Rectangle getContentRectangle(int width) {
                return new Rectangle(width, this.height);
            }
        };
    }

    public static FormInputSize line(int height, int fontSize) {
        return FormInputSize.line(height, new FontOptions(fontSize), height / 2 - fontSize / 2);
    }

    public static FormInputSize line(int height, final FontOptions fontOptions, int fontDrawOffset) {
        return new FormInputSize(height, 0, fontDrawOffset, 0){

            @Override
            public DrawOptions getButtonDrawOptions(GameInterfaceStyle style, ButtonColor color, ButtonState state, int x, int y, int width, Color drawColor) {
                return () -> {};
            }

            @Override
            public DrawOptions getButtonDownDrawOptions(GameInterfaceStyle style, ButtonColor color, ButtonState state, int x, int y, int width, Color drawColor) {
                return () -> {};
            }

            @Override
            public DrawOptions getButtonEdgeDrawOptions(GameInterfaceStyle style, ButtonColor color, ButtonState state, int x, int y, int width, Color drawColor) {
                Color textColor = style.activeTextColor;
                return () -> Renderer.drawRectangleLines(new Rectangle(x, y, width, this.height), (float)textColor.getRed() / 255.0f, (float)textColor.getGreen() / 255.0f, (float)textColor.getBlue() / 255.0f, 1.0f);
            }

            @Override
            public DrawOptions getButtonDownEdgeDrawOptions(GameInterfaceStyle style, ButtonColor color, ButtonState state, int x, int y, int width, Color drawColor) {
                Color textColor = style.activeTextColor;
                return () -> Renderer.drawRectangleLines(new Rectangle(x, y, width, this.height), (float)textColor.getRed() / 255.0f, (float)textColor.getGreen() / 255.0f, (float)textColor.getBlue() / 255.0f, 1.0f);
            }

            @Override
            public DrawOptions getFormTabDrawOptions(GameInterfaceStyle style, ButtonColor color, ButtonState state, int x, int y, int width, Color drawColor) {
                return () -> {};
            }

            @Override
            public DrawOptions getFormTabDownDrawOptions(GameInterfaceStyle style, ButtonColor color, ButtonState state, int x, int y, int width, Color drawColor) {
                return () -> {};
            }

            @Override
            public DrawOptions getFormTabEdgeDrawOptions(GameInterfaceStyle style, ButtonColor color, ButtonState state, int x, int y, int width, Color drawColor) {
                return () -> {};
            }

            @Override
            public DrawOptions getFormTabDownEdgeDrawOptions(GameInterfaceStyle style, ButtonColor color, ButtonState state, int x, int y, int width, Color drawColor) {
                return () -> {};
            }

            @Override
            public Color getButtonColor(GameInterfaceStyle style, ButtonState state) {
                return Color.WHITE;
            }

            @Override
            public Color getTextColor(GameInterfaceStyle style, ButtonState state) {
                if (state == ButtonState.HIGHLIGHTED) {
                    return Color.WHITE;
                }
                return new Color(50, 50, 50);
            }

            @Override
            public DrawOptions getInputDrawOptions(GameInterfaceStyle style, int x, int y, int width) {
                return () -> {};
            }

            @Override
            public FontOptions getFontOptions() {
                return fontOptions;
            }

            @Override
            public Rectangle getContentRectangle(int width) {
                return new Rectangle(width, this.height);
            }
        };
    }

    public FormInputSize(int height, int textureDrawOffset, int fontDrawOffset, int buttonDownContentDrawOffset) {
        this.height = height;
        this.textureDrawOffset = textureDrawOffset;
        this.fontDrawOffset = fontDrawOffset;
        this.buttonDownContentDrawOffset = buttonDownContentDrawOffset;
    }

    public FormInputSize(FormInputSize copy) {
        this.height = copy.height;
        this.textureDrawOffset = copy.textureDrawOffset;
        this.fontDrawOffset = copy.fontDrawOffset;
        this.buttonDownContentDrawOffset = copy.buttonDownContentDrawOffset;
    }

    public abstract DrawOptions getButtonDrawOptions(GameInterfaceStyle var1, ButtonColor var2, ButtonState var3, int var4, int var5, int var6, Color var7);

    public abstract DrawOptions getButtonDownDrawOptions(GameInterfaceStyle var1, ButtonColor var2, ButtonState var3, int var4, int var5, int var6, Color var7);

    public abstract DrawOptions getButtonEdgeDrawOptions(GameInterfaceStyle var1, ButtonColor var2, ButtonState var3, int var4, int var5, int var6, Color var7);

    public abstract DrawOptions getButtonDownEdgeDrawOptions(GameInterfaceStyle var1, ButtonColor var2, ButtonState var3, int var4, int var5, int var6, Color var7);

    public abstract DrawOptions getFormTabDrawOptions(GameInterfaceStyle var1, ButtonColor var2, ButtonState var3, int var4, int var5, int var6, Color var7);

    public abstract DrawOptions getFormTabDownDrawOptions(GameInterfaceStyle var1, ButtonColor var2, ButtonState var3, int var4, int var5, int var6, Color var7);

    public abstract DrawOptions getFormTabEdgeDrawOptions(GameInterfaceStyle var1, ButtonColor var2, ButtonState var3, int var4, int var5, int var6, Color var7);

    public abstract DrawOptions getFormTabDownEdgeDrawOptions(GameInterfaceStyle var1, ButtonColor var2, ButtonState var3, int var4, int var5, int var6, Color var7);

    public abstract Color getButtonColor(GameInterfaceStyle var1, ButtonState var2);

    public abstract Color getTextColor(GameInterfaceStyle var1, ButtonState var2);

    public abstract DrawOptions getInputDrawOptions(GameInterfaceStyle var1, int var2, int var3, int var4);

    public abstract FontOptions getFontOptions();

    public abstract Rectangle getContentRectangle(int var1);

    protected void drawWidthComponent(GameTexture texture, int x, int y, int width, Color drawColor) {
        FormComponent.drawWidthComponent(new GameSprite(texture, 0, 0, texture.getHeight()), new GameSprite(texture, 1, 0, texture.getHeight()), x, y + this.textureDrawOffset, width, drawColor);
    }

    protected void drawTabWidthComponentFull(GameTexture baseTexture, int x, int y, int width, Color drawColor) {
        FormComponent.drawWidthComponent(new GameSprite(baseTexture, 0, 0, baseTexture.getHeight()), new GameSprite(baseTexture, 1, 0, baseTexture.getHeight()), new GameSprite(baseTexture, 2, 0, baseTexture.getHeight()), x, y - baseTexture.getHeight() + this.textureDrawOffset, width, drawColor, false);
    }

    protected void drawTabWidthComponentEdgeFull(GameTexture baseTexture, GameTexture edgeTexture, int x, int y, int width, Color drawColor) {
        int edgeDeltaX = edgeTexture.getWidth() - baseTexture.getWidth();
        int edgeDeltaXSprite = edgeDeltaX / 3;
        FormComponent.drawWidthComponent(new GameSprite(edgeTexture, 0, 0, edgeTexture.getHeight()), new GameSprite(edgeTexture, 1, 0, edgeTexture.getHeight()), new GameSprite(edgeTexture, 2, 0, edgeTexture.getHeight()), x - edgeDeltaXSprite, y - edgeTexture.getHeight() + this.textureDrawOffset, width + edgeDeltaXSprite * 2, drawColor, false);
    }
}

