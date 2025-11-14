/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.EXTFramebufferObject
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.opengl.GL13
 *  org.lwjgl.opengl.GL14
 *  org.lwjgl.opengl.GL30
 */
package necesse.gfx.gameTexture;

import java.nio.ByteBuffer;
import java.util.function.Consumer;
import necesse.gfx.gameTexture.GameFrameBuffer;
import necesse.gfx.gameTexture.GameTexture;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;

public class GameFrameBufferEXT
extends GameFrameBuffer {
    public final int width;
    public final int height;
    public final int frameBufferObject;
    public final int textureID;
    private final boolean complete;

    public GameFrameBufferEXT(Consumer<GameFrameBuffer> binder, int width, int height) {
        super(binder);
        this.width = width;
        this.height = height;
        this.frameBufferObject = EXTFramebufferObject.glGenFramebuffersEXT();
        EXTFramebufferObject.glBindFramebufferEXT((int)36160, (int)this.frameBufferObject);
        this.textureID = GL11.glGenTextures();
        GL11.glBindTexture((int)3553, (int)this.textureID);
        GL11.glTexParameteri((int)3553, (int)10242, (int)33071);
        GL11.glTexParameteri((int)3553, (int)10243, (int)33071);
        GL11.glTexImage2D((int)3553, (int)0, (int)32856, (int)Math.max(1, width), (int)Math.max(1, height), (int)0, (int)6408, (int)5121, (ByteBuffer)null);
        GL11.glTexParameteri((int)3553, (int)10241, (int)9729);
        GL11.glTexParameteri((int)3553, (int)10240, (int)9729);
        EXTFramebufferObject.glFramebufferTexture2DEXT((int)36160, (int)36064, (int)3553, (int)this.textureID, (int)0);
        this.complete = this.checkStatus();
        if (this.complete) {
            EXTFramebufferObject.glBindFramebufferEXT((int)36160, (int)this.frameBufferObject);
            GL11.glClearColor((float)0.0f, (float)0.0f, (float)0.0f, (float)0.0f);
            GL11.glClearDepth((double)0.0);
            GL11.glViewport((int)0, (int)0, (int)width, (int)height);
            GL11.glMatrixMode((int)5889);
            GL11.glLoadIdentity();
            GL11.glOrtho((double)0.0, (double)width, (double)height, (double)0.0, (double)0.0, (double)1.0);
            GL11.glMatrixMode((int)5888);
            GL11.glEnable((int)2929);
            GL11.glDepthFunc((int)519);
            GL11.glEnable((int)3553);
            GL11.glEnable((int)3042);
            GL14.glBlendFuncSeparate((int)770, (int)771, (int)1, (int)771);
        }
        this.glUnbind();
    }

    private boolean checkStatus() {
        int status = EXTFramebufferObject.glCheckFramebufferStatusEXT((int)36160);
        if (status != 36053) {
            System.err.println("Could not create frame buffer");
            switch (status) {
                case 36054: {
                    System.err.println("GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT_EXT");
                    break;
                }
                case 36055: {
                    System.err.println("GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT_EXT");
                    break;
                }
                case 36057: {
                    System.err.println("GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS_EXT");
                    break;
                }
                case 36058: {
                    System.err.println("GL_FRAMEBUFFER_INCOMPLETE_FORMATS_EXT");
                    break;
                }
                case 36059: {
                    System.err.println("GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER_EXT");
                    break;
                }
                case 36060: {
                    System.err.println("GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER_EXT");
                    break;
                }
                case 36061: {
                    System.err.println("GL_FRAMEBUFFER_UNSUPPORTED_EXT");
                }
            }
            return false;
        }
        return true;
    }

    @Override
    public boolean isComplete() {
        return this.complete;
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public int getColorBufferTextureID() {
        return this.textureID;
    }

    @Override
    public int getDepthBufferTextureID() {
        return 0;
    }

    @Override
    protected void glBind() {
        EXTFramebufferObject.glBindFramebufferEXT((int)36160, (int)this.frameBufferObject);
    }

    @Override
    protected void glUnbind() {
        EXTFramebufferObject.glBindFramebufferEXT((int)36160, (int)0);
    }

    @Override
    public void dispose() {
        GL11.glDeleteTextures((int)this.textureID);
        EXTFramebufferObject.glDeleteFramebuffersEXT((int)this.frameBufferObject);
    }

    @Override
    public GameTexture getTextureAndDisposeFrameBuffer(String textureDebugName) {
        GL30.glDeleteFramebuffers((int)this.frameBufferObject);
        return GameTexture.fromTextureID(textureDebugName, this.textureID, this.width, this.height);
    }

    @Override
    public void bindTexture(int texturePos) {
        GL13.glActiveTexture((int)texturePos);
        GL11.glBindTexture((int)3553, (int)this.textureID);
        GL11.glTexParameteri((int)3553, (int)10242, (int)33071);
        GL11.glTexParameteri((int)3553, (int)10243, (int)33071);
    }
}

