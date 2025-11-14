/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.BufferUtils
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.opengl.GL13
 *  org.lwjgl.opengl.GL14
 *  org.lwjgl.stb.STBImageWrite
 */
package necesse.gfx.gameTexture;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.function.Consumer;
import necesse.engine.GameLog;
import necesse.engine.util.GameUtils;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsStart;
import necesse.gfx.gameTexture.AbstractGameTexture;
import necesse.gfx.gameTexture.GameTexture;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.stb.STBImageWrite;

public abstract class GameFrameBuffer
extends AbstractGameTexture {
    private final Consumer<GameFrameBuffer> binder;

    public GameFrameBuffer(Consumer<GameFrameBuffer> binder) {
        this.binder = binder;
    }

    public abstract boolean isComplete();

    public abstract int getColorBufferTextureID();

    public abstract int getDepthBufferTextureID();

    public void clearColor() {
        GL11.glClear((int)16384);
    }

    public void clearDepth() {
        GL11.glClear((int)256);
    }

    public void bindFrameBuffer() {
        this.glBind();
        this.binder.accept(this);
    }

    public void unbindFrameBuffer() {
        this.glUnbind();
        this.binder.accept(null);
    }

    protected abstract void glBind();

    protected abstract void glUnbind();

    public abstract void dispose();

    public abstract GameTexture getTextureAndDisposeFrameBuffer(String var1);

    public TextureDrawOptionsStart initDraw() {
        TextureDrawOptionsStart drawOptions = TextureDrawOptions.initDraw(this);
        drawOptions.mirrorY();
        return drawOptions;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void drawSimple(int textureID, int x, int y, int width, int height, Runnable preDrawLogic, Runnable postDrawLogic) {
        GL11.glDepthMask((boolean)false);
        GL13.glActiveTexture((int)33984);
        GL11.glBindTexture((int)3553, (int)textureID);
        try {
            if (preDrawLogic != null) {
                preDrawLogic.run();
            }
            GL11.glBegin((int)7);
            GL11.glTexCoord2f((float)0.0f, (float)1.0f);
            GL11.glVertex2f((float)x, (float)y);
            GL11.glTexCoord2f((float)1.0f, (float)1.0f);
            GL11.glVertex2f((float)(x + width), (float)y);
            GL11.glTexCoord2f((float)1.0f, (float)0.0f);
            GL11.glVertex2f((float)(x + width), (float)(y + height));
            GL11.glTexCoord2f((float)0.0f, (float)0.0f);
            GL11.glVertex2f((float)x, (float)(y + height));
            GL11.glEnd();
            GL11.glDepthMask((boolean)true);
            GL14.glBlendFuncSeparate((int)770, (int)771, (int)1, (int)771);
        }
        finally {
            if (postDrawLogic != null) {
                postDrawLogic.run();
            }
        }
    }

    public static void draw(int textureID, int x, int y, int width, int height, GameTexture.BlendQuality blendQuality, Runnable preDrawLogic, Runnable postDrawLogic) {
        GL11.glBlendFunc((int)1, (int)771);
        GameFrameBuffer.drawSimple(textureID, x, y, width, height, () -> {
            GL11.glTexParameteri((int)3553, (int)10241, (int)blendQuality.minFilter);
            GL11.glTexParameteri((int)3553, (int)10240, (int)blendQuality.magFilter);
            GL11.glLoadIdentity();
            GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            if (preDrawLogic != null) {
                preDrawLogic.run();
            }
        }, postDrawLogic);
    }

    public static void draw(int textureID, int x, int y, int width, int height, Runnable preDrawLogic, Runnable postDrawLogic) {
        GameFrameBuffer.draw(textureID, x, y, width, height, GameTexture.BlendQuality.LINEAR, preDrawLogic, postDrawLogic);
    }

    public static byte[] getTextureData(int textureID, int width, int height) {
        ByteBuffer buffer = BufferUtils.createByteBuffer((int)(width * height * 4));
        GL13.glActiveTexture((int)33984);
        GL11.glBindTexture((int)3553, (int)textureID);
        GL11.glGetTexImage((int)3553, (int)0, (int)6408, (int)5121, (ByteBuffer)buffer);
        byte[] data = new byte[buffer.limit()];
        buffer.get(data);
        buffer.clear();
        GL11.glBindTexture((int)3553, (int)0);
        return data;
    }

    public void saveTextureImage(int textureID, int width, int height, String filePath) {
        byte[] bytes = GameFrameBuffer.getTextureData(textureID, width, height);
        File file = new File(filePath + ".png");
        GameUtils.mkDirs(file);
        ByteBuffer buffer = BufferUtils.createByteBuffer((int)(width * height * 4));
        buffer.put(bytes);
        buffer.position(0);
        STBImageWrite.stbi_write_png((CharSequence)file.getAbsolutePath(), (int)width, (int)height, (int)4, (ByteBuffer)buffer, (int)0);
        GameLog.debug.println("Saved frame buffer texture image to " + file.getAbsolutePath());
    }
}

