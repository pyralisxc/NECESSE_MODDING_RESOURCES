/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.gameFont;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.LinkedList;
import javax.imageio.ImageIO;
import necesse.engine.GameLog;
import necesse.engine.GlobalData;
import necesse.engine.util.GameUtils;
import necesse.gfx.gameFont.CustomGameFont;
import necesse.gfx.gameFont.FontBasicOptions;
import necesse.gfx.gameFont.GameFont;
import necesse.gfx.gameFont.GameFontGlyphPosition;
import necesse.gfx.gameFont.GameFontGlyphPositionTexture;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.res.ResourceEncoder;

public class TrueTypeGameFontOld
extends GameFont {
    public static boolean debug = false;
    private static boolean debugColor = true;
    private static final char[] ignoredCharacters = new char[]{'\n', '\t', '\b', '\r'};
    private static final int padding = 1;
    private static final int defaultChars = 0;
    public final boolean antiAlias;
    public final int fontHeight;
    public final String file;
    public final Font font;
    public final boolean addOutline;
    private final int drawOffset;
    private final CustomGameFont.CharArray preDefinedChars;
    private GameFontGlyphPositionTexture[] charArray;
    public LinkedList<GameTexture> textures = new LinkedList();

    private static boolean isIgnored(char ch) {
        for (char ignoredCharacter : ignoredCharacters) {
            if (ch != ignoredCharacter) continue;
            return true;
        }
        return false;
    }

    public TrueTypeGameFontOld(String file, int fontSize, boolean antiAlias, boolean addOutline, int addedSize, int drawOffset, CustomGameFont.CharArray preDefinedChars, String additionalChars) {
        this.file = file;
        this.font = TrueTypeGameFontOld.loadTrueTypeFont(file, fontSize - (addOutline ? 2 : 0) + addedSize);
        this.antiAlias = antiAlias;
        this.addOutline = addOutline;
        this.drawOffset = drawOffset;
        this.preDefinedChars = preDefinedChars;
        this.fontHeight = fontSize;
        if (preDefinedChars == null) {
            additionalChars = additionalChars == null ? GameUtils.join(CustomGameFont.fontLayout, "") : GameUtils.join(CustomGameFont.fontLayout, "") + additionalChars;
        }
        this.generateFont(additionalChars);
    }

    public TrueTypeGameFontOld(String file, int fontSize, boolean antiAlias, boolean addOutline, int addedSize, int drawOffset, CustomGameFont.CharArray preDefinedChars) {
        this(file, fontSize, antiAlias, addOutline, addedSize, drawOffset, preDefinedChars, null);
    }

    @Override
    public TrueTypeGameFontOld updateFont(String additionalChars) {
        if (additionalChars == null) {
            return this;
        }
        for (int i = 0; i < additionalChars.length(); ++i) {
            char ch = additionalChars.charAt(i);
            if (ch < this.charArray.length && this.charArray[ch] != null) continue;
            return this.generateFont(additionalChars);
        }
        return this;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private TrueTypeGameFontOld generateFont(String additionalChars) {
        try {
            int i;
            int maxSize = 0;
            if (this.preDefinedChars != null) {
                maxSize = Math.max(maxSize, this.preDefinedChars.chars.length);
            }
            if (additionalChars != null) {
                for (int i2 = 0; i2 < additionalChars.length(); ++i2) {
                    maxSize = Math.max(maxSize, additionalChars.charAt(i2) + '\u0001');
                }
            }
            GameFontGlyphPosition[] charArray = new GameFontGlyphPosition[maxSize];
            TextureSizeGenerator size = new TextureSizeGenerator();
            for (i = 0; i < 0; ++i) {
                if (!this.font.canDisplay((char)i) || TrueTypeGameFontOld.isIgnored((char)i) || this.preDefinedChars != null && this.preDefinedChars.canDraw((char)i)) continue;
                charArray[i] = size.addCharacter(i);
            }
            if (additionalChars != null) {
                for (i = 0; i < additionalChars.length(); ++i) {
                    char ch = additionalChars.charAt(i);
                    if (this.charArray != null && ch < this.charArray.length && this.charArray[ch] != null || charArray[ch] != null || !this.font.canDisplay(ch) || TrueTypeGameFontOld.isIgnored(ch)) continue;
                    charArray[ch] = size.addCharacter(ch);
                }
            }
            TextureGenerator texGen = null;
            if (size.textureWidth > 0 && size.textureHeight > 0) {
                texGen = new TextureGenerator(size.textureWidth, size.textureHeight);
                for (int i3 = 0; i3 < charArray.length; ++i3) {
                    if (charArray[i3] == null) continue;
                    texGen.drawCharacter((char)i3, charArray[i3]);
                }
            }
            TrueTypeGameFontOld trueTypeGameFontOld = this;
            synchronized (trueTypeGameFontOld) {
                this.charArray = this.charArray != null ? Arrays.copyOf(this.charArray, Math.max(charArray.length, this.charArray.length)) : new GameFontGlyphPositionTexture[charArray.length];
                GameTexture texture = texGen != null ? new GameTexture(this.file, texGen.image) : new GameTexture(this.file + " font", 0, 0);
                for (int i4 = 0; i4 < charArray.length; ++i4) {
                    if (this.preDefinedChars != null && this.preDefinedChars.canDraw((char)i4)) {
                        this.charArray[i4] = this.preDefinedChars.chars[i4];
                        continue;
                    }
                    if (charArray[i4] == null) continue;
                    GameFontGlyphPositionTexture glyphPos = new GameFontGlyphPositionTexture(texture, charArray[i4]);
                    glyphPos.drawYOffset += this.drawOffset;
                    this.charArray[i4] = glyphPos;
                }
                this.textures.add(texture);
            }
        }
        catch (Exception e) {
            System.err.println("Failed to create font.");
            e.printStackTrace();
        }
        return this;
    }

    @Override
    public synchronized float drawChar(float x, float y, char ch, FontBasicOptions options) {
        GameFontGlyphPositionTexture glyph = null;
        if (ch < this.charArray.length) {
            glyph = this.charArray[ch];
        }
        if (glyph == null) {
            glyph = this.charArray[63];
        }
        float width = this.getGlyphWidth(glyph, options.getSize());
        float height = this.getGlyphHeight(glyph, options.getSize());
        glyph.texture.bindTexture();
        options.applyGLColor();
        glyph.draw(x, y, width, height);
        return width;
    }

    @Override
    public float drawCharShadow(float x, float y, char ch, FontBasicOptions options) {
        GameFontGlyphPositionTexture glyph = null;
        if (ch < this.charArray.length) {
            glyph = this.charArray[ch];
        }
        if (glyph == null) {
            glyph = this.charArray[63];
        }
        float width = this.getGlyphWidth(glyph, options.getSize());
        float height = this.getGlyphHeight(glyph, options.getSize());
        glyph.texture.bindTexture();
        options.applyGLColor();
        int[] offset = options.getShadowOffset();
        glyph.draw(x + (float)offset[0], y + (float)offset[1], width, height);
        return width;
    }

    private float getGlyphWidth(GameFontGlyphPosition glyph, int size) {
        float ratio = (float)size / (float)this.fontHeight;
        return ratio * (float)glyph.width;
    }

    private float getGlyphHeight(GameFontGlyphPosition glyph, int size) {
        float ratio = (float)size / (float)this.fontHeight;
        return ratio * (float)glyph.height;
    }

    @Override
    public synchronized float getWidth(char ch, FontBasicOptions options) {
        GameFontGlyphPositionTexture glyph = null;
        if (ch < this.charArray.length) {
            glyph = this.charArray[ch];
        }
        if (glyph == null) {
            glyph = this.charArray[63];
        }
        return this.getGlyphWidth(glyph, options.getSize());
    }

    @Override
    public float getHeight(char ch, FontBasicOptions options) {
        float ratio = (float)options.getSize() / (float)this.fontHeight;
        return ratio * (float)this.font.getSize();
    }

    @Override
    public synchronized int getWidthCeil(char ch, FontBasicOptions options) {
        GameFontGlyphPositionTexture glyph = null;
        if (ch < this.charArray.length) {
            glyph = this.charArray[ch];
        }
        if (glyph == null) {
            glyph = this.charArray[63];
        }
        return (int)this.getGlyphWidth(glyph, options.getSize());
    }

    @Override
    public int getFontHeight() {
        return this.fontHeight;
    }

    @Override
    public synchronized int getHeightCeil(char ch, FontBasicOptions options) {
        float ratio = (float)options.getSize() / (float)this.fontHeight;
        return (int)(ratio * (float)this.font.getSize());
    }

    @Override
    public synchronized boolean canDraw(char ch) {
        return ch < this.charArray.length && this.charArray[ch] != null;
    }

    @Override
    public synchronized void deleteTextures() {
        for (GameTexture texture : this.textures) {
            texture.delete();
        }
        this.textures = new LinkedList();
        if (this.preDefinedChars != null) {
            this.preDefinedChars.texture.delete();
        }
    }

    private static Font loadTrueTypeFont(String file, int size) {
        try {
            InputStream inputStream;
            File outsideFile = new File(GlobalData.rootPath() + "res/fonts/" + file + ".ttf");
            if (outsideFile.exists()) {
                inputStream = new FileInputStream(outsideFile);
            } else {
                try {
                    inputStream = ResourceEncoder.getResourceInputStream("fonts/" + file + ".ttf");
                }
                catch (FileNotFoundException e) {
                    inputStream = new FileInputStream(GlobalData.rootPath() + "res/fonts/" + file + ".ttf");
                }
            }
            Font tempFont = Font.createFont(0, inputStream);
            tempFont = tempFont.deriveFont(1, size);
            inputStream.close();
            return tempFont;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private class TextureSizeGenerator {
        public static final int maxWidth = 2048;
        private final Canvas canvas = new Canvas();
        public int textureWidth;
        public int textureHeight;
        private final int actualPadding;
        private int positionX;
        private int positionY;
        private int rowHeight;

        private TextureSizeGenerator() {
            this.positionX = this.actualPadding = 1 + (TrueTypeGameFontOld.this.addOutline ? 2 : 0);
            this.positionY = this.actualPadding;
        }

        public GameFontGlyphPosition addCharacter(int codePoint) {
            FontMetrics metrics = this.canvas.getFontMetrics(TrueTypeGameFontOld.this.font);
            int width = metrics.charWidth(codePoint);
            int height = metrics.getHeight();
            if (this.positionX + width + this.actualPadding >= 2048) {
                this.positionX = this.actualPadding;
                this.positionY += this.rowHeight;
                this.rowHeight = 0;
                this.textureWidth = 2048;
            }
            GameFontGlyphPosition glyph = new GameFontGlyphPosition(this.positionX - (TrueTypeGameFontOld.this.addOutline ? 1 : 0), this.positionY - (TrueTypeGameFontOld.this.addOutline ? 1 : 0), width + (TrueTypeGameFontOld.this.addOutline ? 2 : 0), height + (TrueTypeGameFontOld.this.addOutline ? 2 : 0));
            if (this.positionX + width + this.actualPadding > this.textureWidth) {
                this.textureWidth = this.positionX + width + this.actualPadding;
            }
            if (height + this.actualPadding > this.rowHeight) {
                this.rowHeight = height + this.actualPadding;
                this.textureHeight = this.positionY + this.rowHeight + this.actualPadding;
            }
            this.positionX += width + this.actualPadding;
            return glyph;
        }
    }

    private class TextureGenerator {
        public final BufferedImage image;
        public final Graphics2D graphics;
        public final FontMetrics fontMetrics;

        public TextureGenerator(int textureWidth, int textureHeight) {
            this.image = new BufferedImage(textureWidth, textureHeight, 2);
            this.graphics = (Graphics2D)this.image.getGraphics();
            this.graphics.setColor(new Color(0, 0, 0, 0));
            this.graphics.fillRect(0, 0, textureWidth, textureHeight);
            this.graphics.setFont(TrueTypeGameFontOld.this.font);
            this.fontMetrics = this.graphics.getFontMetrics();
            this.graphics.setColor(Color.WHITE);
            if (TrueTypeGameFontOld.this.antiAlias) {
                this.graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            }
        }

        public void drawCharacter(char ch, GameFontGlyphPosition glyph) {
            int x = glyph.textureX + (TrueTypeGameFontOld.this.addOutline ? 1 : 0);
            int y = glyph.textureY + this.fontMetrics.getAscent() + (TrueTypeGameFontOld.this.addOutline ? 1 : 0);
            if (TrueTypeGameFontOld.this.addOutline) {
                this.graphics.setColor(Color.BLACK);
                this.graphics.drawString(String.valueOf(ch), x - 1, y);
                this.graphics.drawString(String.valueOf(ch), x + 1, y);
                this.graphics.drawString(String.valueOf(ch), x, y - 1);
                this.graphics.drawString(String.valueOf(ch), x, y + 1);
                this.graphics.setColor(Color.WHITE);
            }
            this.graphics.drawString(String.valueOf(ch), x, y);
            if (debug) {
                System.out.println("Drawing " + ch + " at " + x + ", " + y);
                x = glyph.textureX;
                y = glyph.textureY;
                this.graphics.setColor(debugColor ? Color.RED : Color.GREEN);
                debugColor = !debugColor;
                this.graphics.drawLine(x, y, x, y + glyph.height);
                this.graphics.drawLine(x, y, x + glyph.width, y);
                this.graphics.drawLine(x + glyph.width, y, x + glyph.width, y + glyph.height);
                this.graphics.drawLine(x, y + glyph.height, x + glyph.width, y + glyph.height);
                this.graphics.setColor(Color.WHITE);
            }
        }

        public void saveImage(String path) {
            try {
                GameLog.debug.println("Saving image");
                ImageIO.write((RenderedImage)this.image, "PNG", new File(path + ".png"));
                GameLog.debug.println("Saved image");
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

