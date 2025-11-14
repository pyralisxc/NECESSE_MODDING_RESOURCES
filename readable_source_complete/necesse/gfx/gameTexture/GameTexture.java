/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.BufferUtils
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.opengl.GL13
 *  org.lwjgl.stb.STBImage
 *  org.lwjgl.stb.STBImageResize
 *  org.lwjgl.stb.STBImageWrite
 *  org.lwjgl.system.MemoryUtil
 */
package necesse.gfx.gameTexture;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import necesse.engine.GameLoadingScreen;
import necesse.engine.GameLog;
import necesse.engine.GlobalData;
import necesse.engine.util.GameUtils;
import necesse.gfx.GameResources;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsStart;
import necesse.gfx.gameTexture.AbstractGameTexture;
import necesse.gfx.gameTexture.MergeFunction;
import necesse.gfx.res.ResourceEncoder;
import necesse.gfx.ui.GameTextureData;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.stb.STBImage;
import org.lwjgl.stb.STBImageResize;
import org.lwjgl.stb.STBImageWrite;
import org.lwjgl.system.MemoryUtil;

public class GameTexture
extends AbstractGameTexture
implements Serializable {
    private static final HashSet<Integer> generatedTextures = new HashSet();
    private static final HashMap<String, GameTexture> loadedTextures = new HashMap();
    private static boolean shouldFinalizeLoaded = false;
    public static boolean memoryDebug;
    public static long timeSpentFinalizing;
    public static long timeSpentRestoring;
    public static final int BYTES_PER_PIXEL = 4;
    public static BlendQuality overrideBlendQuality;
    public boolean onlyPreloaded;
    private int width;
    private int height;
    public ByteBuffer buffer;
    public String debugName;
    private int textureID;
    private int hash;
    private boolean resetTexture;
    private boolean generatedTexture;
    private BlendQuality blendQuality;
    private boolean finalizeLater;
    private boolean isFinal;

    public static void listUnloadedTextures(List<String> excludes, List<String> excludeDirs) {
        if (excludes == null) {
            excludes = new ArrayList<String>();
        }
        if (excludeDirs == null) {
            excludeDirs = new ArrayList<String>();
        }
        GameTexture.listUnloadedTextures("", excludes, excludeDirs);
    }

    private static void listUnloadedTextures(String dir, List<String> excludes, List<String> excludeDirs) {
        File[] files;
        String subDir;
        excludes.replaceAll(s -> GameUtils.formatFileExtension(s, "png"));
        File fDir = new File(GlobalData.rootPath() + "res/" + dir);
        String string = subDir = dir.isEmpty() ? "" : dir + "/";
        if (fDir.exists() && fDir.isDirectory() && (files = fDir.listFiles()) != null) {
            for (File f : files) {
                GameTexture loadedTexture;
                if (f.isDirectory()) {
                    if (excludeDirs.contains(subDir + f.getName())) continue;
                    GameTexture.listUnloadedTextures(subDir + f.getName(), excludes, excludeDirs);
                    continue;
                }
                String name = f.getName();
                String key = subDir + name;
                if (!name.endsWith(".png") || (loadedTexture = loadedTextures.get(key)) != null && !loadedTexture.onlyPreloaded || excludes.contains(key)) continue;
                GameLog.warn.println("Texture " + key + " is never loaded");
            }
        }
    }

    public static Iterable<Map.Entry<String, GameTexture>> getLoadedTextures() {
        return loadedTextures.entrySet();
    }

    private static void deleteTexture(int textureID, boolean removeFromGeneratedTextures) {
        GL11.glDeleteTextures((int)textureID);
        if (removeFromGeneratedTextures) {
            generatedTextures.remove(textureID);
        }
    }

    private static void deleteTexture(int textureID) {
        GameTexture.deleteTexture(textureID, true);
    }

    public static void deleteGeneratedTextures() {
        generatedTextures.forEach(id -> GameTexture.deleteTexture(id, false));
        generatedTextures.clear();
    }

    public static int getGeneratedTextureCount() {
        return generatedTextures.size();
    }

    public static GameTexture fromFile(String filePath, GameTexture defaultNotFound, boolean forceNotFinalize) {
        try {
            return GameTexture.fromFileRaw(filePath, forceNotFinalize);
        }
        catch (FileNotFoundException e) {
            return defaultNotFound;
        }
    }

    public static GameTexture fromFile(String filePath, GameTexture defaultNotFound) {
        return GameTexture.fromFile(filePath, defaultNotFound, false);
    }

    public static GameTexture fromFile(String filePath, boolean forceNotFinalize) {
        return GameTexture.fromFile(filePath, GameResources.error, forceNotFinalize);
    }

    public static GameTexture fromFile(String filePath) {
        return GameTexture.fromFile(filePath, GameResources.error);
    }

    private static GameTexture fromFileRawUnknown(String filePath, boolean outsideGame, boolean forceNotFinalize) throws FileNotFoundException {
        Objects.requireNonNull(filePath);
        filePath = GameUtils.formatFileExtension(filePath, "png");
        GameTexture out = loadedTextures.get(filePath);
        if (out == null) {
            out = new GameTexture(filePath, outsideGame, true);
            if (!forceNotFinalize) {
                out.makeFinal();
            }
            out.finalizeLater();
            loadedTextures.put(filePath, out);
        }
        out.onlyPreloaded = false;
        return out;
    }

    public static GameTexture fromFileRaw(String filePath, boolean forceNotFinalize) throws FileNotFoundException {
        return GameTexture.fromFileRawUnknown(filePath, false, forceNotFinalize);
    }

    public static GameTexture fromFileRawOutside(String filePath, boolean forceNotFinalize) throws FileNotFoundException {
        return GameTexture.fromFileRawUnknown(filePath, true, forceNotFinalize);
    }

    public static GameTexture fromFileRaw(String filePath) throws FileNotFoundException {
        return GameTexture.fromFileRaw(filePath, false);
    }

    public static GameTexture fromFileRawOutside(String filePath) throws FileNotFoundException {
        return GameTexture.fromFileRawOutside(filePath, false);
    }

    public static GameTexture fromTextureID(String debugName, int textureID, int width, int height) {
        if (textureID <= 0) {
            throw new IllegalArgumentException("Texture ID must be greater than 0");
        }
        GameTexture out = new GameTexture(debugName, width, height);
        out.textureID = textureID;
        out.generatedTexture = true;
        out.resetTexture = false;
        out.isFinal = true;
        generatedTextures.add(textureID);
        return out;
    }

    public static void finalizeLoadedTextures() {
        shouldFinalizeLoaded = true;
        if (memoryDebug) {
            System.out.println("TIME SPENT FINALIZING: " + GameUtils.getTimeStringNano(timeSpentFinalizing));
        }
        loadedTextures.values().forEach(GameTexture::makeFinal);
        if (memoryDebug) {
            System.out.println("TIME SPENT FINALIZING AFTER: " + GameUtils.getTimeStringNano(timeSpentFinalizing));
        }
        if (memoryDebug) {
            System.out.println("TIME SPENT RESTORING: " + GameUtils.getTimeStringNano(timeSpentRestoring));
        }
    }

    public GameTexture(String debugName, int width, int height) {
        this.debugName = debugName;
        this.width = width;
        this.height = height;
        this.buffer = BufferUtils.createByteBuffer((int)(this.width * this.height * 4));
        this.resetTexture();
        this.setBlendQuality(BlendQuality.LINEAR);
    }

    public GameTexture(GameTexture copy, int spriteX, int spriteY, int spriteRes) {
        this(copy.debugName + " copy", spriteRes, spriteRes);
        this.copy(copy, 0, 0, spriteX * spriteRes, spriteY * spriteRes, spriteRes, spriteRes);
        this.resetTexture();
    }

    public GameTexture(GameTexture copy, int x, int y, int width, int height) {
        this(copy.debugName + " copy", width, height);
        this.copy(copy, 0, 0, x, y, width, height);
        this.resetTexture();
    }

    public GameTexture(GameTexture copy) {
        this(copy.debugName + " copy", copy.getWidth(), copy.getHeight());
        copy.ensureNotFinal();
        copy.buffer.clear();
        this.buffer.clear();
        this.buffer.put(copy.buffer);
        this.resetTexture();
    }

    public GameTexture(String debugName, ByteBuffer copy, int width, int height) {
        this(debugName, width, height);
        this.buffer.clear();
        this.buffer.put(copy);
        this.resetTexture();
    }

    public GameTexture(String debugName, byte[] inputBytes) {
        this.debugName = debugName;
        this.loadBytes(inputBytes);
        this.setBlendQuality(BlendQuality.LINEAR);
        this.resetTexture();
    }

    private GameTexture(String file, boolean outsideGame, boolean updateLoadingScreen) throws FileNotFoundException {
        this.debugName = file;
        this.width = 0;
        this.height = 0;
        this.buffer = BufferUtils.createByteBuffer((int)0);
        if (updateLoadingScreen) {
            GameLoadingScreen.drawLoadingSub(file);
        }
        boolean foundInFile = false;
        try {
            byte[] inputBytes;
            if (outsideGame) {
                inputBytes = GameUtils.loadByteFile(file);
            } else {
                File outsideFile = new File(GlobalData.rootPath() + "res/" + file);
                if (outsideFile.exists()) {
                    inputBytes = GameUtils.loadByteFile(outsideFile);
                    foundInFile = true;
                } else {
                    try {
                        inputBytes = ResourceEncoder.getResourceBytes(file);
                        foundInFile = true;
                    }
                    catch (FileNotFoundException e) {
                        inputBytes = GameUtils.loadByteFile(outsideFile);
                    }
                }
            }
            try {
                this.loadBytes(inputBytes);
            }
            catch (IllegalArgumentException e) {
                throw new IOException("Error loading image file " + file + ": " + e.getMessage());
            }
        }
        catch (FileNotFoundException e) {
            throw e;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        if (!(outsideGame || foundInFile || GlobalData.isDevMode())) {
            GameLog.warn.println(file + " was not found in resource file.");
        }
        this.setBlendQuality(BlendQuality.LINEAR);
        this.resetTexture();
    }

    private GameTexture(ByteBuffer buffer, int width, int height) {
        this.buffer = buffer;
        this.width = width;
        this.height = height;
        this.setBlendQuality(BlendQuality.LINEAR);
        this.resetTexture();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void loadBytes(byte[] inputBytes) {
        ByteBuffer rawImageBuffer = null;
        try {
            int height;
            int width;
            IntBuffer widthBuffer = null;
            IntBuffer heightBuffer = null;
            IntBuffer channelsBuffer = null;
            ByteBuffer inputBuffer = null;
            try {
                widthBuffer = MemoryUtil.memAllocInt((int)1);
                heightBuffer = MemoryUtil.memAllocInt((int)1);
                channelsBuffer = MemoryUtil.memAllocInt((int)1);
                inputBuffer = MemoryUtil.memAlloc((int)inputBytes.length);
                inputBuffer.put(inputBytes);
                inputBuffer.position(0);
                rawImageBuffer = STBImage.stbi_load_from_memory((ByteBuffer)inputBuffer, (IntBuffer)widthBuffer, (IntBuffer)heightBuffer, (IntBuffer)channelsBuffer, (int)4);
                width = widthBuffer.get();
                height = heightBuffer.get();
            }
            catch (Throwable throwable) {
                MemoryUtil.memFree((Buffer)widthBuffer);
                MemoryUtil.memFree((Buffer)heightBuffer);
                MemoryUtil.memFree(channelsBuffer);
                MemoryUtil.memFree(inputBuffer);
                throw throwable;
            }
            MemoryUtil.memFree((Buffer)widthBuffer);
            MemoryUtil.memFree((Buffer)heightBuffer);
            MemoryUtil.memFree((Buffer)channelsBuffer);
            MemoryUtil.memFree((Buffer)inputBuffer);
            if (rawImageBuffer == null) {
                throw new IllegalArgumentException(STBImage.stbi_failure_reason());
            }
            this.width = width;
            this.height = height;
            rawImageBuffer.position(0);
            this.buffer = BufferUtils.createByteBuffer((int)(this.width * this.height * 4));
            this.buffer.put(rawImageBuffer);
        }
        catch (Throwable throwable) {
            MemoryUtil.memFree(rawImageBuffer);
            throw throwable;
        }
        MemoryUtil.memFree((Buffer)rawImageBuffer);
    }

    public boolean runPreAntialias(boolean onlyTest) {
        boolean out = false;
        for (int x = 0; x < this.getWidth(); ++x) {
            for (int y = 0; y < this.getHeight(); ++y) {
                Color c = this.getColor(x, y);
                if (c.getAlpha() != 0) continue;
                Color nextColor = this.getSurroundingInvisColor(x, y);
                if (c.getRGB() == nextColor.getRGB()) continue;
                if (onlyTest) {
                    return true;
                }
                this.setPixel(x, y, nextColor);
                out = true;
            }
        }
        return out;
    }

    private Color getSurroundingInvisColor(int x, int y) {
        int r = 0;
        int g = 0;
        int b = 0;
        int n = 0;
        for (int i = x - 1; i <= x + 1; ++i) {
            for (int j = y - 1; j <= y + 1; ++j) {
                Color c;
                if (i < 0 || i >= this.getWidth() || j < 0 || j >= this.getHeight() || i == 0 && j == 0 || (c = this.getColor(i, j)).getAlpha() == 0) continue;
                ++n;
                r += c.getRed();
                g += c.getGreen();
                b += c.getBlue();
            }
        }
        if (n != 0) {
            return new Color(r / n, g / n, b / n, 0);
        }
        return new Color(255, 255, 255, 0);
    }

    public GameTexture(String debugName, int width, int height, ByteBuffer buffer) {
        this(debugName, width, height);
        buffer.position(0);
        this.buffer.position(0);
        this.buffer.put(buffer);
        this.resetTexture();
    }

    public GameTexture(String debugName, int width, int height, byte[] buffer) {
        this(debugName, width, height);
        if (buffer.length != width * height * 4) {
            throw new ArrayIndexOutOfBoundsException();
        }
        this.buffer.position(0);
        this.buffer.put(buffer, 0, buffer.length);
        this.resetTexture();
    }

    public GameTexture(String debugName, GameTextureData data) {
        this(debugName, data.width, data.height);
        byte[] dataBuffer = data.getBuffer();
        if (dataBuffer.length != data.width * data.height * 4) {
            throw new ArrayIndexOutOfBoundsException();
        }
        this.buffer.position(0);
        this.buffer.put(dataBuffer, 0, dataBuffer.length);
        this.setBlendQuality(data.blendQuality);
        this.resetTexture();
        if (data.isFinal) {
            this.makeFinal();
        }
    }

    public GameTexture(String debugName, BufferedImage image) {
        this(debugName, image.getWidth(), image.getHeight());
        int[] pixels = new int[this.width * this.height];
        image.getRGB(0, 0, this.width, this.height, pixels, 0, this.width);
        for (int x = 0; x < this.width; ++x) {
            for (int y = 0; y < this.height; ++y) {
                int pixel = pixels[x + y * this.width];
                int red = pixel >> 16 & 0xFF;
                int green = pixel >> 8 & 0xFF;
                int blue = pixel & 0xFF;
                int alpha = pixel >> 24 & 0xFF;
                this.setPixel(x, y, new Color(red, green, blue, alpha));
            }
        }
        this.resetTexture();
        this.setBlendQuality(BlendQuality.LINEAR);
    }

    public void setBlendQuality(BlendQuality quality) {
        this.blendQuality = quality;
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    public void replaceColor(Color originalColor, Color replacementColor) {
        this.ensureNotFinal();
        for (int i = 0; i < this.width; ++i) {
            for (int j = 0; j < this.height; ++j) {
                if (this.getColor(i, j).hashCode() != originalColor.hashCode()) continue;
                this.setPixel(i, j, replacementColor);
            }
        }
    }

    public void copy(GameTexture image, int applyX, int applyY, int imageX, int imageY, int imageWidth, int imageHeight) {
        if (image == this) {
            throw new IllegalArgumentException("Texture cannot copy itself");
        }
        this.ensureNotFinal();
        image.ensureNotFinal();
        if (applyX < 0) {
            imageX -= applyX;
        }
        if (applyY < 0) {
            imageY -= applyY;
        }
        if (imageX < 0) {
            applyX -= imageX;
        }
        if (imageY < 0) {
            applyY -= imageY;
        }
        if (applyX < 0) {
            applyX = 0;
        }
        if (applyY < 0) {
            applyY = 0;
        }
        if (imageX < 0) {
            imageX = 0;
        }
        if (imageY < 0) {
            imageY = 0;
        }
        imageWidth = Math.min(imageWidth, this.width - applyX);
        imageHeight = Math.min(imageHeight, this.height - applyY);
        imageWidth = Math.min(imageWidth, image.width - imageX);
        imageHeight = Math.min(imageHeight, image.height - imageY);
        for (int j = 0; j < imageHeight; ++j) {
            this.buffer.position(this.getPixelIndex(applyX, applyY + j));
            image.buffer.position(image.getPixelIndex(imageX, imageY + j));
            for (int i = 0; i < imageWidth; ++i) {
                for (int k = 0; k < 4; ++k) {
                    this.buffer.put(image.buffer.get());
                }
            }
        }
        this.resetTexture();
    }

    public void copy(GameTexture image, int applyX, int applyY) {
        this.copy(image, applyX, applyY, 0, 0, image.width, image.height);
    }

    public void merge(GameTexture image, int applyX, int applyY, int imageX, int imageY, int imageWidth, int imageHeight, MergeFunction mergeFunction) {
        if (image == this) {
            throw new IllegalArgumentException("Texture cannot merge itself");
        }
        this.ensureNotFinal();
        image.ensureNotFinal();
        if (applyX < 0) {
            imageX -= applyX;
        }
        if (applyY < 0) {
            imageY -= applyY;
        }
        if (imageX < 0) {
            applyX -= imageX;
        }
        if (imageY < 0) {
            applyY -= imageY;
        }
        if (applyX < 0) {
            applyX = 0;
        }
        if (applyY < 0) {
            applyY = 0;
        }
        if (imageX < 0) {
            imageX = 0;
        }
        if (imageY < 0) {
            imageY = 0;
        }
        imageWidth = Math.min(imageWidth, this.width - applyX);
        imageHeight = Math.min(imageHeight, this.height - applyY);
        imageWidth = Math.min(imageWidth, image.width - imageX);
        imageHeight = Math.min(imageHeight, image.height - imageY);
        for (int i = 0; i < imageWidth; ++i) {
            for (int j = 0; j < imageHeight; ++j) {
                this.setPixel(applyX + i, applyY + j, this.mergeColor(this.getColor(applyX + i, applyY + j), image.getColor(imageX + i, imageY + j), mergeFunction));
            }
        }
    }

    public void merge(GameTexture image, int applyX, int applyY, MergeFunction mergeFunction) {
        this.merge(image, applyX, applyY, 0, 0, image.width, image.height, mergeFunction);
    }

    public void applyColor(Color color, MergeFunction mergeFunction) {
        this.ensureNotFinal();
        for (int i = 0; i < this.width; ++i) {
            for (int j = 0; j < this.height; ++j) {
                int index = this.getPixelIndex(i, j);
                this.setPixel(i, j, this.mergeColor(this.getColor(index), color, mergeFunction));
            }
        }
    }

    public GameTexture resize(int targetWidth, int targetHeight, int horizontalFilter, int verticalFilter) {
        ByteBuffer newBuffer = BufferUtils.createByteBuffer((int)(targetWidth * targetHeight * 4));
        this.ensureNotFinal();
        this.buffer.position(0);
        STBImageResize.stbir_resize((ByteBuffer)this.buffer, (int)this.width, (int)this.height, (int)0, (ByteBuffer)newBuffer, (int)targetWidth, (int)targetHeight, (int)0, (int)0, (int)4, (int)3, (int)1, (int)1, (int)1, (int)horizontalFilter, (int)verticalFilter, (int)0);
        return new GameTexture(newBuffer, targetWidth, targetHeight);
    }

    public GameTexture resize(int targetWidth, int targetHeight, int filter) {
        return this.resize(targetWidth, targetHeight, filter, filter);
    }

    public GameTexture resize(int targetWidth, int targetHeight) {
        return this.resize(targetWidth, targetHeight, 0);
    }

    public GameTexture clamp(int topPadding, int rightPadding, int botPadding, int leftPadding) {
        int x;
        int x2;
        int y1 = 0;
        for (int y = 0; y < this.height; ++y) {
            boolean found = false;
            for (x2 = 0; x2 < this.width; ++x2) {
                if (this.getAlpha(x2, y) <= 0) continue;
                y1 = y;
                found = true;
            }
            if (found) break;
        }
        int y2 = 0;
        for (int y = this.height - 1; y >= 0; --y) {
            boolean found = false;
            for (x = 0; x < this.width; ++x) {
                if (this.getAlpha(x, y) <= 0) continue;
                y2 = y;
                found = true;
            }
            if (found) break;
        }
        int x1 = 0;
        for (x2 = 0; x2 < this.width; ++x2) {
            boolean found = false;
            for (int y = 0; y < this.height; ++y) {
                if (this.getAlpha(x2, y) <= 0) continue;
                x1 = x2;
                found = true;
            }
            if (found) break;
        }
        int x22 = 0;
        for (x = this.width - 1; x >= 0; --x) {
            boolean found = false;
            for (int y = 0; y < this.height; ++y) {
                if (this.getAlpha(x, y) <= 0) continue;
                x22 = x;
                found = true;
            }
            if (found) break;
        }
        int width = Math.max(x22 - x1 + 1, 1) + leftPadding + rightPadding;
        int height = Math.max(y2 - y1 + 1, 1) + topPadding + botPadding;
        GameTexture out = new GameTexture(this.debugName + "Clamp", width, height);
        out.copy(this, leftPadding, topPadding, x1, y1, x22 - x1 + 1, y2 - y1 + 1);
        return out;
    }

    public GameTexture clamp(int padding) {
        return this.clamp(padding, padding, padding, padding);
    }

    public GameTexture flippedX() {
        GameTexture next = new GameTexture(this.debugName + " flippedX", this.getWidth(), this.getHeight());
        for (int x = 0; x < this.getWidth(); ++x) {
            int nextX = this.getWidth() - x - 1;
            for (int y = 0; y < this.getHeight(); ++y) {
                next.setPixel(nextX, y, this.getPixel(x, y));
            }
        }
        return next;
    }

    public GameTexture flippedY() {
        GameTexture next = new GameTexture(this.debugName + " flippedY", this.getWidth(), this.getHeight());
        for (int x = 0; x < this.getWidth(); ++x) {
            for (int y = 0; y < this.getHeight(); ++y) {
                int nextY = this.getHeight() - y - 1;
                next.setPixel(x, nextY, this.getPixel(x, y));
            }
        }
        return next;
    }

    public GameTexture rotatedClockwise() {
        GameTexture next = new GameTexture(this.debugName + " rotatedClockwise", this.getHeight(), this.getWidth());
        for (int x = 0; x < this.getWidth(); ++x) {
            int nextY = x;
            for (int y = 0; y < this.getHeight(); ++y) {
                int nextX = this.getHeight() - y - 1;
                next.setPixel(nextX, nextY, this.getPixel(x, y));
            }
        }
        return next;
    }

    public GameTexture rotatedAnticlockwise() {
        GameTexture next = new GameTexture(this.debugName + " rotatedAnticlockwise", this.getHeight(), this.getWidth());
        for (int x = 0; x < this.getWidth(); ++x) {
            int nextY = this.getWidth() - x - 1;
            for (int y = 0; y < this.getHeight(); ++y) {
                int nextX = y;
                next.setPixel(nextX, nextY, this.getPixel(x, y));
            }
        }
        return next;
    }

    public GameTexture croppedToNonTransparent(boolean keepSquare) {
        int x;
        int y;
        int topLeftX = this.getWidth() - 1;
        int topLeftY = this.getHeight() - 1;
        int bottomRightX = 0;
        int bottomRightY = 0;
        int x2 = 0;
        block0: while (x2 < this.getWidth()) {
            for (y = 0; y < this.getHeight(); ++y) {
                if (this.getAlpha(x2, y) > 0) break block0;
            }
            topLeftX = x2++;
        }
        x2 = this.getWidth() - 1;
        block2: while (x2 >= 0) {
            for (y = 0; y < this.getHeight(); ++y) {
                if (this.getAlpha(x2, y) > 0) break block2;
            }
            bottomRightX = x2--;
        }
        int y2 = 0;
        block4: while (y2 < this.getHeight()) {
            for (x = 0; x < this.getWidth(); ++x) {
                if (this.getAlpha(x, y2) > 0) break block4;
            }
            topLeftY = y2++;
        }
        y2 = this.getHeight() - 1;
        block6: while (y2 >= 0) {
            for (x = 0; x < this.getWidth(); ++x) {
                if (this.getAlpha(x, y2) > 0) break block6;
            }
            bottomRightY = y2--;
        }
        if (bottomRightX <= topLeftX || bottomRightY <= topLeftY) {
            return this;
        }
        int width = bottomRightX - topLeftX;
        int height = bottomRightY - topLeftY;
        if (keepSquare) {
            boolean top = true;
            while (!(width >= height || top && topLeftX <= 0 || !top && bottomRightX >= this.getWidth() - 1)) {
                if (top) {
                    --topLeftX;
                } else {
                    ++bottomRightX;
                }
                top = !top;
                width = bottomRightX - topLeftX;
            }
            top = true;
            while (!(height >= width || top && topLeftY <= 0 || !top && bottomRightY >= this.getHeight() - 1)) {
                if (top) {
                    --topLeftY;
                } else {
                    ++bottomRightY;
                }
                top = !top;
                height = bottomRightY - topLeftY;
            }
        }
        GameTexture croppedTexture = new GameTexture(this.debugName + " croppedToNonTransparent", width, height);
        for (int x3 = 0; x3 < width; ++x3) {
            for (int y3 = 0; y3 < height; ++y3) {
                croppedTexture.setPixel(x3, y3, this.getPixel(x3 + topLeftX, y3 + topLeftY));
            }
        }
        return croppedTexture;
    }

    private Color getColor(byte red, byte green, byte blue, byte alpha) {
        return new Color(red & 0xFF, green & 0xFF, blue & 0xFF, alpha & 0xFF);
    }

    private Color mergeColor(Color background, Color color, MergeFunction mergeFunction) {
        return mergeFunction.merge(background, color);
    }

    public void mergeSprite(GameTexture image, int spriteX, int spriteY, int spriteRes, int x, int y, MergeFunction mergeFunction) {
        this.merge(image, x, y, spriteX * spriteRes, spriteY * spriteRes, spriteRes, spriteRes, mergeFunction);
    }

    public void mergeSprite(GameTexture image, int spriteX, int spriteY, int spriteRes, int x, int y) {
        this.copy(image, x, y, spriteX * spriteRes, spriteY * spriteRes, spriteRes, spriteRes);
    }

    public Color getColor(int x, int y) {
        byte[] pixel = this.getPixel(x, y);
        return this.getColor(pixel[0], pixel[1], pixel[2], pixel[3]);
    }

    public Color getColor(int index) {
        byte[] pixel = this.getPixel(index);
        return this.getColor(pixel[0], pixel[1], pixel[2], pixel[3]);
    }

    public byte[] getPixel(int x, int y) {
        return this.getPixel(this.getPixelIndex(x, y));
    }

    public int getPixelIndex(int x, int y) {
        return (x + y * this.width) * 4;
    }

    public byte[] getPixel(int index) {
        this.ensureNotFinal();
        byte[] out = new byte[4];
        this.buffer.position(index);
        this.buffer.get(out, 0, out.length);
        return out;
    }

    public int getRed(int x, int y) {
        this.ensureNotFinal();
        return this.buffer.get(this.getPixelIndex(x, y)) & 0xFF;
    }

    public int getGreen(int x, int y) {
        this.ensureNotFinal();
        return this.buffer.get(this.getPixelIndex(x, y) + 1) & 0xFF;
    }

    public int getBlue(int x, int y) {
        this.ensureNotFinal();
        return this.buffer.get(this.getPixelIndex(x, y) + 2) & 0xFF;
    }

    public int getAlpha(int x, int y) {
        this.ensureNotFinal();
        return this.buffer.get(this.getPixelIndex(x, y) + 3) & 0xFF;
    }

    public void setPixel(int x, int y, Color color) {
        this.setPixel(x, y, color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    public void setPixel(int x, int y, int red, int green, int blue, int alpha) {
        red = (red % 256 + 256) % 256;
        green = (green % 256 + 256) % 256;
        blue = (blue % 256 + 256) % 256;
        alpha = (alpha % 256 + 256) % 256;
        this.setPixel(x, y, new byte[]{(byte)red, (byte)green, (byte)blue, (byte)alpha});
    }

    public void setPixel(int x, int y, byte[] data) {
        this.putData(this.getPixelIndex(x, y), data);
    }

    public void setColor(int x, int y, Color color) {
        this.setPixel(x, y, color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    public void putData(int index, byte[] data) {
        this.ensureNotFinal();
        this.buffer.position(index);
        this.buffer.put(data, 0, data.length);
        this.resetTexture();
    }

    public void setRed(int x, int y, int red) {
        this.ensureNotFinal();
        this.buffer.put(this.getPixelIndex(x, y), (byte)((red % 256 + 256) % 256));
        this.resetTexture();
    }

    public void setGreen(int x, int y, int green) {
        this.ensureNotFinal();
        this.buffer.put(this.getPixelIndex(x, y) + 1, (byte)((green % 256 + 256) % 256));
        this.resetTexture();
    }

    public void setBlue(int x, int y, int blue) {
        this.ensureNotFinal();
        this.buffer.put(this.getPixelIndex(x, y) + 2, (byte)((blue % 256 + 256) % 256));
        this.resetTexture();
    }

    public void setAlpha(int x, int y, int alpha) {
        this.ensureNotFinal();
        this.buffer.put(this.getPixelIndex(x, y) + 3, (byte)((alpha % 256 + 256) % 256));
        this.resetTexture();
    }

    public void fill(int red, int green, int blue, int alpha) {
        this.fillRect(0, 0, this.width, this.height, red, green, blue, alpha);
    }

    public void fillRect(int x, int y, int width, int height, int red, int green, int blue, int alpha) {
        this.ensureNotFinal();
        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                this.setPixel(x + i, y + j, red, green, blue, alpha);
            }
        }
    }

    public void resetTexture() {
        this.resetTexture(false);
    }

    public void resetTexture(boolean generateNew) {
        this.ensureNotFinal();
        this.resetTexture = true;
        this.hash = 0;
        if (generateNew) {
            this.getTextureID();
        }
    }

    public int getTextureID() {
        if (this.resetTexture) {
            this.delete();
            this.resetTexture = false;
        }
        if (!this.generatedTexture) {
            this.buffer.position(0);
            int textureID = GL11.glGenTextures();
            GL11.glBindTexture((int)3553, (int)textureID);
            GL11.glTexParameteri((int)3553, (int)10242, (int)33071);
            GL11.glTexParameteri((int)3553, (int)10243, (int)33071);
            GL11.glTexImage2D((int)3553, (int)0, (int)32856, (int)this.width, (int)this.height, (int)0, (int)6408, (int)5121, (ByteBuffer)this.buffer);
            this.textureID = textureID;
            this.generatedTexture = true;
            generatedTextures.add(this.textureID);
            if (this.finalizeLater) {
                this.isFinal = true;
                this.finalizeLater = false;
                this.buffer = null;
            }
        }
        return this.textureID;
    }

    public int hashCode() {
        if (this.hash == 0) {
            this.ensureNotFinal();
            this.buffer.position(0);
            this.hash = this.buffer.hashCode();
        }
        return this.hash;
    }

    @Override
    public void bindTexture(int texturePos) {
        GL13.glActiveTexture((int)texturePos);
        GL11.glBindTexture((int)3553, (int)this.getTextureID());
        if (overrideBlendQuality != null) {
            GL11.glTexParameteri((int)3553, (int)10241, (int)GameTexture.overrideBlendQuality.minFilter);
            GL11.glTexParameteri((int)3553, (int)10240, (int)GameTexture.overrideBlendQuality.magFilter);
        } else {
            GL11.glTexParameteri((int)3553, (int)10241, (int)this.blendQuality.minFilter);
            GL11.glTexParameteri((int)3553, (int)10240, (int)this.blendQuality.magFilter);
        }
    }

    public static void unbindTexture(int texturePos) {
        GL13.glActiveTexture((int)texturePos);
        GL11.glBindTexture((int)3553, (int)0);
    }

    public static void unbindTexture() {
        GameTexture.unbindTexture(33984);
    }

    public void delete() {
        if (this.generatedTexture) {
            GameTexture.deleteTexture(this.textureID);
            generatedTextures.remove(this.textureID);
            this.textureID = 0;
            this.generatedTexture = false;
        }
    }

    public GameTexture finalizeLater() {
        this.finalizeLater = true;
        return this;
    }

    public GameTexture makeFinal() {
        long time = System.nanoTime();
        if (memoryDebug && !this.isFinal() && shouldFinalizeLoaded) {
            System.out.println("FINALIZED " + this.debugName);
        }
        this.getTextureID();
        this.isFinal = true;
        this.buffer = null;
        timeSpentFinalizing += System.nanoTime() - time;
        return this;
    }

    public boolean isFinal() {
        return this.isFinal;
    }

    public void ensureNotFinal() {
        if (this.isFinal()) {
            this.restoreFinal();
            if (memoryDebug) {
                System.out.println("RESTORED " + this.debugName);
            }
        }
    }

    public GameTexture restoreFinal() {
        long time = System.nanoTime();
        if (!this.isFinal()) {
            return this;
        }
        this.buffer = this.getTextureData();
        this.isFinal = false;
        this.finalizeLater = false;
        timeSpentRestoring += System.nanoTime() - time;
        return this;
    }

    private ByteBuffer getTextureData() {
        ByteBuffer buffer = BufferUtils.createByteBuffer((int)(this.width * this.height * 4));
        this.bindTexture();
        GL11.glGetTexImage((int)3553, (int)0, (int)6408, (int)5121, (ByteBuffer)buffer);
        return buffer;
    }

    private byte[] getTextureDataArray() {
        ByteBuffer buffer = this.getTextureData();
        byte[] data = new byte[buffer.limit()];
        buffer.get(data);
        return data;
    }

    public IntBuffer getIntBuffer() {
        IntBuffer buffer = BufferUtils.createIntBuffer((int)(this.width * this.height * 4));
        this.bindTexture();
        GL11.glGetTexImage((int)3553, (int)0, (int)6408, (int)5121, (IntBuffer)buffer);
        return buffer;
    }

    public BufferedImage getBufferedImage() {
        return GameTexture.getBufferedImage(this.width, this.height, this.buffer);
    }

    public File saveTextureImage(String filePath) {
        return this.saveTextureImage(filePath, true);
    }

    public File saveTextureImage(String filePath, boolean printDebug) {
        ByteBuffer buffer = this.buffer;
        if (this.isFinal()) {
            buffer = this.getTextureData();
        }
        filePath = GameUtils.formatFileExtension(filePath, "png");
        File file = new File(filePath);
        GameUtils.mkDirs(file);
        buffer.position(0);
        STBImageWrite.stbi_write_png((CharSequence)file.getAbsolutePath(), (int)this.width, (int)this.height, (int)4, (ByteBuffer)buffer, (int)0);
        if (printDebug) {
            GameLog.debug.println("Saved texture image to " + file.getAbsolutePath());
        }
        return file;
    }

    public byte[] encodeTextureToPNGBytes() {
        ByteBuffer buffer = this.buffer;
        if (this.isFinal()) {
            buffer = this.getTextureData();
        }
        buffer.position(0);
        return GameUtils.encodeToPNG(buffer, this.width, this.height, 4);
    }

    public static BufferedImage getBufferedImage(int width, int height, ByteBuffer buffer) {
        try {
            BufferedImage image = new BufferedImage(width, height, 2);
            for (int x = 0; x < width; ++x) {
                for (int y = 0; y < height; ++y) {
                    int index = (x + y * width) * 4;
                    int red = buffer.get(index) & 0xFF;
                    int green = buffer.get(index + 1) & 0xFF;
                    int blue = buffer.get(index + 2) & 0xFF;
                    int alpha = buffer.get(index + 3) & 0xFF;
                    int rgb = new Color(red, green, blue, alpha).getRGB();
                    image.setRGB(x, y, rgb);
                }
            }
            return image;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public GameTextureData getData() {
        ByteBuffer buffer = this.buffer;
        if (this.isFinal()) {
            buffer = this.getTextureData();
        }
        byte[] bufferArray = new byte[buffer.limit()];
        buffer.position(0);
        buffer.get(bufferArray, 0, bufferArray.length);
        return new GameTextureData(this.width, this.height, bufferArray, this.isFinal(), this.blendQuality);
    }

    public TextureDrawOptionsStart initDraw() {
        return TextureDrawOptions.initDraw(this);
    }

    static {
        overrideBlendQuality = null;
    }

    public static enum BlendQuality {
        LINEAR(9729, 9729),
        NEAREST(9728, 9728);

        public final int minFilter;
        public final int magFilter;

        private BlendQuality(int minFilter, int magFilter) {
            this.minFilter = minFilter;
            this.magFilter = magFilter;
        }
    }
}

