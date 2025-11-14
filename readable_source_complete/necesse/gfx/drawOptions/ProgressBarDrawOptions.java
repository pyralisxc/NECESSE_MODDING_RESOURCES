/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.drawOptions;

import java.awt.Color;
import java.util.LinkedList;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.DrawOptionsList;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;

public class ProgressBarDrawOptions {
    protected final GameTexture background;
    protected final int width;
    protected LinkedList<ProgressBarOptions> bars = new LinkedList();
    protected Color backgroundColor = Color.WHITE;
    protected FontOptions fontOptions = new FontOptions(16);
    protected GameMessage text = null;
    protected int limitText = -1;
    protected int textAlign = 1;

    public ProgressBarDrawOptions(GameTexture background, int width) {
        this.background = background;
        this.width = width;
    }

    public ProgressBarDrawOptions color(Color color) {
        this.backgroundColor = color;
        return this;
    }

    public ProgressBarDrawOptions text(GameMessage text) {
        this.text = text;
        return this;
    }

    public ProgressBarDrawOptions text(String text) {
        return this.text(new StaticMessage(text));
    }

    public ProgressBarDrawOptions limitText(int padding) {
        this.limitText = padding;
        return this;
    }

    public ProgressBarDrawOptions textAlignLeft() {
        this.textAlign = 0;
        return this;
    }

    public ProgressBarDrawOptions textAlignCenter() {
        this.textAlign = 1;
        return this;
    }

    public ProgressBarDrawOptions textAlignRight() {
        this.textAlign = 2;
        return this;
    }

    public ProgressBarDrawOptions fontOptions(FontOptions fontOptions) {
        this.fontOptions = fontOptions;
        return this;
    }

    public ProgressBarOptions addBar(GameTexture texture, float percent) {
        ProgressBarOptions out = new ProgressBarOptions(texture, percent);
        this.bars.add(out);
        return out;
    }

    public DrawOptions pos(int x, int y) {
        DrawOptionsList options = new DrawOptionsList();
        options.add(() -> FormComponent.drawWidthComponent(new GameSprite(this.background, 0, 0, this.background.getHeight()), new GameSprite(this.background, 1, 0, this.background.getHeight()), x, y, this.width, this.backgroundColor));
        for (ProgressBarOptions bar : this.bars) {
            options.add(bar.pos(x, y));
        }
        if (this.text != null) {
            int textX;
            int padding = Math.max(this.limitText, 0);
            String displayText = this.limitText >= 0 ? GameUtils.maxString(this.text.translate(), this.fontOptions, this.width - padding * 2) : this.text.translate();
            int textWidth = FontManager.bit.getWidthCeil(displayText, this.fontOptions);
            int textHeight = this.fontOptions.getSize();
            int textY = y + this.background.getHeight() / 2 - textHeight / 2;
            switch (this.textAlign) {
                case 0: {
                    textX = x + padding;
                    break;
                }
                case 2: {
                    textX = x + this.width - textWidth - padding;
                    break;
                }
                default: {
                    textX = x + this.width / 2 - textWidth / 2;
                }
            }
            options.add(() -> FontManager.bit.drawString(textX, textY, displayText, this.fontOptions));
        }
        return options;
    }

    public void draw(int x, int y) {
        this.pos(x, y).draw();
    }

    public class ProgressBarOptions {
        protected final GameTexture texture;
        protected float percent;
        protected Color color = Color.WHITE;
        protected int minWidth;

        public ProgressBarOptions(GameTexture texture, float percent) {
            this.texture = texture;
            this.percent = percent;
        }

        public ProgressBarOptions color(Color color) {
            this.color = color;
            return this;
        }

        public ProgressBarOptions minWidth(int minWidth) {
            this.minWidth = minWidth;
            return this;
        }

        public ProgressBarDrawOptions end() {
            return ProgressBarDrawOptions.this;
        }

        protected DrawOptions pos(int x, int y) {
            float percent;
            int extraFill = ProgressBarDrawOptions.this.background.getHeight() - this.texture.getHeight();
            int fillOffset = extraFill / 2;
            if (this.minWidth > 0) {
                float minPercent = (float)this.minWidth / (float)ProgressBarDrawOptions.this.width;
                percent = GameMath.lerp(this.percent, minPercent, 1.0f);
            } else {
                percent = this.percent;
            }
            int barWidth = (int)Math.ceil((float)(ProgressBarDrawOptions.this.width - extraFill) * percent);
            if (barWidth > 0) {
                return () -> FormComponent.drawWidthComponent(new GameSprite(this.texture, 0, 0, this.texture.getHeight()), new GameSprite(this.texture, 1, 0, this.texture.getHeight()), x + fillOffset, y + fillOffset, barWidth, this.color);
            }
            return () -> {};
        }
    }
}

