/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.BufferUtils
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.opengl.GL13
 *  org.lwjgl.stb.STBTTAlignedQuad
 *  org.lwjgl.stb.STBTTPackContext
 *  org.lwjgl.stb.STBTTPackRange
 *  org.lwjgl.stb.STBTTPackRange$Buffer
 *  org.lwjgl.stb.STBTTPackedchar
 *  org.lwjgl.stb.STBTTPackedchar$Buffer
 *  org.lwjgl.stb.STBTruetype
 *  org.lwjgl.system.CustomBuffer
 *  org.lwjgl.system.MemoryStack
 *  org.lwjgl.system.MemoryUtil
 *  org.lwjgl.system.Struct
 */
package necesse.gfx.gameFont;

import java.awt.Color;
import java.awt.Point;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import necesse.engine.GameLog;
import necesse.gfx.gameFont.CustomGameFont;
import necesse.gfx.gameFont.FontBasicOptions;
import necesse.gfx.gameFont.GameFont;
import necesse.gfx.gameFont.GameFontGlyphPositionTexture;
import necesse.gfx.gameFont.TrueTypeGameFontInfo;
import necesse.gfx.gameFont.TrueTypeGameFontSize;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTexture.MergeFunction;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTPackContext;
import org.lwjgl.stb.STBTTPackRange;
import org.lwjgl.stb.STBTTPackedchar;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.CustomBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.Struct;

public class TrueTypeGameFont
extends GameFont {
    public static int START_TEXTURE_SIZE = 1024;
    public final int fontSize;
    public final int addedFontSize;
    public final int strokeSize;
    private final TrueTypeGameFontSize[] fonts;
    private final HashMap<Point, Float> strokeDistanceMap = new HashMap();
    private final HashMap<Integer, CharacterInfo> charactersData = new HashMap();
    private STBTTPackedchar.Buffer nullBuffer;
    private final LinkedList<STBTTPackedchar.Buffer> buffers = new LinkedList();
    private STBTTPackContext packContext;
    private int bitmapWidth = START_TEXTURE_SIZE;
    private int bitmapHeight = START_TEXTURE_SIZE;
    private ByteBuffer fontBitmap;
    private ByteBuffer strokeBitmap;
    private int fontTexture;
    private int strokeTexture;
    private final STBTTAlignedQuad alignedQuad = STBTTAlignedQuad.malloc();
    private final FloatBuffer xBuffer = MemoryUtil.memAllocFloat((int)1);
    private final FloatBuffer yBuffer = MemoryUtil.memAllocFloat((int)1);
    private final Object bufferLock = new Object();
    private final int yDrawOffset;
    private final CustomGameFont.CharArray preDefinedChars;

    public TrueTypeGameFont(int fontSize, int strokeSize, int addedFontSize, int yDrawOffset, CustomGameFont.CharArray preDefinedChars, TrueTypeGameFontInfo ... fonts) {
        this.fontSize = fontSize;
        this.strokeSize = strokeSize;
        this.addedFontSize = addedFontSize;
        this.yDrawOffset = yDrawOffset;
        this.preDefinedChars = preDefinedChars;
        this.fonts = new TrueTypeGameFontSize[fonts.length];
        for (int i = 0; i < fonts.length; ++i) {
            this.fonts[i] = new TrueTypeGameFontSize(fonts[i], fontSize);
        }
        if (strokeSize > 0) {
            for (int y = -strokeSize; y <= strokeSize; ++y) {
                for (int x = -strokeSize; x <= strokeSize; ++x) {
                    Point p = new Point(x, y);
                    double distance = p.distance(0.0, 0.0);
                    this.strokeDistanceMap.put(p, Float.valueOf((float)distance));
                }
            }
        }
        HashSet<Integer> codePoints = new HashSet<Integer>();
        for (String s : CustomGameFont.fontLayout) {
            for (int i = 0; i < s.length(); ++i) {
                codePoints.add(s.codePointAt(i));
            }
        }
        this.addCharacters(codePoints);
    }

    private void increaseSize() {
        this.bitmapWidth *= 2;
        this.bitmapHeight *= 2;
        GameLog.debug.println("Increased " + this.fontSize + "x" + this.strokeSize + " font map to " + this.bitmapWidth + "x" + this.bitmapHeight);
        if (this.packContext != null) {
            STBTruetype.stbtt_PackEnd((STBTTPackContext)this.packContext);
            this.packContext.free();
        }
        this.buffers.forEach(CustomBuffer::free);
        this.buffers.clear();
        this.nullBuffer = null;
        this.packContext = null;
        this.charactersData.clear();
    }

    private void addCharacters(HashSet<Integer> codePoints) {
        codePoints.removeIf(this.charactersData::containsKey);
        if (codePoints.isEmpty()) {
            return;
        }
        if (this.packContext == null) {
            this.packContext = STBTTPackContext.malloc();
            this.fontBitmap = BufferUtils.createByteBuffer((int)(this.bitmapWidth * this.bitmapHeight));
            if (this.strokeSize > 0) {
                this.strokeBitmap = BufferUtils.createByteBuffer((int)(this.bitmapWidth * this.bitmapHeight));
            }
            int padding = this.strokeSize * 2 + 1;
            STBTruetype.stbtt_PackBegin((STBTTPackContext)this.packContext, (ByteBuffer)this.fontBitmap, (int)this.bitmapWidth, (int)this.bitmapHeight, (int)0, (int)padding);
        }
        for (TrueTypeGameFontSize font : this.fonts) {
            ArrayList<Integer> thisFontCodePoints = new ArrayList<Integer>(codePoints.size());
            int firstCodePoint = Integer.MAX_VALUE;
            for (int codePoint : codePoints) {
                if (!font.info.canDisplay(codePoint)) continue;
                thisFontCodePoints.add(codePoint);
                firstCodePoint = Math.min(firstCodePoint, codePoint);
            }
            if (thisFontCodePoints.isEmpty()) continue;
            STBTTPackedchar.Buffer data = this.packCharacters(font, firstCodePoint, thisFontCodePoints);
            if (data != null) {
                thisFontCodePoints.forEach(codePoints::remove);
                this.buffers.add(data);
                int i = 0;
                for (int codePoint : thisFontCodePoints) {
                    this.charactersData.put(codePoint, new CharacterInfo(codePoint, i++, data));
                }
                continue;
            }
            HashSet<Integer> currentCodePoints = new HashSet<Integer>(this.charactersData.keySet());
            currentCodePoints.addAll(codePoints);
            this.increaseSize();
            this.addCharacters(currentCodePoints);
            return;
        }
        if (!codePoints.isEmpty()) {
            Object currentCodePoints;
            if (this.nullBuffer == null) {
                this.nullBuffer = this.packCharacters(this.fonts[0], 0, Collections.singleton(0));
                if (this.nullBuffer != null) {
                    this.buffers.add(this.nullBuffer);
                } else {
                    currentCodePoints = new HashSet<Integer>(this.charactersData.keySet());
                    this.increaseSize();
                    this.addCharacters((HashSet<Integer>)currentCodePoints);
                    return;
                }
            }
            currentCodePoints = codePoints.iterator();
            while (currentCodePoints.hasNext()) {
                int codePoint = currentCodePoints.next();
                this.charactersData.put(codePoint, new CharacterInfo(0, 0, this.nullBuffer));
            }
        }
        if (this.fontTexture != 0) {
            GL11.glDeleteTextures((int)this.fontTexture);
        }
        this.fontTexture = GL11.glGenTextures();
        GL11.glBindTexture((int)3553, (int)this.fontTexture);
        ByteBuffer fontColorBuffer = this.bitBufferToColorBuffer(this.fontBitmap, this.bitmapWidth, this.bitmapHeight);
        GL11.glTexImage2D((int)3553, (int)0, (int)32856, (int)this.bitmapWidth, (int)this.bitmapHeight, (int)0, (int)6408, (int)5121, (ByteBuffer)fontColorBuffer);
        GL11.glTexParameteri((int)3553, (int)10240, (int)9729);
        GL11.glTexParameteri((int)3553, (int)10241, (int)9729);
        if (this.strokeBitmap != null) {
            if (this.strokeTexture != 0) {
                GL11.glDeleteTextures((int)this.strokeTexture);
            }
            this.strokeTexture = GL11.glGenTextures();
            GL11.glBindTexture((int)3553, (int)this.strokeTexture);
            ByteBuffer strokeColorBuffer = this.bitBufferToColorBuffer(this.strokeBitmap, this.bitmapWidth, this.bitmapHeight);
            GL11.glTexImage2D((int)3553, (int)0, (int)32856, (int)this.bitmapWidth, (int)this.bitmapHeight, (int)0, (int)6408, (int)5121, (ByteBuffer)strokeColorBuffer);
            GL11.glTexParameteri((int)3553, (int)10240, (int)9729);
            GL11.glTexParameteri((int)3553, (int)10241, (int)9729);
        }
    }

    private ByteBuffer generateFontBuffer(ByteBuffer fontBitmap, ByteBuffer strokeBitmap, int width, int height) {
        ByteBuffer colorBuffer = BufferUtils.createByteBuffer((int)(width * height * 4));
        fontBitmap.position(0);
        strokeBitmap.position(0);
        colorBuffer.position(0);
        while (fontBitmap.hasRemaining()) {
            int fontAlpha = fontBitmap.get() & 0xFF;
            int strokeAlpha = strokeBitmap.get() & 0xFF;
            Color color = MergeFunction.MULTIPLY.merge(new Color(0, 0, 0, strokeAlpha), new Color(255, 255, 255, fontAlpha));
            colorBuffer.put(new byte[]{(byte)color.getRed(), (byte)color.getGreen(), (byte)color.getBlue(), (byte)color.getAlpha()});
        }
        fontBitmap.position(0);
        strokeBitmap.position(0);
        colorBuffer.position(0);
        return colorBuffer;
    }

    private ByteBuffer bitBufferToColorBuffer(ByteBuffer bitBuffer, int width, int height) {
        ByteBuffer colorBuffer = BufferUtils.createByteBuffer((int)(width * height * 4));
        bitBuffer.position(0);
        colorBuffer.position(0);
        while (bitBuffer.hasRemaining()) {
            byte alpha = bitBuffer.get();
            colorBuffer.put(new byte[]{-1, -1, -1, alpha});
        }
        bitBuffer.position(0);
        colorBuffer.position(0);
        return colorBuffer;
    }

    private synchronized STBTTPackedchar.Buffer packCharacters(TrueTypeGameFontSize font, int firstCodePoint, Collection<Integer> codePoints) {
        try (MemoryStack stack = MemoryStack.stackPush();){
            byte horizontalOversample = 1;
            byte verticalOversample = 1;
            STBTTPackedchar.Buffer packedData = STBTTPackedchar.malloc((int)codePoints.size());
            STBTTPackRange.Buffer packRanges = STBTTPackRange.malloc((int)1, (MemoryStack)stack);
            IntBuffer ints = stack.mallocInt(codePoints.size());
            for (int codePoint : codePoints) {
                ints.put(codePoint);
            }
            ints.position(0);
            packRanges.put((Struct)STBTTPackRange.malloc((MemoryStack)stack).set(font.fontSize + (float)this.addedFontSize, firstCodePoint, ints, codePoints.size(), packedData, horizontalOversample, verticalOversample));
            packRanges.flip();
            boolean success = STBTruetype.stbtt_PackFontRanges((STBTTPackContext)this.packContext, (ByteBuffer)font.info.getTTFBuffer(), (int)0, (STBTTPackRange.Buffer)packRanges);
            packedData.clear();
            if (!success) {
                packedData.free();
                STBTTPackedchar.Buffer buffer = null;
                return buffer;
            }
            this.generateStrokeBitmap(packedData);
            STBTTPackedchar.Buffer buffer = packedData;
            return buffer;
        }
    }

    private synchronized void generateStrokeBitmap(STBTTPackedchar.Buffer data) {
        if (this.strokeBitmap == null) {
            return;
        }
        data.position(0);
        while (data.hasRemaining()) {
            STBTTPackedchar packedChar = (STBTTPackedchar)data.get();
            int startX = packedChar.x0();
            short endX = packedChar.x1();
            int startY = packedChar.y0();
            short endY = packedChar.y1();
            for (int x = startX; x < endX; ++x) {
                for (int y = startY; y < endY; ++y) {
                    int currentFontAlpha = this.fontBitmap.get(x + y * this.bitmapHeight) & 0xFF;
                    if (currentFontAlpha <= 0) continue;
                    for (Map.Entry<Point, Float> e : this.strokeDistanceMap.entrySet()) {
                        int desiredAlpha;
                        Point offset = e.getKey();
                        float distance = e.getValue().floatValue();
                        if (distance > (float)this.strokeSize + 0.5f) continue;
                        Point p = new Point(x + offset.x, y + offset.y);
                        int currentStrokeAlpha = this.strokeBitmap.get(p.x + p.y * this.bitmapHeight) & 0xFF;
                        if (currentStrokeAlpha >= (desiredAlpha = distance > (float)this.strokeSize - 0.5f ? currentFontAlpha : 255)) continue;
                        this.strokeBitmap.put(p.x + p.y * this.bitmapHeight, (byte)desiredAlpha);
                    }
                }
            }
        }
    }

    public void addCharOffset(String chars, float xOffset, float yOffset) {
        for (int i = 0; i < chars.length(); ++i) {
            this.addCharOffset(chars.charAt(i), xOffset, yOffset);
        }
    }

    public boolean addCharOffset(char character, float xOffset, float yOffset) {
        CharacterInfo info = this.charactersData.get(character);
        if (info != null) {
            info.xOffset += xOffset;
            info.yOffset += yOffset;
            return true;
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public synchronized float drawString(float x, float y, String text, FontBasicOptions options) {
        Object object = this.bufferLock;
        synchronized (object) {
            char c;
            int i;
            boolean pixelFont = options.isPixelFont();
            float ratio = (float)options.getSize() / (float)this.fontSize;
            float height = ratio * (float)this.fontSize;
            y += (float)this.yDrawOffset * ratio;
            y -= (float)this.strokeSize * ratio;
            y += height;
            float textureStrokeX = 0.0f;
            float textureStrokeY = 0.0f;
            TextureSwapper swapper = null;
            if (this.strokeTexture != 0) {
                textureStrokeX = (float)this.strokeSize / (float)this.bitmapWidth;
                textureStrokeY = (float)this.strokeSize / (float)this.bitmapHeight;
                this.xBuffer.put(0, x);
                this.yBuffer.put(0, y);
                options.applyGLStrokeColor();
                for (i = 0; i < text.length(); ++i) {
                    c = text.charAt(i);
                    this.drawCharQuad(c, textureStrokeX, textureStrokeY, ratio, swapper, true, pixelFont);
                }
            }
            this.xBuffer.put(0, x);
            this.yBuffer.put(0, y);
            options.applyGLColor();
            for (i = 0; i < text.length(); ++i) {
                c = text.charAt(i);
                this.drawCharQuad(c, textureStrokeX, textureStrokeY, ratio, swapper, false, pixelFont);
            }
            return this.xBuffer.get(0) - x;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public float drawStringShadow(float x, float y, String text, FontBasicOptions options) {
        Object object = this.bufferLock;
        synchronized (object) {
            char c;
            int i;
            int[] offset = options.getShadowOffset();
            x += (float)offset[0];
            y += (float)offset[1];
            boolean pixelFont = options.isPixelFont();
            float ratio = (float)options.getSize() / (float)this.fontSize;
            float height = ratio * (float)this.fontSize;
            y += (float)this.yDrawOffset * ratio;
            y -= (float)this.strokeSize * ratio;
            y += height;
            float textureStrokeX = 0.0f;
            float textureStrokeY = 0.0f;
            TextureSwapper swapper = null;
            if (this.strokeTexture != 0) {
                textureStrokeX = (float)this.strokeSize / (float)this.bitmapWidth;
                textureStrokeY = (float)this.strokeSize / (float)this.bitmapHeight;
                this.xBuffer.put(0, x);
                this.yBuffer.put(0, y);
                options.applyGLShadowColor();
                for (i = 0; i < text.length(); ++i) {
                    c = text.charAt(i);
                    this.drawCharQuad(c, textureStrokeX, textureStrokeY, ratio, swapper, true, pixelFont);
                }
            }
            this.xBuffer.put(0, x);
            this.yBuffer.put(0, y);
            options.applyGLShadowColor();
            for (i = 0; i < text.length(); ++i) {
                c = text.charAt(i);
                this.drawCharQuad(c, textureStrokeX, textureStrokeY, ratio, swapper, false, pixelFont);
            }
            return this.xBuffer.get(0) - x;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private synchronized void drawCharQuad(char ch, float textureStrokeX, float textureStrokeY, float ratio, TextureSwapper swapper, boolean isStroke, boolean pixelFont) {
        Object object = this.bufferLock;
        synchronized (object) {
            GameFontGlyphPositionTexture glyph = null;
            if (pixelFont && this.preDefinedChars != null && ch < this.preDefinedChars.chars.length) {
                glyph = this.preDefinedChars.chars[ch];
            }
            if (glyph != null) {
                if (isStroke) {
                    return;
                }
                float startX = this.xBuffer.get(0);
                float startY = this.yBuffer.get(0);
                float width = (float)glyph.width * ratio;
                glyph.texture.bindTexture();
                glyph.draw(startX, startY - (float)(glyph.height + this.yDrawOffset) * ratio, width, (float)glyph.height * ratio);
                this.xBuffer.put(0, startX + width);
            } else {
                CharacterInfo info = this.charactersData.get(ch);
                if (info == null) {
                    info = this.charactersData.get(63);
                }
                info.data.position(0);
                float startX = this.xBuffer.get(0);
                float startY = this.yBuffer.get(0);
                STBTruetype.stbtt_GetPackedQuad((STBTTPackedchar.Buffer)info.data, (int)this.bitmapWidth, (int)this.bitmapHeight, (int)info.charIndex, (FloatBuffer)this.xBuffer, (FloatBuffer)this.yBuffer, (STBTTAlignedQuad)this.alignedQuad, (boolean)true);
                float advanced = (this.xBuffer.get(0) - startX) * ratio;
                this.xBuffer.put(0, startX + advanced);
                float yOffset = this.alignedQuad.y0() - startY + info.yOffset;
                float x = this.alignedQuad.x0() + info.xOffset * ratio;
                float y = startY + (yOffset + (float)this.strokeSize) * ratio - (float)this.strokeSize;
                float alignedWidth = this.alignedQuad.x1() - this.alignedQuad.x0();
                float alignedHeight = this.alignedQuad.y1() - this.alignedQuad.y0();
                float width = (alignedWidth + (float)this.strokeSize) * ratio;
                float height = (alignedHeight + (float)this.strokeSize) * ratio;
                GL13.glActiveTexture((int)33984);
                GL11.glBindTexture((int)3553, (int)(isStroke ? this.strokeTexture : this.fontTexture));
                GL11.glBegin((int)7);
                this.drawQuad(x, y, x + width, y + height, this.alignedQuad.s0() - textureStrokeX, this.alignedQuad.t0() - textureStrokeY, this.alignedQuad.s1() + textureStrokeX, this.alignedQuad.t1() + textureStrokeY);
                GL11.glEnd();
            }
        }
    }

    private void drawQuad(float x0, float y0, float x1, float y1, float s0, float t0, float s1, float t1) {
        GL11.glTexCoord2f((float)s0, (float)t0);
        GL11.glVertex2f((float)x0, (float)y0);
        GL11.glTexCoord2f((float)s1, (float)t0);
        GL11.glVertex2f((float)x1, (float)y0);
        GL11.glTexCoord2f((float)s1, (float)t1);
        GL11.glVertex2f((float)x1, (float)y1);
        GL11.glTexCoord2f((float)s0, (float)t1);
        GL11.glVertex2f((float)x0, (float)y1);
    }

    @Override
    public synchronized float drawChar(float x, float y, char ch, FontBasicOptions options) {
        return this.drawString(x, y, Character.toString(ch), options);
    }

    @Override
    public float drawCharShadow(float x, float y, char ch, FontBasicOptions options) {
        return this.drawStringShadow(x, y, Character.toString(ch), options);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public synchronized float getWidth(char ch, FontBasicOptions options) {
        float ratio = (float)options.getSize() / (float)this.getFontHeight();
        GameFontGlyphPositionTexture glyph = null;
        if (options.isPixelFont() && this.preDefinedChars != null && ch < this.preDefinedChars.chars.length) {
            glyph = this.preDefinedChars.chars[ch];
        }
        if (glyph != null) {
            return (float)glyph.width * ratio;
        }
        Object object = this.bufferLock;
        synchronized (object) {
            CharacterInfo info = this.charactersData.get(ch);
            if (info == null) {
                info = this.charactersData.get(63);
            }
            return info.getWidth() * ratio;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public synchronized float getWidth(String str, FontBasicOptions options) {
        float ratio = (float)options.getSize() / (float)this.getFontHeight();
        float width = 0.0f;
        for (int i = 0; i < str.length(); ++i) {
            char ch = str.charAt(i);
            GameFontGlyphPositionTexture glyph = null;
            if (options.isPixelFont() && this.preDefinedChars != null && ch < this.preDefinedChars.chars.length) {
                glyph = this.preDefinedChars.chars[ch];
            }
            if (glyph != null) {
                width += (float)glyph.width * ratio;
                continue;
            }
            Object object = this.bufferLock;
            synchronized (object) {
                CharacterInfo info = this.charactersData.get(ch);
                if (info == null) {
                    info = this.charactersData.get(63);
                }
                float advanced = info.getWidth() * ratio;
                width += advanced;
                continue;
            }
        }
        return width;
    }

    @Override
    public float getHeight(char ch, FontBasicOptions options) {
        float ratio = (float)options.getSize() / (float)this.fontSize;
        return ratio * (float)this.fontSize;
    }

    @Override
    public float getHeight(String str, FontBasicOptions options) {
        float ratio = (float)options.getSize() / (float)this.fontSize;
        return ratio * (float)this.fontSize;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public synchronized int getWidthCeil(String str, FontBasicOptions options) {
        float ratio = (float)options.getSize() / (float)this.getFontHeight();
        float width = 0.0f;
        for (int i = 0; i < str.length(); ++i) {
            char ch = str.charAt(i);
            GameFontGlyphPositionTexture glyph = null;
            if (options.isPixelFont() && this.preDefinedChars != null && ch < this.preDefinedChars.chars.length) {
                glyph = this.preDefinedChars.chars[ch];
            }
            if (glyph != null) {
                width += (float)glyph.width * ratio;
                continue;
            }
            Object object = this.bufferLock;
            synchronized (object) {
                CharacterInfo info = this.charactersData.get(ch);
                if (info == null) {
                    info = this.charactersData.get(63);
                }
                float advanced = info.getWidth() * ratio;
                width += advanced;
                continue;
            }
        }
        return (int)Math.ceil(width);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public synchronized int getWidthCeil(char ch, FontBasicOptions options) {
        float ratio = (float)options.getSize() / (float)this.getFontHeight();
        GameFontGlyphPositionTexture glyph = null;
        if (options.isPixelFont() && this.preDefinedChars != null && ch < this.preDefinedChars.chars.length) {
            glyph = this.preDefinedChars.chars[ch];
        }
        if (glyph != null) {
            return (int)Math.ceil((float)glyph.width * ratio);
        }
        Object object = this.bufferLock;
        synchronized (object) {
            CharacterInfo info = this.charactersData.get(ch);
            if (info == null) {
                info = this.charactersData.get(63);
            }
            float advanced = info.getWidth() * ratio;
            return (int)Math.ceil(advanced);
        }
    }

    @Override
    public int getFontHeight() {
        return this.fontSize;
    }

    @Override
    public int getHeightCeil(String str, FontBasicOptions options) {
        float ratio = (float)options.getSize() / (float)this.fontSize;
        return (int)(ratio * (float)this.fontSize);
    }

    @Override
    public int getHeightCeil(char ch, FontBasicOptions options) {
        float ratio = (float)options.getSize() / (float)this.fontSize;
        return (int)(ratio * (float)this.fontSize);
    }

    @Override
    public boolean canDraw(char ch) {
        return this.charactersData.containsKey(ch);
    }

    @Override
    public void deleteTextures() {
        this.dispose();
    }

    @Override
    public GameFont updateFont(String additionalChars) {
        if (additionalChars == null) {
            return this;
        }
        HashSet<Integer> codePoints = new HashSet<Integer>();
        for (int i = 0; i < additionalChars.length(); ++i) {
            codePoints.add(additionalChars.codePointAt(i));
        }
        this.addCharacters(codePoints);
        return this;
    }

    public void dispose() {
        if (this.preDefinedChars != null) {
            this.preDefinedChars.texture.delete();
        }
        if (this.packContext != null) {
            STBTruetype.stbtt_PackEnd((STBTTPackContext)this.packContext);
            this.packContext.free();
        }
        this.buffers.forEach(CustomBuffer::free);
        this.buffers.clear();
        this.packContext = null;
        this.charactersData.clear();
        if (this.fontTexture != 0) {
            GL11.glDeleteTextures((int)this.fontTexture);
        }
        this.fontTexture = 0;
        if (this.strokeTexture != 0) {
            GL11.glDeleteTextures((int)this.strokeTexture);
        }
        this.strokeTexture = 0;
        MemoryUtil.memFree((Buffer)this.xBuffer);
        MemoryUtil.memFree((Buffer)this.yBuffer);
        this.alignedQuad.free();
    }

    private static class CharacterInfo {
        public final int codePoint;
        public final int charIndex;
        public final STBTTPackedchar.Buffer data;
        public final int x0;
        public final int y0;
        public final int x1;
        public final int y1;
        public final float xOff;
        public final float yOff;
        public final float xAdvance;
        public final float xOff2;
        public final float yOff2;
        public float yOffset;
        public float xOffset;

        public CharacterInfo(int codePoint, int charIndex, STBTTPackedchar.Buffer data) {
            this.codePoint = codePoint;
            this.charIndex = charIndex;
            this.data = data;
            STBTTPackedchar charData = (STBTTPackedchar)data.get(charIndex);
            this.x0 = charData.x0();
            this.y0 = charData.y0();
            this.x1 = charData.x1();
            this.y1 = charData.y1();
            this.xOff = charData.xoff();
            this.yOff = charData.yoff();
            this.xAdvance = charData.xadvance();
            this.xOff2 = charData.xoff2();
            this.yOff2 = charData.yoff2();
        }

        public float getWidth() {
            return this.xAdvance;
        }

        public int getHeight() {
            return this.y1 - this.y0;
        }
    }

    private static class TextureSwapper {
        private int currentTextureID;
        private boolean drawing;

        public void useTexture(GameTexture texture) {
            if (this.currentTextureID == texture.getTextureID()) {
                return;
            }
            if (this.drawing) {
                GL11.glEnd();
                this.drawing = false;
            }
            this.currentTextureID = texture.getTextureID();
            texture.bindTexture();
            if (!this.drawing) {
                GL11.glBegin((int)7);
            }
            this.drawing = true;
        }

        public void useTexture(int textureID) {
            if (this.currentTextureID == textureID) {
                return;
            }
            if (this.drawing) {
                GL11.glEnd();
                this.drawing = false;
            }
            this.currentTextureID = textureID;
            GL13.glActiveTexture((int)33984);
            GL11.glBindTexture((int)3553, (int)textureID);
            if (!this.drawing) {
                GL11.glBegin((int)7);
            }
            this.drawing = true;
        }

        public void end() {
            if (this.drawing) {
                GL11.glEnd();
                this.drawing = false;
            }
            this.currentTextureID = 0;
        }
    }
}

